/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.loadflow.jsonconfig;

import java.util.List;

/**
 * @author Bertrand Rix <bertrand.rix at artelys.com>
 */
public class LoadflowTestPlanJson {
    List<LoadflowTestCaseJson> testCases;

    public LoadflowTestPlanJson(List<LoadflowTestCaseJson> testCases) {
        this.testCases = testCases;
    }

    public List<LoadflowTestCaseJson> getTestCases() {
        return testCases;
    }
}
