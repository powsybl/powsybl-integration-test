/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.model;

import java.util.List;

/**
 * A test runner : performs tests and returns a list of messages that are the checks errors.
 *
 * @param <P> type of parameters
 * @param <R> type of results
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public interface TestRunner<P extends ComputationParameters, R extends ComputationResults> {

    /**
     * Run tests on provided {@link TestCase} and return a List containing all errors messages that were found
     *
     * @param testCase test case to run
     * @return a list containing all error messages. Empty if no error was found.
     */
    List<String> runTests(TestCase<P, R> testCase);
}
