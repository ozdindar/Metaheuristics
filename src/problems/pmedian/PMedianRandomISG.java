package problems.pmedian;

import base.OptimizationProblem;
import problems.base.InitialSolutionGenerator;
import representation.BinaryString;
import representation.base.Representation;
import util.random.RNG;
import util.random.SecureRandomRNG;

import java.util.*;

public class PMedianRandomISG implements InitialSolutionGenerator {

    @Override
    public List<Representation> generate(OptimizationProblem problem, RNG rng, int c) {
        if (!(problem instanceof PMedianProblem)) {
            throw new IllegalArgumentException("Problem must be an instance of PMedianProblem.");
        }

        PMedianProblem pMedian = (PMedianProblem) problem;
        int n = pMedian.getDimension();
        int p = pMedian.getP();

        List<Representation> solutions = new ArrayList<>();

        for (int i = 0; i < c; i++) {
            Set<Integer> facilityIndices = new HashSet<>();
            while (facilityIndices.size() < p) {
                facilityIndices.add(rng.randInt(n));
            }

            StringBuilder code = new StringBuilder();
            for (int j = 0; j < n; j++) {
                code.append('0');
            }
            for (int index : facilityIndices) {
                code.setCharAt(index, '1');
            }

            solutions.add(new BinaryString(code.toString()));
        }

        return solutions;
    }

    @Override
    public List<Representation> generate(OptimizationProblem problem, int c) {
        return generate(problem, new SecureRandomRNG(), c);
    }

    public static void main(String[] args) {
        PMedianProblem problem = new PMedianProblem(new double[100][100],30);
        PMedianRandomISG isg= new PMedianRandomISG();
        List<Representation> bsList = isg.generate(problem,new SecureRandomRNG(),5);

        bsList.forEach(System.out::println);
    }
}
