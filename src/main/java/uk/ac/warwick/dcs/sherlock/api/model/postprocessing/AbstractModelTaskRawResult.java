package uk.ac.warwick.dcs.sherlock.api.model.postprocessing;

import java.io.Serial;
import java.io.Serializable;

/**
 * Raw results storage class, acts a stored cache. Data structure from this can be directly accessed in post-processing
 * <p>
 * Must be serializable!!!
 */
public abstract class AbstractModelTaskRawResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 24L;

    /**
     * Tests if the rawResult set should be discarded, as it contains no results data
     *
     * @return is empty of data
     */
    public abstract boolean isEmpty();

    /**
     * Check that this object is of the same exact type as the baseline, including check any generic types are equal
     * <p>
     * TODO: suggested implementation here.....
     *
     * @param baseline the baseline object, in the set, current instance must be of the same exact type as this
     * @return is same type?
     */
    public abstract boolean testType(AbstractModelTaskRawResult baseline);

}
