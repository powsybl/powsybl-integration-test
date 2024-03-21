/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.securityanalysis.model;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.iidm.network.Network;
import com.powsybl.integrationtest.model.ComputationRunner;
import com.powsybl.security.LimitViolationFilter;
import com.powsybl.security.SecurityAnalysis;
import com.powsybl.security.SecurityAnalysisReport;
import com.powsybl.security.detectors.DefaultLimitViolationDetector;

import java.util.Collections;

/**
 * A {@link ComputationRunner} that runs Security Analysis calculations.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class SecurityAnalysisComputationRunner
        implements ComputationRunner<SecurityAnalysisComputationParameters, SecurityAnalysisComputationResults> {
    @Override
    public SecurityAnalysisComputationResults computeResults(SecurityAnalysisComputationParameters parameters) {
        Network network = parameters.getNetwork();
        SecurityAnalysisReport report = SecurityAnalysis.run(network, network.getVariantManager().getWorkingVariantId(),
            nwk -> parameters.getContingencies(), parameters.getSecurityAnalysisParameters(),
            LocalComputationManager.getDefault(), LimitViolationFilter.load(), new DefaultLimitViolationDetector(),
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
            parameters.getStateMonitors(), ReportNode.NO_OP);
        return new SecurityAnalysisComputationResults(parameters.getNetwork(), report.getResult());
    }
}
