package demos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.Action;

import demos.MDPModelGenerator.GridModel;
import parser.State;
import parser.VarList;
import parser.ast.Declaration;
import parser.ast.DeclarationBool;
import parser.ast.DeclarationInt;
import parser.ast.DeclarationType;
import parser.ast.Expression;
import parser.type.Type;
import parser.type.TypeBool;
import parser.type.TypeInt;
import prism.DefaultModelGenerator;
import prism.ModelGenerator;
import prism.ModelType;
import prism.Prism;
import prism.PrismDevNullLog;
import prism.PrismException;
import prism.PrismFileLog;
import prism.PrismLangException;
import prism.PrismLog;

/**
 * 
 * 
 * 
 * 
 * This is the main file to run the experiements
 * 
 * 
 * 
 * 
 ***/
public class MDPModelGeneratorDSTG {
	public static ArrayList<String> AgentsName = new ArrayList<String>();
	public static HashMap<Integer, ArrayList<String>> AgentsActions = new HashMap<Integer, ArrayList<String>>();
	public static HashMap<String, Integer> ActionOutcomes = new HashMap<String, Integer>();
	public static HashMap<String, ArrayList<String>> AgentAction = new HashMap<String, ArrayList<String>>();
	public static HashMap<String, ArrayList<Double>> transitionProb = new HashMap<String, ArrayList<Double>>();
	public static HashMap<Integer, ArrayList<Double>> SelectionTransitionProb = new HashMap<Integer, ArrayList<Double>>();
	public static HashMap<String, ArrayList<Double>> NormalizeSelectionTransitionProb = new HashMap<String, ArrayList<Double>>();
	public static HashMap<String, ArrayList<String>> transitionValue = new HashMap<String, ArrayList<String>>();
	public static HashMap<Integer, ArrayList<String>> AgenttransitionValue = new HashMap<Integer, ArrayList<String>>();
	public static ArrayList<String> variables = new ArrayList<String>();
	public static HashMap<Integer, Integer> ModelMaxGenLimit = new HashMap<Integer, Integer>(); // key = variable index
																								// and value = maximum
																								// value
	static int numberofAgents = 0;
	static int scale = 10;

