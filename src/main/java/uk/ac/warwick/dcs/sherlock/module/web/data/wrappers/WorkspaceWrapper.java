package uk.ac.warwick.dcs.sherlock.module.web.data.wrappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.warwick.dcs.sherlock.api.component.*;
import uk.ac.warwick.dcs.sherlock.api.exception.SubmissionUnsupportedException;
import uk.ac.warwick.dcs.sherlock.api.exception.WorkspaceUnsupportedException;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.TDetector;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Workspace;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.SubmissionsForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.forms.WorkspaceForm;
import uk.ac.warwick.dcs.sherlock.module.web.data.repositories.WorkspaceRepository;
import uk.ac.warwick.dcs.sherlock.module.web.exceptions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The wrapper that manages the workspaces
 */
public class WorkspaceWrapper {
    /**
     * The database workspace entity
     */
    private Workspace workspace;
    /**
     * The engine workspace entity
     */
    private IWorkspace iWorkspace;

    /**
     * Initialise the workspace wrapper using the workspace form to create
     * a new workspace
     *
     * @param workspaceForm       the form
     * @param account             the account of the current user
     * @param workspaceRepository the database repository
     */
    public WorkspaceWrapper(
            WorkspaceForm workspaceForm,
            Account account,
            WorkspaceRepository workspaceRepository
    ) {
        this.iWorkspace = SherlockEngine.storage.createWorkspace(workspaceForm.getName(), workspaceForm.getLanguage());
        this.workspace = new Workspace(account, this.iWorkspace.getPersistentId());
        workspaceRepository.save(this.workspace);
    }

    /**
     * Initialise the workspace wrapper using an existing workspace
     *
     * @param workspace the workspace to manage
     * @throws IWorkspaceNotFound if the workspace entity was not found in the engine
     */
    public WorkspaceWrapper(Workspace workspace) throws IWorkspaceNotFound {
        this.init(workspace);
    }

    /**
     * Initialise the workspace wrapper using an id to find one in the database
     *
     * @param id                  the id of the account to find
     * @param account             the account of the current user
     * @param workspaceRepository the database repository
     * @throws WorkspaceNotFound  if the workspace was not found in the web database
     * @throws IWorkspaceNotFound if the workspace entity was not found in the engine
     */
    public WorkspaceWrapper(long id, Account account, WorkspaceRepository workspaceRepository)
            throws WorkspaceNotFound, IWorkspaceNotFound {
        Workspace workspace = workspaceRepository.findByIdAndAccount(id, account);

        if (workspace == null)
            throw new WorkspaceNotFound("Unable to find workspace.");

        this.init(workspace);
    }

    /**
     * Gets the list of workspaces owned by the current user
     *
     * @param account             the account of the current user
     * @param workspaceRepository the database repository
     * @return the list of workspaces
     */
    public static List<WorkspaceWrapper> findByAccount(Account account, WorkspaceRepository workspaceRepository) {
        List<Workspace> workspaces = workspaceRepository.findByAccount(account);
        List<WorkspaceWrapper> wrappers = new ArrayList<>();

        assert workspaces != null;
        for (Workspace workspace : workspaces) {
            try {
                wrappers.add(new WorkspaceWrapper(workspace));
            } catch (IWorkspaceNotFound iWorkspaceNotFound) {
                //Workspace no longer found in Engine database, remove from UI database
                workspaceRepository.delete(workspace);
            }
        }

        return wrappers;
    }

    /**
     * Finish initialising the wrapper
     *
     * @param workspace the workspace ot initialise the wrapper with
     * @throws IWorkspaceNotFound if the workspace entity was not found in the engine
     */
    private void init(Workspace workspace) throws IWorkspaceNotFound {
        this.workspace = workspace;

        List<Long> engineId = Collections.singletonList(this.workspace.engineId);
        List<IWorkspace> iWorkspaces = SherlockEngine.storage.getWorkspaces(engineId);

        if (iWorkspaces.size() == 1) {
            this.iWorkspace = iWorkspaces.getFirst();
        } else {
            throw new IWorkspaceNotFound("Unable to find workspace in engine.");
        }
    }

    /**
     * Get the web database workspace entity
     *
     * @return the workspace
     */
    public Workspace getWorkspace() {
        return workspace;
    }

    /**
     * Get the engine workspace entity
     *
     * @return the workspace
     */
    public IWorkspace getiWorkspace() {
        return this.iWorkspace;
    }

    /**
     * Get the workspace web id
     *
     * @return the id
     */
    public long getId() {
        return this.workspace.id;
    }

    /**
     * Get the workspace engine id
     *
     * @return the id
     */
    public long getEngineId() {
        return this.iWorkspace.getPersistentId();
    }

    /**
     * Get the workspace name
     *
     * @return the name
     */
    public String getName() {
        return this.iWorkspace.getName();
    }

    /**
     * Set the workspace name
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.iWorkspace.setName(name);
    }

    /**
     * Get the workspace language
     *
     * @return the language
     */
    public String getLanguage() {
        return this.iWorkspace.getLanguage();
    }

