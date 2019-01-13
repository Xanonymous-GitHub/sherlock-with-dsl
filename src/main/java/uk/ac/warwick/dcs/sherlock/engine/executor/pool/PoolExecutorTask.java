package uk.ac.warwick.dcs.sherlock.engine.executor.pool;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.ModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IPreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;
import uk.ac.warwick.dcs.sherlock.engine.component.ITask;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.IPriorityWorkSchedulerWrapper;
import uk.ac.warwick.dcs.sherlock.engine.executor.work.IWorkTask;

import java.util.*;

public class PoolExecutorTask implements Runnable, IWorkTask {

	private IPriorityWorkSchedulerWrapper scheduler;
	private ITask task;

	private Language language;
	private Class<? extends Lexer> lexerClass;
	private Class<? extends Parser> parserClass;
	private List<IPreProcessingStrategy> preProcessingStrategies;

	List<ModelDataItem> dataItems;

	PoolExecutorTask(IPriorityWorkSchedulerWrapper scheduler, ITask task, Language language) {
		this.scheduler = scheduler;
		this.task = task;
		this.language = language;

		this.dataItems = Collections.synchronizedList(new LinkedList<>());

		try {
			IDetector instance = task.getDetector().newInstance();
			this.lexerClass = instance.getLexer(language);
			this.parserClass = instance.getParser(language);
			this.preProcessingStrategies = instance.getPreProcessors(); // TODO: update this to be by language (for parser based stuff)
		}
		catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addModelDataItem(ModelDataItem item) {
		this.dataItems.add(item);
	}

	@Override
	public Language getLanguage() {
		return this.language;
	}

	@Override
	public Class<? extends Lexer> getLexerClass() {
		return this.lexerClass;
	}

	@Override
	public Class<? extends Parser> getParserClass() {
		return this.parserClass;
	}

	@Override
	public List<IPreProcessingStrategy> getPreProcessingStrategies() {
		return preProcessingStrategies;
	}

	@Override
	public Class<? extends IDetector> getDetector() {
		return this.task.getDetector();
	}

	@Override
	public void run() {

	}
}

