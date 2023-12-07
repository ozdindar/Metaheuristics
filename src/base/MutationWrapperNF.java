package base;

import metaheuristic.ea.base.MutationOperator;
import representation.SimpleIndividual;
import representation.base.Individual;
import representation.base.Representation;

/**
 * Created by dindar.oz on 13.06.2017.
 */
public class MutationWrapperNF implements NeighboringFunction {

    MutationOperator mutationOperator;

    public MutationWrapperNF(MutationOperator mutationOperator) {
        this.mutationOperator = mutationOperator;
    }

    @Override
    public Individual apply(OptimizationProblem problem, Individual i) {


        Representation representation = mutationOperator.apply(problem,i.getRepresentation());
        double cost = problem.cost(representation);

        return new SimpleIndividual(representation,cost);
    }

    @Override
    public NeighboringFunction clone() {
        return new MutationWrapperNF(mutationOperator);
    }
}
