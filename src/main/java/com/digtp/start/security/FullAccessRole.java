/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.security;

import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.security.role.annotation.SpecificPolicy;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

/**
 * Full access security role.
 *
 * <p>Provides unrestricted access to all entities, views, menus, and resources.
 * This role should be assigned only to system administrators.
 */
@ResourceRole(name = "Full Access", code = FullAccessRole.CODE)
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.CommentRequired
// PMD.MissingSerialVersionUID: Interfaces don't need serialVersionUID (PMD recognizes this)
public interface FullAccessRole {

    String CODE = "system-full-access";

    @EntityPolicy(entityName = "*", actions = EntityPolicyAction.ALL)
    @EntityAttributePolicy(entityName = "*", attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @ViewPolicy(viewIds = "*")
    @MenuPolicy(menuIds = "*")
    @SpecificPolicy(resources = "*")
    void fullAccess();
}
