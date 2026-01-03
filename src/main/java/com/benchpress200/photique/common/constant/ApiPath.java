package com.benchpress200.photique.common.constant;

public class ApiPath {
    /*
     * BASE & COMMON
     */
    private static final String VERSION = "/api/v1";
    private static final String ALL = "/*";
    private static final String MY_DATA = "/me";
    private static final String COMMENT_DOMAIN = "/comments";
    private static final String COMMENT_DATA = COMMENT_DOMAIN + "/{commentId}";
    private static final String LIKE_DOMAIN = "/likes";
    private static final String BOOKMARK_DOMAIN = "/bookmarks";


    /*
     * AUTH
     */
    private static final String AUTH_DOMAIN = "/auth";

    // api/v1/auth
    public static final String AUTH_ROOT = VERSION + AUTH_DOMAIN;

    // api/v1/auth/login
    public static final String AUTH_LOGIN = AUTH_ROOT + "/login";

    // api/v1/auth/logout
    public static final String AUTH_LOGOUT = AUTH_ROOT + "/logout";

    // api/v1/auth/mail/join
    public static final String AUTH_MAIL_JOIN = AUTH_ROOT + "/mail/join";

    // api/v1/auth/mail/password
    public static final String AUTH_MAIL_PASSWORD = AUTH_ROOT + "/mail/password";

    // api/v1/auth/code
    public static final String AUTH_CODE = AUTH_ROOT + "/code";

    // api/v1/auth/refresh
    public static final String AUTH_REFRESH_TOKEN = AUTH_ROOT + "/refresh";


    /*
     * USER & FOLLOW
     */
    private static final String USER_DOMAIN = "/users";
    private static final String FOLLOW_DOMAIN = "/follows";

    // api/v1/users
    public static final String USER_ROOT = VERSION + USER_DOMAIN;

    // api/v1/users/{userId}
    public static final String USER_DATA = USER_ROOT + "/{userId}";

    // api/v1/users/{userId}/password
    public static final String USER_PASSWORD = USER_DATA + "/password";

    // api/v1/users/password
    public static final String USER_PASSWORD_RESET = USER_ROOT + "/password";

    // api/v1/users/nickname
    public static final String USER_NICKNAME_EXISTS = USER_ROOT + "/nickname";

    // api/v1/users/me
    public static final String USER_MY_DATA = USER_ROOT + MY_DATA;

    // api/v1/users/{userId}/follows
    public static final String FOLLOW_ROOT = USER_DATA + FOLLOW_DOMAIN;

    // api/v1/users/{userId}/follows/follower
    public static final String FOLLOWER = FOLLOW_ROOT + "follower";

    // api/v1/users/{userId}/followers/followee
    public static final String FOLLOWEE = FOLLOW_ROOT + "followee";


    /*
     * SINGLEWORK
     */
    private static final String SINGLEWORK_DOMAIN = "/singleworks";

    // api/v1/singleworks
    public static final String SINGLEWORK_ROOT = VERSION + SINGLEWORK_DOMAIN;

    // api/v1/singleworks/{singleworkId}
    public static final String SINGLEWORK_DATA = SINGLEWORK_ROOT + "/{singleworkId}";

    // api/v1/singleworks/{singleworkId}/likes
    public static final String SINGLEWORK_LIKE = SINGLEWORK_DATA + LIKE_DOMAIN;

    // api/v1/singleworks/{singleworkId}/comments
    public static final String SINGLEWORK_COMMENT = SINGLEWORK_DATA + COMMENT_DOMAIN;

    // api/v1/singleworks/{singleworkId}/comments/{commentId}
    public static final String SINGLEWORK_COMMENT_DATA = SINGLEWORK_ROOT + COMMENT_DATA;

    // api/v1/singleworks/me/likes
    public static final String SINGLEWORK_MY_LIKE = SINGLEWORK_ROOT + LIKE_DOMAIN + MY_DATA;

    // api/v1/singleworks/me
    public static final String SINGLEWORK_MY_DATA = SINGLEWORK_ROOT + MY_DATA;


    /*
     * EXHIBITION
     */
    private static final String EXHIBITION_DOMAIN = "/exhibitions";

    // api/v1/exhibitions
    public static final String EXHIBITION_ROOT = VERSION + EXHIBITION_DOMAIN;

    // api/v1/exhibitions/{exhibitionId}
    public static final String EXHIBITION_DATA = EXHIBITION_ROOT + "/{exhibitionId}";

    // api/v1/exhibitions/{exhibitionId}/likes
    public static final String EXHIBITION_LIKE = EXHIBITION_DATA + LIKE_DOMAIN;

    // api/v1/exhibitions/{exhibitionId}/bookmarks
    public static final String EXHIBITION_BOOKMARK = EXHIBITION_DATA + BOOKMARK_DOMAIN;

    // api/v1/exhibitions/{exhibitionId}/comments
    public static final String EXHIBITION_COMMENT = EXHIBITION_DATA + COMMENT_DOMAIN;

    // api/v1/exhibitions/comments/{commentId}
    public static final String EXHIBITION_COMMENT_DATA = EXHIBITION_ROOT + COMMENT_DATA;

    // api/v1/exhibitions/me/likes
    public static final String EXHIBITION_MY_LIKE = EXHIBITION_ROOT + LIKE_DOMAIN + MY_DATA;

    // api/v1/exhibitions/me/bookmarks
    public static final String EXHIBITION_MY_BOOKMARK = EXHIBITION_ROOT + BOOKMARK_DOMAIN + MY_DATA;

    // api/v1/exhibitions/me
    public static final String EXHIBITION_MY_DATA = EXHIBITION_ROOT + MY_DATA;

    // api/v1/exhibitions/chats/connection
    public static final String EXHIBITION_CHAT_CONNECTION = EXHIBITION_ROOT + "/chats/connection";


    /*
     * NOTIFICATION
     */
    private static final String NOTIFICATION_DOMAIN = "/notifications";

    // api/v1/notifications
    public static final String NOTIFICATION_ROOT = VERSION + NOTIFICATION_DOMAIN;

    // api/v1/notifications/{notificationId}
    public static final String NOTIFICATION_DATA = NOTIFICATION_ROOT + "/{notificationId}";
}
