#!/bin/bash

if [ $# != 1 ]
then 
	echo "Usar: ./practica2.sh -opcion"
else
	if [ $1 -eq "-1" ]
	then
		javac -cp ./lib/tika-app-2.9.0.jar:./lib/lucene-9.3.0/modules/lucene-core-9.3.0.jar:./lib/lucene-9.3.0/modules/lucene-analysis-common-9.3.0.jar ./src/Ejercicio1.java 
		java -cp ./lib/tika-app-2.9.0.jar:./lib/lucene-9.3.0/modules/lucene-core-9.3.0.jar:./src/:./lib/lucene-9.3.0/modules/lucene-analysis-common-9.3.0.jar Ejercicio1 ./documentos

	elif [ $1 -eq "-2" ]
	then
		javac -cp ./lib/tika-app-2.9.0.jar:./lib/lucene-9.3.0/modules/lucene-core-9.3.0.jar:./lib/lucene-9.3.0/modules/lucene-analysis-common-9.3.0.jar ./src/Ejercicio2.java
		java -cp ./lib/tika-app-2.9.0.jar:./lib/lucene-9.3.0/modules/lucene-core-9.3.0.jar:./src/:./lib/lucene-9.3.0/modules/lucene-analysis-common-9.3.0.jar Ejercicio2

	elif [ $1 -eq "-3" ]
	then
		javac -cp ./lib/tika-app-2.9.0.jar:./lib/lucene-9.3.0/modules/lucene-core-9.3.0.jar:./lib/lucene-9.3.0/modules/lucene-analysis-common-9.3.0.jar ./src/Ejercicio3.java
		java -cp ./lib/tika-app-2.9.0.jar:./lib/lucene-9.3.0/modules/lucene-core-9.3.0.jar:./src/:./lib/lucene-9.3.0/modules/lucene-analysis-common-9.3.0.jar Ejercicio3 ./documentos
	elif [ $1 -eq "-4" ]
	then
		javac -cp ./lib/tika-app-2.9.0.jar:./lib/lucene-9.3.0/modules/lucene-core-9.3.0.jar:./lib/lucene-9.3.0/modules/lucene-analysis-common-9.3.0.jar ./src/Ejercicio4.java
		java -cp ./lib/tika-app-2.9.0.jar:./lib/lucene-9.3.0/modules/lucene-core-9.3.0.jar:./src/:./lib/lucene-9.3.0/modules/lucene-analysis-common-9.3.0.jar Ejercicio4
	elif [ $1 -eq "-5" ]
	then 
		rm -rf ./Estadisticas
		rm -rf ./AnalizadorPersonalizado
	else
		echo "Error: no has usado una opcion [-1, -2. -3, -4]."
	fi
fi

