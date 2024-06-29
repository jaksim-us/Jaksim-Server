package org.sopt.jaksim.user.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.jaksim.global.common.BaseResponse;
import org.sopt.jaksim.user.dto.request.UserSignInRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import static org.sopt.jaksim.global.common.Constants.AUTHORIZATION;

@Tag(name = "회원 관련 API")
public interface UserApi {
    @Operation(
            summary = "로그인 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "로그인이 완료되었습니다."),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청입니다.",
                            content = @Content),
                    @ApiResponse(
                            responseCode = "400",
                            description = "유효하지 않은 플랫폼 타입입니다.",
                            content = @Content),
                    @ApiResponse(
                            responseCode = "400",
                            description = "유효하지 않은 인가 코드입니다. 이미 회원가입된 사용자입니다.",
                            content = @Content),
                    @ApiResponse(
                            responseCode = "401",
                            description = " 사용자의 로그인 검증을 실패했습니다.",
                            content = @Content),
                    @ApiResponse(
                            responseCode = "401",
                            description = "해당 유저의 ID 토큰이 유효하지 않습니다.",
                            content = @Content),
                    @ApiResponse(
                            responseCode = "401",
                            description = "해당 유저의 ID 토큰이 null 입니다.",
                            content = @Content),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 회원입니다.",
                            content = @Content),
                    @ApiResponse(
                            responseCode = "405",
                            description = "잘못된 HTTP method 요청입니다.",
                            content = @Content),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 내부 오류입니다.",
                            content = @Content)})
    @PostMapping("/user/signin")
    public ResponseEntity<BaseResponse<?>> signin(@RequestHeader(AUTHORIZATION) final String accessToken);

}
