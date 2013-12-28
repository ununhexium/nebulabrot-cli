package net.lab0.nebula.cli.command;

import java.io.File;

import joptsimple.OptionSpec;
import net.lab0.nebula.cli.AbstractCommand;
import net.lab0.nebula.cli.NebulaCLI;
import net.lab0.nebula.cli.VerboseLevel;
import net.lab0.nebula.cli.listener.CliGeneralListener;
import net.lab0.nebula.cli.listener.CliQuadTreeManagerListener;
import net.lab0.nebula.exception.ProjectException;
import net.lab0.nebula.project.Project;

public class Import
extends AbstractCommand
{
    private OptionSpec<File>    sourceFile;
    private OptionSpec<Integer> maxDepth;
    
    public Import()
    {
        super("import");
        
        maxDepth = parser.accepts("max-depth", "Specifies the maximum depth (inclusive) to use at the import").withRequiredArg()
        .ofType(Integer.class).defaultsTo(Integer.MAX_VALUE);
        parser.accepts("quad-tree", "The imported element is a quad tree").requiredIf("max-depth");
        
        sourceFile = parser.accepts("path", "Path to the file to import").requiredIf("quad-tree").withRequiredArg()
        .describedAs("the absolute or relative file path").ofType(File.class).required();
    }
    
    @Override
    public boolean execute(Project project)
    {
        if (opt.has("quad-tree"))
        {
            try
            {
                CliGeneralListener generalListener = null;
                generalListener = new CliGeneralListener(VerboseLevel.DETAIL);
                
                CliQuadTreeManagerListener quadTreeListener = null;
                quadTreeListener = new CliQuadTreeManagerListener();
                
                project.importQuadTree(sourceFile.value(opt).toPath(), maxDepth.value(opt), quadTreeListener,
                generalListener);
                
                NebulaCLI.cliPrint("Imported " + sourceFile.value(opt), VerboseLevel.INFO);
                
                return true;
            }
            catch (ProjectException e)
            {
                NebulaCLI.cliPrint("Error while importing the quad tree into the project: ", e, VerboseLevel.ERROR);
            }
        }
        else
        {
            NebulaCLI.cliPrintHelp(parser);
        }
        return false;
    }
}
