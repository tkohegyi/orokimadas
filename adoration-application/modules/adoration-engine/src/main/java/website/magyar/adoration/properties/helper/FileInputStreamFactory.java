package website.magyar.adoration.properties.helper;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Factory class.
 * Used for creating instances of {@link FileInputStream}.*
 */
@Component
public class FileInputStreamFactory {

    /**
     * Creates a new instance of {@link FileInputStream}.
     *
     * @param location the location of the file
     * @return the new instance
     * @throws FileNotFoundException if the file does not exist,
     *                               is a directory rather than a regular file,
     *                               or for some other reason cannot be opened for reading.
     */
    public FileInputStream createFileInputStream(final String location) throws FileNotFoundException {
        return new FileInputStream(location);
    }

    /**
     * Creates a new instance of {@link FileInputStream}.
     *
     * @param file the file to read
     * @return the new instance
     * @throws FileNotFoundException if the file does not exist,
     *                               is a directory rather than a regular file,
     *                               or for some other reason cannot be opened for reading.
     */
    public FileInputStream createFileInputStream(final File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

}
