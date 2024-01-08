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
import java.util.Map.Entry;
import java.util.regex.Pattern;

//import cern.colt.Arrays;
public class ReadIntention {
    static int numberofIntention = 0;
    static Set<String> variables = new HashSet<String>();
    public static HashMap<String, Integer> ActionOutcomes = new HashMap<String, Integer>();
    final static Map<String, ArrayList<Integer>> ActionVarValues = new HashMap<>(); // key = var name and values =
                                                                                    // values
    public static final Map<String, Map<String, Boolean>> actionsconsequences = new HashMap<>();
    final static Map<String, ArrayList<String>> MechRange = new HashMap<>();
    public static HashMap<String, ArrayList<String>> intentionAction = new HashMap<String, ArrayList<String>>();
    static ArrayList<String> intentionId = new ArrayList<String>();

    public static ArrayList<String> getAgentNames() throws FileNotFoundException, IOException {

        return intentionId;

    }

    public static Set<String> getVariables() throws FileNotFoundException, IOException {

        return variables;

    }

    public static int getNumberOfAgent() throws FileNotFoundException, IOException {

        return numberofIntention;

    }

    public static HashMap<String, ArrayList<String>> getAgentMechanisms() throws FileNotFoundException, IOException {

        return intentionAction;

    }

    public static HashMap<String, Integer> getMechNoOfConse() throws FileNotFoundException, IOException {

        return ActionOutcomes;

    }

    public static void getData() throws FileNotFoundException, IOException {
        System.out.println("get data");
        File directoryPath = new File("Intentions");
        File filesList[] = directoryPath.listFiles();
        Scanner sc = null;
        numberofIntention = filesList.length;
        Map<String, Map<String, String>> result = new HashMap<>();
        for (File file : filesList) {
            Map<String, ArrayList<String>> ActionVar = new HashMap<>();

            ArrayList<String> setOfActions = new ArrayList<String>();
            // System.out.println("File name: " + file.getName());
            intentionId.add(file.getName());
            sc = new Scanner(file);
            String input;
            while (sc.hasNextLine()) {
                input = sc.nextLine();
                // System.out.println(input);
                String[] label = input.split(":");
                String actionName = label[0].trim();
                Map<String, String> varValue = new HashMap<>();
                setOfActions.add(actionName);
                String[] mVar = label[1].split(",");
                ArrayList<String> setOfvar = new ArrayList<String>();
                for (int a = 0; a < mVar.length; a++) {
                    String varName = mVar[a].trim();
                    System.out.println("var " + varName);
                    variables.add(varName);
                    setOfvar.add(varName);
                    varValue.put(varName, "true");
                }
                ActionVar.put(actionName, setOfvar);
                result.put(actionName, varValue);

            }
            intentionAction.put(file.getName(), setOfActions);
        }
        System.out.println("intentionActions " + intentionAction);
        System.out.println("Result " + result);

        Map<String, Map<String, Map<String, String>>> combinedResult = new HashMap<>();
        for (Map.Entry<String, ArrayList<String>> entry : intentionAction.entrySet()) {
            String keyI = entry.getKey();
            ArrayList<String> valuesI = entry.getValue();
            Map<String, String> valuesResult = new HashMap<>();
            System.out.println("=====Processing  current intention:=== " + keyI);
            Map<String, Map<String, String>> combinedval = new HashMap<>();
            for (int i = 0; i < valuesI.size(); i++) {
                String element = valuesI.get(i);
                if (result.containsKey(element)) {
                    valuesResult = (result.get(element));
                    combinedval.put(element, valuesResult);
                }
                System.out.println("check=== " + combinedval);
            }
            System.out.println("Set of actions in the current intnetions: " + valuesI);
            System.out.println("valuesResult " + valuesResult);
            System.out.println("==========Finish current intnetiom=========== " + keyI);
            combinedResult.put(keyI, combinedval);

        }
        System.out.println("Combined Result:");
        System.out.println(combinedResult);

    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        System.out.println("Hello, World!");
        getData();
    }
}