import lombok.Getter;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;

@Getter
public class RunOptions {
    @Option(name = "-xsd", usage = "path to xsd for validation", required = true)
    private File xsdPath;

    @Option(name = "-xmls-dir", usage = "path to directory with xmls", required = true)
    private File xmlsDir;

    @Option(name = "-log", usage = "path to output log file")
    private File logFile = new File("validation.log");

    RunOptions parseArgs(String... args) throws CmdLineException {
        CmdLineParser cmdLineParser = new CmdLineParser(this);
        cmdLineParser.parseArgument(args);

        if (!xsdPath.exists() && !xsdPath.isFile() && !xsdPath.getName().toLowerCase().endsWith(".xsd")) {
            throw new CmdLineException(String.format("-xsd %s is not a xsd", xsdPath));
        }
        if (!xmlsDir.exists() && !xmlsDir.isDirectory()) {
            throw new CmdLineException(String.format("-xmls-dir %s does not exist or not a dir", xmlsDir));
        }
        return this;
    }
}
