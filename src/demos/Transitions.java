package demos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Transitions {
    public static HashMap<String, Integer> getActionsTransitions(String action) throws IOException {
        ArrayList<String> actions = new ArrayList<String>();
        HashMap<String, Integer> Actiontransitions = new HashMap<String, Integer>();
        actions.add(action);
        List<String> transitions = Files
                .readAllLines(Paths.get("src\\demos\\Data\\AgentTranProb\\" + action));
        System.out.println("transitions " + transitions.size());
        Actiontransitions.put(action, transitions.size());
        transitions.clear();

        System.out.println("transitions " + Actiontransitions);
        return Actiontransitions;
    }
}
