/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.tests;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.powsybl.commons.PowsyblException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

/**
 * @author Bertrand Rix <bertrand.rix at artelys.com>
 */
public class LoadFlowTestCaseJson {

    private String network;

    private String parameters;

    private String networkRef;

    private String resultRef;

    private String id;

    public LoadFlowTestCaseJson(String id, String network, String parameters, String networkRef, String resultRef) {
        this.id = id;
        this.network = network;
        this.parameters = parameters;
        this.networkRef = networkRef;
        this.resultRef = resultRef;
    }

    public String getId() {
        return id;
    }

    public String getNetwork() {
        return network;
    }

    public String getParameters() {
        return parameters;
    }

    public String getNetworkRef() {
        return networkRef;
    }

    public String getResultRef() {
        return resultRef;
    }

    public static LoadFlowTestCaseJson parseJson(JsonParser parser) {
        Objects.requireNonNull(parser);
        try {
            JsonToken token;
            String network = null;
            String parameters = null;
            String networkRef = null;
            String resultRef = null;
            String id = null;
            while ((token = parser.nextToken()) != null) {
                if (token == JsonToken.FIELD_NAME) {
                    String fieldName = parser.getCurrentName();
                    switch (fieldName) {
                        case "id":
                            parser.nextToken();
                            id = parser.getText();
                            break;
                        case "network":
                            parser.nextToken();
                            network = parser.getText();
                            break;
                        case "parameters":
                            parser.nextToken();
                            parameters = parser.getText();
                            break;
                        case "networkRef":
                            parser.nextToken();
                            networkRef = parser.getText();
                            break;
                        case "resultRef":
                            parser.nextToken();
                            resultRef = parser.getText();
                            break;
                        default:
                            throw new PowsyblException("Unexpected field: " + fieldName);
                    }
                } else if (token == JsonToken.END_OBJECT) {
                    return new LoadFlowTestCaseJson(id, network, parameters, networkRef, resultRef);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        throw new PowsyblException("Parsing error");
    }
}
