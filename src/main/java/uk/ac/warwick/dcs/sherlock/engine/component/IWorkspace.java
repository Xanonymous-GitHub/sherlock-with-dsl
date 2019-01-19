package uk.ac.warwick.dcs.sherlock.engine.component;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;

import java.util.*;

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

}