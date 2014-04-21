package net.lab0.nebula.cli.command;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import joptsimple.OptionSpec;
import net.lab0.nebula.cli.AbstractCommand;
import net.lab0.nebula.cli.NebulaCLI;
import net.lab0.nebula.cli.VerboseLevel;
import net.lab0.nebula.cli.listener.CliQuadTreeManagerListener;
import net.lab0.nebula.core.QuadTreeManager;
import net.lab0.nebula.enums.Indexing;
import net.lab0.nebula.exception.InvalidBinaryFileException;
import net.lab0.nebula.listener.QuadTreeManagerListener;
import net.lab0.nebula.project.Project;
import nu.xom.ParsingException;

@SuppressWarnings("deprecation")
public class Cut
extends AbstractCommand
{
    private OptionSpec<File>    input;
    private OptionSpec<File>    output;
    private OptionSpec<Integer> cutDepth;
    
    public Cut()
    {
        super("cut");
        
        input = parser.acceptsAll(Arrays.asList("in", "input"), "Quad tree input file").withRequiredArg().ofType(File.class)
        .required().describedAs("A quad tree");
        output = parser.acceptsAll(Arrays.asList("out", "output"), "Quad tree output file").withRequiredArg().ofType(File.class)
        .required().describedAs("A quad tree");
        cutDepth = parser.accepts("depth", "The cut depth. The indicated depth is kept in the quad tree.")
        .withRequiredArg().ofType(Integer.class).required();
        parser.accepts("indexed", "Saves the file with indexation");
    }
    
    @Override
    public boolean execute(Project project)
    {
        QuadTreeManagerListener listener = null;
        listener = new CliQuadTreeManagerListener();
        
        // step 1: read
        QuadTreeManager manager = null;
        try
        {
            manager = new QuadTreeManager(input.value(opt).toPath(), listener, cutDepth.value(opt));
        }
        catch (ClassNotFoundException | NoSuchAlgorithmException | ParsingException | IOException
        | InvalidBinaryFileException e)
        {
            NebulaCLI.cliPrint("Was not able to load the source quad tree at " + input.value(opt).getAbsolutePath(), e,
            VerboseLevel.ERROR);
        }
        
        // step 2: check the cut depth, should be useless
        manager.getQuadTreeRoot().strip(cutDepth.value(opt));
        
        // step 3: save
        Indexing indexing = opt.has("indexed") ? Indexing.USE_INDEXING : Indexing.NO_INDEXING;
        try
        {
            if (NebulaCLI.shouldPrint(VerboseLevel.DETAIL))
            {
                NebulaCLI.cliPrint("Saving " + manager.getQuadTreeRoot().getTotalNodesCount(), VerboseLevel.DETAIL);
            }
            manager.saveToBinaryFile(output.value(opt).toPath(), indexing);
        }
        catch (IOException e)
        {
            NebulaCLI.cliPrint("Was not able to save the quad tree to " + output.value(opt).getAbsolutePath(), e,
            VerboseLevel.ERROR);
        }
        return false;
    }
    
}
