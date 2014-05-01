package net.lab0.nebula.cli.command;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import joptsimple.OptionSpec;
import net.lab0.nebula.cli.AbstractCommand;
import net.lab0.nebula.cli.NebulaCLI;
import net.lab0.nebula.cli.VerboseLevel;
import net.lab0.nebula.enums.Status;
import net.lab0.nebula.exe.MandelbrotQTNStats.Aggregate;
import net.lab0.nebula.project.Project;
import net.lab0.tools.HumanReadable;
import net.lab0.tools.MyString;

/**
 * Statistics computation command line parser
 */
public class Statistics
extends AbstractCommand
{
    private OptionSpec<Integer> tree;
    private OptionSpec<Void>    human;
    private OptionSpec<Void>    csv;
    
    public Statistics()
    {
        super("statistics");
        
        tree = parser.accepts("tree", "Get stats about the tree <id>").withRequiredArg().ofType(Integer.class)
        .describedAs("Id");
        
        human = parser.accepts("human", "Prints human readable figures");
        csv = parser.accepts("csv", "Prints CSV");
    }
    
    @Override
    public boolean execute(Project project)
    {
        if (opt.has(tree))
        {
            treeStats(project);
        }
        
        return false;
    }
    
    /**
     * Compute and displays the statistics related to a quad tree
     */
    private void treeStats(Project project)
    {
        Map<Integer, Aggregate> res = project.getStatisticsModule().computeTreeStatistics(opt.valueOf(tree));
        System.out.println(res.size());
        long grandTotal = 0;
        List<Integer> keys = new ArrayList<Integer>(res.keySet());
        Collections.sort(keys);
        
        if (opt.has(csv))
        {
            NebulaCLI.cliPrint("DEPTH, INSIDE, OUTSIDE, BROWSED, VOID, TOTAL", VerboseLevel.INFO);
        }
        for (int i : keys)
        {
            
            long in = res.get(i).getCounts().get(Status.INSIDE);
            long out = res.get(i).getCounts().get(Status.OUTSIDE);
            long br = res.get(i).getCounts().get(Status.BROWSED);
            long v = res.get(i).getCounts().get(Status.VOID);
            long t = res.get(i).getTotal();
            
            if (opt.has(human))
            {
                NebulaCLI.cliPrint(
                "Depth " + i + " Counts: INSIDE " + HumanReadable.humanReadableNumber(in, true, "") + " OUTSIDE "
                + HumanReadable.humanReadableNumber(out, true, "") + " BROWSED "
                + HumanReadable.humanReadableNumber(br, true, "") + " VOID "
                + HumanReadable.humanReadableNumber(v, true, "") + " TOTAL "
                + HumanReadable.humanReadableNumber(t, true, ""), VerboseLevel.INFO);
            }
            else if (opt.has(csv))
            {
                NebulaCLI.cliPrint("" + i + "," + in + "," + out + "," + br + "," + v + "," + t, VerboseLevel.INFO);
            }
            else
            {
                NebulaCLI.cliPrint("Depth " + i + " Counts: INSIDE " + in + " OUTSIDE " + out + " BROWSED " + br
                + " VOID " + v + " TOTAL " + t, VerboseLevel.INFO);
            }
            
            grandTotal += t;
        }
        
        if (opt.has(human))
        {
            NebulaCLI.cliPrint("Total number of nodes: " + HumanReadable.humanReadableNumber(grandTotal),
            VerboseLevel.INFO);
        }
        else
        {
            NebulaCLI.cliPrint("Total number of nodes: " + grandTotal, VerboseLevel.INFO);
        }
    }
}
