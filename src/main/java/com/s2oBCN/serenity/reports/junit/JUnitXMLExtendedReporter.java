package com.s2oBCN.serenity.reports.junit;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import net.thucydides.core.model.ReportNamer;
import net.thucydides.core.model.ReportType;
import net.thucydides.core.model.TestOutcome;
import net.thucydides.core.reports.AcceptanceTestReporter;
import net.thucydides.core.reports.OutcomeFormat;
import net.thucydides.core.reports.ThucydidesReporter;
import net.thucydides.core.reports.io.SafelyMoveFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.UUID;

public class JUnitXMLExtendedReporter  extends ThucydidesReporter implements AcceptanceTestReporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JUnitXMLExtendedReporter.class);

    private final JUnitXMLExtendedConverter jUnitXMLExtendedConverter;

    public final static String FILE_PREFIX = "JUNIT-EXTENDED-";

    public JUnitXMLExtendedReporter() {
        jUnitXMLExtendedConverter = new JUnitXMLExtendedConverter();
    }

    @Override
    public String getName() {
        return "jUnitXMLExtended";
    }

    @Override
    public Optional<OutcomeFormat> getFormat() {
        return Optional.of(OutcomeFormat.XML);
    }

    @Override
    public void setQualifier(final String qualifier) {
        LOGGER.debug("Option not used");
    }

    @Override
    public void setResourceDirectory(String resourceDirectoryPath) {
        LOGGER.debug("Option not used");
    }

    @Override
    public File generateReportFor(TestOutcome testOutcome) throws IOException {

        LOGGER.debug("GENERATING JUNIT EXTENDED REPORTS");

        Preconditions.checkNotNull(getOutputDirectory());

        String reportFilename = reportFilenameFor(testOutcome);
        String unique = UUID.randomUUID().toString();
        File temporary = new File(getOutputDirectory(), reportFilename.concat(unique));
        File report = new File(getOutputDirectory(), reportFilename);
        report.createNewFile();

        LOGGER.debug("GENERATING JUNIT REPORT {} using temporary file {}", reportFilename, temporary);
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(temporary))) {
            jUnitXMLExtendedConverter.write(testOutcome, outputStream);
            outputStream.flush();
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        } catch (TransformerException e) {
            throw new IOException(e);
        }
        SafelyMoveFiles.withMaxRetriesOf(3).from(temporary.toPath()).to(report.toPath());
        return report;
    }

    private String reportFilenameFor(TestOutcome testOutcome) {
        ReportNamer reportNamer = ReportNamer.forReportType(ReportType.XML);
        return FILE_PREFIX  + reportNamer.getNormalizedTestNameFor(testOutcome);
    }
}