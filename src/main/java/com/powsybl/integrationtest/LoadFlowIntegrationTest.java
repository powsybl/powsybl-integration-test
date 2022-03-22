package com.powsybl.integrationtest;

import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.loadflow.LoadFlow;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoadFlowIntegrationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadFlowIntegrationTest.class);

    private LoadFlowIntegrationTest() {
    }

    public static void main(String[] args) {
        Network network = EurostagTutorialExample1Factory.create();
        LoadFlowParameters parameters = new LoadFlowParameters().setNoGeneratorReactiveLimits(true)
                                             .setDistributedSlack(false);
        LoadFlowResult result = LoadFlow.run(network, parameters);
    }
}
