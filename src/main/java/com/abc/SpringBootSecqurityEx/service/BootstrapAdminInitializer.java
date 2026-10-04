package com.abc.SpringBootSecqurityEx.service;

import com.abc.SpringBootSecqurityEx.entity.User;
import com.abc.SpringBootSecqurityEx.enums.ERole;
import com.abc.SpringBootSecqurityEx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class BootstrapAdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final String adminUsername;
    private final String adminEmail;
    private final String adminPassword;

    private final String moderatorUsername;
    private final String moderatorEmail;
    private final String moderatorPassword;

    private final String employeeUsername;
    private final String employeeEmail;
    private final String employeePassword;

    private final String userUsername;
    private final String userEmail;
    private final String userPassword;

    private final String premiumUsername;
    private final String premiumEmail;
    private final String premiumPassword;

    public BootstrapAdminInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,

            @Value("${app.bootstrap-admin.username:}") String adminUsername,
            @Value("${app.bootstrap-admin.email:}") String adminEmail,
            @Value("${app.bootstrap-admin.password:}") String adminPassword,

            @Value("${app.bootstrap-moderator.username:}") String moderatorUsername,
            @Value("${app.bootstrap-moderator.email:}") String moderatorEmail,
            @Value("${app.bootstrap-moderator.password:}") String moderatorPassword,

            @Value("${app.bootstrap-employee.username:}") String employeeUsername,
            @Value("${app.bootstrap-employee.email:}") String employeeEmail,
            @Value("${app.bootstrap-employee.password:}") String employeePassword,

            @Value("${app.bootstrap-user.username:}") String userUsername,
            @Value("${app.bootstrap-user.email:}") String userEmail,
            @Value("${app.bootstrap-user.password:}") String userPassword,

            @Value("${app.bootstrap-premium.username:}") String premiumUsername,
            @Value("${app.bootstrap-premium.email:}") String premiumEmail,
            @Value("${app.bootstrap-premium.password:}") String premiumPassword
    ) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

        this.adminUsername = adminUsername;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;

        this.moderatorUsername = moderatorUsername;
        this.moderatorEmail = moderatorEmail;
        this.moderatorPassword = moderatorPassword;

        this.employeeUsername = employeeUsername;
        this.employeeEmail = employeeEmail;
        this.employeePassword = employeePassword;

        this.userUsername = userUsername;
        this.userEmail = userEmail;
        this.userPassword = userPassword;

        this.premiumUsername = premiumUsername;
        this.premiumEmail = premiumEmail;
        this.premiumPassword = premiumPassword;
    }

    @Override
    public void run(String... args) {

        createUser(
                adminUsername,
                adminEmail,
                adminPassword,
                "Administrator",
                ERole.ROLE_ADMIN
        );

        createUser(
                moderatorUsername,
                moderatorEmail,
                moderatorPassword,
                "Moderator",
                ERole.ROLE_MODERATOR
        );

        createUser(
                employeeUsername,
                employeeEmail,
                employeePassword,
                "Employee",
                ERole.ROLE_EMPLOYEE
        );

        createUser(
                userUsername,
                userEmail,
                userPassword,
                "Normal User",
                ERole.ROLE_USER
        );

        createUser(
                premiumUsername,
                premiumEmail,
                premiumPassword,
                "Premium User",
                ERole.ROLE_PREMIUM_USER
        );
    }

    private void createUser(
            String username,
            String email,
            String password,
            String firstName,
            ERole role
    ) {

        // --------------------------------------------------
        // 1. Skip completely empty configuration
        // --------------------------------------------------

        if (isBlank(username)
                && isBlank(email)
                && isBlank(password)) {

            System.out.println(
                    "Bootstrap configuration not provided for role: "
                            + role
            );

            return;
        }

        // --------------------------------------------------
        // 2. Validate configuration
        // --------------------------------------------------

        if (isBlank(username)) {
            throw new IllegalStateException(
                    "Bootstrap username is missing for role: " + role
            );
        }

        if (isBlank(email)) {
            throw new IllegalStateException(
                    "Bootstrap email is missing for user: " + username
            );
        }

        if (isBlank(password)) {
            throw new IllegalStateException(
                    "Bootstrap password is missing for user: " + username
            );
        }

        if (password.length() < 4) {
            throw new IllegalStateException(
                    "Bootstrap password must contain at least 4 characters: "
                            + username
            );
        }

        // --------------------------------------------------
        // 3. Check username
        // --------------------------------------------------

        if (userRepository.existsByUserName(username)) {

            System.out.println(
                    "Bootstrap user already exists: "
                            + username
            );

            return;
        }

        // --------------------------------------------------
        // 4. Check email
        // --------------------------------------------------

        if (userRepository.existsByEmail(email)) {

            System.out.println(
                    "Bootstrap email already exists: "
                            + email
            );

            return;
        }

        // --------------------------------------------------
        // 5. Create user
        // --------------------------------------------------

        User user = new User();

        user.setUserName(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setUserFirstName(firstName);
        user.setRoles(EnumSet.of(role));

        userRepository.save(user);

        System.out.println(
                "=========================================="
        );

        System.out.println(
                "Bootstrap user created"
        );

        System.out.println(
                "Username : " + username
        );

        System.out.println(
                "Email    : " + email
        );

        System.out.println(
                "Role     : " + role
        );

        System.out.println(
                "=========================================="
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}