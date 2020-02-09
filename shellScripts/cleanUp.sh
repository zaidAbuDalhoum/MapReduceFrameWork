#!/bin/bash

numOfMappers=$1
numOfReducers=$2


a=0;
while [ $a -lt $numOfMappers ] 
do 

docker rm mapper"$a"
    a=`expr $a + 1` 
done

rm toBeMapped*
rm -r environment
rm MappingFunction.class
rm ReducingFunction.class



b=0;
while [ $b -lt $numOfReducers ] 
do 
docker rm reducer"$b"

    b=`expr $b + 1` 
done 
exit 0;



