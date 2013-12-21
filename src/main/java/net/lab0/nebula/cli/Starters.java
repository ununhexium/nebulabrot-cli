package net.lab0.nebula.cli;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class Starters
{
    public static void main(String[] args) throws Exception
    {
        LinkedHashMap<String, String[]> commands = new LinkedHashMap<>();
        
        // init
        commands.put("init", new String[] { "init", "--name", "Testing" });
        
        // import
        commands.put("import", new String[] { "--verbose", "9", "import", "--path",
                "R:\\dev\\nebula\\tree\\bin\\p256i65536d5D16binNoIndex", "--quad-tree", "--max-depth", "5" });
        
        String choice = "";
        
        while (!"stop".equals(choice))
        {
            System.out.println("exit");
            for (Entry<String, String[]> entry : commands.entrySet())
            {
                System.out.println("" + entry.getKey() + " - " + Arrays.toString(entry.getValue()));
            }
            try (
                Scanner scanner = new Scanner(System.in);)
            {
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
