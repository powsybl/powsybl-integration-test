/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.integrationtest.securityanalysis.model;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import com.powsybl.integrationtest.model.AbstractTestRunner;
import com.powsybl.integrationtest.model.ComputationRunner;
import com.powsybl.integrationtest.model.TestRunner;
import com.powsybl.integrationtest.utils.NetworksComparator;
import com.powsybl.security.LimitViolation;
import com.powsybl.security.LimitViolationsResult;
import com.powsybl.security.SecurityAnalysisResult;
import com.powsybl.security.results.*;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.powsybl.integrationtest.utils.CompareUtils.assertDeltaMax;
import static com.powsybl.integrationtest.utils.CompareUtils.assertEquals;

/**
 * A {@link TestRunner} for Security Analysis
 *
 * @author Arthur Michaut <arthur.michaut at artelys.com>
 */
public class SecurityAnalysisTestRunner
        extends AbstractTestRunner<SecurityAnalysisComputationParameters, SecurityAnalysisComputationResults> {

    private final double deltaV = 1e-6;
    private final double deltaI = 1e-6;
    private final double deltaP = 1e-6;
    private final double deltaQ = 1e-6;
    private final double deltaAngle = 1e-6;
    private final double deltaOther = 1e-6;

    @Override
    protected ComputationRunner<SecurityAnalysisComputationParameters, SecurityAnalysisComputationResults> getComputationRunner() {
        return new SecurityAnalysisComputationRunner();
    }

    @Override
    protected List<String> performIdentityChecks(String logPrefix, SecurityAnalysisComputationResults actual,
                                                 SecurityAnalysisComputationResults expected) {
        List<String> errors = new ArrayList<>();
        NetworksComparator nComparator = new NetworksComparator(deltaV, deltaAngle, deltaP, deltaQ);
        errors.addAll(nComparator.compareNetworks(logPrefix, actual.getNetwork(), expected.getNetwork()));
        errors.addAll(compareResults(logPrefix, actual.getResults(), expected.getResults()));
        return errors;
    }

    @Nonnull
    private Collection<String> compareResults(String logPrefix, SecurityAnalysisResult actualResults,
                                              SecurityAnalysisResult expectedResults) {
        List<String> errorMessages = new ArrayList<>();
        // Compare pre-contingency results
        final String preLF = logPrefix + " [pre-contingency]";
        PreContingencyResult actPreRes = actualResults.getPreContingencyResult();
        PreContingencyResult expPreRes = expectedResults.getPreContingencyResult();
        assertEquals(actPreRes.getStatus(), expPreRes.getStatus(),
                preLF + " Unexpected pre-contingencies result status", errorMessages);
        errorMessages.addAll(compareNetworkResults(preLF, actPreRes.getNetworkResult(), expPreRes.getNetworkResult()));
        errorMessages.addAll(compareLimitViolations(preLF, actPreRes.getLimitViolationsResult(), expPreRes.getLimitViolationsResult()));
        // Compare post-contingency results
        Map<String, PostContingencyResult> actualPCResults =
                extractIndexedResults(actualResults.getPostContingencyResults(), r -> r.getContingency().getId());
        Map<String, PostContingencyResult> expectedPCResults =
                extractIndexedResults(expectedResults.getPostContingencyResults(), r -> r.getContingency().getId());
        assertEquals(
                actualPCResults.keySet(),
                expectedPCResults.keySet(),
                logPrefix + " Unexpected contingencies in results",
                errorMessages);
        for (String contingencyId : actualPCResults.keySet()) {
            final String postLF = logPrefix + " [contingency " + contingencyId + "]";
            PostContingencyResult actPostRes = actualPCResults.get(contingencyId);
            PostContingencyResult expPostRes = expectedPCResults.get(contingencyId);
            assertEquals(actPostRes.getStatus(), expPostRes.getStatus(),
                    postLF + " Unexpected post-contingencies result status", errorMessages);
            errorMessages.addAll(compareNetworkResults(postLF, actPostRes.getNetworkResult(), expPostRes.getNetworkResult()));
            errorMessages.addAll(compareLimitViolations(postLF, actPostRes.getLimitViolationsResult(), expPostRes.getLimitViolationsResult()));
        }
        return errorMessages;
    }

    private Collection<String> compareNetworkResults(String logPrefix, NetworkResult actualNResult,
                                                     NetworkResult expectedNResult) {
        List<String> errorMessages = new ArrayList<>();
        Set<String> actualBranches
                = extractIndexedResults(actualNResult.getBranchResults(), BranchResult::getBranchId).keySet();
        Set<String> expectedBranches
                = extractIndexedResults(expectedNResult.getBranchResults(), BranchResult::getBranchId).keySet();
        assertEquals(actualBranches, expectedBranches,
                logPrefix + " Unexpected branches in results", errorMessages);
        if (actualBranches.equals(expectedBranches)) {
            for (BranchResult actPreContBranchRes : actualNResult.getBranchResults()) {
                String branchId = actPreContBranchRes.getBranchId();
                BranchResult expPreContBranchRes = expectedNResult.getBranchResult(branchId);
                errorMessages.addAll(compareBranchResults(logPrefix, actPreContBranchRes, expPreContBranchRes));
            }
        }
        Set<String> actualBuses = extractIndexedResults(actualNResult.getBusResults(), BusResult::getBusId).keySet();
        Set<String> expectedBuses = extractIndexedResults(expectedNResult.getBusResults(), BusResult::getBusId).keySet();
        assertEquals(actualBuses, expectedBuses,
                logPrefix + " Unexpected buses in results", errorMessages);
        if (actualBuses.equals(expectedBuses)) {
            for (BusResult actualPreContBusRes : actualNResult.getBusResults()) {
                String busId = actualPreContBusRes.getBusId();
                BusResult expectedPreContBusRes = expectedNResult.getBusResult(busId);
                errorMessages.addAll(compareBusResults(logPrefix, actualPreContBusRes, expectedPreContBusRes));
            }
        }
        Map<String, ThreeWindingsTransformerResult> actual3WTransfos
                = extractIndexedResults(actualNResult.getThreeWindingsTransformerResults(),
                ThreeWindingsTransformerResult::getThreeWindingsTransformerId);
        Map<String, ThreeWindingsTransformerResult> expected3Wtransfos
                = extractIndexedResults(expectedNResult.getThreeWindingsTransformerResults(),
                ThreeWindingsTransformerResult::getThreeWindingsTransformerId);
        assertEquals(actual3WTransfos.keySet(), expected3Wtransfos.keySet(),
                logPrefix + " Unexpected 3W transformers in results",
                errorMessages);
        if (actual3WTransfos.equals(expected3Wtransfos)) {
            for (ThreeWindingsTransformerResult actualPreCont3WTransfoResults :
                    actualNResult.getThreeWindingsTransformerResults()) {
                String threeWindingsTransformerId = actualPreCont3WTransfoResults.getThreeWindingsTransformerId();
                ThreeWindingsTransformerResult expectedPreCont3WTransfoResults = actualNResult
                        .getThreeWindingsTransformerResult(threeWindingsTransformerId);
                errorMessages.addAll(compare3WResults(logPrefix, actualPreCont3WTransfoResults,
                        expectedPreCont3WTransfoResults));
            }
        }
        return errorMessages;
    }

    @Nonnull
    private Collection<String> compareBranchResults(String logPrefix, BranchResult result, BranchResult reference) {
        ArrayList<String> errorMessages = new ArrayList<>();
        final String prefix = "[SA branch result]" + logPrefix;
        assertDeltaMax(result.getI1(), reference.getI1(), this.deltaI,
                prefix + " Unexpected terminal 1 I for branch id: " + result.getBranchId(), errorMessages);
        assertDeltaMax(result.getI2(), reference.getI2(), this.deltaI,
                prefix + " Unexpected terminal 2 I for branch id: " + result.getBranchId(), errorMessages);
        assertDeltaMax(result.getP1(), reference.getP1(), this.deltaP,
                prefix + " Unexpected terminal 1 P for branch id: " + result.getBranchId(), errorMessages);
        assertDeltaMax(result.getP2(), reference.getP2(), this.deltaP,
                prefix + " Unexpected terminal 2 P for branch id: " + result.getBranchId(), errorMessages);
        assertDeltaMax(result.getQ1(), reference.getQ1(), this.deltaQ,
                prefix + " Unexpected terminal 1 Q for branch id: " + result.getBranchId(), errorMessages);
        assertDeltaMax(result.getQ2(), reference.getQ2(), this.deltaQ,
                prefix + " Unexpected terminal 2 Q for branch id: " + result.getBranchId(), errorMessages);
        assertDeltaMax(result.getFlowTransfer(), reference.getFlowTransfer(), this.deltaP,
                prefix + " Unexpected flow transfer for branch id: " + result.getBranchId(), errorMessages);
        return errorMessages;
    }

    @Nonnull
    private Collection<String> compareBusResults(String logPrefix, BusResult result, BusResult reference) {
        ArrayList<String> errorMessages = new ArrayList<>();
        final String prefix = "[SA bus result]" + logPrefix;
        assertDeltaMax(result.getV(), reference.getV(), deltaV,
                prefix + " Unexpected bus V for bus id: " + result.getBusId(), errorMessages);
        assertDeltaMax(result.getAngle(), reference.getAngle(), deltaAngle,
                prefix + " Unexpected bus angle for bus id: " + result.getBusId(), errorMessages);
        assertEquals(result.getVoltageLevelId(), reference.getVoltageLevelId(),
                prefix + " Unexpected bus  voltage level for bus id: " + result.getBusId(), errorMessages);
        return errorMessages;
    }

    @Nonnull
    private Collection<String> compare3WResults(String logPrefix,
                                                ThreeWindingsTransformerResult result,
                                                ThreeWindingsTransformerResult reference) {
        ArrayList<String> errorMessages = new ArrayList<>();
        final String prefix = "[SA 3WT result]" + logPrefix;
        assertDeltaMax(result.getI1(), reference.getI1(), deltaI,
                prefix + " Unexpected terminal 1 I for 3WT id : " + result.getThreeWindingsTransformerId(),
                errorMessages);
        assertDeltaMax(result.getI2(), reference.getI2(), deltaI,
                prefix + " Unexpected terminal 2 I for 3WT id : " + result.getThreeWindingsTransformerId(),
                errorMessages);
        assertDeltaMax(result.getI3(), reference.getI3(), deltaI,
                prefix + " Unexpected terminal 3 I for 3WT id : " + result.getThreeWindingsTransformerId(),
                errorMessages);
        assertDeltaMax(result.getP1(), reference.getP1(), deltaP,
                prefix + " Unexpected terminal 1 P for 3WT id : " + result.getThreeWindingsTransformerId(),
                errorMessages);
        assertDeltaMax(result.getP2(), reference.getP2(), deltaP,
                prefix + " Unexpected terminal 2 P for 3WT id : " + result.getThreeWindingsTransformerId(),
                errorMessages);
        assertDeltaMax(result.getP3(), reference.getP3(), deltaP,
                prefix + " Unexpected terminal 3 P for 3WT id : " + result.getThreeWindingsTransformerId(),
                errorMessages);
        assertDeltaMax(result.getQ1(), reference.getQ1(), deltaQ,
                prefix + " Unexpected terminal 1 Q for 3WT id : " + result.getThreeWindingsTransformerId(),
                errorMessages);
        assertDeltaMax(result.getQ2(), reference.getQ2(), deltaQ,
                prefix + " Unexpected terminal 2 Q for 3WT id : " + result.getThreeWindingsTransformerId(),
                errorMessages);
        assertDeltaMax(result.getQ3(), reference.getQ3(), deltaQ,
                prefix + " Unexpected terminal 3 Q for 3WT id : " + result.getThreeWindingsTransformerId(),
                errorMessages);
        return errorMessages;
    }

    @Nonnull
    private Collection<String> compareLimitViolations(String logPrefix, LimitViolationsResult result,
                                                      LimitViolationsResult reference) {
        ArrayList<String> errorMessages = new ArrayList<>();
        final String prefix = "[SA LV result]" + logPrefix;
        // Index limits to be able to compare them
        Multimap<String, LimitViolation> resultLimits = extractIndexedMultiResults(result.getLimitViolations(),
            lv -> lv.getSubjectId() + '/' + lv.getLimitType().toString() + lv.getSide());
        Multimap<String, LimitViolation> referenceLimits = extractIndexedMultiResults(reference.getLimitViolations(),
            lv -> lv.getSubjectId() + '/' + lv.getLimitType().toString() + lv.getSide());
        // Check that indexes are the same
        assertEquals(resultLimits.keySet(), referenceLimits.keySet(),
                prefix + " Unexpected limit violations", errorMessages);
        if (resultLimits.keySet().equals(referenceLimits.keySet())) {
            for (String key : resultLimits.keySet()) {
                Collection<LimitViolation> lvs = resultLimits.get(key);
                Collection<LimitViolation> refLvs = referenceLimits.get(key);
                // Check that there are the same number of limit violations for key
                assertEquals(lvs.size(), refLvs.size(), prefix + " Unexpected number of limit violations", errorMessages);
                if (lvs.size() == refLvs.size()) {
                    List<LimitViolation> sortedLvs = getSortedList(lvs, Comparator.comparing(LimitViolation::getValue));
                    List<LimitViolation> sortedRefLvs = getSortedList(refLvs, Comparator.comparing(LimitViolation::getValue));
                    for (int i = 0; i < sortedLvs.size(); i++) {
                        checkLimitViolation(sortedRefLvs.get(i), sortedLvs.get(i), key, prefix, errorMessages);
                    }
                }
            }
        }
        return errorMessages;
    }

    private void checkLimitViolation(LimitViolation refLv, LimitViolation lv, String key, String prefix, ArrayList<String> errorMessages) {
        // Find the right delta for the physical value of the limit violation
        double delta = switch (lv.getLimitType()) {
            case ACTIVE_POWER, APPARENT_POWER -> deltaP;
            case CURRENT, HIGH_SHORT_CIRCUIT_CURRENT, LOW_SHORT_CIRCUIT_CURRENT -> deltaI;
            case HIGH_VOLTAGE, LOW_VOLTAGE -> deltaV;
            default -> deltaOther;
        };
        assertDeltaMax(lv.getValue(), refLv.getValue(), delta,
                prefix + " Unexpected LV value for LV: " + key, errorMessages);
        assertDeltaMax(lv.getLimit(), refLv.getLimit(), delta,
                prefix + " Unexpected LV limit for LV: " + key, errorMessages);
        assertDeltaMax(lv.getLimitReduction(), refLv.getLimitReduction(), delta,
                prefix + " Unexpected LV limit reduction for LV: " + key, errorMessages);
        assertEquals(lv.getLimitName(), refLv.getLimitName(),
                prefix + " Unexpected limit name for LV: " + key, errorMessages);

        assertEquals(lv.getSide(), refLv.getSide(),
                prefix + " Unexpected side for LV: " + key, errorMessages);

        assertEquals(lv.getSubjectId(), refLv.getSubjectId(),
                prefix + " Unexpected subjectId for LV: " + key, errorMessages);
        assertEquals(lv.getSubjectName(), refLv.getSubjectName(),
                prefix + " Unexpected subjectName for LV: " + key, errorMessages);

        assertEquals(lv.getAcceptableDuration(), refLv.getAcceptableDuration(),
                prefix + " Unexpected acceptable duration for LV: " + key, errorMessages);
    }

    private <A> List<A> getSortedList(Collection<A> collection, Comparator<A> comparator) {
        return collection.stream().sorted(comparator).toList();
    }

    /**
     * @return a map from the provided list, using a key extractor to index each element
     */
    private <T, U> Map<T, U> extractIndexedResults(Collection<U> list, Function<U, T> keyExtractor) {
        return list.stream().collect(Collectors.toMap(keyExtractor, Function.identity()));
    }

    /**
     * @return a multi-map from the provided list, using a key extractor to index each element
     */
    private <T, U> Multimap<T, U> extractIndexedMultiResults(Collection<U> list, Function<U, T> keyExtractor) {
        return list.stream().collect(ImmutableListMultimap.toImmutableListMultimap(keyExtractor, Function.identity()));
    }
}
