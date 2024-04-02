package com.umc5th.muffler.entity;

import static com.umc5th.muffler.entity.constant.Status.ACTIVE;

import com.umc5th.muffler.entity.base.BaseTimeEntity;
import com.umc5th.muffler.entity.constant.Role;
import com.umc5th.muffler.entity.constant.SocialType;
import com.umc5th.muffler.entity.constant.Status;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
@Entity
@Getter
@Table(name = "members")
public class Member extends BaseTimeEntity implements Persistable<String>, UserDetails {

    @Id
    private String id;

    @Column(length = 20)
    private String name;

    private String profileImg;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    private Role role; // authority는 하나만 가능 (ex. "USER,ADMIN"처럼 2개 불가능)

    @Column
    private String refreshToken;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = ACTIVE;

    @Builder.Default
    @OneToMany(mappedBy = "member")
    private List<Goal> goals = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private MemberAlarm memberAlarm;

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setNameAndProfile(String name, String profileImg) {
        this.name = name;
        this.profileImg = profileImg;
    }

    public void addCategory(Category category) {
        category.setMember(this);
        this.categories.add(category);
    }
    public void setMemberAlarm(MemberAlarm memberAlarm) {
        this.memberAlarm = memberAlarm;
    }
    public void enrollToken(String token) {
        memberAlarm.enrollToken(token);
    }
    public void deleteToken() {memberAlarm.deleteToken();}

    @Override
    public boolean isNew() {
        return getCreatedAt() == null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return status.isActive();
    }

    @Override
    public boolean isAccountNonLocked() {
        return status.isActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return status.isActive();
    }

    @Override
    public boolean isEnabled() {
        return status.isActive();
    }
}
