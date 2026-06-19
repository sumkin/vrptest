package com.vrp.solver;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;

import java.util.Collection;

public class VrpSolver {

    private static final int MAX_ITERATIONS = 50;

    public static VehicleRoutingProblemSolution solve(VehicleRoutingProblem vrp) {
        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(vrp);
        algorithm.setMaxIterations(MAX_ITERATIONS);

        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
        return Solutions.bestOf(solutions);
    }

    public static void printSolution(VehicleRoutingProblem vrp, VehicleRoutingProblemSolution solution) {
        SolutionPrinter.print(vrp, solution, SolutionPrinter.Print.VERBOSE);
    }
}
