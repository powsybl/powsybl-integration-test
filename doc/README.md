# How to use powsybl-integration-test

## Main concepts

### Calculations, parameters, results
To run tests, you will need to provide the test runners a bunch of test cases. A test case is composed of two things:
- Parameters. An element that contains all the data necessary to run the calculation.
- Results. An element that contains all the data that are produced by the calculation.

```
 ------------         -------------         ---------
| parameters | ----> | calculation | ----> | results |
 ------------         -------------         ---------
```

### Test cases
A test case is the base input of all tests. It contains: 
- **parameters** that are a container for calculation inputs
- **references** that are *expected* results for the calculation, using the provided parameters.

Obviously, a test case actual form depends on the calculation that you want to run. For instance, a test case for Load 
Flow calculations will contain:
- **Load flow calculation parameters**
  - A network, defining the problem topology.
  - A LoadFlowParameters object, defining the parameters to apply to the calculation 
    (See Java class [com.powsybl.loadflow.LoadFlowParameters](https://github.com/powsybl/powsybl-core/blob/main/loadflow/loadflow-api/src/main/java/com/powsybl/loadflow/LoadFlowParameters.java)).
- **Load flow calculation results**
  - A network, containing all the numeric results of the calculation (associated to the equipments).
  - A LoadFlowResult object, with data on the execution.
    (See Java class [com.powsybl.loadflow.LoadFlowResult](https://github.com/powsybl/powsybl-core/blob/main/loadflow/loadflow-api/src/main/java/com/powsybl/loadflow/LoadFlowResult.java))
    
### Test case definition
Test cases need to be defined in a test plan JSON file, each test case being a distinct JSON object.
Report to [loadFlowTestPlan.json](../src/test/resources/loadFlowTestPlan.json) to see a live example.

Each test case references files that are located in the [src/test/resources](../src/test/resources) directory.
In this example, each test case defines input and output for a Loadflow test:
- **inputNetwork**: network on which loadflow calculation is run. Needs to be a file in a supported format for network 
  import by Poswybl (xiidm, cgmes or matpower).
- **inputParameters**: parameters to use for the calculation. Needs to be in the powsybl JSON serialised format.
- **expectedNetwork**: expected network after calculation (serialized form).
- **expectedResults**: expected results after calculation. Needs to be in the powsybl JSON serialised format.

## Testcase creator
To create a new test case, you will often need to create your own expected network and expected results files. The 
process can be tedious, especially if you want to add a lot of networks. The [LoadflowTestcaseCreator](../src/main/java/com/powsybl/integrationtest/creation/loadflow/LoadflowTestcaseCreator.java)
will help you automate your testcase creation by generating loadflow results files.

At the moment, the class is only usable by its main method. You need to use your IDE to run it directly, providing an
input file such as the example : [example-loadflow-input](example-loadflow-input.json).
This is an example file for loadflow test cases building. Note that the file is not usable as-is : you need to modify 
it to match your own file structure.

In this file, you will see that each link must be provided as an absolute path. The information you need to provide for
each test case is the following:
- **testCaseName**: the name that will be given to the generated files (excluding the file extension).
- **networkPath**: path to the input network file (xiidm, cgmes or matpower).
- **lfParametersPath**: path to the JSON serialization of the loadflow parameters.
- **outputPath**: path to the output directory.

After running the creator, the testcase reference files will be located in the **outputPath** directory.
- A network file, in XIIDM format.
- A result file (JSON serialization).

## Security analysis test cases creation

To create new security analysis test cases, you will need to fill an input file like this one: [example-sa-input.json](example-sa-input.json). 
The structure is very similar to the one for the loadflow, except for:
- **saParametersPath**: path to the JSON serialization of the security analysis parameters.
- **contingenciesOutputPath**: path to the contingencies' directory.
- **stateMonitorsOutputPath**: path to the state monitors' directory.
- **contingenciesSuppliers**: the list of *ContingenciesSupplier* implementation you want to use for this test case.
- **sateMonitorsSuppliers**: the list of *StateMonitorsSupplier* implementation you want to use for this test case.

### Contingencies and StateMonitors suppliers

It is possible to use and/or create implementations of the interfaces [ContingenciesSupplier](../src/main/java/com/powsybl/integrationtest/creation/security/contingencies/ContingenciesSupplier.java) and [StateMonitorsSuppliers](../src/main/java/com/powsybl/integrationtest/creation/security/statemonitors/StateMonitorsSupplier.java).
These implementations are different strategies to create contingencies and state monitors.

There is some implementations already existing and ready to use:

Contingencies:
- [RandomContingenciesSupplier](../src/main/java/com/powsybl/integrationtest/creation/security/contingencies/RandomContingenciesSupplier.java)
- [LinesVoltageLevelContingenciesSupplier](../src/main/java/com/powsybl/integrationtest/creation/security/contingencies/ContingenciesSupplier.java)
- [PairOfLinesContingenciesSupplier](../src/main/java/com/powsybl/integrationtest/creation/security/contingencies/ContingenciesSupplier.java)

StateMonitors:
- [RandomStateMonitorsSupplier](../src/main/java/com/powsybl/integrationtest/creation/security/contingencies/ContingenciesSupplier.java)

## How to use an existing implementation

To use an already existing implementation of one of these interfaces, you need to provide the name of the chosen implementation
and its configuration (i.e. *ContingenciesSupplierParameters*, an inner class in [SATestcaseCreatorParameters](../src/main/java/com/powsybl/integrationtest/creation/security/SATestcaseCreatorParameters.java))

The configuration's parameters can be different from an implementation to another, here's how it works:
- **name**: implementation's name (e.g. "RandomContingenciesSupplier")
- **configuration**: set configuration's parameters values (e.g. "Generator": 100) 


## How to create a new implementation

Once you know how to use an existing implementation, you can create a new implementation that fits your needs. 

Let's say you want to create a new [ContingenciesSupplier](../src/main/java/com/powsybl/integrationtest/creation/security/contingencies/ContingenciesSupplier.java)
you will need to:
1. Create a new class `YourImplementationContingenciesSupplier.java`
2. Which `implements ContingenciesSupplier`
3. Add `@AutoService(ContingenciesSupplier.class)` to be able to choose this implementation to create new test cases
4. Implement the method `getContingencies(network, configuration)` which returns a `List<Contingency>`
   1. First in this method you can set your parameters with `configuration`
   2. Then you can use these parameters to implement the contingencies selection's strategy you want

