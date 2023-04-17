# powsybl-integration-test
Contains integration testing for powsybl library blocks

## Test plan references updating

When breaking changes are introduced and the references must be updated you can automatically update references by launching the two tests :

- LoadFlowIntegrationTest.updateLoadFlowReference
- SecurityAnalysisIntegrationTest.updateSaReference

Those two tests iterate on the test plans, execute them and override the reference files associated.

You can then add the modified file to be commited.
