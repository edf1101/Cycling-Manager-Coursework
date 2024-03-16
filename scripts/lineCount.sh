#! /bin/bash

# This script how many lines of code are in the project.
cd ..

echo "Counting lines of .java ..."
cat src/cycling/*.java | wc -l

echo "Counting lines of .tex ..."
cat *.tex | wc -l

echo "Counting lines of .sh ..."
cat scripts/*.sh | wc -l