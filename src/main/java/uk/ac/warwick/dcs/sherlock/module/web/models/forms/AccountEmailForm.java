package uk.ac.warwick.dcs.sherlock.module.web.models.forms;

import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.PasswordsMatch;
import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.ValidPassword;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AccountEmailForm {
    @Size(min = 1, message = "{error_email_empty}")
    @Email(message = "{error_email_invalid}")
    public String email;

    @NotNull(message = "{error_old_password_invalid}")
    @ValidPassword
    public String oldPassword;

    public AccountEmailForm() { }

    public AccountEmailForm(Account account) {
        this.email = account.getEmail();
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
