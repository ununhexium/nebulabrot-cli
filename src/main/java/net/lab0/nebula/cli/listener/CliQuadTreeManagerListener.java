package net.lab0.nebula.cli.listener;

import net.lab0.nebula.cli.NebulaCLI;
import net.lab0.nebula.cli.VerboseLevel;
import net.lab0.nebula.listener.QuadTreeManagerListener;
import net.lab0.tools.HumanReadable;

public class CliQuadTreeManagerListener
implements QuadTreeManagerListener
{
    
    @Override
    public void computeProgress(int current, int total)
    {
    }
    
    @Override
    public void computationFinished(boolean remaining)
    {
    }
    
    @Override
    public void threadSleeping(long threadId)
    {
    }
    
    @Override
    public void threadResumed(long threadId)
    {
    }
    
    @Override
    public void threadStarted(long threadId, String name)
    {
    }
    
    @Override
    public void loadingFile(int current, int total)
    {
        NebulaCLI.cliPrint("Loading file " + current + "/" + total, VerboseLevel.PROGRESS);
    }
    
    @Override
    public void loadingOfCurrentFileProgress(long current, long total)
    {
        NebulaCLI.cliPrint(
        "Loading " + HumanReadable.humanReadableSizeInBytes(current) + "/" + HumanReadable.humanReadableSizeInBytes(total),
        VerboseLevel.PROGRESS);
    }
    
}
