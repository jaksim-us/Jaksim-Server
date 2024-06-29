package org.sopt.jaksim.user.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.jaksim.user.dto.Tokens;
import org.sopt.jaksim.user.dto.UserInfo;
import org.sopt.jaksim.user.dto.request.UserSignUpRequest;
import org.sopt.jaksim.user.dto.request.UserReissueRequest;
import org.sopt.jaksim.user.dto.request.UserSignInRequest;
import org.sopt.jaksim.user.dto.response.UserSignInResponse;
import org.sopt.jaksim.user.dto.response.UserSignUpResponse;
import org.sopt.jaksim.user.service.UserService;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserFacade {
    private final UserService userService;

    public UserSignUpResponse signup(UserSignUpRequest userSignUpRequest) {
        return userService.signup(userSignUpRequest);
    }

    public UserSignInResponse signin() {
        return userService.signIn();
    }

    public UserSignInResponse reissue(String refreshToken, UserReissueRequest userReissueRequest) {
        return userService.reissue(refreshToken, userReissueRequest);
    }



}
