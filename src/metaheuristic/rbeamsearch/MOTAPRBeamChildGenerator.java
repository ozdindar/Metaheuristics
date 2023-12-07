package metaheuristic.rbeamsearch;

import base.OptimizationProblem;
import problems.motap.MOTAProblem;
import representation.IntegerAssignment;
import representation.SimpleIndividual;
import representation.base.Individual;

import java.util.List;

public class MOTAPRBeamChildGenerator implements RBeamChildGenerator {

    private void addChild(MOTAProblem mp, Individual parent, int task, List<RBeamNode> children, int assignmentIndex) {
        int originalIndex = ((IntegerAssignment)parent.getRepresentation()).get(task);
        for(int i = 0 ; i<mp.getProcessorCount() ; i++) {
            if (i == originalIndex)
                continue;

            double diff = mp.costDifference(mp.getTask(), task, originalIndex, i, (IntegerAssignment) parent.getRepresentation()); // OriginalIndex's and i's positions reversed to get -costDifference.
            MotapRBeamNodeData rBeamNodeData = new MotapRBeamNodeData(task,originalIndex,i);
            RBeamNode<MotapRBeamNodeData> reassignment = new RBeamNode<MotapRBeamNodeData>(rBeamNodeData,parent.getCost()+diff,diff,assignmentIndex);
            children.add(reassignment);
        }
    }

    @Override
    public void generate(OptimizationProblem problem, List<RBeamNode> children, List<Individual> parents) {
        MOTAProblem mp = (MOTAProblem) problem;
        children.clear();
        for(int parentIndex = 0 ; parentIndex<parents.size() ; parentIndex++){
            Individual parent =  parents.get(parentIndex);
            IntegerAssignment parentAssignment = (IntegerAssignment) parent.getRepresentation();
            for(int t = 0; t<parentAssignment.getLength() ; t++){
                addChild(mp,parent,t,children,parentIndex);
            }
        }
    }

    @Override
    public Individual createChild(OptimizationProblem problem, RBeamNode child, List<Individual> parents) {

        IntegerAssignment assignment = (IntegerAssignment) parents.get(child.parentIndex).getRepresentation().clone();
        MotapRBeamNodeData nodeData = (MotapRBeamNodeData) child.nodeData;
        assignment.set(nodeData.task,nodeData.newProcessor);

        Individual individual = new SimpleIndividual(assignment,child.cost);

        return individual;
    }

    @Override
    public RBeamChildGenerator clone() {
        RBeamChildGenerator clone = new MOTAPRBeamChildGenerator();
        return clone;
    }
}
