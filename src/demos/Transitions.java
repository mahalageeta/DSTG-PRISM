package demos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Transitions {
    /**
     * @param intList
     * @return
     * @throws IOException
     */
    public static HashMap<String, Integer> getActionsTransitions(String currrentInten) throws IOException {
        HashMap<String, Integer> Actiontransitions = new HashMap<String, Integer>();
        ArrayList<String> actions = new ArrayList<String>();
        File subfolder = new File("src\\demos\\Data\\AgentTranProb", currrentInten);
        if (subfolder.exists() && subfolder.isDirectory()) {
            File[] files = subfolder.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    String actionname = file.getName();
                    List<String> transitions = Files.readAllLines(Paths.get(file.getAbsolutePath()));
                    actions.add(file.getName());
                    Actiontransitions.put(actionname, transitions.size());

                }
            } else {
                System.out.println("Actions infromation is not provided");
            }
        } else {
            System.out.println("Intnetion information not exist");
        }

        // System.out.println("set of actions " + actions);
        // System.out.println("Transitions " + Actiontransitions);
        return Actiontransitions;

    }

}
