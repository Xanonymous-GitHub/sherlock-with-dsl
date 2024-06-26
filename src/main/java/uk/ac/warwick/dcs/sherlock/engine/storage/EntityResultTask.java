package uk.ac.warwick.dcs.sherlock.engine.storage;

import uk.ac.warwick.dcs.sherlock.api.component.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.component.IResultTask;
import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.component.ITask;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * IResultTask object for base storage implementation
 */
@Entity(name = "ResultTask")
public class EntityResultTask implements IResultTask, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne
    private EntityResultFile fileRes;

    private EntityTask task;
    private float taskScore;

    private Map<EntityFile, Float> fileScores;
    private List<EntityCodeBlockGroup> containingBlocks;

    EntityResultTask() {
        super();
    }

    EntityResultTask(EntityResultFile fileRes, EntityTask task) {
        super();
        this.fileRes = fileRes;
        this.task = task;
        this.taskScore = 0;

        this.fileScores = new HashMap<>();
        this.containingBlocks = new LinkedList<>();
    }

    @Override
    public void addContainingBlock(ICodeBlockGroup blockGroup) {
        if (blockGroup instanceof EntityCodeBlockGroup g) {
            this.containingBlocks.add(g);
        }
    }

    @Override
    public void addContainingBlock(Collection<ICodeBlockGroup> blockGroups) {
        if (blockGroups != null && !blockGroups.isEmpty()) {
            blockGroups.forEach(this::addContainingBlock);
        }
    }

    @Override
    public void addFileScore(ISourceFile file, float score) {
        if (file instanceof EntityFile) {
            this.fileScores.put((EntityFile) file, score);
        }
    }

    @Override
    public List<ICodeBlockGroup> getContainingBlocks() {
        return new LinkedList<>(this.containingBlocks);
    }

    @Override
    public float getFileScore(ISourceFile file) {
        if (file instanceof EntityFile) {
            return this.fileScores.getOrDefault(file, 0f);
        }

        return 0;
    }

    @Override
    public Map<ISourceFile, Float> getFileScores() {
        return new HashMap<>(this.fileScores);
    }

    @Override
    public ITask getTask() {
        return this.task;
    }

    @Override
    public float getTaskScore() {
        return this.taskScore;
    }

    @Override
    public void setTaskScore(float score) {
        this.taskScore = score;
    }

    void remove() {
        this.containingBlocks.forEach(EntityCodeBlockGroup::markRemove);
        BaseStorage.instance.database.removeObject(this);
    }
}
