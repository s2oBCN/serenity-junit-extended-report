## Serenity jUnit Extended Report

The intention of this module is to produce xUnit files for Serenity-Cucumber executions, it will produce one XML file per Scenario. Where:
* The testSuite will be the definition of the scenario.
* A step of the Cucumber scenario will be a testCase
* The tags of the scenario will be the <properties> of the testSuite.

In our case, we use this information in order to synchronize our tests with HPALM.