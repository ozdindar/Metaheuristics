package metaheuristic.mbo;

import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.EAService;
import metaheuristic.ea.base.MutationOperator;
import problems.base.InitialSolutionGenerator;
import representation.CostBasedComparator;
import representation.ListPopulation;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Population;
import representation.base.Representation;
import util.random.RNG;
import util.random.RandUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dindar.oz on 01.06.2015.
 *
 *
 * Reference Paper: "Migrating Birds Optimization: A new metaheuristic approach and its performance on quadratic assignment problem"
 *
 */
public class PMBO extends AbstractMetaheuristic
{
    RNG rng = RandUtil.getDefaultRNG();

    MutationOperator neighboringFunction;
    private int nPopulation, nTours, nNeighbourSolutions, nSharedNSolutions;

    PFlock flock;

    @Override
    protected synchronized void increaseNeighboringCount() {
       neighboringCount++;
    }

    @Override
    protected synchronized void increaseNeighboringCount(int c) {
        neighboringCount+=c;
    }

    int iterationCount =0;
    private int poolSize;

    public PMBO(List<MutationOperator> neighboringFunctions, TerminalCondition terminalCondition, int nPopulation, int nTours, int nNeighbourSolutions, int nSharedNSolutions, RNG rng) {
        this.neighboringFunction = neighboringFunctions.get(0);
        this.terminalCondition = terminalCondition;
        this.nPopulation = nPopulation;
        this.nTours = nTours;
        this.nNeighbourSolutions = nNeighbourSolutions;
        this.nSharedNSolutions = nSharedNSolutions;
        this.rng = rng;

    }

    public PMBO(List<MutationOperator> neighboringFunctions, TerminalCondition terminalCondition, int nPopulation, int nTours, int nNeighbourSolutions, int nSharedNSolutions) {
        this.neighboringFunction = neighboringFunctions.get(0);
        this.terminalCondition = terminalCondition;
        this.nPopulation = nPopulation;
        this.nTours = nTours;
        this.nNeighbourSolutions = nNeighbourSolutions;
        this.nSharedNSolutions = nSharedNSolutions;

    }

    @Override
    public String generateResultString() {
        //todo:
        return super.generateResultString();
    }

    @Override
    public int getIterationCount() {
        return iterationCount;
    }


    public Population generateInitialPopulation(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator, int c) {
        List<Representation> initialStates = solutionGenerator.generate(problem,rng,c);
        Population initialPopulation = new ListPopulation();
        for(Representation r: initialStates)
        {
            Individual i = new SimpleIndividual(r,problem.cost(r));
            initialPopulation.add(i);
            updateBestIfNecessary(i.getRepresentation(),i.getCost());
            increaseNeighboringCount();
        }
        return initialPopulation;
    }

    @Override
    public void perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {

        iterationCount =0;

        RandUtil.setRNG(rng);

        generateInitialFlock(problem,solutionGenerator);



        int ind;
        double leaderCost;
        boolean leftSide = true;
        Bird temp;
        Individual[] neighbours;
        boolean finished= false;

        while (!terminalCondition.isSatisfied(this,problem))
        {
            for(int j=0; j<nTours; j++) {

                /*
                if (terminalCondition.isSatisfied(this,problem)) {
                    finished = true;
                    break;
                }*/

                flock.generateNeighbors(rng,problem,neighboringFunction,nNeighbourSolutions,nSharedNSolutions);

                Individual leaderNeighbors[] = flock.getLeader().getNeighbours();
                Individual leaderCandidate = leaderNeighbors[0];


                updateBestIfNecessary(leaderCandidate.getRepresentation(),leaderCandidate.getCost());
                increaseNeighboringCount(leaderNeighbors.length*neighboringFunction.neighboringCount());


                if (leaderCandidate.getCost() < flock.getLeader().getIndividual().getCost()) {
                    flock.setLeader(new Bird(leaderCandidate));
                }

                //add nb2, nb4, ..., nb_2x to the neighbour set of s_left1
                for (int l = 2; l <= 2 * nSharedNSolutions; l += 2) {
                    flock.getLeftLimitBird().addSharedNeighbour(leaderNeighbors[l]);
                }

                //add nb3, nb5, ..., nb_2x+1 to the neighbour set of s_right1
                for (int l = 1; l < 2 * nSharedNSolutions + 1; l += 2) {
                    flock.getRightLimitBird().addSharedNeighbour(leaderNeighbors[l]);
                }


                improveWing(problem, flock.getLeftLimit(), true);
                improveWing(problem, flock.getRightLimit(), false);
                increaseNeighboringCount(nPopulation*(nNeighbourSolutions-nSharedNSolutions)*neighboringFunction.neighboringCount());
            }

            if (finished)
                break;


            int leaderIndex = flock.getLeaderIndex();
            ind = flock.getLeaderIndex();

            if(leftSide){
                for(int u=0; u<leaderIndex; u++){
                    flock.swapBirds(ind,ind-1);
                    ind--;
                }
            }
            else{
                for(int u=0; u<leaderIndex; u++){
                    flock.swapBirds(ind,ind+1);
                    ind++;
                }
            }

            leftSide = !leftSide;

            iterationCount++;

            //fireIterationEvent(new EAIterationEvent(iterationCount,getNeighboringCount(), bestKnownCost,bestKnownSolution));
            //System.out.println(iterationCount+"-iteration: Average-F:"+ PopulationUtil.averageFitness(population.getIndividuals())+"  Best-F:"+ population.getBest());
        }
        //printBest();
    }

