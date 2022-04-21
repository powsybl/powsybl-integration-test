/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.tests;

import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.impl.NetworkFactoryImpl;
import com.powsybl.loadflow.LoadFlow;
import com.powsybl.loadflow.LoadFlowResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * @author Bertrand Rix <bertrand.rix at artelys.com>
 */
public final class LoadFlowIntegrationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadFlowIntegrationTest.class);

    static final double DELTA_V = 1e-3;

    static final double DELTA_P = 1e-3;

    private LoadFlowIntegrationTest() {
    }

    static void compareBuses(Network test, Network reference) {
        for (Bus bus : test.getBusView().getBuses()) {
            Bus refBus = reference.getBusView().getBus(bus.getId());
            assertEquals(bus.getV(), refBus.getV(), DELTA_V,
                    "Bus from test and bus from reference are different, bus id : " + bus.getId());
        }
    }

    static void compareBranches(Network test, Network reference) {
        for (Branch testBranch : test.getBranches()) {
            Branch refBranch = reference.getBranch(testBranch.getId());
            if (testBranch.getTerminal1().isConnected() && testBranch.getTerminal1().getBusView().getBus().isInMainConnectedComponent()) {
                assertEquals(testBranch.getTerminal1().getP(), refBranch.getTerminal1().getP(), DELTA_P,
                        "Branch from test and branch from reference are different on terminal1 P, branch id : " + testBranch.getId());
            }
            if (testBranch.getTerminal2().isConnected() && testBranch.getTerminal2().getBusView().getBus().isInMainConnectedComponent()) {
                assertEquals(testBranch.getTerminal2().getP(), refBranch.getTerminal2().getP(), DELTA_P,
                        "Branch from test and branch from reference are different on terminal2 P, branch id : " + testBranch.getId());
            }
        }
    }

    static void compareLoads(Network test, Network reference) {
        for (Load testLoad : test.getLoads()) {
            Load refLoad = reference.getLoad(testLoad.getId());
            if (testLoad.getTerminal().getBusView().getBus() != null && testLoad.getTerminal().getBusView().getBus().isInMainConnectedComponent()) {
                assertEquals(testLoad.getTerminal().getP(), refLoad.getTerminal().getP(), DELTA_P, "Load from test and load from reference are different on terminal P, load id : " + testLoad.getId());
            }
        }
    }

    static void compareGenerators(Network test, Network reference) {
        for (Generator testGenerator : test.getGenerators()) {
            Generator refGenerator = reference.getGenerator(testGenerator.getId());
            if (testGenerator.getTerminal().getBusView().getBus() != null && testGenerator.getTerminal().getBusView().getBus().isInMainConnectedComponent()) {
                assertEquals(testGenerator.getTerminal().getP(), refGenerator.getTerminal().getP(), DELTA_P,
                        "Generator from test and generator from reference are different on terminal P, load id : " + testGenerator.getId());
            }
        }
    }

    static void compareVcsConverterStations(Network test, Network reference) {
        for (VscConverterStation testConverter : test.getVscConverterStations()) {
            VscConverterStation refConverter = reference.getVscConverterStation(testConverter.getId());
            if (testConverter.getTerminal().getBusView().getBus() != null && testConverter.getTerminal().getBusView().getBus().isInMainConnectedComponent()) {
                assertEquals(testConverter.getTerminal().getP(), refConverter.getTerminal().getP(), DELTA_P,
                        "VCSConverter from test and VCSConverter from reference are different on terminal P, VCSConverter id : " + testConverter.getId());
            }
        }
    }

    static void compareLccConverterStations(Network test, Network reference) {
        for (LccConverterStation testConverter : test.getLccConverterStations()) {
            LccConverterStation refConverter = reference.getLccConverterStation(testConverter.getId());
            if (testConverter.getTerminal().getBusView().getBus() != null && testConverter.getTerminal().getBusView().getBus().isInMainConnectedComponent()) {
                assertEquals(testConverter.getTerminal().getP(), refConverter.getTerminal().getP(), DELTA_P,
                        "LCCConverter from test and LCCConverter from reference are different on terminal P, LCCConverter id : " + testConverter.getId());
            }
        }
    }

    static void compareTwoWindingTransformers(Network test, Network reference) {
        for (TwoWindingsTransformer testTwt : test.getTwoWindingsTransformers()) {
            TwoWindingsTransformer refTwt = reference.getTwoWindingsTransformer(testTwt.getId());
            if (testTwt.getRatioTapChanger() != null && testTwt.getRatioTapChanger().isRegulating() && testTwt.getRatioTapChanger().getRegulationTerminal() != null
                && testTwt.getRatioTapChanger().getRegulationTerminal().isConnected()) {
                assertEquals(testTwt.getRatioTapChanger().getTapPosition(), refTwt.getRatioTapChanger().getTapPosition(), DELTA_P,
                        "TwoWindingsTransformer from test and TwoWindingsTransformer from reference have different TapPosition, TwoWindingsTransformer id : " + testTwt.getId());
                assertEquals(testTwt.getRatioTapChanger().getTargetV(), refTwt.getRatioTapChanger().getTargetV(), DELTA_P,
                        "TwoWindingsTransformer from test and TwoWindingsTransformer from reference have different TargetV, TwoWindingsTransformer id : " + testTwt.getId());
            }
        }
    }

    static Network loadReference(String networkResourceName, String parameterType) {
        return Importers.getImporter("XIIDM")
                           .importData(new ResourceDataSource("NetworkReference_" + networkResourceName + "_" + parameterType,
                                   new ResourceSet("/loadflowreferences/", "NetworkReference_" + networkResourceName + "_" + parameterType + ".xiidm")),
                                   new NetworkFactoryImpl(),
                                   null);
    }

    public static void main(String[] args) {
        for (MatPowerNetworkResource networkResource : MatPowerNetworkResource.values()) {
            Network network = networkResource.getNetwork();
            for (LoadFlowParametersResource parameterType : LoadFlowParametersResource.values()) {
                LOGGER.info("Running load flow on network " + networkResource.name() + " with parameters : " + parameterType.name());
                LoadFlowResult result = LoadFlow.run(network, parameterType.getParameters());
                LOGGER.info("Load flow result isOk : {}.", result.isOk());

                //Load reference and compare
                Network ref = loadReference(networkResource.name(), parameterType.name());
                compareBuses(network, ref);
                compareBranches(network, ref);
                compareLoads(network, ref);
                compareGenerators(network, ref);
                compareVcsConverterStations(network, ref);
                compareLccConverterStations(network, ref);
                compareTwoWindingTransformers(network, ref);

            }
        }
    }
}
