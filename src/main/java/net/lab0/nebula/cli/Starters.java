package net.lab0.nebula.cli;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class Starters
{
    private static final String NEBULA_PROJECT_PATH = "R:\\dev\\nebula\\project";
    
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
        
        // compute
        commands.put("comp", new String[] { "--path", NEBULA_PROJECT_PATH, "nebula", "compute", "--points", "--power",
                "17", "--max-depth", "10", "--max-iter", "65536", "--min-iter", "64", "--size", "4096", "--tree", "1" });
        
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
