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
     */
    public HashMap<Integer, ArrayList<String>> getAgentActions(int numberofAgents) {
        HashMap<Integer, ArrayList<String>> AgentsActions = new HashMap<Integer, ArrayList<String>>();
        for (int a = 0; a < numberofAgents; a++) {
            int agentId = (a + 1);
            // System.out.println("Agent "+ (a+1));
            ArrayList<String> MechanismA1 = new ArrayList<String>();
            try {
                File myData = new File("D:\\prism-master\\AgentsMech\\Agent-" + (a + 1));
                Scanner myDataReader = new Scanner(myData);
                String phi = "";
                while (myDataReader.hasNextLine()) {
                    ArrayList<Integer> Dvalues = new ArrayList<Integer>();
                    String line = myDataReader.nextLine();
                    String[] valueExtraction = line.split(Pattern.quote(":").trim(), 3);
                    String mechanismName = valueExtraction[0].trim();
                    MechanismA1.add(mechanismName);

                }

                myDataReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            AgentsActions.put(agentId, MechanismA1);
        }
        // System.out.println(AgentsMechanism);
        return AgentsActions;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        System.out.println("Hello, World!");

    }

}
