package uk.ac.warwick.dcs.sherlock.module.web.data.models.forms;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.PasswordsMatch;
import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.ValidPassword;

/**
 * The form for changing an account password
 */
@PasswordsMatch
public class AccountPasswordForm {
    @NotNull(message = "{error.current_password.invalid}")
    @ValidPassword
    public String oldPassword;

    @NotNull(message = "{error.password.empty}")
    @Size.List({
            @Size(
                    min = 8,
                    message = "{error.password.empty}"),
            @Size(
                    max = 64,
                    message = "{error.password.max_length}")
    })
    public String newPassword;

    public String confirmPassword;

    public AccountPasswordForm() {
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
