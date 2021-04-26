package website.magyar.adoration.web.service;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Wrapper for {@link org.apache.commons.io.FileUtils}.
 */
@Component
public class FileUtils {

    /**
     * Returns the files contained in the folder (non-recursive) with the provided extension.
     *
     * @param folder    the folder to check for files
     * @param extension the provided extension
     * @return the files in the folder of the specified extension
     */
    @SuppressWarnings("unchecked")
    public Collection<File> getFilesWithExtension(final String folder, final String extension) {
        return org.apache.commons.io.FileUtils.listFiles(new File(folder), new String[]{extension}, false);
    }

    /**
     * Reads the contents of a file into a String using the default encoding for the VM. The file is always closed.
     *
     * @param file the file to read, must not be null
     * @return the file contents, never null
     * @throws IOException in case of an I/O error
     */
    public String readFileToString(final File file) throws IOException {
        return org.apache.commons.io.FileUtils.readFileToString(file);
    }
}
