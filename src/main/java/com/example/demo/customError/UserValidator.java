package com.example.demo.customError;

import com.example.demo.database.entity.User;
import com.example.demo.model.user.UserCreateDto;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class UserValidator implements Validator {
    @Autowired
    private UserService userService;
    @Override
    public boolean supports(Class<?> clazz) {
        return UserCreateDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserCreateDto userCreateDto = (UserCreateDto) target;

        if (userCreateDto.getName() == null) {
            errors.rejectValue("name", "error.name.notNull",
                    ValidationMessages.NAME_NOT_NULL);
        }

        if (userCreateDto.getUsername() == null || userCreateDto.getUsername().isEmpty()) {
            errors.rejectValue("username", "error.username.notBlank",
                    ValidationMessages.USERNAME_NOT_BLANK);
        } else {
            Optional<User> existingUser = userService.findByUsername(userCreateDto.getUsername());
            if (existingUser.isPresent() && !existingUser.get().getUserId().equals(userCreateDto.getId())) {
                errors.rejectValue("username", "error.username.duplicate",
                        "Username already exists");
            }
        }

        if (userCreateDto.getPassword() == null || userCreateDto.getPassword().isEmpty()) {
            errors.rejectValue("password", "error.password.notBlank",
                    ValidationMessages.PASSWORD_NOT_BLANK);
        } else if (userCreateDto.getPassword().length() < 7) {
            errors.rejectValue("password", "error.password.minLength",
                    ValidationMessages.PASSWORD_MIN_LENGTH);
        }

        if (userCreateDto.getEmail() == null) {
            errors.rejectValue("email", "error.email.notNull",
                    ValidationMessages.EMAIL_NOT_NULL);
        } else if (!userCreateDto.getEmail().matches("^[a-zA-Z0-9._%+-]+@gmail.com$")) {
            errors.rejectValue("email", "error.email.invalid",
                    ValidationMessages.EMAIL_INVALID);
        }

        if (!userCreateDto.getPhone().matches("\\d{10,12}")) {
            errors.rejectValue("phone", "error.phone.invalid",
                    ValidationMessages.PHONE_INVALID);
        }

        if (userCreateDto.getGender() == null) {
            errors.rejectValue("gender", "error.gender.notNull",
                    ValidationMessages.GENDER_NOT_NULL);
        }

        if (userCreateDto.getRole() == null) {
            errors.rejectValue("role", "error.role.notNull",
                    ValidationMessages.ROLE_NOT_NULL);
        }

        if (userCreateDto.getDepartment() == null) {
            errors.rejectValue("department", "error.department.notNull",
                    ValidationMessages.DEPARTMENT_NOT_NULL);
        }

        if (userCreateDto.getStatus() == null) {
            errors.rejectValue("status", "error.status.notNull",
                    ValidationMessages.STATUS_NOT_NULL);
        }

        if (userCreateDto.getNotes() == null) {
            // no error, note is optional
        } else if (userCreateDto.getNotes().length() > 500) {
            errors.rejectValue("notes", "error.notes.tooLong",
                    ValidationMessages.NOTE_TOO_LONG);
        }

    }

}
