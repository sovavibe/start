/*
 * (c) Copyright 2025 Digital Technologies and Platforms LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.digtp.start.entity;

// TODO: Replace package name with your package

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * [YourEntity] entity representing a [description].
 *
 * <p>Best Practices:
 * <ul>
 *   <li>@JmixEntity + @Entity for Jmix framework</li>
 *   <li>@JmixGeneratedValue for ID generation</li>
 *   <li>@Version for optimistic locking (REQUIRED)</li>
 *   <li>@InstanceName for display name</li>
 *   <li>@Table with explicit name and indexes</li>
 *   <li>@Column with explicit name, nullable, length</li>
 *   <li>Bean Validation (@NotNull, @Email, @Size) at entity level</li>
 *   <li>@EqualsAndHashCode(onlyExplicitlyIncluded=true) for performance</li>
 *   <li>@ToString(onlyExplicitlyIncluded=true) to exclude sensitive data</li>
 *   <li>List.copyOf() for immutable collections (defensive programming)</li>
 *   <li>String.formatted() for modern Java string formatting</li>
 *   <li>Text blocks for multi-line strings</li>
 * </ul>
 *
 * <p>Reference: See User.java for complete example
 */
@JmixEntity
@Entity
@Table(
        name = "YOUR_ENTITY", // TODO: Replace with your table name
        indexes = {@Index(name = "IDX_YOUR_ENTITY_ON_NAME", columnList = "NAME", unique = true)})
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class YourEntity {

    // TODO: Define constants for field lengths
    private static final int NAME_MAX_LENGTH = 100;
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
    @Size(min = 1, max = NAME_MAX_LENGTH)
    @Column(name = "NAME", nullable = false, length = NAME_MAX_LENGTH)
    private String name;

    @Email(message = "Email address has invalid format: ${validatedValue}")
    @Size(max = DEFAULT_STRING_LENGTH)
    @Column(name = "EMAIL", length = DEFAULT_STRING_LENGTH)
    private String email;

    // TODO: Add more fields as needed
    // Example:
    // @NotNull
    // @Column(name = "ACTIVE", nullable = false)
    // private Boolean active = true;

    // TODO: If you have collections, use List.copyOf() for immutability
    // Example:
    // @Transient
    // private List<String> tags;
    //
    // public List<String> getTags() {
    //     return tags != null ? List.copyOf(tags) : Collections.emptyList();
    // }

    /**
     * Returns display name for the entity.
     *
     * <p>Uses @InstanceName annotation for Jmix framework integration.
     * Uses text blocks and String.formatted() for modern Java.
     *
     * @return formatted display name
     */
    @InstanceName
    public String getDisplayName() {
        final String nameSafe = Objects.requireNonNullElse(name, "");
        return """
            %s [%s]
            """
                .formatted(nameSafe, id != null ? id.toString() : "new")
                .trim();
    }
}

