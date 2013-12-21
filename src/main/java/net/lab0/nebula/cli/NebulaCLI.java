package net.lab0.nebula.cli;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import net.lab0.nebula.cli.command.Import;
import net.lab0.nebula.cli.command.Init;
import net.lab0.nebula.exception.NonEmptyFolderException;
import net.lab0.nebula.exception.ProjectException;
import net.lab0.nebula.project.Project;
import net.lab0.tools.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * The CLI for the nebulabrot project
 */
public class NebulaCLI
{
    private static final OptionParser        parser      = new OptionParser();
    private static OptionSet                 opt;
    
    // general parameters
    private static final OptionSpec<String>  projectPath = parser.accepts("path", "The path to the project")
                                                         .withRequiredArg().defaultsTo(".");
    
    private static final OptionSpec<Integer> verbosity   = parser
                                                         .acceptsAll(
                                                         Arrays.asList("verbose", "v"),
                                                         "The verbosity level,"
                                                         + " starting at 0 (minimal output: high level errors),"
                                                         + " 1 (high level status),"
                                                         + " 2 (low level status, high level progress),"
                                                         + " 3(low level progress). -1 for no output.")
                                                         .withRequiredArg().ofType(Integer.class).defaultsTo(0);
    
    private static Map<String, BaseCommand>  commands    = new HashMap<>();
    private static Logger                    log         = LoggerFactory.getLogger(NebulaCLI.class);
    
    public static void main(String[] args)
    throws IOException
    {
        try
        {
            log.debug("Start");
            
            log.trace("Base commands parsing");
            Pair<String[], String[]> splitArgs = parseBaseCommands(args);
            
            log.trace("Configuring global parser");
            configureParser(parser);
            
            log.trace("Parsing");
            opt = parser.parse(splitArgs.a);
            
            log.trace("Open project");
            Project project = openProject();
            
            String command = splitArgs.b[0];
            String[] commandArgs = Arrays.copyOfRange(splitArgs.b, 1, splitArgs.b.length);
            commands.get(command).parse(commandArgs);
            boolean modified = commands.get(command).execute(project);
            
            if (modified)
            {
                log.trace("Save project");
                saveProject(project);
            }
        }
        catch (Exception e)
        {
            net.lab0.tools.Throwables.printMessages(e);
            log.error("Caught error in main", e);
        }
    }
    
    private static Pair<String[], String[]> parseBaseCommands(String... args)
    {
        registerCommands();
        
        Set<String> availableCommands = commands.keySet();
        Set<String> userCommands = Sets.newHashSet(args);
        
        Set<String> validCommands = Sets.intersection(availableCommands, userCommands);
        
        if (validCommands.size() == 0)
        {
            String[] commandsArray = availableCommands.toArray(new String[availableCommands.size()]);
            cliPrint("You didn't specify any command. Available commands are: " + Arrays.toString(commandsArray));
            cliPrint("Global options are");
            cliPrintHelp(parser);
            System.exit(1);
            return null;
        }
        else
        {
            // find the first valid command and extract its arguments
            int index = 0;
            for (String arg : args)
            {
                if (availableCommands.contains(arg))
                {
                    break;
                }
                index++;
            }
            
            // the global args
            String[] global = new String[0];
            if (index > 0)
            {
                global = Arrays.copyOfRange(args, 0, index);
                log.trace("Global commands " + Arrays.toString(global));
            }
            
            // the commands args
            String[] command = Arrays.copyOfRange(args, index, args.length);
            log.trace("Specific commands " + Arrays.toString(command));
            
            return new Pair<String[], String[]>(global, command);
        }
    }
    
    /**
     * registers the commands that the user can call
     */
    private static void registerCommands()
    {
        addCommand(new Init());
        addCommand(new Import());
    }
    
    private static void addCommand(BaseCommand command)
    {
        commands.put(command.getName(), command);
    }
    
    private static void saveProject(Project project)
    {
        try
        {
            project.saveProjectsParameters();
        }
        catch (JAXBException e)
        {
            log.error("Error while saving the project", e);
            System.exit(1);
        }
    }
    
    /**
     * Tries to open the project. Prints an error and exits if there was a problem.
     */
    private static Project openProject()
    {
        try
        {
            Path path = FileSystems.getDefault().getPath(projectPath.value(opt));
            return new Project(path);
        }
        catch (ProjectException | NonEmptyFolderException | JAXBException e)
        {
            log.error("Error while opening a project", e);
        }
        System.exit(1);
        return null;
    }
    
    public static void configureParser(OptionParser parser)
    {
        parser.posixlyCorrect(true);
        parser.recognizeAlternativeLongOptions(false);
        
        parser.acceptsAll(Arrays.asList("h", "help"), "Help").forHelp();
    }
    
    public static void cliPrint(String s)
    {
        log.info(s);
        if (verbosity.value(opt) > -1)
        {
            // System.out.println(s);
        }
        
    }
    
    public static int getVerbosityLevel()
    {
        return opt.valueOf(NebulaCLI.verbosity);
    }
    
    public static String getProjectPath()
    {
        return opt.valueOf(projectPath);
    }
    
    public static void cliPrintHelp(OptionParser parser)
    {
        try
        {
            if (verbosity.value(opt) > -1)
            {
                parser.printHelpOn(System.out);
            }
        }
        catch (IOException e)
        {
            log.warn("Was not able to print a message on std out", e);
        }
    }
}
