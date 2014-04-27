package net.lab0.nebula.cli.command;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.tukaani.xz.FilterOptions;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;

import com.google.common.io.ByteStreams;

import joptsimple.OptionSpec;
import net.lab0.nebula.cli.AbstractCommand;
import net.lab0.nebula.cli.NebulaCLI;
import net.lab0.nebula.cli.VerboseLevel;
import net.lab0.nebula.project.Project;

/**
 * Compresses a file with LZMA2
 * 
 * @author 116@lab0.net
 * 
 */
public class XZ
extends AbstractCommand
{
    OptionSpec<String>  file;
    OptionSpec<Integer> level;
    
    public XZ()
    {
        super("xz");
        
        file = parser.accepts("file", "Compress a file in XZ format").withRequiredArg().ofType(String.class);
        
        level = parser.accepts("level", "Compression level").withRequiredArg().ofType(Integer.class)
        .describedAs("[0-10]");
    }
    
    @Override
    public boolean execute(Project project)
    {
        Path inputPath = FileSystems.getDefault().getPath(opt.valueOf(file));
        Path outputPath = inputPath.getParent().resolve(inputPath.getFileName() + ".xz");
        
        try (
            FileInputStream fis = new FileInputStream(inputPath.toFile());
            FileOutputStream fos = new FileOutputStream(outputPath.toFile());
            XZOutputStream outputStream = new XZOutputStream(fos, new LZMA2Options(opt.valueOf(level))))
        {
            ByteStreams.copy(fis, outputStream);
        }
        catch (Exception e)
        {
            NebulaCLI.cliPrint("Error while zipping", e, VerboseLevel.ERROR);
        }
        
        return false;
    }
    
}
