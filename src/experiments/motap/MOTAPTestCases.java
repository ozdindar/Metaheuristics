/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package experiments.motap;

import metaheuristic.MetaHeuristicService;
import metaheuristic.ea.EAService;
import metaheuristic.tabu.TabuService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Dindar
 */
public class MOTAPTestCases {

    private static final int repeat = 1;  // Repetition count

    private static final int NeighboringCount = 30000;
    private static final long CPUTime= 10000;

    // Comment out the algorithms that you dont want to include
    public static List<ITestCaseGenerator> MetaHeuristics = Arrays.asList( new EACases(), new MBOCases(), new PMBOCases(), new IslandEACases(), new IslandMBOCases());


    public interface ITestCaseGenerator{
        public  void generateCases(BufferedWriter writer, File[] files) throws IOException;
    }


    /*  EA  Test Cases  */
    public static class EACases implements ITestCaseGenerator{

        private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.MOTAPRandomSG};

        private static final String[] mutationOperators = {EAService.MutationOperators.SimpleMotap};
        private static final String[] mutationStrategies = {EAService.MutationStrategies.Simple_02_1};

        private static final String[] crossOverOperators = {EAService.CrossOverOperators.SimpleMOTAP};
        private static final String[] crossOverStrategies = {EAService.CrossOverStrategies.Simple_08};

        private static final String[] victimSelectors = {EAService.VictimSelectors.Simple};
        private static final String[] parentSelectors = { EAService.ParentSelectors.RouletteWheel};


        private static final String[] terminalConditions = {EAService.TerminalConditions.CPUTimeBased+addParams(CPUTime)};

        private static final String[] initialPopulationSizes = {"100"};

