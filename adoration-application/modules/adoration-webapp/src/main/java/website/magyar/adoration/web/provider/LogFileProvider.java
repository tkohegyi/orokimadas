package website.magyar.adoration.web.provider;

import website.magyar.adoration.web.service.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Provides access to the log files.
 */
@Component
public class LogFileProvider {

    private static final String ERROR_MESSAGE = "Error occurred while reading file!";
    private static final String FILE_NOT_FOUND_MESSAGE = "File not found!";
    private static final String LOG_PATH = "log";
    private final Logger logger = LoggerFactory.getLogger(LogFileProvider.class);
    @Autowired
    private FileUtils fileUtils;

    /**
     * Returns the collection of log file names.
     *
     * @return the collection of log file names
     */
    public Collection<String> getLogFileNames() {
        var files = getLogFiles(LOG_PATH);
        return createFileNames(files);
    }

    /**
     * Returns the content of the log file.
     *
     * @param fileName the name of the log file
     * @return the content of the log file
     */
    public String getLogContent(final String fileName) {
        var files = getLogFiles(LOG_PATH);
        var file = findFile(fileName, files);
        return getContent(file);
    }

    private Collection<File> getLogFiles(final String logPath) {
        return fileUtils.getFilesWithExtension(logPath, "txt");
    }

    private Collection<String> createFileNames(final Collection<File> files) {
        Collection<String> fileNames = new ArrayList<>();
        for (var file : files) {
            fileNames.add(file.getName());
        }
        return fileNames;
    }

    private String getContent(final File file) {
        var source = FILE_NOT_FOUND_MESSAGE;
        if (file != null) {
            try {
                source = fileUtils.readFileToString(file);
            } catch (IOException e) {
                source = ERROR_MESSAGE;
                logger.warn(ERROR_MESSAGE, e);
            }
        }
        return source;
    }

    private File findFile(final String fileName, final Collection<File> files) {
        File result = null;
        for (var file : files) {
            if (file.getName().equals(fileName)) {
                result = file;
                break;
            }
        }
        return result;
    }
}
