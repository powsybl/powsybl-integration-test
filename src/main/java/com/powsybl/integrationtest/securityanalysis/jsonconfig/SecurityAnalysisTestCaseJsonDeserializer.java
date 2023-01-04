/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.securityanalysis.jsonconfig;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.powsybl.commons.PowsyblException;

import java.io.IOException;
import java.util.Objects;

/**
 * Deserializer for JSON representations of {@link SecurityAnalysisTestCaseJson}
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class SecurityAnalysisTestCaseJsonDeserializer extends StdDeserializer<SecurityAnalysisTestCaseJson> {

    protected SecurityAnalysisTestCaseJsonDeserializer() {
        super(SecurityAnalysisTestCaseJson.class);
    }

    @Override
    public SecurityAnalysisTestCaseJson deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        Objects.requireNonNull(parser);
        JsonToken token;
        String id = null;
        String inputNetwork = null;
        String inputParameters = null;
        while ((token = parser.nextToken()) != null) {
            if (token == JsonToken.FIELD_NAME) {
                switch (parser.getCurrentName()) {
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
                    default:
                        throw new AssertionError("Unexpected field: " + parser.getCurrentName());
                }
            } else if (token == JsonToken.END_OBJECT) {
                return new SecurityAnalysisTestCaseJson(id, inputNetwork, inputParameters);
            }
        }
        throw new PowsyblException("Parsing error");
    }
}
