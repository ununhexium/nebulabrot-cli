package net.lab0.nebula.cli;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public abstract class AbstractCommand
implements BaseCommand
{
    protected OptionParser parser = new OptionParser();
    protected OptionSet    opt;
    private String         name;
    
    public AbstractCommand(String name)
    {
        super();
        this.name = name;
        
        NebulaCLI.configureParser(parser);
    }
    
    public String getName()
    {
        return name;
    }
    
    public void parse(String... args)
    {
        opt = parser.parse(args);
    }
}
