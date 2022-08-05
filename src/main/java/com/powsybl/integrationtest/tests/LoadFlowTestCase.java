/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.tests;

import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Network;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.loadflow.json.JsonLoadFlowParameters;
import com.powsybl.loadflow.json.LoadFlowResultDeserializer;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Bertrand Rix <bertrand.rix at artelys.com>
 */
public class LoadFlowTestCase {

    private final Network network;

    private final LoadFlowParameters parameters;

    private final Network networkRef;

    private final LoadFlowResult resultRef;

    private final String id;

    public LoadFlowTestCase(String id, Network network, LoadFlowParameters parameters,
                            Network networkRef, LoadFlowResult resultRef) {
        this.id = id;
        this.network = network;
        this.parameters = parameters;
        this.networkRef = networkRef;
        this.resultRef = resultRef;
    }

    public Network getNetwork() {
        return network;
    }

    public String getId() {
        return id;
    }

    public LoadFlowParameters getParameters() {
        return parameters;
    }

    public LoadFlowResult getResultRef() {
        return resultRef;
    }

    public Network getNetworkRef() {
        return networkRef;
    }

    public static LoadFlowTestCase loadFromJson(LoadFlowTestCaseJson testCaseDescription) throws IOException {
        LoadFlowParameters parameters = JsonLoadFlowParameters.read(
                Objects.requireNonNull(LoadFlowTestCase.class.getResourceAsStream("/" + testCaseDescription.getParameters())));
        LoadFlowResult result = LoadFlowResultDeserializer.read(
                Objects.requireNonNull(LoadFlowTestCase.class.getResourceAsStream("/" + testCaseDescription.getResultRef())));

        return new LoadFlowTestCase(testCaseDescription.getId(),
                Importers.loadNetwork(testCaseDescription.getNetwork(),
                        Objects.requireNonNull(LoadFlowTestCase.class.getResourceAsStream("/" + testCaseDescription.getNetwork()))),
                parameters,
                Importers.loadNetwork(testCaseDescription.getNetworkRef(),
                        Objects.requireNonNull(LoadFlowTestCase.class.getResourceAsStream("/" + testCaseDescription.getNetworkRef()))),
                result);
    }
}
