/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.creation.security.contingencies;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.auto.service.AutoService;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.contingency.Contingency;
import com.powsybl.iidm.network.*;

import java.io.IOException;
import java.util.*;

import static com.powsybl.contingency.Contingency.builder;
import static com.powsybl.integrationtest.utils.SampleUtils.createSamples;

/**
 * Efficient implementation for ContingenciesProvider.
 * Creates a list of contingencies with the following rules:
 * N-1 contingencies: Create contingencies for a subset of elements for each {@link com.powsybl.contingency.ContingencyElementType} in the network, where the subset size is given for each class of element.
 * N-2 contingencies: Create a contingency for each pair of line with same voltage levels.
 *
 * @author Théo Le Colleter <theo.le-colleter at artelys.com>
 */
@AutoService(ContingenciesProvider.class)
public class RandomContingenciesProvider extends StdDeserializer<ContingenciesProvider> implements ContingenciesProvider  {

    private HashMap<Class, Double> contingenciesRate;

    private Random r;

    public RandomContingenciesProvider() {
        this(null);
    }

    protected RandomContingenciesProvider(Class<?> vc) {
        super(vc);
    }

    @Override
    public ContingenciesProvider deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException, JacksonException
    {
        JsonNode node = jsonParser.readValueAsTree();
        Double generators = node.get("generators").asDouble();
        Double staticVarCompensators = node.get("staticVarCompensators").asDouble();
        Double shuntCompensators = node.get("shuntCompensators").asDouble();
        Double branches = node.get("branches").asDouble();
        Double hvdcLines = node.get("hvdcLines").asDouble();
        Double busbarSections = node.get("busbarSections").asDouble();
        Double danglingLines = node.get("danglingLines").asDouble();
        Double threeWindingsTransformers = node.get("threeWindingsTransformers").asDouble();
        Double loads = node.get("loads").asDouble();
        Double switches = node.get("switches").asDouble();

        HashMap<Class, Double> classRateHashMap = new HashMap<>();
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

        this.contingenciesRate = classRateHashMap;

        this.r = new Random();
        this.r.setSeed(0);

        return this;
    }

    @Override
    public List<Contingency> getContingencies(final Network network) {
        List<Contingency> contingencies = new ArrayList<>();
        HashMap<Class, List<Contingency>> contingenciesByClass = new HashMap<>();
        // Get and add every element of the network in a list of contingencies (N-1)
        network.getGenerators().forEach(e -> contingenciesByClass.computeIfAbsent(Generator.class, k -> new ArrayList<>()).add(Contingency.generator(e.getId())));
        network.getStaticVarCompensators().forEach(e -> contingenciesByClass.computeIfAbsent(StaticVarCompensator.class, k -> new ArrayList<>()).add(Contingency.staticVarCompensator(e.getId())));
        network.getShuntCompensators().forEach(e -> contingenciesByClass.computeIfAbsent(ShuntCompensator.class, k -> new ArrayList<>()).add(Contingency.shuntCompensator(e.getId())));
        network.getBranches().forEach(e -> contingenciesByClass.computeIfAbsent(Branch.class, k -> new ArrayList<>()).add(Contingency.branch(e.getId())));
        network.getHvdcLines().forEach(e -> contingenciesByClass.computeIfAbsent(HvdcLine.class, k -> new ArrayList<>()).add(Contingency.hvdcLine(e.getId())));
        network.getBusbarSections().forEach(e -> contingenciesByClass.computeIfAbsent(BusbarSection.class, k -> new ArrayList<>()).add(Contingency.busbarSection(e.getId())));
        network.getDanglingLines().forEach(e -> contingenciesByClass.computeIfAbsent(DanglingLine.class, k -> new ArrayList<>()).add(Contingency.danglingLine(e.getId())));
        network.getThreeWindingsTransformers().forEach(e -> contingenciesByClass.computeIfAbsent(ThreeWindingsTransformer.class, k -> new ArrayList<>()).add(Contingency.threeWindingsTransformer(e.getId())));
        network.getLoads().forEach(e -> contingenciesByClass.computeIfAbsent(Load.class, k -> new ArrayList<>()).add(Contingency.load(e.getId())));
        network.getSwitches().forEach(e -> contingenciesByClass.computeIfAbsent(Switch.class, k -> new ArrayList<>()).add(builder(e.getId()).addSwitch(e.getId()).build()));

        HashMap<Class, Set<Contingency>> contingenciesSampleByClass = createSamples(contingenciesByClass, contingenciesRate, r);
        contingenciesSampleByClass.forEach((c, contingenciesList) -> contingencies.addAll(contingenciesList));

        return contingencies;
    }
}
