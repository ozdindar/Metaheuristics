/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package experiments.motap;


import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dindar
 */
public class MOTAPTestCaseGenerator {

    private static String testCaseFileName="./motap_test_cases/MOTAPTestCases_Comparison2_PILS.txt";
    private static String motapDataFolder = "./data/motapData/hard";



    private static File files[] = null;

    public static void main(String args[]){
        generateTestCases();
    }

    private static void generateTestCases() {
        BufferedWriter writer;

        File file = new File(testCaseFileName);
        File filesDirectory = new File(motapDataFolder);
        files = filesDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (new File(dir+"/"+name).isDirectory())
                    return false;
                if (name.equals("solutions.txt"))
                    return false;
                return true;
            }
        });

        try {
            writer = new BufferedWriter(new FileWriter(file));

            generateCases(writer);

            writer.flush();
            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(MOTAPTestCaseGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void generateCases(BufferedWriter writer) throws IOException {
        for (MOTAPComparison2TestCases.ITestCaseGenerator testCaseGenerator: MOTAPComparison2TestCases.MetaHeuristics)
        {
            testCaseGenerator.generateCases(writer,files);
        }
    }




    private static void writeRepeated(BufferedWriter writer, int run, String s) throws IOException {
        for (int i =0;i<run;i++ )
        {
            writer.write(s);
        }
    }




}
