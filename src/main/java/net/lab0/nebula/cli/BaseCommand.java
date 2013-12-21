package net.lab0.nebula.cli;

import net.lab0.nebula.project.Project;

/**
 * The interface that base command must implement.
 * 
 * @author 116
 * 
 */
public interface BaseCommand
{
    public String getName();
    
    public void parse(String... args);
    
    /**
     * 
     * @param project
     * @return <code>true</code> if the project was modified after the run of the command
     */
    public boolean execute(Project project);
}
