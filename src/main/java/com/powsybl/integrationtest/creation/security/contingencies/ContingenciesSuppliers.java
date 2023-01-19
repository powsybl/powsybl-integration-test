/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.integrationtest.creation.security.contingencies;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * This class allows to select an implementation of {@link ContingenciesSupplier} with a given name.
 *
 * @author Théo Le Colleter <theo.le-colleter at artelys.com>
 */
public final class ContingenciesSuppliers {
    private ContingenciesSuppliers() {

    }

    private static final Map<String, ContingenciesSupplier> SUPPLIERS = new HashMap<>();

    static {
        ServiceLoader<ContingenciesSupplier> loader = ServiceLoader.load(ContingenciesSupplier.class);
        loader.forEach(p -> SUPPLIERS.put(p.getClass().getSimpleName(), p));
    }

    /**
     * @param supplierName
     * @return the {@link ContingenciesSupplier}'s implementation where the class name is supplierName
     */
    public static ContingenciesSupplier getInstance(String supplierName) {
        return SUPPLIERS.get(supplierName);
    }
}
