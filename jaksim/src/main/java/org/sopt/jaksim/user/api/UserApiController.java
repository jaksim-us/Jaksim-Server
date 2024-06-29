package org.sopt.jaksim.user.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.jaksim.global.common.ApiResponseUtil;
import org.sopt.jaksim.global.common.BaseResponse;
import org.sopt.jaksim.global.message.SuccessMessage;
import org.sopt.jaksim.user.dto.request.UserSignUpRequest;
import org.sopt.jaksim.user.dto.request.UserReissueRequest;
import org.sopt.jaksim.user.dto.request.UserSignInRequest;
import org.sopt.jaksim.user.dto.response.UserSignInResponse;
import org.sopt.jaksim.user.dto.response.UserSignUpResponse;
import org.sopt.jaksim.user.facade.UserFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.sopt.jaksim.global.common.Constants.AUTHORIZATION;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserApiController implements UserApi{

    private final UserFacade userFacade;

    @PostMapping("/auth/google/callback")
    public ResponseEntity<BaseResponse<?>> signup(@RequestBody final UserSignUpRequest userSignUpRequest) {
        UserSignUpResponse response = userFacade.signup(userSignUpRequest);
        return ApiResponseUtil.success(SuccessMessage.USER_SIGN_UP_SUCCESS, response);
    }

    @Override
    @PostMapping("/user/signin")
    public ResponseEntity<BaseResponse<?>> signin(@RequestHeader(AUTHORIZATION) final String accessToken) {
        final UserSignInResponse response = userFacade.signin();
        return ApiResponseUtil.success(SuccessMessage.USER_SIGN_IN_SUCCESS, response);
    }
//

    @PostMapping("/reissue")
    public ResponseEntity<BaseResponse<?>> reissue(@RequestHeader(AUTHORIZATION) final String refreshToken,
                                                      @RequestBody final UserReissueRequest userReissueRequest) {
        UserSignInResponse response = userFacade.reissue(refreshToken, userReissueRequest);
        return ApiResponseUtil.success(SuccessMessage.USER_TOKEN_REISSUE_SUCCESS, response);
    }

}






