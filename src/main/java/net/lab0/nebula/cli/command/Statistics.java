package net.lab0.nebula.cli.command;

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

/**
 * Statistics computation command line parser
 */
public class Statistics
extends AbstractCommand
{
    private OptionSpec<Integer> tree;
    private OptionSpec<Void>    human;
    private OptionSpec<Void>    csv;
    private OptionSpec<Void>    count;
    private OptionSpec<Void>    area;
    private OptionSpec<Void>    cumulative;
    
    public Statistics()
    {
        super("statistics");
        
        tree = parser.accepts("tree", "Get stats about the tree <id>").withRequiredArg().ofType(Integer.class)
        .describedAs("Id");
        
        human = parser.accepts("human", "Prints human readable figures");
        
        csv = parser.accepts("csv", "Prints CSV");
        
        count = parser.accepts("count", "Computes the nodes count");
        
        area = parser.accepts("area", "Computes an area").requiredIf(cumulative);
        
        cumulative = parser.accepts("sum", "Sums the areas for inside and outside nodes");
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
        List<Integer> keys = new ArrayList<Integer>(res.keySet());
        Collections.sort(keys);
        
        if (opt.has(count))
        {
            printCount(res);
        }
        
        if (opt.has(area))
        {
            printArea(res);
        }
        
    }
    
    private void printArea(Map<Integer, Aggregate> res)
    {
        List<Integer> keys = new ArrayList<Integer>(res.keySet());
        Collections.sort(keys);
        
        NebulaCLI.cliPrint("DEPTH, INSIDE, OUTSIDE, BROWSED, VOID, TOTAL", VerboseLevel.INFO);
        
        double previousIn = 0;
        double previousOut = 0;
        
        for (int i : keys)
        {
            double area = getAreaAtDepth(i);
            
            long in = res.get(i).getCounts().get(Status.INSIDE);
            long out = res.get(i).getCounts().get(Status.OUTSIDE);
            long br = res.get(i).getCounts().get(Status.BROWSED);
            long v = res.get(i).getCounts().get(Status.VOID);
            
            if (opt.has(human))
            {
                NebulaCLI.cliPrint("Depth " + i + " Counts: INSIDE " + (in * area + previousIn) + " OUTSIDE "
                + (out * area + previousOut) + " BROWSED " + (br * area) + " VOID " + (v * area), VerboseLevel.INFO);
            }
            else if (opt.has(csv))
            {
                NebulaCLI.cliPrint("" + i + "," + (in * area + previousIn) + "," + (out * area + previousOut) + ","
                + (br * area) + "," + (v * area), VerboseLevel.INFO);
            }
            else
            {
                NebulaCLI.cliPrint("Depth " + i + " Counts: INSIDE " + (in * area + previousIn) + " OUTSIDE "
                + (out * area + previousOut) + " BROWSED " + (br * area) + " VOID " + (v * area), VerboseLevel.INFO);
            }
            
            if (opt.has(cumulative))
            {
                previousIn += in * area;
                previousOut += out * area;
            }
        }
    }

    private double getAreaAtDepth(int i)
    {
        return Math.pow(4, -(i - 2));
    }
    
    private void printCount(Map<Integer, Aggregate> res)
    {
        List<Integer> keys = new ArrayList<Integer>(res.keySet());
        Collections.sort(keys);
        long grandTotal = 0;
        
        NebulaCLI.cliPrint("DEPTH, INSIDE, OUTSIDE, BROWSED, VOID, TOTAL", VerboseLevel.INFO);
        for (int i : keys)
        {
            long in = res.get(i).getCounts().get(Status.INSIDE);
            long out = res.get(i).getCounts().get(Status.OUTSIDE);
            long br = res.get(i).getCounts().get(Status.BROWSED);
            long v = res.get(i).getCounts().get(Status.VOID);
            long t = res.get(i).getTotal();
            grandTotal += t;
            
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
