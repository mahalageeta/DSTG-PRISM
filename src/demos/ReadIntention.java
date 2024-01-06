package demos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

//import cern.colt.Arrays;
public class ReadIntention {
    static int numberofAgents = 0;
    static Set<String> variables = new HashSet<String>();
    public static HashMap<String, Integer> ActionOutcomes = new HashMap<String, Integer>();
    final static Map<String, ArrayList<Integer>> ActionVarValues = new HashMap<>(); // key = var name and values =
                                                                                    // values
    final static Map<String, ArrayList<String>> MechRange = new HashMap<>();
    public static HashMap<String, ArrayList<String>> AgentAction = new HashMap<String, ArrayList<String>>();
    static ArrayList<String> AgentsName = new ArrayList<String>();

    public static void getStateTranProbGeneration() throws FileNotFoundException, IOException {
        for (Map.Entry mapElement : ActionOutcomes.entrySet()) {
            String mechanism = (String) mapElement.getKey();
            int consequences = ((int) mapElement.getValue());
            calculateStateTransitionProbability_New(mechanism, consequences);
        }

        // System.out.println(" MechVarOperator "+MechVarOperator);

    }

    public static ArrayList<String> getAgentNames() throws FileNotFoundException, IOException {

        return AgentsName;

    }

    public static Set<String> getVariables() throws FileNotFoundException, IOException {

        return variables;

    }

    public static int getNumberOfAgent() throws FileNotFoundException, IOException {

        return numberofAgents;

    }

    public static HashMap<String, ArrayList<String>> getAgentMechanisms() throws FileNotFoundException, IOException {

        return AgentAction;

    }

    public static HashMap<String, Integer> getMechNoOfConse() throws FileNotFoundException, IOException {

        return ActionOutcomes;

    }

    public static void dataGenerate(int scale) throws FileNotFoundException, IOException {
        // System.out.println("dataGenerate "+scale);
        // Creating a File object for directory
        File directoryPath = new File("D:\\prism-master\\AgentsMech");
        // List of all files and directories
        File filesList[] = directoryPath.listFiles();
        Scanner sc = null;

        for (File file : filesList) {
            ArrayList<String> setOfMech = new ArrayList<String>();
            sc = new Scanner(file);
            String input;
            while (sc.hasNextLine()) {
                input = sc.nextLine();
                // System.out.println(input);
                String[] label = input.split(":");
                String actionName = label[0].trim();
                setOfMech.add(actionName);

                String[] m = label[1].split(Pattern.quote("(").trim(), 2);
                String[] gaurd = m[1].split(Pattern.quote(",").trim(), 2);
                String[] mVar = gaurd[1].split(",(?![^\\(\\[]*[\\]\\)])");
                // System.out.println("Action Name "+actionName );
                FileWriter fw1 = new FileWriter("D:\\prism-master\\data\\" + actionName);
                for (int a = 0; a < mVar.length; a++) {
                    // System.out.println(mVar[a]);
                    if (mVar[a].contains("+=")) {
                        String[] varNameValue = mVar[a].split(Pattern.quote("+="));
                        if (varNameValue.length > 1) {
                            String varName = varNameValue[0].trim();
                            varName = varName.replaceAll("[{]", "");
                            String varRange = varNameValue[1].trim().replaceAll("[}\\[\\])]", "");
                            // System.out.println( varName );
                            fw1.write(varName + ":");
                            if (varRange.contains("*")) {
                                // System.out.println("depdent");
                                String[] depVar = varRange.split("\\*");
                                String independentVarName = depVar[0].trim();
                                String[] range = depVar[1].split(Pattern.quote(",").trim());
                                String min = range[0].trim();
                                String max = range[1].trim();
                                // System.out.println("MechRange "+ MechRange);
                                double finalLowerbound = (Double.parseDouble(min)
                                        * Double.parseDouble(MechRange.get(independentVarName).get(0)));
                                // System.out.println("finalLowerbound "+ Math.round(finalLowerbound));

                                double finalUpperbound = (Double.parseDouble(max)
                                        * Double.parseDouble(MechRange.get(independentVarName).get(1)));
                                // System.out.println("finalUpperbound "+ Math.round(finalUpperbound));
                                fw1.write(Math.round(finalLowerbound) + "#" + Math.round(finalUpperbound) + ":");
                                ArrayList<Double> randomValueGner = new ArrayList<Double>();
                                Set<Double> numbers = new HashSet<Double>();
                                if ((min.equalsIgnoreCase("0.0")) && (max.equalsIgnoreCase("0.0"))) { // null actions
                                    randomValueGner.add(0.0);
                                } else { // not null actions

                                    numbers = getRandomNumbersWithNoDuplicates(Double.parseDouble(min),
                                            Double.parseDouble(max),
                                            ActionOutcomes.get(actionName));
                                    // System.out.println("numbers "+numbers);
                                    randomValueGner.addAll(numbers);
                                }
                                for (int p = 0; p < randomValueGner.size(); p++) {
                                    // System.out.println(MechVarValues);
                                    double dependentVal = ActionVarValues.get(independentVarName).get(p)
                                            * randomValueGner.get(p);

                                    fw1.write(Math.round(
                                            Double.parseDouble(new DecimalFormat("##.##").format(dependentVal))) + ",");
                                }

                            } else {
                                // System.out.println("Indepdent ");
                                if (varRange.contains(",")) {
                                    ArrayList<String> lowerUpperBound = new ArrayList();
                                    String[] range = varRange.split(Pattern.quote(",").trim());
                                    String min = range[0].trim();
                                    String max = range[1].trim();
                                    lowerUpperBound.add(min);
                                    lowerUpperBound.add(max);
                                    MechRange.put(varName, lowerUpperBound);
                                    fw1.write(min + "#" + max + ":");
                                    ArrayList<Integer> randomValInteger = new ArrayList<Integer>();
                                    for (int i = Integer.parseInt(min); i <= Integer.parseInt(max); i += scale) {
                                        // System.out.println("value of i "+i);
                                        randomValInteger.add(i);
                                        fw1.write((int) i + ",");
                                    }
                                    ActionVarValues.put(varName, randomValInteger);
                                    fw1.write("\n");

                                }
                            }
                        }
                    }

                }
                fw1.close();
            }

        }
    }

