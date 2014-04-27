package net.lab0.nebula.cli.command;

import java.io.IOException;
import java.nio.file.Path;

import joptsimple.OptionSpec;
import net.lab0.nebula.cli.AbstractCommand;
import net.lab0.nebula.cli.NebulaCLI;
import net.lab0.nebula.cli.VerboseLevel;
import net.lab0.nebula.project.Project;

/**
 * 
 * Class to handle the concatenation of XZ files for points data sets
 * 
 * @author 116@lab0.net
 * 
 */
public class XZcat
extends AbstractCommand
{
    private OptionSpec<Integer> pointsId;
    private OptionSpec<Long>    size;
    private OptionSpec<String>  unit;
    
    public XZcat()
    {
        super("xzcat");
        
        pointsId = parser.accepts("points", "The id of the points set that will be concatenated").withRequiredArg()
        .ofType(Integer.class).describedAs("points id");
        
        size = parser.accepts("size", "The maximum size of the concatenated data").requiredIf(pointsId)
        .withRequiredArg().ofType(Long.class);
        
        unit = parser.accepts("unit", "The size's unit").withRequiredArg().ofType(String.class)
        .describedAs("B, kB. MB, GB, TB").defaultsTo("B");
    }
    
    @Override
    public boolean execute(Project project)
    {
        if (opt.has(pointsId))
        {
            long bytes = opt.valueOf(size);
            
            if (opt.has(unit))
            {
                switch (opt.valueOf(unit))
                {
                    case "TB":
                        bytes *= 1024L;
                    case "GB":
                        bytes *= 1024L;
                    case "MB":
                        bytes *= 1024L;
                    case "kB":
                        bytes *= 1024L;
                    case "B":
                        break;
                }
            }
            
            Path out;
            try
            {
                out = project.concatenatePoints(opt.valueOf(pointsId), bytes);
                NebulaCLI.cliPrint("The was concatenated to: " + out.toAbsolutePath(), VerboseLevel.INFO);
            }
            catch (IOException e)
            {
                NebulaCLI.cliPrint("Error while aggregating the points' data", e, VerboseLevel.INFO);
            }
            
        }
        return false;
    }
    
}
