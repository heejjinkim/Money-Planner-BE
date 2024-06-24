package com.umc5th.muffler.entity;

import com.umc5th.muffler.entity.base.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Getter
public class Inquiry extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String memberId;
}
