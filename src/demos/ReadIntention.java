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

import parser.ast.SystemBrackets;

//import cern.colt.Arrays;
public class ReadIntention {
    public static Map<String, ArrayList<String>> getAgentActions(String currrentInten)
            throws FileNotFoundException, IOException {
        File directoryPath = new File("Intentions");
        File filesList[] = directoryPath.listFiles();
        File subfolder = new File("Intentions", currrentInten);
        Map<String, ArrayList<String>> agentActions = new HashMap<>();
        Scanner sc = null;
        if (subfolder.exists() && subfolder.isDirectory()) {

            File[] files = subfolder.listFiles();
            if (files != null && files.length > 0) {
                // System.out.println("Files in subfolder " + currrentInten + " of folder " +
                // "Intentions" + ":");

                Map<String, ArrayList<String>> agentActions1 = new HashMap<>();
                for (File file : files) {
                    ArrayList<String> setOfActions = new ArrayList<String>();
                    String agentName = file.getName();
                    // System.out.println("Agent Name " + agentName);
                    sc = new Scanner(file);
                    String input;
                    while (sc.hasNextLine()) {
                        input = sc.nextLine();
                        // System.out.println(input);
                        String[] label = input.split(":");
                        String actionName = label[0].trim();
                        setOfActions.add(actionName);

                    }

                    // System.out.println(setOfActions);
                    agentActions1.put(agentName, setOfActions);
                    agentActions.putAll(agentActions1);
                }
            } else {
                System.out.println("There is no information for intnetion id" + currrentInten);
            }

        } else {
            System.out.println("The Intention folder is empty");

        }

        // System.out.println("check " + result);

        return agentActions;

    }

    public static Map<String, Set<String>> getvaribles(String currrentInten)
            throws FileNotFoundException, IOException {

        Map<String, Set<String>> agentvar = new HashMap<>();
        File directoryPath = new File("Intentions");
        File filesList[] = directoryPath.listFiles();
        File subfolder = new File("Intentions", currrentInten);
        Scanner sc = null;
        if (subfolder.exists() && subfolder.isDirectory()) {
            File[] files = subfolder.listFiles();
            if (files != null && files.length > 0) {

                for (File file : files) {

                    String agentName = file.getName();
                    sc = new Scanner(file);
                    String input;

                    while (sc.hasNextLine()) {
                        Set<String> varibles = new HashSet<String>();
                        input = sc.nextLine();
                        // System.out.println("input " + input);
                        String actionName = "";
                        String[] label = input.split(":");
                        String actionVar = label[1].trim();
                        actionName = label[0].trim();
                        // System.out.println("actionName " + actionName);
                        // System.out.println("actionVar " + actionVar);
                        varibles.add(actionVar);
                        agentvar.put(actionName, varibles);

                    }

                }
            } else {
                System.out.println("There is no information for intnetion id" + currrentInten);
            }

        } else {
            System.out.println("The Intention folder is empty");

        }

        return agentvar;

    }

}

// public static void getData() throws FileNotFoundException, IOException {
// System.out.println("get data");
// Map<String, Map<String, String>> result = new HashMap<>();
// File directoryPath = new File("Intentions");
// File filesList[] = directoryPath.listFiles();
// Scanner sc = null;
// numberofIntention = filesList.length;
// System.out.println("Number of Intnetion " + numberofIntention);
// for (int f = 0; f < filesList.length; f++) {
// System.out.println("Intention id " + filesList[f].getName());
// File intList[] = filesList[f].listFiles();
// for (File f1 : intList) {
// String agentName = f1.getName();
// System.out.println("Agent Name " + agentName);
// Map<String, ArrayList<String>> ActionVar = new HashMap<>();
// ArrayList<String> setOfActions = new ArrayList<String>();
// intentionId.add(filesList[f].getName());
// sc = new Scanner(f1);
// String input;
// while (sc.hasNextLine()) {
// input = sc.nextLine();
// // System.out.println(input);
// String[] label = input.split(":");
// String actionName = label[0].trim();
// Map<String, String> varValue = new HashMap<>();
// setOfActions.add(actionName);
// String[] mVar = label[1].split(",");
// ArrayList<String> setOfvar = new ArrayList<String>();
// for (int a = 0; a < mVar.length; a++) {
// String varName = mVar[a].trim();
// System.out.println("var " + varName);
// variables.add(varName);
// setOfvar.add(varName);
// varValue.put(varName, "true");
// }
// ActionVar.put(actionName, setOfvar);
// result.put(actionName, varValue);

// }
// intentionAction.put(filesList[f].getName(), setOfActions);
// AgentsActions.put(agentName, setOfActions);
// }

// System.out.println("intentionActions " + intentionAction);
// System.out.println("Agent Actions " + AgentsActions);
// System.out.println("Result " + result);

// Map<String, Map<String, Map<String, String>>> combinedResult = new
// HashMap<>();
// for (Map.Entry<String, ArrayList<String>> entry : intentionAction.entrySet())
// {
// String keyI = entry.getKey();
// ArrayList<String> valuesI = entry.getValue();
// Map<String, String> valuesResult = new HashMap<>();
// System.out.println("=====Processing current intention:=== " + keyI);
// Map<String, Map<String, String>> combinedval = new HashMap<>();
// for (int i = 0; i < valuesI.size(); i++) {
// String element = valuesI.get(i);
// if (result.containsKey(element)) {
// valuesResult = (result.get(element));
// combinedval.put(element, valuesResult);
// }
// System.out.println("check=== " + combinedval);
// }
// System.out.println("Set of actions in the current intnetions: " + valuesI);
// System.out.println("valuesResult " + valuesResult);
// System.out.println("==========Finish current intnetiom=========== " + keyI);
// combinedResult.put(keyI, combinedval);

// }
// System.out.println("Combined Result:");
// System.out.println(combinedResult);
// }

// }
