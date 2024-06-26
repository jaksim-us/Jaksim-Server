package org.sopt.jaksim.user.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24 * 1000L * 14)
public class RefreshToken {
    @Id
    private Long id;
    @Indexed
    private String refreshToken;

    public static RefreshToken of(Long userId, String refreshToken) {
        return RefreshToken.builder()
                .id(userId)
                .refreshToken(refreshToken)
                .build();
    }
}