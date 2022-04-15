/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.tests;

import com.powsybl.iidm.network.Network;
import com.powsybl.loadflow.LoadFlow;
import com.powsybl.loadflow.LoadFlowResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Bertrand Rix <bertrand.rix at artelys.com>
 */
public final class LoadFlowIntegrationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadFlowIntegrationTest.class);

    private LoadFlowIntegrationTest() {
    }

    public static void main(String[] args) {
        for (MatPowerNetworkResource networkResource : MatPowerNetworkResource.values()) {
            Network network = networkResource.getNetwork();
            for (LoadFlowParametersResource parameterType : LoadFlowParametersResource.values()) {
                LOGGER.info("Running load flow on network " + networkResource.name() + " with parameters : " + parameterType.name());
                LoadFlowResult result = LoadFlow.run(network, parameterType.getParameters());
                LOGGER.info("Load flow result isOk : {}.", result.isOk());
            }
        }
    }
}
