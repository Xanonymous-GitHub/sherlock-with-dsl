package uk.ac.warwick.dcs.sherlock.engine.executor.work;

import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.ModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.engine.executor.JobStatus;

import java.util.List;

/**
 * Access interface for work tasks
 */
public interface IWorkTask {

    void addModelDataItem(ModelDataItem item);

    Class<? extends IDetector> getDetector();

    String getLanguage();

    List<PreProcessingStrategy> getPreProcessingStrategies();

    JobStatus getJobStatus();

}
