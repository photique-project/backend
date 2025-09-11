package com.benchpress200.photique.common.constant;

public class URL {
    // BASE
    public static final String BASE_URL = "/api/v1";
    public static final String ALL = "/*";
    public static final String COMMENT_DOMAIN = "/comments";

    // AUTH
    public static final String AUTH_DOMAIN = "/auth";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";
    public static final String JOIN_MAIL = "/mail/join";
    public static final String PASSWORD_MAIL = "/mail/password";
    public static final String VALIDATE_CODE = "/code";
    public static final String VALIDATE_NICKNAME = "/nickname";

    // USER
    public static final String USER_DOMAIN = "/users";
    public static final String USER_DATA = "/{userId}";
    public static final String WHO_AM_I = "/me";
    public static final String PASSWORD = "/password";

    // FOLLOW
    public static final String FOLLOW_DOMAIN = "/follows";
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

    // NOTIFICATION
    public static final String SUB = "/subscribe";
    public static final String NOTIFICATION_DOMAIN = "/notifications";
    public static final String NOTIFICATION_DATA = "/{notificationId}";
    public static final String UNREAD = "/unread";
}
