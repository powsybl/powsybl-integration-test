/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.model;

/**
 * A component that runs computations with inputs and returns outputs.
 *
 * @param <P> type of inputs for the computation
 * @param <R> type of outputs of the computation
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public interface ComputationRunner<P extends ComputationParameters, R extends ComputationResults> {

    /**
     * Do execute computation
     *
     * @param parameters inputs for the computation
     * @return outputs of the computation
     */
    R computeResults(P parameters);

}
