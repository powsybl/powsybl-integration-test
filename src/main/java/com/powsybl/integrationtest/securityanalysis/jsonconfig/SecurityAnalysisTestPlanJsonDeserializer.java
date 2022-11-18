/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.securityanalysis.jsonconfig;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Deserializer for JSON representations of {@link SecurityAnalysisTestPlanJson}
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class SecurityAnalysisTestPlanJsonDeserializer extends StdDeserializer<SecurityAnalysisTestPlanJson> {

    protected SecurityAnalysisTestPlanJsonDeserializer() {
        super(SecurityAnalysisTestPlanJson.class);
    }

    @Override
    public SecurityAnalysisTestPlanJson deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        String version = null;
        List<SecurityAnalysisTestCaseJson> testCases = null;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            switch (parser.getCurrentName()) {
                case "version":
                    parser.nextToken();
                    version = parser.getValueAsString();
                    break;
                case "testCases":
                    parser.nextToken();
                    testCases = parser.readValueAs(new TypeReference<ArrayList<SecurityAnalysisTestCaseJson>>() {
                    });
                    break;
                default:
                    throw new AssertionError("Invalid test plan JSON field: " + parser.getCurrentName());
            }
        }
        if (!"1.0".equals(version)) {
            throw new AssertionError("Only version 1.0 is supported: " + parser.getCurrentName());
        }
        return new SecurityAnalysisTestPlanJson(testCases);
    }
}
