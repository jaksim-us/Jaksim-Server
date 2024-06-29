package org.sopt.jaksim.user.dto;

public record Tokens (
    String accessToken,
    String refreshToken,
    String idToken
) {
    public static Tokens of(String accessToken, String refreshToken, String idToken) {
        return new Tokens(accessToken, refreshToken, idToken);
    }
}
