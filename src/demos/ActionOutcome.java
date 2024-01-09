package demos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActionOutcome {
    public static ArrayList<String> getActiionTransitionValue(HashMap<String, Integer> transitions) {
        ReadingStateTransition rst = new ReadingStateTransition();
        ArrayList<String> choiceTranValues = new ArrayList<String>();
        List<String> keys = new ArrayList<>(transitions.keySet());
        for (int k = 0; k < keys.size(); k++) {
            String choice = keys.get(k);
            // System.out.println("choice "+choice);
            for (int t = 0; t < transitions.get(choice); t++) {
                try {
                    String[] varValue = rst.getTransitionValue(t, choice);
                    String varValues = Stream.of(varValue).collect(Collectors.joining(","));
                    choiceTranValues.add(varValues);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
        return choiceTranValues;
    }

    public static ArrayList<String> getExecutionValuesNew(HashMap<String, Integer> transitions) {
        ReadingStateTransition rst = new ReadingStateTransition();
        ArrayList<String> choiceTranValues = new ArrayList<String>();
        List<String> keys = new ArrayList<>(transitions.keySet());
        for (int k = 0; k < keys.size(); k++) {
            String choice = keys.get(k);
            // System.out.println("choice "+choice);
            for (int t = 0; t < transitions.get(choice); t++) {
                try {
                    String[] varValue = rst.getTransitionValueNew(t, choice);
                    String varValues = Stream.of(varValue).collect(Collectors.joining(","));
                    choiceTranValues.add(varValues);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
        return choiceTranValues;
    }
}
