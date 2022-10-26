/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.loadflow.jsonconfig;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.powsybl.commons.PowsyblException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

/**
 * @author Bertrand Rix <bertrand.rix at artelys.com>
 */
public class LoadflowTestCaseJson {

    private final String id;

    private final String inputNetwork;

    private final String inputParameters;

    private final String expectedNetwork;

    private final String expectedResults;

    public LoadflowTestCaseJson(String id, String inputNetwork, String inputParameters, String expectedNetwork, String expectedResults) {
        this.id = Objects.requireNonNull(id);
        this.inputNetwork = Objects.requireNonNull(inputNetwork);
        this.inputParameters = Objects.requireNonNull(inputParameters);
        this.expectedNetwork = Objects.requireNonNull(expectedNetwork);
        this.expectedResults = Objects.requireNonNull(expectedResults);
    }

    public String getId() {
        return id;
    }

    public String getInputNetwork() {
        return inputNetwork;
    }

    public String getInputParameters() {
        return inputParameters;
    }

    public String getExpectedNetwork() {
        return expectedNetwork;
    }

    public String getExpectedResults() {
        return expectedResults;
    }

    public static LoadflowTestCaseJson parseJson(JsonParser parser) {
        Objects.requireNonNull(parser);
        try {
            JsonToken token;
            String inputNetwork = null;
            String inputParameters = null;
            String expectedNetwork = null;
            String expectedResults = null;
            String id = null;
            while ((token = parser.nextToken()) != null) {
                if (token == JsonToken.FIELD_NAME) {
                    String fieldName = parser.getCurrentName();
                    switch (fieldName) {
                        case "id":
                            parser.nextToken();
                            id = parser.getText();
                            break;
                        case "inputNetwork":
                            parser.nextToken();
                            inputNetwork = parser.getText();
                            break;
                        case "inputParameters":
                            parser.nextToken();
                            inputParameters = parser.getText();
                            break;
                        case "expectedNetwork":
                            parser.nextToken();
                            expectedNetwork = parser.getText();
                            break;
                        case "expectedResults":
                            parser.nextToken();
                            expectedResults = parser.getText();
                            break;
                        default:
                            throw new PowsyblException("Unexpected field: " + fieldName);
                    }
                } else if (token == JsonToken.END_OBJECT) {
                    return new LoadflowTestCaseJson(id, inputNetwork, inputParameters, expectedNetwork, expectedResults);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        throw new PowsyblException("Parsing error");
    }
}
