/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.integrationtest.creation.security.contingencies;

import com.powsybl.contingency.Contingency;
import com.powsybl.iidm.network.Network;

import java.util.List;
import java.util.Map;

/**
 * Interface allowing to implement different contingencies' supplier (i.e. different strategies to create contingencies).
 *
 * Different from {@link com.powsybl.contingency.ContingenciesProvider} since we need to set the configuration according to the input.
 * @author Théo Le Colleter <theo.le-colleter at artelys.com>
 */
public interface ContingenciesSupplier {

    /**
     * Set the configuration according to the input.
     * <p>Create and return a list of {@link Contingency} which is created according to different strategy, depending on the implementation used.</p>
     * @param network create contingencies from and for a given {@link Network}
     * @param configuration set parameters according to a given configuration. Can be empty if no parameter is needed.
     * @return a list of {@link Contingency}, where a contingency's id is the concatenation of all the contingency's elements' names
     */
    List<Contingency> getContingencies(Network network, Map<String, ?> configuration);
}
