package demos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import demos.MDPModelGenerator.GridModel;
import parser.State;
import parser.VarList;
import parser.ast.Declaration;
import parser.ast.DeclarationBool;
import parser.ast.DeclarationInt;
import parser.ast.DeclarationType;
import parser.ast.Expression;
import parser.type.Type;
import parser.type.TypeBool;
import parser.type.TypeInt;
import prism.DefaultModelGenerator;
import prism.ModelGenerator;
import prism.ModelType;
import prism.Prism;
import prism.PrismDevNullLog;
import prism.PrismException;
import prism.PrismFileLog;
import prism.PrismLangException;
import prism.PrismLog;

public class MDPBDI {
    static double w = 2.718;
    final static int randomSampleVaueGen = 2;
    final static int numberofAgents = 2;
    static int numberofIntention = 0;

    public static HashMap<Integer, Integer> transitionsVar = new HashMap<Integer, Integer>();
    public static HashMap<String, Integer> transitions = new HashMap<String, Integer>();
    public static Map<Integer, Double> transitionProb1 = new HashMap<Integer, Double>();
    public static ArrayList<String> transitionValues = new ArrayList<String>();
    public static Map<String, Double> choiceSelecProb = new HashMap<String, Double>();
    public static ArrayList<String> variables = new ArrayList<String>();
    public static HashMap<Integer, Integer> ModelMaxGenLimit = new HashMap<Integer, Integer>();

    public static void main(String[] args) throws IOException {

        File directoryPath = new File("src\\demos\\Data\\AgentTranProb");
        File filesList[] = directoryPath.listFiles();
        numberofIntention = filesList.length;
        System.out.println("Number of Intnetion " + numberofIntention);
        for (int f = 0; f < filesList.length; f++) {
            HashMap<String, Integer> Actiontransitions = new HashMap<String, Integer>();
            HashMap<String, ArrayList<String>> AgentsActions = new HashMap<String, ArrayList<String>>();
            Map<String, Map<String, Map<String, String>>> agentActions = new HashMap<>();
            /****
             * For each intnetion id we need to build the MDP
             */
            System.out.println("Now for each intnetion id i need to build the mdp");
            System.out.println("Intention id " + filesList[f].getName());
            String currrentInten = filesList[f].getName();

            System.out.println("Read the actions for Intnetion id = " + filesList[f].getName());
            ReadIntention ri = new ReadIntention();
            agentActions = ri.getAgentActions(currrentInten);
            System.out.println("Agents Action " + agentActions);

            Transitions at = new Transitions();
            transitions = at.getActionsTransitions(currrentInten);
            System.out.println("Choice and it's transitions " + transitions);

            ActionOutcome acv = new ActionOutcome();
            transitionValues = acv.getExecutionValuesNew(currrentInten, transitions);
            System.out.println(" Value In each Transitions " + transitionValues);

            Set<String> variablesSet = new HashSet<String>();
            agentActions.values().forEach(
                    innerMap -> innerMap.values()
                            .forEach(subInnerMap -> subInnerMap.keySet().forEach(variablesSet::add)));
            System.out.println("variables " + variablesSet);

            Instant start = Instant.now();
            new MDPBDI().run();

            Instant end = Instant.now();
            Duration timeElapsed = Duration.between(start, end);
            System.out.println("Time taken: " + timeElapsed.toMillis() + "milliseconds");

        }

    }

