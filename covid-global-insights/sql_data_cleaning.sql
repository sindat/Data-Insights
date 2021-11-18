-- Data source: https://ourworldindata.org/covid-deaths

use covid_data_research;



-- CLEANING 1 - CREATING A COLUMN WITH A DATE FORMAT WITHOUT TIME, AS ALL THE TIMESTAMPS WERE 00:00 ON THE SaleDate COLUMN
ALTER TABLE MASTERDATA_HOUSING_NASHVILLE
    ADD SaleDateNoTimestamp Date

UPDATE MASTERDATA_HOUSING_NASHVILLE
    SET SaleDateNoTimestamp = CONVERT(DATE,SaleDate)



-- CLEANING 2 - FILL IN MISSING ADDRESS DATA IF IDENTICAL HOUSE WITH ADDRESS IS FOUND

-- FIND MISSING PropertyAddress FIELDS
SELECT
	a.[UniqueID ],
    a.ParcelID,
    a.PropertyAddress,
    b.[UniqueID ],
    b.ParcelID,
    b.PropertyAddress

FROM MASTERDATA_HOUSING_NASHVILLE a

JOIN MASTERDATA_HOUSING_NASHVILLE b

	ON
	    a.ParcelID = b.ParcelID -- match on ParcelID, same property
	AND
        a.[UniqueID] <> b.[UniqueID] -- make sure identical row is not matched to find unique rows for the same property

WHERE b.PropertyAddress IS NULL

-- UPDATE MISSING PropertyAddress FIELDS USING THE SAME LOGIC
BEGIN TRAN
    UPDATE a

        SET a.PropertyAddress = b.PropertyAddress

            FROM MASTERDATA_HOUSING_NASHVILLE a
                
                JOIN MASTERDATA_HOUSING_NASHVILLE b
                    
                    ON
                        a.ParcelID = b.ParcelID -- match on ParcelID, same property
                    AND
                        a.[UniqueID ] <> b.[UniqueID ] -- make sure identical row is not matched to find unique rows for the same property
        
        WHERE a.PropertyAddress IS NULL -- updating rows where the PropertyAddress is empty
COMMIT


-- CLEANING 3 - SEPARATE PropertyAddress INTO STREET AND CITY, WHICH ARE SEPARATED BY A COMMA

-- DISPLAY THE SEPARATED VALUES
SELECT

	SUBSTRING(PropertyAddress, 1, CHARINDEX(',', PropertyAddress) - 1) as 'Street',
	SUBSTRING(PropertyAddress, CHARINDEX(',', PropertyAddress) + 1, LEN(PropertyAddress)) as 'City'

FROM MASTERDATA_HOUSING_NASHVILLE


-- ADD THE NEW TABLE COLUMNS THAT STORE THE NEW SEPARATED STREET AND CITY ATTRIBUTES
ALTER TABLE MASTERDATA_HOUSING_NASHVILLE
    ADD Street NVARCHAR(255)

UPDATE MASTERDATA_HOUSING_NASHVILLE
    SET Street = SUBSTRING(PropertyAddress, 1, CHARINDEX(',', PropertyAddress) - 1)


ALTER TABLE MASTERDATA_HOUSING_NASHVILLE
    ADD City NVARCHAR(255)

UPDATE MASTERDATA_HOUSING_NASHVILLE
    SET City = SUBSTRING(PropertyAddress, CHARINDEX(',', PropertyAddress) + 1, LEN(PropertyAddress))




-- CLEANING 4 - SEPARATE OUT THE OWNER'S ADDRESS INTO 3 PARTS, CURRENTLY IT IS IN ONE ROW SEPARATED BY A COMMA

/****************************************************************************************************************
    -- NOTE: PARSENAME TAKES THE FIRST VALUES VIA A DELIMITER IN REVERSE
    -- THEREFORE STREET, WHICH IS THE FIRST VALUE, IS UNDER INDEX 3, AS WE HAVE 3 SEPARATED VALUES
*****************************************************************************************************************/

