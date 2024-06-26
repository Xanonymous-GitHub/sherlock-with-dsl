package uk.ac.warwick.dcs.sherlock.engine.storage;

import uk.ac.warwick.dcs.sherlock.api.component.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.component.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.exception.UnknownDetectionTypeException;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;
import uk.ac.warwick.dcs.sherlock.api.registry.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import javax.persistence.Entity;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * ICodeBlockGroup object for base storage implementation
 */
@SuppressWarnings("Duplicates")
@Entity(name = "CodeBlockGroup")
public class EntityCodeBlockGroup implements ICodeBlockGroup, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    Map<Long, EntityCodeBlock> blockMap;

    private String type;
    private String comment;

    public EntityCodeBlockGroup() {
        super();
        this.type = null;
        this.comment = null;
        this.blockMap = new HashMap<>();
    }

    @Override
    public void addCodeBlock(ISourceFile file, float score, ITuple<Integer, Integer> line) {
        if (this.blockMap.containsKey(file.getPersistentId())) {
            this.blockMap.get(file.getPersistentId()).append(score, line);
        } else {
            if (file instanceof EntityFile) {
                EntityCodeBlock block = new EntityCodeBlock(this, (EntityFile) file, score, line);
                this.blockMap.put(file.getPersistentId(), block);
            } else {
                BaseStorage.logger.error("Could not add file {}, it is not from the database", file.getFileDisplayName());
            }
        }
    }

    @Override
    public void addCodeBlock(ISourceFile file, float score, List<ITuple<Integer, Integer>> lines) {
        if (this.blockMap.containsKey(file.getPersistentId())) {
            this.blockMap.get(file.getPersistentId()).append(score, lines);
        } else {
            if (file instanceof EntityFile) {
                EntityCodeBlock block = new EntityCodeBlock(this, (EntityFile) file, score, lines);
                this.blockMap.put(file.getPersistentId(), block);
            } else {
                BaseStorage.logger.error("Could not add file {}, it is not from the database", file.getFileDisplayName());
            }
        }
    }

    @Override
    public boolean filePresent(ISourceFile file) {
        return this.blockMap.containsKey(file.getPersistentId());
    }

    @Override
    public boolean submissionIdPresent(long submissionId) {
        for (EntityCodeBlock codeBlock : this.blockMap.values()) {
            if (codeBlock.getFile().getSubmission().getId() == submissionId)
                return true;
        }

        return false;
    }

    @Override
    public ICodeBlock getCodeBlock(ISourceFile file) {
        return this.blockMap.get(file.getPersistentId());
    }

    @Override
    public List<? extends ICodeBlock> getCodeBlocks() {
        return new LinkedList<>(this.blockMap.values());
    }

    @Override
    public String getComment() {
        return this.comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public DetectionType getDetectionType() throws UnknownDetectionTypeException {
        return SherlockRegistry.getDetectionType(this.type);
    }

    @Override
    public void setDetectionType(String detectionTypeIdentifier) throws UnknownDetectionTypeException {
        this.type = detectionTypeIdentifier;
    }

    @Override
    public boolean isPopulated() {
        return this.blockMap.size() > 1;
    }

    void markRemove() {
        for (EntityCodeBlock e : this.blockMap.values()) {
            e.markRemove();
        }

        this.type = "---remove---";
    }
}
