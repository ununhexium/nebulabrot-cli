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
import net.lab0.nebula.cli.command.ComputeNebula;
import net.lab0.nebula.cli.command.ComputePoints;
import net.lab0.nebula.cli.command.Cut;
import net.lab0.nebula.cli.command.Import;
import net.lab0.nebula.cli.command.Info;
import net.lab0.nebula.cli.command.Init;
import net.lab0.nebula.cli.command.Split;
import net.lab0.nebula.exception.NonEmptyFolderException;
import net.lab0.nebula.exception.ProjectException;
import net.lab0.nebula.project.Project;
import net.lab0.tools.Pair;
import net.lab0.tools.Throwables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

/**
 * The CLI for the nebulabrot project
 */
public class NebulaCLI
{
    private static final OptionParser             parser           = new OptionParser();
    private static OptionSet                      opt;
    
    private static VerboseLevel                   defaultVerbosity = VerboseLevel.OFF;
    
    // general parameters
    private static final OptionSpec<String>       projectPath      = parser.accepts("path", "The path to the project")
                                                                   .withRequiredArg().defaultsTo(".");
    
    private static final OptionSpec<VerboseLevel> verbosity        = parser
                                                                   .acceptsAll(Arrays.asList("verbose", "v"),
                                                                   "The verbosity level: " + VerboseLevel.toHelp())
                                                                   .withRequiredArg().ofType(VerboseLevel.class)
                                                                   .defaultsTo(defaultVerbosity);
    
    private static Map<String, BaseCommand>       commands         = new HashMap<>();
    private static Logger                         log              = LoggerFactory.getLogger(NebulaCLI.class);
    
    public static void main(String[] args)
    throws IOException
    {
        try
        {
            log.debug("Start v0.0.0.1");
            
            log.debug("Base commands parsing");
            Pair<String[], String[]> splitArgs = parseBaseCommands(args);
            
            log.debug("Configuring global parser");
            configureParser(parser);
            
            log.debug("Parsing");
            opt = parser.parse(splitArgs.a);
            if (opt.has("help"))
            {
                cliPrintHelp(parser);
            }
            else
            {
                parseAndExecuteCommand(splitArgs);
            }
        }
        catch (joptsimple.OptionException e)
        {
            NebulaCLI.cliPrint("Error while parsing the command line", e, VerboseLevel.ERROR);
        }
        catch (Exception e)
        {
            for (String s : Throwables.getMessages(e))
            {
                cliPrint(s, VerboseLevel.ERROR);
            }
            log.error("Caught error in main", e);
        }
        System.exit(0);//TODO Need to fix the thread pool executor
    }

    private static void parseAndExecuteCommand(Pair<String[], String[]> splitArgs)
    {
        String command = splitArgs.b[0];
        String[] commandArgs = Arrays.copyOfRange(splitArgs.b, 1, splitArgs.b.length);
        commands.get(command).parse(commandArgs);
        if (!commands.get(command).askedForHelp())
        {
            log.debug("Open project");
            Project project = openProject();
            boolean modified = commands.get(command).execute(project);
            if (modified)
            {
                log.debug("Save project");
                saveProject(project);
            }
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
            cliPrint("You didn't specify any command. Available commands are: " + Arrays.toString(commandsArray),
            VerboseLevel.ERROR);
            cliPrint("Global options are", VerboseLevel.INFO);
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
        addCommand(new ComputePoints());
        addCommand(new Cut());
        addCommand(new Info());
        addCommand(new Init());
        addCommand(new Import());
        addCommand(new Split());
        addCommand(new ComputeNebula());
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
    
    public static void cliPrint(String s, VerboseLevel severity)
    {
        if (severity.eq(VerboseLevel.OFF))
        {
            log.warn("INTERNAL A message cannot be logged with a severity of OFF -> changed to TRACE");
            severity = VerboseLevel.TRACE;
        }
        
        logOutput(s, severity);
        
        printOutput(s, severity);
    }
    
    private static void printOutput(String s, VerboseLevel severity)
    {
        if (shouldPrint(severity))
        {
            if (severity.eq(VerboseLevel.WARN))
            {
                System.out.print("WW ");
            }
            else if (severity.eq(VerboseLevel.ERROR))
            {
                System.out.print("EE ");
            }
            else if (severity.eq(VerboseLevel.FATAL))
            {
                System.out.print("FF ");
            }
            else {
                System.out.print("   ");
            }
            System.out.println(s);
        }
    }
    
    public static void cliPrint(String s, Throwable t, VerboseLevel level)
    {
        cliPrint(s, level);
        for (String msg : Throwables.getMessages(t))
        {
            cliPrint(msg, level);
        }
    }
    
    public static boolean shouldPrint(VerboseLevel severity)
    {
        VerboseLevel verbosity = null;
        // this happens when we want to print something before the main args are parsed
        if (opt == null)
        {
            verbosity = VerboseLevel.ALL;
        }
        else
        {
            verbosity = NebulaCLI.verbosity.value(opt);
        }
        return (severity).gte(verbosity);
    }
    
    private static void logOutput(String s, VerboseLevel severity)
    {
        switch (severity)
        {
            case ALL:
            case TRACE:
                log.trace(s);
                break;
            
            case DEBUG:
            case PROGRESS:
                log.debug(s);
                break;
            
            case DETAIL:
            case INFO:
                log.info(s);
                break;
            
            case WARN:
                log.warn(s);
                break;
            
            case ERROR:
            case FATAL:
                log.error(s);
                break;
            
            case OFF:
            default:
                break;
        }
    }
    
    public static VerboseLevel getVerbosityLevel()
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
            parser.printHelpOn(System.out);
        }
        catch (IOException e)
        {
            log.warn("Was not able to print a message on std out", e);
        }
    }
}
