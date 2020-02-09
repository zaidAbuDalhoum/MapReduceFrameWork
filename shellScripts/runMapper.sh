#!/bin/bash

mapperID=$1
IPAddress=$2
path=$(pwd)

docker run --name "mapper$mapperID" -d -v  $path/environment/mapper"$mapperID":/usr/src/app/java/volume mapreduce MappingFunction.java $IPAddress

exit 0;

