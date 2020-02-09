#!/bin/bash

numOfMappers=$1
numOfReducers=$2
numOfLines=$3
filePath=$4

split -d -l$3 $filePath toBeMapped

mkdir environment
a=0;
k=0;
while [ $a -lt $numOfMappers ] 
do 
mkdir environment/mapper"$a"
cp MappingFunction.java environment/mapper"$a"
cp toBeMapped"$k$a" environment/mapper"$a"/toBeMapped.txt
a=`expr $a + 1` 
if [[ $a == 9 ]] ; then
a=0;
k=`expr $k + 1` 
fi
done 

b=0;
while [ $b -lt $numOfReducers ] 
do 
mkdir environment/reducer"$b"
cp ReducingFunction.java environment/reducer$b

b=`expr $b + 1`

done 
exit 0;
