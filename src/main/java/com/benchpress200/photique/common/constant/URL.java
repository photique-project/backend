package com.benchpress200.photique.common.constant;

public class URL {
    // BASE
    public static final String BASE_URL = "/api/v1";

    // AUTH
    public static final String AUTH_DOMAIN = "/auth";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";
    public static final String SEND_MAIL = "/mail";
    public static final String VALIDATE_CODE = "/code";
    public static final String VALIDATE_NICKNAME = "/nickname";

    // USER
    public static final String USER_DOMAIN = "/users";
    public static final String USER_DATA = "/{userId}";
    public static final String GET_USER_ID = "/id";

    // SINGLE WORK
    public static final String SINGLE_WORK_DOMAIN = "/singleworks";
    public static final String SINGLE_WORK_DATA = "/{singleworkId}";
    public static final String LIKE = "/like";

    // SINGLE WORK COMMENT
    public static final String SINGLE_WORK_COMMENT_DOMAIN = "/singleworks/{singleworkId}/comments";
    public static final String SINGLE_WORK_COMMENT_DATA = "/{commentId}";
}
