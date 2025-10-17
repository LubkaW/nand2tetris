#!/bin/bash

set -e

echo "-- Cleaning up old build artifacts... --"
rm -rf out
mkdir -p out

echo "-- Compiling the HackAssembler... --"
javac -d out src/dev/kasal/hackassembler/*.java

echo "-- Compiling the TestAssembler... --"
javac -d out -cp out test/TestAssembler.java

echo "-- Running GoldFile Tests... --"
java -cp out TestAssembler

echo "-- Compilation completed successfully --"