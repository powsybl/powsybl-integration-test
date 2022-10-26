/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.jsonconfig;

import com.powsybl.integrationtest.model.ComputationParameters;
import com.powsybl.integrationtest.model.ComputationResults;
import com.powsybl.integrationtest.model.TestCase;
import com.powsybl.integrationtest.model.TestPlan;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

/**
 * A component that reads files and outputs
 *
 * @param <T> type of test cases
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public interface TestPlanReader<P extends ComputationParameters, R extends ComputationResults, T extends TestCase<P, R>> {

    /**
     * Read a file and extract a test plan from it
     *
     * @param input path leading to the test plan configuration file
     * @return the test plan extracted from the input file
     * @throws IllegalArgumentException if file does not comply to the expected format
     * @throws IOException              if file cannot be read correctly
     */
    TestPlan<P, R, T> extractTestPlan(InputStream input) throws IOException, URISyntaxException;
}
