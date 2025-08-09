package metaheuristic.tabu;

import base.NeighboringFunction;
import base.OptimizationProblem;
import exceptions.UnknownObjectType;
import metaheuristic.tabu.base.Aspiration;
import metaheuristic.tabu.base.TabuList;
import metaheuristic.tabu.mediumtermmemory.MOTAPFrequencyMatrix;
import metaheuristic.tabu.mediumtermmemory.MOTAPRecencyMatrix;
import problems.motap.MOTAProblem;
import problems.motap.mutation.GRMR_Tabu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dindar.oz on 10.07.2015.
 */
public class TabuService {

    public static String PARAMS_DELIMITER = "|";
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

    public static class TabuLists {

        public static final String SolutionMemory = "solutionmemory";

        public static TabuList createTabuList(String tlName, OptimizationProblem problem) {

            String name = extractName(tlName);
            String[] params = extractParams(tlName);

            if (name.equals(SolutionMemory))
                return new SolutionMemoryTabuList(Integer.parseInt(params[0]));

            else throw new UnknownObjectType("Unknown TabuList:"+name);
        }
    }

    public static class Aspirations {

        public static final String NULLNAME = "null";

        public static Aspiration createAspiration(String tlName, OptimizationProblem problem) {

            if (tlName.equals(NULLNAME))
                return null;

            String name = extractName(tlName);
            String[] params = extractParams(tlName);


            throw new UnknownObjectType("Unknown TabuList:"+name);
        }
    }

    public static class MediumTermMemories {

        public static final String NULLNAME = "null";
        public static final String MOTAPRECENCY = "motaprecency";
        public static final String MOTAPFREQUENCY = "motapfrequency";


        public static MediumTermMemory createMediumTermMemory(String tlName, OptimizationProblem problem) {

            if (tlName.equals(NULLNAME))
                return null;

            String name = extractName(tlName);
            String[] params = extractParams(tlName);

            if (name.equals(MOTAPRECENCY)) {
                if (params.length==1)
                    return new MOTAPRecencyMatrix((MOTAProblem)problem,Double.parseDouble(params[0]));
                if (params.length==3)
                    return new MOTAPRecencyMatrix(Integer.parseInt(params[0]),Integer.parseInt(params[1]),Double.parseDouble(params[2]));
            }if (name.equals(MOTAPFREQUENCY))
                return new MOTAPFrequencyMatrix((MOTAProblem) problem,Double.parseDouble(params[0]));


            throw new UnknownObjectType("Unknown TabuList:"+name);
        }
    }

    public static class NeighboringFunctions{

        public static String GRMR = "GRMR";

        public static List<NeighboringFunction> createNeighboringFunctions(String nfName, OptimizationProblem problem) {

            String nfNames[] = nfName.split(OBJECT_DELIMITER);

            List<NeighboringFunction> nfList = new ArrayList<>();
            for (int i=0;i<nfNames.length;i++)
            {
                nfList.add(createNeighboringFunction(nfNames[i],problem));
            }

            return nfList;

        }

        public static NeighboringFunction createNeighboringFunction(String nfName, OptimizationProblem problem) {
            String name = extractName(nfName);
            String params[] = extractParams(nfName);

            if (name.equals(GRMR)) {
                if (params == null)
                    return new GRMR_Tabu();
                else
                    return new GRMR_Tabu(Integer.parseInt(params[0]),Integer.parseInt(params[1]));
            }
            else throw new UnknownObjectType("Unknown Mutation:"+name);
        }
    }


}
