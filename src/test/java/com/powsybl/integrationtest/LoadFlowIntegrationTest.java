/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest;

import com.powsybl.commons.datasource.ResourceDataSource;
import com.powsybl.commons.datasource.ResourceSet;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.impl.NetworkFactoryImpl;
import com.powsybl.integrationtest.tests.LoadFlowParametersResource;
import com.powsybl.integrationtest.tests.MatPowerNetworkResource;
import com.powsybl.loadflow.LoadFlow;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.loadflow.json.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Bertrand Rix <bertrand.rix at artelys.com>
 */
public final class LoadFlowIntegrationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadFlowIntegrationTest.class);

    static final double DELTA_V = 1e-3;

    static final double DELTA_P = 1e-3;

    static final double DELTA_Q = 1e-3;

    static final double DELTA_ANGLE = 1e-3;

    static final double DELTA_SLACK_POWER_MISMATCH = 1e-3;

    static List<String> detectedDifferences;

    private LoadFlowIntegrationTest() {
    }

    public static void compareBuses(Network test, Network reference) {
        for (Bus bus : test.getBusView().getBuses()) {
            Bus refBus = reference.getBusView().getBus(bus.getId());
            assertAndLog(bus.getV(), refBus.getV(), DELTA_V, "Bus voltage from test and bus voltage from reference are different, bus id: " + bus.getId());
            assertAndLog(bus.getAngle(), refBus.getAngle(), DELTA_ANGLE, "Bus angle from test and bus angle from reference are different, bus id: " + bus.getId());
        }
    }

    public static void compareBranches(Network test, Network reference) {
        for (Branch<?> testBranch : test.getBranches()) {
            Branch<?> refBranch = reference.getBranch(testBranch.getId());
            if (testBranch.getTerminal1().isConnected() && testBranch.getTerminal1().getBusView().getBus().isInMainConnectedComponent()) {
                assertAndLog(testBranch.getTerminal1().getP(), refBranch.getTerminal1().getP(), DELTA_P,
                        "Branch from test and branch from reference are different on terminal1 P, branch id: " + testBranch.getId());
                assertAndLog(testBranch.getTerminal1().getQ(), refBranch.getTerminal1().getQ(), DELTA_Q,
                        "Branch from test and branch from reference are different on terminal1 Q, branch id: " + testBranch.getId());
            }
            if (testBranch.getTerminal2().isConnected() && testBranch.getTerminal2().getBusView().getBus().isInMainConnectedComponent()) {
                assertAndLog(testBranch.getTerminal2().getP(), refBranch.getTerminal2().getP(), DELTA_P,
                        "Branch from test and branch from reference are different on terminal2 P, branch id: " + testBranch.getId());
                assertAndLog(testBranch.getTerminal2().getQ(), refBranch.getTerminal2().getQ(), DELTA_Q,
                        "Branch from test and branch from reference are different on terminal2 Q, branch id: " + testBranch.getId());
            }
        }
    }

    public static void compareLoads(Network test, Network reference) {
        for (Load testLoad : test.getLoads()) {
            Load refLoad = reference.getLoad(testLoad.getId());
            if (testLoad.getTerminal().getBusView().getBus() != null && testLoad.getTerminal().getBusView().getBus().isInMainConnectedComponent()) {
                assertAndLog(testLoad.getTerminal().getP(), refLoad.getTerminal().getP(), DELTA_P,
                        "Load from test and load from reference are different on terminal P, load id: " + testLoad.getId());
                assertAndLog(testLoad.getTerminal().getQ(), refLoad.getTerminal().getQ(), DELTA_Q,
                        "Load from test and load from reference are different on terminal Q, load id: " + testLoad.getId());
            }
        }
    }

    public static void compareGenerators(Network test, Network reference) {
        for (Generator testGenerator : test.getGenerators()) {
            Generator refGenerator = reference.getGenerator(testGenerator.getId());
            if (testGenerator.getTerminal().getBusView().getBus() != null && testGenerator.getTerminal().getBusView().getBus().isInMainConnectedComponent()) {
                assertAndLog(testGenerator.getTerminal().getP(), refGenerator.getTerminal().getP(), DELTA_P,
                        "Generator from test and generator from reference are different on terminal P, generator id: " + testGenerator.getId());
                assertAndLog(testGenerator.getTerminal().getQ(), refGenerator.getTerminal().getQ(), DELTA_Q,
                        "Generator from test and generator from reference are different on terminal Q, generator id: " + testGenerator.getId());
            }
        }
    }

    public static void compareVcsConverterStations(Network test, Network reference) {
        for (VscConverterStation testConverter : test.getVscConverterStations()) {
            VscConverterStation refConverter = reference.getVscConverterStation(testConverter.getId());
            if (testConverter.getTerminal().getBusView().getBus() != null && testConverter.getTerminal().getBusView().getBus().isInMainConnectedComponent()) {
                assertAndLog(testConverter.getTerminal().getP(), refConverter.getTerminal().getP(), DELTA_P,
                        "VCSConverter from test and VCSConverter from reference are different on terminal P, VCSConverter id: " + testConverter.getId());
                assertAndLog(testConverter.getTerminal().getQ(), refConverter.getTerminal().getQ(), DELTA_Q,
                        "VCSConverter from test and VCSConverter from reference are different on terminal Q, VCSConverter id: " + testConverter.getId());
            }
        }
    }

    public static void compareLccConverterStations(Network test, Network reference) {
        for (LccConverterStation testConverter : test.getLccConverterStations()) {
            LccConverterStation refConverter = reference.getLccConverterStation(testConverter.getId());
            if (testConverter.getTerminal().getBusView().getBus() != null && testConverter.getTerminal().getBusView().getBus().isInMainConnectedComponent()) {
                assertAndLog(testConverter.getTerminal().getP(), refConverter.getTerminal().getP(), DELTA_P,
                        "LCCConverter from test and LCCConverter from reference are different on terminal P, LCCConverter id: " + testConverter.getId());
                assertAndLog(testConverter.getTerminal().getQ(), refConverter.getTerminal().getQ(), DELTA_Q,
                        "LCCConverter from test and LCCConverter from reference are different on terminal Q, LCCConverter id: " + testConverter.getId());
            }
        }
    }

    public static void compareTwoWindingTransformers(Network test, Network reference) {
        for (TwoWindingsTransformer testTwt : test.getTwoWindingsTransformers()) {
            TwoWindingsTransformer refTwt = reference.getTwoWindingsTransformer(testTwt.getId());
            if (testTwt.getRatioTapChanger() != null && testTwt.getRatioTapChanger().isRegulating() && testTwt.getRatioTapChanger().getRegulationTerminal() != null
                    && testTwt.getRatioTapChanger().getRegulationTerminal().isConnected()) {
                assertAndLog(testTwt.getRatioTapChanger().getTapPosition(), refTwt.getRatioTapChanger().getTapPosition(), 0,
                        "TwoWindingsTransformer from test and TwoWindingsTransformer from reference have different RTC TapPosition, TwoWindingsTransformer id: " + testTwt.getId());
            }
            if (testTwt.getPhaseTapChanger() != null && testTwt.getPhaseTapChanger().isRegulating() && testTwt.getPhaseTapChanger().getRegulationTerminal() != null
                    && testTwt.getPhaseTapChanger().getRegulationTerminal().isConnected()) {
                assertAndLog(testTwt.getPhaseTapChanger().getTapPosition(), refTwt.getPhaseTapChanger().getTapPosition(), 0,
                        "TwoWindingsTransformer from test and TwoWindingsTransformer from reference have different PTC TapPosition, TwoWindingsTransformer id: " + testTwt.getId());
            }
        }
    }

    public static void compareResults(LoadFlowResult test, LoadFlowResult reference) {
        assertAndLog(test.isOk(), reference.isOk(),
                "LoadFlowResult reference and test isOk is different.");
        assertAndLog(test.getComponentResults().size(), reference.getComponentResults().size(),
                "LoadFlowResult reference and test component results are not the same size.");

        if (test.getComponentResults().size() == reference.getComponentResults().size()) {
            for (int i = 0; i < test.getComponentResults().size(); ++i) {
                assertAndLog(test.getComponentResults().get(0).getStatus(),
                        reference.getComponentResults().get(0).getStatus(),
                        "LoadFlowResult reference and test components " + i + " have different status.");
                assertAndLog(test.getComponentResults().get(i).getIterationCount(), reference.getComponentResults().get(i).getIterationCount(),
                        "LoadFlowResult reference and test components " + i + " have different iteration count.");
                assertAndLog(test.getComponentResults().get(i).getSlackBusId(), reference.getComponentResults().get(i).getSlackBusId(),
                        "LoadFlowResult reference and test components " + i + " have different slack bus id.");
                assertAndLog(test.getComponentResults().get(0).getSlackBusActivePowerMismatch(),
                        reference.getComponentResults().get(0).getSlackBusActivePowerMismatch(),
                        DELTA_SLACK_POWER_MISMATCH,
                        "LoadFlowResult reference and test components " + i + " have different slack power mismatch.");
            }
        }
    }

    public static Network loadNetworkReference(String networkResourceName, String parameterType) {
        return Importers.getImporter("XIIDM")
                .importData(new ResourceDataSource("NetworkReference_" + networkResourceName + "_" + parameterType,
                                new ResourceSet("/loadflowreferences/", "NetworkReference_" + networkResourceName + "_" + parameterType + ".xiidm")),
                        new NetworkFactoryImpl(),
                        null);
    }

    public static LoadFlowResult loadResultReference(String networkResourceName, String parameterType) {
        try {
            return LoadFlowResultDeserializer.read(
                    Objects.requireNonNull(LoadFlowIntegrationTest.class.getResourceAsStream(
                            "/loadflowreferences/LoadFlowResult_" + networkResourceName + "_" + parameterType + ".json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void assertAndLog(T value, T reference, String differenceInfo) {
        if (value != reference) {
            detectedDifferences.add(String.format(differenceInfo + " value is %s, ref is %s.", value, reference));
        }
    }

    public static void assertAndLog(double value, double reference, double delta, String differenceInfo) {
        if (Math.abs(reference - value) > delta) {
            detectedDifferences.add(String.format(differenceInfo + " value is %s, ref is %s, accepted delta is %s.", value, reference, delta));
        }
    }

    public static void assertAndLog(String value, String reference, String differenceInfo) {
        if (!value.equals(reference)) {
            detectedDifferences.add(String.format(differenceInfo + " value is %s, ref is %s.", value, reference));
        }
    }

    @Test
    void loadFlowIntegrationTests() {
        boolean differencesDetected = false;
        for (MatPowerNetworkResource networkResource : MatPowerNetworkResource.values()) {
            Network network = networkResource.getNetwork();
            for (LoadFlowParametersResource parameterType : LoadFlowParametersResource.values()) {
                LOGGER.info("Running load flow on network {} with parameters {}: ", networkResource.name(), parameterType.name());
                LoadFlowResult result = LoadFlow.run(network, parameterType.getParameters());
                LOGGER.info("Load flow result isOk : {}.", result.isOk());

                detectedDifferences = new ArrayList<>();
                //Load reference and compare
                Network ref = loadNetworkReference(networkResource.name(), parameterType.name());
                LoadFlowResult resultRef = loadResultReference(networkResource.name(), parameterType.name());
                compareResults(result, resultRef);
                compareBuses(network, ref);
                compareBranches(network, ref);
                compareLoads(network, ref);
                compareGenerators(network, ref);
                compareVcsConverterStations(network, ref);
                compareLccConverterStations(network, ref);
                compareTwoWindingTransformers(network, ref);

                for (String differenceInfo : detectedDifferences) {
                    LOGGER.warn(differenceInfo);
                }
                differencesDetected |= !detectedDifferences.isEmpty();
            }
        }
        assertFalse(differencesDetected, "Differences have been detected between the test network and the reference.");
    }
}
