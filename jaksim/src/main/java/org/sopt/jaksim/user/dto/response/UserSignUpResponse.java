package org.sopt.jaksim.user.dto.response;

import org.sopt.jaksim.user.dto.UserInfo;

public record UserSignUpResponse(
        String accessToken,
        String refreshToken,
        UserInfo userInfo
) {
    public static UserSignUpResponse of (String accessToken, String refreshToken, UserInfo userInfo) {
        return new UserSignUpResponse(accessToken, refreshToken, userInfo);
    }
}
