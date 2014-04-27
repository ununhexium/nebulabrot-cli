package net.lab0.nebula.cli;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * Class to start the CLI in debug mode
 * 
 * @author 116@lab0.net
 * 
 */
public class Starters
{
    private static final String NEBULA_PROJECT_PATH = "R:\\dev\\nebula\\cli\\test1";
    
    public static void main(String[] args)
    throws Exception
    {
        LinkedHashMap<String, String[]> commands = new LinkedHashMap<>();
        
        // init
        commands.put("init", new String[] { "--path", NEBULA_PROJECT_PATH, "init", "--name", "Testing" });
        
        // import
        commands.put("import", new String[] { "--path", NEBULA_PROJECT_PATH, "--verbose", "PROGRESS", "import",
                "--path", "R:\\dev\\nebula\\tree\\bin\\out", "--quad-tree", "--max-depth", "6" });
        
        // cut
        commands.put("cut", new String[] { "--path", NEBULA_PROJECT_PATH, "--verbose", "PROGRESS", "cut", "--in",
                "R:\\dev\\nebula\\tree\\bin\\p256i65536d5D16binNoIndex", "--out", "R:\\dev\\nebula\\tree\\bin\\out",
                "--depth", "7" });
        
        // compute points
        commands.put("comp", new String[] { "--path", NEBULA_PROJECT_PATH, "nebula", "compute", "--points", "--power",
                "12", "--max-depth", "8", "--max-iter", "65536", "--min-iter", "64", "--block-size", "4096", "--tree",
                "1" });
        
        // compute nebula
        commands.put("nebula", new String[] { "--path", NEBULA_PROJECT_PATH, "nebula", "--points", "9", "--viewport",
                "2.0,2.0,-2.0,-2.0", "--x-res", "1024", "--y-res", "1024" });
        
        // xzcat
        commands.put("xzcat", new String[] { "--path", NEBULA_PROJECT_PATH, "xzcat", "--points", "1", "--size", "200",
                "--unit", "MB" });
        
        String choice = "";
        
        try (
            Scanner scanner = new Scanner(System.in))
        {
            while (!"exit".equals(choice))
            {
                System.out.println("exit");
                for (Entry<String, String[]> entry : commands.entrySet())
                {
                    System.out.println("" + entry.getKey() + " - " + Arrays.toString(entry.getValue()));
                }
                
                choice = scanner.nextLine();
                String[] command = commands.get(choice);
                if (command != null)
                {
                    NebulaCLI.main(command);
                }
            }
        }
        
        System.out.println("end");
    }
}
