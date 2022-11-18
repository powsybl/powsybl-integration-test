/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.loadflow.model;

import com.powsybl.iidm.network.*;
import com.powsybl.integrationtest.model.AbstractTestRunner;
import com.powsybl.integrationtest.model.ComputationRunner;
import com.powsybl.integrationtest.utils.NetworksComparator;
import com.powsybl.loadflow.LoadFlow;
import com.powsybl.loadflow.LoadFlowResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.powsybl.integrationtest.utils.CompareUtils.assertDeltaMax;
import static com.powsybl.integrationtest.utils.CompareUtils.assertEquals;

/**
 * A service that can perform LoadFlow results comparison.
 * Feed it with a {@link LoadFlowTestCase} and let it check if actual results are equal to those expected.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class LoadFlowTestRunner extends AbstractTestRunner<LoadFlowComputationParameters, LoadFlowComputationResults> {

    private final double deltaV = 1e-9;
    private final double deltaP = 1e-9;
    private final double deltaQ = 1e-9;
    private final double deltaAngle = 1e-9;
    private final double deltaSlackPowerMismatch = 1e-9;

    @Override
    protected ComputationRunner<LoadFlowComputationParameters, LoadFlowComputationResults> getComputationRunner() {
        return parameters -> {
            LoadFlowResult result = LoadFlow.run(parameters.getNetwork(), parameters.getParameters());
            return new LoadFlowComputationResults(parameters.getNetwork(), result);
        };
    }

    @Override
    protected List<String> performIdentityChecks(String logPrefix, LoadFlowComputationResults actual, LoadFlowComputationResults expected) {
        List<String> errors = new ArrayList<>();
        errors.addAll(compareNetworks(logPrefix, actual.getNetwork(), expected.getNetwork()));
        errors.addAll(compareResults(logPrefix, actual.getResult(), expected.getResult()));
        return errors;
    }

    private Collection<String> compareNetworks(String logPrefix, Network actual, Network expected) {
        NetworksComparator nComparator = new NetworksComparator(deltaV, deltaAngle, deltaP, deltaQ);
        return new ArrayList<>(nComparator.compareNetworks(logPrefix, actual, expected));
    }

    List<String> compareResults(String logPrefix, LoadFlowResult test, LoadFlowResult reference) {
        List<String> errors = new ArrayList<>();
        assertEquals(test.isOk(), reference.isOk(),
                logPrefix + "LoadFlowResult reference and test isOk is different.", errors);
        assertEquals(test.getComponentResults().size(), reference.getComponentResults().size(),
                logPrefix + "LoadFlowResult reference and test component results are not the same size.", errors);

        if (test.getComponentResults().size() == reference.getComponentResults().size()) {
            for (int i = 0; i < test.getComponentResults().size(); ++i) {
                assertEquals(test.getComponentResults().get(i).getStatus(),
                        reference.getComponentResults().get(i).getStatus(),
                        logPrefix + "Status has unexpected value for component #" + i, errors);
                assertEquals(test.getComponentResults().get(i).getIterationCount(), reference.getComponentResults().get(i).getIterationCount(),
                        logPrefix + "Iterations count has ", errors);
                assertEquals(test.getComponentResults().get(i).getSlackBusId(),
                        reference.getComponentResults().get(i).getSlackBusId(),
                        logPrefix + "Slack bus id has unexpected value for component #" + i, errors);
                assertDeltaMax(test.getComponentResults().get(i).getSlackBusActivePowerMismatch(),
                        reference.getComponentResults().get(i).getSlackBusActivePowerMismatch(),
                        deltaSlackPowerMismatch,
                        logPrefix + "Slack power mismatch has unexpected value for component #" + i, errors);
            }
        }
        return errors;
    }
}