    private void improveWing(OptimizationProblem problem,int i, boolean isLeft) {

        Individual[] neighbours;
        Individual[] allNeighbours;
        while(true) {

            if (isLeft && (!controlLeftWing(i)))
                break;
            else if (!isLeft && !controlRightWing(i))
                break;

            Bird bird = flock.getBird(i);
            neighbours = bird.getNeighbours();

            updateBestIfNecessary(neighbours[0].getRepresentation(),neighbours[0].getCost());



            // exploitation
            if(neighbours[0].getCost() < flock.getBird(i).getIndividual().getCost()){
                allNeighbours = new Individual[nNeighbourSolutions-1];
                System.arraycopy(neighbours, 1, allNeighbours, 0, neighbours.length-1);

                for(int l=neighbours.length-1, u=0; l<nNeighbourSolutions-1; l++, u++)
                    allNeighbours[l] = flock.getBird(i).getSharedNeighbours().get(u);

                flock.setBird(i, new Bird(neighbours[0]));
            }
            // exploration
            else if ( flock.getBird(i).getSharedNeighbours().getBestCost() < flock.getBird(i).getIndividual().getCost())
            {
                allNeighbours = new Individual[nNeighbourSolutions-1];
                System.arraycopy(neighbours, 0, allNeighbours, 0, neighbours.length);


                for(int j=neighbours.length, u=1; j<nNeighbourSolutions-1; j++, u++)
                    allNeighbours[j] = flock.getBird(i).getSharedNeighbours().get(u);

                flock.setBird(i,new Bird(flock.getBird(i).getSharedNeighbours().getBest()));
            }

            else {
                allNeighbours = new Individual[nNeighbourSolutions];
                System.arraycopy(neighbours, 0, allNeighbours, 0, neighbours.length);

                for (int j = neighbours.length, u = 0; j < nNeighbourSolutions; j++, u++)
                    allNeighbours[j] = flock.getBird(i).getSharedNeighbours().get(u);
            }

            Arrays.sort(allNeighbours,new CostBasedComparator());

            // share x-best neighbours
            if(isLeft){
                if(i != 0){
                    for(int j=0; j<nSharedNSolutions; j++)
                        flock.getBird(i-1).addSharedNeighbour(allNeighbours[j]);
                }
            }
            else{
                if(i != nPopulation-1){
                    for(int j=0; j<nSharedNSolutions; j++)
                        flock.getBird(i+1).addSharedNeighbour(allNeighbours[j]);
                }
            }



            flock.getBird(i).clearNeighbours();
            flock.getBird(i).clearSharedNeighbours();

            if(isLeft) i--;
            else i++;
        }
    }

    private boolean controlRightWing(int i) {
        return (i<nPopulation);
    }

    private boolean controlLeftWing(int i) {
        return (i>=0);
    }

    private Individual[] generateNeighbors(OptimizationProblem problem, Individual individual, int neighborCount) {
        Individual[] neighbors = new Individual[neighborCount];
        for (int n=0;n<neighborCount;n++)
        {
            neighbors[n] = createNeighbor(problem,individual);
        }
        return neighbors;
    }

    private void generateInitialFlock(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        Population initialPopulation = generateInitialPopulation(problem,solutionGenerator,nPopulation);
        flock = new PFlock(poolSize);
        for (int p =0; p<initialPopulation.size();p++)
        {
            flock.addBird( new Bird(initialPopulation.get(p)) );

        }

    }

    private Individual createNeighbor(OptimizationProblem problem, Individual individual) {
        Individual neighbor = individual.clone();

        Representation r = neighboringFunction.apply(problem,neighbor.getRepresentation());
        neighbor = new SimpleIndividual(r, problem.cost(r));

        updateBestIfNecessary(neighbor.getRepresentation(),neighbor.getCost());

        increaseNeighboringCount();

        return neighbor;
    }


    @Override
    public String defaultName() {
        return "PMBO";
    }


    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {
        if (params.length<8)
            throw new InvalidParameters("PMBO needs 8 params. You provided:"+ params.length);

        List<MutationOperator> neighborFunctions = EAService.MutationOperators.createMutationOperators(params[0],problem);
        TerminalCondition terminalCondition = EAService.TerminalConditions.createTerminalCondition(params[1],problem);
        int nPopulation = Integer.parseInt(params[2]);
        int nTours = Integer.parseInt(params[3]);
        int nNeighborSolutions = Integer.parseInt(params[4]);
        int nSharedNSolutions = Integer.parseInt(params[5]);

        RNG r = RandUtil.getDefaultRNG();
        if (params.length==7)
            r = EAService.RNGs.createRNG(params[6],problem);

        PMBO mbo = new PMBO(neighborFunctions,terminalCondition,nPopulation,nTours,nNeighborSolutions,nSharedNSolutions,r);

        mbo.poolSize= Integer.parseInt(params[7]);

        return mbo;
    }
}
