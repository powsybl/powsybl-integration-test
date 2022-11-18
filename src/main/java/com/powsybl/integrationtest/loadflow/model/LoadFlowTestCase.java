/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.loadflow.model;

import com.powsybl.integrationtest.model.TestCase;

import java.util.Objects;

/**
 * A {@link TestCase} for LoadFlow computations. Contains {@link LoadFlowComputationParameters} to be used for the
 * computation and {@link LoadFlowComputationResults} that are the expected results of the computation using the
 * provided parameters.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class LoadFlowTestCase implements TestCase<LoadFlowComputationParameters, LoadFlowComputationResults> {

    private final String id;
    private final LoadFlowComputationParameters parameters;
    private final LoadFlowComputationResults expectedResults;

    public LoadFlowTestCase(String id, LoadFlowComputationParameters parameters, LoadFlowComputationResults expectedResults) {
        this.id = Objects.requireNonNull(id);
        this.parameters = Objects.requireNonNull(parameters);
        this.expectedResults = Objects.requireNonNull(expectedResults);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public LoadFlowComputationParameters getParameters() {
        return parameters;
    }

    @Override
    public LoadFlowComputationResults getExpectedResults() {
        return expectedResults;
    }
}
