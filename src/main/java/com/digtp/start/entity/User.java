package com.digtp.start.entity;

import io.jmix.core.HasTimeZone;
import io.jmix.core.annotation.Secret;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.SystemLevel;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.security.authentication.JmixUserDetails;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;

/**
 * User entity representing an application user.
 *
 * <p>Implements JmixUserDetails for authentication and HasTimeZone for timezone support.
 * Provides user management functionality including username, password, email, and timezone.
 */
@JmixEntity
@Entity
@Table(
        name = "USER_",
        indexes = {@Index(name = "IDX_USER__ON_USERNAME", columnList = "USERNAME", unique = true)})
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User implements JmixUserDetails, HasTimeZone {

    private static final int USERNAME_MAX_LENGTH = 100;
    private static final int DEFAULT_STRING_LENGTH = 255;

    @Id
    @Column(name = "ID")
    @JmixGeneratedValue
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @Version
    @Column(name = "VERSION", nullable = false)
    @EqualsAndHashCode.Include
    private Integer version;

    @NotNull
    @Size(min = 1, max = USERNAME_MAX_LENGTH)
    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Secret
    @SystemLevel
    @Column(name = "PASSWORD")
    private String password;

    @Length(max = DEFAULT_STRING_LENGTH)
    @Column(name = "FIRST_NAME", length = DEFAULT_STRING_LENGTH)
    private String firstName;

    @Length(max = DEFAULT_STRING_LENGTH)
    @Column(name = "LAST_NAME", length = DEFAULT_STRING_LENGTH)
    private String lastName;

    @Email(message = "Email address has invalid format: ${validatedValue}")
    @Length(max = DEFAULT_STRING_LENGTH)
    @Column(name = "EMAIL", length = DEFAULT_STRING_LENGTH)
    private String email;

    @NotNull
    @Column(name = "ACTIVE", nullable = false)
    private Boolean active = true;

    @Length(max = DEFAULT_STRING_LENGTH)
    @Column(name = "TIME_ZONE_ID", length = DEFAULT_STRING_LENGTH)
    private String timeZoneId;

    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities != null ? authorities : Collections.emptyList();
    }

    @Override
    public void setAuthorities(final Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(active);
    }

    @InstanceName
    @DependsOnProperties({"firstName", "lastName", "username"})
    public String getDisplayName() {
        final String firstNameSafe = Objects.requireNonNullElse(firstName, "");
        final String lastNameSafe = Objects.requireNonNullElse(lastName, "");
        return """
                %s %s [%s]
                """
                .formatted(firstNameSafe, lastNameSafe, username)
                .trim();
    }

    @Override
    public String getTimeZoneId() {
        return timeZoneId;
    }

    @Override
    public boolean isAutoTimeZone() {
        return true;
    }
}
