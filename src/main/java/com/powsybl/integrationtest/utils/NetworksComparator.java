package com.powsybl.integrationtest.utils;

import com.powsybl.iidm.network.*;

import java.util.ArrayList;
import java.util.List;

import static com.powsybl.integrationtest.utils.CompareUtils.assertDeltaMax;
import static com.powsybl.integrationtest.utils.CompareUtils.assertEquals;

/**
 * A component that can compare two provided networks and return a list of string containing a message for each
 * spotted difference.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class NetworksComparator {

    private final double deltaV;
    private final double deltaAngle;
    private final double deltaP;
    private final double deltaQ;

    public NetworksComparator(double deltaV, double deltaAngle, double deltaP, double deltaQ) {
        this.deltaV = deltaV;
        this.deltaAngle = deltaAngle;
        this.deltaP = deltaP;
        this.deltaQ = deltaQ;
    }

    /**
     * Check all the differences between a network and another one.
     *
     * @param logPrefix      a prefix to add to all generated error messages
     * @param aNetwork       a network
     * @param anotherNetwork another network
     * @return a list containing error message. An error message will be added to the list for each found difference
     *         between {@code aNetwork} and {@code anotherNetwork}.
     */
    public List<String> compareNetworks(String logPrefix, Network aNetwork, Network anotherNetwork) {
        List<String> errorMessages = new ArrayList<>();
        errorMessages.addAll(compareBuses(logPrefix, aNetwork.getNetwork(), anotherNetwork.getNetwork()));
        errorMessages.addAll(compareBranches(logPrefix, aNetwork.getNetwork(), anotherNetwork.getNetwork()));
        errorMessages.addAll(compareLoads(logPrefix, aNetwork.getNetwork(), anotherNetwork.getNetwork()));
        errorMessages.addAll(compareGenerators(logPrefix, aNetwork.getNetwork(), anotherNetwork.getNetwork()));
        errorMessages.addAll(compareVcsConverterStations(logPrefix, aNetwork.getNetwork(), anotherNetwork.getNetwork()));
        errorMessages.addAll(compareLccConverterStations(logPrefix, aNetwork.getNetwork(), anotherNetwork.getNetwork()));
        errorMessages.addAll(compareTwoWindingTransformers(logPrefix, aNetwork.getNetwork(), anotherNetwork.getNetwork()));
        errorMessages.addAll(compareThreeWindingTransformers(logPrefix, aNetwork.getNetwork(), anotherNetwork.getNetwork()));
        errorMessages.addAll(compareShuntCompensators(logPrefix, aNetwork.getNetwork(), anotherNetwork.getNetwork()));
        return errorMessages;
    }

    private List<String> compareBuses(String logPrefix, Network test, Network reference) {
        List<String> errors = new ArrayList<>();
        for (Bus bus : test.getBusView().getBuses()) {
            Bus refBus = reference.getBusView().getBus(bus.getId());
            assertDeltaMax(bus.getV(), refBus.getV(), deltaV,
                    logPrefix + "Bus voltage is different from expected, bus id: " + bus.getId(), errors);
            assertDeltaMax(bus.getAngle(), refBus.getAngle(), deltaAngle,
                    logPrefix + "Bus angle is different from expected, bus id: " + bus.getId(), errors);
        }
        return errors;
    }

    private List<String> compareBranches(String logPrefix, Network test, Network reference) {
        List<String> errors = new ArrayList<>();
        for (Branch<?> testBranch : test.getBranches()) {
            Branch<?> refBranch = reference.getBranch(testBranch.getId());
            if (testBranch.getTerminal1().isConnected() && testBranch.getTerminal1().getBusView().getBus().isInMainConnectedComponent()) {
                assertDeltaMax(testBranch.getTerminal1().getP(), refBranch.getTerminal1().getP(), deltaP,
                        logPrefix + "Unexpected terminal 1 P for branch id: " + testBranch.getId(), errors);
                assertDeltaMax(testBranch.getTerminal1().getQ(), refBranch.getTerminal1().getQ(), deltaQ,
                        logPrefix + "Unexpected terminal 1 Q for branch id: " + testBranch.getId(), errors);
            }
            if (testBranch.getTerminal2().isConnected() && testBranch.getTerminal2().getBusView().getBus().isInMainConnectedComponent()) {
                assertDeltaMax(testBranch.getTerminal2().getP(), refBranch.getTerminal2().getP(), deltaP,
                        logPrefix + "Unexpected terminal 2 P for branch id: " + testBranch.getId(), errors);
                assertDeltaMax(testBranch.getTerminal2().getQ(), refBranch.getTerminal2().getQ(), deltaQ,
                        logPrefix + "Unexpected terminal 2 Q for branch id: " + testBranch.getId(), errors);
            }
        }
        return errors;
    }

    private List<String> compareLoads(String logPrefix, Network test, Network reference) {
        List<String> errors = new ArrayList<>();
        for (Load testLoad : test.getLoads()) {
            Load refLoad = reference.getLoad(testLoad.getId());
            if (testLoad.getTerminal().getBusView().getBus() != null && testLoad.getTerminal().getBusView().getBus().isInMainConnectedComponent()) {
                assertDeltaMax(testLoad.getTerminal().getP(), refLoad.getTerminal().getP(), deltaP,
                        logPrefix + "Unexpected load P value for load id: " + testLoad.getId(), errors);
                assertDeltaMax(testLoad.getTerminal().getQ(), refLoad.getTerminal().getQ(), deltaQ,
                        logPrefix + "Unexpected load Q value for load id: " + testLoad.getId(), errors);
            }
        }
        return errors;
    }

    private List<String> compareGenerators(String logPrefix, Network test, Network reference) {
        List<String> errors = new ArrayList<>();
        for (Generator testGenerator : test.getGenerators()) {
            Generator refGenerator = reference.getGenerator(testGenerator.getId());
            if (testGenerator.getTerminal().getBusView().getBus() != null && testGenerator.getTerminal().getBusView().getBus().isInMainConnectedComponent()) {
                assertDeltaMax(testGenerator.getTerminal().getP(), refGenerator.getTerminal().getP(), deltaP,
                        logPrefix + "Unexpected generator P for generator id: " + testGenerator.getId(), errors);
                assertDeltaMax(testGenerator.getTerminal().getQ(), refGenerator.getTerminal().getQ(), deltaQ,
                        logPrefix + "Unexpected generator Q for generator id: " + testGenerator.getId(), errors);
            }
        }
        return errors;
    }

    private List<String> compareVcsConverterStations(String logPrefix, Network test, Network reference) {
        List<String> errors = new ArrayList<>();
        for (VscConverterStation testConverter : test.getVscConverterStations()) {
            VscConverterStation refConverter = reference.getVscConverterStation(testConverter.getId());
            if (testConverter.getTerminal().getBusView().getBus() != null
                    && testConverter.getTerminal().getBusView().getBus().isInMainConnectedComponent()) {
                assertDeltaMax(testConverter.getTerminal().getP(), refConverter.getTerminal().getP(), deltaP,
                        logPrefix + "Unexpected VCS converter P for VCS converter id: " + testConverter.getId(), errors);
                assertDeltaMax(testConverter.getTerminal().getQ(), refConverter.getTerminal().getQ(), deltaQ,
                        logPrefix + "Unexpected VCS converter Q for VCS converter id: " + testConverter.getId(), errors);
            }
        }
        return errors;
    }

    private List<String> compareLccConverterStations(String logPrefix, Network test, Network reference) {
        List<String> errors = new ArrayList<>();
        for (LccConverterStation testConverter : test.getLccConverterStations()) {
            LccConverterStation refConverter = reference.getLccConverterStation(testConverter.getId());
            if (testConverter.getTerminal().getBusView().getBus() != null
                    && testConverter.getTerminal().getBusView().getBus().isInMainConnectedComponent()) {
                assertDeltaMax(testConverter.getTerminal().getP(), refConverter.getTerminal().getP(), deltaP,
                        logPrefix + "Unexpected LCC Converter P for LCC converter id: " + testConverter.getId(), errors);
                assertDeltaMax(testConverter.getTerminal().getQ(), refConverter.getTerminal().getQ(), deltaQ,
                        logPrefix + "Unexpected LCC Converter P for LCC converter id: " + testConverter.getId(), errors);
            }
        }
        return errors;
    }

    private List<String> compareTwoWindingTransformers(String logPrefix, Network test, Network reference) {
        List<String> errors = new ArrayList<>();
        for (TwoWindingsTransformer test2WT : test.getTwoWindingsTransformers()) {
            TwoWindingsTransformer ref2WT = reference.getTwoWindingsTransformer(test2WT.getId());
            if (test2WT.getRatioTapChanger() != null
                    && test2WT.getRatioTapChanger().isRegulating() && test2WT.getRatioTapChanger().getRegulationTerminal() != null
                    && test2WT.getRatioTapChanger().getRegulationTerminal().isConnected()) {
                assertDeltaMax(test2WT.getRatioTapChanger().getTapPosition(), ref2WT.getRatioTapChanger().getTapPosition(), 0,
                        logPrefix + "Unexpected ratio tap changer position for transformer id: " + test2WT.getId(), errors);
            }
            if (test2WT.getPhaseTapChanger() != null
                    && test2WT.getPhaseTapChanger().isRegulating() && test2WT.getPhaseTapChanger().getRegulationTerminal() != null
                    && test2WT.getPhaseTapChanger().getRegulationTerminal().isConnected()) {
                assertDeltaMax(test2WT.getPhaseTapChanger().getTapPosition(), ref2WT.getPhaseTapChanger().getTapPosition(), 0,
                        logPrefix + "Unexpected phase tap changer position for transformer id: " + test2WT.getId(), errors);
            }
        }
        return errors;
    }

    private List<String> compareThreeWindingTransformers(String logPrefix, Network test, Network reference) {
        List<String> errors = new ArrayList<>();
        for (ThreeWindingsTransformer test3WT : test.getThreeWindingsTransformers()) {
            ThreeWindingsTransformer ref3WT = reference.getThreeWindingsTransformer(test3WT.getId());
            assertDeltaMax(test3WT.getRatedU0(), ref3WT.getRatedU0(), deltaV,
                    logPrefix + "Unexpected RatedU0 for 3W transformer id : " + test3WT.getId(), errors);

            assertDeltaMax(test3WT.getLeg1().getRatedU(), ref3WT.getLeg1().getRatedU(), deltaV,
                    logPrefix + "Unexpected RatedU for Leg1 in 3W transformer id : " + test3WT.getId(), errors);
            assertDeltaMax(test3WT.getLeg2().getRatedU(), ref3WT.getLeg2().getRatedU(), deltaV,
                    logPrefix + "Unexpected RatedU for Leg2 in 3W transformer id : " + test3WT.getId(), errors);
            assertDeltaMax(test3WT.getLeg2().getRatedU(), ref3WT.getLeg2().getRatedU(), deltaV,
                    logPrefix + "Unexpected RatedU for Leg3 in 3W transformer id : " + test3WT.getId(), errors);

            assertDeltaMax(test3WT.getLeg1().getRatedS(), ref3WT.getLeg1().getRatedS(), deltaP,
                    logPrefix + "Unexpected RatedS for Leg1 in 3W transformer id : " + test3WT.getId(), errors);
            assertDeltaMax(test3WT.getLeg2().getRatedS(), ref3WT.getLeg2().getRatedS(), deltaP,
                    logPrefix + "Unexpected RatedS for Leg2 in 3W transformer id : " + test3WT.getId(), errors);
            assertDeltaMax(test3WT.getLeg3().getRatedS(), ref3WT.getLeg3().getRatedS(), deltaP,
                    logPrefix + "Unexpected RatedS for Leg3 in 3W transformer id : " + test3WT.getId(), errors);
        }
        return errors;
    }

    private List<String> compareShuntCompensators(String logPrefix, Network test, Network reference) {
        ArrayList<String> errors = new ArrayList<>();
        for (ShuntCompensator testSC : test.getShuntCompensators()) {
            ShuntCompensator refSC = reference.getShuntCompensator(testSC.getId());
            assertEquals(testSC.getSectionCount(), refSC.getSectionCount(),
                    logPrefix + "Unexpected section count for shunt compensator id : " + testSC.getId(), errors);
            assertDeltaMax(testSC.getTerminal().getQ(), refSC.getTerminal().getQ(), deltaQ,
                    logPrefix + "Unexpected Q for shunt compensator terminal id : " + testSC.getId(), errors);
            assertDeltaMax(testSC.getTerminal().getP(), refSC.getTerminal().getP(), deltaP,
                    logPrefix + "Unexpected P for shunt compensator terminal id : " + testSC.getId(), errors);
        }
        return errors;
    }
}
