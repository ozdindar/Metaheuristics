package metaheuristic.pso;

import base.OptimizationProblem;
import exceptions.UnknownObjectType;
import metaheuristic.pso.base.ParticleHandler;

/**
 * Created by dindar.oz on 10.07.2015.
 */
public class PSOService {

    private static String PARAMS_DELIMITER = "|";
    private static String OBJECT_DELIMITER = ";";

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
            return nameStr.substring(delimiterIndex).split("\\"+PARAMS_DELIMITER);
    }


    public static class ParticleHandlers {

        public static final String DoubleVector = "doublevector";
        public static final String IntegerPermutation = "integerpermutation";

        public static ParticleHandler createParticleHandler(String phName, OptimizationProblem problem)
        {
            String name = extractName(phName);
            String[] params = extractParams(phName);

            if (name.equals(DoubleVector))
                return new DoubleVectorParticleHandler();


            else throw new UnknownObjectType("Unknown Parent Selector:"+name);

        }
    }
}
