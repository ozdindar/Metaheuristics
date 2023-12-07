package metaheuristic.ea.mutation;

import base.OptimizationProblem;
import metaheuristic.ea.base.MutationOperator;
import metaheuristic.ea.base.MutationStrategy;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Population;
import representation.base.Representation;
import util.random.RandUtil;

import java.util.List;

public class SimpleMutationStrategy implements MutationStrategy {

    private boolean allowRepetition = false;
    private double MUTATION_PROBABILITY =1;
    private int MUTATION_COUNT=1;

    public void setAllowRepetition(boolean allowRepetition) {
        this.allowRepetition = allowRepetition;
    }

    public SimpleMutationStrategy(double MUTATION_PROBABILITY, int MUTATION_COUNT) {
        this.MUTATION_PROBABILITY = MUTATION_PROBABILITY;
        this.MUTATION_COUNT = MUTATION_COUNT;
    }

    public SimpleMutationStrategy(double mutationRate) {
        MUTATION_PROBABILITY = mutationRate;
    }

    @Override
    public void applyMutations(OptimizationProblem problem, Population population, List<MutationOperator> mutationOperators) {
        if (RandUtil.rollDice(MUTATION_PROBABILITY)) {
            for (int i = 0; i < MUTATION_COUNT; i++) {
                int m = RandUtil.randInt(population.size());
                int mo = RandUtil.randInt(mutationOperators.size());
                Individual victim = population.get(m);


                Representation mutantRep = mutationOperators.get(mo).apply(problem, victim.getRepresentation());

                Individual mutant = new SimpleIndividual(mutantRep, problem.cost(mutantRep));

                if (allowRepetition || !population.contains(mutant)) {
                    population.remove(victim);
                    population.add(mutant);
                }
            }
        }
    }

    @Override
    public int getMutationCount() {
        return MUTATION_COUNT;
    }
}