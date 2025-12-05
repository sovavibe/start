/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.user;

import com.digtp.start.StartApplication;
import com.digtp.start.entity.User;
import com.digtp.start.testsupport.AbstractIntegrationTest;
import com.digtp.start.view.user.UserListView;
import io.jmix.core.DataManager;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.component.textfield.JmixPasswordField;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.data.grid.DataGridItems;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.testassist.FlowuiTestAssistConfiguration;
import io.jmix.flowui.testassist.UiTest;
import io.jmix.flowui.testassist.UiTestUtils;
import io.jmix.flowui.view.View;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Sample UI integration test for the User entity.
 */
@UiTest
@SpringBootTest(classes = {StartApplication.class, FlowuiTestAssistConfiguration.class})
@ActiveProfiles("test")
class UserUiTest extends AbstractIntegrationTest {

    @Autowired
    DataManager dataManager;

    @Autowired
    ViewNavigators viewNavigators;

    @Test
    void testCreateUser() {
        // Navigate to user list view
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();

        final View<?> userListView = getCurrentViewAsView();

        // click "Create" button
        final JmixButton createBtn = UiTestUtils.getComponent(userListView, "createButton");
        createBtn.click();

        // Get detail view
        final View<?> userDetailView = getCurrentViewAsView();

        // Set username and password in the fields
        final TypedTextField<String> usernameField = UiTestUtils.getComponent(userDetailView, "usernameField");
        final String username = "test-user-" + System.currentTimeMillis();
        usernameField.setValue(username);

        final JmixPasswordField passwordField = UiTestUtils.getComponent(userDetailView, "passwordField");
        passwordField.setValue("test-passwd");

        final JmixPasswordField confirmPasswordField = UiTestUtils.getComponent(userDetailView, "confirmPasswordField");
        confirmPasswordField.setValue("test-passwd");

        // Click "OK"
        final JmixButton commitAndCloseBtn = UiTestUtils.getComponent(userDetailView, "saveAndCloseButton");
        commitAndCloseBtn.click();

        // Get navigated user list view
        final View<?> userListViewAfterSave = getCurrentViewAsView();

        // Check the created user is shown in the table
        final DataGrid<User> usersDataGrid = UiTestUtils.getComponent(userListViewAfterSave, "usersDataGrid");

        final DataGridItems<User> usersDataGridItems = usersDataGrid.getItems();
        Assertions.assertNotNull(usersDataGridItems);

        final User foundUser = usersDataGridItems.getItems().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElseThrow();
        Assertions.assertNotNull(foundUser);
    }

    @AfterEach
    void afterEach() {
        dataManager
                .load(User.class)
                .query("e.username like ?1", "test-user-%")
                .list()
                .forEach(dataManager::remove);
    }
}
