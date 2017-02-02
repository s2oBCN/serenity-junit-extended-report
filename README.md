## Serenity jUnit Extended Report

The intention of this module is to produce xUnit files for Serenity-Cucumber executions, it will produce one XML file per Scenario. Where:
* The testSuite will be the definition of the scenario.
* A step of the Cucumber scenario will be a testCase
* The tags of the scenario will be the <properties> of the testSuite.

In our case, we use this information in order to synchronize our tests with HPALM.

Here is an example of an Scenario:

```cucumber
@InternalTcId("399F2B4C-0D49-4C08-8309-0EA998DD4A15")
@Author("user1")
@Reviewer("user2")
@ReviewDate("2017-01-16T14:41:21")
@Requirement("TRQ3049,TRQ12345")
@Nic("17124,123123")
Scenario: kdasñkdadska
Given don pepito and don Jose
When don Jose says 'hola don Pepito'
Then don Pepito reponds 'hola don Jose'
alert(s);
```

an its xUnit xml:
```xml
<testsuites>
    <testsuite name="test.unit.lib.Comparators...." timestamp="2017-01-20T09:03:26Z" tests="3" failures="0" errors="0" time="0.000173">
        <properties>
            <property name="InternalTcId" value="399F2B4C-0D49-4C08-8309-0EA998DD4A15"/>
            <property name="Author" value="user1"/>
            <property name="Reviewer" value="user2"/>
            <property name="ReviewDate" value="2017-01-16T14:41:21"/>
            <property name="Requirement" value="TRQ3049,TRQ12345"/>
            <property name="Nic" value="17124,123123"/>
            <property name="Description" value="kdasñkdadska"/>
        </properties>
        <testcase name="don pepito and don Jose"/>
        <testcase name="don jose says 'hola don Pepito'"/>
        <testcase name="don Pepito reponds 'hola don Jose'"/>
    </testsuite>
<testsuites/>    
```    