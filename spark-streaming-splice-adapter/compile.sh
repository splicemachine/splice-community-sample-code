#!/bin/bash -f

sm_version=${sm_version:-"2.6.1.1736"}
smjars="db-client db-shared pipeline_api splice_protocol utilities db-drda db-tools-i18n splice_access_api splice_machine splice_si_api db-engine db-tools-ij splice_encoding splicemachine-cdh5.8.3-2.1.1_2.11 splice_timestamp_api"

if [ ! -f ./pom-template.xml ]; then
    echo
    echo "ERROR: Missing ./pom-template.xml"
    echo
    exit 1
fi

sed -e "s/SM_VERSION/$sm_version/" ./pom-template.xml > ./pom.xml

mvn clean compile package -Pcdh5.8.3

if [ $? -eq 0 ]; then
    if [ -d ./target/splicemachine-jars ]; then rm -rf ./target/splicemachine-jars; fi
    mkdir ./target/splicemachine-jars
    for smjar in $smjars; do
        cp ~/.m2/repository/com/splicemachine/$smjar/$sm_version/$smjar-$sm_version.jar ./target/splicemachine-jars/
    done
fi
