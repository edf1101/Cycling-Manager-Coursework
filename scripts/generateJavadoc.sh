#! /bin/bash

# This script generates the Javadoc for the project and saves it in the docs folder.
cd ..
echo "Cleaning up the doc folder..."
rm -rf doc/*
cd src/cycling
echo "Generating Javadoc..."
javadoc -d ../../doc *.java
echo "Javadoc generated and saved in the doc folder."