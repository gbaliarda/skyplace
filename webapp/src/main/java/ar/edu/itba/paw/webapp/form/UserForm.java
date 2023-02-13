package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.FieldsEqualConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.UniqueEmailConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.UniqueUsernameConstraint;
import ar.edu.itba.paw.webapp.validators.interfaces.ValidChainConstraint;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@FieldsEqualConstraint(first = "password", second = "passwordRepeat", errorMessageTemplate = "PasswordsEqualConstraint.userForm")
public class UserForm {

    @NotBlank
    @Email
    @UniqueEmailConstraint(errorMessageTemplate = "UniqueEmailConstraint.email")
    private String email;

    @NotBlank
    @Pattern(regexp = "^0x[a-fA-F0-9]{40}$")
    private String walletAddress;

    @NotBlank
    @ValidChainConstraint(errorMessageTemplate = "InvalidChainException.message")
    private String walletChain;

    @NotBlank
    @Size(min = 6, max = 20)
    @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9]*")
    @UniqueUsernameConstraint(errorMessageTemplate = "UniqueUsernameConstraint.username")
    private String username;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank
    @Size(min = 6, max = 100)
    private String passwordRepeat;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWalletChain() {
        return walletChain;
    }

    public void setWalletChain(String walletChain) {
        this.walletChain = walletChain;
    }

    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }
}
