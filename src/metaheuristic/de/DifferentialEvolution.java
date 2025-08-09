package metaheuristic.de;

import base.OptimizationProblem;
import base.TerminalCondition;
import metaheuristic.AbstractMetaheuristic;
import metaheuristic.BasePIterationEvent;
import metaheuristic.BaseSIterationEvent;
import metaheuristic.pso.base.ContinousProblem;
import problems.base.InitialSolutionGenerator;
import representation.AgeingIndividual;
import representation.DoubleVector;
import representation.ListPopulation;
import representation.base.Individual;
import representation.base.Population;

import java.util.ArrayList;
import java.util.List;

public class DifferentialEvolution extends AbstractMetaheuristic {

    private int populationSize = 50;
    private double crossoverRate = 0.9;
    private double differentialWeight = 0.8;

    private Population population;
    private BoundaryHandler boundaryHandler;


    private long iterationCount = 0;


    public DifferentialEvolution(BoundaryHandler boundaryHandler, TerminalCondition tc)
    {
        this.boundaryHandler = boundaryHandler;
        this.terminalCondition = tc;
    }

    public DifferentialEvolution(BoundaryHandler boundaryHandler, TerminalCondition tc, int populationSize, double crossoverRate, double differentialWeight) {
        this(boundaryHandler,tc);
        this.populationSize = populationSize;
        this.crossoverRate = crossoverRate;
        this.differentialWeight = differentialWeight;
    }


    public void init(OptimizationProblem problem, InitialSolutionGenerator isg) {
        super.init(problem);
        iterationCount=0;

        // Initialize population
        ContinousProblem cProblem = (ContinousProblem) problem;
        generateInitialPopulation(cProblem, isg);
    }

    @Override
    public void perform(OptimizationProblem problem, InitialSolutionGenerator solutionGenerator) {
        if (!(problem instanceof ContinousProblem)) {
            throw new IllegalArgumentException("DifferentialEvolution requires a ContinousProblem.");
        }
        init(problem,solutionGenerator);

        ContinousProblem cProblem = (ContinousProblem) problem;
        int dimension = cProblem.getDimensionCount();


        while (!terminalCondition.isSatisfied(this,problem)) {
            iterationCount++;
            Population newPopulation = new ListPopulation();

            for (int i = 0; i < populationSize; i++) {
                DoubleVector target = (DoubleVector) population.get(i).getRepresentation();

                // Mutation
                DoubleVector mutant = performMutation(i, dimension);
                boundaryHandler.handle(cProblem,mutant.getValues(),target.getValues());

                DoubleVector trial = performCrossOver(dimension, mutant, target);



                // Selection
                double trialCost = problem.cost(trial);
                double targetCost = problem.cost(target);
                if (trialCost < targetCost) {
                    newPopulation.add(new AgeingIndividual(trial,trialCost));
                    updateBestIfNecessary(trial, trialCost);
                } else {
                    newPopulation.add(new AgeingIndividual(target,targetCost));
                }

                increaseNeighboringCount();
            }

            population = newPopulation;

            if (debugTrace) {
                printBest();
            }

            Individual best = population.getBest();
            fireIterationEvent(new BasePIterationEvent(iterationCount,neighboringCount,best.getCost(),best.getRepresentation(),population));
        }

        logEnd();
    }

    private DoubleVector performCrossOver(int dimension, DoubleVector mutant, DoubleVector target) {
        DoubleVector trial = new DoubleVector(dimension);
        int randIndex = (int) (Math.random() * dimension);
        for (int d = 0; d < dimension; d++) {
            if (Math.random() < crossoverRate || d == randIndex) {
                trial.set(d, mutant.get(d));
            } else {
                trial.set(d, target.get(d));
            }
        }
        return trial;
    }

    private DoubleVector performMutation(int index, int dimension) {
        int a, b, c;
        do { a = randomIndex(); } while (a == index);
        do { b = randomIndex(); } while (b == index || b == a);
        do { c = randomIndex(); } while (c == index || c == a || c == b);

        double[] A = ((DoubleVector) population.get(a).getRepresentation()).getValues();
        double[] B = ((DoubleVector) population.get(b).getRepresentation()).getValues();
        double[] C = ((DoubleVector) population.get(c).getRepresentation()).getValues();

        double[] mutant = new double[dimension];
        for (int d = 0; d < dimension; d++) {
            mutant[d] = A[d] + differentialWeight * (B[d] - C[d]);
        }
        return new DoubleVector(mutant);
    }

    private void generateInitialPopulation(ContinousProblem cProblem, InitialSolutionGenerator solutionGenerator) {
        population = new ListPopulation();
        for (int i = 0; i < populationSize; i++) {
            DoubleVector individual = (DoubleVector) solutionGenerator.generate(cProblem,1).get(0);
            double cost = cProblem.cost(individual);
            population.add(new AgeingIndividual(individual, cost));
            updateBestIfNecessary(individual, cost);
        }
    }


    private int randomIndex() {
        return (int) (Math.random() * populationSize);
    }

    @Override
    public int getIterationCount()
    {
        return (int) iterationCount;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String defaultName() {
        return "DE";
    }
}