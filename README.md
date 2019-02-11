# Problem Statement
Remove the duplicate records from the given JSON based on the following rules. 
1. The data from the newest date should be preferred
2. duplicate IDs count as dups. Duplicate emails count as dups. Both must be unique in our dataset. Duplicate values elsewhere do not count as dups.
3. If the dates are identical the data from the record provided last in the list should be preferred.

# Solution Approach
Approach1: Given the problem, the records have two unique keys (ID, email), so if we are storing
the records in the list, then for each new record whole list need to be scan to find the available duplicate records.
This makes the O(n) time complexity for searching and O(n) for traversing each record, the total problem time complexity is O(n*n).

Approach 2: Using the two maps are providing the O(1) time complexity for searching the record based on ID and email.
First map store the key as ID and value as JSONObject, another map contains the key as email and value as ID, the total problem time complexity is O(n*1)= O(n)

# Getting Started
This project is java command line, the program starts from Application.java
Copy or clone the repository and bild the artifact or get the already build artifact from out/artifacts/marketo-first-step/marketo-first-step.jar

There are three input files are need to run the program, input json file name, output file to reconcile the dups record and change log file.By default output name is output.json and change log is change.log

# Installing
git clone https://github.com/guptapraveen2/marketo-first-step.git

## Running the build and tests
* There are Junit test cases, that demo the functionality.
* Coverage Details: Class (100%), Method(95%), Line (86%)

Unit Test:
```bash
com\marketo\processor\ReconcilerTest.java
com\marketo\utils\JsonCompareTest.java
```

Integration Test:
```bash
com\marketo\ApplicationIntegrationTest.java
```
## Deployment
java -jar out\artifacts\marketo_first_step_jar\marketo-first-step.jar
OR 
run the Application.java

# Libraries
* json-simple-1.1.1.jar for parsing the JSON  
* junit-4.12.jar for JUnit test cases.
* hamcrest-core-1.3.jar for JUnit test cases.


