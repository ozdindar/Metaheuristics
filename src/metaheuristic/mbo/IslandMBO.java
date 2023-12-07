package metaheuristic.mbo;

import base.OptimizationProblem;
import base.TerminalCondition;
import exceptions.InvalidParameters;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.MetaHeuristic;
import metaheuristic.ea.EAIterationEvent;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by dindar.oz on 01.06.2015.
 *
 *
 * Reference Paper: "Migrating Birds Optimization: A new metaheuristic approach and its performance on quadratic assignment problem"
 *
 */
public class IslandMBO extends AbstractMetaheuristic
{
    RNG rng = RandUtil.getDefaultRNG();

    List<MutationOperator> neighboringFunctions;
    private int nPopulation, nTours, nNeighbourSolutions, nSharedNSolutions;

    List<IslandFlock> flocks = new ArrayList<IslandFlock>();

    int poolSize;


    ForkJoinPool pool;
    
    int immigrationPeriod=5;
    int immigrantCount = 5;
    
    private int populationCount = 4;
    int iterationCount =0;

    public IslandMBO(List<MutationOperator> neighboringFunctions, TerminalCondition terminalCondition, int nPopulation, int nTours, int nNeighbourSolutions, int nSharedNSolutions, RNG rng) {
        this.neighboringFunctions = neighboringFunctions;
        this.terminalCondition = terminalCondition;
        this.nPopulation = nPopulation;
        this.nTours = nTours;
        this.nNeighbourSolutions = nNeighbourSolutions;
        this.nSharedNSolutions = nSharedNSolutions;
        this.rng = rng;

    }

