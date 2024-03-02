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
    public static HashMap<Integer, ArrayList<String>> AgenttransitionValueG = new HashMap<Integer, ArrayList<String>>();
    public static HashMap<Integer, ArrayList<Double>> SelectionTransitionProb = new HashMap<Integer, ArrayList<Double>>();
    public static HashMap<String, ArrayList<Double>> transitionProbG = new HashMap<String, ArrayList<Double>>();
    public static Map<String, Set<String>> variablesG = new HashMap();
    public static ArrayList<String> AgentsNameG = new ArrayList<String>();
    public static HashMap<Integer, ArrayList<String>> AgentsActionsG = new HashMap<Integer, ArrayList<String>>();
    public static HashMap<String, ArrayList<String>> transitionValueG = new HashMap<String, ArrayList<String>>();
    static int numberofAgents = 0;

    public static void main(String[] args) throws IOException {

        File directoryPath = new File("src\\demos\\Data\\AgentTranProb");
        File filesList[] = directoryPath.listFiles();
        Integer numberofIntention = filesList.length;
        System.out.println("Number of Intnetion " + numberofIntention);
        for (int f = 0; f < filesList.length; f++) {
            Map<String, Set<String>> variablesSet = new HashMap<>();
            ArrayList<String> AgentsName = new ArrayList<String>();
            Map<String, ArrayList<String>> agentActions = new HashMap<>();
            HashMap<Integer, ArrayList<String>> AgentsActions = new HashMap<Integer, ArrayList<String>>();
            HashMap<String, ArrayList<String>> transitionValue = new HashMap<String, ArrayList<String>>();
            HashMap<String, ArrayList<Double>> transitionProb = new HashMap<String, ArrayList<Double>>();
            HashMap<Integer, ArrayList<String>> AgenttransitionValue = new HashMap<Integer, ArrayList<String>>();
            Map<String, Set<String>> variables = new HashMap<>();
            HashMap<String, Integer> ActionOutcomes = new HashMap<String, Integer>();

            /****
             * For each intnetion id we need to build the MDP
             */
            // System.out.println("Now for each intnetion id i need to build the mdp");
            System.out.println("************* Intention id ************ " + filesList[f].getName());
            String currrentInten = filesList[f].getName();
            // System.out.println("Read the actions for Intnetion id = " +
            // filesList[f].getName());
            ReadIntention ri = new ReadIntention();
            agentActions = ri.getAgentActions(currrentInten);
            // System.out.println("Agents Action " + agentActions);
            variablesSet = ri.getvaribles(currrentInten);
            // System.out.println(" Varibles " + variablesSet);
            variables.putAll(variablesSet);
            AgentsName.addAll(agentActions.keySet());
            // System.out.println(" AgentsName " + AgentsName);
            numberofAgents = AgentsName.size();

            Transitions at = new Transitions();
            ActionOutcomes = at.getActionsTransitions(currrentInten);
            // System.out.println("Action Number of consequences " + ActionOutcomes);

            // ActionOutcome acv = new ActionOutcome();
            // transitionValues = acv.getExecutionValuesNew(currrentInten, transitions);
            // System.out.println(" Value In each Transitions " + transitionValues);

            // ActionOutcome acp = new ActionOutcome();
            // transitionProb = acp.getExecutionProb(currrentInten, transitions);
            // System.out.println(" Prob In each Transitions " + transitionProb);

            // if (!variables.isEmpty()) {
            // variables.clear();
            // }

            // variables.addAll(variablesSet);

            // System.out.println("Varibles " + variables);

            List<String> agents = new ArrayList<>(agentActions.keySet());

            for (int i = 0; i < agents.size(); i++) {
                AgentsActions.put(i + 1, agentActions.get(agents.get(i)));
            }

            for (int a = 0; a < AgentsActions.size(); a++) {
                ArrayList<String> Agentmechvalue = new ArrayList<String>();
                for (int m = 0; m < AgentsActions.get(a + 1).size(); m++) {
                    String action = AgentsActions.get(a + 1).get(m);
                    Path path = Paths.get("src\\demos\\Data\\AgentTranProb\\" + currrentInten + "\\" + action);
                    try {
                        ArrayList<String> totalTransitions = (ArrayList<String>) Files.readAllLines(path,
                                StandardCharsets.UTF_8);
                        ArrayList<Double> mechprob = new ArrayList<Double>();
                        ArrayList<String> mechvalue = new ArrayList<String>();
                        for (int t = 0; t < totalTransitions.size(); t++) {
                            String tranValue = totalTransitions.get(t);
                            String[] eachValue = tranValue.split(":", 2);
                            String Values = eachValue[0].trim();
                            String prob = eachValue[1].trim();
                            mechvalue.add(Values);
                            Agentmechvalue.add(Values);
                            mechprob.add(Double.parseDouble(prob));
                        }
                        transitionValue.put(action, mechvalue);
                        transitionProb.put(action, mechprob);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
                AgenttransitionValue.put(a + 1, Agentmechvalue);
            }

            // System.out.println("numberofAgents" + numberofAgents);
            // // System.out.println("Agents =" + agents);
            // System.out.println("AgentActions mapping " + AgentsActions);
            // System.out.println("transitionValue " + transitionValue);
            // System.out.println("transitionVProb " + transitionProb);
            // System.out.println("Agent transitionValue " + AgenttransitionValue);
            // variables.add(0, "agent");
            // System.out.println("variables " + variables);

            if (!variablesG.isEmpty()) {
                variablesG.clear();
            }

            variablesG.putAll(variables);

            if (!AgentsNameG.isEmpty()) {
                AgentsNameG.clear();
            }

            AgentsNameG.addAll(AgentsName);

            if (!AgentsActionsG.isEmpty()) {
                AgentsActionsG.clear();
            }

            AgentsActionsG.putAll(AgentsActions);

            if (!transitionProbG.isEmpty()) {
                transitionProbG.clear();
            }

            transitionProbG.putAll(transitionProb);

            if (!AgenttransitionValueG.isEmpty()) {
                AgenttransitionValueG.clear();
            }

            AgenttransitionValueG.putAll(AgenttransitionValue);

            if (!transitionValueG.isEmpty()) {
                transitionValueG.clear();
            }

            transitionValueG.putAll(transitionValue);

            System.out.println("numberofAgents = " + numberofAgents);
            System.out.println("AgentsNameG " + AgentsNameG);
            System.out.println("AgentsActionsG " + AgentsActionsG);
            System.out.println("variablesG " + variablesG);
            System.out.println("transitionValueG " + transitionValueG);
            System.out.println("transitionProbG " + transitionProbG);
            System.out.println("AgenttransitionValueG " + AgenttransitionValueG);

            // Instant start = Instant.now();
            // new MDPBDI().run();

            // Instant end = Instant.now();
            // Duration timeElapsed = Duration.between(start, end);
            // System.out.println("Time taken: " + timeElapsed.toMillis() + "milliseconds");

            System.out.println("=================  END  ====================");

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
                    "Pmax=?[F \"goal1\"]"

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
        private State exploreState;
        HashMap<String, Boolean> valueOfVariable = new HashMap<String, Boolean>();
        // HashMap<String, Object> valueOfVariable = new HashMap<>();

        public MDPModel() {
        }

        @Override
        public ModelType getModelType() {
            System.out.println("getModelType = " + ModelType.MDP);
            return ModelType.MDP;
        }

        @Override
        public List<String> getVarNames() {
            System.out.println(" getVarNames = " + variablesG);
            return variablesG;

        }

        @Override
        public List<Type> getVarTypes() {
            List<Type> resultList = new ArrayList<>();
            for (int i = 0; i < variablesG.size(); i++) {
                resultList.add(TypeBool.getInstance());
            }
            // return Arrays.asList(TypeInt.getInstance(), TypeBool.getInstance(),
            // TypeBool.getInstance());
            System.out.println("getVarTypes = " + resultList);
            return resultList;
        }

        @Override
        public State getInitialState() throws PrismException {
            System.out.println(" getInitialState ");
            State initialState = new State(variablesG.size());
            initialState = initialState.setValue(0, 0);
            for (int i = 1; i < variablesG.size(); i++) {
                initialState = initialState.setValue(i, false);
            }

            System.out.println("Initial State " + initialState);
            return initialState;
        }

        @Override
        public DeclarationType getVarDeclarationType(int i) throws PrismException {
            System.out.println("getVarDeclarationType " + i);
            Type type = getVarType(i);
            // if (i == 0) {
            // return new DeclarationInt(Expression.Int(1), Expression.Int(2));
            // } else {
            // return new DeclarationBool();
            // }

            return new DeclarationBool();
        }

        // There is just one label: "goal"

        @Override
        public List<String> getLabelNames() {
            System.out.println("getLabelNames");
            return Arrays.asList("achivement", "maintain");
        }

        @Override
        public void exploreState(State exploreState) throws PrismException {
            // Store the state (for reference, and because will clone/copy it later)
            System.out.println();
            System.out.println("=============== exploreState================== " + exploreState);
            this.exploreState = exploreState;
            // valueOfVariable.put(variablesG.get(0), ((Integer)
            // exploreState.varValues[0]).intValue());
            for (int s = 0; s < variablesG.size(); s++) {
                valueOfVariable.put(variablesG.get(s), ((Boolean) exploreState.varValues[s]).booleanValue());

            }

        }

        @Override
        public int getNumChoices() throws PrismException {
            System.out.println("getNumChoices " + 1);
            // From sequence of actions like a1,a2 then at a time a1 is alivalble
            return 1;

        }

        @Override
        public int getNumTransitions(int i) throws PrismException {
            System.out.println("getNumTransitions and value of i = " + i);
            int transitions = 0;
            int agent = (int) valueOfVariable.get(variablesG.get(0));
            // System.out.println("agent = " + agent + " " + AgentsNameG.get(agent - 1));
            ArrayList<Double> SelectionExecutionprob = new ArrayList<Double>();
            for (int a = 0; a < AgentsActionsG.get(agent).size(); a++) {
                String action = AgentsActionsG.get(agent).get(a);
                System.out.println("action " + action);
                transitions = transitions + transitionProbG.get(action).size();
                for (int t = 0; t < transitionProbG.get(action).size(); t++) {
                    double prob = transitionProbG.get(action).get(t);
                    System.out.println("execution " + prob);
                    prob = Double.parseDouble(new DecimalFormat("##.###").format(prob));
                    System.out.println("execution  " + prob);
                    SelectionExecutionprob.add(prob);
                }

                SelectionTransitionProb.put(agent, SelectionExecutionprob);

            }

            System.out.println("number of transitions = " + transitions);
            return transitions;
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
            int agent = (int) valueOfVariable.get(variablesG.get(0));
            String action = AgentsNameG.get(agent - 1);
            System.out.println("Transit Action agent = " + agent + " Agent Name " + action);
            return action;

        }

        @Override
        public Double getTransitionProbability(int i, int offset) throws PrismException {
            int agent = (int) valueOfVariable.get(variablesG.get(0));
            System.out.println(" agent " + agent);
            double prob = SelectionTransitionProb.get(agent).get(offset);
            System.out.println("Offset" + offset + "Transit Probability " + prob);
            return prob;

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
            System.out.println("Compute Transition Target i and offset " + i);
            System.out.println("Compute Transition Target offset " + offset);
            System.out.println("valueOfVariable " + valueOfVariable);
            State target = new State(exploreState);
            int agent = (int) valueOfVariable.get(variablesG.get(0));
            String tranValue = AgenttransitionValueG.get(agent).get(offset);
            System.out.println("tranValue " + tranValue);
            String[] varValue = tranValue.split(",", (variablesG.size() - 1));
            System.out.println("var value " + Arrays.toString(varValue));
            System.out.println("current state " + target);
            String currentTarget = target.toString().replaceAll("\\(",
                    "").replaceAll("\\)", "");
            String[] parts = currentTarget.split(",", (variablesG.size() - 1));
            System.out.println(variablesG.size());
            Boolean newvalue = false;
            for (int v = 0; v < varValue.length; v++) {
                newvalue = Boolean.parseBoolean(varValue[v]);
            }
            if ((agent == numberofAgents)) {
                System.out.println("agent value change to 1");
                target.setValue(0, 1);
            } else {
                System.out.println("Agent avlue change to 2");
                target.setValue(0, agent + 1);
            }

            for (int p = 0; p < parts.length; p++) {
                System.out.println("valueOfVariable.get(p)" + (valueOfVariable.get(variablesG.get(p))));
                Boolean newTotalValue = Boolean.parseBoolean(varValue[p]);
                target.setValue(p, newTotalValue);
            }
            System.out.println("new state " + target);
            return target;

        }

        @Override
        public boolean isLabelTrue(int i) throws PrismException {
            switch (i) {
                // achievement
                case 0:

                    return (int) valueOfVariable.get(variablesG.get(1)) > 1;
                // maintain
                case 1:

                    return (int) valueOfVariable.get(variablesG.get(1)) < 2;

                default:
                    throw new PrismException("Label number \"" + i + "\" not defined");
            }
        }

    }
}
