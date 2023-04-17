/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest;

import com.powsybl.integrationtest.model.TestPlan;
import com.powsybl.integrationtest.securityanalysis.jsonconfig.SecurityAnalysisTestCaseJson;
import com.powsybl.integrationtest.securityanalysis.jsonconfig.SecurityAnalysisTestPlanJson;
import com.powsybl.integrationtest.securityanalysis.jsonconfig.SecurityAnalysisTestPlanReader;
import com.powsybl.integrationtest.securityanalysis.model.SecurityAnalysisComputationParameters;
import com.powsybl.integrationtest.securityanalysis.model.SecurityAnalysisComputationResults;
import com.powsybl.integrationtest.securityanalysis.model.SecurityAnalysisTestCase;
import com.powsybl.integrationtest.securityanalysis.model.SecurityAnalysisTestRunner;
import com.powsybl.security.converter.SecurityAnalysisResultExporter;
import com.powsybl.security.converter.SecurityAnalysisResultExporters;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Test to check that SA computations outputs are as expected.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class SecurityAnalysisIntegrationTest {

    @Test
    void runSATests() throws IOException, URISyntaxException {
        SecurityAnalysisTestRunner runner = new SecurityAnalysisTestRunner();
        SecurityAnalysisTestPlanReader reader = new SecurityAnalysisTestPlanReader();
        try (InputStream res = getClass().getClassLoader().getResourceAsStream("saTestPlan.json")) {
            TestPlan<SecurityAnalysisComputationParameters, SecurityAnalysisComputationResults, SecurityAnalysisTestCase> testPlan = reader.extractTestPlan(res);
            List<String> errors = new ArrayList<>();
            for (SecurityAnalysisTestCase testCase : testPlan.getTestCases()) {
                errors.addAll(runner.runTests(testCase));
            }
            if (!errors.isEmpty()) {
                StringJoiner joiner = new StringJoiner("\n");
                if (errors.size() > 100) {
                    // Show only the 100 first errors
                    for (int i = 0; i < 100; i++) {
                        joiner.add(errors.get(i));
                    }
                    joiner.add("and " + (errors.size() - 100) + " more...");
                } else {
                    errors.forEach(joiner::add);
                }
                throw new IllegalStateException(joiner.toString());
            }
        }
    }

    /**
     * Update security analysis test plan references by running the test cases and overriding the references with the results.
     * This test is disabled and should only be used to update the references when you know what you are doing.
     * @throws IOException
     * @throws URISyntaxException
     */
    @Test @Disabled("See test description")
    void updateSaReference() throws IOException, URISyntaxException {
        SecurityAnalysisTestRunner runner = new SecurityAnalysisTestRunner();
        SecurityAnalysisTestPlanReader reader = new SecurityAnalysisTestPlanReader();
        try (InputStream res = getClass().getClassLoader().getResourceAsStream("saTestPlan.json")) {
            SecurityAnalysisTestPlanJson jsonPlan = reader.readTestPlan(res);

            for (SecurityAnalysisTestCaseJson testCaseJson : jsonPlan.getTestCases()) {
                SecurityAnalysisTestCase testCase = SecurityAnalysisTestPlanReader.buildFromJson(testCaseJson);
                SecurityAnalysisComputationResults results = runner.runTestsWithoutChecks(testCase);
                Path resourceDirectory = Paths.get("src", "test", "resources");
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