-- SEPARATION INTO 3 VALUES - STREET (3), CITY (2), STATE (1) - DISPLAY THE SEPARATED VALUES
SELECT
	PARSENAME(REPLACE(OwnerAddress, ',', '.'),3),
	PARSENAME(REPLACE(OwnerAddress, ',', '.'),2),
	PARSENAME(REPLACE(OwnerAddress, ',', '.'),1)  
FROM
	MASTERDATA_HOUSING_NASHVILLE

-- ADD THE NECESSARY TABLE COLUMNS WHICH HOST THE SEPARATED VALUES
ALTER TABLE MASTERDATA_HOUSING_NASHVILLE
    ADD OwnerAddressStreet VARCHAR(255)

UPDATE MASTERDATA_HOUSING_NASHVILLE
    SET OwnerAddressStreet = PARSENAME(REPLACE(OwnerAddress, ',', '.'),3)


ALTER TABLE MASTERDATA_HOUSING_NASHVILLE
    ADD OwnerAddressCity VARCHAR(255)

UPDATE MASTERDATA_HOUSING_NASHVILLE
    SET OwnerAddressCity = PARSENAME(REPLACE(OwnerAddress, ',', '.'),2)


ALTER TABLE MASTERDATA_HOUSING_NASHVILLE
    ADD OwnerAddressState VARCHAR(255)

UPDATE MASTERDATA_HOUSING_NASHVILLE
    SET OwnerAddressState = PARSENAME(REPLACE(OwnerAddress, ',', '.'),1)



-- CLEANING 5 - REGULATE TERMS - IN THIS CASE YES AND NO
/*
    WE WANT ONLY YES AND NO IN THE TABLE, CURRENTLY THERE ARE:
        - YES
        - NO
        - Y
        - N

    WE FIND OUT WHICH ONES ARE MORE USED, AND TRANSFER THE OTHER ONES INTO THE SAME FORMAT
*/


SELECT 
    SoldAsVacant,
    Count(*)
FROM MASTERDATA_HOUSING_NASHVILLE
    GROUP BY SoldAsVacant
    ORDER BY 2

SELECT SoldAsVacant,

	CASE When SoldAsVacant = 'Y' THEN 'Yes'
		 When SoldAsVacant = 'N' THEN 'No'
	ELSE
		SoldAsVacant
	END

FROM MASTERDATA_HOUSING_NASHVILLE

UPDATE MASTERDATA_HOUSING_NASHVILLE
SET
	SoldAsVacant =
		CASE When SoldAsVacant = 'Y' THEN 'Yes'
			 When SoldAsVacant = 'N' THEN 'No'
		ELSE
			SoldAsVacant
		END

SELECT SoldAsVacant
FROM MASTERDATA_HOUSING_NASHVILLE
    WHERE SoldAsVacant = 'N'

SELECT
	SoldAsVacant,
	Count(*)
FROM MASTERDATA_HOUSING_NASHVILLE
GROUP BY SoldAsVacant



-- CLEANING 6 - REMOVING DUPLICATES
-- ALL ROWS WITH COUNTED ROW GREATER THAN 1 MEAN THEY ARE THERE MULTIPLE TIMES AS PER THE PARTITION PARAMETERS
WITH EliminateDuplicatesWithRowCount AS(

SELECT 
	*,
	ROW_NUMBER() OVER (
		PARTITION BY ParcelID,
					PropertyAddress,
					SalePrice,
					SaleDate,
					LegalReference
					ORDER BY UniqueID
	) row_to_eliminate_as_duplicate 

FROM MASTERDATA_HOUSING_NASHVILLE
)

SELECT * FROM EliminateDuplicatesWithRowCount
    WHERE row_to_eliminate_as_duplicate != 1

-- DELETE THE DUPLICATES
DELETE FROM EliminateDuplicatesWithRowCount
    WHERE row_to_eliminate_as_duplicate != 1



-- OPTIONAL - REMOVE UNUSED COLUMNS
--ALTER TABLE MASTERDATA_HOUSING_NASHVILLE
--DROP COLUMN SaleDate
