package com.powsybl.integrationtest.utils;

import com.powsybl.integrationtest.securityanalysis.jsonconfig.SecurityAnalysisTestCaseJson;
import com.powsybl.integrationtest.securityanalysis.jsonconfig.SecurityAnalysisTestPlanJson;
import com.powsybl.integrationtest.securityanalysis.jsonconfig.SecurityAnalysisTestPlanReader;
import com.powsybl.integrationtest.securityanalysis.model.SecurityAnalysisComputationResults;
import com.powsybl.integrationtest.securityanalysis.model.SecurityAnalysisTestCase;
import com.powsybl.integrationtest.securityanalysis.model.SecurityAnalysisTestRunner;
import com.powsybl.security.converter.SecurityAnalysisResultExporter;
import com.powsybl.security.converter.SecurityAnalysisResultExporters;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class SecurityAnalysisTestPlanUpdater {

    private SecurityAnalysisTestPlanUpdater() {
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        SecurityAnalysisTestRunner runner = new SecurityAnalysisTestRunner();
        SecurityAnalysisTestPlanReader reader = new SecurityAnalysisTestPlanReader();
        Path resourceDirectory = Paths.get("src", "test", "resources");
        try (InputStream res = Files.newInputStream(resourceDirectory.resolve("saTestPlan.json"))) {
            SecurityAnalysisTestPlanJson jsonPlan = reader.readTestPlan(res);

            for (SecurityAnalysisTestCaseJson testCaseJson : jsonPlan.getTestCases()) {
                SecurityAnalysisTestCase testCase = SecurityAnalysisTestPlanReader.buildFromJson(testCaseJson, resourceDirectory);
                SecurityAnalysisComputationResults results = runner.runTestsWithoutChecks(testCase);
                results.getNetwork().write("XIIDM", null, resourceDirectory.resolve(testCaseJson.getExpectedNetwork()));
                SecurityAnalysisResultExporter exporter = SecurityAnalysisResultExporters.getExporter("JSON");
                try (Writer writer = Files.newBufferedWriter(resourceDirectory.resolve(testCaseJson.getExpectedResults()))) {
                    exporter.export(results.getResults(), writer);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
    }
}
