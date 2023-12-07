package experiments.motap;


import problems.motap.Module;
import problems.motap.*;
import util.random.RandUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dindar.oz on 06.08.2015.
 */
public class MOTAPGenerator {





    public static Task createSampleTask() {
        List<IModule> moduleList = createSampleModules();

        HashMap<Pair<Integer,Integer>,Integer> tig = createSampleTIG();
        return new Task(moduleList,tig);
    }


    public static void generateProblems(String outputPath, int[] moduleCounts, int[] processorCounts, double[] densities, double[] CRRs, int repeat ) throws IOException {
        for (int m =0;m<moduleCounts.length;m++)
        {
            for (int p =0;p<processorCounts.length;p++)
            {
                for (int d =0;d<densities.length;d++)
                {
                    for (int c =0;c<CRRs.length;c++)
                    {
                        for (int r = 0; r < repeat; r++)
                        {
                            generateProblem(outputPath, moduleCounts[m], processorCounts[p], densities[d], CRRs[c],r);
                        }
                    }
                }
            }
        }
    }

    private static void generateProblem(String outputPath, int moduleCount, int processorCount, double density, double crr,int machineIndex) throws IOException {
        MOTAProblem p = createRandomProblem(moduleCount,processorCount,density,crr);
        p.writeToFile(outputPath+"/mtp_"+processorCount+"_"+moduleCount+"_"+density+"_"+crr+"_"+machineIndex+".dat");
    }

    public static MOTAProblem createRandomProblem(int moduleCount, int processorCount, double density,double CCR)
    {
        Task task = createRandomTask(moduleCount,processorCount,density,CCR);
        DCS dcs = createRandomDCS(processorCount,(double)task.totalMemoryRequirement()/(double)processorCount,(double)task.totalCRR()/(double)processorCount);


        return new MOTAProblem(dcs,task);
    }

    public static Task createRandomTask(int moduleCount, int processorCount, double density,double CCR) {
        List<IModule> moduleList = createRandomModules(moduleCount, processorCount);

        double avgComputation = 0;
        for(int i=0;i<moduleCount;i++)
            avgComputation += moduleList.get(i).getCRR();

        avgComputation /= moduleCount;

        HashMap<Pair<Integer,Integer>,Integer> tig = createRandomTIG(moduleCount, processorCount, density, (int) (CCR*avgComputation));
        return new Task(moduleList,tig);
    }

    public static Task createRandomTask(int moduleCount, int processorCount, double density) {
        return createRandomTask(moduleCount,processorCount,density,1);
    }

    private DCS createSampleDCS() {



        DCS dcs = new DCS();
        dcs.setComputationViolationFactor(1);
        dcs.setMemoryViolationFactor(1);
        List<IProcessor> processors = createSampleProcessors();
        dcs.setProcessors(processors);
        ICommunicationLink[][] pig = createSamplePIG();
        dcs.setPig(pig);
        return dcs;

    }

    public static DCS createRandomDCS(int processorCount, double avgMem, double avgC) {



        DCS dcs = new DCS();
        dcs.setComputationViolationFactor(100);
        dcs.setMemoryViolationFactor(100);
        List<IProcessor> processors = createRandomProcessors(processorCount,avgMem,avgC);
        dcs.setProcessors(processors);
        ICommunicationLink[][] pig = createRandomPIG(processorCount);
        dcs.setPig(pig);
        return dcs;

    }

    public static DCS createRandomDCS(int processorCount) {

        return createRandomDCS(processorCount,50,50);

    }

    private ICommunicationLink[][] createSamplePIG() {
        ICommunicationLink[][] pig = new CommunicationLink[4][4];


        for (int p1 =0; p1<3;p1++)
        {
            pig[p1] = new CommunicationLink[4];
            for (int p2 =p1; p2<4;p2++)
            {
                int cc = 2;
                int tr = 3;
                double fr = 0.1;
                pig[p1][p2] = new CommunicationLink(cc,tr,fr);
                pig[p2][p1] = new CommunicationLink(cc,tr,fr);
            }
        }


        return pig;
    }

    public static int         MODULE_COUNT = 20;
    public static int         PROCESSOR_COUNT = 6;
    public static double      MODULE_CONNECTIVITY = 0.4;
    //private static double      PROCESSOR_CONNECTIVITY = 0.6;


    private static ICommunicationLink[][] createRandomPIG(int processorCount) {

        ICommunicationLink pig[][] = new CommunicationLink[processorCount][];

        for (int p1 =0; p1<processorCount;p1++)
        {
            pig[p1] = new CommunicationLink[processorCount];
        }

        for (int p1 =0; p1<processorCount-1;p1++)
        {
            for (int p2 =p1+1; p2<processorCount;p2++)
            {
                int cc = LINK_COMMUNICATION_COST_LB ;
                int tr = (int) RandUtil.randDouble(LINK_TR_LB ,LINK_TR_UB);
                double fr = RandUtil.randDouble(LINK_FR_LB,LINK_FR_UB);
                pig[p1][p2] = new CommunicationLink(cc,tr,fr);
                pig[p2][p1] = new CommunicationLink(cc,tr,fr);
            }
        }


        return pig;
    }

