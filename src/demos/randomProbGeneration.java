package demos;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class randomProbGeneration {

    public static ArrayList<Double> finalStateTranProb(int randomSampleVaueGen) {
        int n = randomSampleVaueGen;
        double notInclude = 0.0;
        ArrayList<Double> StateProb = new ArrayList<Double>();
        StateProb = stateTransitionProb(n);
        for (int s = 0; s < StateProb.size(); s++) {
            if (StateProb.get(s).equals(0.0)) {
                StateProb = stateTransitionProb(n);
                break;
            }
        }
        return StateProb;

    }

    public static ArrayList<Double> stateTransitionProb(int n) {
        ArrayList<Double> StateProb = new ArrayList<Double>();
        double prob[] = new double[n];
        double count = 0.0;
        while (count != 1.0) {
            count = 0.0;
            prob = probGene(n);
            List<double[]> list = Arrays.asList(prob);
            for (int i = 0; i < prob.length; i++) {
                double pro = Double.parseDouble(new DecimalFormat("##.##").format(prob[i]));

                count = count + pro;
            }

        }
        // System.out.println(count);
        for (int i = 0; i < prob.length; i++) {
            // System.out.println(prob[i]);
            double pro = Double.parseDouble(new DecimalFormat("##.##").format(prob[i]));
            StateProb.add(pro);

        }

        return StateProb;
    }

    public static double[] probGene(int n) {
        double a[] = new double[n];
        double s = 0.0d;
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            a[i] = random.nextDouble() + .01;
            // a[i] = 1.0d - random.nextDouble();
            // a[i] = -1 * Math.log(a[i]);
            // System.out.println("a[i]" + a[i]);
            s += a[i];
        }
        for (int i = 0; i < n; i++) {
            a[i] /= s;
            // System.out.println(a[i] /= s);
        }

        return a;
    }

}
