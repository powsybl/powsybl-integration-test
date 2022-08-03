/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.tests;

import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.json.JsonLoadFlowParameters;


public enum LoadFlowParametersResource {
    BASIC("/LoadFlowParametersBasic.json"),
    STANDARD("/LoadFlowParametersStandard.json");

    private final LoadFlowParameters parameters;

    LoadFlowParametersResource(String fileResource) {
        parameters = JsonLoadFlowParameters.read(getClass().getResourceAsStream(fileResource));
    }

    public LoadFlowParameters getParameters() {
        return parameters;
    }

}
