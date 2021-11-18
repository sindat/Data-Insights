-- Data source: https://ourworldindata.org/covid-deaths

use covid_data_research
GO

/************************
	DATA DISCOVERIES
*************************/

-- 1 - DISCOVERY - LIKELIHOOD OF DYING IF CONTRACTED COVID IN SAID COUNTRY
SELECT

	location,
	date,
	total_cases,
	new_cases,
	total_deaths,
	population,
	(total_deaths / total_cases) * 100 AS 'Death to Cases Ratio'

FROM MASTERDATA_COVID

WHERE total_deaths IS NOT NULL

ORDER BY location, date



-- 2 - DISCOVERY - TOTAL CASES VS POPULATION - WHAT PERCENTAGE OF THE POPULATION GOT COVID PER COUNTRY
SELECT

	location,
	date,
	total_cases,
	population,
	(total_cases / population) * 100 AS 'Cases to Total Population Ratio'

FROM MASTERDATA_COVID

WHERE total_cases IS NOT NULL

ORDER BY location, date



-- 3 - DISCOVERY - COUNTRIES WITH THE HIGHEST INFECTION RATES PER POPULATION
SELECT

	location, 
	population,
	MAX (total_cases) as 'Highest Infection Count',
	MAX ((total_cases / population)) as 'Highest Cases to Total Population Ratio'

FROM MASTERDATA_COVID

GROUP BY location, population

ORDER BY 'Highest Cases to Total Population Ratio' DESC



-- 4 - DISCOVERY - COUNTRIES WITH THE HIGHEST DEATH RATE PER POPULATION
SELECT

	location as 'Country',
	max(cast(total_deaths as int)) as 'Total Deaths'

FROM MASTERDATA_COVID

WHERE continent IS NOT NULL

GROUP BY location

ORDER BY 'Total Deaths' DESC



-- 5 - DISCOVERY - HIGHEST DEATH RATE PER CONTINENT
SELECT

	continent,
	MAX(cast(total_deaths as int)) as 'Total Deaths'

FROM MASTERDATA_COVID

WHERE continent IS NOT NULL

GROUP BY continent

ORDER BY 'Total Deaths' DESC



-- 6 - DISCOVERY - TOTAL VACCINATIONS PER COUNTRY, SORTED BY DATE
SELECT

	continent,
	location,
	date,
	population,
	new_vaccinations,
	SUM (CONVERT (int, new_vaccinations)) OVER (PARTITION BY location ORDER BY location, date) as 'Total vaccinations per country'

FROM MASTERDATA_COVID

WHERE continent IS NOT NULL



-- 7 - DISCOVERY - PERCENTAGE OF PEOPLE VACCINATED PER COUNTRY

WITH population_vs_vaccinations (continent, location, date, population, new_vaccinations, total_vaccinations_per_country)

AS (
   SELECT

		continent,
		location,
		date,
		population,
		new_vaccinations,
		SUM (CONVERT (int, new_vaccinations)) OVER (PARTITION BY location ORDER BY location, date) as 'Total vaccinations per country'
    
	FROM MASTERDATA_COVID
) 

SELECT 
	*, 
	(total_vaccinations_per_country/population)*100 as 'Percentage of people vaccinated'
	
FROM
	population_vs_vaccinations




/************************
	DATA STORAGE OPTIONS
*************************/


-- OPTION 1 - INSERTING DATA FROM A TEMP TABLE INTO A NEW TABLE TO PRESERVE THE SELECTED DATA
DROP TABLE IF EXISTS #percent_of_people_vaccinated
CREATE TABLE #percent_of_people_vaccinated
(
	continent NVARCHAR(255),
	location NVARCHAR(255),
	date datetime,
	population numeric,
	new_vaccinations numeric,
	total_vaccinated_per_country numeric
)

INSERT INTO #percent_of_people_vaccinated
	SELECT

			continent,
			location,
			date,
			population,
			new_vaccinations,
			SUM (CONVERT (int, new_vaccinations)) OVER (PARTITION BY location ORDER BY location, date)
	
	FROM MASTERDATA_COVID

SELECT * FROM #percent_of_people_vaccinated



-- OPTION 2 - CREATING A VIEW TO STORE A QUERY
CREATE VIEW percentage_of_population_vaccinated
	AS
		SELECT

			continent,
			location,
			date,
			population,
			new_vaccinations,
			SUM (CONVERT (int, new_vaccinations)) OVER (PARTITION BY location ORDER BY location, date) as 'Total people vaccinated'
	
		FROM MASTERDATA_COVID
