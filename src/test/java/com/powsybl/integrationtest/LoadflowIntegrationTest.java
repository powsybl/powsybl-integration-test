/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest;

import com.powsybl.integrationtest.loadflow.jsonconfig.LoadflowTestPlanReader;
import com.powsybl.integrationtest.loadflow.model.LoadflowComputationParameters;
import com.powsybl.integrationtest.loadflow.model.LoadflowComputationResults;
import com.powsybl.integrationtest.loadflow.model.LoadflowTestCase;
import com.powsybl.integrationtest.loadflow.model.LoadflowTestRunner;
import com.powsybl.integrationtest.model.TestPlan;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Test to check that Loadflow computations outputs are as expected.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class LoadflowIntegrationTest {

    @Test
    void runLoadflowTests() throws IOException, URISyntaxException {
        LoadflowTestRunner runner = new LoadflowTestRunner();
        LoadflowTestPlanReader reader = new LoadflowTestPlanReader();
        try (InputStream res = getClass().getClassLoader().getResourceAsStream("loadFlowTestPlan.json")) {
            TestPlan<LoadflowComputationParameters, LoadflowComputationResults, LoadflowTestCase> testPlan = reader.extractTestPlan(res);
            List<String> errors = new ArrayList<>();
            for (LoadflowTestCase testCase : testPlan.getTestCases()) {
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
