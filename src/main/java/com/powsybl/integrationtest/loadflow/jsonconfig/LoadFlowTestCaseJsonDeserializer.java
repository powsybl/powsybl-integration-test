/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.loadflow.jsonconfig;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.powsybl.commons.PowsyblException;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Bertrand Rix <bertrand.rix at artelys.com>
 */
public class LoadFlowTestCaseJsonDeserializer extends StdDeserializer<LoadFlowTestCaseJson> {

    protected LoadFlowTestCaseJsonDeserializer() {
        super(LoadFlowTestCaseJson.class);
    }

    @Override
    public LoadFlowTestCaseJson deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        Objects.requireNonNull(parser);
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
                return new LoadFlowTestCaseJson(id, inputNetwork, inputParameters, expectedNetwork, expectedResults);
            }
        }
        throw new PowsyblException("Parsing error");
    }
}
