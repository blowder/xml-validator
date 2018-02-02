import org.kohsuke.args4j.CmdLineException;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {

    private static Schema initSchema(File xsdFile) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return factory.newSchema(new StreamSource(xsdFile));
    }

    public static void main(String[] args) throws CmdLineException, IOException, SAXException {
        RunOptions runOptions = new RunOptions().parseArgs(args);

        Schema schema = initSchema(runOptions.getXsdPath());
        FileWriter logWriter = new FileWriter(runOptions.getLogFile(), true);

        ExecutorService executorService = Executors.newFixedThreadPool(100);

        DirectoryStream<Path> paths = Files.newDirectoryStream(runOptions.getXmlsDir().toPath());
        for (Path path : paths) {
            executorService.submit(() -> {
                File source = path.toFile();
                if (source.getName().toLowerCase().endsWith(".xml")) {
                    Validator validator = schema.newValidator();
                    validator.setErrorHandler(new XmlErrorsCollector(source.getName(), logWriter));
                    try {
                        validator.validate(new StreamSource(source));
                        logWriter.flush();
                    } catch (SAXException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Not a xml, skipped: " + source.getAbsolutePath());
                }
            });
        }
        executorService.shutdown();
        System.out.println("---All jobs were scheduled---");
    }


}
