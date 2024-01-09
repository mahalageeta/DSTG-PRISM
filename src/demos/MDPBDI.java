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
    public static HashMap<String, ArrayList<String>> AgentsActions = new HashMap<String, ArrayList<String>>();
    public static HashMap<Integer, Integer> transitionsVar = new HashMap<Integer, Integer>(); // key = transition or
                                                                                              // offset and value =
                                                                                              // varible index
    public static HashMap<String, Integer> transitions = new HashMap<String, Integer>(); // key = transition or offset
                                                                                         // and value = varible index
    public static Map<String, Map<String, String>> norm = new HashMap<String, Map<String, String>>(); // norm id such as
                                                                                                      // C3, P1 and
                                                                                                      // value is
                                                                                                      // another map
                                                                                                      // which has key =
                                                                                                      // variable name
                                                                                                      // and value =
                                                                                                      // variable value
    public static Map<String, Integer> AgentNorms = new HashMap<String, Integer>(); // key norm id and value = agent
                                                                                    // such as key P1 and Value = 1
                                                                                    // (Agent1)
    public static Map<String, String> normPreconditions = new HashMap<String, String>();
    public static Map<Integer, Double> transitionProb1 = new HashMap<Integer, Double>();
    public static ArrayList<String> transitionValues = new ArrayList<String>();
    public static Map<String, Double> choiceSelecProb = new HashMap<String, Double>();
    public static ArrayList<String> variables = new ArrayList<String>();
    public static HashMap<Integer, Integer> ModelMaxGenLimit = new HashMap<Integer, Integer>(); // key = variable index
                                                                                                // and value = maximum
                                                                                                // value

    public static void main(String[] args) throws IOException {
        /***
         * 
         * Before start the experiments we want delete all the data
         * 
         * 
         * 
         ***/
        // File file = new File("D:\\prism-master\\data");
        // deletePreInput(file);
        // File file1 = new File("D:\\prism-master\\StateTranProb\\AgentMechTranProb");
        // deletePreInput(file1);
        // File file2 = new File("D:\\prism-master\\StateTranProb\\ComMechTranProb");
        // deletePreInput(file2);
        // System.out.println("Previous Data Deleted");
        // run4Input ri = new run4Input();
        // ri.getInputsForPrism(randomSampleVaueGen,numberofAgents);
        // System.out.println("New Data Generated");

        ReadIntention ri = new ReadIntention();
        AgentsActions = ri.getAgentActions();
        System.out.println("Agents Action " + AgentsActions);

        Transitions at = new Transitions();
        transitions = at.getActionsTransitions();
        System.out.println("Choice and it's transitions " + transitions);

        ActionOutcome acv = new ActionOutcome();
        transitionValues = acv.getActiionTransitionValue(transitions);
        System.out.println(" Value In each Transitions " + transitionValues);

        Set<String> variablesSet = new HashSet<String>();
        Varibles var = new Varibles();
        variablesSet = var.getVaribles(numberofAgents);
        variables.addAll(variablesSet);
        System.out.println("variables " + variables);

        AssignModelMaxGenValues amgv = new AssignModelMaxGenValues();
        ModelMaxGenLimit = amgv.giveVarDeclarationType();
        System.out.println("ModelMaxGenLimit " + ModelMaxGenLimit);

        Instant start = Instant.now();
        new MDPBDI().run();

        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: " + timeElapsed.toMillis() + " milliseconds");
    }

    private static void deletePreInput(File file) {

        String[] myFiles;
        if (file.isDirectory()) {
            myFiles = file.list();
            for (int i = 0; i < myFiles.length; i++) {
                File myFile = new File(file, myFiles[i]);
                myFile.delete();
            }

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
            // maxPPE =30 and maxPollution =20

            MDPModel modelGen = new MDPModel(500, 200);

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
                    "P>=0.7[pollution<=60 U<=5 PPE>=215]",

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

        public MDPModel(int maxPPE, int maxPollution) {
            this.maxPPE = maxPPE;
            this.maxPollution = maxPollution;

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
            choiceSelecProb = getSelectionProbability1(i);
            System.out.println("selectionProb " + choiceSelecProb);
            ArrayList<Double> tranP = getCombinedMechExecutionProb(i, choiceSelecProb);
            // System.out.println("tranP "+tranP );
            transitionProb1.clear();
            transitionProb1 = getFinalTransitionProb(tranP);
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
            // System.out.println("Transit action " +actions.get(i) );
            System.out.println("------------------------------");
            // return actions.get(i);
            return "ConceptualProbAction";

        }

        @Override
        public double getTransitionProbability(int i, int offset) throws PrismException {
            double Proba = (transitionProb1.get(offset));
            System.out.println("Transition = " + offset + ", Probability = " + Proba);
            return Proba;

        }

        private Map<Integer, Double> getFinalTransitionProb(ArrayList<Double> tranP) {

            Map<Integer, Double> transitionProb = new HashMap<Integer, Double>();

            double sum = tranP.stream()
                    .mapToDouble(a -> a)
                    .sum();

            sum = Double.parseDouble(new DecimalFormat("##.###").format(sum));
            // System.out.println(" trap sum "+ sum);

            for (int t = 0; t < tranP.size(); t++) {
                double prob = tranP.get(t) / sum;
                prob = Double.parseDouble(new DecimalFormat("##.###").format(prob));
                // System.out.println( "check "+prob);
                transitionProb.put(t, prob);
            }

            double sum1 = transitionProb.values().stream().collect(Collectors.summingDouble(Double::doubleValue));
            sum1 = Double.parseDouble(new DecimalFormat("##.###").format(sum1));
            // System.out.println(sum1);

            if (sum1 > 1.0) {
                double max = Integer.MIN_VALUE;
                Integer name = null;
                // System.out.println("not 1 " +sum1);
                double diffsum = (sum1 - 1.0);
                diffsum = Double.parseDouble(new DecimalFormat("##.###").format(diffsum));
                ;
                // System.out.println("diference " +diffsum);
                Set<Entry<Integer, Double>> entries = transitionProb.entrySet();
                for (Entry<Integer, Double> entry : entries) {
                    if (entry.getValue() > max) {
                        max = entry.getValue();
                        name = entry.getKey();
                    }
                }
                // System.out.println("Entry with the highest value: "+name);
                double newvalue = Double
                        .parseDouble(new DecimalFormat("##.###").format(transitionProb.get(name) - diffsum));
                transitionProb.put(name, newvalue);
            } else if (sum1 < 1.0) {
                double min = Integer.MAX_VALUE;
                Integer name = null;
                // System.out.println("not 1 " +sum1);
                double diffsum = (1.0 - sum1);
                diffsum = Double.parseDouble(new DecimalFormat("##.###").format(diffsum));
                ;
                // System.out.println("diference " +diffsum);
                Set<Entry<Integer, Double>> entries = transitionProb.entrySet();
                for (Entry<Integer, Double> entry : entries) {
                    if (entry.getValue() < min) {
                        min = entry.getValue();
                        name = entry.getKey();
                    }
                }
                // System.out.println("Entry with the highest value: "+name);
                double newvalue = Double
                        .parseDouble(new DecimalFormat("##.###").format(transitionProb.get(name) + diffsum));
                transitionProb.put(name, newvalue);
            }

            return transitionProb;
        }

        private ArrayList<Double> getCombinedMechExecutionProb(int i, Map<String, Double> choiceSelecProb) {

            ReadingStateTranProb rstp = new ReadingStateTranProb();

            ArrayList<Double> tranP = new ArrayList<Double>();
            List<String> keys = new ArrayList<>(choiceSelecProb.keySet());
            // System.out.println("keys "+keys);

            for (int k = 0; k < keys.size(); k++) {
                String choice = keys.get(k);
                // System.out.println("choice "+ choice);
                for (int t = 0; t < transitions.get(choice); t++) {
                    try {
                        double MechConProb = Double.parseDouble(rstp.getTransitionProb(t, choice));

                        // tranProb = Double.parseDouble(new DecimalFormat("##.###").format(tranProb));
                        // System.out.println("MechConProb " + MechConProb);

                        double tranProb = MechConProb * choiceSelecProb.get(choice);
                        /// System.out.println("tranProb " + tranProb);
                        tranP.add(Double.parseDouble(new DecimalFormat("##.###").format(tranProb)));

                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }

            }

            // System.out.println("tranP "+tranP);

            return tranP;
        }

        private Map<String, Double> getSelectionProbability1(int i) {

            Map<String, Double> mechSelectionProb = new HashMap<String, Double>();
            for (int a = 0; a < AgentsMechanism.size(); a++) {
                // System.out.println("AgentsMechanism" + AgentsMechanism.get(a+1));
                Map<String, Map<String, String>> Anorm = new HashMap<String, Map<String, String>>();
                Map<String, String> AnormPreconditions = new HashMap<String, String>();
                Map<Integer, ArrayList<String>> reverseMap = new HashMap<>(
                        AgentNorms.entrySet().stream()
                                .collect(Collectors.groupingBy(Map.Entry::getValue)).values().stream()
                                .collect(Collectors.toMap(
                                        item -> item.get(0).getValue(),
                                        item -> new ArrayList<>(
                                                item.stream()
                                                        .map(Map.Entry::getKey)
                                                        .collect(Collectors.toList())))));

                for (int v = 0; v < reverseMap.get(a + 1).size(); v++) {
                    Anorm.put(reverseMap.get(a + 1).get(v), norm.get(reverseMap.get(a + 1).get(v)));
                    AnormPreconditions.put(reverseMap.get(a + 1).get(v),
                            normPreconditions.get(reverseMap.get(a + 1).get(v)));
                }
                System.out.println("Anorm " + Anorm);
                System.out.println("AnormPreconditions " + AnormPreconditions);
                Map<String, Double> finalProbMech = getMechLiklihoodValue(Anorm, AnormPreconditions, a);

                // System.out.println("Selection Probabilities ="+finalProbMech);
                mechSelectionProb.putAll(finalProbMech);

            }
            // System.out.println("Mechanism Selection Probabilities "+mechSelectionProb);

            ArrayList<String> keys = new ArrayList<>(transitions.keySet());
            // System.out.println("check keys "+keys);

            for (int an = 0; an < keys.size(); an++) {
                double selectionProb = 1.0;

                String[] mechanisms = keys.get(an).split("&");
                // System.out.println("Mechanism "+ mechanisms);
                // System.out.println("Mechanism "+ mechanisms.length);
                for (int m = 0; m < mechanisms.length; m++) {
                    if (mechSelectionProb.containsKey(mechanisms[m])) {
                        // System.out.println(" mechanism "+mechanisms[m]);
                        // System.out.println(" selecion prob for mechanism
                        // "+mechSelectionProb.get(mechanisms[m]));
                        selectionProb = selectionProb * mechSelectionProb.get(mechanisms[m]);
                        // System.out.println("in the selecion selection prob "+selectionProb);
                        selectionProb = Double.parseDouble(new DecimalFormat("#.####").format(selectionProb));
                        // selectionProb = Double.parseDouble(selectionProb);
                        // System.out.println(" selectionProb "+selectionProb);

                    }
                }

                choiceSelecProb.put(keys.get(an), selectionProb);

            }
            System.out.println("choiceSelecProb " + choiceSelecProb);
            return choiceSelecProb;
        }
        // private double getSelectionProbability(int i) {
        // for(int a=0; a<actions.size();a++) {
        // System.out.println("action "+ actions.get(i));
        // }
        // String[] mechanisms =actions.get(i).split("&");
        //
        // // reverseMap {1=[P1, C1], 2=[P2, C2], 3=[C3, P3]}
        // Map<Integer, ArrayList<String>> reverseMap = new HashMap<>(
        // AgentNorms.entrySet().stream()
        // .collect(Collectors.groupingBy(Map.Entry::getValue)).values().stream()
        // .collect(Collectors.toMap(
        // item -> item.get(0).getValue(),
        // item -> new ArrayList<>(
        // item.stream()
        // .map(Map.Entry::getKey)
        // .collect(Collectors.toList())
        // ))
        // ));
        // double selectionProb =1.0;
        // for(int a=0; a<AgentsMechanism.size();a++) {
        // System.out.println("AgentsMechanism" + AgentsMechanism.get(a+1));
        // Map<String, Map<String, String>> Anorm = new HashMap<String, Map<String,
        // String>>();
        // Map<String, String> AnormPreconditions = new HashMap<String,String>();
        // // System.out.println("norm "+ norm);
        // // System.out.println("normPreconditions "+normPreconditions);
        // // System.out.println(" reverseMap "+ reverseMap);
        //
        //
        // for(int v=0; v<reverseMap.get(a+1).size();v++) {
        // Anorm.put(reverseMap.get(a+1).get(v), norm.get(reverseMap.get(a+1).get(v)));
        // AnormPreconditions.put(reverseMap.get(a+1).get(v),normPreconditions.get(reverseMap.get(a+1).get(v)));
        // }
        // System.out.println("Anorm "+Anorm);
        // System.out.println("AnormPreconditions "+AnormPreconditions);
        // Map<String, Double> finalProbMech =
        // getMechLiklihoodValue(Anorm,AnormPreconditions,a);
        // // System.out.println("Selection Probabilities ="+finalProbMech);
        // for(int m=0;m<mechanisms.length;m++) {
        // if(finalProbMech.containsKey(mechanisms[m])) {
        // //System.out.println(" mechanism "+mechanisms[m]);
        // //System.out.println(" selecion prob for
        // mechanism"+finalProbMech.get(mechanisms[m]));
        // selectionProb = selectionProb*finalProbMech.get(mechanisms[m]);
        // //System.out.println("in the selecion selection prob "+selectionProb);
        // selectionProb = Double.parseDouble(new
        // DecimalFormat("##.###").format(selectionProb));
        //
        //
        // }
        // }
        // }
        // System.out.println("final return ="+selectionProb);
        // return selectionProb;
        // }

        /*
         * The implementation of equation 1 in the paper
         * 
         */
        private Map<String, Double> getMechLiklihoodValue(Map<String, Map<String, String>> norm,
                Map<String, String> normPreconditions, int a) {
            Map<String, Map<String, Double>> wValueOfMech = new HashMap<String, Map<String, Double>>();
            Map<String, Double> finalProbMech = new HashMap<String, Double>();

            for (int m = 0; m < AgentsMechanism.get(a + 1).size(); m++) {
                // System.out.println("action " + AgentsMechanism.get(a+1).get(m));

                String mechanism = AgentsMechanism.get(a + 1).get(m);
                Map<String, Double> normCorrW = new HashMap<String, Double>();
                // System.out.println("mechanism " + mechanism);

                try {
                    File myMech = new File("D:\\prism-master\\data\\" + mechanism);
                    Scanner myMechReader = new Scanner(myMech);
                    while (myMechReader.hasNextLine()) {
                        Double minRange = 0.0;
                        Double maxRange = 0.0;
                        Double singleRange = 0.0;
                        String data = myMechReader.nextLine();
                        String[] label = data.split(":");
                        String varM = label[0].trim();
                        String rangeM = label[1].trim();
                        if (rangeM.contains("#")) {
                            String[] minMax = rangeM.split("#");
                            minRange = Double.parseDouble((minMax[0].trim()));
                            maxRange = Double.parseDouble(minMax[1].trim());

                        } else {

                            singleRange = Double.parseDouble(rangeM.trim());

                        }

                        for (Entry<String, Map<String, String>> n : norm.entrySet()) {
                            String normId = n.getKey(); // like P1 ,C1
                            String normPre = "";
                            String preVar = "";
                            Integer prevalue;
                            if (normPreconditions.get(normId).contains(">=")) {

                                String[] preExp = normPreconditions.get(normId).split(">=");
                                preVar = preExp[0].trim();
                                prevalue = Integer.parseInt(preExp[1].trim());
                            } else {
                                normPre = normPreconditions.get(normId);
                            }

                            String normType = normId.replaceAll("[^A-Z]", "");

                            /****
                             * 
                             * For Prohibition P
                             * 
                             */
                            if ((normType.equalsIgnoreCase("P"))) {

                                if (n.getValue().containsKey(varM)) {
                                    double prob = 0.0;
                                    String AtomName = varM;
                                    int nConsquentValue = Integer.parseInt(n.getValue().get(AtomName));
                                    // System.out.println(" P nConsquentValue "+ nConsquentValue);
                                    // System.out.println(" P valueOfVariable.get(varM) "+
                                    // valueOfVariable.get(varM));
                                    double currentTarget = nConsquentValue - valueOfVariable.get(varM);
                                    // System.out.println(" P currentTarget "+ currentTarget);
                                    // System.out.println("currentTarget "+ currentTarget);
                                    // System.out.println("Upper bound "+ maxRange);
                                    // System.out.println("Lower bound "+ minRange);

                                    if (maxRange <= currentTarget) {

                                        prob = 1.0;
                                        // System.out.println(" P return prob maxRange <= currentTarget "+ prob);

                                    } else if (minRange >= currentTarget) {

                                        prob = 0.0;
                                        // System.out.println(" P return prob minRange >= currentTarget "+ prob);

                                    } else {

                                        prob = Double.parseDouble(new DecimalFormat("##.###").format(
                                                (double) (currentTarget - minRange) / (maxRange - minRange)));
                                        // System.out.println(" P return prob else "+ prob);

                                    }

                                    double result = Double
                                            .parseDouble(new DecimalFormat("##.###").format(Math.pow(w, prob)));

                                    // System.out.println(" P return w power prob "+result);

                                    normCorrW.put(normId, result);
                                }

                            } else if (normType.equalsIgnoreCase("C")) {

                                if (n.getValue().containsKey(varM)) {
                                    if ((normPre.equalsIgnoreCase("true"))) {

                                        double prob = 0.0;
                                        String AtomName = varM;

                                        int nConsquentValue = Integer.parseInt(n.getValue().get(AtomName));
                                        // System.out.println(" C nConsquentValue "+ nConsquentValue);
                                        // System.out.println(" C valueOfVariable.get(varM) "+
                                        // valueOfVariable.get(varM));
                                        double currentTarget = nConsquentValue - valueOfVariable.get(varM);
                                        // System.out.println("currentTarget "+ currentTarget);
                                        // System.out.println("Upper bound "+ maxRange);
                                        // System.out.println("Lower bound "+ minRange);
                                        if (maxRange <= currentTarget) {
                                            prob = 0.0;
                                            // System.out.println(" C return prob maxRange <= currentTarget "+ prob);

                                        } else if (minRange >= currentTarget) {

                                            prob = 1.0;
                                            // System.out.println(" C return prob minRange >= currentTarget "+ prob);

                                        } else {

                                            prob = Double.parseDouble(new DecimalFormat("##.###").format(
                                                    (double) (maxRange - currentTarget) / (maxRange - minRange)));
                                            // System.out.println(" C return prob else "+ prob);

                                        }

                                        ;

                                        double result = Double
                                                .parseDouble(new DecimalFormat("##.###").format(Math.pow(w, prob)));

                                        // System.out.println(" C return w power prob "+result);

                                        normCorrW.put(normId, result);
                                    }
                                    // if((!(normPre.equalsIgnoreCase("true")))&&(valueOfVariable.get(variables.get(2))>=200))
                                    // { // C2 in revised MAS // make pollution>=200
                                    //
                                    // double prob = 0.0;
                                    // String AtomName = varM;
                                    // int nConsquentValue = Integer.parseInt(n.getValue().get(AtomName));
                                    // double currentTarget = nConsquentValue -
                                    // valueOfVariable.get(variables.get(2));
                                    //
                                    // if (maxRange <= currentTarget) {
                                    // prob = 0.0;
                                    //
                                    // } else if (minRange >= currentTarget) {
                                    //
                                    // prob = 1.0;
                                    //
                                    // } else {
                                    //
                                    // prob = Double.parseDouble(new DecimalFormat("##.##").format(
                                    // (double) (maxRange -currentTarget ) / (maxRange - minRange)));
                                    //
                                    // }
                                    //
                                    //
                                    //
                                    // double result = Double
                                    // .parseDouble(new DecimalFormat("##.#").format(Math.pow(w, prob)));
                                    //
                                    // normCorrW.put(normId, result);
                                    // }
                                }
                            }

                        }

                        // wValueOfMech.put((String) AgentsMechanism.get(i).get(a), normCorrW);
                        // System.out.println("normCorrW "+normCorrW);

                        wValueOfMech.put(mechanism, normCorrW);

                    }
                    myMechReader.close();
                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }

            }
            // System.out.println("wValueOfMech "+ wValueOfMech);
            for (Entry<String, Map<String, Double>> w : wValueOfMech.entrySet()) {
                // System.out.println("w.getKey() " +w.getKey());
                double finalMProb = 1.0;
                Set<String> normKeys = w.getValue().keySet();
                String[] normkeysArray = normKeys.toArray(new String[normKeys.size()]);
                // System.out.println("normkeysArray "+Arrays.toString(normkeysArray));

                for (int f = 0; f < normkeysArray.length; f++) {
                    // System.out.println(normkeysArray[f]);

                    double sumOfw = 0.0;
                    for (Entry<String, Map<String, Double>> w1 : wValueOfMech.entrySet()) {

                        sumOfw = sumOfw + w1.getValue().get(normkeysArray[f]);
                    }

                    // System.out.println("sumOfw "+sumOfw);
                    double nProb = w.getValue().get(normkeysArray[f]) / sumOfw;
                    // System.out.println("After Softfunction nProb "+nProb);
                    finalMProb = Double.parseDouble(new DecimalFormat("##.###").format(finalMProb * nProb));

                }

                finalProbMech.put(w.getKey(), finalMProb);

            }
            /***
             * Normalize the Probabilities
             * 
             **/

            Double sum = finalProbMech.values().stream().mapToDouble(d -> d).sum();
            sum = Double.parseDouble(new DecimalFormat("##.###").format(sum));
            // System.out.println(" sum "+ sum);

            Map<String, Double> finalProbMech1 = new HashMap<String, Double>();
            ArrayList<Double> prob = new ArrayList<Double>();
            for (Entry<String, Double> pair : finalProbMech.entrySet()) {
                prob.add(pair.getValue());
            }

            List keys = new ArrayList(finalProbMech.keySet());
            for (int t = 0; t < finalProbMech.keySet().size(); t++) {
                double probability = prob.get(t) / sum;
                probability = Double.parseDouble(new DecimalFormat("##.###").format(probability));
                finalProbMech1.put((String) keys.get(t), probability);
            }

            // System.out.println("getMechLiklihoodValue finalProbMech1 == " +
            // finalProbMech1);
            return finalProbMech1;
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

            // if((valueOfVariable.get(variables.get(0))<=maxPPE) &&
            // (valueOfVariable.get(variables.get(1))<= maxPollution)) {
            // try {
            //
            // varValue = rst.getTransitionValue(offset,actions.get(i));
            //
            // } catch(IOException e) {
            // e.printStackTrace();
            // }
            // //System.out.println("varValue "+ Arrays.toString(varValue));
            // System.out.println("current state "+ target);
            // String currentTarget = target.toString().replaceAll("\\(",
            // "").replaceAll("\\)","");
            //
            // String[] parts = currentTarget.split(",",variables.size()); // current value
            // of each variable
            // //System.out.println("parts "+ Arrays.toString(parts) +" "+parts.length);
            // for(int p=0; p<parts.length;p++) {
            // //System.out.println("combinedVarValue "+combinedVarValue.get(p));
            // //System.out.println("valueOfVariable.get(p)
            // "+(valueOfVariable.get(variables.get(p))));
            //
            //
            //
            //
            // int newTotalValue =
            // valueOfVariable.get(variables.get(p))+Integer.parseInt(varValue[p]);
            // if(newTotalValue<0) {
            // newTotalValue =0; // Avoid - valued for pollution like 0-1 = -1
            // }
            // //System.out.println( "valueOfVariable.get(p)+combinedVarValue.get(p) "+
            // newTotalValue);
            // target.setValue(p, newTotalValue );
            // }
            // System.out.println("new state "+ target);
            // return target;
            // }

            // Never happens
            // System.out.println("never happen "+ target);
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
