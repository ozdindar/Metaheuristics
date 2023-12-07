package metaheuristic.ils;

import base.OptimizationProblem;
import problems.motap.MOTAProblem;
import representation.IntegerAssignment;
import representation.base.Individual;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.Collections;

public class MOTAPRandomPerturbator implements Perturbator {

    double ratio;
    private int perturbateCount=0;

    public MOTAPRandomPerturbator(double ratio) {
        this.ratio = ratio;
    }

    public MOTAPRandomPerturbator(MOTAPRandomPerturbator other) {
        ratio = other.ratio;
        perturbateCount = other.perturbateCount;
    }

    @Override
    public void perturbate(OptimizationProblem problem, Individual individual) {
        MOTAProblem mp = (MOTAProblem) problem;
        IntegerAssignment assignment = (IntegerAssignment) individual.getRepresentation();

        //System.out.println("Perturbated:"+ (perturbateCount++));

        int reassignmentCount = (int) (assignment.getLength()*ratio);

        ArrayList<Integer> tasks = generateTasks(assignment.getLength());

        for (int i = 0; i < reassignmentCount; i++) {
            reassignTask(assignment,tasks.get(i),((MOTAProblem) problem).getProcessorCount());
        }

        individual.update(assignment,problem.cost(assignment));

    }

    @Override
    public Perturbator clone() {
        return new MOTAPRandomPerturbator(this);
    }

    private void reassignTask(IntegerAssignment assignment, Integer task, Integer processorCount) {
        assignment.set(task, RandUtil.randInt(processorCount));
    }

    private ArrayList<Integer> generateTasks(int length) {
        ArrayList tasks = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            tasks.add(i);
        }
        Collections.shuffle(tasks);
        return tasks;
    }
}
