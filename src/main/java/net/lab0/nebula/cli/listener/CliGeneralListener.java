package net.lab0.nebula.cli.listener;

import net.lab0.nebula.cli.NebulaCLI;
import net.lab0.nebula.cli.VerboseLevel;
import net.lab0.nebula.listener.GeneralListener;

public class CliGeneralListener
implements GeneralListener
{
    private VerboseLevel severity;
    
    public CliGeneralListener(VerboseLevel severity)
    {
        super();
        this.severity = severity;
    }
    
    @Override
    public void print(String s)
    {
        NebulaCLI.cliPrint(s, severity);
    }
    
}