    public void run() throws FileNotFoundException {

        try {
            // Create a log for PRISM output (hidden or stdout)
            PrismLog mainLog = new PrismFileLog("stdout");
            // Initialise PRISM engine
            Prism prism = new Prism(mainLog);
            prism.initialise();
            prism.setEngine(Prism.EXPLICIT);
            // Create a model generator to specify the model that PRISM should build
            MDPModel modelGen = new MDPModel();
            // Load the model generator into PRISM,
            // export the model to a dot file (which triggers its construction)
            prism.loadModelGenerator(modelGen);
            prism.exportTransToFile(true, Prism.EXPORT_DOT_STATES, new File("mdp.dot"));
            prism.exportTransToFile(true, Prism.EXPORT_PLAIN, new File("ExStates.tra"));
            // prism.exportTransToFile(true,Prism.EXPORT_ROWS, new
            // File("ExStateTransitionRowWise.tra"));
            prism.exportStatesToFile(Prism.EXPORT_PLAIN, new File("ExStates.sta"));
            prism.exportLabelsToFile(null, Prism.EXPORT_PLAIN, new File("ExStates.lab"));
            // Then do some model checking and print the result
            String[] props = new String[] {
                    "P>=0.7[p1=true U<=5 q1=flase]",

            };
            for (String prop : props) {
                System.out.println(prop + ":");
                System.out.println(prism.modelCheck(prop).getResult());
            }

            // Close down PRISM
            prism.closeDown();

        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (PrismException e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    class MDPModel implements ModelGenerator {

        private int maxPPE;
        private int maxPollution;
        private State exploreState;

        HashMap<String, Integer> valueOfVariable = new HashMap<String, Integer>();
        HashMap<String, Integer> MaxValueOfvariables = new HashMap<String, Integer>();

        /***
         * 
         * Extract Number of mechanisms/ actions are in our STS specification
         * 
         * @throws IOException
         * 
         */

        public MDPModel() {
        }

        @Override
        public ModelType getModelType() {
            System.out.println("  getModelType() ");
            return ModelType.MDP;
        }

        @Override
        public List<String> getVarNames() {
            System.out.println(" getVarNames() ");
            return variables;

        }

        @Override
        public List<Type> getVarTypes() {
            System.out.println("getVarTypes");
            return Arrays.asList(TypeInt.getInstance(), TypeInt.getInstance());
        }

        @Override
        public State getInitialState() throws PrismException {
            System.out.println(" getInitialState ");
            return new State(variables.size()).setValue(0, 1).setValue(1, 0);
        }

        @Override
        public DeclarationType getVarDeclarationType(int i) throws PrismException {
            System.out.println("getVarDeclarationType " + i);
            System.out.println("ModelMaxGenLimit " + ModelMaxGenLimit.get(i));

            Type type = getVarType(i);
            MaxValueOfvariables.put(variables.get(i), ModelMaxGenLimit.get(i));
            System.out.println("MaxValueOfvariables " + MaxValueOfvariables);
            return new DeclarationInt(Expression.Int(0), Expression.Int(ModelMaxGenLimit.get(i)));

        }

        @Override
        public List<String> getLabelNames() {
            return Arrays.asList("target");

            // return variables;
        }

        @Override
        public void exploreState(State exploreState) throws PrismException {
            // Store the state (for reference, and because will clone/copy it later)
            System.out.println();
            System.out.println("=============== exploreState================== " + exploreState);
            this.exploreState = exploreState;
            for (int s = 0; s < variables.size(); s++) {
                valueOfVariable.put(variables.get(s), ((Integer) exploreState.varValues[s]).intValue());

            }

        }

        @Override
        public int getNumChoices() throws PrismException {
            System.out.println("getNumChoices()");
            // int totalActions = actions.size();
            // System.out.println("getNumChoices() = Number of Branches "+totalActions);
            // return totalActions;
            return 1; // Currently we are considering one conceptual action which contains our all
                      // choices.

        }

        @Override
        public int getNumTransitions(int i) throws PrismException {
            System.out.println("Mechanism Selection Probability");
            choiceSelecProb.clear();
            // choiceSelecProb = getSelectionProbability1(i);
            System.out.println("selectionProb " + choiceSelecProb);
            // ArrayList<Double> tranP = getCombinedMechExecutionProb(i, choiceSelecProb);
            // System.out.println("tranP "+tranP );
            transitionProb1.clear();
            // transitionProb1 = getFinalTransitionProb(tranP);
            double sumCheck = transitionProb1.values().stream()
                    .mapToDouble(a -> a)
                    .sum();

            // System.out.println("transitionProb " +transitionProb1);
            System.out.println("Sum of All Tranistions " + sumCheck);
            // System.out.println("transitionValues "+transitionValues);

            // return transitions.get(actions.get(i));
            //
            if (!(transitionProb1.size() == transitionValues.size())) {
                System.out.println("There number of transition prob is not equal to number of value/consequence");
            }
            return transitionProb1.size();

        }

        @Override
        /**
         * Get the action label of a transition within a choice, specified by its
         * index/offset.
         * The label can be any Object, but will often be treated as a string, so it
         * should at least
         * have a meaningful toString() method implemented. Absence of an action label
         * is denoted by null.
         * Note: For most types of models, the action label will be the same for all
         * transitions within
         * the same nondeterministic choice (i.e. for each different value of
         * {@code offset}),
         * but for Markov chains this may not necessarily be the case.
         * 
         * @param i      Index of the nondeterministic choice
         * @param offset Index of the transition within the choice
         */

        public Object getTransitionAction(int i, int offset) throws PrismException {
            System.out.println("Transit action " + actions.get(i));
            System.out.println("------------------------------");
            return actions.get(i);

        }

        @Override
        public Double getTransitionProbability(int i, int offset) throws PrismException {
            double Proba = (transitionProb1.get(offset));
            System.out.println("Transition = " + offset + ", Probability = " + Proba);
            return Proba;

        }

        @Override
        /**
         * Get the target (as a new State object) of a transition within a choice,
         * specified by its index/offset.
         * 
         * @param i      Index of the nondeterministic choice
         * @param offset Index of the transition within the choice
         */
        public State computeTransitionTarget(int i, int offset) throws PrismException {
            System.out.println("Compute Transition Target offset " + offset);
            State target = new State(exploreState);
            String[] varValue = null;
            // System.out.println("valueOfVariable "+ valueOfVariable);
            if ((valueOfVariable.get(variables.get(0)) <= maxPPE)
                    && (valueOfVariable.get(variables.get(1)) <= maxPollution)) {
                String tranValue = transitionValues.get(offset);
                varValue = tranValue.split(",", variables.size());
                // System.out.println("current state "+ target);
                String currentTarget = target.toString().replaceAll("\\(", "").replaceAll("\\)", "");
                String[] parts = currentTarget.split(",", variables.size());
                for (int p = 0; p < parts.length; p++) {
                    // System.out.println("valueOfVariable.get(p)
                    // "+(valueOfVariable.get(variables.get(p))));
                    int newTotalValue = valueOfVariable.get(variables.get(p)) + Integer.parseInt(varValue[p]);
                    if (newTotalValue < 0) {
                        newTotalValue = 0; // Avoid - valued for pollution like 0-1 = -1
                    }
                    // System.out.println( "valueOfVariable.get(p)+combinedVarValue.get(p) "+
                    // newTotalValue);
                    target.setValue(p, newTotalValue);
                }
                // System.out.println("new state "+ target);
                return target;

            }

            return target;
        }

        @Override
        public boolean isLabelTrue(int i) throws PrismException {

            switch (i) {
                // achievement // maintain
                case 0:
                    System.out.println("isLabelTrue " + variables.get(0) + " " + valueOfVariable.get(variables.get(0)));
                    return valueOfVariable.get(variables.get(0)) > 200;
                case 1:
                    System.out.println("isLabelTrue " + variables.get(0) + " " + valueOfVariable.get(variables.get(0)));
                    return valueOfVariable.get(variables.get(1)) < 100;

                default:
                    throw new PrismException("Label number \"" + i + "\" not defined");
            }
        }

    }
}
