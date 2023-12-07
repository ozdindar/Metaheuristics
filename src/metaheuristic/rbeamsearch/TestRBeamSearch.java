package metaheuristic.rbeamsearch;

import experiments.motap.MOTAPGenerator;
import metaheuristic.ea.terminalcondition.MotapBasedTc;
import problems.motap.MOTAProblem;
import problems.motap.mutation.Release.ReleaseWithMemory;
import problems.motap.solutionGenerator.MOTAPRandomSG;
import representation.IntegerAssignment;
import representation.SimpleIndividual;
import representation.base.Individual;

public class TestRBeamSearch {

    static MOTAProblem testInstance = MOTAPGenerator.createRandomProblem(10,8,0.8,0.5);
    static MOTAProblem testInstanceMicro = MOTAPGenerator.createRandomProblem(2,2,0.8,0.5);

    public static void main(String[] args) {
        testCorrectNess();
    }

    private static void testCorrectNess() {
        IntegerAssignment assignment= new IntegerAssignment(new int[]{1,0,1,0,2,2,2,3,3,3});
        IntegerAssignment assignmentMicro= new IntegerAssignment(new int[]{1,1});
        double cost = testInstance.cost(assignment);
        double costMicro = testInstanceMicro.cost(assignmentMicro);

        Individual i = new SimpleIndividual(assignment,cost);
        Individual iMicro = new SimpleIndividual(assignmentMicro,costMicro);
        System.out.println(i);
        System.out.println(iMicro);


        //RBeamSearch bs = new RBeamSearch(2,10,100000,new MOTAPRBeamChildGenerator(),new ReleaseWithMemory());

        SimpleRBeamSearch bs = new SimpleRBeamSearch(3,5,6,new MOTAPRBeamChildGenerator(),new ReleaseWithMemory(),new MotapBasedTc(testInstance,500));
        bs.setCurrentSolution(i);
        //bs.setCurrentSolution(iMicro);
        bs.perform(testInstance,new MOTAPRandomSG());
        //bs.perform(testInstanceMicro,new MOTAPRandomSG());

        System.out.println(bs.getBestKnownSolution()+ " "+ bs.getBestKnownCost());



    }
}
