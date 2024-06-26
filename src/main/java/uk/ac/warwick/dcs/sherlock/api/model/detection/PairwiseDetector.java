package uk.ac.warwick.dcs.sherlock.api.model.detection;

import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.ExecutorUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * An abstract IDetector implementation which constructs an individual, parallel worker for each combination of files in the dataset. This can be used as a base for pairwise matching algorithms.
 * <br><br>
 * More advanced implementations should directly implement the IDetector interface.
 */
public class PairwiseDetector<T extends PairwiseDetectorWorker> extends Detector<T> {

    /**
     * Class object for the generic type of this detector's worker
     */
    private final Class<T> typeArgumentClass;

    /**
     * {@link IDetector} implementation which automatically builds a worker for each possible combination of the source files passed
     *
     * @param displayName             user facing display name for the detector
     * @param typeArgumentClass       class object for the generic type of this detector's worker
     * @param preProcessingStrategies preprocessing strategies to use for this detector, can be one of many.
     */
    public PairwiseDetector(String displayName, Class<T> typeArgumentClass, PreProcessingStrategy... preProcessingStrategies) {
        this(displayName, "", typeArgumentClass, preProcessingStrategies);
    }

    /**
     * {@link IDetector} implementation which automatically builds a worker for each possible combination of the source files passed
     *
     * @param displayName             user facing display name for the detector
     * @param description             user facing description for the detector
     * @param typeArgumentClass       class object for the generic type of this detector's worker
     * @param preProcessingStrategies preprocessing strategies to use for this detector, can be one of many.
     */
    public PairwiseDetector(String displayName, String description, Class<T> typeArgumentClass, PreProcessingStrategy... preProcessingStrategies) {
        super(displayName, description, preProcessingStrategies);

        this.typeArgumentClass = typeArgumentClass;
    }

    /**
     * Recursively creates a list of all the possible combinations (unordered) of a specific size of an input list
     *
     * @param list list of items to find combinations of
     * @param size size of the combinations, 2 returns pairs etc...
     * @param <E>  list typing
     * @return list of all possible combinations
     */
    private static <E> Stream<List<E>> combinations(List<E> list, int size) {
        if (size == 0) {
            return Stream.of(Collections.emptyList());
        } else {
            return IntStream.range(0, list.size()).boxed().flatMap(i -> combinations(list.subList(i + 1, list.size()), size - 1).map(t -> pipe(list.get(i), t)));
        }
    }

    private static <E> List<E> pipe(E head, List<E> tail) {
        List<E> newList = new ArrayList<>(tail);
        newList.addFirst(head);
        return newList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<T> buildWorkers(List<ModelDataItem> data) {
        return combinations(data, 2).filter(x -> !x.getFirst().getFile().getSubmission().equals(x.get(1).getFile().getSubmission())).map(x -> this.getAbstractPairwiseDetectorWorker(x.getFirst(), x.get(1)))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Fetches a new instance of the worker for this implementation
     *
     * @param file1Data ModelDataItem for file 1
     * @param file2Data ModelFataItem for file 2
     * @return the new worker instance
     */
    public T getAbstractPairwiseDetectorWorker(ModelDataItem file1Data, ModelDataItem file2Data) {

        try {
            try {
                return this.typeArgumentClass.getConstructor(IDetector.class, ModelDataItem.class, ModelDataItem.class).newInstance(this, file1Data, file2Data);
            } catch (NoSuchMethodException e) {
                return this.typeArgumentClass.getConstructor(this.getClass(), IDetector.class, ModelDataItem.class, ModelDataItem.class).newInstance(this, this, file1Data, file2Data);
            }
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException |
                 NoSuchMethodException e) {
            ExecutorUtils.logger
                    .error("Could not build workers for detector {}. Ensure that the detector is not an inner class and its worker class {} has a constructor matching constructor(IDetector parent, ModelDataItem file1Data, ModelDataItem file2Data)",
                            this.getClass().getName(), this.typeArgumentClass.getName());
        }

        return null;
    }
}
