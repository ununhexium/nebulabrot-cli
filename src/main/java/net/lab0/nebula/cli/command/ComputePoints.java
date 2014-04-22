package net.lab0.nebula.cli.command;

import java.io.FileNotFoundException;
import java.util.Arrays;

import joptsimple.OptionSpec;
import net.lab0.nebula.cli.AbstractCommand;
import net.lab0.nebula.cli.NebulaCLI;
import net.lab0.nebula.cli.VerboseLevel;
import net.lab0.nebula.exception.ProjectException;
import net.lab0.nebula.project.ComputingRoutine;
import net.lab0.nebula.project.PointsComputingParameters;
import net.lab0.nebula.project.Project;

public class ComputePoints
extends AbstractCommand
{
    private OptionSpec<Integer>          treeId;
    private OptionSpec<Integer>          powerOf2;
    private OptionSpec<Integer>          blockSize;
    private OptionSpec<Integer>          maxDepth;
    private OptionSpec<Long>             maxIter;
    private OptionSpec<Long>             minIter;
    private OptionSpec<ComputingRoutine> computingRoutine;
    
    public ComputePoints()
    {
        super("points");
        
        String pointsDesc = "The number of points on the side of the root node. " + "Expressed as a power of 2."
        + " This is the X parameter in 2^X";
        
        treeId = parser.accepts("tree", "The id of the tree that will be split").withRequiredArg()
        .ofType(Integer.class).describedAs("tree id");
        
        powerOf2 = parser.accepts("power", pointsDesc).withRequiredArg().ofType(Integer.class)
        .describedAs("max value: 52");
        
        blockSize = parser
        .accepts("block-size", "The size of block that should be used when reading points to compte from the source")
        .withRequiredArg().ofType(Integer.class);
        
        maxDepth = parser.accepts("max-depth", "The maximum depth that should be used (inclusive)").withRequiredArg()
        .ofType(Integer.class).defaultsTo(256);
        
        maxIter = parser.accepts("max-iter", "The maximum iteration to reach while computing").withRequiredArg()
        .ofType(Long.class);
        
        minIter = parser.accepts("min-iter", "The minimum iteration to reach while computing").withRequiredArg()
        .ofType(Long.class);
        
        computingRoutine = parser.accepts("method", "The computing method to use").withRequiredArg()
        .ofType(ComputingRoutine.class).describedAs("CPU or OCL").defaultsTo(ComputingRoutine.CPU);
    }
    
    @Override
    public boolean execute(Project project)
    {
        int power = powerOf2.value(opt);
        if (power > 52 || power < 3)
        {
            NebulaCLI.cliPrint("The power of 2 (" + power + ") can be at most 52 and must be positive.",
            VerboseLevel.ERROR);
        }
        long minPower = (maxDepth.value(opt) + 2); // min 16 points per leaf node
        if (powerOf2.value(opt) < minPower)
        {
            NebulaCLI.cliPrint("You should use at least 2^" + minPower + " points per side with the max depth "
            + maxDepth.value(opt), VerboseLevel.WARN);
        }
        
        long points = 1L << powerOf2.value(opt);
        System.out.println(Arrays.toString(opt.specs().toArray()));
        PointsComputingParameters parameters = new PointsComputingParameters(treeId.value(opt), maxIter.value(opt),
        points);
        
        parameters.setBlockSize(blockSize.value(opt));
        parameters.setMaxDepth(maxDepth.value(opt));
        parameters.setRoutine(computingRoutine.value(opt));
        parameters.setMinimumIteration(minIter.value(opt));
        
        try
        {
            project.computePoints(parameters);
            return true;
        }
        catch (FileNotFoundException | InterruptedException e)
        {
            NebulaCLI.cliPrint("System error while computing points", e, VerboseLevel.FATAL);
        }
        catch (ProjectException e)
        {
            NebulaCLI.cliPrint("Error while computing points", e, VerboseLevel.ERROR);
        }
        
        return false;
    }
}
