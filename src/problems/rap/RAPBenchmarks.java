package problems.rap;

/**
 * Created by dindar.oz on 6/23/2017.
 */
public class RAPBenchmarks {

    public static RAP createBMInstance(int nmax,int cost, int weight) {
        Component components[][] = new Component[][]{
                {new Component(0.9, 1, 3), new Component(0.93, 1, 4), new Component(0.91, 2, 2), new Component(0.95, 2, 5)},
                {new Component(0.95, 2, 8), new Component(0.94, 1, 10), new Component(0.93, 1, 9)},
                {new Component(0.85, 2, 7), new Component(0.90, 3, 5), new Component(0.87, 1, 6), new Component(0.92, 4, 4)},
                {new Component(0.83, 3, 5), new Component(0.87, 4, 6), new Component(0.85, 5, 4)},
                {new Component(0.94, 2, 4), new Component(0.93, 2, 3), new Component(0.95, 3, 5)},

                {new Component(0.99, 3, 5), new Component(0.98, 3, 4), new Component(0.97, 2, 5), new Component(0.96, 2, 4)},
                {new Component(0.91, 4, 7), new Component(0.92, 4, 8), new Component(0.94, 5, 9)},
                {new Component(0.81, 3, 4), new Component(0.90, 5, 7), new Component(0.91, 6, 6)},
                {new Component(0.97, 2, 8), new Component(0.99, 3, 9), new Component(0.96, 4, 7), new Component(0.91, 3, 8)},
                {new Component(0.83, 4, 6), new Component(0.85, 4, 5), new Component(0.90, 5, 6)},

                {new Component(0.94, 3, 5), new Component(0.95, 4, 6), new Component(0.96, 5, 6)},
                {new Component(0.79, 2, 4), new Component(0.82, 3, 5), new Component(0.85, 4, 6), new Component(0.90, 5, 7)},
                {new Component(0.98, 2, 5), new Component(0.99, 3, 5), new Component(0.97, 2, 6)},
                {new Component(0.90, 4, 6), new Component(0.92, 4, 7), new Component(0.95, 5, 6), new Component(0.99, 6, 9)}
        };



        int k[] = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};

        return new RAP(cost, weight, nmax, components, k);


    }

    public static RAP[] createBMInstances(int nmax,int costs[], int weights[]) {

        RAP[] raps= new RAP[costs.length*weights.length];
        for (int c = 0; c < costs.length; c++) {
            for (int w = 0; w < weights.length; w++) {
                raps[c*weights.length+w] = createBMInstance(costs[c], weights[w], nmax);
            }
        }
        return raps;
    }





}
