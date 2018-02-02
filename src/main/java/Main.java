import org.kohsuke.args4j.CmdLineException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;


public class Main {


    public static void main(String[] args) throws CmdLineException, IOException, SAXException {
        RunOptions runOptions = new RunOptions().parseArgs(args);

        Schema schema = initSchema(runOptions.getXsdPath());

        //TODO: parallel and add correct handlers
        Validator validator = schema.newValidator();
        validator.setErrorHandler(
                new ErrorHandler() {
                    @Override
                    public void warning(SAXParseException e) throws SAXException {

                    }

                    @Override
                    public void error(SAXParseException e) throws SAXException {
                        //todo: save errors to log file
                        System.out.println(String.format("Error happened in line:%s with message:%s", e.getLineNumber(), e.getMessage()));
                    }

                    @Override
                    public void fatalError(SAXParseException e) throws SAXException {

                    }
                }
        );
        DirectoryStream<Path> paths = Files.newDirectoryStream(runOptions.getXmlsDir().toPath());
        for (Path path : paths) {
            File source = path.toFile();
            if (source.getName().toLowerCase().endsWith(".xml")) {
                validator.validate(new StreamSource(source));
                System.out.println(source.getAbsolutePath());
            } else {
                System.out.println("Not a xml, skipped: " + source.getAbsolutePath());
            }

        }

    }

    private static Schema initSchema(File xsdFile) throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return factory.newSchema(new StreamSource(xsdFile));
    }
}
