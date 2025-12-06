/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.view.user;

import com.digtp.start.entity.User;
import com.digtp.start.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.DialogMode;
import io.jmix.flowui.view.LookupComponent;
import io.jmix.flowui.view.StandardListView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import lombok.extern.slf4j.Slf4j;

/**
 * List view for displaying and managing User entities.
 *
 * <p>Provides a data grid showing all users in the system with standard
 * list operations (create, edit, delete). Uses Jmix StandardListView
 * which provides automatic data loading and standard actions.
 *
 * <p><b>Performance Note:</b> When adding relationships to the User entity
 * (e.g., roles, departments), consider using FetchPlan in the data loader
 * to avoid N+1 query problems. Example:
 * <pre>{@code
 * @ViewComponent
 * private CollectionLoader<User> usersDl;
 *
 * @Subscribe
 * public void onInit(final InitEvent event) {
 *     FetchPlan fetchPlan = fetchPlans.builder(User.class)
 *         .add("roles")  // Eager load relationships
 *         .build();
 *     usersDl.setFetchPlan(fetchPlan);
 * }
 * }</pre>
 */
@Route(value = "users", layout = MainView.class)
@ViewController(id = "User.list")
@ViewDescriptor(path = "user-list-view.xml")
@LookupComponent("usersDataGrid")
@DialogMode(width = "64em")
@Slf4j
// Framework: Jmix views extend multiple framework classes (StandardListView, etc.)
@SuppressWarnings({
    // Framework: Jmix views contain framework-managed non-serializable beans (MessageBundle, UI components)
    "java:S1948", // non-serializable field
    // Framework: Jmix views extend multiple framework classes (StandardListView, etc.)
    "java:S110", // too many parents
    // Framework: Jmix lifecycle methods may have same names as parent methods
    "java:S2177", // method name conflict
    // Framework: Jmix views extend StandardListView which requires design for extension
    "java:S2150", // design for extension
    // Framework: Jmix lifecycle methods (onInit, etc.) don't need JavaDoc
    "java:S1186", // missing javadoc
    // Framework: @ViewComponent is Vaadin/Jmix mechanism for UI component injection from XML (not Spring field
    // injection)
    "java:S6813", // field injection
    // Framework: Error Prone StrictUnusedVariable requires underscore prefix for unused variables
    "java:S117", // unused variable
    // Framework: Jmix View contains framework-managed non-serializable beans (MessageBundle, UI components)
    "PMD.NonSerializableClass" // non-serializable class
})
public class UserListView extends StandardListView<User> {

    private static final long serialVersionUID = 1L;

    @Subscribe
    // Framework: Jmix lifecycle methods require InitEvent parameter signature
    @SuppressWarnings("java:S1172") // unused parameter
    public void onInit(final InitEvent _event) {
        log.debug("User list view initialized");
    }
}
