/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.security;

import io.jmix.security.model.SecurityScope;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.security.role.annotation.SpecificPolicy;
import io.jmix.securityflowui.role.UiMinimalPolicies;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

/**
 * Minimal UI access security role.
 *
 * <p>Provides minimal access to UI components: MainView and LoginView.
 * This role is typically used for anonymous or restricted users.
 */
@ResourceRole(name = "UI: minimal access", code = UiMinimalRole.CODE, scope = SecurityScope.UI)
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.CommentRequired
// PMD.MissingSerialVersionUID: Interfaces don't need serialVersionUID (PMD recognizes this)
public interface UiMinimalRole extends UiMinimalPolicies {

    String CODE = "ui-minimal";

    @ViewPolicy(viewIds = "MainView")
    void main();

    @ViewPolicy(viewIds = "LoginView")
    @SpecificPolicy(resources = "ui.loginToUi")
    void login();
}
