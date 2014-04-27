package net.lab0.nebula.cli.command;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import joptsimple.OptionSpec;
import net.lab0.nebula.cli.AbstractCommand;
import net.lab0.nebula.cli.Clazz;
import net.lab0.nebula.cli.NebulaCLI;
import net.lab0.nebula.cli.VerboseLevel;
import net.lab0.nebula.exception.InvalidBinaryFileException;
import net.lab0.nebula.project.Project;
import nu.xom.ParsingException;

/**
 * Command to render a data set into an image
 * 
 * @author 116@lab0.net
 * 
 */
public class Image
extends AbstractCommand
{
    private OptionSpec<Clazz>   clazz;
    private OptionSpec<Integer> nebulaId;
    private OptionSpec<String>  nebulaSubId;
    private OptionSpec<String>  imageName;
    private OptionSpec<Void>    gmaps;
    private OptionSpec<Integer> tilesSize;
    
    public Image()
    {
        super("image");
        
        clazz = parser.accepts("class", "The class of object you want to render").withRequiredArg().ofType(Clazz.class)
        .describedAs("NEBULA").required();
        
        nebulaId = parser.accepts("set-id", "The id of the class set you want to use").withRequiredArg()
        .ofType(Integer.class).describedAs("id");
        
        nebulaSubId = parser.accepts("sub-id", "The id of the object you want to render").withRequiredArg()
        .ofType(String.class).describedAs("id");
        
        imageName = parser.accepts("name", "The name of the output file").withRequiredArg().ofType(String.class);
        
        gmaps = parser.accepts("gmaps", "Outputs the result as a set of files to use in gmaps");
        
        tilesSize = parser.accepts("tile-size", "The size of the gmaps tiles").requiredIf(gmaps).withRequiredArg()
        .ofType(Integer.class).describedAs("pixels");
    }
    
    @Override
    public boolean execute(Project project)
    {
        switch (opt.valueOf(clazz))
        {
            case NEBULA:
                if (opt.has(nebulaId) && opt.has(nebulaSubId))
                {
                    if (opt.has(gmaps))
                    {
                        imageGmaps(project);
                    }
                    else
                    {
                        imageNebula(project);
                    }
                }
                break;
        }
        
        return false;
    }
    
    private void imageGmaps(Project project)
    {
        try
        {
            project.renderNebulaGmaps(opt.valueOf(nebulaId), opt.valueOf(nebulaSubId), opt.valueOf(imageName),
            opt.valueOf(tilesSize));
        }
        catch (NoSuchAlgorithmException | ParsingException | IOException | InvalidBinaryFileException e)
        {
            NebulaCLI.cliPrint("Error while rendering the google map", e, VerboseLevel.ERROR);
        }
    }
    
    private void imageNebula(Project project)
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
