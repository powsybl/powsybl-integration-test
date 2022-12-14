/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.creation.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.powsybl.iidm.network.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

/**
 * Parameters used by the SA test cases creator. Made to be loaded from a JSON file
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class SATestcaseCreatorParameters {

    private List<Parameters> parameters;

    public List<Parameters> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameters> parameters) {
        this.parameters = parameters;
    }

    static class StateMonitorsRate {
        @JsonProperty
        private int branches;
        @JsonProperty
        private int voltageLevels;
        @JsonProperty
        private int threeWindingsTransformers;

        public int getBranchesRate() {
            return  branches;
        }

        public int getVoltageLevelsRate() {
            return voltageLevels;
        }

        public int getThreeWindingsTransformersRate() {
            return threeWindingsTransformers;
        }

        public HashMap<Class, Integer> getRates() {
            HashMap<Class, Integer> classRateHashMap = new HashMap<>();
            classRateHashMap.put(Branch.class, branches);
            classRateHashMap.put(VoltageLevel.class, voltageLevels);
            classRateHashMap.put(ThreeWindingsTransformer.class, threeWindingsTransformers);

            return classRateHashMap;
        }
    }

    static class ContingenciesRate {
        @JsonProperty
        private int generators;
        @JsonProperty
        private int staticVarCompensators;
        @JsonProperty
        private int shuntCompensators;
        @JsonProperty
        private int branches;
        @JsonProperty
        private int hvdcLines;
        @JsonProperty
        private int busbarSections;
        @JsonProperty
        private int danglingLines;
        @JsonProperty
        private int threeWindingsTransformers;
        @JsonProperty
        private int loads;
        @JsonProperty
        private int switches;

        public HashMap<Class, Integer> getRates() {
            HashMap<Class, Integer> classRateHashMap = new HashMap<>();
            classRateHashMap.put(Generator.class, generators);
            classRateHashMap.put(StaticVarCompensator.class, staticVarCompensators);
            classRateHashMap.put(ShuntCompensator.class, shuntCompensators);
            classRateHashMap.put(Branch.class, branches);
            classRateHashMap.put(HvdcLine.class, hvdcLines);
            classRateHashMap.put(BusbarSection.class, busbarSections);
            classRateHashMap.put(DanglingLine.class, danglingLines);
            classRateHashMap.put(ThreeWindingsTransformer.class, threeWindingsTransformers);
            classRateHashMap.put(Load.class, loads);
            classRateHashMap.put(Switch.class, switches);

            return classRateHashMap;
        }
    }

    static class Rate {
        @JsonProperty
        private StateMonitorsRate stateMonitors;

        @JsonProperty
        private ContingenciesRate contingencies;

        public StateMonitorsRate getStateMonitorsRate() {
            return stateMonitors;
        }

        public ContingenciesRate getContingenciesRate() {
            return contingencies;
        }
    }

    static class Parameters {
        @JsonProperty
        private String testCaseName;
        @JsonProperty
        private Path networkPath;
        @JsonProperty
        private Path saParametersPath;
        @JsonProperty
        private Path contingenciesOutputPath;
        @JsonProperty
        private Path stateMonitorsOutputPath;
        @JsonProperty
        private Path outputPath;
        @JsonProperty
        private Rate rate;

        public String getTestCaseName() {
            return testCaseName;
        }

        @SuppressWarnings("unused")
        public void setTestCaseName(String testCaseName) {
            this.testCaseName = testCaseName;
        }

        public Path getNetworkPath() {
            return networkPath;
        }

        @SuppressWarnings("unused")
        public void setNetworkPath(Path networkPath) {
            this.networkPath = networkPath;
        }

        public Path getSAParametersPath() {
            return saParametersPath;
        }

        @SuppressWarnings("unused")
        public void setSAParametersPath(Path saParametersPath) {
            this.saParametersPath = saParametersPath;
        }

        public Path getContingenciesOutputPath() {
            return contingenciesOutputPath;
        }

        @SuppressWarnings("unused")
        public void setContingenciesOutputPath(Path contingenciesOutputPath) {
            this.contingenciesOutputPath = contingenciesOutputPath;
        }

        public Path getStateMonitorsOutputPath() {
            return stateMonitorsOutputPath;
        }

        @SuppressWarnings("unused")
        public void setStateMonitorsPath(Path stateMonitorsPath) {
            this.stateMonitorsOutputPath = stateMonitorsPath;
        }

        public Path getOutputPath() {
            return outputPath;
        }

        @SuppressWarnings("unused")
        public void setOutputPath(Path outputPath) {
            this.outputPath = outputPath;
        }

        public Rate getRate() {
            return rate;
        }
    }

    /**
     * Load SA test case creation parameters from a file
     *
     * @param inputFilePath path to the input file
     * @return {@link SATestcaseCreatorParameters} loaded from input file
     */
    static SATestcaseCreatorParameters load(Path inputFilePath) throws IOException {
        final ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        return mapper.readValue(inputFilePath.toFile(), SATestcaseCreatorParameters.class);
    }

}
