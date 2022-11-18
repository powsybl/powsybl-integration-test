/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.loadflow.jsonconfig;

import java.util.Objects;

/**
 * @author Bertrand Rix <bertrand.rix at artelys.com>
 */
public class LoadFlowTestCaseJson {

    private final String id;

    private final String inputNetwork;

    private final String inputParameters;

    private final String expectedNetwork;

    private final String expectedResults;

    public LoadFlowTestCaseJson(String id, String inputNetwork, String inputParameters, String expectedNetwork, String expectedResults) {
        this.id = Objects.requireNonNull(id);
        this.inputNetwork = Objects.requireNonNull(inputNetwork);
        this.inputParameters = Objects.requireNonNull(inputParameters);
        this.expectedNetwork = Objects.requireNonNull(expectedNetwork);
        this.expectedResults = Objects.requireNonNull(expectedResults);
    }

    public String getId() {
        return id;
    }

    public String getInputNetwork() {
        return inputNetwork;
    }

    public String getInputParameters() {
        return inputParameters;
    }

    public String getExpectedNetwork() {
        return expectedNetwork;
    }

    public String getExpectedResults() {
        return expectedResults;
    }

}
