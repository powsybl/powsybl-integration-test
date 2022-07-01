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

    LoadFlowParameters getParameters() {
        return parameters;
    }

}
