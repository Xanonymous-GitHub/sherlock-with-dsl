package uk.ac.warwick.dcs.sherlock.module.web.data.models.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.ValidPassword;

/**
 * The form for changing an account email
 */
public class AccountEmailForm {
    @Size(min = 1, message = "{error.email.empty}")
    @Email(message = "{error.email.invalid}")
    public String email;

    @NotNull(message = "{error.current_password.invalid}")
    @ValidPassword
    public String oldPassword;

    public AccountEmailForm() {
    }

    public AccountEmailForm(Account account) {
        this.email = account.email;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
