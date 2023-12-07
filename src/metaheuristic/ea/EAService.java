package metaheuristic.ea;

import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.UnknownObjectType;
import metaheuristic.ea.base.*;
import metaheuristic.ea.crossover.OneCutCrossover;
import metaheuristic.ea.crossover.PTLCutCrossover;
import metaheuristic.ea.crossover.SimpleCrossOverStrategy;
import metaheuristic.ea.crossover.SimplePermutationCrossover;
import metaheuristic.ea.mutation.*;
import metaheuristic.ea.parentselector.CostBasedParentSelector;
import metaheuristic.ea.parentselector.RouletteWheelParentSelector;
import metaheuristic.ea.parentselector.TournamentParentSelector;
import metaheuristic.ea.terminalcondition.*;
import metaheuristic.ea.victimselector.RouletteWheelVictimSelector;
import metaheuristic.ea.victimselector.SimpleVictimSelector;
import metaheuristic.ils.MOTAPRandomPerturbator;
import metaheuristic.ils.Perturbator;
import problems.base.InitialSolutionGenerator;
import problems.motap.MOTAProblem;
import problems.motap.crossover.SimpleMOTACrossOver;
import problems.motap.mutation.*;
import problems.motap.mutation.GRMR.GRMR;
import problems.motap.solutionGenerator.MOTAPRandomSG;
import problems.motap.solutionGenerator.RandomLoadBalancedSG;
import problems.pcb.PCBProblem;
import problems.pcb.mutation.GuidedRandomRemoveReinsert;
import problems.pcb.mutation.GuidedTwoOpt;
import problems.pcb.terminalcondition.PCBProblemTC;
import util.random.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 22.06.2015.
 */
public class EAService {

    private static String PARAMS_DELIMITER = "|";
    public static String OBJECT_DELIMITER = ";";

    static String extractName(String nameStr)
    {
        int delimiterIndex = nameStr.indexOf(PARAMS_DELIMITER);
        if (delimiterIndex<0)
            return nameStr;
        else
            return nameStr.substring(0,delimiterIndex);
    }

    static String[] extractParams(String nameStr)
    {
        int delimiterIndex = nameStr.indexOf(PARAMS_DELIMITER);
        if (delimiterIndex<0)
            return null;
        else
            return nameStr.substring(delimiterIndex+1).split("\\"+PARAMS_DELIMITER);
    }


    public static class MutationOperators {
        private static final int DEFAULT_MUTATION_COUNT = 1;
        private static final double DEFAULT_GREED = 0.3;

        public static final String AdjacentInterchange          = "adjacentinterchange";
        public static final String RandomInterchange            = "randominterchange";
        public static final String MoveSingleTerm               = "movesingleterm";
        public static final String MoveSubSequence              = "movesubsequence";
        public static final String ReverseSubSequence           = "reversesubsequence";
        public static final String ReverseAndOrMoveSubSequence  = "reverseandormovesubsequence";
        public static final String SwapSubSequences             = "swapsubsequences";
        public static final String ReverseAndSwapSubSequences   = "reverseandswapsubsequences";
        public static final String TwoOpt                       = "twoopt";
        public static final String GuidedTwoOpt                 = "guidedtwoopt";
        public static final String RandomRemoveReinsert         = "randomremovereinsert";
        public static final String GuidedRandomRemoveReinsert   = "guidedrandomremovereinsert";

        // Mutations specific for MOTAP Problem. Do not use for other problems
        public static final String SimpleMotap              = "simplemotap";
        public static final String ExecutionGreedy          = "executiongreedy";
        public static final String CommunicationGreedy      = "communicationgreedy";
        public static final String ProcessorReleasing       = "processorreleasing";
        public static final String GreedySwap               = "greedyswap";
        public static final String GreedySwapWithExchange   = "greedyswapwithexchange";
        public static final String GreedySearch             = "greedysearch";
        public static final String GrMr                     = "grmr";

