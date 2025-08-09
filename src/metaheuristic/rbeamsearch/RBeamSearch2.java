package metaheuristic.rbeamsearch;

import base.OptimizationProblem;
import metaheuristic.AbstractSMetaheuristic;
import problems.base.InitialSolutionGenerator;
import problems.motap.mutation.Release.ReleaseHandler;
import representation.CostBasedComparator;
import representation.base.Individual;
import util.random.RandUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RBeamSearch2 extends AbstractSMetaheuristic {

    private static final int ROULETTE_SIZE = 10;
    private static final int BEAM_SIZE = 5;
    private int depth = 1;
    private int releaseCount=1;


    int maxIterationCount;
    private int iterationCount=0;

    RBeamChildGenerator childGenerator;
    ReleaseHandler releaseHandler;
    private int  rcc =0;

    public RBeamSearch2(int releaseCount, int depth, int maxIterationCount, RBeamChildGenerator childGenerator, ReleaseHandler releaseHandler) {
        this.depth = depth;
        this.releaseCount = releaseCount;
        this.maxIterationCount = maxIterationCount;
        this.childGenerator = childGenerator;
        this.releaseHandler = releaseHandler;
    }

    @Override
    protected void _perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {

        while (true)
        {
            if (!beamSearch(problem,currentSolution))
            {
                break;
                //System.out.println("Release Called: + "+ (rcc++));
            }

            updateBestIfNecessary(currentSolution.getRepresentation(),currentSolution.getCost());
            iterationCount++;
            increaseNeighboringCount();
        }

    }

    @Override
    public AbstractSMetaheuristic clone() {
        AbstractSMetaheuristic clone = new RBeamSearch2(releaseCount,depth,maxIterationCount,childGenerator.clone(),releaseHandler.clone());
        return clone;
    }

    private boolean beamSearch(OptimizationProblem problem, Individual currentSolution) {
        int count = 0;


        List<RBeamNode> children = new ArrayList<RBeamNode>();
        List<Individual> parents = new ArrayList<>();
        List<Individual> globalParentsPool = new ArrayList<>();

        globalParentsPool.add(currentSolution);
        parents.add(currentSolution);
        while(count++ <= depth && !parents.isEmpty()){
            childGenerator.generate(problem,children,parents);
            //generateChildren(problem,reassignmentList,assignmentList); // Fills reassignmentList.

            selectChildren(problem,children,parents,globalParentsPool); // Select IntegerAssignments.
        }

        Collections.sort(globalParentsPool,new CostBasedComparator());
        if((globalParentsPool.get(0).getCost()>=currentSolution.getCost())){ // No profitable operation !
            return false;
        }
        Individual newAssignment= assignmentWheel(globalParentsPool,currentSolution.getCost());
        currentSolution.update(newAssignment.getRepresentation(),newAssignment.getCost());

        return true;
    }


    @Override
    public void init(OptimizationProblem problem) {
        super.init(problem);
        iterationCount=0;
        rcc=0;
    }

    private void addAllChildren(OptimizationProblem problem, List<RBeamNode> children, List<Individual> globalParentsPool, List<Individual> parents, List<Individual> selectedChildren) {

        for(int i = 0; i< children.size() ; i++){
            RBeamNode child = children.get(i);
            Individual newChild = childGenerator.createChild(problem,child,parents);

            if (!globalParentsPool.contains(newChild)&& !selectedChildren.contains(newChild)) {
                selectedChildren.add(newChild);
                if (selectedChildren.size()>=BEAM_SIZE)
                    break;
            }
        }
    }

    private void addGainingChildren(List<RBeamNode> children, List<Individual> parents, List<RBeamNode> rouletteList) {

        for(int i = 0; i< children.size() ; i++){
            RBeamNode reassignment = children.get(i);
            if(reassignment.deltaCost >=0){ // There is no more gain.
                break;
            }
            rouletteList.add(reassignment);
            if (rouletteList.size()>= ROULETTE_SIZE)
                break;

        }

    }

    private void selectLosingChildren(OptimizationProblem problem, List<RBeamNode> children, List<Individual> parents, int startIndex , List<Individual> selectedChildren) {

        for(int i = startIndex ; i<children.size(); i++){
            RBeamNode child = children.get(i);
            if(child.deltaCost <0){
                continue;
            }
            Individual newChild = childGenerator.createChild(problem,child,parents);

            selectedChildren.add(newChild);
            if (selectedChildren.size()>=BEAM_SIZE)
                break;

        }
    }

    private int[] applyRouletteWheel(List<RBeamNode> rouletteList) {
        double weights[] =  new double[ROULETTE_SIZE];
        int i;
        for( i = 0 ; i<ROULETTE_SIZE && i<rouletteList.size(); i++){
            weights[i] = -1*rouletteList.get(i).getDeltaCost();
        }
        return RandUtil.roulletteSelectMulti(weights,BEAM_SIZE);
    }

    private void selectGainingChildren(OptimizationProblem problem, List<Individual> parents, List<RBeamNode> rouletteList, List<Individual> selectedChildren) {
        int[] selectedIndexes = applyRouletteWheel(rouletteList);
        for (int i = 0; i < selectedIndexes.length; i++) {
            RBeamNode child = rouletteList.get(selectedIndexes[i]);

            Individual newChild = childGenerator.createChild(problem,child,parents);
            if (!selectedChildren.contains(newChild))
                selectedChildren.add(newChild);
        }
    }

    private void selectChildren(OptimizationProblem problem, List<RBeamNode> children, List<Individual> parents, List<Individual> globalParentsPool) {
        Collections.sort(children,new DeltaCostBasedComparator());
        List<RBeamNode> rouletteList = new ArrayList<>(); // List to give to roulette select.
        List<Individual> selectedChildren = new ArrayList<>(); // List to change with old assignmentList.

        if(children.size()<=BEAM_SIZE){
            addAllChildren(problem,children,globalParentsPool,parents,selectedChildren); //I don't have enough reassignments to fill newAssignmentList.
        }
        else {
            addGainingChildren(children, parents, rouletteList);
            if (rouletteList.size() <= BEAM_SIZE) {
                addAllChildren(problem,rouletteList,globalParentsPool, parents,selectedChildren);
                selectLosingChildren(problem,children, parents, selectedChildren.size(), selectedChildren); // selects and fills required amount of assignments for newAssignmentList
            } else {
                selectGainingChildren(problem, parents, rouletteList, selectedChildren);// negative reassignments are more than BEAM_SIZE.
            }
        }


        parents.clear();
        parents.addAll(selectedChildren);

        for (int i = 0; i < selectedChildren.size(); i++) {
            if (!globalParentsPool.contains(selectedChildren.get(i)))
                globalParentsPool.add(selectedChildren.get(i));
        }

    }

    private Individual assignmentWheel(List<Individual> assignmentList, double initialCost) {
        double weight[] =  new double[ROULETTE_SIZE];
        int i;
        for( i = 0 ; i<ROULETTE_SIZE && i<assignmentList.size(); i++){
            if(assignmentList.get(i).getCost()>=initialCost){ // Do not take negative or zero elements.
                break;
            }
            weight[i] = -1*(assignmentList.get(i).getCost()-initialCost);
        }

        int index = RandUtil.rouletteSelect(weight,i);


        return  assignmentList.get(index);
    }

    @Override
    public String defaultName() {
        return "RBeamSearch";
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }



}
