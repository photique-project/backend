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

    // FOLLOW
    public static final String FOLLOW_DOMAIN = "/follows";
    public static final String UNFOLLOW_TARGET = "/{targetUserId}";
    public static final String FOLLOWER = "/follower";
    public static final String FOLLOWING = "/following";

    // SINGLE WORK
    public static final String SINGLE_WORK_DOMAIN = "/singleworks";
    public static final String SINGLE_WORK_DATA = "/{singleworkId}";
    public static final String LIKE = "/like";

    // SINGLE WORK COMMENT
    public static final String SINGLE_WORK_COMMENT_DOMAIN = "/singleworks/{singleworkId}/comments";
    public static final String SINGLE_WORK_COMMENT_DATA = "/{commentId}";

    // EXHIBITION
    public static final String EXHIBITION_DOMAIN = "/exhibitions";
    public static final String EXHIBITION_DATA = "/{exhibitionId}";
    public static final String BOOKMARK = "/bookmark";

    // EXHIBITION COMMENT
    public static final String EXHIBITION_COMMENT_DOMAIN = "/exhibitions/{exhibitionId}/comments";
    public static final String EXHIBITION_COMMENT_DATA = "/{commentId}";
}
