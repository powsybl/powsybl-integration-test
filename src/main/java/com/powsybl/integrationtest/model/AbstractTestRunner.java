/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A {@link TestRunner} that runs a given computation with the provided parameters and retrieve results from the
 * computation. Then, results are compared to expected results.
 *
 * @param <P> Type of computation parameters
 * @param <R> Type of computation results
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public abstract class AbstractTestRunner<P extends ComputationParameters, R extends ComputationResults>
        implements TestRunner<P, R> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTestRunner.class);

    @Override
    public List<String> runTests(TestCase<P, R> testCase) {
        LOGGER.debug("Running calculation on testcase [" + testCase.getId()  + "]");
        R actualResults = runTestsWithoutChecks(testCase);
        LOGGER.debug("Now comparing results and expectations for testcase [" + testCase.getId()  + "] ...");
        List<String> errors = performIdentityChecks("[" + testCase.getId() + "] ", actualResults,
                testCase.getExpectedResults());
        if (LOGGER.isDebugEnabled()) {
            if (errors.isEmpty()) {
                LOGGER.debug("... OK");
            } else {
                LOGGER.error("... KO (" + errors.size() + " errors)");
            }
        }
        return errors;
    }

    @Override
    public R runTestsWithoutChecks(TestCase<P, R> testCase) {
        return getComputationRunner().computeResults(testCase.getParameters());
    }

    /**
     * Perform tests to check that results and expected results are actually the same.
     * For each difference, add a message in the returned list of errors.
     *
     * @param logPrefix a prefix to put in each log to identify the testcase for every error log
     * @param actual    results that were obtained
     * @param expected  results that were expected
     * @return a list of errors containing a message for each difference between actual and expected
     */
    protected abstract List<String> performIdentityChecks(String logPrefix, R actual, R expected);

    /**
     * @return a component that executes the computation
     */
    protected abstract ComputationRunner<P, R> getComputationRunner();
}
