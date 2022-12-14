/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest;

import com.powsybl.integrationtest.model.TestPlan;
import com.powsybl.integrationtest.securityanalysis.jsonconfig.SecurityAnalysisTestPlanReader;
import com.powsybl.integrationtest.securityanalysis.model.SecurityAnalysisComputationParameters;
import com.powsybl.integrationtest.securityanalysis.model.SecurityAnalysisComputationResults;
import com.powsybl.integrationtest.securityanalysis.model.SecurityAnalysisTestCase;
import com.powsybl.integrationtest.securityanalysis.model.SecurityAnalysisTestRunner;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
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
}
