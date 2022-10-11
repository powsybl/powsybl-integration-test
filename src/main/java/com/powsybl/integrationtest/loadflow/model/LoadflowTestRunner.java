/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.loadflow.model;

import com.powsybl.iidm.network.*;
import com.powsybl.integrationtest.model.AbstractTestRunner;
import com.powsybl.integrationtest.model.ComputationRunner;
import com.powsybl.loadflow.LoadFlow;
import com.powsybl.loadflow.LoadFlowResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A service that can perform Loadflow results comparison.
 * Feed it with a {@link LoadflowTestCase} and let it check if actual results are equal to those expected.
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class LoadflowTestRunner extends AbstractTestRunner<LoadflowComputationParameters, LoadflowComputationResults> {

    private final double deltaV = 1e-9;
    private final double deltaP = 1e-9;
    private final double deltaQ = 1e-9;
    private final double deltaAngle = 1e-9;
    private final double deltaSlackPowerMismatch = 1e-9;

    @Override
    protected ComputationRunner<LoadflowComputationParameters, LoadflowComputationResults> getComputationRunner() {
        return parameters -> {
            LoadFlowResult result = LoadFlow.run(parameters.getNetwork(), parameters.getParameters());
            return new LoadflowComputationResults(parameters.getNetwork(), result);
        };
    }

    @Override
    protected List<String> performIdentityChecks(String logPrefix, LoadflowComputationResults actual, LoadflowComputationResults expected) {
        List<String> errors = new ArrayList<>();
        errors.addAll(compareResults(logPrefix, actual.getResult(), expected.getResult()));
        errors.addAll(compareBuses(logPrefix, actual.getNetwork(), expected.getNetwork()));
        errors.addAll(compareBranches(logPrefix, actual.getNetwork(), expected.getNetwork()));
        errors.addAll(compareLoads(logPrefix, actual.getNetwork(), expected.getNetwork()));
        errors.addAll(compareGenerators(logPrefix, actual.getNetwork(), expected.getNetwork()));
        errors.addAll(compareVcsConverterStations(logPrefix, actual.getNetwork(), expected.getNetwork()));
        errors.addAll(compareLccConverterStations(logPrefix, actual.getNetwork(), expected.getNetwork()));
        errors.addAll(compareTwoWindingTransformers(logPrefix, actual.getNetwork(), expected.getNetwork()));
        return errors;
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
        for (TwoWindingsTransformer testTwt : test.getTwoWindingsTransformers()) {
            TwoWindingsTransformer refTwt = reference.getTwoWindingsTransformer(testTwt.getId());
            if (testTwt.getRatioTapChanger() != null
                    && testTwt.getRatioTapChanger().isRegulating() && testTwt.getRatioTapChanger().getRegulationTerminal() != null
                    && testTwt.getRatioTapChanger().getRegulationTerminal().isConnected()) {
                assertDeltaMax(testTwt.getRatioTapChanger().getTapPosition(), refTwt.getRatioTapChanger().getTapPosition(), 0,
                        logPrefix + "Unexpected LCC Converter P for LCC converter id: " + testTwt.getId(), errors);
            }
            if (testTwt.getPhaseTapChanger() != null
                    && testTwt.getPhaseTapChanger().isRegulating() && testTwt.getPhaseTapChanger().getRegulationTerminal() != null
                    && testTwt.getPhaseTapChanger().getRegulationTerminal().isConnected()) {
                assertDeltaMax(testTwt.getPhaseTapChanger().getTapPosition(), refTwt.getPhaseTapChanger().getTapPosition(), 0,
                        logPrefix + "Unexpected LCC Converter Q for LCC converter id: " + testTwt.getId(), errors);
            }
        }
        return errors;
    }

    List<String> compareResults(String logPrefix, LoadFlowResult test, LoadFlowResult reference) {
        List<String> errors = new ArrayList<>();
        assertEquals(test.isOk(), reference.isOk(),
                logPrefix + "LoadFlowResult reference and test isOk is different.", errors);
        assertEquals(test.getComponentResults().size(), reference.getComponentResults().size(),
                logPrefix + "LoadFlowResult reference and test component results are not the same size.", errors);

        if (test.getComponentResults().size() == reference.getComponentResults().size()) {
            for (int i = 0; i < test.getComponentResults().size(); ++i) {
                assertEquals(test.getComponentResults().get(i).getStatus(),
                        reference.getComponentResults().get(i).getStatus(),
                        logPrefix + "Status has unexpected value for component #" + i, errors);
                assertEquals(test.getComponentResults().get(i).getIterationCount(), reference.getComponentResults().get(i).getIterationCount(),
                        logPrefix + "Iterations count has ", errors);
                assertEquals(test.getComponentResults().get(i).getSlackBusId(),
                        reference.getComponentResults().get(i).getSlackBusId(),
                        logPrefix + "Slack bus id has unexpected value for component #" + i, errors);
                assertDeltaMax(test.getComponentResults().get(i).getSlackBusActivePowerMismatch(),
                        reference.getComponentResults().get(i).getSlackBusActivePowerMismatch(),
                        deltaSlackPowerMismatch,
                        logPrefix + "Slack power mismatch has unexpected value for component #" + i, errors);
            }
        }
        return errors;
    }

    /**
     * Check that values difference is smaller than {@code deltaV}. Since the {@link Comparable#compareTo} method is
     * used for values comparison, it is advisable to use only numeric values in arguments.
     * If difference bewteen the vlaues is greater than {@code deltaV}, {@code errMessage} gets added to {@code errors}
     * list.
     *
     * @param value1     first value
     * @param value2     second value
     * @param deltaV     maximal tolerated difference between
     * @param errMessage message to add to errors list if check fails
     * @param errors     errors list
     */
    private void assertDeltaMax(double value1, double value2, double deltaV, String errMessage, List<String> errors) {
        if (Math.abs(value1 - value2) > deltaV) {
            errors.add(errMessage + " (expected " + value2 + " but had " + value1 + ")");
        }
    }

    /**
     * Check that provided values are equal
     *
     * @param value1     First value
     * @param value2     Second value
     * @param errMessage Message to add to {@code errors} if values are not equal.
     * @param errors     errors list
     * @param <T>        type of values to compare
     */
    private <T> void assertEquals(T value1, T value2, String errMessage, List<String> errors) {
        if (!Objects.equals(value1, value2)) {
            errors.add(errMessage);
        }
    }
}
