package net.lab0.nebula.cli.command;

import java.io.File;

import joptsimple.OptionSpec;
import net.lab0.nebula.cli.AbstractCommand;
import net.lab0.nebula.cli.NebulaCLI;
import net.lab0.nebula.exception.ProjectException;
import net.lab0.nebula.listener.ConsoleGeneralListener;
import net.lab0.nebula.listener.ConsoleQuadTreeManagerListener;
import net.lab0.nebula.project.Project;

public class Import
extends AbstractCommand
{
    private OptionSpec<File>    sourceFile;
    private OptionSpec<Integer> maxDepth;
    
    public Import()
    {
        super("import");
        
        maxDepth = parser.accepts("max-depth", "Specifies the maximum depth to use at the import").withRequiredArg()
        .ofType(Integer.class).defaultsTo(Integer.MAX_VALUE);
        parser.accepts("quad-tree", "The imported element is a quad tree").requiredIf("max-depth");
        
        sourceFile = parser.accepts("path", "Path to the file to import").requiredIf("quad-tree").withRequiredArg()
        .describedAs("the absolute or relative file path").ofType(File.class);
    }
    
    @Override
    public boolean execute(Project project)
    {
        if (opt.has("quad-tree"))
        {
            try
            {
                ConsoleGeneralListener generalListener = null;
                if (NebulaCLI.getVerbosityLevel() > 2)
                {
                    generalListener = new ConsoleGeneralListener();
                }
                ConsoleQuadTreeManagerListener quadTreeListener = null;
                if (NebulaCLI.getVerbosityLevel() > 3)
                {
                    quadTreeListener = new ConsoleQuadTreeManagerListener();
                }
                
                project.importQuadTree(sourceFile.value(opt).toPath(), maxDepth.value(opt), quadTreeListener,
                generalListener);
                
                if (NebulaCLI.getVerbosityLevel() > 0)
                {
                    NebulaCLI.cliPrint("Imported " + sourceFile.value(opt));
                }
                
                return true;
            }
            catch (ProjectException e)
            {
                NebulaCLI.cliPrint("Error while importing the quad tree into the project: ");
                NebulaCLI.cliPrint(e.getMessage());
                Throwable t = null;
                while ((t = t.getCause()) != null)
                {
                    NebulaCLI.cliPrint(e.getMessage());
                }
            }
        }
        else
        {
            NebulaCLI.cliPrintHelp(parser);
        }
        return false;
    }
}
