/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.integrationtest.creation.security.statemonitors;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * This class allows to select an implementation of {@link StateMonitorsSupplier} with a given name.
 *
 * @author Théo Le Colleter <theo.le-colleter at artelys.com>
 */
public final class StateMonitorsSuppliers {
    private StateMonitorsSuppliers() {

    }

    private static final Map<String, StateMonitorsSupplier> SUPPLIERS = new HashMap<>();

    static {
        ServiceLoader<StateMonitorsSupplier> loader = ServiceLoader.load(StateMonitorsSupplier.class);
        loader.forEach(p -> SUPPLIERS.put(p.getClass().getSimpleName(), p));
    }

    public static StateMonitorsSupplier getInstance(String supplierName) {
        return SUPPLIERS.get(supplierName);
    }
}
