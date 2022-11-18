/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.securityanalysis.model;

import com.powsybl.integrationtest.model.TestCase;

/**
 * A {@link TestCase} for Security Analysis computations. Holds a {@link SecurityAnalysisComputationParameters} and
 * a {@link SecurityAnalysisComputationResults}. Can be used with an {@link SecurityAnalysisTestRunner}.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class SecurityAnalysisTestCase
        implements TestCase<SecurityAnalysisComputationParameters, SecurityAnalysisComputationResults> {

    private final String id;
    private final SecurityAnalysisComputationParameters parameters;
    private final SecurityAnalysisComputationResults results;

    public SecurityAnalysisTestCase(String id, SecurityAnalysisComputationParameters parameters,
                                    SecurityAnalysisComputationResults results) {
        this.id = id;
        this.parameters = parameters;
        this.results = results;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public SecurityAnalysisComputationParameters getParameters() {
        return parameters;
    }

    @Override
    public SecurityAnalysisComputationResults getExpectedResults() {
        return results;
    }
}
