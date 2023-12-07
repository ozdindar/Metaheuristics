package problems.motap.mutation.Release;

import base.OptimizationProblem;
import exceptions.UnknownObjectType;

public class ReleaseService {

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

    public static class ReleaseHandlers {

        public static final String BasicRelease   = "basicrelease";
        public static final String RouletteRelease = "rouletterelease";
        public static final String ReleaseWithMemory = "releasewithmemory";

        public static ReleaseHandler createReleaseHandler(String tcName, OptimizationProblem problem) {

            String name = extractName(tcName);
            String[] params = extractParams(tcName);


            if (name.equals(BasicRelease))
                return new BasicRelease();
            else if (name.equals(RouletteRelease))
                return new RouletteRelease();
            else if (name.equals(ReleaseWithMemory))
                return new ReleaseWithMemory();

            else throw new UnknownObjectType("Unknown Release Handler:"+name);
        }
    }
}
