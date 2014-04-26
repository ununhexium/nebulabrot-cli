package net.lab0.nebula.cli.command;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import joptsimple.OptionSpec;
import net.lab0.nebula.cli.AbstractCommand;
import net.lab0.nebula.cli.NebulaCLI;
import net.lab0.nebula.cli.VerboseLevel;
import net.lab0.nebula.exception.InvalidBinaryFileException;
import net.lab0.nebula.project.Project;
import net.lab0.tools.MyString;
import nu.xom.ParsingException;

public class Image
extends AbstractCommand
{
    private enum Clazz
    {
        NEBULA,
    }
    
    private OptionSpec<Clazz>   clazz;
    private OptionSpec<Integer> nebulaId;
    private OptionSpec<String>  nebulaSubId;
    private OptionSpec<String>  imageName;
    
    public Image()
    {
        super("image");
        
        clazz = parser.accepts("class", "The class of object you want to render").withRequiredArg().ofType(Clazz.class)
        .describedAs("NEBULA");
        
        nebulaId = parser.accepts("set-id", "The id of the class set you want to use").withRequiredArg()
        .ofType(Integer.class).describedAs("id");
        
        nebulaSubId = parser.accepts("sub-id", "The id of the object you want to render").withRequiredArg()
        .ofType(String.class).describedAs("id");
        
        imageName = parser.accepts("name", "The name of the output file").withRequiredArg().ofType(String.class);
    }
    
    @Override
    public boolean execute(Project project)
    {
        switch (opt.valueOf(clazz))
        {
            case NEBULA:
                imageNebula(project);
                break;
        }
        
        return false;
    }
    
    private void imageNebula(Project project)
    {
        
        if (opt.has(nebulaId) && opt.has(nebulaSubId))
        {
            try
            {
                project.renderNebula(opt.valueOf(nebulaId), opt.valueOf(nebulaSubId), opt.valueOf(imageName));
            }
            catch (NoSuchAlgorithmException | IOException | ParsingException | InvalidBinaryFileException e)
            {
                NebulaCLI.cliPrint("Error while rendering the image", e, VerboseLevel.ERROR);
            }
        }
    }
    
}
