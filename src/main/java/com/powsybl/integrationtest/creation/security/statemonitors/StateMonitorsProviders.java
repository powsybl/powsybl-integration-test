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

public final class StateMonitorsProviders {
    private StateMonitorsProviders() {

    }

    private static final Map<String, StateMonitorsSupplier> PROVIDERS = new HashMap<>();

    static {
        ServiceLoader<StateMonitorsSupplier> loader = ServiceLoader.load(StateMonitorsSupplier.class);
        loader.forEach(p -> PROVIDERS.put(p.getClass().getSimpleName(), p));
    }

    public static StateMonitorsSupplier getInstance(String providerName) {
        return PROVIDERS.get(providerName);
    }
}
