/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest;

import com.powsybl.integrationtest.loadflow.jsonconfig.LoadFlowTestPlanReader;
import com.powsybl.integrationtest.loadflow.model.LoadFlowComputationParameters;
import com.powsybl.integrationtest.loadflow.model.LoadFlowComputationResults;
import com.powsybl.integrationtest.loadflow.model.LoadFlowTestCase;
import com.powsybl.integrationtest.loadflow.model.LoadFlowTestRunner;
import com.powsybl.integrationtest.model.TestPlan;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Test to check that LoadFlow computations outputs are as expected.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class LoadFlowIntegrationTest {

    @Test
    void runLoadFlowTests() throws IOException, URISyntaxException {
        LoadFlowTestRunner runner = new LoadFlowTestRunner();
        LoadFlowTestPlanReader reader = new LoadFlowTestPlanReader();
        try (InputStream res = getClass().getClassLoader().getResourceAsStream("loadFlowTestPlan.json")) {
            TestPlan<LoadFlowComputationParameters, LoadFlowComputationResults, LoadFlowTestCase> testPlan = reader.extractTestPlan(res);
            List<String> errors = new ArrayList<>();
            for (LoadFlowTestCase testCase : testPlan.getTestCases()) {
                errors.addAll(runner.runTests(testCase));
            }
            if (!errors.isEmpty()) {
                StringJoiner joiner = new StringJoiner("\n");
                errors.forEach(joiner::add);
                throw new IllegalStateException(joiner.toString());
            }
        }
    }
}
