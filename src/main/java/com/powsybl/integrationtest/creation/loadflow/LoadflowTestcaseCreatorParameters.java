/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.creation.loadflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Parameters used by the Loadflow test cases creator. Made to be loaded from a JSON file
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
class LoadflowTestcaseCreatorParameters {

    @JsonProperty
    List<Parameters> parameters;

    public List<Parameters> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameters> parameters) {
        this.parameters = parameters;
    }

    static class Parameters {
        @JsonProperty
        private String testCaseName;
        @JsonProperty
        private Path networkPath;
        @JsonProperty
        private Path lfParametersPath;
        @JsonProperty
        private Path outputPath;

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

        public Path getLfParametersPath() {
            return lfParametersPath;
        }

        @SuppressWarnings("unused")
        public void setLfParametersPath(Path lfParametersPath) {
            this.lfParametersPath = lfParametersPath;
        }

        public Path getOutputPath() {
            return outputPath;
        }

        @SuppressWarnings("unused")
        public void setOutputPath(Path outputPath) {
            this.outputPath = outputPath;
        }

    }

    /**
     * Load the content of provided JSON file and produce {@link LoadflowTestcaseCreatorParameters} from it
     *
     * @param inputFilePath path to the file to use as input
     * @return the LoadflowTestcaseCreatorParameters built from provided input file
     * @throws IOException if file can not be loaded or read
     */
    static LoadflowTestcaseCreatorParameters load(Path inputFilePath) throws IOException {
        final ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        return mapper.readValue(inputFilePath.toFile(), LoadflowTestcaseCreatorParameters.class);
    }
}
