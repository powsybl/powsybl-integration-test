/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.securityanalysis.jsonconfig;

import java.util.List;

/**
 * A test plan that contains SA test cases.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class SecurityAnalysisTestPlanJson {

    List<SecurityAnalysisTestCaseJson> testCases;

    public SecurityAnalysisTestPlanJson(List<SecurityAnalysisTestCaseJson> testCases) {
        this.testCases = testCases;
    }

    public List<SecurityAnalysisTestCaseJson> getTestCases() {
        return testCases;
    }
}
