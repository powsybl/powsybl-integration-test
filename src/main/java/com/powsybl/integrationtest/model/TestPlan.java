/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.model;

import java.util.Collections;
import java.util.List;

/**
 * A Test plan comprises a series of tests to execute sequentially.
 *
 * @param <P> type of parameters for the tests
 * @param <R> type of results of the tests
 * @param <TC> type of test cases
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class TestPlan<P extends ComputationParameters, R extends ComputationResults, TC extends TestCase<P, R>> {

    private final List<TC> testCases;

    public TestPlan(List<TC> testCases) {
        if (testCases == null || testCases.isEmpty()) {
            throw new IllegalArgumentException("Can't build a test plan without test cases.");
        }
        this.testCases = testCases;
    }

    /**
     * @return an ordered list containing all test cases for this case plan
     */
    public List<TC> getTestCases() {
        return Collections.unmodifiableList(testCases);
    }
}
