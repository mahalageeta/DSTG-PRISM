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
    public static HashMap<String, Integer> getActionsTransitions(File intList[]) throws IOException {
        HashMap<String, Integer> Actiontransitions = new HashMap<String, Integer>();
        ArrayList<String> actions = new ArrayList<String>();
        for (File f1 : intList) {
            String actionname = f1.getName();
            List<String> transitions = Files.readAllLines(Paths.get(f1.getAbsolutePath()));
            actions.add(f1.getName());
            Actiontransitions.put(actionname, transitions.size());
        }
        // System.out.println("set of actions " + actions);
        // System.out.println("Transitions " + Actiontransitions);
        return Actiontransitions;

    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        System.out.println("Transition Probability");
        File directoryPath = new File("src\\demos\\Data\\AgentTranProb");
        File filesList[] = directoryPath.listFiles();
        for (int f = 0; f < filesList.length; f++) {
            HashMap<String, Integer> Actiontransitions = new HashMap<String, Integer>();
            System.out.println("Intention id " + filesList[f].getName());
            File intList[] = filesList[f].listFiles();
            ArrayList<String> actions = new ArrayList<String>();
            Actiontransitions = getActionsTransitions(intList);
            System.out.println("Transitions " + Actiontransitions);

        }

    }
}
