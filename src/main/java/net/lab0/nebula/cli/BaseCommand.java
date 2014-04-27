package net.lab0.nebula.cli;

import net.lab0.nebula.project.Project;

/**
 * The interface that the commands must implement.
 * 
 * @see AbstractCommand for a partial implementation
 * 
 * @author 116
 * 
 */
public interface BaseCommand
{
    /**
     * 
     * @return the name used for calling this command on the prompt
     */
    public String getName();
    
    /**
     * Parses the command line input's
     * 
     * @param args
     */
    public void parse(String... args);
    
    /**
     * 
     * @param project
     *            The project on which the command was executed.
     * @return <code>true</code> if the project was modified after the run of the command
     */
    public boolean execute(Project project);
    
    /**
     * 
     * @return <code>true</code> if the user asked help for this command. Typically if the user typed --help or -h as a
     *         command arguments.
     */
    public boolean askedForHelp();
}
