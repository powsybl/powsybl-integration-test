/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.tests;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author Bertrand Rix <bertrand.rix at artelys.com>
 */
public class IntegrationTestModule extends SimpleModule {

    public IntegrationTestModule() {
        addDeserializer(LoadFlowTestCaseJson.class, new LoadFlowTestCaseJsonDeserializer());
        addDeserializer(LoadFlowTestPlanJson.class, new LoadFlowTestPlanJsonDeserializer());
    }
}
