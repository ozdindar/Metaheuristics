/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//package _sdtspmetaheuristics;
//
//import problems.pcb.Component;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// *
// * @author Z420
// */
//public class PCBData {
//
//    private String name;
//    private HashMap<Integer, Component> componentMap;
//    private HashMap<Integer, ArrayList<Integer>> guidedMap;
//
//    private final double thresholdDistance = 81.2;
//    private double threshold = 81.2; //  280 mm/sec x 0.29 sec =  81.2 mm
//
//    private int N;
//
//    public PCBData(String name){
//        this.name = name;
//        componentMap = new HashMap();
//        guidedMap = new HashMap();
//    }
//
//    public void addComponent(Component component) {
//        componentMap.put(component.getIndex(), component);
//    }
//
//    public HashMap<Integer, Component> getComponentMap() {
//        return componentMap;
//    }
//
//    public void constructGuidedMap(){
//        double distance;
//        ArrayList<Integer> indexList;
//        for(Component c1 : componentMap.values()){
//            indexList = new ArrayList();
//            while(indexList.isEmpty()){
//                for(Component c2 : componentMap.values()){
//                    distance = c1.getDistance(c2);
//                    //System.err.println("Distance bw " + c1.getIndex() + " and " + c2.getIndex() + " : " + distance);
//                    if(distance <= threshold)
//                        if(c1.getIndex() != c2.getIndex())
//                            indexList.add(c2.getIndex());
//                }
//                threshold += 2.0;
//            }
//            threshold = thresholdDistance;
//            guidedMap.put(c1.getIndex(), indexList);
//        }
//    }
//
//    public void setN(int N){
//        this.N = N;
//    }
//
//    public int getN(){
//        return N;
//    }
//
//    public HashMap<Integer, ArrayList<Integer>> getGuidedMap(){
//        return guidedMap;
//    }
//
//    public String getName() {
//        return name;
//    }
//}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problems.pcb;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

;

/**
 *
 * @author Z420
 */
public class PCBData {

    //    public static final int boardCarrierSpeed = 120; // 120 mm per second
    //    public static final double[] turretTimes = new double[]{0.20,0.23,0.33,0.40}; //TDK RX-5A



    public static final int boardCarrierSpeed = 280; // 280 mm per second
    public static final double[] turretTimes = new double[]{0.15,0.19,0.24,0.29}; //FUJI CP-43

    private HashMap<Integer, Component> componentMap;
    private HashMap<Integer, ArrayList<Integer>> guidedMap;


    private String name;

    private final double thresholdDistance = 81.2;
    private double threshold = 81.2; //  280 mm/sec x 0.29 sec =  81.2 mm

    private int N;

    public PCBData(String name){
        componentMap = new HashMap();
        guidedMap = new HashMap();
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public void addComponent(Component component) {
        componentMap.put(component.getIndex(), component);
    }

    public HashMap<Integer, Component> getComponentMap() {
        return componentMap;
    }

    public void constructGuidedMap(){
        double distance;
        ArrayList<Integer> indexList;
        for(Component c1 : componentMap.values()){
            indexList = new ArrayList();
            while(indexList.isEmpty()){
                for(Component c2 : componentMap.values()){
                    distance = c1.getDistance(c2);
                    //System.err.println("Distance bw " + c1.getIndex() + " and " + c2.getIndex() + " : " + distance);
                    if(distance <= threshold)
                        if(c1.getIndex() != c2.getIndex())
                            indexList.add(c2.getIndex());
                }
                threshold += 2.0;
            }
            threshold = thresholdDistance;
            guidedMap.put(c1.getIndex(), indexList);
        }
    }

    public void setN(int N){
        this.N = N;
    }

    public int getN(){
        return N;
    }

    public HashMap<Integer, ArrayList<Integer>> getGuidedMap(){
        return guidedMap;
    }

    public static PCBData constructPCBData(String pcbName,String fileName) {

        BufferedReader reader;
        int index, type, group;
        double dimensions[], turretTime;
        Component component;
        PCBData data = new PCBData(pcbName);

        try{
            reader = new BufferedReader(new FileReader(new File(fileName)));
            String line;
            String[] params;
            int N = 0;

            while((line = reader.readLine()) != null){
                params = line.split("\t");

                index = Integer.parseInt(params[0]);
                dimensions = new double[2];

                dimensions[0] = Double.parseDouble(params[1]);
                dimensions[1] = Double.parseDouble(params[2]);

                type = Integer.parseInt(params[3]);

                group = Integer.parseInt(params[4]);

                turretTime = turretTimes[group-1];

                component = new Component(index, dimensions, type, group, turretTime);
                data.addComponent(component);

                N++;
            }

            data.setN(N);

            HashMap<Integer, Component> compMap;
            compMap = data.getComponentMap();

            for(Component c1 : compMap.values()){
                c1.initializeNeighbourArray(N);
                for(Component c2 : compMap.values())
                    c1.ComputeandSavetheDistance(c2);
            }

            data.constructGuidedMap();
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return data;

    }

    public static void main(String args[])
    {
        PCBData pcbData = PCBData.constructPCBData("rPS11AK08-9.txt" , "./data/realPCB/rPS11AK08-9.txt");




        System.out.println(pcbData.getN());
    }

}
