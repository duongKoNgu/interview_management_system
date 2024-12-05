package com.example.demo.customError;

public final class ValidationMessages {
    private ValidationMessages() {}
    public static final String SCHEDULE_TITLE_NOT_BLANK = "Please enter Schedule Title";
    public static final String CANDIDATE_LIST_NOT_BLANK = "Please enter Candidate Name";
    public static final String SCHEDULE_TIME_NOT_NULL = "Please enter Schedule Time";
    public static final String SCHEDULE_FROM_NOT_NULL = "Please enter Schedule From";
    public static final String SCHEDULE_TO_NOT_NULL = "Please enter Schedule To";
    public static final String LOCATION_NOT_BLANK = "Please enter Location";
    public static final String JOB_NOT_BLANK = "Please enter Job";
    public static final String INTERVIEW_NOT_BLANK = "Please enter Interviewer";
    public static final String RECRUITER_OWNER_NOT_BLANK = "Please enter Recruiter Owner";
    public static final String MEETING_ID_NOT_BLANK = "Please enter Meeting ID";
    public static final String SCHEDULE_TIME_VALID = "Schedule time should not be yesterday or in the past";
    public static final String SCHEDULE_TIMES_VALID = "Start time must be before end time";

    public static final String NAME_NOT_NULL = "Please enter Fullname";
    public static final String USERNAME_NOT_BLANK = "Please enter Username";
    public static final String PASSWORD_NOT_BLANK = "Please enter Password";
    public static final String PASSWORD_MIN_LENGTH = "Password must be at least 7 characters long";
    public static final String EMAIL_NOT_NULL = "Please enter Email";
    public static final String EMAIL_INVALID = "Email address must be in the format of 'username@gmail.com'";
    public static final String PHONE_INVALID = "Phone number must be between 10 and 12 digits";
    public static final String GENDER_NOT_NULL = "Please enter Gender";
    public static final String ROLE_NOT_NULL = "Please enter Role";
    public static final String DEPARTMENT_NOT_NULL = "Please enter Department";
    public static final String STATUS_NOT_NULL = "Please enter Status";
    public static final String NOTE_TOO_LONG = "Note cannot exceed 500 characters";}
