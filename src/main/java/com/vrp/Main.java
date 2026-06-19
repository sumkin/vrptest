package com.vrp;

import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.vrp.reader.ExcelReader;
import com.vrp.reader.InputData;
import com.vrp.solver.VrpProblemBuilder;
import com.vrp.solver.SolutionExporter;
import com.vrp.solver.SolutionFormatter;
import com.vrp.solver.VrpSolver;

public class Main {
    public static void main(String[] args) throws Exception {
        String path = args.length > 0 ? args[0] : "data/Fleet Size_0520.xlsx";
        InputData data = ExcelReader.read(path);
        System.out.println("Loaded: " + data);

        VehicleRoutingProblem vrp = VrpProblemBuilder.build(data);
        System.out.println("VRP problem built: " + vrp.getVehicles().size() + " vehicles, " + vrp.getJobs().size() + " jobs");
        System.out.println("Solving...");

        VehicleRoutingProblemSolution solution = VrpSolver.solve(vrp);
        SolutionFormatter.print(solution);

        String outPath = args.length > 1 ? args[1] : "data/solution.xlsx";
        SolutionExporter.export(solution, outPath);
    }
}
