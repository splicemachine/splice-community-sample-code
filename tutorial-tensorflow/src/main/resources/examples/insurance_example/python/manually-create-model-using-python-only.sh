MODEL_DIR="/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/insurance_out"
MODEL_TYPE="wide_n_deep"
TRAIN_STEPS=200
TRAIN_DATA="/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/python/data/train/part-r-00000.csv"
TEST_DATA="/Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/python/data/test/part-r-00000.csv"
HASH_BUCKET_SIZE=100
DIMENSION=8
DNN_HIDDEN_UNITS="100, 50"
INPUT_DATA="{\"columns\": [  \"average_income\",  \"avg_age\",  \"avg_size_household\",  \"cars_1\",  \"cars_2\",  \"contrib_3rd_party_insurance_agriculture\",  \"contrib_3rd_party_insurance_firms\",  \"contrib_agricultural_machines_policies\",  \"contrib_bicycle_policies\",  \"contrib_boat_policies\",  \"contrib_car_policies\",  \"contrib_delivery_van_policies\",  \"contrib_disability_insurance_policies\",  \"contrib_family_accidents_insurance_policies\",  \"contrib_fire_policies\",  \"contrib_life_insurance_policies\",  \"contrib_lorry_policies\",  \"contrib_moped_policies\",  \"contrib_motorcycle_scooter_policies\",  \"contrib_porperty_insurance_policies\",  \"contrib_private_3rd_party_insurance\",  \"contrib_private_accident_insurance_policies\",  \"contrib_social_security_insurance_policies\",  \"contrib_surface_policies\",  \"contrib_tractor_policies\",  \"contrib_trailer_policies\",  \"customer_main_type\",  \"customer_subtype\",  \"entrepreneur\",  \"farmer\",  \"high_level_education\",  \"high_status\",      \"home_owners\",  \"household_without_children\",  \"household_with_children\",  \"income_30_to_45\",  \"income_45_to_75\",  \"income_over_123\",  \"income_under_30\",  \"inocme_75_to_112\",  \"living_together\",  \"lower_level_education\",  \"married\",  \"medium_level_education\",  \"middle_management\",  \"national_health_service\",  \"no_char\",  \"no_religion\",  \"num_agricultural_machines_policies\",  \"num_bicycle_policies\",  \"num_boat_policies\",  \"num_car_policies\",  \"num_delivery_van_policies\",  \"num_disability_insurance_policies\",  \"num_family_accident_insurance_policies\",  \"num_fire_policies\",  \"num_houses\",  \"num_life_insurance\",  \"num_lorry_policies\",  \"num_mobile_home\",  \"num_moped_policies\",  \"num_motorcycle_scooter_policies\",  \"num_private_3rd_party_insurance\",  \"num_private_3rd_party_insurance_agriculture\",  \"num_private_3rd_party_insurance_firms\",  \"num_private_accident_insurance_policies\",  \"num_property_insurance_policies\",  \"num_social_security_ins_policies\",  \"num_surfboard_policies\",  \"num_tractor_policies\",  \"num_trailer_policies\",  \"other_relation\",  \"other_religion\",  \"private_health_insurance\",  \"protestant\",  \"purchasing_power_class\",  \"rented_house\",  \"roman_catholic\",  \"singles\",  \"skilled_labourers\",  \"social_class_a\",  \"social_class_b1\",  \"social_class_b2\",  \"social_class_c\",  \"social_class_d\",  \"unskilled_labourers\"],\"continuous_columns\": [  \"protestant\",  \"skilled_labourers\",  \"income_45_to_75\",  \"contrib_life_insurance_policies\",  \"num_lorry_policies\",  \"other_religion\",  \"unskilled_labourers\",  \"inocme_75_to_112\",  \"contrib_private_accident_insurance_policies\",  \"num_trailer_policies\",  \"no_religion\",  \"social_class_a\",  \"income_over_123\",  \"contrib_family_accidents_insurance_policies\",  \"num_tractor_policies\",  \"married\",  \"social_class_b1\",  \"average_income\",  \"contrib_disability_insurance_policies\",  \"num_agricultural_machines_policies\",  \"living_together\",  \"social_class_b2\",  \"purchasing_power_class\",  \"contrib_fire_policies\",  \"num_moped_policies\",  \"other_relation\",  \"social_class_c\",  \"contrib_private_3rd_party_insurance\",  \"contrib_surface_policies\",  \"num_life_insurance\",  \"singles\",  \"social_class_d\",  \"contrib_3rd_party_insurance_firms\",  \"contrib_boat_policies\",  \"num_private_accident_insurance_policies\",  \"household_without_children\",  \"rented_house\",  \"contrib_3rd_party_insurance_agriculture\",  \"contrib_bicycle_policies\",  \"num_family_accident_insurance_policies\",  \"household_with_children\",  \"home_owners\",  \"contrib_car_policies\",  \"contrib_porperty_insurance_policies\",  \"num_disability_insurance_policies\",  \"high_level_education\",  \"cars_1\",  \"contrib_delivery_van_policies\",  \"contrib_social_security_insurance_policies\",  \"num_fire_policies\",  \"customer_subtype\",  \"medium_level_education\",  \"cars_2\",  \"contrib_motorcycle_scooter_policies\",  \"num_private_3rd_party_insurance\",  \"num_surfboard_policies\",  \"num_houses\",  \"lower_level_education\",  \"no_char\",  \"contrib_lorry_policies\",  \"num_private_3rd_party_insurance_firms\",  \"num_boat_policies\",  \"avg_size_household\",  \"high_status\",  \"national_health_service\",  \"contrib_trailer_policies\",  \"num_private_3rd_party_insurance_agriculture\",  \"num_bicycle_policies\",  \"avg_age\",  \"entrepreneur\",  \"private_health_insurance\",  \"contrib_tractor_policies\",  \"num_car_policies\",  \"num_property_insurance_policies\",  \"customer_main_type\",  \"farmer\",  \"income_under_30\",  \"contrib_agricultural_machines_policies\",  \"num_delivery_van_policies\",  \"num_social_security_ins_policies\",  \"roman_catholic\",  \"middle_management\",  \"income_30_to_45\",  \"contrib_moped_policies\",  \"num_motorcycle_scooter_policies\"],\"label_column\": \"num_mobile_home\"}"


cd /Users/erindriggers/dev/workspace/tensorflow/splice-community-sample-code/tutorial-tensorflow/src/main/resources/python

python  Tensor-Demo.py \
	--model_type=$MODEL_TYPE \
	--hash_bucket_size=$HASH_BUCKET_SIZE \
	--dimension=$DIMENSION \
	--train_data=$TRAIN_DATA \
	--test_data=$TEST_DATA \
	--dnn_hidden_units="$DNN_HIDDEN_UNITS" \
	--model_dir=$MODEL_DIR \
	--inputs="$INPUT_DATA"
