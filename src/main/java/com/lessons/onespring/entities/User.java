package com.lessons.onespring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.lessons.onespring.constants.Constant.PASSWORD_CHANGE_REQUIRED;

@JsonIgnoreProperties(value={ "password" }, allowSetters= true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="users", uniqueConstraints={
        @UniqueConstraint(columnNames = {"email", "deleted_at"})
})
@Entity
@Where(clause = "deleted_at is null")
@EntityListeners(AuditingEntityListener.class)
public class User extends AuditEntity<String> implements UserDetails
{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled;

    @Column
    private String photo;

    @Column(name = "account_status", nullable = false)
    private Integer accountStatus;

    @Column(nullable = false)
    private String name;

    @Column
    private LocalDate dob;

    @Column(name = "deleted_at")
    private Date deletedAt;

    @JsonManagedReference
    @ManyToMany(cascade= CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name="users_privileges",
            joinColumns={@JoinColumn(name="user_id",
                    referencedColumnName="id")},
            inverseJoinColumns={@JoinColumn(name="privilege_id",
                    referencedColumnName="id")})
    private Set<Privilege> privileges;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getGrantedAuthorities(
                this.getPrivileges().stream().map(Privilege::getName).collect(Collectors.toList())
        );
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return this.email;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return this.enabled;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return this.enabled;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return this.enabled;
    }

    public boolean isPasswordChangeRequired() {
        return this.accountStatus == PASSWORD_CHANGE_REQUIRED;
    }
}
