#!/bin/bash

if [ "$1" == "indice" ]; then
	javac -cp lucene-9.3.0/modules/lucene-core-9.3.0.jar:commons-lang3-3.12.0.jar:opencsv-5.8.jar Indice.java

elif [ "$1" == "busqueda" ]; then
	javac -cp lucene-9.3.0/modules/lucene-core-9.3.0.jar:commons-lang3-3.12.0.jar:lucene-9.3.0/modules/lucene-queryparser-9.3.0.jar IndiceSearcher.java
	
else
	echo "Opción no válida. Pruebe con: [ ./ejecutar.sh indice ó ./ejecutar.sh busqueda]"
fi
