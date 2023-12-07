/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package experiments.pcb;

import experiments.TestCaseGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dindar
 */
public class PCBTestCaseGenerator {

    private static String testCaseFileName="PSOTestCases.txt";
    private static String pcbDataFolder = "./data/realPCB";
    private static final String k_values[] = {"1"};//{"0","1","2","3","4","5","6","7","8","9","10"};         //"0","1","2","3","4","5","6"



    private static File files[] = null;

    public static void main(String args[]){
        generateTestCases();
    }

    private static void generateTestCases() {
        BufferedWriter writer;

        File file = new File(testCaseFileName);
        File filesDirectory = new File(pcbDataFolder);
        files = filesDirectory.listFiles();

        try {
            writer = new BufferedWriter(new FileWriter(file));

            generateCases(writer);

            writer.flush();
            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(PCBTestCaseGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void generateCases(BufferedWriter writer) throws IOException {
        for(int i=0; i<files.length; i++){
            for(int j=0; j<k_values.length; j++)
            {
                String prefix = files[i].getName()+"\t"+k_values[j];
                TestCaseGenerator.generateTestCases(writer,prefix);
            }
        }
    }




    private static void writeRepeated(BufferedWriter writer, int run, String s) throws IOException {
        for (int i =0;i<run;i++ )
        {
            writer.write(s);
        }
    }




}
