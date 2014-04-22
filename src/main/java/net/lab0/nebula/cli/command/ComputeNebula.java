package net.lab0.nebula.cli.command;

import java.util.List;

import joptsimple.OptionSpec;
import net.lab0.nebula.cli.AbstractCommand;
import net.lab0.nebula.cli.NebulaCLI;
import net.lab0.nebula.cli.VerboseLevel;
import net.lab0.nebula.project.Project;
import net.lab0.tools.geom.Point;
import net.lab0.tools.geom.PointInterface;
import net.lab0.tools.geom.Rectangle;
import net.lab0.tools.geom.RectangleInterface;

public class ComputeNebula
extends AbstractCommand
{
    private OptionSpec<Integer> pointsId;
    private OptionSpec<Double>  viewport;
    private OptionSpec<Integer> xRes;
    private OptionSpec<Integer> yRes;
    private OptionSpec<Long>    maxIter;
    private OptionSpec<Long>    minIter;
    private OptionSpec<String>  imageName;
    
    public ComputeNebula()
    {
        super("nebula");
        
        pointsId = parser.accepts("points", "The id of the points set that will be split").withRequiredArg()
        .ofType(Integer.class).describedAs("points id");
        
        viewport = parser.accepts("viewport").withRequiredArg().ofType(Double.class).withValuesSeparatedBy(',')
        .describedAs("Ax,Ay,Bx,By");
        
        xRes = parser.accepts("x-res", "The width of the ouput image").withRequiredArg().ofType(Integer.class)
        .describedAs("X resolution");
        
        yRes = parser.accepts("y-res", "The height of the ouput image").withRequiredArg().ofType(Integer.class)
        .describedAs("Y resolution");
        
        maxIter = parser.accepts("max-iter", "The maximum iteration to reach while computing").withRequiredArg()
        .ofType(Long.class).defaultsTo(Long.MAX_VALUE);
        
        minIter = parser.accepts("min-iter", "The minimum iteration to reach while computing").withRequiredArg()
        .ofType(Long.class).defaultsTo(-1L);
        
        imageName = parser.accepts("name", "The name to give to the ouput image").withRequiredArg()
        .ofType(String.class).defaultsTo("final");
    }
    
    @Override
    public boolean execute(Project project)
    {
        List<Double> coordinates = viewport.values(opt);
        
        if (coordinates.size() != 4)
        {
            NebulaCLI.cliPrint("There must be exactly 4 coordinates for the viewport: [Ax, Ay, Bx, By], "
            + "where A and B are two points on opposite corners of the viewport rectangles.", VerboseLevel.ERROR);
        }
        PointInterface a = new Point(coordinates.get(0), coordinates.get(1));
        PointInterface b = new Point(coordinates.get(2), coordinates.get(3));
        RectangleInterface viewPort = new Rectangle(a, b);
        
        try
        {
            project.computeNebula(pointsId.value(opt), viewPort, xRes.value(opt), yRes.value(opt), minIter.value(opt),
            maxIter.value(opt), imageName.value(opt));
        }
        catch (Exception e)
        {
            NebulaCLI.cliPrint("Error while computing", e, VerboseLevel.ERROR);
        }
        
        return false;
    }
    
}
