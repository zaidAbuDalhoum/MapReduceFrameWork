#!/bin/bash

reducerID=$1
IPAddress=$2
path=$(pwd)

if [[ $reducerID -lt 10 ]] ; then

docker run --name "reducer$reducerID" -d -p 800"$reducerID":8080 -v $path/environment/reducer"$reducerID":/usr/src/app/java/volume mapreduce ReducingFunction.java $IPAddress

elif [[ $reducerID -gt 10 ]] || [[ $reducerID -eq 10 ]]  ; then

docker run --name "reducer$reducerID" -d -p 80"$reducerID":8080 -v $path/environment/reducer"$reducerID":/usr/src/app/java/volume mapreduce ReducingFunction.java $IPAddress

fi 
exit 0;

