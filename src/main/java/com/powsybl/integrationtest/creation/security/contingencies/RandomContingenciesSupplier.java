/*
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.creation.security.contingencies;

import com.google.auto.service.AutoService;
import com.powsybl.contingency.Contingency;
import com.powsybl.iidm.network.*;

import java.util.*;

import static com.powsybl.contingency.Contingency.builder;
import static com.powsybl.integrationtest.utils.SampleUtils.createSamples;

/**
 * Implementation of {@link ContingenciesSupplier} which creates a list of contingencies for subset of elements in the network.
 *
 * @author Théo Le Colleter <theo.le-colleter at artelys.com>
 */
@AutoService(ContingenciesSupplier.class)
public class RandomContingenciesSupplier implements ContingenciesSupplier {

    /**
     * Hashmap associating:
     * <ul>
     *     <li> Class of the {@link Network}'s element (e.g. {@link Line}, {@link Branch},...)
     *     <li> to a rate, which is a percentage (from 0 to 100) indicating the size of the subset of elements of the corresponding <i>Class</i> we want to select (randomly here) to create contingencies.
     * </ul>
     */
    private HashMap<String, Double> contingenciesRate;

    private Random r;

    /**
     * Creates N-1 contingencies for a subset of elements, for each {@link com.powsybl.contingency.ContingencyElementType} in the network,
     * where the subset size is determined from a rate associated to each class of element in {@link #contingenciesRate}.
     */
    @Override
    public List<Contingency> getContingencies(final Network network, HashMap<String, ?> configuration) {
        // Set configuration
        this.contingenciesRate = new HashMap<>();
        ((HashMap<String, Number>) configuration).forEach((element, rate) -> this.contingenciesRate.put(element, rate.doubleValue()));
        this.r = new Random();
        this.r.setSeed(0);

        List<Contingency> contingencies = new ArrayList<>();
        HashMap<String, List<Contingency>> contingenciesByClass = new HashMap<>();
        // Get and add every element of the network in a list of contingencies (N-1)
        network.getGenerators().forEach(e -> contingenciesByClass.computeIfAbsent(Generator.class.getSimpleName(), k -> new ArrayList<>()).add(Contingency.generator(e.getId())));
        network.getStaticVarCompensators().forEach(e -> contingenciesByClass.computeIfAbsent(StaticVarCompensator.class.getSimpleName(), k -> new ArrayList<>()).add(Contingency.staticVarCompensator(e.getId())));
        network.getShuntCompensators().forEach(e -> contingenciesByClass.computeIfAbsent(ShuntCompensator.class.getSimpleName(), k -> new ArrayList<>()).add(Contingency.shuntCompensator(e.getId())));
        network.getBranches().forEach(e -> contingenciesByClass.computeIfAbsent(Branch.class.getSimpleName(), k -> new ArrayList<>()).add(Contingency.branch(e.getId())));
        network.getHvdcLines().forEach(e -> contingenciesByClass.computeIfAbsent(HvdcLine.class.getSimpleName(), k -> new ArrayList<>()).add(Contingency.hvdcLine(e.getId())));
        network.getBusbarSections().forEach(e -> contingenciesByClass.computeIfAbsent(BusbarSection.class.getSimpleName(), k -> new ArrayList<>()).add(Contingency.busbarSection(e.getId())));
        network.getDanglingLines().forEach(e -> contingenciesByClass.computeIfAbsent(DanglingLine.class.getSimpleName(), k -> new ArrayList<>()).add(Contingency.danglingLine(e.getId())));
        network.getThreeWindingsTransformers().forEach(e -> contingenciesByClass.computeIfAbsent(ThreeWindingsTransformer.class.getSimpleName(), k -> new ArrayList<>()).add(Contingency.threeWindingsTransformer(e.getId())));
        network.getLoads().forEach(e -> contingenciesByClass.computeIfAbsent(Load.class.getSimpleName(), k -> new ArrayList<>()).add(Contingency.load(e.getId())));
        network.getSwitches().forEach(e -> contingenciesByClass.computeIfAbsent(Switch.class.getSimpleName(), k -> new ArrayList<>()).add(builder(e.getId()).addSwitch(e.getId()).build()));

        HashMap<String, Set<Contingency>> contingenciesSampleByClass = createSamples(contingenciesByClass, contingenciesRate, r);
        contingenciesSampleByClass.forEach((clazz, contingencySet) -> contingencies.addAll(contingencySet));

        return contingencies;
    }
}
