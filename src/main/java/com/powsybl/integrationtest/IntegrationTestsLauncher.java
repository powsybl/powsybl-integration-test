package com.powsybl.integrationtest;

import com.powsybl.integrationtest.tests.LoadFlowIntegrationTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IntegrationTestsLauncher {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationTestsLauncher.class);

    private IntegrationTestsLauncher() {
    }

    public static void main(String[] args) {
        LoadFlowIntegrationTest.main(args);
    }
}
