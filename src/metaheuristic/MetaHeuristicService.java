package metaheuristic;


import base.OptimizationProblem;
import metaheuristic.dde.DDE;
import metaheuristic.dpso.DPSO;
import metaheuristic.ea.EA;
import metaheuristic.ea.IslandEA;
import metaheuristic.grasp.PGrasp;
import metaheuristic.hbmo.HBMO;
import metaheuristic.ils.PILS;
import metaheuristic.island.GenericIsland;
import metaheuristic.island.IslandModul;
import metaheuristic.mbo.MBO;
import metaheuristic.mbo.MBO2;
import metaheuristic.mbo.PMBO;
import metaheuristic.phbmo.PHBMO;
import metaheuristic.pso.PSO;
import metaheuristic.sos.SOS;
import metaheuristic.ss.PSSWM;
import metaheuristic.ss.ScatterSearch;
import metaheuristic.ss2.ScatterSearch2;
import metaheuristic.sso.SSO;
import metaheuristic.tabu.TabuSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dindar.oz on 19.06.2015.
 */
public class MetaHeuristicService {

    public static final String M_BA     = "BA";
    public static final String M_MBO    = "MBO";
    public static final String M_ABC    = "ABC";
    public static final String M_SA     = "SA";
    public static final String M_EA     = "EA";
    public static final String M_PSO    = "PSO";
    public static final String M_TABU   = "TABU";
    public static final String M_DPSO   = "DPSO";
    public static final String M_DDE    = "DDE";
    public static final String M_SOS    = "SOS";
    public static final String M_MBO2    = "MBO2";
    public static final String M_HBMO   = "HBMO";
    public static final String M_SS   = "SS";
    public static final String M_SS2   = "SS2";
    public static final String M_PMBO    = "PMBO";
    public static final String M_IMBO    = "IMBO";
    public static final String M_IEA    = "IEA";
    public static final String M_IHBMO = "ISLANDHBMO";
    public static final String M_ISS = "ISLANDSS";
    public static final String M_SSO = "SSO";
    public static final String M_ISSO = "ISSO";
    public static final String M_PSSWM   = "PSSWM";
    public static final String M_PHBMO = "PHBMO";
    public static final String M_PGRASP = "PGRASP";
    public static final String M_PILS = "PILS";


    public static MetaHeuristic createMetaheuristic(OptimizationProblem problem, String name, String[] params) {
        if (name.equals(M_EA))
            return EA.createInstance(problem,params);
        else if (name.equals(M_PSO))
            return PSO.createInstance(problem,params);
        else if (name.equals(M_TABU))
            return TabuSearch.createInstance(problem,params);
        else if (name.equals(M_DPSO))
            return DPSO.createInstance(problem,params);
        else if (name.equals(M_DDE))
            return DDE.createInstance(problem,params);
        else if (name.equals(M_SOS))
            return SOS.createInstance(problem, params);
        else if (name.equals(M_SOS))
            return SOS.createInstance(problem, params);
        else if (name.equals(M_MBO))
            return MBO.createInstance(problem, params);
        else if (name.equals(M_MBO2))
            return MBO2.createInstance(problem, params);
        else if (name.equals(M_HBMO))
            return HBMO.createInstance(problem, params);
        else if (name.equals(M_PHBMO))
            return PHBMO.createInstance(problem, params);
        else if (name.equals(M_SS))
            return ScatterSearch.createInstance(problem, params);
        else if (name.equals(M_PSSWM))
            return PSSWM.createInstance(problem, params);
        else if (name.equals(M_SS2))
            return ScatterSearch2.createInstance(problem, params);
        else if (name.equals(M_PMBO))
            return PMBO.createInstance(problem, params);
        else if (name.equals(M_IMBO))
            //return IslandMBO.createInstance(problem, params);
            return createGenericIslandMBO(problem,params);
        else if (name.equals(M_IEA))
            return IslandEA.createInstance(problem, params);
        else if (name.equals(M_SSO))
            return SSO.createInstance(problem, params);
        else if (name.equals(M_PGRASP))
            return PGrasp.createInstance(problem, params);
        else if (name.equals(M_PILS))
            return PILS.createInstance(problem, params);
        // This is done just for the review and implemented ugly. To be refactored later.
        else if (name.equals(M_IHBMO))
           return createGenericIslandHBMO(problem,params);
        else if (name.equals(M_ISS))
                return createGenericIslandSS(problem,params);
        else if (name.equals(M_ISSO))
            return createGenericIslandSSO(problem,params);
        throw  new RuntimeException("Invalid Metaheuristic: "+ name);
    }

    private static MetaHeuristic createGenericIslandSSO(OptimizationProblem problem, String[] params) {
        int moduleCount = Integer.parseInt(params[1]);

        List<IslandModul> moduls = new ArrayList<IslandModul>();
        for (int i = 0; i < moduleCount; i++) {
            moduls.add((IslandModul) SSO.createInstance(problem, Arrays.copyOfRange(params, 5, 7)));
        }
        return GenericIsland.createInstance(problem, params, moduls);
    }

    private static MetaHeuristic createGenericIslandMBO(OptimizationProblem problem, String[] params) {
        int moduleCount = Integer.parseInt(params[1]);

        List<IslandModul> moduls = new ArrayList<IslandModul>();
        for (int i = 0; i < moduleCount; i++) {
            moduls.add((IslandModul) MBO.createInstance(problem, Arrays.copyOfRange(params, 5, 12)));
        }
        return GenericIsland.createInstance(problem, params, moduls);
    }

    private static MetaHeuristic createGenericIslandHBMO(OptimizationProblem problem, String[] params) {
        int moduleCount = Integer.parseInt(params[1]);
        
        List<IslandModul> moduls = new ArrayList<IslandModul>();
        for (int i = 0; i < moduleCount; i++) {
            moduls.add((IslandModul) HBMO.createInstance(problem, Arrays.copyOfRange(params, 5, 12)));
        }
        return GenericIsland.createInstance(problem, params, moduls);
    }

    private static MetaHeuristic createGenericIslandSS(OptimizationProblem problem, String[] params) {
        int moduleCount = Integer.parseInt(params[1]);

        List<IslandModul> moduls = new ArrayList<IslandModul>();
        for (int i = 0; i < moduleCount; i++) {
            moduls.add((IslandModul) ScatterSearch.createInstance(problem, Arrays.copyOfRange(params, 5, 7)));
        }
        return GenericIsland.createInstance(problem, params, moduls);
    }
}
