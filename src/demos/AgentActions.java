package demos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AgentActions {
    /***
     * 
     * This is the functions to get the current action/ actions in the intention or
     * partially executed plan
     * 
     * @param numberofAgents
     * @return
     * @throws FileNotFoundException
     */
    public static HashMap<Integer, ArrayList<String>> getAgentActions(File f1) throws FileNotFoundException {
        HashMap<Integer, ArrayList<String>> AgentsActions = new HashMap<Integer, ArrayList<String>>();
        Scanner sc = new Scanner(f1);
        String input;
        while (sc.hasNextLine()) {
            input = sc.nextLine();
            String[] label = input.split(":");
            String actionName = label[0].trim();

        }

        return AgentsActions;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        System.out.println("Extracting agents Actions");
        File directoryPath = new File("Intentions");
        File filesList[] = directoryPath.listFiles();
        Scanner sc = null;
        Integer numberofIntention = filesList.length;
        System.out.println("Number of Intnetion " + numberofIntention);
        for (int f = 0; f < filesList.length; f++) {
            Integer numerOfAgents = filesList.length;
            System.out.println("Intention id " + filesList[f].getName());
            System.out.println("Number of Agent " + numerOfAgents);
            File intList[] = filesList[f].listFiles();
            for (File f1 : intList) {
                String agentName = f1.getName();
                System.out.println("Agent Name " + agentName);
                ArrayList<String> setOfActions = new ArrayList<String>();
                getAgentActions(f1);

            }

        }

    }

}
