package metaheuristic.sso;

import base.OptimizationProblem;
import problems.motap.MOTAProblem;
import representation.IntegerAssignment;
import representation.base.Individual;
import representation.base.Representation;
import util.random.RandUtil;

public class SSOParticle {

    Individual solution;
    Individual personalBest;

    public SSOParticle(Individual i) {
        solution = i;
        personalBest = i.clone();

    }


    public void update(OptimizationProblem problem, Representation globalBest, double cg, double cp, double cw) {
        IntegerAssignment assignment = (IntegerAssignment) solution.getRepresentation();
        IntegerAssignment gbAssignment = (IntegerAssignment) globalBest;
        IntegerAssignment pbAssignment = (IntegerAssignment) personalBest.getRepresentation();
        MOTAProblem motaProblem = (MOTAProblem) problem;
        for (int i = 0; i < assignment.getLength(); i++) {
            int p = generateP(assignment.get(i),gbAssignment.get(i),pbAssignment.get(i), motaProblem.getProcessorCount(),cg,cp,cw);
            assignment.set(i,p);
        }
        solution.update(assignment,problem.cost(assignment));
        if (solution.getCost()<personalBest.getCost())
            personalBest = solution.clone();
    }

    private int generateP(Integer xi, Integer gi, Integer pi, int xmax, double cg, double cp, double cw) {
        double r = RandUtil.randDouble();
        if (r<cg)
            return gi;
        else if (r<cp)
            return pi;
        else if (r<cw)
            return xi;
        else return RandUtil.randInt(xmax);
    }

    public Individual getPersonalBest() {
        return personalBest;
    }

    public Individual getSolution() {
        return solution;
    }

    public void setSolution(Individual individual) {
        solution = individual;
        if (solution.getCost()<personalBest.getCost())
            personalBest = solution.clone();
    }
}
