package uk.ac.warwick.dcs.sherlock.api.component;

import java.io.InputStream;
import java.util.List;

/**
 * Interface for accessing the data from an individual source file from a submission
 */
public interface ISourceFile {

    /**
     * File equality check
     *
     * @param file file to compare
     * @return equals
     */
    boolean equals(ISourceFile file);

    /**
     * @return the content of the file
     */
    InputStream getFileContents();

    /**
     * @return the content of the file as a string
     */
    String getFileContentsAsString();

    /**
     * @return the content of the file as a list of strings
     */
    List<String> getFileContentsAsStringList();

    /**
     * @return a web path safe file identifier
     */
    String getFileIdentifier();

    /**
     * @return string containing display formatted file name
     */
    String getFileDisplayName();

    /**
     * @return string containing display formatted file path
     */
    String getFileDisplayPath();

    /**
     * @return fetches a unique, persistent id for the file. No other file should EVER be able to take this ID, even if this file is deleted.
     */
    long getPersistentId();

    /**
     * Get the id for the immediate parent submission, this may not be the top level submission seen by the user
     *
     * @return the id for the submission
     */
    long getArchiveId();

    /**
     * Get the top level, "super parent", submission object, this is the submission for this file seen by the user, NOT a sub-directory
     *
     * @return the highest level parent submission
     */
    ISubmission getSubmission();

    /**
     * Remove the file
     */
    void remove();

    /**
     * Count of the number of lines in the file containing characters, non empty
     *
     * @return count of non empty lines
     */
    int getNonEmptyLineCount();

    /**
     * Count of the total number of lines in the file
     *
     * @return count of all lines
     */
    int getTotalLineCount();

    /**
     * Fetches the file size in bytes
     *
     * @return file size in bytes
     */
    long getFileSize();

    /**
     * Fetches the file size in String form with the correct extension
     *
     * @param si use SI (1000) or binary (1024) for calculations
     * @return string for the file size
     */
    String getDisplayFileSize(boolean si);

}
