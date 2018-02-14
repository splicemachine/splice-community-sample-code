#!/bin/bash

for ((cnt = 0; cnt < 2000; cnt++))
do
  echo $cnt
  sh ./run_prod.sh 
  sleep 2
done


