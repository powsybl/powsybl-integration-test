/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.model;

/**
 * An object that represents a testcase. Contains parameters and expected results for provided parameters.
 * Cannot be used without a matching {@link ComputationRunner} that does perform the computation related to those
 * parameters/results
 * Basically, a given {@link TestCase} is equivalent to the following boolean predicate : "When performing the said
 * computation on these <strong>parameters</strong>, output is <strong>this</strong>".
 *
 * @param <P> type of parameters for the computation
 * @param <R> type of results of the computation
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public interface TestCase<P extends ComputationParameters, R extends ComputationResults> {

    /**
     * @return Identifier (name) for this testcase. All test cases in the same test plan should have unique identifiers.
     */
    String getId();

    /**
     * @return Parameters to use for the computation
     */
    P getParameters();

    /**
     * @return Expected results of the computation (when inputting the right parameters)
     */
    R getExpectedResults();
}
