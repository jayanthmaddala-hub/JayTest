# Rentoza Automation Challenge

## Overview
This repository contains automated tests for:
- UI Functional Testing (Selenium Java + TestNG)
- API Testing (GraphQL using Java HTTP/RestAssured)
- Optional Performance Testing

## Project Structure
- `src/test/java/ui` → Selenium UI tests (Task 1, Task 2)
- `src/test/java/api` → API GraphQL validation tests
- `src/test/java/performance` → Load/stress testing notes/scripts
- `reports` → Screenshots and test reports

## Prerequisites
- Java 11+
- Maven
- Google Chrome + ChromeDriver

## Run Tests
```bash
mvn clean test
```
