package demos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import parser.ast.SystemBrackets;

public class ActionOutcome {
    public static ArrayList<String> getExecutionValuesNew(String currentInten, HashMap<String, Integer> transitions) {
        ReadingStateTransition rst = new ReadingStateTransition();
        ArrayList<String> choiceTranValues = new ArrayList<String>();
        // System.out.println("check number of transistions" + transitions);
        List<String> keys = new ArrayList<>(transitions.keySet());
        for (int k = 0; k < keys.size(); k++) {
            String choice = keys.get(k);
            // System.out.println("choice " + choice);
            for (int t = 0; t < transitions.get(choice); t++) {
                try {
                    // System.out.println("here " + t);
                    String[] varValue = rst.getTransitionValue(currentInten, t, choice);
                    String varValues = Stream.of(varValue).collect(Collectors.joining(","));
                    choiceTranValues.add(varValues);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        // System.out.println("choiceTranVal " + choiceTranValues);
        return choiceTranValues;
    }
}
