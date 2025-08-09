package metaheuristic.ss;

import base.OptimizationProblem;
import exceptions.UnknownObjectType;
import metaheuristic.AbstractSMetaheuristic;
import metaheuristic.ea.terminalcondition.NotImprovementTC;
import metaheuristic.rbeamsearch.MOTAPRBeamChildGenerator;
import metaheuristic.rbeamsearch.RBeamSearch;
import metaheuristic.rbeamsearch.SimpleLocalSearch;
import metaheuristic.rbeamsearch.SimpleRBeamSearch;
import problems.motap.mutation.Release.BasicRelease;
import problems.motap.mutation.SimpleMOTAPNF;

public class SSService {

    private static String PARAMS_DELIMITER = "|";
    public static String OBJECT_DELIMITER = ";";

    static String extractName(String nameStr)
    {
        int delimiterIndex = nameStr.indexOf(PARAMS_DELIMITER);
        if (delimiterIndex<0)
            return nameStr;
        else
            return nameStr.substring(0,delimiterIndex);
    }

    static String[] extractParams(String nameStr)
    {
        int delimiterIndex = nameStr.indexOf(PARAMS_DELIMITER);
        if (delimiterIndex<0)
            return null;
        else
            return nameStr.substring(delimiterIndex+1).split("\\"+PARAMS_DELIMITER);
    }

    public static class SMetaheuristics {


        public static final String RBeamSearch = "rbeamsearch";
        public static final String SimpleRBeamSearch = "simplerbeamsearch";
        public static final String SimpleLocalSearch = "simplelocalsearch";


        public static AbstractSMetaheuristic createAbstractSMetaheuristic(String nfName, OptimizationProblem problem) {

            String name = extractName(nfName);
            String params[] = extractParams(nfName);

            if (name.equals(RBeamSearch)) {
                metaheuristic.rbeamsearch.RBeamSearch rbs = new RBeamSearch(Integer.parseInt(params[0]),Integer.parseInt(params[1]),Integer.parseInt(params[2]), new MOTAPRBeamChildGenerator(),new BasicRelease());
                Integer beamSize = Integer.parseInt(params[3]);
                rbs.setBEAM_SIZE(beamSize);
                rbs.setROULETTE_SIZE(beamSize*2);
                return rbs;
            }
            else if (name.equals(SimpleRBeamSearch)) {
                SimpleRBeamSearch srbs = new SimpleRBeamSearch(Integer.parseInt(params[0]),Integer.parseInt(params[1]),Integer.parseInt(params[2]), new MOTAPRBeamChildGenerator(),new BasicRelease(),new NotImprovementTC(2));
                Integer beamSize = Integer.parseInt(params[3]);
                srbs.setBEAM_SIZE(beamSize);
                srbs.setROULETTE_SIZE(beamSize*2);
                return srbs;
            }else if(name.equals(SimpleLocalSearch)){
                return new SimpleLocalSearch(new SimpleMOTAPNF(1),new NotImprovementTC(3));
            }

            else throw new UnknownObjectType("Unknown Mutation:" + name);
        }

    }
}
