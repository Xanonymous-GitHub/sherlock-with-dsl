package uk.ac.warwick.dcs.sherlock.module.web.data.models.forms;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.WorkspaceWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.ValidLanguage;

/**
 * The form to add/modify workspaces
 */
public class WorkspaceForm {

    @NotNull(message = "{error.name.empty}")
    @Size.List({
            @Size(
                    min = 1,
                    message = "{error.name.empty}"),
            @Size(
                    max = 64,
                    message = "{error.name.max_length}")
    })
    public String name;

    @NotNull(message = "{error.language.empty}")
    @ValidLanguage
    public String language;

    public WorkspaceForm() {
    }

    public WorkspaceForm(WorkspaceWrapper workspaceWrapper) {
        this.name = workspaceWrapper.getName();
        this.language = workspaceWrapper.getLanguage();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
