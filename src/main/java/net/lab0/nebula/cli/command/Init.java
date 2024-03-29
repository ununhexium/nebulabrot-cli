package net.lab0.nebula.cli.command;

import joptsimple.OptionSpec;
import net.lab0.nebula.cli.AbstractCommand;
import net.lab0.nebula.cli.NebulaCLI;
import net.lab0.nebula.cli.VerboseLevel;
import net.lab0.nebula.project.Project;

/**
 * Initializes a project.
 * 
 * @author 116
 * 
 */
public class Init
extends AbstractCommand
{
    private OptionSpec<String> projectName;
    
    public Init()
    {
        super("init");
        
        projectName = parser.accepts("name", "The name to give to a project").withRequiredArg().ofType(String.class)
        .defaultsTo("");
    }
    
    public boolean execute(Project project)
    {
        String name = projectName.value(opt);
        project.getProjetInformation().name = name;
        if (project.isNewProject())
        {
            NebulaCLI.cliPrint("Project '" + name + "' created", VerboseLevel.INFO);
            return true;
        }
        else
        {
            NebulaCLI.cliPrint("A project named '" + name + "' already exists.", VerboseLevel.WARN);
        }
        return false;
    }
    
}