    public static void getData(int scale) throws FileNotFoundException, IOException {
        System.out.println("get data");
        // Creating a File object for directory
        File directoryPath = new File("D:\\prism-master\\AgentsMech");
        // List of all files and directories
        File filesList[] = directoryPath.listFiles();
        Scanner sc = null;
        numberofAgents = filesList.length;
        for (File file : filesList) {
            ArrayList<String> setOfMech = new ArrayList<String>();
            // System.out.println("File name: "+file.getName());
            AgentsName.add(file.getName());
            // System.out.println("File path: "+file.getAbsolutePath());
            // Instantiating the Scanner class
            sc = new Scanner(file);
            String input;
            while (sc.hasNextLine()) {
                input = sc.nextLine();
                // System.out.println(input);
                String[] label = input.split(":");
                String actionName = label[0].trim();
                setOfMech.add(actionName);

                String[] m = label[1].split(Pattern.quote("(").trim(), 2);
                String[] gaurd = m[1].split(Pattern.quote(",").trim(), 2);
                String[] mVar = gaurd[1].split(",(?![^\\(\\[]*[\\]\\)])");
                for (int a = 0; a < mVar.length; a++) {
                    if (mVar[a].contains("+=")) {
                        String[] varNameValue = mVar[a].split(Pattern.quote("+="));
                        if (varNameValue.length > 1) {
                            String varName = varNameValue[0].trim();
                            varName = varName.replaceAll("[{]", "");
                            // System.out.println("varName "+varName);
                            if (!(variables.contains(varName))) {
                                variables.add(varName);
                            }
                        }
                    }
                }
                if (mVar[0].contains("+=")) {
                    String[] varNameValue = mVar[0].split(Pattern.quote("+="));
                    if (varNameValue.length > 1) {
                        String varName = varNameValue[0].trim();
                        varName = varName.replaceAll("[{]", "");
                        String varRange = varNameValue[1].trim().replaceAll("[}\\[\\])]", "");
                        if (varRange.contains(",")) {
                            String[] range = varRange.split(Pattern.quote(",").trim());
                            String min = range[0].trim();
                            String max = range[1].trim();
                            int consequence = (Integer.parseInt(max) - Integer.parseInt(min)) / scale;
                            ActionOutcomes.put(actionName, consequence + 1);

                        }
                    }
                }
            }
            AgentAction.put(file.getName(), setOfMech);

        }
    }

    public static void calculateStateTransitionProbability_New(String actionName, int randomSampleVaueGen)
            throws IOException {
        ArrayList<Double> StateProb = new ArrayList<Double>();
        randomProbGeneration rp = new randomProbGeneration();
        // System.out.println("State Transition Probability Generattion");
        FileWriter myWriter = new FileWriter(
                "D:\\prism-master\\StateTranProb\\AgentMechTranProb\\" + actionName);

        Map<String, ArrayList<Integer>> dataCorrTOvAR = new HashMap<>();
        try {
            File myData = new File("D:\\prism-master\\data\\" + actionName);
            Scanner myDataReader = new Scanner(myData);
            String phi = "";
            while (myDataReader.hasNextLine()) {
                ArrayList<Integer> Dvalues = new ArrayList<Integer>();
                String line = myDataReader.nextLine();
                String[] valueExtraction = line.split(Pattern.quote(":").trim(), 3);
                phi = valueExtraction[0].trim();
                String[] val = valueExtraction[2].split(",");
                for (int i = 0; i < val.length; i++) {
                    Dvalues.add(Integer.parseInt(val[i].trim()));

                }
                dataCorrTOvAR.put(phi, Dvalues);

            }

            myDataReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        // call to get state transition probabilities
        StateProb = rp.finalStateTranProb(randomSampleVaueGen);
        Set<String> phiKeys = dataCorrTOvAR.keySet();
        String[] keysArray = phiKeys.toArray(new String[phiKeys.size()]);
        List<Integer> firstPhi = new ArrayList<Integer>(dataCorrTOvAR.get(keysArray[0])); // PPE
        List<Integer> nextPhi = new ArrayList<Integer>(dataCorrTOvAR.get(keysArray[1])); // pollution
        for (int x = 0; x < firstPhi.size(); x++) {

            try {
                if (actionName != null && actionName.length() > 0
                        && actionName.charAt(actionName.length() - 1) == '0') { // null action
                    myWriter.write(firstPhi.get(x) + "," + nextPhi.get(x) + ":" + 1.0 + "\n"); // probability always 1
                } else {
                    myWriter.write(firstPhi.get(x) + "," + nextPhi.get(x) + ":" + StateProb.get(x) + "\n");
                }

            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
        myWriter.close();
        // System.out.println("Please see StateTranProb");
        // System.out.println();

    }

    public static Set<Double> getRandomNumbersWithNoDuplicates(double min, double max, int length) {
        Set<Double> numbers = new HashSet<Double>();
        while (numbers.size() < length) {
            double random_int = (Math.random() * (max - min) + min);
            numbers.add(Double.parseDouble(new DecimalFormat("##.##").format(random_int)));
        }

        return numbers;
    }
}