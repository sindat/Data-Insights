use covid_data_research


-- CASE 1 - LIKELIHOOD OF DYING IF CONTRACTED COVID IN SAID COUNTRY
--SELECT

--TOP 30

--location, date, total_cases, new_cases, total_deaths, population, (total_deaths / total_cases) * 100 AS 'Death to Cases Ratio'

--FROM MASTERDATA_COVID

--WHERE location LIKE '%rocco%' AND total_deaths IS NOT NULL

--ORDER BY location, date



-- CASE 2 - LOOKING AT TOTAL CASES VS POPULATION - WHAT PERCENTAGE OF THE POPULATION GOT COVID
--SELECT

--TOP 30

--location, date, total_cases, population, (total_cases / population) * 100 AS 'Cases to Total Population Ratio'

--FROM MASTERDATA_COVID

--WHERE location LIKE '%zechia%' AND total_cases IS NOT NULL

--ORDER BY location, date



-- CASE 3 - COUNTRIES WITH THE HIGHEST INFECTION RATES PER POPULATION
--SELECT

--location, 
--population,
--MAX(total_cases) as 'Highest Infection Count',
--MAX((total_cases / population)) as 'Highest Cases to Total Population Ratio'

--FROM MASTERDATA_COVID

--GROUP BY location, population
--ORDER BY 'Highest Cases to Total Population Ratio' DESC


--CASE 4 - COUNTRIES WITH THE HIGHEST DEATH RATE PER POPULATION
--SELECT

--location as 'Country',
--max(cast(total_deaths as int)) as 'Total Deaths'

--FROM MASTERDATA_COVID

--WHERE continent IS NOT NULL

--GROUP BY location

--ORDER BY 'Total Deaths' DESC



--CASE 5 - BREAK DOWN BY CONTINENT
SELECT
continent,
MAX(cast(total_deaths as int)) as 'Total Deaths'

FROM MASTERDATA_COVID

WHERE continent IS NOT NULL

GROUP BY continent
ORDER BY 'Total Deaths' DESC


use covid_data_research;
GO

-- LOOKING AT TOTAL POPULATION VS VACCINATIONS
--SELECT

--continent,
--location,
--date,
--population,
--new_vaccinations,
--SUM (CONVERT (int, new_vaccinations)) OVER (PARTITION BY location ORDER BY location, date) as 'Total vaccinations per country'

--FROM MASTERDATA_COVID

--WHERE continent IS NOT NULL



-- CASE OF CREATING A TEMP TABLE - CTE

--WITH population_vs_vaccinations (continent, location, date, population, new_vaccinations, total_vaccinations_per_country)

--AS (
--    SELECT

--	TOP 400

--		continent,
--		location,
--		date,
--		population,
--		new_vaccinations,
--		SUM (CONVERT (int, new_vaccinations)) OVER (PARTITION BY location ORDER BY location, date) as 'Total vaccinations per country'
    
--	FROM MASTERDATA_COVID
--) 

--SELECT 
--	*, 
--	(total_vaccinations_per_country/population)*100 as 'Percentage of people vaccinated'
	
--FROM
--	population_vs_vaccinations





 --INSERTING DATA FROM A TEMP TABLE INTO A REAL TABLE TO PRESERVE THE DATA
--DROP TABLE IF EXISTS #percent_of_people_vaccinated
--CREATE TABLE #percent_of_people_vaccinated
--(
--	continent NVARCHAR(255),
--	location NVARCHAR(255),
--	date datetime,
--	population numeric,
--	new_vaccinations numeric,
--	total_vaccinated_per_country numeric
--)

--INSERT INTO #percent_of_people_vaccinated
--	SELECT

--		TOP 400

--			continent,
--			location,
--			date,
--			population,
--			new_vaccinations,
--			SUM (CONVERT (int, new_vaccinations)) OVER (PARTITION BY location ORDER BY location, date)
	
--	FROM MASTERDATA_COVID

--SELECT * FROM #percent_of_people_vaccinated


-- CREATING A VIEW TO STORE FOR LATER DATA VISUALIZATIONS
CREATE VIEW percentage_of_population_vaccinated
AS
SELECT

		TOP 400

			continent,
			location,
			date,
			population,
			new_vaccinations,
			SUM (CONVERT (int, new_vaccinations)) OVER (PARTITION BY location ORDER BY location, date) as 'Total people vaccinated'
	
	FROM MASTERDATA_COVID
