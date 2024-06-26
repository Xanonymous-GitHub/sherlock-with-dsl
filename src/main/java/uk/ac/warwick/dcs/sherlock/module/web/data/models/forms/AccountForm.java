package uk.ac.warwick.dcs.sherlock.module.web.data.models.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Role;
import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.ValidPassword;

import java.util.Set;

/**
 * The form for creating a new sub-account
 */
public class AccountForm {
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

    @Size(min = 1, message = "{error.email.empty}")
    @Email(message = "{error.email.invalid}")
    public String email;

    @NotNull(message = "{error.admin.empty}")
    public boolean isAdmin;

    @NotNull(message = "{error.current_password.invalid}")
    @ValidPassword
    public String oldPassword;

    public AccountForm() {
    }

    public AccountForm(Account account) {
        this.name = account.username;
        this.email = account.email;

        this.isAdmin = false;
        Set<Role> roles = account.roles;
        for (Role role : roles) {
            if (role.name != null && role.name.equals("ADMIN")) {
                this.isAdmin = true;
                break;
            }
        }
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean admin) {
        isAdmin = admin;
    }
}