    /**
     * Set the workspace language
     *
     * @param language the new language
     */
    public void setLanguage(String language) {
        this.iWorkspace.setLanguage(language);
    }

    /**
     * Get the workspace files
     *
     * @return the list of files
     */
    public List<ISourceFile> getFiles() {
        return this.iWorkspace.getFiles();
    }

    /**
     * Get the workspace submissions
     *
     * @return the list of submissions
     */
    public List<ISubmission> getSubmissions() {
        return this.iWorkspace.getSubmissions();
    }

    /**
     * Get the workspace jobs
     *
     * @return the list of jobs
     */
    public List<IJob> getJobs() {
        return this.iWorkspace.getJobs();
    }

    /**
     * Update the workspace using the workspace form
     *
     * @param workspaceForm the form to use
     */
    public void set(WorkspaceForm workspaceForm) {
        this.setName(workspaceForm.getName());
        this.setLanguage(workspaceForm.getLanguage());
    }

    /**
     * Delete the workspace
     *
     * @param workspaceRepository the database repository
     */
    public void delete(WorkspaceRepository workspaceRepository) {
        this.iWorkspace.remove();
        workspaceRepository.delete(this.workspace);
    }

    /**
     * Add submissions to this workspace
     *
     * @param submissionsForm the form to use
     * @throws NoFilesUploaded  if no files were uploaded
     * @throws FileUploadFailed if uploading the files failed
     */
    public List<ITuple<ISubmission, ISubmission>> addSubmissions(SubmissionsForm submissionsForm) throws NoFilesUploaded, FileUploadFailed {
        int count = 0; //the number of submissions uploaded
        List<ITuple<ISubmission, ISubmission>> collisions = new ArrayList<>();
        boolean multiple = submissionsForm.getFiles().length == 1 && !submissionsForm.getSingle();

        for (MultipartFile file : submissionsForm.getFiles()) {
            if (file.getSize() > 0) {
                try {
                    collisions.addAll(SherlockEngine.storage.storeFile(this.getiWorkspace(), file.getOriginalFilename(), file.getBytes(), multiple));
                } catch (IOException | WorkspaceUnsupportedException e) {
                    throw new FileUploadFailed(e.getMessage());
                }
                count++;
            }
        }

        if (count == 0) {
            throw new NoFilesUploaded("No submissions uploaded");
        }

        for (ITuple<ISubmission, ISubmission> collision : collisions) {
            if (submissionsForm.getDuplicate() == 0) { //replace
                try {
                    SherlockEngine.storage.writePendingSubmission(collision.getValue());
                } catch (SubmissionUnsupportedException e) {
//                    e.printStackTrace();
                }
            } else if (submissionsForm.getDuplicate() == 1) { //keep
                try {
                    SherlockEngine.storage.removePendingSubmission(collision.getValue());
                } catch (SubmissionUnsupportedException e) {
//                    e.printStackTrace();
                }
            } else { //merge
                try {
                    SherlockEngine.storage.mergePendingSubmission(collision.getKey(), collision.getValue());
                } catch (SubmissionUnsupportedException e) {
//                    e.printStackTrace();
                }
            }
        }

        return collisions;
    }

    /**
     * Runs a template on a workspace
     *
     * @param templateWrapper the template to run
     * @throws TemplateContainsNoDetectors if there are no detectors in the template
     * @throws ClassNotFoundException      if the detector no longer exists
     * @throws ParameterNotFound           if the parameter no longer exists
     * @throws DetectorNotFound            if the detector no longer exists
     * @throws NoFilesUploaded             if no files were uploaded
     */
    public long runTemplate(TemplateWrapper templateWrapper) throws TemplateContainsNoDetectors, ClassNotFoundException, ParameterNotFound, DetectorNotFound, NoFilesUploaded {
        if (templateWrapper.getTemplate().detectors.isEmpty())
            throw new TemplateContainsNoDetectors("No detectors in chosen template.");

        if (this.getFiles().isEmpty()) {
            throw new NoFilesUploaded("No files in workspace");
        }

        IJob job = this.iWorkspace.createJob();

        for (TDetector td : templateWrapper.getTemplate().detectors) {
            Class<? extends IDetector> detector = (Class<? extends IDetector>) Class.forName(td.name, true, SherlockEngine.classloader);
            job.addDetector(detector);
        }

        job.prepare();

        Logger logger = LoggerFactory.getLogger(WorkspaceWrapper.class);
        for (ITask task : job.getTasks()) {
            for (DetectorWrapper detectorWrapper : templateWrapper.getDetectors()) {
                if (task.getDetector().getName().equals(detectorWrapper.getEngineDetector().getName())) {
                    for (ParameterWrapper parameterWrapper : detectorWrapper.getParametersList()) {
                        logger.info("Detector {} has had parameter {} set to {}",
                                task.getDetector().getName(),
                                parameterWrapper.getParameterObj().getDisplayName(),
                                parameterWrapper.getParameter().value
                        );
                        task.setParameter(parameterWrapper.getParameterObj(), parameterWrapper.getParameter().value);
                    }
                }
            }
        }

        SherlockEngine.executor.submitJob(job);

        return job.getPersistentId();
    }
}
