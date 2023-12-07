/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package experiments;

import metaheuristic.MetaHeuristicService;
import metaheuristic.ea.EAService;
import metaheuristic.pso.PSOService;
import metaheuristic.tabu.TabuService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Dindar
 */
public class TestCaseGenerator {

    private static final int repeat = 1;  // Repetition count


    // Comment out the algorithms that you dont want to include
    public static List<ITestCaseGenerator> MetaHeuristics = Arrays.asList(  new MBOCases(),
                                                                            new ABCCases(),
                                                                            new SACases(),
                                                                            new EACases(),
                                                                            new DPSOCases(),
                                                                            new DDECases(),
                                                                            new TabuCases(),
                                                                            new SOSCases(),
                                                                            new PSOCases()
    );


    private interface ITestCaseGenerator{
        public  void generateCases(BufferedWriter writer, String prefix) throws IOException;
    }

    public static class MBOCases implements ITestCaseGenerator{
        public static String MBO_NF[] = {"twoopt"};    /*"adjacentinterchange", "randominterchange", "movesingleterm", "movesubsequence",
                                                                "reversesubsequence", "reverseandormovesubsequence", "swapsubsequences",
                                                                "reverseandswapsubsequences", "twoopt", "randomremoveandreinsert"*/


        public static String mbo_n[] = {"25"};   // "13", "25", "51"
        public static String mbo_m[] = {"5"};    // "5", "10", "20"
        public static int mbo_p[] = {5};   // 3, 5, 7
        public static int mbo_x[] = {1};   // 1, 2, 3

        public void generateCases(BufferedWriter writer, String  prefix ) throws IOException {

            for(int l=0; l< MBO_NF.length; l++){
                for(int m=0; m< mbo_n.length; m++){
                    for(int n=0; n< mbo_m.length; n++){
                        for(int o=0; o< mbo_p.length; o++){
                            for(int p=0; p< mbo_x.length; p++){
                                if(2* mbo_x[p]+1 <= mbo_p[o]) {
                                    writeRepeated(writer, repeat,  prefix+"\t"+
                                                                MetaHeuristicService.M_MBO + "\t"+
                                                                MBO_NF[l]+"\t"+
                                                                mbo_n[m]+"\t"+
                                                                mbo_m[n]+"\t"+
                                                                mbo_p[o]+"\t"+
                                                                mbo_x[p]+"\n");
                                }
                            }
                        }
                    }
                }
            }

        }

    }

    public static class SACases implements ITestCaseGenerator{
        private static final String SA_NF[] = {"twoopt"};    /*"adjacentinterchange", "randominterchange", "movesingleterm", "movesubsequence",
                                                             "reversesubsequence", "reverseandormovesubsequence", "swapsubsequences",
                                                             "reverseandswapsubsequences", "twoopt", "randomremoveandreinsert"*/

        private static final String sa_T[] = {"100"};   // "100", "1000"
        private static final String sa_R_initials[] = {"5"};  // "5", "20"
        private static final String sa_a[] = {"1.5"};    // "1.1", "1.5"
        private static final String sa_b[] = {"1.5"};    // "1.1", "1.5"

        public void generateCases(BufferedWriter writer,String  prefix) throws IOException {
            for(int l=0; l<SA_NF.length; l++){
                for(int m=0; m<sa_T.length; m++){
                    for(int n=0; n<sa_R_initials.length; n++){
                        for(int o=0; o<sa_a.length; o++){
                            for(int p=0; p<sa_b.length; p++){
                                writeRepeated(writer, repeat,   prefix+"\t"+
                                                            MetaHeuristicService.M_SA + "\t"+
                                                            SA_NF[l]+"\t"+
                                                            sa_T[m]+"\t"+
                                                            sa_R_initials[n]+"\t"+
                                                            sa_a[o]+"\t"+
                                                            sa_b[p]+ "\n");
                            }
                        }
                    }
                }
            }
        }

    }

