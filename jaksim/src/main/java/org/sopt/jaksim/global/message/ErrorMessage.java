package org.sopt.jaksim.global.message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum ErrorMessage {
    /**
     * 400 Bad Request
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "e4000", "잘못된 요청입니다."),
    INVALID_PLATFORM_TYPE(HttpStatus.BAD_REQUEST, "e4001", "유효하지 않은 플랫폼 타입입니다."),
    INVALID_GRANT_BY_OAUTH(HttpStatus.BAD_REQUEST, "e4002", "유효하지 않은 인가 코드입니다. 이미 회원가입된 사용자입니다."),
    /**
     * 401 Unauthorized
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "e4010", "리소스 접근 권한이 없습니다."),
    JWT_UNAUTHORIZED_EXCEPTION(HttpStatus.UNAUTHORIZED, "e4011", "사용자의 로그인 검증을 실패했습니다."),
    INVALID_ID_TOKEN(HttpStatus.UNAUTHORIZED, "e4013", "해당 유저의 ID 토큰이 유효하지 않습니다."),
    INVALID_ID_TOKEN_IS_NULL(HttpStatus.UNAUTHORIZED, "e4014", "해당 유저의 ID 토큰이 null 입니다."),
    MISMATCH_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "e4015", "리프레시 토큰이 일치하지 않습니다."),

    /**
     * 403 Forbidden
     */
    FORBIDDEN(HttpStatus.FORBIDDEN, "e4030", "리소스 접근 권한이 없습니다."),

    /**
     * 404 Not Found
     */
    NOT_FOUND(HttpStatus.NOT_FOUND, "e4040", "대상을 찾을 수 없습니다."),
    NOT_FOUND_REFRESH_TOKEN_FROM_REDIS(HttpStatus.NOT_FOUND, "e4041", "리프레시 토큰을 찾을 수 없습니다."),

    /**
     * 405 Method Not Allowed
     */

    /**
     * 409 Conflict
     */

    /**
     * 500 Internal Server Error
     */
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


   }
