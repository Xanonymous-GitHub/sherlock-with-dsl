package uk.ac.warwick.dcs.sherlock.engine.executor.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.warwick.dcs.sherlock.api.SherlockHelper;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;
import uk.ac.warwick.dcs.sherlock.engine.executor.IExecutor;

import java.lang.reflect.Field;
import java.util.*;

public class ExecutorUtils {

	public static final Logger logger = LoggerFactory.getLogger(IExecutor.class);

	public static <T> void processAdjustableParameters(T instance, Map<String, Float> params) {
		Arrays.stream(instance.getClass().getDeclaredFields()).map(f -> new Tuple<>(f, f.getDeclaredAnnotationsByType(AdjustableParameter.class))).filter(x -> x.getValue().length == 1).forEach(x -> {
			String ref = SherlockHelper.buildFieldReference(x.getKey());
			if (params.containsKey(ref)) {
				float val = params.get(ref);
				boolean isInt = x.getKey().getType().equals(int.class);

				if (isInt && val % 1 != 0) {
					synchronized (logger) {
						logger.error("Trying to assign a float value to integer adjustable parameter {}", ref);
					}
					return;
				}

				if (val > x.getValue()[0].maxumumBound() || val < x.getValue()[0].minimumBound()) {
					synchronized (logger) {
						logger.error("Trying to assign an out of bounds value to adjustable parameter {}", ref);
					}
					return;
				}

				Field f = x.getKey();
				f.setAccessible(true);
				try {
					if (isInt) {
						int vali = (int) val;
						f.set(instance, vali);
					}
					else {
						f.set(instance, val);
					}
				}
				catch (IllegalAccessException | IllegalArgumentException | NullPointerException e) {
					synchronized (logger) {
						logger.error("Could not set adjustable parameter", e);
					}
				}
			}
		});
	}

}