    /*  ABC  Test Cases  */
    public static class ABCCases implements ITestCaseGenerator{
        private static final String ABC_NF[] = {"twoopt"};    /*"adjacentinterchange", "randominterchange", "movesingleterm", "movesubsequence",
                                                             "reversesubsequence", "reverseandormovesubsequence", "swapsubsequences",
                                                             "reverseandswapsubsequences", "twoopt", "randomremoveandreinsert"*/

        private static final String abc_CS[] = {"30"};  // "10", "20", "30"
        private static final String abc_limitDenom[] = {"2"};  // "2", "3"


        public void generateCases(BufferedWriter writer,String  prefix) throws IOException {
            for(int l=0; l<ABC_NF.length; l++){
                for(int m=0; m<abc_CS.length; m++){
                    for(int n=0; n<abc_limitDenom.length; n++){
                        writeRepeated(writer, repeat,   prefix+"\t"+
                                                    MetaHeuristicService.M_ABC + "\t"+
                                                    ABC_NF[l]+"\t"+
                                                    abc_CS[m]+"\t"+
                                                    abc_limitDenom[n]+"\n");

                    }
                }
            }
        }
    }

    /*  EA  Test Cases  */
    public static class EACases implements ITestCaseGenerator{

        private static final String[] mutationOperators = {EAService.MutationOperators.ReverseSubSequence};
        private static final String[] mutationStrategies = {EAService.MutationStrategies.Simple_02_1};

        private static final String[] crossOverOperators = {EAService.CrossOverOperators.OneCut};
        private static final String[] crossOverStrategies = {EAService.CrossOverStrategies.Simple_02};

        private static final String[] victimSelectors = {EAService.VictimSelectors.RouletteWheel};
        private static final String[] parentSelectors = {EAService.ParentSelectors.CostBased, EAService.ParentSelectors.RouletteWheel, EAService.ParentSelectors.Tournament+addParams(5)};

        private static final String[] terminalConditions = {EAService.TerminalConditions.PCBProblem};

        private static final String[] initialPopulationSizes = {"100"};

