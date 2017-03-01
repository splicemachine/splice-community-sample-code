# Overview
The Insurance Company Benchmark data set is from the UCI Machine Learning Repository (https://archive.ics.uci.edu/ml/datasets/Insurance+Company+Benchmark+%28COIL+2000%29).  This data set was used in the CoIL 2000 Challenge and contains information on customers for an insurance company. The data consists of 86 variables and includes product usage data and socio-demographic data.  The data was supplied by the Dutch data mining company Sentient Machine Research and is based on a real world business problem. The training set contains over 5000 descriptions of customers, including the information of whether or not they have a caravan insurance policy. A test set contains 4000 customers of whom only the organisers know if they have a caravan insurance policy. 

# Data Set Properties
|Property|Value|
|----|----|
|Data Set Characteristics|Multivariate|
|Attribute Characteristics|Categorical, Integer|
|Associated Tasks|Regression, Description|
|Number of Attributes|86|

# Tables
The insurance data is comprised of 9 tables: 

* **INSURANCE.TRAINING_DATA**: Contains the census data that is used to train the model - 4,999 records
* **INSURANCE.TESTING_DATA**: Contains the census data that is used to test the model - 823 records
* **INSURANCE.LIVE_DATA**: Contains the data that is used when running the model to predict values in real time. - 16,062 records
* **INSURANCE.LIVE_DATA_EXPECTED**: The expected values for a current prediction of the LIVE_DATA - 4,000 records
* **INSURANCE.DOMAIN_CUSTOMER_SUBTYPE**:  Domain values for the customer subtype column - 41 records
* **INSURANCE.DOMAIN_AVERAGE_AGE**: Domain values for the average age column - 6 records
* **INSURANCE.DOMAIN_CUSTOMER_TYPE**: Domain values for the customer type column - 10 records
* **INSURANCE.DOMAIN_CATHOLIC**:  Domain values for the catholic column - 10 records
* **INSURANCE.DOMAIN_PRIVATE_PARTY_INSURANCE**:  Domain values for the private party insurance column - 10 records

## Data / Table Structure
All TRAINING_DATA,TESTING_DATA and LIVE_DATA  tables have the similar definitions - the only difference is that LIVE_DATA has an ID column at the beginning to be able to match it to records in the LIVE_DATA_EXPECTED table.


|Column Number|Source Name|Source Description|Splice Machine Column|Splice Machine Data Type|
|----|----|----|----|----|
|1|MOSTYPE |Customer Subtype (domain)|CUSTOMER_SUBTYPE|INTEGER|
|2|MAANTH|Number of houses 1 - 10|NUM_HOUSES|INTEGER|
|3|MGE|Avg size household 1 - 6|AVG_SIZE_HOUSEHOLD|INTEGER|
|4|MGE|Avg age see L1|AVG_AGE|INTEGER|
|5|MOSHOOFD|Customer main type see L2|CUSTOMER_MAIN_TYPE|INTEGER|
|6|MGODR|Roman catholic see L3|ROMAN_CATHOLIC|INTEGER|
|7|MGODPR Pro|Protestant ...|PROTESTANT|INTEGER|
|8|MGODO|Other religion|OTHER_RELIGION|INTEGER|
|9|MG|No religion|NO_RELIGION|INTEGER|
|10|MRELGE|Married|MARRIED|INTEGER|
|11|MRELSA|Living together|LIVING_TOGETHER|INTEGER|
|12|MRELOV|Other relation|OTHER_RELATION|INTEGER|
|13|MFALLEEN|Singles|SINGLES|INTEGER|
|14|MFGEKIND|Household without children|HOUSEHOLD_WITHOUT_CHILDREN|INTEGER|
|15|MFWEKIND|Household with children|HOUSEHOLD_WITH_CHILDREN|INTEGER|
|16|MOPLHOOG|High level education|HIGH_LEVEL_EDUCATION|INTEGER|
|17|MOPLMIDD|Medium level education|MEDIUM_LEVEL_EDUCATION|INTEGER|
|18|MOPLLAAG|Lower level education|LOWER_LEVEL_EDUCATION|INTEGER|
|19|MBERHOOG|High status|HIGH_STATUS|INTEGER|
|20|MBERZELF|Entrepreneur|ENTREPRENEUR|INTEGER|
|21|MBERBOER|Farmer|FARMER|INTEGER|
|22|MBERMIDD|Middle management|MIDDLE_MANAGEMENT|INTEGER|
|23|MBERARBG|Skilled labourers|SKILLED_LABOURERS|INTEGER|
|24|MBERARBO|Unskilled labourers|UNSKILLED_LABOURERS|INTEGER|
|25|MSKA|Social class A|SOCIAL_CLASS_A|INTEGER|
|26|MSKB1|Social class B1|SOCIAL_CLASS_B1|INTEGER|
|27|MSKB2|Social class B2|SOCIAL_CLASS_B2|INTEGER|
|28|MSKC|Social class C|SOCIAL_CLASS_C|INTEGER|
|29|MSKD|Social class D|SOCIAL_CLASS_D|INTEGER|
|30|MHHUUR|Rented house|RENTED_HOUSE|INTEGER|
|31|MHKOOP|Home owners|HOME_OWNERS|INTEGER|
|32|MAUT1|1 car|CARS_1|INTEGER|
|33|MAUT2|2 cars|CARS_2|INTEGER|
|34|MAUT0|No car|NO_CHAR|INTEGER|
|35|MZFONDS|National Health Service|NATIONAL_HEALTH_SERVICE|INTEGER|
|36|MZPART|Private health insurance|PRIVATE_HEALTH_INSURANCE|INTEGER|
|37|MINKM30|Income < 30.000|INCOME_UNDER_30|INTEGER|
|38|MINK3045|Income 30-45.000|INCOME_30_TO_45|INTEGER|
|39|MINK4575|Income 45-75.000|INCOME_45_TO_75|INTEGER|
|40|MINK7512|Income 75-122.000|INOCME_75_TO_112|INTEGER|
|41|MINK123M|Income >123.000|INCOME_OVER_123|INTEGER|
|42|MINKGEM|Average income|AVERAGE_INCOME|INTEGER|
|43|MKOOPKLA|Purchasing power class|PURCHASING_POWER_CLASS|INTEGER|
|44|PWAPART|Contribution private third party insurance see L4|CONTRIB_PRIVATE_3RD_PARTY_INSURANCE|INTEGER|
|45|PWABEDR|Contribution third party insurance (firms) ...|CONTRIB_3RD_PARTY_INSURANCE_FIRMS|INTEGER|
|46|PWALAND|Contribution third party insurane (agriculture)|CONTRIB_3RD_PARTY_INSURANCE_AGRICULTURE|INTEGER|
|47|PPERSAUT|Contribution car policies|CONTRIB_CAR_POLICIES|INTEGER|
|48|PBESAUT|Contribution delivery van policies|CONTRIB_DELIVERY_VAN_POLICIES|INTEGER|
|49|PMOTSCO|Contribution motorcycle/scooter policies|CONTRIB_MOTORCYCLE_SCOOTER_POLICIES|INTEGER|
|50|PVRAAUT|Contribution lorry policies|CONTRIB_LORRY_POLICIES|INTEGER|
|51|PAANHANG|Contribution trailer policies|CONTRIB_TRAILER_POLICIES|INTEGER|
|52|PTRACTOR|Contribution tractor policies|CONTRIB_TRACTOR_POLICIES|INTEGER|
|53|PWERKT|Contribution agricultural machines policies|CONTRIB_AGRICULTURAL_MACHINES_POLICIES|INTEGER|
|54|PBROM|Contribution moped policies|CONTRIB_MOPED_POLICIES|INTEGER|
|55|PLEVEN|Contribution life insurances|CONTRIB_LIFE_INSURANCE_POLICIES|INTEGER|
|56|PPERSONG|Contribution private accident insurance policies|CONTRIB_PRIVATE_ACCIDENT_INSURANCE_POLICIES|INTEGER|
|57|PGEZONG|Contribution family accidents insurance policies|CONTRIB_FAMILY_ACCIDENTS_INSURANCE_POLICIES|INTEGER|
|58|PWAOREG|Contribution disability insurance policies|CONTRIB_DISABILITY_INSURANCE_POLICIES|INTEGER|
|59|PBRAND|Contribution fire policies|CONTRIB_FIRE_POLICIES|INTEGER|
|60|PZEILPL|Contribution surfboard policies|CONTRIB_SURFACE_POLICIES|INTEGER|
|61|PPLEZIER|Contribution boat policies|CONTRIB_BOAT_POLICIES|INTEGER|
|62|PFIETS|Contribution bicycle policies|CONTRIB_BICYCLE_POLICIES|INTEGER|
|63|PINBOED|Contribution property insurance policies|CONTRIB_PORPERTY_INSURANCE_POLICIES|INTEGER|
|64|PBYSTAND|Contribution social security insurance policies|CONTRIB_SOCIAL_SECURITY_INSURANCE_POLICIES|INTEGER|
|65|AWAPART|Number of private third party insurance 1 - 12|NUM_PRIVATE_3RD_PARTY_INSURANCE|INTEGER|
|66|AWABEDR|Number of third party insurance (firms) ...|NUM_PRIVATE_3RD_PARTY_INSURANCE_FIRMS|INTEGER|
|67|AWALAND|Number of third party insurance (agriculture)|NUM_PRIVATE_3RD_PARTY_INSURANCE_AGRICULTURE|INTEGER|
|68|APERSAUT|Number of car policies|NUM_CAR_POLICIES|INTEGER|
|69|ABESAUT|Number of delivery van policies|NUM_DELIVERY_VAN_POLICIES|INTEGER|
|70|AMOTSCO|Number of motorcycle/scooter policies|NUM_MOTORCYCLE_SCOOTER_POLICIES|INTEGER|
|71|AVRAAUT|Number of lorry policies|NUM_LORRY_POLICIES|INTEGER|
|72|AAANHANG|Number of trailer policies|NUM_TRAILER_POLICIES|INTEGER|
|73|ATRACTOR|Number of tractor policies|NUM_TRACTOR_POLICIES|INTEGER|
|74|AWERKT|Number of agricultural machines policies|NUM_AGRICULTURAL_MACHINES_POLICIES|INTEGER|
|75|ABROM|Number of moped policies|NUM_MOPED_POLICIES|INTEGER|
|76|ALEVEN|Number of life insurances|NUM_LIFE_INSURANCE|INTEGER|
|77|APERSONG|Number of private accident insurance policies|NUM_PRIVATE_ACCIDENT_INSURANCE_POLICIES|INTEGER|
|78|AGEZONG|Number of family accidents insurance policies|NUM_FAMILY_ACCIDENT_INSURANCE_POLICIES|INTEGER|
|79|AWAOREG|Number of disability insurance policies|NUM_DISABILITY_INSURANCE_POLICIES|INTEGER|
|80|ABRAND|Number of fire policies|NUM_FIRE_POLICIES|INTEGER|
|81|AZEILPL|Number of surf board policies|NUM_SURFBOARD_POLICIES|INTEGER|
|82|APLEZIER|Number of boat policies|NUM_BOAT_POLICIES|INTEGER|
|83|AFIETS|Number of bicycle policies|NUM_BICYCLE_POLICIES|INTEGER|
|84|AINBOED|Number of property insurance policies|NUM_PROPERTY_INSURANCE_POLICIES|INTEGER|
|85|ABYSTAND|Number of social security insurance policies|NUM_SOCIAL_SECURITY_INS_POLICIES|INTEGER|
|86|CARAVAN|Number of mobile home policies 0 - 1|NUM_MOBILE_HOME|INTEGER|

* **INSURANCE.LIVE_DATA_EXPECTED**: has two columns ID and EXPECTED_RESULT
* **INSURANCE.DOMAIN_xxx**: tables have two columns VALUE and LABEL

## Sample Data
|CUSTOMER_SUBTYPE|NUM_HOUSES|AVG_SIZE_HOUSEHOLD|AVG_AGE|CUSTOMER_MAIN_TYPE|ROMAN_CATHOLIC|PROTESTANT|OTHER_RELIGION|NO_RELIGION|MARRIED|LIVING_TOGETHER|OTHER_RELATION|SINGLES|HOUSEHOLD_WITHOUT_CHILDREN|HOUSEHOLD_WITH_CHILDREN|HIGH_LEVEL_EDUCATION|MEDIUM_LEVEL_EDUCATION|LOWER_LEVEL_EDUCATION|HIGH_STATUS|ENTREPRENEUR|FARMER|MIDDLE_MANAGEMENT|SKILLED_LABOURERS|UNSKILLED_LABOURERS|SOCIAL_CLASS_A|SOCIAL_CLASS_B1|SOCIAL_CLASS_B2|SOCIAL_CLASS_C|SOCIAL_CLASS_D|RENTED_HOUSE|HOME_OWNERS|CARS_1|CARS_2|NO_CHAR|NATIONAL_HEALTH_SERVICE|PRIVATE_HEALTH_INSURANCE|INCOME_UNDER_30|INCOME_30_TO_45|INCOME_45_TO_75|INOCME_75_TO_112|INCOME_OVER_123|AVERAGE_INCOME|PURCHASING_POWER_CLASS|CONTRIB_PRIVATE_3RD_PARTY_INSURANCE|CONTRIB_3RD_PARTY_INSURANCE_FIRMS|CONTRIB_3RD_PARTY_INSURANCE_AGRICULTURE|CONTRIB_CAR_POLICIES|CONTRIB_DELIVERY_VAN_POLICIES|CONTRIB_MOTORCYCLE_SCOOTER_POLICIES|CONTRIB_LORRY_POLICIES|CONTRIB_TRAILER_POLICIES|CONTRIB_TRACTOR_POLICIES|CONTRIB_AGRICULTURAL_MACHINES_POLICIES|CONTRIB_MOPED_POLICIES|CONTRIB_LIFE_INSURANCE_POLICIES|CONTRIB_PRIVATE_ACCIDENT_INSURANCE_POLICIES|CONTRIB_FAMILY_ACCIDENTS_INSURANCE_POLICIES|CONTRIB_DISABILITY_INSURANCE_POLICIES|CONTRIB_FIRE_POLICIES|CONTRIB_SURFACE_POLICIES|CONTRIB_BOAT_POLICIES|CONTRIB_BICYCLE_POLICIES|CONTRIB_PORPERTY_INSURANCE_POLICIES|CONTRIB_SOCIAL_SECURITY_INSURANCE_POLICIES|NUM_PRIVATE_3RD_PARTY_INSURANCE|NUM_PRIVATE_3RD_PARTY_INSURANCE_FIRMS|NUM_PRIVATE_3RD_PARTY_INSURANCE_AGRICULTURE|NUM_CAR_POLICIES|NUM_DELIVERY_VAN_POLICIES|NUM_MOTORCYCLE_SCOOTER_POLICIES|NUM_LORRY_POLICIES|NUM_TRAILER_POLICIES|NUM_TRACTOR_POLICIES|NUM_AGRICULTURAL_MACHINES_POLICIES|NUM_MOPED_POLICIES|NUM_LIFE_INSURANCE|NUM_PRIVATE_ACCIDENT_INSURANCE_POLICIES|NUM_FAMILY_ACCIDENT_INSURANCE_POLICIES|NUM_DISABILITY_INSURANCE_POLICIES|NUM_FIRE_POLICIES|NUM_SURFBOARD_POLICIES|NUM_BOAT_POLICIES|NUM_BICYCLE_POLICIES|NUM_PROPERTY_INSURANCE_POLICIES|NUM_SOCIAL_SECURITY_INS_POLICIES|NUM_MOBILE_HOME|
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
|33|1|3|2|8|0|5|1|3|7|0|2|1|2|6|1|2|7|1|0|1|2|5|2|1|1|2|6|1|1|8|8|0|1|8|1|0|4|5|0|0|4|3|0|0|0|6|0|0|0|0|0|0|0|0|0|0|0|5|0|0|0|0|0|0|0|0|1|0|0|0|0|0|0|0|0|0|0|0|1|0|0|0|0|0|0|
|37|1|2|2|8|1|4|1|4|6|2|2|0|4|5|0|5|4|0|0|0|5|0|4|0|2|3|5|0|2|7|7|1|2|6|3|2|0|5|2|0|5|4|2|0|0|0|0|0|0|0|0|0|0|0|0|0|0|2|0|0|0|0|0|2|0|0|0|0|0|0|0|0|0|0|0|0|0|0|1|0|0|0|0|0|0|
|37|1|2|2|8|0|4|2|4|3|2|4|4|4|2|0|5|4|0|0|0|7|0|2|0|5|0|4|0|7|2|7|0|2|9|0|4|5|0|0|0|3|4|2|0|0|6|0|0|0|0|0|0|0|0|0|0|0|2|0|0|0|0|0|1|0|0|1|0|0|0|0|0|0|0|0|0|0|0|1|0|0|0|0|0|0|


