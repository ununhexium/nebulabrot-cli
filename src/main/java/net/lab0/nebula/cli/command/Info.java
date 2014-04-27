package net.lab0.nebula.cli.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import joptsimple.OptionSpec;
import net.lab0.nebula.cli.AbstractCommand;
import net.lab0.nebula.cli.NebulaCLI;
import net.lab0.nebula.cli.VerboseLevel;
import net.lab0.nebula.project.Project;
import net.lab0.nebula.project.QuadTreeInformation;

/**
 * Display quick information about this project.
 * 
 * @author 116@lab0.net
 * 
 */
public class Info
extends AbstractCommand
{
    private OptionSpec<Integer> tree;
    
    public Info()
    {
        super("info");
        
        parser.accepts("trees");
        tree = parser.accepts("tree", "Displays information about a tree").withRequiredArg().ofType(Integer.class)
        .describedAs("ID");
    }
    
    @Override
    public boolean execute(Project project)
    {
        if (opt.has(tree))
        {
            displayTreeInfo(project, tree.value(opt));
        }
        if (opt.has("trees"))
        {
            displayTreesList(project);
        }
        return false;
    }
    
    private void displayTreesList(Project project)
    {
        List<QuadTreeInformation> qts = project.getProjetInformation().quadTreesInformation.quadTrees;
        List<Integer> ids = new ArrayList<>(qts.size());
        for (QuadTreeInformation info : qts)
        {
            ids.add(info.id);
        }
        NebulaCLI.cliPrint(Arrays.toString(ids.toArray()), VerboseLevel.INFO);
    }
    
    private void displayTreeInfo(Project project, int id)
    {
        VerboseLevel severity = VerboseLevel.INFO;
        for (QuadTreeInformation info : project.getProjetInformation().quadTreesInformation.quadTrees)
        {
            if (info.id == id)
            {
                NebulaCLI.cliPrint("id: " + info.id, severity);
                NebulaCLI.cliPrint("maxDepth: " + info.maxDepth, severity);
                NebulaCLI.cliPrint("max iteration count: " + info.maximumIterationCount, severity);
                NebulaCLI.cliPrint("max iteration difference: " + info.maximumIterationDifference, severity);
                NebulaCLI.cliPrint("nodes count: " + info.nodesCount, severity);
                NebulaCLI.cliPrint("points per side: " + info.pointsCountPerSide, severity);
            }
        }
    }
    
}
