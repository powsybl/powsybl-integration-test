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
 * A {@link TestCase} for Loadflow computations. Contains {@link LoadflowComputationParameters} to be used for the
 * computation and {@link LoadflowComputationResults} that are the expected results of the computation using the
 * provided parameters.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class LoadflowTestCase implements TestCase<LoadflowComputationParameters, LoadflowComputationResults> {

    private final String id;
    private final LoadflowComputationParameters parameters;
    private final LoadflowComputationResults expectedResults;

    public LoadflowTestCase(String id, LoadflowComputationParameters parameters, LoadflowComputationResults expectedResults) {
        this.id = Objects.requireNonNull(id);
        this.parameters = Objects.requireNonNull(parameters);
        this.expectedResults = Objects.requireNonNull(expectedResults);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public LoadflowComputationParameters getParameters() {
        return parameters;
    }

    @Override
    public LoadflowComputationResults getExpectedResults() {
        return expectedResults;
    }
}
