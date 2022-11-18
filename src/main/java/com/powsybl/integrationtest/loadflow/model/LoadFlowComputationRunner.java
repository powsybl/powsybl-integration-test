/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.loadflow.model;

import com.powsybl.integrationtest.model.ComputationRunner;
import com.powsybl.loadflow.LoadFlow;
import com.powsybl.loadflow.LoadFlowResult;

/**
 * A {@link ComputationRunner} for LoadFlow calculations. Provide it with a {@link LoadFlowComputationParameters}
 * and it will return a {@link LoadFlowComputationResults}.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class LoadFlowComputationRunner implements ComputationRunner<LoadFlowComputationParameters, LoadFlowComputationResults> {
    @Override
    public LoadFlowComputationResults computeResults(LoadFlowComputationParameters parameters) {
        LoadFlowResult result = LoadFlow.run(parameters.getNetwork(), parameters.getParameters());
        return new LoadFlowComputationResults(parameters.getNetwork(), result);
    }
}