    public IslandMBO(List<MutationOperator> neighboringFunctions, TerminalCondition terminalCondition, int nPopulation, int nTours, int nNeighbourSolutions, int nSharedNSolutions) {
        this.neighboringFunctions = neighboringFunctions;
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

    @Override
    public void init(OptimizationProblem problem) {
        super.init(problem);
        flocks.clear();
        pool = new ForkJoinPool(poolSize);
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

        for(int i = 0; i < populationCount; i++)
        {
        	IslandFlock flock = generateInitialFlock(problem,solutionGenerator);
        	flocks.add(i, flock);
        }

        while (!terminalCondition.isSatisfied(this,problem))
        {
        	Collection<Callable<Object> > callables= new ArrayList<>();
    		
    		for(int i = 0; i < populationCount; i++)
            {
    			final int current = i;

    			MetaHeuristic alg = this;
    			//MetaHeuristic thisAlg = this;
    			Callable<Object> islandThread = new Callable<Object>() {
    	            @Override
    	            public Object call() throws Exception {

    	            	IslandFlock flock = flocks.get(current);
                        boolean side= true;
    	            	for (int iteration = 0; iteration < immigrationPeriod; iteration++) {
                            if ((terminalCondition.isSatisfied(alg,problem)))
                                break;
    	            	    makeTours(flock, problem);
                            reorderFlock(flock, side);
                            side=!side;
                        }


    	                return null;
    	            }
    	        };
    			
    	        callables.add(islandThread);
            }
    		pool.invokeAll(callables);
    		

    		
    		iterationCount+=immigrationPeriod;

    		//synchronize
            migrateIslands();

            fireIterationEvent(new EAIterationEvent(iterationCount,getNeighboringCount(), bestKnownCost,bestKnownSolution));
            //System.out.println(iterationCount+"-iteration: Average-F:"+ PopulationUtil.averageFitness(population.getIndividuals())+"  Best-F:"+ population.getBest());
        }
        printBest();
    }

    private void reorderFlock(IslandFlock flock, boolean side) {
        int leaderIndex = flock.getLeaderIndex();
        int ind = flock.getLeaderIndex();

        if(side){
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
    }

    private void makeTours(IslandFlock flock, OptimizationProblem problem) {
        for(int j=0; j<nTours; j++) {

        /*if (terminalCondition.isSatisfied(thisAlg,problem)) {
            finished = true;
            break;
        }*/

            //	generate k neighbours of leader solution
            //neighbours = generateNeighbors(problem, flock.getLeader().getIndividual(), nNeighbourSolutions);
            flock.generateNeighbors(rng,problem,neighboringFunctions,nNeighbourSolutions,nSharedNSolutions);

            Individual leaderNeighbors[] = flock.getLeader().getNeighbours();
            Individual leaderCandidate = leaderNeighbors[0];


            updateBestIfNecessary(leaderCandidate.getRepresentation(),leaderCandidate.getCost());
            increaseNeighboringCount(leaderNeighbors.length);


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


            improveWing(problem, flock, flock.getLeftLimit(), true);
            improveWing(problem, flock, flock.getRightLimit(), false);

        }
    }

    private void migrateIslands() {
	    	
    	for(int i = 0; i < populationCount; i++)
    	{
    		IslandFlock from;
    		if(i != populationCount-1)  //populations other than the last one
    			from = flocks.get(i+1);
    		else 
    			from = flocks.get(0);
    	
    		IslandFlock to = flocks.get(i);
    		to.setLeader(from.getLeader());
    	}
    }

    private void improveWing(OptimizationProblem problem, IslandFlock flock, int i, boolean isLeft) {

        Individual[] neighbours;
        Individual[] allNeighbours;
        while(true) {

            if (isLeft && (!controlLeftWing(i)))
                break;
            else if (!isLeft && !controlRightWing(i))
                break;

            Bird bird = flock.getBird(i);
            neighbours = bird.getNeighbours();

            if (neighbours.length != nNeighbourSolutions-nSharedNSolutions)
            {
                boolean aha = true;
            }

            updateBestIfNecessary(neighbours[0].getRepresentation(),bird.getNeighbours()[0].getCost());
            increaseNeighboringCount(neighbours.length);


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

    private IslandFlock generateInitialFlock(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        Population initialPopulation = generateInitialPopulation(problem,solutionGenerator,nPopulation);
        IslandFlock flock = new IslandFlock();
        for (int p =0; p<initialPopulation.size();p++)
        {
            flock.addBird( new Bird(initialPopulation.get(p)) );

        }
        return flock;
    }

    private Individual createNeighbor(OptimizationProblem problem, Individual individual) {
        Individual neighbor = individual.clone();

        int nf = rng.randInt(neighboringFunctions.size());
        Representation r = neighboringFunctions.get(nf).apply(problem,neighbor.getRepresentation());
        neighbor = new SimpleIndividual(r, problem.cost(r));

        updateBestIfNecessary(neighbor.getRepresentation(),neighbor.getCost());

        increaseNeighboringCount();

        return neighbor;
    }


    @Override
    public String defaultName() {
        return "IslandMBO";
    }


    public static MetaHeuristic createInstance(OptimizationProblem problem, String[] params) {
        if (params.length<11)
            throw new InvalidParameters("HMBO needs 11 params. You provided:"+ params.length);

        List<MutationOperator> neighborFunctions = EAService.MutationOperators.createMutationOperators(params[0],problem);
        TerminalCondition terminalCondition = EAService.TerminalConditions.createTerminalCondition(params[1],problem);
        int nPopulation = Integer.parseInt(params[2]);
        int nTours = Integer.parseInt(params[3]);
        int nNeighborSolutions = Integer.parseInt(params[4]);
        int nSharedNSolutions = Integer.parseInt(params[5]);

        RNG r = RandUtil.getDefaultRNG();
        if (params.length==7)
            r = EAService.RNGs.createRNG(params[6],problem);

        IslandMBO mbo = new IslandMBO(neighborFunctions,terminalCondition,nPopulation,nTours,nNeighborSolutions,nSharedNSolutions,r);

        mbo.populationCount = Integer.parseInt(params[7]);

        mbo.immigrationPeriod = Integer.parseInt(params[8]);

        mbo.immigrantCount = Integer.parseInt(params[9]);

        mbo.poolSize = Integer.parseInt(params[10]);
        return mbo;
    }
}