        public static MutationOperator createMutationOperator(String moName, OptimizationProblem problem) {

            String name = extractName(moName);
            String params[] = extractParams(moName);

            if (name.equals(AdjacentInterchange))
            {
                return new AdjacentInterchange();
            }
            else if (name.equals(RandomInterchange))
            {
                return new RandomInterchange();
            }
            else if (name.equals(MoveSingleTerm)) {
                if (params == null)
                    return new MoveSingleTerm();
                else
                    return new MoveSingleTerm(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
            }
            else if (name.equals(MoveSubSequence)) {
                if (params == null)
                    return new MoveSubSequence();
                else
                    return new MoveSubSequence(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
            }
            else             if (name.equals(ReverseSubSequence)) {
                if (params == null)
                    return new ReverseSubSequence();
                else
                    return new ReverseSubSequence(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
            }
            else if (name.equals(ReverseAndOrMoveSubSequence)) {
                if (params == null)
                    return new ReverseAndOrMoveSubSequence();
                else
                    return new ReverseAndOrMoveSubSequence(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
            }
            else if (name.equals(SwapSubSequences)) {
                if (params == null)
                    return new SwapSubSequences(false);
                else
                    return new SwapSubSequences(Integer.parseInt(params[0]), Integer.parseInt(params[1]),false);
            }
            else if (name.equals(ReverseAndSwapSubSequences)) {
                if (params == null)
                    return new SwapSubSequences(true);
                else
                    return new SwapSubSequences(Integer.parseInt(params[0]), Integer.parseInt(params[1]),true);
            }
            else if (name.equals(TwoOpt)) {
                return new TwoOpt();
            }
            else if (name.equals(GuidedTwoOpt)) {
                return new GuidedTwoOpt(((PCBProblem)problem).getData().getGuidedMap());
            }
            else if (name.equals(RandomRemoveReinsert)) {
                return new RandomRemoveReinsert();
            }
            else if (name.equals(GuidedRandomRemoveReinsert)) {
                return new GuidedRandomRemoveReinsert(((PCBProblem)problem).getData().getGuidedMap());
            }
            else             if (name.equals(SimpleMotap))
            {
                return new SimpleMOTAPMutation(DEFAULT_MUTATION_COUNT);
            }
            else if (name.equals(ExecutionGreedy))
            {
                if (params == null)
                    return new ExecutionGreedySwap(DEFAULT_MUTATION_COUNT, DEFAULT_GREED);
                else
                    return new ExecutionGreedySwap(Integer.parseInt(params[0]), Double.parseDouble(params[1]));
            }
            else if (name.equals(CommunicationGreedy)) {
                if (params == null)
                    return new CommunicationGreedySwap(DEFAULT_MUTATION_COUNT, DEFAULT_GREED);
                else
                    return new CommunicationGreedySwap(Integer.parseInt(params[0]), Double.parseDouble(params[1]));
            }
            else if (name.equals(ProcessorReleasing)) {
                if (params == null)
                    return new ProcessorRelaxingMutation(DEFAULT_MUTATION_COUNT,DEFAULT_GREED);
                else
                    return new ProcessorRelaxingMutation(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
            }
            else if (name.equals(GreedySwap)) {
                if (params == null)
                    return new GreedySwap(DEFAULT_MUTATION_COUNT);
                else
                    return new GreedySwap(Integer.parseInt(params[0]));
            }
            else if (name.equals(GreedySwapWithExchange)) {
                if (params == null)
                    return new GreedySwapWithEscape();
                else
                    return new GreedySwapWithEscape(Integer.parseInt(params[0]));
            }
            else if (name.equals(GrMr)) {
                return new GRMR();

            }
            else if (name.equals(GreedySearch)) {
                return new GreedySearch();

            }
            else throw new UnknownObjectType("Unknown Mutation:"+name);
        }

        public static List<MutationOperator> createMutationOperators(String moName, OptimizationProblem problem) {

            String moNames[] = moName.split(OBJECT_DELIMITER);

            List<MutationOperator> moList = new ArrayList<>();
            for (int i=0;i<moNames.length;i++)
            {
                moList.add(createMutationOperator(moNames[i],problem));
            }

            return moList;

        }
    }

    public static class MutationStrategies{
        public static final String Simple = "simple";
        public static final String Simple_02_1 = "simple"+PARAMS_DELIMITER+0.2+PARAMS_DELIMITER+1;
        public static final String Simple_04_1 = "simple"+PARAMS_DELIMITER+0.4+PARAMS_DELIMITER+1;
        public static final String Simple_06_1 = "simple"+PARAMS_DELIMITER+0.6+PARAMS_DELIMITER+1;

        public static MutationStrategy createMutationStrategy(String msName, OptimizationProblem problem) {
            String name = extractName(msName);
            String[] params = extractParams(msName);


            if (name.equals(Simple)) {
                if (params.length ==1)
                    return new SimpleMutationStrategy(Double.parseDouble(params[0]));
                else if (params.length==2)
                  return new SimpleMutationStrategy(Double.parseDouble(params[0]), Integer.parseInt(params[1]));
                else throw new UnknownObjectType("Invalid Mutation Strategy Params:"+name);
            }
            else throw new UnknownObjectType("Unknown Mutation Strategy:"+name);
        }
    }

    public static class CrossOverOperators {

        public static final String OneCut = "onecut";
        public static final String PTLCut = "ptlcut";
        public static final String Simple = "simple";
        public static final String SimpleMOTAP = "simplemotap";

        public static CrossOverOperator createCrossOverOperator(String coName, OptimizationProblem problem) {
            String name = extractName(coName);
            String[] params = extractParams(coName);


            if (name.equals(OneCut))
                return new OneCutCrossover();
            else if (name.equals(PTLCut))
                return new PTLCutCrossover();
            else if (name.equals(Simple))
                return new SimplePermutationCrossover();
            else if (name.equals(SimpleMOTAP))
                return new SimpleMOTACrossOver();

            else throw new UnknownObjectType("Unknown CrossOver:"+name);
        }
        public static List<CrossOverOperator> createCrossOverOperators(String coName, OptimizationProblem problem) {

            String coNames[] = coName.split(OBJECT_DELIMITER);

            List<CrossOverOperator> coList = new ArrayList<>();
            for (int i=0;i<coNames.length;i++)
            {
                coList.add(createCrossOverOperator(coNames[i], problem));
            }

            return coList;
        }
    }


    public static class CrossOverStrategies {
        private static final String CrossPair   = "crosspair";
        private static final String Simple      = "simple";
        public static final String Simple_02 = "simple"+PARAMS_DELIMITER+0.2;
        public static final String Simple_04 = "simple"+PARAMS_DELIMITER+0.4;
        public static final String Simple_06 = "simple"+PARAMS_DELIMITER+0.6;
        public static final String Simple_08 = "simple"+PARAMS_DELIMITER+0.8;


        public static CrossOverStrategy createCrossOverStrategy(String csName, OptimizationProblem problem) {
            String name = extractName(csName);
            String[] params = extractParams(csName);

            if (name.equals(Simple))
                return new SimpleCrossOverStrategy(Double.parseDouble(params[0]));
            else throw new UnknownObjectType("Unknown CrossOverStrategy:"+name);
        }
    }

    public static class VictimSelectors {
        public static String Simple = "simple";
        public static String RouletteWheel = "roulettewheel";

        public static VictimSelector createVictimSelector(String vsName, OptimizationProblem problem) {
            String name = extractName(vsName);
            String[] params = extractParams(vsName);

            if (name.equals(Simple))
                return new SimpleVictimSelector();
            else if (name.equals(RouletteWheel))
                return new RouletteWheelVictimSelector();
            else throw new UnknownObjectType("Unknown Victim Selector:"+name);
        }
    }

    public static class Perturbators {
        public static String MotapRandomPerturbator = "motaprandomperturbator";


        public static Perturbator createPerturbator(String pName, OptimizationProblem problem) {
            String name = extractName(pName);
            String[] params = extractParams(pName);

            if (name.equals(MotapRandomPerturbator))
                return new MOTAPRandomPerturbator(Double.parseDouble(params[0]));
            else throw new UnknownObjectType("Unknown Victim Selector:"+name);
        }
    }

    public static class TerminalConditions {

        public static final String IterationBased   = "iterationbased";
        public static final String NeighboringBased = "neighboringbased";
        public static final String MotapNeighboringBased = "motapneighboringbased";
        public static final String CostBased =      "costbased";
        public static final String PCBProblem       = "pcbproblem";
        public static final String CPUTimeBased ="cputimebased";
        public static final String MotapBased = "motapbased";

        public static TerminalCondition createTerminalCondition(String tcName, OptimizationProblem problem) {

            String name = extractName(tcName);
            String[] params = extractParams(tcName);


            if (name.equals(IterationBased))
                return new IterationBasedTC(Long.parseLong(params[0]));
            else if (name.equals(NeighboringBased))
                return new NeighboringBasedTC(Long.parseLong(params[0]));
            else if (name.equals(MotapNeighboringBased))
                return new MotapNeighboringBasedTC(Long.parseLong(params[0]));
            else if (name.equals(CPUTimeBased))
                return new CPUTimeBasedTC(Long.parseLong(params[0]));
            else if (name.equals(CostBased))
                return new CostBasedTC(Double.parseDouble(params[0]));
            else if (name.equals(PCBProblem))
                return new PCBProblemTC((problems.pcb.PCBProblem)problem);
            else if (name.equals(MotapBased))
                return new MotapBasedTc((MOTAProblem) problem,Integer.parseInt(params[0]));

            else throw new UnknownObjectType("Unknown Terminal Condition:"+name);
        }
    }


    public static class ParentSelectors {
        public static final String CostBased = "costbased";
        public static final String RouletteWheel = "roulettewheel";
        public static final String Tournament = "Tournament";

        public static ParentSelector createParentSelector(String psName, OptimizationProblem problem)
        {
            String name = extractName(psName);
            String[] params = extractParams(psName);

            if (name.equals(CostBased)) {
                if (params == null)
                    return new CostBasedParentSelector();
                else return new CostBasedParentSelector(Integer.parseInt(params[0]));

            } else  if (name.equals(RouletteWheel))
            {
                if (params == null)
                    return new RouletteWheelParentSelector();
                else return new RouletteWheelParentSelector(Integer.parseInt(params[0]));
            }
            else if (name.equals(Tournament)) {
                if (params == null)
                    return new TournamentParentSelector();
                else if (params.length==1)
                    return new TournamentParentSelector(Integer.parseInt(params[0]));
                else return new TournamentParentSelector(Integer.parseInt(params[0]),Integer.parseInt(params[1]));
            }
            else throw new UnknownObjectType("Unknown Parent Selector:"+name);

        }

    }


    public static class RNGs {

        public static final String SecureRandom = "securerandom";
        public static final String MersenneTwister = "mersennetwister";
        public static final String Lozi = "lozi";
        public static final String Burgers = "burgers";
        public static final String Arnold = "arnold";
        public static final String Tent = "tent";
        public static final String Sinai = "sinai";
        public static final String Logistic = "logistic";

        public static RNG createRNG(String rngName, OptimizationProblem problem) {
            String name = extractName(rngName);
            String[] params = extractParams(rngName);


            if (name.equals(Lozi))
                return new ChaosRNG(new LoziCPRNG());
            else if (name.equals(Burgers))
                return new ChaosRNG(new BurgersCPRNG());
            else if (name.equals(Arnold))
                return new ChaosRNG(new ArnoldCatCPRNG());
            else if (name.equals(Tent))
                return new ChaosRNG(new TentCPRNG());
            else if (name.equals(SecureRandom))
                return new SecureRandomRNG();
            else if (name.equals(MersenneTwister))
                return new MersenneTwisterRNG();
            else if (name.equals(Sinai))
                return new ChaosRNG(new SinaiCPRNG());
            else if (name.equals(Logistic))
                return new ChaosRNG(new DelayedLogisticCPRNG());

            else throw new UnknownObjectType("Unknown CrossOver:"+name);
        }
        public static List<RNG> createRNGs(String rngName, OptimizationProblem problem) {

            String coNames[] = rngName.split(OBJECT_DELIMITER);

            List<RNG> rngList = new ArrayList<>();
            for (int i=0;i<coNames.length;i++)
            {
                rngList.add(createRNG(coNames[i], problem));
            }

            return rngList;
        }
    }


    public static class InitialSolutionGenerators {
        public static String MOTAPRandomSG = "motaprandomsg";
        public static String RandomLoadBalancedSG = "randomloadbalanced";

        public static InitialSolutionGenerator createSG(String sgName, OptimizationProblem problem) {
            String name = extractName(sgName);
            String[] params = extractParams(sgName);


            if (name.equals(MOTAPRandomSG))
                return new MOTAPRandomSG();
            else if (name.equals(RandomLoadBalancedSG))
                return new RandomLoadBalancedSG();
            else throw new UnknownObjectType("Unknown CrossOver:"+name);
        }
        public static List<InitialSolutionGenerator> createSGs(String rngName, OptimizationProblem problem) {

            String coNames[] = rngName.split(OBJECT_DELIMITER);

            List<InitialSolutionGenerator> sgList = new ArrayList<>();
            for (int i=0;i<coNames.length;i++)
            {
                sgList.add(createSG(coNames[i], problem));
            }

            return sgList;
        }
    }
}