        public void generateCases(BufferedWriter writer,File[] files) throws IOException {

            for (int isg =0; isg<solutionGenerators.length;isg++) {
                for (int mo = 0; mo < mutationOperators.length; mo++) {
                    for (int ms = 0; ms < mutationStrategies.length; ms++) {
                        for (int co = 0; co < crossOverOperators.length; co++) {
                            for (int cs = 0; cs < crossOverStrategies.length; cs++) {
                                for (int vs = 0; vs < victimSelectors.length; vs++) {
                                    for (int ps = 0; ps < parentSelectors.length; ps++) {
                                        for (int tc = 0; tc < terminalConditions.length; tc++) {
                                            for (int ips = 0; ips < initialPopulationSizes.length; ips++) {
                                                    for (File file:files) {
                                                        String prefix = file.getName();

                                                        if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
                                                            continue;
                                                        writeRepeated(writer, repeat, prefix + "\t" +
                                                                MetaHeuristicService.M_EA + "\t" +
                                                                solutionGenerators[isg] + "\t" +
                                                                mutationOperators[mo] + "\t" +
                                                                mutationStrategies[ms] + "\t" +
                                                                crossOverOperators[co] + "\t" +
                                                                crossOverStrategies[cs] + "\t" +
                                                                victimSelectors[vs] + "\t" +
                                                                parentSelectors[ps] + "\t" +
                                                                terminalConditions[tc] + "\t" +
                                                                initialPopulationSizes[ips] + "\n");

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

    /*  EA  Test Cases  */
    public static class IslandEACases implements ITestCaseGenerator{

        private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.MOTAPRandomSG};

        private static final String[] mutationOperators = {EAService.MutationOperators.SimpleMotap};
        private static final String[] mutationStrategies = {EAService.MutationStrategies.Simple_02_1};

        private static final String[] crossOverOperators = {EAService.CrossOverOperators.SimpleMOTAP};
        private static final String[] crossOverStrategies = {EAService.CrossOverStrategies.Simple_08};

        private static final String[] victimSelectors = {EAService.VictimSelectors.Simple};
        private static final String[] parentSelectors = { EAService.ParentSelectors.RouletteWheel};


        private static final String[] terminalConditions = {EAService.TerminalConditions.CPUTimeBased+addParams(CPUTime)};

        private static final int[] initialPopulationSizes = {100};

        private static final int[] populationCounts = {8};

        private static final int[] immigrationPeriods = {8};
        private static final int[] immigrantCounts  =   {8};

        private static final int[] poolSizes  =   {8};


        public void generateCases(BufferedWriter writer,File[] files) throws IOException {

            for (int isg =0; isg<solutionGenerators.length;isg++) {
                for (int mo = 0; mo < mutationOperators.length; mo++) {
                    for (int ms = 0; ms < mutationStrategies.length; ms++) {
                        for (int co = 0; co < crossOverOperators.length; co++) {
                            for (int cs = 0; cs < crossOverStrategies.length; cs++) {
                                for (int vs = 0; vs < victimSelectors.length; vs++) {
                                    for (int ps = 0; ps < parentSelectors.length; ps++) {
                                        for (int tc = 0; tc < terminalConditions.length; tc++) {
                                            for (int ips = 0; ips < initialPopulationSizes.length; ips++) {
                                                for (int pc = 0; pc < populationCounts.length; pc++) {
                                                    for (int ip = 0; ip < immigrationPeriods.length; ip++) {
                                                        for (int ic = 0; ic < immigrantCounts.length; ic++) {
                                                            for (int pools = 0; pools < poolSizes.length; pools++) {
                                                                for (File file:files) {
                                                                    String prefix = file.getName();

                                                                    if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
                                                                        continue;
                                                                    writeRepeated(writer, repeat, prefix + "\t" +
                                                                            MetaHeuristicService.M_EA + "\t" +
                                                                            solutionGenerators[isg] + "\t" +
                                                                            mutationOperators[mo] + "\t" +
                                                                            mutationStrategies[ms] + "\t" +
                                                                            crossOverOperators[co] + "\t" +
                                                                            crossOverStrategies[cs] + "\t" +
                                                                            victimSelectors[vs] + "\t" +
                                                                            parentSelectors[ps] + "\t" +
                                                                            terminalConditions[tc] + "\t" +
                                                                            initialPopulationSizes[ips] + "\t"+
                                                                            populationCounts[pc] + "\t" +
                                                                            immigrationPeriods[ip] + "\t" +
                                                                            immigrantCounts[ic] + "\t" +
                                                                            poolSizes[pools] +"\n");

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

    public static class MBOCases implements ITestCaseGenerator{
        public static String MBO_NF[] = { EAService.MutationOperators.SimpleMotap };

        private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.MOTAPRandomSG};

        private static final String[] terminalConditions = {EAService.TerminalConditions.CPUTimeBased+addParams(CPUTime)};

        public static int nPopulations[] = {51};   // "13", "25", "51"
        public static int nTours[] = {10};    // "5", "10", "20"
        public static int nNeighborSolutions[] = {3};   // 3, 5, 7
        public static int nSharedNSolutions[] = {1};   // 1, 2, 3
        public static String nRNG[] = {EAService.RNGs.MersenneTwister};//{EAService.RNGs.MersenneTwister, EAService.RNGs.Arnold, EAService.RNGs.Burgers, EAService.RNGs.Lozi, EAService.RNGs.Tent};

        public void generateCases(BufferedWriter writer, File[]  files ) throws IOException {

            for (int isg = 0; isg < solutionGenerators.length; isg++) {
                for (int l = 0; l < MBO_NF.length; l++) {
                    for (int tc = 0; tc < terminalConditions.length; tc++) {
                        for (int p = 0; p < nPopulations.length; p++) {
                            for (int t = 0; t < nTours.length; t++) {
                                for (int ns = 0; ns < nNeighborSolutions.length; ns++) {
                                    for (int ss = 0; ss < nSharedNSolutions.length; ss++) {
                                        for (int r = 0; r < nRNG.length; r++) {
                                            for (File file:files) {
                                                String prefix = file.getName();

                                                if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
                                                    continue;
                                                writeRepeated(writer, repeat, prefix + "\t" +
                                                        MetaHeuristicService.M_MBO + "\t" +
                                                        solutionGenerators[isg] + "\t" +
                                                        MBO_NF[l] + "\t" +
                                                        terminalConditions[tc] + "\t" +
                                                        nPopulations[p] + "\t" +
                                                        nTours[t] + "\t" +
                                                        nNeighborSolutions[ns] + "\t" +
                                                        nSharedNSolutions[ss] + "\t" +
                                                        nRNG[r] + "\n");
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

    public static class PMBOCases implements ITestCaseGenerator{
        public static String MBO_NF[] = { EAService.MutationOperators.SimpleMotap };

        private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.MOTAPRandomSG};

        private static final String[] terminalConditions = {EAService.TerminalConditions.CPUTimeBased+addParams(CPUTime)};

        public static int nPopulations[] = {51};   // "13", "25", "51"
        public static int nTours[] = {10};    // "5", "10", "20"
        public static int nNeighborSolutions[] = {3};   // 3, 5, 7
        public static int nSharedNSolutions[] = {1};   // 1, 2, 3
        public static String nRNG[] = {EAService.RNGs.MersenneTwister};//{EAService.RNGs.MersenneTwister, EAService.RNGs.Arnold, EAService.RNGs.Burgers, EAService.RNGs.Lozi, EAService.RNGs.Tent};

        public static int poolSizes[] = {1};

        public void generateCases(BufferedWriter writer, File[]  files ) throws IOException {

            for (int isg = 0; isg < solutionGenerators.length; isg++) {
                for (int l = 0; l < MBO_NF.length; l++) {
                    for (int tc = 0; tc < terminalConditions.length; tc++) {
                        for (int p = 0; p < nPopulations.length; p++) {
                            for (int t = 0; t < nTours.length; t++) {
                                for (int ns = 0; ns < nNeighborSolutions.length; ns++) {
                                    for (int ss = 0; ss < nSharedNSolutions.length; ss++) {
                                        for (int r = 0; r < nRNG.length; r++) {
                                            for (int ps = 0; ps < poolSizes.length; ps++) {
                                                for (File file:files) {
                                                    String prefix = file.getName();

                                                    if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
                                                        continue;
                                                    writeRepeated(writer, repeat, prefix + "\t" +
                                                            MetaHeuristicService.M_MBO + "\t" +
                                                            solutionGenerators[isg] + "\t" +
                                                            MBO_NF[l] + "\t" +
                                                            terminalConditions[tc] + "\t" +
                                                            nPopulations[p] + "\t" +
                                                            nTours[t] + "\t" +
                                                            nNeighborSolutions[ns] + "\t" +
                                                            nSharedNSolutions[ss] + "\t" +
                                                            nRNG[r] + "\t" +
                                                            poolSizes[ps] + "\n");
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


    public static class IslandMBOCases implements ITestCaseGenerator{
        public static String MBO_NF[] = { EAService.MutationOperators.SimpleMotap };

        private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.MOTAPRandomSG};

        private static final String[] terminalConditions = {EAService.TerminalConditions.CPUTimeBased+addParams(CPUTime)};

        public static int nPopulations[] = {51};   // "13", "25", "51"
        public static int nTours[] = {10};    // "5", "10", "20"
        public static int nNeighborSolutions[] = {3};   // 3, 5, 7
        public static int nSharedNSolutions[] = {1};   // 1, 2, 3
        public static String nRNG[] = {EAService.RNGs.MersenneTwister};//{EAService.RNGs.MersenneTwister, EAService.RNGs.Arnold, EAService.RNGs.Burgers, EAService.RNGs.Lozi, EAService.RNGs.Tent};

        private static final int[] populationCounts = {8};

        private static final int[] immigrationPeriods = {8};
        private static final int[] immigrantCounts  =   {8};

        private static final int[] poolSizes  =   {8};

        public void generateCases(BufferedWriter writer, File[]  files ) throws IOException {

            for (int isg = 0; isg < solutionGenerators.length; isg++) {
                for (int l = 0; l < MBO_NF.length; l++) {
                    for (int tc = 0; tc < terminalConditions.length; tc++) {
                        for (int p = 0; p < nPopulations.length; p++) {
                            for (int t = 0; t < nTours.length; t++) {
                                for (int ns = 0; ns < nNeighborSolutions.length; ns++) {
                                    for (int ss = 0; ss < nSharedNSolutions.length; ss++) {
                                        for (int r = 0; r < nRNG.length; r++) {
                                            for (int pc = 0; pc < populationCounts.length; pc++) {
                                                for (int ip = 0; ip < immigrationPeriods.length; ip++) {
                                                    for (int ic = 0; ic < immigrantCounts.length; ic++) {
                                                        for (int pools = 0; pools < poolSizes.length; pools++) {
                                                            for (File file : files) {
                                                                String prefix = file.getName();

                                                                if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
                                                                    continue;
                                                                writeRepeated(writer, repeat, prefix + "\t" +
                                                                        MetaHeuristicService.M_MBO + "\t" +
                                                                        solutionGenerators[isg] + "\t" +
                                                                        MBO_NF[l] + "\t" +
                                                                        terminalConditions[tc] + "\t" +
                                                                        nPopulations[p] + "\t" +
                                                                        nTours[t] + "\t" +
                                                                        nNeighborSolutions[ns] + "\t" +
                                                                        nSharedNSolutions[ss] + "\t" +
                                                                        nRNG[r] + "\n");
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


    public static class MBO2Cases implements ITestCaseGenerator{

        private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.MOTAPRandomSG};

        public static String MBO_NF[] = {//makeList(EAService.MutationOperators.GreedySwapWithExchange, EAService.MutationOperators.SimpleMotap)
                                        EAService.MutationOperators.GreedySwapWithExchange+addParams(3)};

        private static final String[] terminalConditions = {EAService.TerminalConditions.CPUTimeBased+addParams(CPUTime)};

        public static int nPopulations[] = {51};   // "13", "25", "51"
        public static int nTours[] = {10};    // "5", "10", "20"
        public static int nNeighborSolutions[] = {3};   // 3, 5, 7
        public static int nSharedNSolutions[] = {1};   // 1, 2, 3

        public void generateCases(BufferedWriter writer, File[]  files ) throws IOException {

            for (int isg = 0; isg < solutionGenerators.length; isg++) {
                for (int l = 0; l < MBO_NF.length; l++) {
                    for (int tc = 0; tc < terminalConditions.length; tc++) {
                        for (int p = 0; p < nPopulations.length; p++) {
                            for (int t = 0; t < nTours.length; t++) {
                                for (int ns = 0; ns < nNeighborSolutions.length; ns++) {
                                    for (int ss = 0; ss < nSharedNSolutions.length; ss++) {
                                        for (File file:files) {
                                            String prefix = file.getName();

                                            if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
                                                continue;
                                            writeRepeated(writer, repeat, prefix + "\t" +
                                                    MetaHeuristicService.M_MBO2 + "\t" +
                                                    solutionGenerators[isg] + "\t" +
                                                    MBO_NF[l] + "\t" +
                                                    terminalConditions[tc] + "\t" +
                                                    nPopulations[p] + "\t" +
                                                    nTours[t] + "\t" +
                                                    nNeighborSolutions[ns] + "\t" +
                                                    nSharedNSolutions[ss] + "\n");
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

    public static class HBMOCases implements ITestCaseGenerator{
        private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.MOTAPRandomSG};

        public static String MBO_NF[] = {   EAService.MutationOperators.GreedySearch ,
                                            //makeList(EAService.MutationOperators.GreedySwap+addParams(5),EAService.MutationOperators.SimpleMotap)
                                            };

        private static final String[] terminalConditions = {EAService.TerminalConditions.CPUTimeBased+addParams(CPUTime)};

        private static final String[] crossOverOperators = {EAService.CrossOverOperators.SimpleMOTAP};

        public static int drs[] = {35};   // "13", "25", "51"
        public static int spmaxs[] = {10};    // "5", "10", "20"
        public static int bmaxs[] = {3};   // 3, 5, 7
        public static double darsads[] = {0.1};   // 1, 2, 3

        public void generateCases(BufferedWriter writer, File[]  files ) throws IOException {

            for (int isg = 0; isg < solutionGenerators.length; isg++) {
                for (int l = 0; l < MBO_NF.length; l++) {
                    for (int tc = 0; tc < terminalConditions.length; tc++) {
                        for (int co = 0; co < crossOverOperators.length; co++) {
                            for (int d = 0; d < drs.length; d++) {
                                for (int s = 0; s < spmaxs.length; s++) {
                                    for (int b = 0; b < bmaxs.length; b++) {
                                        for (int ds = 0; ds < darsads.length; ds++) {
                                            for (File file:files) {
                                                String prefix = file.getName();

                                                if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
                                                    continue;
                                                writeRepeated(writer, repeat, prefix + "\t" +
                                                        MetaHeuristicService.M_HBMO + "\t" +
                                                        solutionGenerators[isg] + "\t" +
                                                        MBO_NF[l] + "\t" +
                                                        terminalConditions[tc] + "\t" +
                                                        crossOverOperators[co] + "\t" +
                                                        drs[d] + "\t" +
                                                        spmaxs[s] + "\t" +
                                                        bmaxs[b] + "\t" +
                                                        darsads[ds] + "\n");
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


    public static class ScatterSearchCases implements ITestCaseGenerator{

        private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.RandomLoadBalancedSG};

        private static final String[] terminalConditions = {EAService.TerminalConditions.CPUTimeBased+addParams(CPUTime)};

        public static int lsIterationCounts[] = {100};
        public static int refSetSizes[] =  {5};


        public static int intensityMemorySizes[] =  {20};
        public static int diversityMemorySizes[] =  {0};
        public static int neighborHoodSizes[]     =  {8};



        public void generateCases(BufferedWriter writer, File[] files ) throws IOException {

            for (int isg = 0; isg < solutionGenerators.length; isg++) {
                for (int tc = 0; tc < terminalConditions.length; tc++) {
                    for (int lsic=0;lsic<lsIterationCounts.length;lsic++) {
                        for (int rss=0;rss<refSetSizes.length;rss++) {
                            for (int ims=0;ims<intensityMemorySizes.length;ims++) {
                                for (int dms=0;dms<diversityMemorySizes.length;dms++) {
                                    for (int ns = 0; ns< neighborHoodSizes.length; ns++) {
                                        for (File file:files) {
                                            String prefix = file.getName();

                                            if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
                                                continue;
                                            writeRepeated(writer, repeat, prefix + "\t" +
                                                    MetaHeuristicService.M_SS + "\t" +
                                                    solutionGenerators[isg] + "\t" +
                                                    terminalConditions[tc] + "\t" +
                                                    lsIterationCounts[lsic] + "\t" +
                                                    refSetSizes[rss] + "\t" +
                                                    intensityMemorySizes[ims] + "\t" +
                                                    diversityMemorySizes[dms] + "\t" +
                                                    neighborHoodSizes[ns] + "\n");
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
    public static class TabuCases implements ITestCaseGenerator {

        private static final String[] solutionGenerators = {EAService.InitialSolutionGenerators.RandomLoadBalancedSG};

        private static final String[] neighborhoodFunctions = {EAService.MutationOperators.GreedySwapWithExchange+addParams(3)};

        private static final String[] tabuLists = {TabuService.TabuLists.SolutionMemory + addParams(5)};

        private static final String[] aspirations= {null};

        private static final String[] terminalConditions = {EAService.TerminalConditions.CPUTimeBased+addParams(CPUTime)};

        private static final Object INTENSITYMEMORYSIZE = 20;
        private static final Object DIVERSITYMEMORYSIZE = 5;
        public static final String[] intensityMemories =  {
                TabuService.MediumTermMemories.MOTAPRECENCY+ addParams(10,20,0.1),
                TabuService.MediumTermMemories.MOTAPRECENCY+ addParams(10,50,0.2),
                TabuService.MediumTermMemories.MOTAPRECENCY+ addParams(10,100,0.2),
                TabuService.MediumTermMemories.MOTAPRECENCY+ addParams(20,20,0.1),
                TabuService.MediumTermMemories.MOTAPRECENCY+ addParams(20,50,0.2),
                TabuService.MediumTermMemories.MOTAPRECENCY+ addParams(20,100,0.2)};
        public static final String diversityMemories[] =   { TabuService.MediumTermMemories.MOTAPFREQUENCY + addParams(DIVERSITYMEMORYSIZE)};

        private static final String[] neighborhoodSizes = {"8"};

        public void generateCases(BufferedWriter writer,File[] files) throws IOException {
            for (int isg = 0; isg < solutionGenerators.length; isg++) {
                for (int nf = 0; nf < neighborhoodFunctions.length; nf++) {
                    for (int tl = 0; tl < tabuLists.length; tl++) {
                        for (int a = 0; a < aspirations.length; a++) {
                            for (int tc = 0; tc < terminalConditions.length; tc++) {
                                for (int ims = 0; ims < intensityMemories.length; ims++) {
                                    for (int dms = 0; dms < diversityMemories.length; dms++) {
                                        for (int ns = 0; ns < neighborhoodSizes.length; ns++) {
                                            for (File file : files) {
                                                String prefix = file.getName();

                                                if (prefix.equals(MOTAPTestCaseRunner.SOLUTION_FILENAME))
                                                    continue;
                                                writeRepeated(writer, repeat, prefix + "\t" +
                                                        MetaHeuristicService.M_TABU + "\t" +
                                                        solutionGenerators[isg] + "\t" +
                                                        neighborhoodFunctions[nf] + "\t" +
                                                        tabuLists[tl] + "\t" +
                                                        aspirations[a] + "\t" +
                                                        intensityMemories[ims] + "\t" +
                                                        diversityMemories[dms] + "\t" +
                                                        neighborhoodSizes[ns] + "\t" +
                                                        terminalConditions[tc] + "\n");
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

    public static String makeList(String...items)
    {
        String s = "";
        for (String param:items)
            s += param + EAService.OBJECT_DELIMITER;

        return s;
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


    public static void generateTestCases(BufferedWriter writer, File[] files) throws IOException {
        for (ITestCaseGenerator generator:MetaHeuristics)
        {
            generator.generateCases(writer,files);
        }
    }

}
