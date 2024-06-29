package org.sopt.jaksim.user.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.jaksim.auth.PrincipalHandler;
import org.sopt.jaksim.auth.UserAuthentication;
import org.sopt.jaksim.auth.jwt.JwtTokenProvider;
import org.sopt.jaksim.global.exception.NotFoundException;
import org.sopt.jaksim.global.exception.OAuthException;
import org.sopt.jaksim.global.exception.UnauthorizedException;
import org.sopt.jaksim.global.message.ErrorMessage;
import org.sopt.jaksim.user.domain.Platform;
import org.sopt.jaksim.user.domain.RefreshToken;
import org.sopt.jaksim.user.dto.Tokens;
import org.sopt.jaksim.user.domain.User;
import org.sopt.jaksim.user.dto.UserInfo;
import org.sopt.jaksim.user.dto.request.UserReissueRequest;
import org.sopt.jaksim.user.dto.request.UserSignInRequest;
import org.sopt.jaksim.user.dto.request.UserSignUpRequest;
import org.sopt.jaksim.user.dto.response.UserSignInResponse;
import org.sopt.jaksim.user.dto.response.UserSignUpResponse;
import org.sopt.jaksim.user.repository.RedisTokenRepository;
import org.sopt.jaksim.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.util.Collections;



@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PrincipalHandler principalHandler;
    private final RedisTokenRepository redisTokenRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}") String CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}") String CLIENT_SECRET;
    @Value("${google.redirect-url}") String REDIRECT_URL;
    @Value("${google.request-url}") String REQUEST_URL;

    @Transactional
    public UserSignUpResponse signup(UserSignUpRequest userSignUpRequest) {
        try {
            Tokens tokens = createTokens(userSignUpRequest.authorizationCode());
            UserInfo userInfo = validateIdToken(tokens.idToken());
            User saveUser = saveUser(userInfo);
            String accessToken = issueAccessTokenByUserId(saveUser.getId());
            String refreshToken = issueRefreshTokenByUserId(saveUser.getId());
            updateRefreshToken(refreshToken, saveUser);
            return UserSignUpResponse.of(accessToken, refreshToken, userInfo);
        } catch (Exception e) {
            if (e.getMessage().contains("invalid_grant")) {
                throw new OAuthException(ErrorMessage.INVALID_GRANT_BY_OAUTH);
            }
        }
        return null;
    }

    @Transactional
    public UserSignInResponse signIn() {
        User user = getUserByPrincipal();

        // atk, rtk 발급
        String accessToken = issueAccessTokenByUserId(user.getId());
        String refreshToken = issueRefreshTokenByUserId(user.getId());

        // redis에 rtk 저장
        redisTokenRepository.save(RefreshToken.of(user.getId(), refreshToken));
        updateRefreshToken(refreshToken, user);
        return UserSignInResponse.of(accessToken, refreshToken, user.getId().toString());
    }

    public Tokens createTokens(String authCode) {
        HttpEntity<MultiValueMap<String, String>> entity = setHeaderAndParameter(authCode);
        Tokens tokens = handleTokenRequest(entity);
        return tokens;
    }

    public UserInfo validateIdToken(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(getHttpTransport(), getJsonFactory())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken validatedIdToken = verifier.verify(idToken);

            if (idToken != null) {
                UserInfo userInfo = parseIdToken(validatedIdToken);
                return userInfo;
            } else {
                throw new UnauthorizedException(ErrorMessage.INVALID_ID_TOKEN_IS_NULL);
            }
        }
        catch (Exception e) {
            throw new UnauthorizedException(ErrorMessage.INVALID_ID_TOKEN);
        }
    }

    public HttpEntity<MultiValueMap<String, String>> setHeaderAndParameter(String authCode) {
        // Header Setting
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // Parameter Setting
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authCode);
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", CLIENT_SECRET);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", REDIRECT_URL);

        return new HttpEntity<>(params, headers);
    }

    public Tokens handleTokenRequest(HttpEntity<?> entity) {
        // RestTemplate Initialize
        RestTemplate rt = new RestTemplate();

        // 전송 및 결과 처리
        ResponseEntity<String> response = rt.exchange(
                REQUEST_URL,
                HttpMethod.POST,
                entity,
                String.class
        );

        String result = response.getBody();
        JSONObject jsonObject = new JSONObject(result);
        String accessToken = jsonObject.getString("access_token");
        String refreshToken = jsonObject.getString("refresh_token");
        String idToken = jsonObject.getString("id_token");
        return Tokens.of(accessToken, refreshToken, idToken);
    }

    private HttpTransport getHttpTransport() {
        return new NetHttpTransport();
    }

    private JsonFactory getJsonFactory() {
        JsonFactory jsonFactory = new JacksonFactory(); //lo 예시로 JacksonFactory 사용
        return jsonFactory;
    }

    private UserInfo parseIdToken (GoogleIdToken validatedIdToken) {
        GoogleIdToken.Payload payload = validatedIdToken.getPayload();

        return UserInfo.of(
                payload.getSubject(),
                payload.getEmail(),
                (String) payload.get("name"),
                (String) payload.get("picture"),
                (String) payload.get("locale"),
                (String) payload.get("family_name"),
                (String) payload.get("given_name")
        );
    }

    public User saveUser(UserInfo userInfo) {
        User user = User.createUser(userInfo.email(), userInfo.name());
        return userRepository.save(user);
    }

    public UserSignInResponse reissue(String token, UserReissueRequest userReissueRequest) {
       Long userId = userReissueRequest.userId();
       validateRefreshToken(token, userId);
       User user = getUser(userId);
       String accessToken = issueAccessTokenByUserId(userId);
       String refreshToken = issueRefreshTokenByUserId(userId);
       updateRefreshToken(refreshToken, user);
       return UserSignInResponse.of(accessToken, refreshToken, userId.toString());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.NOT_FOUND)
        );
    }

    public User getUserByPrincipal() {
        Long userId = principalHandler.getUserIdFromPrincipal();
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.NOT_FOUND)
        );
    }

    private String issueAccessTokenByUserId(Long userId) {
        return jwtTokenProvider.issueAccessToken(UserAuthentication.createUserAuthentication(userId));
    }

    private String issueRefreshTokenByUserId(Long userId) {
        return jwtTokenProvider.issueRefreshToken(UserAuthentication.createUserAuthentication(userId));
    }

    private void validateRefreshToken(String refreshToken, Long userId) {
        // jwt 클래스 따로 만들어서 할 예정
        jwtTokenProvider.validateToken(refreshToken);
        String storedRefreshToken = getRefreshTokenFromRedis(userId).getRefreshToken();
        jwtTokenProvider.equalsRefreshToken(refreshToken,storedRefreshToken);
    }

    private RefreshToken getRefreshTokenFromRedis(Long userId) {
        return redisTokenRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(ErrorMessage.NOT_FOUND_REFRESH_TOKEN_FROM_REDIS)
        );
    }
    private void updateRefreshToken(String refreshToken, User user) {
        user.setRefreshToken(refreshToken);
        redisTokenRepository.save(RefreshToken.of(user.getId(), refreshToken));
    }
}

