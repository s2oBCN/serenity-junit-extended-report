package com.s2oBCN.serenity.reports.junit;

import com.google.common.collect.Lists;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.model.TestStep;
import net.thucydides.core.model.TestTag;
import net.thucydides.core.model.stacktrace.FailureCause;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

public class JUnitXMLExtendedConverter {

    public void write(TestOutcome outcome, OutputStream outputStream) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root elements
        Document doc = docBuilder.newDocument();

        Element testSuiteElement = buildTestSuiteElement(doc, outcome);

        Element propertiesElement = doc.createElement("properties");
        if (!outcome.getTags().isEmpty() && outcome.getTags().size()>1) {
            buildPropertyElementList(doc, propertiesElement, outcome);
        }
        testSuiteElement.appendChild(propertiesElement);

        for (TestStep testStep: outcome.getTestSteps()){
            Element testCaseElement = buildTestCaseElement(doc, testStep);
            testSuiteElement.appendChild(testCaseElement);
        }

        doc.appendChild(testSuiteElement);

        // write the content into xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(outputStream);

        transformer.transform(source, result);
    }

    private void buildPropertyElementList(Document doc, Element propertiesElement, TestOutcome outcome) {
        Set<TestTag> tags = outcome.getTags();
        List<TestTag> orderedTags = Lists.newArrayList(tags);
        for (TestTag tag : orderedTags) {
            if (tag.getType() != "story"){
                Element propertyElement = doc.createElement("property");
                propertyElement.setAttribute("name", tag.getType());
                propertyElement.setAttribute("value", tag.getName());
                propertiesElement.appendChild(propertyElement);
            }
        }
    }

    private Element buildTestCaseElement(Document doc, TestStep testStep) {
        Element testCaseElement = doc.createElement("testcase");
        testCaseElement.setAttribute("name", testStep.getDescription());
        if (testStep.isFailure()) {
            testCaseElement.appendChild(failureElement(doc, testStep));
        } else if (testStep.isError()){
            testCaseElement.appendChild(errorElement(doc, testStep));
        } else  if (testStep.isSkipped() || testStep.isPending() || testStep.isIgnored()) {
            testCaseElement.appendChild(doc.createElement("skipped"));
        }
        if (testStep.getNestedException() != null) {
            testCaseElement.appendChild(syserrorElement(doc, testStep.getNestedException()));
        }
        return testCaseElement;
    }

    private Element failureElement(Document doc, TestStep testStep) {
        Element testCaseElement = doc.createElement("failure");
        addFailureCause(doc, testCaseElement, testStep.getException());
        return testCaseElement;
    }

    private void addFailureCause(Document doc, Element testCaseElement, FailureCause failureCause) {
        if ((failureCause != null) && (failureCause.getMessage() != null)) {
            testCaseElement.setAttribute("message", failureCause.getMessage());
            testCaseElement.appendChild(doc.createTextNode(failureCause.getMessage()));
        }
        if ((failureCause != null) && (failureCause.getErrorType() != null)) {
            testCaseElement.setAttribute("type", failureCause.getErrorType());
        }
    }

    private Element errorElement(Document doc, TestStep testStep) {
        Element testCaseElement = doc.createElement("error");
        addFailureCause(doc, testCaseElement, testStep.getException());
        return testCaseElement;
    }

    private Element syserrorElement(Document doc, FailureCause nestedTestFailureCause) {
        Element testCaseElement = doc.createElement("system-err");

        StringBuilder printedStackTrace = new StringBuilder();
        printedStackTrace.append(nestedTestFailureCause.getMessage());
        printedStackTrace.append(System.lineSeparator());
        for(StackTraceElement element : nestedTestFailureCause.getStackTrace()) {
            printedStackTrace.append(element.toString());
            printedStackTrace.append(System.lineSeparator());
        }
        testCaseElement.appendChild(doc.createTextNode(printedStackTrace.toString()));
        return testCaseElement;
    }

    private Element buildTestSuiteElement(Document doc, TestOutcome testOutcome) {
        int errors = testOutcome.getErrorCount();
        int failures = testOutcome.getFailureCount();
        int success = testOutcome.getSuccessCount();
        int skipped = testOutcome.getStepCount() - errors - failures - success;

        Element testSuiteElement = doc.createElement("testsuite");
        testSuiteElement.setAttribute("name", testOutcome.getTestCaseName());
        testSuiteElement.setAttribute("time", Double.toString(testOutcome.getDurationInSeconds()));
        testSuiteElement.setAttribute("tests", Integer.toString(testOutcome.getStepCount()));
        testSuiteElement.setAttribute("errors", Integer.toString(errors));
        testSuiteElement.setAttribute("skipped", Integer.toString(skipped));
        testSuiteElement.setAttribute("failures", Integer.toString(failures));
        if (testOutcome.getStartTime() != null) {
            testSuiteElement.setAttribute("timestamp", testOutcome.getStartTime().toString("YYYY-MM-DD hh:mm:ss"));
        }
        return testSuiteElement;
    }
}
