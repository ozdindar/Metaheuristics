package experiments.motap;


import base.OptimizationProblem;
import metaheuristic.MetaHeuristicService;
import metaheuristic.RepeatingMetaHeuristicEngine;
import metaheuristic.ea.EAService;
import problems.base.InitialSolutionGenerator;
import problems.motap.CPLEXSolver;
import problems.motap.MOTAProblem;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MOTAPTestCaseRunner {

    public static final String SOLUTION_FILENAME = "solutions.txt";
    private static final double EXACTSOLUTION_TIMEOUT = 3600;
    //public static String outputFileName;



    public static int threadID=0;


    public static ArrayList<Runnable> testCases = new ArrayList();
    private static HashMap<String, MOTAProblem> MOTAPDataMap = new HashMap<>();
    private static HashMap<String, String> MOTAPSolutionMap = new HashMap<>();
    private static int repeatCount = 10;// Default to 10
    private boolean debugTrace= false;

    ExecutorService threadPool;


    private void init(String inputFolder, String testCases) throws IOException, ClassNotFoundException {
        fillMOTAPDataMap(inputFolder);
        fillMOTAPSolutionMap(inputFolder,inputFolder+"/"+SOLUTION_FILENAME);


        threadPool = Executors.newFixedThreadPool(1);

        File testCaseFile = new File(testCases);
        if (testCaseFile.isFile())
            loadTestCases(testCaseFile,debugTrace);
        else if (testCaseFile.isDirectory())
        {
            File[] testCaseFiles = testCaseFile.listFiles();
            for(File tcf:testCaseFiles)
            {
                loadTestCases(tcf,debugTrace);
            }
        }
    }



    public MOTAPTestCaseRunner(String[] args) throws IOException, ClassNotFoundException {
        String inputFolder = args[1];
        String testCases  = args[0];

        if (args.length>2 && args[2].equals("debug"))
            debugTrace=true;

        if (args.length>3 )
            repeatCount= Integer.parseInt(args[3]);
        //outputFileName = args[2];
        init(inputFolder,testCases);
    }

    public void start() throws IOException{
        for(int i=0; i<testCases.size(); i++)
            threadPool.execute(testCases.get(i));
        

        threadPool.shutdown();
        while(!threadPool.isTerminated()){}
        System.out.println("Finished all threads!");

    }

    public static void main(String args[]) throws Exception{
        checkUsage(args);
        MOTAPTestCaseRunner runner = new MOTAPTestCaseRunner(args);
        runner.start();
    }

    private static void checkUsage(String[] args) {
        if(args.length < 2){
            System.err.println("Usage: java Main <testCases> <problemsDirectory>");
            System.exit(1);
        }
    }

    private static void loadTestCases(File testCaseFile, boolean debugTrace){
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(testCaseFile));
            String line;
            while((line = reader.readLine()) != null)
            {
                loadTestCase(line,testCaseFile.getName()+".out",debugTrace);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MOTAPTestCaseRunner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MOTAPTestCaseRunner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static OptimizationProblem generateProblem(String[] params) throws IOException, ClassNotFoundException {
        String inputFile = params[0];

        OptimizationProblem problem = MOTAPDataMap.get(inputFile);

        if (problem ==null) {
            boolean aha = true;
        }
        return MOTAPDataMap.get(inputFile);
    }


    private static void loadTestCase(String line, String outputFileName, boolean debugTrace) throws IOException, ClassNotFoundException {
        String[] params = line.trim().split("\t");

        OptimizationProblem problem = generateProblem(params);
        Runnable solver = generateSolver(problem,params,outputFileName,debugTrace);

        testCases.add(solver);
        threadID++;
    }

    private static Runnable generateSolver(OptimizationProblem problem, String[] params, String outputFileName, boolean debugTrace) {

            String prefix = createPrefix(params);
            String suffix = (MOTAPSolutionMap.containsKey(params[0])) ? ""+MOTAPSolutionMap.get(params[0]):"NULL";
            InitialSolutionGenerator solutionGenerator = EAService.InitialSolutionGenerators.createSG(params[2],problem);
            return new RepeatingMetaHeuristicEngine(MetaHeuristicService.createMetaheuristic(problem, params[1], Arrays.copyOfRange(params, 3, params.length)),
                    problem, solutionGenerator,
                    outputFileName,prefix,suffix,repeatCount,debugTrace);

    }

    private static String createPrefix(String[] params) {
        String res = "";
        for (int i=0;i<params.length;i++)
            if (i>1)
                res += params[i]+ "-";
        else  res += params[i]+ " ";

        return res;
    }

    private static void fillMOTAPDataMap(String problemDirectory) throws IOException, ClassNotFoundException {
        File folder = new File(problemDirectory);
        System.out.printf(folder.getAbsolutePath());
        File fileList[] =  folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (new File(dir+"/"+name).isDirectory())
                    return false;
                if (name.equals("solutions.txt"))
                    return false;
                return true;
            }
        });
        if (fileList == null)
        {
            throw new RuntimeException("Problem could not be loaded from:" + folder.getAbsolutePath());
        }

        for(int i=0; i<fileList.length; i++) {
            if (fileList[i].getName().equals(SOLUTION_FILENAME))
                continue;
            MOTAProblem problem =  MOTAProblem.readFromFile(problemDirectory + "/" + fileList[i].getName());
            MOTAPDataMap.put(fileList[i].getName(),problem );
        }
    }

    private void fillMOTAPSolutionMap(String inputFolder, String solutionFileName) throws IOException, ClassNotFoundException {

        File solutionFile = new File(solutionFileName);
        if (!solutionFile.exists())
        {
            createExactSolutions(inputFolder,solutionFile);
        }
        BufferedReader reader = new BufferedReader(new FileReader(solutionFile));
        String line;
        while((line = reader.readLine()) != null)
        {
            String[] params = line.trim().split("\t");;
            if (MOTAPDataMap.containsKey(params[0]))
                MOTAPSolutionMap.put(params[0],params[1]+"\t"+params[2]);
        }

    }

    private void createExactSolutions(String inputFolder,File solutionFile) throws IOException, ClassNotFoundException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(solutionFile));

        for ( String motapFile: MOTAPDataMap.keySet())
        {
            String motapFilePath = inputFolder+"/"+motapFile;
            long tStart = System.currentTimeMillis();
            double val = CPLEXSolver.solveMOTAP(MOTAProblem.readFromFile(motapFilePath), EXACTSOLUTION_TIMEOUT);
            long tDelta = System.currentTimeMillis() - tStart;
            double elapsedTime = tDelta / 1000.0;

            writer.write(motapFile+"\t"+val+"\t"+elapsedTime);
            writer.newLine();
            writer.flush();
        }
        writer.close();
    }

}
