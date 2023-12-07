package problems.motap;

import ilog.concert.*;
import ilog.cplex.IloCplex;

/**
 * Created by dindar.oz on 06.08.2015.
 */
public class CPLEXSolver {

    public static void main(String args[])
    {
        //MOTAProblem p = new MOTAProblem(MOTAPGenerator.createRandomDCS(6, 50, 40),MOTAPGenerator.createRandomTask(10,6,0.8,0.5));

        //solveMOTAP(p, 3600);
        model1();
    }



    public static double solveMOTAP(MOTAProblem problem, double timeOut)
    {
        try {
            IloCplex cplex = new IloCplex();
            IloNumVar x[][] = createVariables(cplex,problem);

            IloNumExpr communicationCost = createCommunicationCostExpression(cplex,problem,x);
            IloNumExpr comReliabilityCost = createCommunicationReliabilityCostExpression(cplex, problem, x);
            communicationCost = cplex.sum(cplex.prod(problem.CostFactor,communicationCost),cplex.prod(problem.ReliabilityFactor,comReliabilityCost));

            IloNumExpr executionCost = createExecutionCostExpression(cplex, problem, x);
            IloNumExpr execReliabilityCost = createExecutionReliabilityCostExpression(cplex, problem, x);
            executionCost = cplex.sum(cplex.prod(problem.CostFactor,executionCost),cplex.prod(problem.ReliabilityFactor,execReliabilityCost));


            IloNumExpr objective = cplex.sum(communicationCost,executionCost);
            cplex.addMinimize(objective);

            addMemoryConstraints(cplex,problem,x);
            addComputationConstraints(cplex, problem, x);

            addEqualityConstraints(cplex,problem,x);

            cplex.setParam(IloCplex.DoubleParam.TiLim,timeOut);
            boolean solved = cplex.solve();

            cplex.exportModel("sample.lp");


            if (solved && cplex.getStatus() == IloCplex.Status.Optimal)
            {
                printResult(problem, cplex, x,communicationCost, executionCost, comReliabilityCost, execReliabilityCost);
            }
            return cplex.getObjValue();

        } catch (IloException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static void printResult(MOTAProblem problem, IloCplex cplex,IloNumVar x[][], IloNumExpr communicationCost, IloNumExpr executionCost, IloNumExpr comReliabilityCost, IloNumExpr execReliabilityCost) throws IloException {
        System.out.println(problem.dcs);
        System.out.println(problem.task);
        System.out.println("Min Cost: " + cplex.getObjValue());
        System.out.println("Com Cost: " + cplex.getValue(communicationCost));
        System.out.println("Exe Cost: " + cplex.getValue(executionCost));
        System.out.println("CoR Cost: " + cplex.getValue(comReliabilityCost));
        System.out.println("ExR Cost: " + cplex.getValue(execReliabilityCost));
        for (int p = 0;p<problem.dcs.processors.size();p++) {
            for (int m=0;m<problem.task.modules.size();m++)
            {
                System.out.print(valueToString(cplex.getValue(x[p][m]))+"\t");
            }
            System.out.println();
        }

    }

    private static String valueToString(double value) {
        if (Math.abs(value-0)<0.0000001)
            return "0";
        else if (Math.abs(value-1)<0.0000001)
            return "1";
        else return String.valueOf(value);
    }

    private static IloNumExpr createReliabilityCostExpression(IloCplex cplex, MOTAProblem problem, IloNumVar[][] x) throws IloException {
        IloNumExpr communicationReliabilityCost = createCommunicationReliabilityCostExpression(cplex, problem, x);
        IloNumExpr executionReliabilityCost     = createExecutionReliabilityCostExpression(cplex, problem, x);
        return cplex.sum(communicationReliabilityCost,executionReliabilityCost);
    }

    private static void addEqualityConstraints(IloCplex cplex, MOTAProblem problem, IloNumVar[][] x) throws IloException {
        int processorCount = problem.dcs.processors.size();
        int moduleCount = problem.task.modules.size();
        for (int m=0;m<moduleCount;m++) {
            IloLinearNumExpr exp = cplex.linearNumExpr();
            for (int p = 0;p<processorCount;p++)
            {
                exp.addTerm(1,x[p][m]);
            }
            cplex.addEq(1, exp);
        }
    }

    private static void addMemoryConstraints(IloCplex cplex, MOTAProblem problem, IloNumVar[][] x) throws IloException {
        int processorCount = problem.dcs.processors.size();
        int moduleCount = problem.task.modules.size();

        for (int p = 0;p<processorCount;p++) {
            IloLinearNumExpr exp = cplex.linearNumExpr();
            for (int m=0;m<moduleCount;m++)
            {
                exp.addTerm( problem.task.getModules().get(m).getMemoryRequirement(),x[p][m]);
            }
            cplex.addGe(problem.dcs.processors.get(p).getTotalMemory(),exp);
        }
    }

    private static void addComputationConstraints(IloCplex cplex, MOTAProblem problem, IloNumVar[][] x) throws IloException {
        int processorCount = problem.dcs.processors.size();
        int moduleCount = problem.task.modules.size();

        for (int p = 0;p<processorCount-1;p++) {
            IloLinearNumExpr exp = cplex.linearNumExpr();
            for (int m=0;m<moduleCount;m++)
            {
                exp.addTerm( problem.task.getModules().get(m).getCRR(),x[p][m]);
            }
            cplex.addGe(problem.dcs.processors.get(p).getTotalComputationalResource(),exp);
        }
    }


    private static IloNumExpr createExecutionCostExpression(IloCplex cplex, MOTAProblem problem, IloNumVar[][] x) throws IloException {
        int processorCount = problem.dcs.processors.size();
        int moduleCount = problem.task.modules.size();
        IloLinearNumExpr exp = cplex.linearNumExpr();

        for (int p1 = 0;p1<processorCount;p1++)
        {
            for (int m1=0;m1<moduleCount;m1++)
            {
                exp.addTerm(problem.dcs.execution_cost_of(p1, m1, problem.task),x[p1][m1]);
            }
        }

        return exp;
    }

    private static IloNumExpr createExecutionReliabilityCostExpression(IloCplex cplex, MOTAProblem problem, IloNumVar[][] x) throws IloException {
        int processorCount = problem.dcs.processors.size();
        int moduleCount = problem.task.modules.size();
        IloLinearNumExpr exp = cplex.linearNumExpr();

        for (int p1 = 0;p1<processorCount;p1++)
        {
            for (int m1=0;m1<moduleCount;m1++)
            {
                double val = problem.dcs.processors.get(p1).getFailureRate()*problem.task.getModules().get(m1).getAET(p1);
                if (val == 0)
                    continue;
                exp.addTerm(val,x[p1][m1]);
            }
        }

        return exp;
    }

    private static IloNumExpr createCommunicationCostExpression(IloCplex cplex,MOTAProblem problem, IloNumVar[][] x) throws IloException {
        int processorCount = problem.dcs.processors.size();
        int moduleCount = problem.task.modules.size();
        IloQuadNumExpr exp = cplex.quadNumExpr();

        for (int p1 = 0;p1<processorCount-1;p1++)
        {
            for (int p2 =p1+1;p2<processorCount;p2++)
            {
                for (int m1=0;m1<moduleCount-1;m1++)
                {
                    for (int m2=m1+1;m2<moduleCount;m2++)
                    {
                        double val = problem.dcs.communication_cost_of(p1, p2, m1, m2, problem.task);
                        if (val == 0)
                            continue;
                        exp.addTerm(val, x[p1][m1], x[p2][m2]);
                        exp.addTerm(val, x[p1][m2], x[p2][m1]);
                    }
                }
            }
        }

        return exp;
    }

    private static IloNumExpr createCommunicationReliabilityCostExpression(IloCplex cplex,MOTAProblem problem, IloNumVar[][] x) throws IloException {
        int processorCount = problem.dcs.processors.size();
        int moduleCount = problem.task.modules.size();
        IloQuadNumExpr exp = cplex.quadNumExpr();

        for (int p1 = 0;p1<processorCount-1;p1++)
        {
            for (int p2 =p1+1;p2<processorCount;p2++)
            {
                ICommunicationLink link = problem.dcs.getLink(p1,p2);

                for (int m1=0;m1<moduleCount-1;m1++)
                {
                    for (int m2=m1+1;m2<moduleCount;m2++)
                    {
                        int c_m1m2 = problem.task.getCommunication(m1,m2);

                        double val = link.getFailureRate()*c_m1m2/link.getTransmissionRate();
                        if (val == 0)
                            continue;
                        exp.addTerm(val, x[p1][m1], x[p2][m2]);
                        exp.addTerm(val, x[p1][m2], x[p2][m1]);
                    }
                }
            }
        }

        return exp;
    }

    private static IloNumVar[][] createVariables(IloCplex cplex,MOTAProblem problem) throws IloException {
        int processorCount = problem.dcs.processors.size();
        int moduleCount = problem.task.modules.size();
        IloNumVar x[][] = new IloNumVar[processorCount][];
        for (int p = 0;p<processorCount;p++)
        {
            x[p] = new IloNumVar[moduleCount];
            for (int m =0;m<moduleCount;m++)
            {
                x[p][m] = cplex.numVar(0,1,IloNumVarType.Bool);
            }
        }

        return x;
    }

    public static void model1()
    {
        try {
            IloCplex cplex = new IloCplex();

            // variables
            IloNumVar x = cplex.numVar(0,Double.MAX_VALUE,"x");
            IloNumVar y = cplex.numVar(0,Double.MAX_VALUE,"y");

            // expressions


            IloLinearNumExpr objective = cplex.linearNumExpr();
            objective.addTerm(0.12,x);
            objective.addTerm(0.15,y);

            // define objective
            cplex.addMinimize(objective);

            // define constraints
            cplex.addGe(cplex.sum(cplex.prod(60,x),cplex.prod(60,y)),300);
            cplex.addGe(cplex.sum(cplex.prod(12,x),cplex.prod(6,y)),36);
            cplex.addGe(cplex.sum(cplex.prod(10,x),cplex.prod(30,y)),90);

            if (cplex.solve())
            {
                System.out.println("obj = " + cplex.getObjValue());
                System.out.println("x = " + cplex.getValue(x));
                System.out.println("y = " + cplex.getValue(y));
            }else {
                System.out.println("CPLEX could not solve the problem");
            }



        } catch (IloException e) {
            e.printStackTrace();
        }
    }

}
