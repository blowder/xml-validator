import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.FileWriter;
import java.io.IOException;

public class XmlErrorsCollector implements ErrorHandler {
    private static final String LOG_MESSAGE_PATTERN = "[%1$-4s][%2$-80s][line:%3$-4s,col:%4$-4s][%5$s]\n";
    private final FileWriter log;
    private final String fileName;


    public XmlErrorsCollector(String fileName, FileWriter log) {
        this.fileName = fileName;
        this.log = log;
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        log(String.format(LOG_MESSAGE_PATTERN, "WARN", fileName, exception.getLineNumber(), exception.getColumnNumber(), exception.getMessage()));
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        log(String.format(LOG_MESSAGE_PATTERN, "ERROR", fileName, exception.getLineNumber(), exception.getColumnNumber(), exception.getMessage()));
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        log(String.format(LOG_MESSAGE_PATTERN, "FATAL", fileName, exception.getLineNumber(), exception.getColumnNumber(), exception.getMessage()));
    }

    private void log(String message) throws SAXException {
        try {
            log.append(message);
        } catch (IOException e) {
            throw new SAXException(e);
        }
    }

}
