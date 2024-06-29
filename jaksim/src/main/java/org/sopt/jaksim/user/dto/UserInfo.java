package org.sopt.jaksim.user.dto;

public record UserInfo(
        String userId,
        String email,
        String name,
        String pictureUrl,
        String locale,
        String familyName,
        String givenName
) {
    public static UserInfo of (String userId, String email, String name, String pictureUrl, String locale, String familyName, String givenName) {
        return new UserInfo(userId, email, name, pictureUrl, locale, familyName, givenName);
    }
}
