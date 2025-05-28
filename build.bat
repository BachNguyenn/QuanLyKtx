@echo off
echo Compiling project...

:: Create necessary directories
mkdir bin 2>nul
mkdir bin\main 2>nul
mkdir bin\test 2>nul

:: Compile main sources
javac -d bin/main -cp src src/model/*.java src/util/*.java src/gui/*.java

:: Compile test sources
javac -d bin/test -cp "bin/main;lib/junit-4.13.2.jar;lib/hamcrest-core-1.3.jar" src/test/model/*.java src/test/util/*.java

:: Run tests
java -cp "bin/test;bin/main;lib/junit-4.13.2.jar;lib/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore test.model.StudentTest test.model.RoomTest test.model.ContractTest test.model.FeeTest test.util.DataStorageTest test.util.ReportGeneratorTest

echo Build complete. 