package org.sopt.jaksim.user.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/login")
public class SocialLoginTempController {

    // https://accounts.google.com/o/oauth2/v2/auth?scope=email%20profile&access_type=offline&include_granted_scopes=true&response_type=code&state=state_parameter_passthrough_value&redirect_uri=http://localhost:8080/login/oauth2/code/google&client_id=659770443420-vbmil1na1ls85peb64g8krccn7ulmvu2.apps.googleusercontent.com
    // Goolge이 Redirect로 보내는 URL, Authorization code 반환
    // 클라에서 해줄 작업
    @GetMapping("/oauth2/code/google")
    public void redirectByGoogle(@RequestParam("code") String code) throws GeneralSecurityException, IOException {
        log.info("authorization code : " + code);
    }
}
