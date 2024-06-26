package uk.ac.warwick.dcs.sherlock.module.web.data.models.forms;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import uk.ac.warwick.dcs.sherlock.module.web.data.wrappers.TemplateWrapper;
import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.ValidLanguage;

import java.util.ArrayList;
import java.util.List;

/**
 * The form to add/modify templates
 */
public class TemplateForm {

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

    @NotNull(message = "{error.public.empty}")
    public boolean isPublic;

    public List<String> detectors = new ArrayList<>();

    public TemplateForm() {
    }

    public TemplateForm(String language) {
        this.language = language;
    }

    public TemplateForm(TemplateWrapper templateWrapper) {
        assert templateWrapper.getTemplate() != null;
        this.name = templateWrapper.getTemplate().name;
        this.language = templateWrapper.getTemplate().language;
        this.isPublic = templateWrapper.getTemplate().isPublic;
        templateWrapper.getTemplate().detectors.forEach(d -> this.detectors.add(d.name));
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

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public List<String> getDetectors() {
        return detectors;
    }

    public void setDetectors(List<String> detectors) {
        this.detectors = detectors;
    }

    //Required for form binding
    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}
