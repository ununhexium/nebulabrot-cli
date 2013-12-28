package net.lab0.nebula.cli.command;

import java.io.FileNotFoundException;

import net.lab0.nebula.cli.AbstractCommand;
import net.lab0.nebula.cli.NebulaCLI;
import net.lab0.nebula.cli.VerboseLevel;
import net.lab0.nebula.exception.ProjectException;
import net.lab0.nebula.project.Project;
import joptsimple.OptionSpec;

/**
 * Splits the nodes of a quad tree into several chunks of data for future processing.
 * 
 * @author 116
 * 
 */
public class Split
extends AbstractCommand
{
    private OptionSpec<Integer> blockSize;
    private OptionSpec<Integer> treeId;
    
    public Split()
    {
        super("split");
        
        blockSize = parser.accepts("size", "The number of elements to put in each block").withRequiredArg()
        .ofType(Integer.class);
        treeId = parser.accepts("tree", "The id of the tree that will be split").withRequiredArg()
        .ofType(Integer.class).describedAs("tree id").required();
    }
    
    @Override
    public boolean execute(Project project)
    {
        try
        {
            project.splitNodes(treeId.value(opt), blockSize.value(opt));
            return true;
        }
        catch (FileNotFoundException | ProjectException e)
        {
            NebulaCLI.cliPrint("Error while splitting nodes", e, VerboseLevel.ERROR);
        }
        return false;
    }
}
