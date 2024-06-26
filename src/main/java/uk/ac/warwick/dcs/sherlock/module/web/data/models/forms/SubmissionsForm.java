package uk.ac.warwick.dcs.sherlock.module.web.data.models.forms;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

/**
 * The form to upload submission(s)
 */
public class SubmissionsForm {
    @NotEmpty(message = "{error.file.empty}")
    public MultipartFile[] files;

    @NotNull(message = "{error.single.empty}")
    public boolean single;

    @NotNull(message = "{error.duplicate}")
    @Min(value = 0, message = "{error.duplicate}")
    @Max(value = 2, message = "{error.duplicate}")
    public int duplicate;

    public SubmissionsForm() {
    }

    public SubmissionsForm(MultipartFile[] files, boolean single) {
        this.files = files;
        this.single = single;
    }

    public MultipartFile[] getFiles() {
        return this.files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }

    public boolean isSingle() {
        return single;
    }

    public boolean getSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public int getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(int duplicate) {
        this.duplicate = duplicate;
    }
}
