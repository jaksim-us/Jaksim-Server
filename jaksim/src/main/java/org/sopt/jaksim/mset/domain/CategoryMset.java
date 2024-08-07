package org.sopt.jaksim.mset.domain;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.jaksim.global.common.BaseTimeEntity;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
public class CategoryMset extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long categoryId;
    @Column(nullable = false)
    private Long msetId;

    public static CategoryMset create(Long categoryId, Long msetId) {
        return CategoryMset.builder()
                .categoryId(categoryId)
                .msetId(msetId)
                .build();
    }
}
