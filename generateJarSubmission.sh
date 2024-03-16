#! /bin/bash

# This script generates the .jar submission and checks it runs correctly.
# make sure build and bin directories are empty
echo "Cleaning build and bin directories"
rm -rf build/*
rm -rf bin/*
echo "Compiling class files"
javac -d bin/ src/cycling/*.java
echo "Creating jar file"
jar cvf build/cycling.jar -C bin .
jar uvf build/cycling.jar -C src .
jar uvf build/cycling.jar doc
jar uvf build/cycling.jar res
echo "Running jar file with tests"
cd TestSystem
javac -cp ../build/cycling.jar CyclingPortalTestApp.java
java -cp .:../build/cycling.jar CyclingPortalTestApp
echo "Done"