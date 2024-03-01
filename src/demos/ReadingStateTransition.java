package demos;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import parser.ast.SystemBrackets;

public class ReadingStateTransition {

    static String[] getTransitionValue(String currentInten, int offset, String actionName) throws IOException {

        Path path = Paths.get("src\\demos\\Data\\AgentTranProb\\" + currentInten + "\\" + actionName);
        List<String> totalTransitions = Files.readAllLines(path, StandardCharsets.UTF_8);
        // System.out.println("total transitions" + totalTransitions);
        String StateTran = totalTransitions.get(offset);
        // System.out.println("StateTran " + StateTran);
        String[] varValue = StateTran.split(":");
        String Values = varValue[0].trim();
        String[] eachValue = Values.split(",");
        return eachValue;

    }
}
