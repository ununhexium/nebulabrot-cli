package net.lab0.nebula.cli;

import java.io.IOException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 * The base of all commands classes.
 * 
 * @author 116@lab0.net
 * 
 */
public abstract class AbstractCommand
implements BaseCommand
{
    /**
     * The parser to use when adding option specs
     */
    protected OptionParser parser = new OptionParser();
    /**
     * The result set of the parsed options is stored in there
     */
    protected OptionSet    opt;
    private String         name;
    
    /**
     * 
     * @param name
     *            The name to call that command on the prompt
     */
    public AbstractCommand(String name)
    {
        super();
        this.name = name;
        
        // sue the same configuration for all parser extending this class
        NebulaCLI.configureParser(parser);
    }
    
    public String getName()
    {
        return name;
    }
    
    public void parse(String... args)
    {
        opt = parser.parse(args);
        if (askedForHelp())
        {
            try
            {
                parser.printHelpOn(System.out);
            }
            catch (IOException e)
            {
                NebulaCLI.cliPrint("Was not able to display the help message for the command " + this.getName(), e,
                VerboseLevel.WARN);
            }
        }
    }
    
    /**
     * @return <code>true</code> if the command line arguments contain the --help flag. Doesn't test for -h flag as it
     *         could be a subcommand's shortcut.
     */
    public boolean askedForHelp()
    {
        return opt.has("help");
    }
}
