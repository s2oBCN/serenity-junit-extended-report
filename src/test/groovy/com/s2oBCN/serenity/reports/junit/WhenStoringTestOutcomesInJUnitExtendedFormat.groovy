package com.s2oBCN.serenity.reports.junit

import com.s2oBCN.TestStepFactory
import net.thucydides.core.model.TestOutcome
import net.thucydides.core.model.TestTag
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class WhenStoringTestOutcomesInJUnitExtendedFormat extends Specification {

    private static final DateTime FIRST_OF_JANUARY = new LocalDateTime(2013, 1, 1, 0, 0, 0, 0).toDateTime()
    private static final DateTime SECOND_OF_JANUARY = new LocalDateTime(2013, 1, 2, 0, 0, 0, 0).toDateTime()

    def JUnitXMLExtendedReporter reporter

    File outputDirectory

    @Rule
    TemporaryFolder temporaryFolder

    def setup() {
        outputDirectory = temporaryFolder.newFolder()
        reporter = new JUnitXMLExtendedReporter()
        reporter.setOutputDirectory(outputDirectory)
    }

    class SomeTestScenario {
        public void a_simple_test_case() {
        }

        public void should_do_this() {
        }

        public void should_do_that() {
        }
    }

    class AUserStory {
    }

    def "JUnit XML report should contain a testsuite element with a summary of the test results"() {
        given:
            def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
            testOutcome.startTime = FIRST_OF_JANUARY
            testOutcome.description = "Some description"
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))

        when:
            reporter.generateReportFor(testOutcome)
            def junitXMLReport = new File(outputDirectory.getAbsolutePath(), outputDirectory.list()[0]).text
        then:
            junitXMLReport.contains '<testsuite errors="0" failures="0" name="com.s2oBCN.serenity.reports.junit.WhenStoringTestOutcomesInJUnitExtendedFormat.SomeTestScenario" skipped="0" tests="1" time="0.0"'

        and:
            junitXMLReport.contains '<testcase name="step 1"/>'
    }

    def "JUnit XML report should contain tags as properties of the test results"() {
        given:
            def testOutcome = TestOutcome.forTest("should_do_this", SomeTestScenario.class)
            testOutcome.startTime = FIRST_OF_JANUARY
            testOutcome.description = "Some description"
            testOutcome.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
            def tag = TestTag.withName("5FFEA912-8A69-496C-9C5C-29851422F78F").andType("InternalTcId")
            testOutcome.addTag(tag)
        when:
            reporter.generateReportFor(testOutcome)
            def junitXMLReport = new File(outputDirectory.getAbsolutePath(), outputDirectory.list()[0]).text
        then:
            junitXMLReport.contains '<testsuite errors="0" failures="0" name="com.s2oBCN.serenity.reports.junit.WhenStoringTestOutcomesInJUnitExtendedFormat.SomeTestScenario" skipped="0" tests="1" time="0.0"'
        and:
            junitXMLReport.contains '<property name="InternalTcId" value="5FFEA912-8A69-496C-9C5C-29851422F78F"/>'
    }


    def "JUnit XML report should handle multiple test results in a test suite"() {
        given:
            def testOutcome2 = TestOutcome.forTest("should_do_that", SomeTestScenario.class)
            testOutcome2.startTime = FIRST_OF_JANUARY
            testOutcome2.description = "Some description"
            testOutcome2.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
            testOutcome2.recordStep(TestStepFactory.successfulTestStepCalled("step 2").startingAt(FIRST_OF_JANUARY))
        when:
            reporter.generateReportFor(testOutcome2)
            def junitXMLReport = new File(outputDirectory.getAbsolutePath(), outputDirectory.list()[0]).text
        then:
            junitXMLReport.contains 'testsuite errors="0" failures="0" name="com.s2oBCN.serenity.reports.junit.WhenStoringTestOutcomesInJUnitExtendedFormat.SomeTestScenario" skipped="0" tests="2"'
        and:
            junitXMLReport.contains '<testcase name="step 1"/><testcase name="step 2"/>'
    }


    def "JUnit XML report should handle failing tests"() {
        given:
            def testOutcome2 = TestOutcome.forTest("should_do_that", SomeTestScenario.class)
            testOutcome2.startTime = FIRST_OF_JANUARY
            testOutcome2.description = "Some description"
            testOutcome2.recordStep(TestStepFactory.successfulTestStepCalled("step 1").startingAt(FIRST_OF_JANUARY))
            def failingStep = TestStepFactory.failingTestStepCalled("step 2").startingAt(FIRST_OF_JANUARY);
            failingStep.failedWith(new AssertionError("Oh noses!"))
            testOutcome2.recordStep(failingStep)
        when:
            reporter.generateReportFor(testOutcome2)
            def junitXMLReport = new File(outputDirectory.getAbsolutePath(), outputDirectory.list()[0]).text
        then:
            junitXMLReport.contains '<failure message="Oh noses!" type="java.lang.AssertionError">Oh noses!</failure>'
    }

}