    private static final double PROCESSOR_FR_LB = 0.00005;
    private static final double PROCESSOR_FR_UB = 0.00010;
    private static final double LINK_FR_LB = 0.00015;
    private static final double LINK_FR_UB = 0.00030;

    private static final int PROCESSOR_EXECUTION_COST_LB = 1;
    private static final int PROCESSOR_EXECUTION_COST_UB = 1;

    private static final int LINK_COMMUNICATION_COST_LB = 1;
    private static final int LINK_COMMUNICATION_COST_UB = 1;

    private static final int LINK_TR_LB = 1;
    private static final int LINK_TR_UB = 10;

    private static List<IProcessor> createRandomProcessors(int processorCount,double avgMem, double avgC) {

        List<IProcessor> processors = new ArrayList<>();

        for (int i=0;i<processorCount;i++)
        {
            double fr = RandUtil.randDouble(PROCESSOR_FR_LB ,PROCESSOR_FR_UB);
            int tm = (int) (avgMem+ RandUtil.randInt((int) avgMem));
            int tcr = (int) (avgC+ RandUtil.randInt((int) avgC));
            int ec = PROCESSOR_EXECUTION_COST_LB;
            IProcessor p = new Processor("P"+i,fr,tm,tcr,ec);
            processors.add(p);
        }

        return processors;
    }

    private static HashMap<Pair<Integer, Integer>, Integer> createRandomTIG(int taskCount, int processorCount, double density, int avgCommunication) {

        int linkCount = (int) (density*taskCount*(taskCount-1)/2);

        HashMap<Pair<Integer, Integer>, Integer> tig = new HashMap<>();

        while(tig.size()<linkCount)
        {
            int p1 = RandUtil.randInt(taskCount);
            int p2 = p1;
            while (p2 ==p1)
                p2 = RandUtil.randInt(taskCount);

            if (tig.containsKey(Pair.of(p1,p2)) || tig.containsKey(Pair.of(p2,p1)))
                continue;;
            int c = 1+RandUtil.randInt(avgCommunication);
            Pair connection = Pair.of(p1,p2);
            tig.put(connection,c);
        }

        return tig;
    }


    private List<IProcessor> createSampleProcessors() {
        IProcessor p1 = new Processor(0.9,10,20,3);
        IProcessor p2 = new Processor(0.8,15,30,5);
        IProcessor p3 = new Processor(0.7,12,40,2);
        IProcessor p4 = new Processor(0.99,11,12,12);

        List<IProcessor> processors = Arrays.asList(p1, p2, p3, p4);
        return processors;
    }

    public static HashMap<Pair<Integer, Integer>, Integer> createSampleTIG() {
        HashMap<Pair<Integer, Integer>, Integer> tig = new HashMap<>();
        tig.put(Pair.of(0, 1), 3);
        tig.put(Pair.of(1, 2),5);
        tig.put(Pair.of(1, 3),2);
        tig.put(Pair.of(4, 5),3);
        tig.put(Pair.of(0, 5),5);
        tig.put(Pair.of(2, 4),1);
        return tig;
    }

    public static List<IModule> createRandomModules(int moduleCount, int processorCount) {
        List<IModule> modules = new ArrayList<>();
        for (int i=0;i<moduleCount;i++)
        {
            int executionTimes[] = new int[processorCount];
            int crr =1+ RandUtil.randInt(5);
            int mr = 1+ RandUtil.randInt(5);
            for (int p=0;p<processorCount;p++)
            {
                executionTimes[p] = 1 + RandUtil.randInt(10);
            }
            IModule m = new Module("M"+i,executionTimes,crr,mr);

            modules.add(m);
        }
        return modules;
    }

    public static List<IModule> createSampleModules() {
        IModule m1 = new Module(new int[]{1,2,1,1},4,6);
        IModule m2 = new Module(new int[]{2,1,4,1},6,15);
        IModule m3 = new Module(new int[]{1,1,5,7},11,3);
        IModule m4 = new Module(new int[]{5,4,10,1},5,3);
        IModule m5 = new Module(new int[]{3,2,3,8},2,4);

        return Arrays.asList(m1, m2, m3, m4, m5);
    }

    public static void main(String[] args) throws IOException {
        //generateProblems("./data/motapData/test",new int[]{40},new int[]{8},new double[]{0.5},new double[]{1},1);
        //generateProblems("./data/motapData",new int[]{20,30,40},new int[]{8},new double[]{0.3,0.5,0.8},new double[]{0.5,1,2},10);
        generateProblems("./data/motapData/hard",new int[]{80,120},new int[]{16},new double[]{0.5},new double[]{1},10);
    }

}
