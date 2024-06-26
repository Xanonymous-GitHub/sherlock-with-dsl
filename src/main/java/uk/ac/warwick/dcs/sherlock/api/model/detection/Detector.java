package uk.ac.warwick.dcs.sherlock.api.model.detection;

import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract implementation of the IDetector interface, this should be used as the base to construct all detectors
 * <br><br>
 * Supports adjustable parameters see {@link uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter}
 *
 * @param <T> {@link DetectorWorker} implementation used by this detector
 */
public abstract class Detector<T extends DetectorWorker> implements IDetector<T> {

    private final String name;
    private final List<PreProcessingStrategy> strategies;
    private String description;

    /**
     * {@link IDetector} implementation without description
     *
     * @param displayName             user facing display name for the detector
     * @param preProcessingStrategies preprocessing strategies to use for this detector, can be one of many.
     */
    public Detector(String displayName, PreProcessingStrategy... preProcessingStrategies) {
        this(displayName, "", preProcessingStrategies);
    }

    /**
     * {@link IDetector} implementation with description
     *
     * @param displayName             user facing display name for the detector
     * @param description             user facing description for the detector
     * @param preProcessingStrategies preprocessing strategies to use for this detector, can be one of many.
     */
    public Detector(String displayName, String description, PreProcessingStrategy... preProcessingStrategies) {
        this.name = displayName;
        this.description = description;
        this.strategies = new LinkedList<>();
        Collections.addAll(this.strategies, preProcessingStrategies);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract List<T> buildWorkers(List<ModelDataItem> data);

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getDescription() {
        return this.description;
    }

    /**
     * Sets the user facing description
     *
     * @param description description string
     */
    public final void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getDisplayName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<PreProcessingStrategy> getPreProcessors() {
        return this.strategies;
    }
}
