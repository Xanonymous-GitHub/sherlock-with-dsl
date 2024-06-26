package uk.ac.warwick.dcs.sherlock.api.component;

import java.util.List;

/**
 * Workspace object groups, submission, jobs, results. Is top level access to all database objects
 */
public interface IWorkspace {

    /**
     * Creates a new job instance
     *
     * @return the new job
     */
    IJob createJob();

    /**
     * @return the list of files currently associated with the workspace
     */
    List<ISourceFile> getFiles();

    /**
     * @return the list of submissions to the workspace
     */
    List<ISubmission> getSubmissions();

    /**
     * @return list of job history
     */
    List<IJob> getJobs();

    /**
     * @return the language for the workspace
     */
    String getLanguage();

    /**
     * @param lang set the workspace to use this language
     */
    void setLanguage(String lang);

    /**
     * @return the name of the workspace
     */
    String getName();

    /**
     * @param name set the workspace name to this
     */
    void setName(String name);

    /**
     * @return the unique id for the workspace
     */
    long getPersistentId();

    /**
     * Remove the workspace, and all of the files and results associated, cannot be undone
     */
    void remove();

}
