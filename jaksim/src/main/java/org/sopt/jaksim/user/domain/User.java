package org.sopt.jaksim.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.jaksim.global.common.BaseTimeEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@Setter
@Table(name = "users")
@Entity
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    private Platform platform;
    private String refreshToken;

    public static User createUser(String email, String name) {
        return User.builder()
                .email(email)
                .name(name)
                .platform(Platform.GOOGLE)
                .build();
    }
}
