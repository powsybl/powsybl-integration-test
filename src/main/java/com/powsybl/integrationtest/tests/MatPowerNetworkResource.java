package com.powsybl.integrationtest.tests;

import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.impl.NetworkFactoryImpl;


public enum MatPowerNetworkResource {

    CASE1888RTE("case1888rte"),
    CASE6515RTE("case6515rte");

    Network network;

    MatPowerNetworkResource(String resourceName) {
        network = Importers.getImporter("MATPOWER")
                           .importData(new ResourceDataSource(resourceName, new ResourceSet("/", resourceName + ".mat")),
                                   new NetworkFactoryImpl(),
                                   null);
    }

    Network getNetwork() {
        return network;
    }
}
