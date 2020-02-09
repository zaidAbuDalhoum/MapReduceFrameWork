#!/bin/bash

numOfReducers=$1

a=0;

while [ $a -lt $numOfReducers ] 
do 
docker inspect -f "{{ .NetworkSettings.IPAddress }}" reducer"$a" 
a=`expr $a + 1` 
done 

exit 0;