	public static void main(String[] args) throws FileNotFoundException {
		/***
		 * 
		 * Before start the experiments we want delete all the data
		 * 
		 * 
		 ***/
		File file = new File("src\\demos\\Data");
		deletePreInput(file);
		File file1 = new File("src\\demos\\Data\\AgentTranProb");
		deletePreInput(file1);
		System.out.println("Previous Data Deleted");
		/*****
		 * 
		 * Read the data for each intention
		 * 
		 */
		ReadIntention rss = new ReadIntention();

		try {

			Set<String> variablesSet = new HashSet<String>();
			// Get necessary information at run time
			rss.getData(scale);

			numberofAgents = rss.getNumberOfAgent();
			AgentAction = rss.getAgentMechanisms();
			ActionOutcomes = rss.getMechNoOfConse();
			AgentsName = rss.getAgentNames();
			variablesSet = rss.getVariables();
			variables.addAll(variablesSet);
			// Generate the data
			System.out.println("numberofAgents " + numberofAgents + " " + AgentsName);
			System.out.println("mechanismOutcome " + ActionOutcomes);
			System.out.println("AgentAction " + AgentAction);
			rss.dataGenerate(scale);
			rss.getStateTranProbGeneration();

		} catch (FileNotFoundException e2) {

			e2.printStackTrace();
		} catch (IOException e2) {

			e2.printStackTrace();
		}

		List<String> agents = new ArrayList<>(AgentAction.keySet());
		for (int i = 0; i < agents.size(); i++) {
			AgentsActions.put(i + 1, AgentAction.get(agents.get(i)));
		}
		System.out.println("AgentActions mapping " + AgentsActions);

		for (int a = 0; a < AgentsActions.size(); a++) {
			ArrayList<String> Agentmechvalue = new ArrayList<String>();
			for (int m = 0; m < AgentsActions.get(a + 1).size(); m++) {
				String action = AgentsActions.get(a + 1).get(m);
				Path path = Paths.get("src\\demos\\Data\\AgentTranProb\\" + action);
				try {
					ArrayList<String> totalTransitions = (ArrayList<String>) Files.readAllLines(path,
							StandardCharsets.UTF_8);
					ArrayList<Double> mechprob = new ArrayList<Double>();
					ArrayList<String> mechvalue = new ArrayList<String>();
					for (int t = 0; t < totalTransitions.size(); t++) {
						String tranValue = totalTransitions.get(t);
						String[] eachValue = tranValue.split(":", 2);
						String Values = eachValue[0].trim();
						String prob = eachValue[1].trim();
						mechvalue.add(Values);
						Agentmechvalue.add(Values);
						mechprob.add(Double.parseDouble(prob));
					}
					transitionValue.put(action, mechvalue);
					transitionProb.put(action, mechprob);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			AgenttransitionValue.put(a + 1, Agentmechvalue);
		}

		System.out.println("transitionValue " + transitionValue);
		System.out.println("transitionVProb " + transitionProb);
		System.out.println("Agent transitionValue " + AgenttransitionValue);
		variables.add(0, "agent");

		System.out.println("variables " + variables);

		// AssignModelMaxGenValues amgv = new AssignModelMaxGenValues();
		// try {
		// ModelMaxGenLimit = amgv.giveVarDeclarationType();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println("ModelMaxGenLimit "+ModelMaxGenLimit);
		//
		new MDPModelGeneratorDSTG().run();
	}

	private static void deletePreInput(File file) {

		String[] myFiles;
		if (file.isDirectory()) {
			myFiles = file.list();
			for (int i = 0; i < myFiles.length; i++) {
				File myFile = new File(file, myFiles[i]);
				myFile.delete();
			}

		}
	}

	public void run() throws FileNotFoundException {
		try {
			// Create a log for PRISM output (hidden or stdout)
			// PrismLog mainLog = new PrismDevNullLog();

			PrismLog mainLog = new PrismFileLog("stdout");
			Prism prism = new Prism(mainLog);
			prism.initialise();
			// prism.setEngine(Prism.EXPLICIT);
			prism.setEngine(Prism.HYBRID);
			ExampleModel modelGen = new ExampleModel(2, 1100, 800);
			prism.loadModelGenerator(modelGen);
			prism.exportTransToFile(true, Prism.EXPORT_DOT_STATES, new File("mdp.dot"));
			prism.exportTransToFile(true, Prism.EXPORT_PLAIN, new File("ExStates.tra"));
			prism.exportStatesToFile(Prism.EXPORT_PLAIN, new File("ExStates.sta"));
			prism.exportLabelsToFile(null, Prism.EXPORT_PLAIN, new File("ExStates.lab"));
			String[] props = new String[] {
					// "filter(print,Pmin=?[F<=5 pollution<=100 ],pollution>=200)",

					"Pmin=?[G<=10((pollution>=150) =>(P>=1[F<=5 (pollution<=100)]))]",
					"Pmin=?[G<=10((pollution>=200) =>(P>=1[F<=5 (pollution<=100)]))]",
					"Pmin=?[G<=10((pollution>=250) =>(P>=1[F<=5 (pollution<=100)]))]",
					// "filter(print,Pmin=?[F<=5(pollution>=100 =>P>=1[F<=5
					// (pollution<=70&pollution!=0)])])",
					// "(pollution>=200)=>P>=1[F<=1 (pollution<=70&pollution!=0) ]"
			};
			// "Pmin=?[F<=10 pollution<=60 {pollution>=80} {max}]",
			// "Pmin=?[F<=15 pollution<=60 {pollution>=80} {max}]",
			// "Pmin=?[F<=20 pollution<=60 {pollution>=80} {max}]",
			for (String prop : props) {
				System.out.println(prop + ":");
				System.out.println(prism.modelCheck(prop).getResult());
			}

			prism.closeDown();

		} catch (FileNotFoundException e) {
			System.out.println("Error: " + e.getMessage());
			System.exit(1);
		} catch (PrismException e) {
			System.out.println("Error: " + e.getMessage());
			System.exit(1);
		}
	}

	class ExampleModel implements ModelGenerator {

		private int maxagent;
		private int maxPPE;
		private int maxPollution;
		private State exploreState;
		HashMap<String, Integer> valueOfVariable = new HashMap<String, Integer>();
		HashMap<String, Integer> MaxValueOfvariables = new HashMap<String, Integer>();

		public ExampleModel(int maxagent, int maxPPE, int maxPollution) {
			this.maxPPE = maxPPE;
			this.maxPollution = maxPollution;
			this.maxagent = maxagent;
		}

		@Override
		public ModelType getModelType() {
			// System.out.println("ModelType");
			return ModelType.MDP;
		}

		@Override
		public List<String> getVarNames() {
			// System.out.println("getVarNames");
			// System.out.println("variables "+variables);
			return variables;
		}

		@Override
		public List<Type> getVarTypes() {
			// System.out.println("getVarTypes");
			return Arrays.asList(TypeInt.getInstance(), TypeInt.getInstance(), TypeInt.getInstance());
		}

		@Override
		public State getInitialState() throws PrismException {
			// System.out.println("getInitialState");
			return new State(variables.size()).setValue(0, 1).setValue(1, 10).setValue(2, 0);
		}

		@Override
		public DeclarationType getVarDeclarationType(int i) throws PrismException {
			// System.out.println("getVarDeclarationType");
			Type type = getVarType(i);
			// Here i represent the number of varibles in our model

			switch (i) {
				case 0:
					// selected_agent
					return new DeclarationInt(Expression.Int(1), Expression.Int(2));
				case 1:
					// PPE range
					return new DeclarationInt(Expression.Int(10), Expression.Int(1300));

				case 2:
					// pollution range
					return new DeclarationInt(Expression.Int(0), Expression.Int(1000));
				// return new DeclarationDouble(Expression.Int(0), Expression.Int(10));

				default:
					throw new PrismException("No default declaration avaiable for type " + type);
				// return new DeclarationInt(Expression.Int(1), Expression.Int(15));
			}

			// MaxValueOfvariables.put(variables.get(i),ModelMaxGenLimit.get(i));
			// System.out.println("MaxValueOfvariables "+MaxValueOfvariables);
			// return new DeclarationInt(Expression.Int(1),
			// Expression.Int(ModelMaxGenLimit.get(i)));

		}

		@Override
		public List<String> getLabelNames() {
			// System.out.println(" getLabelNames");
			return Arrays.asList("achivement", "maintain");
		}

		@Override
		public void exploreState(State exploreState) throws PrismException {
			// Store the state (for reference, and because will clone/copy it later)
			// System.out.println("====================Explore State ================");

			// System.out.println("explore state "+exploreState );
			this.exploreState = exploreState;
			// agent = ((Integer) exploreState.varValues[0]).intValue();
			for (int s = 0; s < variables.size(); s++) {
				// System.out.println("var "+ variables.get(s));
				// System.out.println(((Integer) exploreState.varValues[s+1]).intValue());
				valueOfVariable.put(variables.get(s), ((Integer) exploreState.varValues[s]).intValue());

			}
		}

		@Override
		public int getNumChoices() throws PrismException {

			// System.out.println("getNumChoices");
			// Number of actions
			int agent = valueOfVariable.get(variables.get(0));
			// System.out.println("Current Agent is "+ agent);

			// int choices = AgentsMechanism.get(agent).size();

			// System.out.println("number of choice "+ 1);
			// return choices;
			return 1;

		}

		@Override
		public int getNumTransitions(int i) throws PrismException {
			// System.out.println("getNumTransitions and value of i = "+ i);
			int agent = valueOfVariable.get(variables.get(0));
			// System.out.println("agent = "+ agent +" "+AgentsName.get(agent-1));
			Map<String, Map<String, String>> normData = new HashMap<String, Map<String, String>>();
			Map<String, String> normPreconditionsn = new HashMap<String, String>();
			// System.out.println("normPreconditions "+ normPreconditionsn);
			Map<String, Double> finalProbMech = getMechLiklihoodValue(normData, normPreconditionsn, agent);
			// System.out.println("Selection Proba===="+ finalProbMech);
			int transitions = 0;
			ArrayList<Double> SelectionExecutionprob = new ArrayList<Double>();
			for (int a = 0; a < AgentsActions.get(agent).size(); a++) {
				String action = AgentsActions.get(agent).get(a);
				System.out.println("action " + action);
				transitions = transitions + transitionProb.get(action).size();
				double mechSelectionProb = finalProbMech.get(action);
				System.out.println("Action Selection Proba " + mechSelectionProb);
				SelectionTransitionProb.clear();
				for (int t = 0; t < transitionProb.get(action).size(); t++) {
					double prob = transitionProb.get(action).get(t);
					// System.out.println("execution "+ prob);
					double finalProb = prob * mechSelectionProb;
					finalProb = Double.parseDouble(new DecimalFormat("##.###").format(finalProb));
					// System.out.println("execution and slection "+ finalProb);

					SelectionExecutionprob.add(finalProb);
				}

				// ArrayList<Double> NormalizemechSelectionExecutionprob =
				// getFinalTransitionProb(SelectionExecutionprob);
				// double sum2 = NormalizemechSelectionExecutionprob.stream()
				// .mapToDouble(a1 -> a1)
				// .sum();
				// System.out.println("sum "+sum2);
				SelectionTransitionProb.put(agent, SelectionExecutionprob);
				// System.out.println("Combined "+ SelectionExecutionprob);
				// System.out.println(" NormalizeCombined "+
				// NormalizemechSelectionExecutionprob);

			}
			// System.out.println("SelectionTransitionProb "+SelectionTransitionProb);
			// System.out.println("number of transitions = "+ transitions);

			return transitions;

		}

		@Override
		/**
		 * Get the action label of a transition within a choice, specified by its
		 * index/offset.
		 * The label can be any Object, but will often be treated as a string, so it
		 * should at least
		 * have a meaningful toString() method implemented. Absence of an action label
		 * is denoted by null.
		 * Note: For most types of models, the action label will be the same for all
		 * transitions within
		 * the same nondeterministic choice (i.e. for each different value of
		 * {@code offset}),
		 * but for Markov chains this may not necessarily be the case.
		 * 
		 * @param i      Index of the nondeterministic choice
		 * @param offset Index of the transition within the choice
		 */

		public Object getTransitionAction(int i, int offset) throws PrismException {
			int agent = valueOfVariable.get(variables.get(0));
			String action = AgentsName.get(agent - 1);
			// System.out.println("Transit Action agent = "+agent+" Agent Name "+ action);

			// return action;
			return action;
		}

		@Override
		public Object getTransitionProbability(int i, int offset) throws PrismException {
			int agent = valueOfVariable.get(variables.get(0));
			// System.out.println(" agent "+ agent);
			double prob = SelectionTransitionProb.get(agent).get(offset);
			// System.out.println("Offset" +offset +"Transit Probability "+ prob);
			return prob;
		}

		@Override
		/**
		 * Get the target (as a new State object) of a transition within a choice,
		 * specified by its index/offset.
		 * 
		 * @param i      Index of the nondeterministic choice
		 * @param offset Index of the transition within the choice
		 */
		public State computeTransitionTarget(int i, int offset) throws PrismException {
			// System.out.println("computeTransitionTarget + offset "+ offset);
			State target = new State(exploreState);
			int agent = valueOfVariable.get(variables.get(0));
			// System.out.println(" agent "+ agent);
			if ((valueOfVariable.get(variables.get(1)) <= maxPPE)
					&& (valueOfVariable.get(variables.get(2)) <= maxPollution)) {
				// String tranValue = transitionValue.get(action).get(offset);
				String tranValue = AgenttransitionValue.get(agent).get(offset);
				// System.out.println("tranValue "+tranValue);
				String[] varValue = tranValue.split(",", (variables.size() - 1));
				// System.out.println("var value "+Arrays.toString(varValue));
				// System.out.println("current state "+ target);
				String currentTarget = target.toString().replaceAll("\\(", "").replaceAll("\\)", "");
				String[] parts = currentTarget.split(",", (variables.size() - 1));
				// System.out.println(variables.size());
				int sum = 0;
				for (int v = 0; v < varValue.length; v++) {
					sum = sum + Integer.parseInt(varValue[v]);
				}
				if ((agent == numberofAgents) && (sum != 0)) {
					// System.out.println("agent value change to 1");
					target.setValue(0, 1);
				} else if ((sum != 0)) {
					// System.out.println("Agent avlue change to 2");
					target.setValue(0, agent + 1);
				}

				for (int p = 0; p < parts.length; p++) {
					// System.out.println("valueOfVariable.get(p+1)
					// "+(valueOfVariable.get(variables.get(p))));
					// System.out.println(valueOfVariable);
					// System.out.println(variables.get(p+1));
					int newTotalValue = valueOfVariable.get(variables.get(p + 1)) + Integer.parseInt(varValue[p]);

					if (newTotalValue < 0) {
						newTotalValue = 0; // Avoid - valued for pollution like 0-1 = -1
					}
					// System.out.println( "valueOfVariable.get(p)+combinedVarValue.get(p) "+
					// newTotalValue);
					target.setValue(p + 1, newTotalValue);
				}
				// System.out.println("new state "+ target);
				return target;

			}

			// Never happens
			// System.out.println("never happen "+ target);
			return target;
		}

		@Override
		public boolean isLabelTrue(int i) throws PrismException {
			switch (i) {
				// achievement
				case 0:

					return valueOfVariable.get(variables.get(1)) > 1;
				// maintain
				case 1:

					return valueOfVariable.get(variables.get(1)) < 2;

				default:
					throw new PrismException("Label number \"" + i + "\" not defined");
			}
		}

		private ArrayList<Double> getFinalTransitionProb(ArrayList<Double> tranP) {

			ArrayList<Double> transitionProb = new ArrayList<Double>();

			double sum = tranP.stream()
					.mapToDouble(a -> a)
					.sum();

			sum = Double.parseDouble(new DecimalFormat("##.###").format(sum));
			// System.out.println(" trap sum "+ tranP);
			// System.out.println(" trap sum "+ sum);

			for (int t = 0; t < tranP.size(); t++) {
				double prob = tranP.get(t) / sum;
				prob = Double.parseDouble(new DecimalFormat("##.###").format(prob));
				// System.out.println( "check "+prob);
				transitionProb.add(prob);
			}

			double sum1 = transitionProb.stream()
					.mapToDouble(a -> a)
					.sum();
			sum1 = Double.parseDouble(new DecimalFormat("##.###").format(sum1));
			// System.out.println("check" +sum1);

			if (sum1 > 1.0) {
				double max = Integer.MIN_VALUE;
				Integer index = null;
				/// System.out.println("not 1 " +sum1);
				double diffsum = (sum1 - 1.0);
				diffsum = Double.parseDouble(new DecimalFormat("##.###").format(diffsum));

				max = Collections.max(transitionProb);
				double newvalue = Double
						.parseDouble(new DecimalFormat("##.###").format(transitionProb.get(index) - diffsum));
				transitionProb.add(index, newvalue);
			} else if (sum1 < 1.0) {
				double min = Integer.MAX_VALUE;
				Integer index = null;
				// System.out.println("not 1 " +sum1);
				double diffsum = (1.0 - sum1);
				diffsum = Double.parseDouble(new DecimalFormat("##.###").format(diffsum));
				;
				// System.out.println("diference " +diffsum);
				min = Collections.min(transitionProb);

				// System.out.println("Entry with the highest value: "+name);
				double newvalue = Double
						.parseDouble(new DecimalFormat("##.###").format(transitionProb.get(index) + diffsum));
				transitionProb.add(index, newvalue);
			}

			return transitionProb;
		}

		private Map<String, Double> getMechLiklihoodValue(Map<String, Map<String, String>> norm,
				Map<String, String> normPreconditions, int a) {
			Map<String, Map<String, Double>> wValueOfMech = new HashMap<String, Map<String, Double>>();
			Map<String, Double> finalProbMech = new HashMap<String, Double>();
			System.out.println(" norm " + norm);
			// System.out.println("normPreconditions "+ normPreconditions);
			for (int m = 0; m < AgentsActions.get(a).size(); m++) {

				String mechanism = AgentsActions.get(a).get(m);
				System.out.println("action " + mechanism);

				Map<String, Double> normCorrW = new HashMap<String, Double>();
				System.out.println("mechanism " + mechanism);

				try {
					File myMech = new File("D:\\prism-master\\data\\" + mechanism);
					Scanner myMechReader = new Scanner(myMech);
					while (myMechReader.hasNextLine()) {
						Double minRange = 0.0;
						Double maxRange = 0.0;
						Double singleRange = 0.0;
						String data = myMechReader.nextLine();
						String[] label = data.split(":");
						String varM = label[0].trim();
						String rangeM = label[1].trim();
						if (rangeM.contains("#")) {
							String[] minMax = rangeM.split("#");
							minRange = Double.parseDouble((minMax[0].trim()));
							maxRange = Double.parseDouble(minMax[1].trim());
							// System.out.println("max = "+maxRange +" min = "+minRange);

						} else {

							singleRange = Double.parseDouble(rangeM.trim());

						}

						for (Entry<String, Map<String, String>> n : norm.entrySet()) {

							String normId = n.getKey(); // like P1 ,C1

							String normPre = "";
							String preVar = "";
							Integer prevalue = 0;
							if (normPreconditions.get(normId).contains(">=")) {
								// System.out.println("normId "+normId);

								String[] preExp = normPreconditions.get(normId).split(">=");
								preVar = preExp[0].trim();
								prevalue = Integer.parseInt(preExp[1].trim());
								// System.out.println(" prevalue "+ prevalue);
							} else {
								normPre = normPreconditions.get(normId);
							}

							String normType = normId.replaceAll("[^A-Z]", "");

							/****
							 * 
							 * For Prohibition P
							 * 
							 */
							if ((normType.equalsIgnoreCase("P"))) {

								if (n.getValue().containsKey(varM)) {

									double prob = 0.0;
									String AtomName = varM;
									int nConsquentValue = Integer.parseInt(n.getValue().get(AtomName));
									System.out.println(" P nConsquentValue " + nConsquentValue);
									System.out.println(" P valueOfVariable.get(varM) " + valueOfVariable.get(varM));
									double currentTarget = nConsquentValue - valueOfVariable.get(varM);
									System.out.println(" P currentTarget " + currentTarget);
									// System.out.println("currentTarget "+ currentTarget);
									System.out.println(" P Upper bound " + maxRange);
									System.out.println("P Lower bound " + minRange);

									if (maxRange <= currentTarget) {

										prob = 1.0;
										System.out.println(" P return prob  Upper bound <= currentTarget " + prob);

									} else if (minRange >= currentTarget) {

										prob = 0.0;
										System.out.println(" P return prob Lower bound >= currentTarget " + prob);

									} else {

										prob = Double.parseDouble(new DecimalFormat("##.###").format(
												(double) (currentTarget - minRange) / (maxRange - minRange)));
										System.out.println(" P return prob else " + prob);

									}

									double result = Double
											.parseDouble(new DecimalFormat("##.###").format(Math.pow(w, prob)));

									System.out.println(" P return  w power prob " + result);

									normCorrW.put(normId, result);
								}

							} else if (normType.equalsIgnoreCase("C")) {

								if (n.getValue().containsKey(varM)) {
									if ((normPre.equalsIgnoreCase("true"))) {
										double prob = 0.0;
										String AtomName = varM;

										int nConsquentValue = Integer.parseInt(n.getValue().get(AtomName));
										System.out.println(" C nConsquentValue " + nConsquentValue);
										System.out.println(" C valueOfVariable.get(varM) " + valueOfVariable.get(varM));
										double currentTarget = nConsquentValue - valueOfVariable.get(varM);
										System.out.println("currentTarget " + currentTarget);
										System.out.println("Upper bound " + maxRange);
										System.out.println("Lower bound " + minRange);
										if (maxRange <= currentTarget) {
											prob = 0.0;
											System.out.println(" C return prob maxRange <= currentTarget " + prob);

										} else if (minRange >= currentTarget) {

											prob = 1.0;
											System.out.println(" C return prob minRange >= currentTarget " + prob);

										} else {

											prob = Double.parseDouble(new DecimalFormat("##.###").format(
													(double) (maxRange - currentTarget) / (maxRange - minRange)));
											System.out.println(" C return prob else " + prob);

										}

										;

										double result = Double
												.parseDouble(new DecimalFormat("##.###").format(Math.pow(w, prob)));

										System.out.println(" C return  w power prob " + result);

										normCorrW.put(normId, result);
									} else if ((!(normPre.equalsIgnoreCase("true")))
											&& (valueOfVariable.get(varM) >= prevalue)) {
										// System.out.println("varM "+ varM +" " + valueOfVariable.get(varM) );
										// C2 in revised MAS // make pollution>=200

										double prob = 0.0;
										String AtomName = varM;
										int nConsquentValue = Integer.parseInt(n.getValue().get(AtomName));
										// System.out.println("nConsquentValue "+nConsquentValue);
										double currentTarget = nConsquentValue - valueOfVariable.get(varM);
										// System.out.println("currentTarget "+currentTarget);

										if (maxRange <= currentTarget) {
											prob = 0.0;
											// System.out.println(" C return prob maxRange <= currentTarget "+ prob);

										} else if (minRange >= currentTarget) {

											prob = 1.0;
											// System.out.println(" C return prob minRange >= currentTarget "+ prob);

										} else {

											prob = Double.parseDouble(new DecimalFormat("##.###").format(
													(double) (maxRange - currentTarget) / (maxRange - minRange)));
											// System.out.println(" C return prob otherwise "+ prob);

										}

										double result = Double
												.parseDouble(new DecimalFormat("##.###").format(Math.pow(w, prob)));

										normCorrW.put(normId, result);
									}
								}
							}

						}

						// wValueOfMech.put((String) AgentsMechanism.get(i).get(a), normCorrW);
						// System.out.println("normCorrW "+normCorrW);

						wValueOfMech.put(mechanism, normCorrW);

					}
					myMechReader.close();
				} catch (FileNotFoundException e) {
					System.out.println("An error occurred.");
					e.printStackTrace();
				}

			}
			// System.out.println("wValueOfMech "+ wValueOfMech);
			for (Entry<String, Map<String, Double>> w : wValueOfMech.entrySet()) {
				// System.out.println("w.getKey() " +w.getKey());
				double finalMProb = 1.0;
				Set<String> normKeys = w.getValue().keySet();
				String[] normkeysArray = normKeys.toArray(new String[normKeys.size()]);
				// System.out.println("normkeysArray "+Arrays.toString(normkeysArray));

				for (int f = 0; f < normkeysArray.length; f++) {
					// System.out.println(normkeysArray[f]);

					double sumOfw = 0.0;
					for (Entry<String, Map<String, Double>> w1 : wValueOfMech.entrySet()) {

						sumOfw = sumOfw + w1.getValue().get(normkeysArray[f]);
					}

					// System.out.println("sumOfw "+sumOfw);
					double nProb = w.getValue().get(normkeysArray[f]) / sumOfw;
					// System.out.println("After Softfunction nProb "+nProb);
					finalMProb = Double.parseDouble(new DecimalFormat("##.###").format(finalMProb * nProb));

				}

				finalProbMech.put(w.getKey(), finalMProb);

			}
			/***
			 * Normalize the Probabilities
			 * 
			 **/

			Double sum = finalProbMech.values().stream().mapToDouble(d -> d).sum();
			sum = Double.parseDouble(new DecimalFormat("##.###").format(sum));
			// System.out.println(" sum "+ sum);

			Map<String, Double> finalProbMech1 = new HashMap<String, Double>();
			ArrayList<Double> prob = new ArrayList<Double>();
			for (Entry<String, Double> pair : finalProbMech.entrySet()) {
				prob.add(pair.getValue());
			}

			List keys = new ArrayList(finalProbMech.keySet());
			for (int t = 0; t < finalProbMech.keySet().size(); t++) {
				double probability = prob.get(t) / sum;
				probability = Double.parseDouble(new DecimalFormat("##.###").format(probability));
				finalProbMech1.put((String) keys.get(t), probability);
			}

			// System.out.println("getMechLiklihoodValue finalProbMech1 == " +
			// finalProbMech1);
			return finalProbMech1;
		}

	}
}