        public void generateCases(BufferedWriter writer,String prefix) throws IOException {
            for(int mo=0; mo<mutationOperators.length; mo++){
                for(int ms=0; ms<mutationStrategies.length; ms++){
                    for(int co=0; co<crossOverOperators.length; co++){
                        for(int cs=0; cs<crossOverStrategies.length; cs++){
                            for(int vs=0; vs<victimSelectors.length; vs++){
                                for(int ps=0; ps<parentSelectors.length; ps++){
                                    for(int tc=0; tc<terminalConditions.length; tc++){
                                        for(int ips=0; ips<initialPopulationSizes.length; ips++){
                                            writeRepeated(writer, repeat,   prefix+"\t"+
                                                                        MetaHeuristicService.M_EA + "\t"+
                                                                        mutationOperators[mo]+"\t"+
                                                                        mutationStrategies[ms]+"\t"+
                                                                        crossOverOperators[co]+"\t"+
                                                                        crossOverStrategies[cs]+"\t"+
                                                                        victimSelectors[vs]+"\t"+
                                                                        parentSelectors[ps]+"\t"+
                                                                        terminalConditions[tc]+"\t"+
                                                                        initialPopulationSizes[ips]+"\n");

                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }


    }


    /*  TABU-SEARCH  Test Cases  */
    public static class TabuCases implements ITestCaseGenerator{

        private static final String[] neighborhoodFunctions = {EAService.MutationOperators.ReverseSubSequence};

        private static final String[] tabuLists = {TabuService.TabuLists.SolutionMemory + addParams(5)};

        private static final String[] aspirations= {null};

        private static final String[] terminalConditions = {EAService.TerminalConditions.PCBProblem};

        private static final String[] neighborhoodSizes = {"10"};

        public void generateCases(BufferedWriter writer,String prefix) throws IOException {
            for(int nf=0; nf<neighborhoodFunctions.length; nf++){
                for(int tl=0; tl<tabuLists.length; tl++){
                    for(int a=0; a<aspirations.length; a++){
                        for(int tc=0; tc<terminalConditions.length; tc++){
                            for(int ns=0; ns<neighborhoodSizes.length; ns++){
                                writeRepeated(writer, repeat,   prefix+"\t"+
                                        MetaHeuristicService.M_TABU+"\t"+
                                        neighborhoodFunctions[nf]+"\t"+
                                        tabuLists[tl]+"\t"+
                                        aspirations[a]+"\t"+
                                        neighborhoodSizes[ns]+"\t"+
                                        terminalConditions[tc]+"\n");
                            }
                        }
                    }
                }
            }
        }
    }

    /*  DPSO  Test Cases  */
    public static class DPSOCases implements ITestCaseGenerator{
        private static final String[] Ws = {"0.6"};

        private static final String[] siPs= {"0.6"};

        private static final String[] siGs= {"0.6"};

        private static final String[] swarmSizes = {"30"};

        private static final String[] terminalConditions = {EAService.TerminalConditions.PCBProblem};

        public void generateCases(BufferedWriter writer,String prefix) throws IOException {
            for(int w=0; w<Ws.length; w++){
                for(int siP=0; siP<siPs.length; siP++){
                    for(int siG=0; siG<siGs.length; siG++){
                        for(int ss=0; ss<swarmSizes.length; ss++){
                            for(int tc=0; tc<terminalConditions.length; tc++){
                                writeRepeated(writer, repeat,   prefix+"\t"+
                                        MetaHeuristicService.M_DPSO+"\t"+
                                        Ws[w]+"\t"+
                                        siPs[siP]+"\t"+
                                        siGs[siG]+"\t"+
                                        swarmSizes[ss]+"\t"+
                                        terminalConditions[tc]+"\n");
                            }
                        }
                    }
                }
            }
        }
    }

    /*  DDE  Test Cases  */
    public static class DDECases implements ITestCaseGenerator{
        private static final String[] initialPopulationSizes = {"50"};

        private static final String[] mutationProbabilities= {"0.5"};

        private static final String[] crossOverProbabilities = {"0.5"};

        private static final String[] mutationRepetitions = {"3"};

        private static final String[] terminalConditions = {EAService.TerminalConditions.PCBProblem};

        public void generateCases(BufferedWriter writer,String prefix) throws IOException {
            for(int ips=0; ips<initialPopulationSizes.length; ips++){
                for(int mp=0; mp<mutationProbabilities.length; mp++){
                    for(int cop=0; cop<crossOverProbabilities.length; cop++){
                        for(int mr=0; mr<mutationRepetitions.length; mr++){
                            for(int tc=0; tc<terminalConditions.length; tc++){
                                writeRepeated(writer, repeat,   prefix+"\t"+
                                        MetaHeuristicService.M_DDE+"\t"+
                                        initialPopulationSizes[ips]+"\t"+
                                        mutationProbabilities[mp]+"\t"+
                                        crossOverProbabilities[cop]+"\t"+
                                        mutationRepetitions[mr]+"\t"+
                                        terminalConditions[tc]+"\n");
                            }
                        }
                    }
                }
            }
        }
    }

    /*  SOS  Test Cases  */
    public static class SOSCases implements ITestCaseGenerator{

        private static final String[] mutationOperators = {EAService.MutationOperators.GuidedTwoOpt};
        private static final String[] mutationStrategies = {EAService.MutationStrategies.Simple_02_1};

        private static final String[] crossOverOperators = {EAService.CrossOverOperators.OneCut};
        private static final String[] crossOverStrategies = {EAService.CrossOverStrategies.Simple_02};

        private static final String[] victimSelectors = {EAService.VictimSelectors.Simple};
        private static final String[] parentSelectors = {EAService.ParentSelectors.CostBased};

        private static final String[] terminalConditions = {EAService.TerminalConditions.PCBProblem};

        private static final String[] initialPopulationSizes = {"100"};
        private static final String[] forkIntervals = {"10"};
        private static final String[] childDiameters = {"0.4"};
        private static final String[] minChildPopulations = {"10"};
        private static final String[] maxChildPopulations = {"20"};
        private static final String[] maxChildrens = {"5"};
        private static final String[] minParentPopulations = {"20"};
        private static final String[] totalPopulations = {"100"};

        public void generateCases(BufferedWriter writer,String prefix) throws IOException {
            for(int mo=0; mo<mutationOperators.length; mo++){
                for(int ms=0; ms<mutationStrategies.length; ms++){
                    for(int co=0; co<crossOverOperators.length; co++){
                        for(int cs=0; cs<crossOverStrategies.length; cs++){
                            for(int vs=0; vs<victimSelectors.length; vs++){
                                for(int ps=0; ps<parentSelectors.length; ps++){
                                    for(int tc=0; tc<terminalConditions.length; tc++){
                                        for(int ips=0; ips<initialPopulationSizes.length; ips++){
                                            for(int fi=0; fi<forkIntervals.length; fi++){
                                                for(int cd=0; cd<childDiameters.length; cd++){
                                                    for(int mincp=0; mincp<minChildPopulations.length; mincp++){
                                                        for(int maxcp=0; maxcp<maxChildPopulations.length; maxcp++){
                                                            for(int maxc=0; maxc<maxChildrens.length; maxc++){
                                                                for(int minpp=0; minpp<minParentPopulations.length; minpp++){
                                                                    for(int tp=0; tp<totalPopulations.length; tp++){
                                                                        writeRepeated(writer, repeat,   prefix+"\t"+
                                                                                                    MetaHeuristicService.M_EA + "\t"+
                                                                                                    mutationOperators[mo]+"\t"+
                                                                                                    mutationStrategies[ms]+"\t"+
                                                                                                    crossOverOperators[co]+"\t"+
                                                                                                    crossOverStrategies[cs]+"\t"+
                                                                                                    victimSelectors[vs]+"\t"+
                                                                                                    parentSelectors[ps]+"\t"+
                                                                                                    terminalConditions[tc]+"\t"+
                                                                                                    initialPopulationSizes[ips]+"\t"+
                                                                                                    forkIntervals[fi]+"\t"+
                                                                                                    childDiameters[cd]+"\t"+
                                                                                                    minChildPopulations[mincp]+"\t"+
                                                                                                    maxChildPopulations[maxcp]+"\t"+
                                                                                                    maxChildrens[maxc]+"\t"+
                                                                                                    minParentPopulations[minpp]+"\t"+
                                                                                                    totalPopulations[tp]+"\n");
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /*  PSO  Test Cases  */
    public static class PSOCases implements ITestCaseGenerator{

        private static final String[] particleHandlers = {PSOService.ParticleHandlers.IntegerPermutation};

        private static final String[] Ws = {"0.8"};

        private static final String[] siPs= {"0.5"};

        private static final String[] siGs= {"0.9"};

        private static final String[] swarmSizes = {"100"};

        private static final String[] terminalConditions = {EAService.TerminalConditions.PCBProblem};

        public void generateCases(BufferedWriter writer,String prefix) throws IOException {
            for(int ph=0; ph<particleHandlers.length; ph++){
                for(int w=0; w<Ws.length; w++){
                    for(int siP=0; siP<siPs.length; siP++){
                        for(int siG=0; siG<siGs.length; siG++){
                            for(int ss=0; ss<swarmSizes.length; ss++){
                                for(int tc=0; tc<terminalConditions.length; tc++){
                                    writeRepeated(writer, repeat,   prefix+"\t"+
                                                                MetaHeuristicService.M_PSO+"\t"+
                                                                particleHandlers[ph]+"\t"+
                                                                Ws[w]+"\t"+
                                                                siPs[siP]+"\t"+
                                                                siGs[siG]+"\t"+
                                                                swarmSizes[ss]+"\t"+
                                                                terminalConditions[tc]+"\n");
                                }
                            }
                        }
                    }
                }

            }
        }
    }


    public static String addParams(Object ...params )
    {
        String s = "";
        for (Object param:params)
            s += TabuService.PARAMS_DELIMITER + param;

        return s;
    }

    private static void writeRepeated(BufferedWriter writer, int run, String s) throws IOException {
        for (int i =0;i<run;i++ )
        {
            writer.write(s);
        }
    }


    public static void generateTestCases(BufferedWriter writer, String prefix) throws IOException {
        for (ITestCaseGenerator generator:MetaHeuristics)
        {
            generator.generateCases(writer,prefix);
        }
    }

}
