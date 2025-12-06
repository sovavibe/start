/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.view.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.digtp.start.StartApplication;
import com.digtp.start.testsupport.AbstractIntegrationTest;
import com.digtp.start.testsupport.AuthenticatedAsAdmin;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.testassist.FlowuiTestAssistConfiguration;
import io.jmix.flowui.testassist.UiTest;
import io.jmix.flowui.testassist.UiTestUtils;
import io.jmix.flowui.view.View;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@UiTest
@SpringBootTest(classes = {StartApplication.class, FlowuiTestAssistConfiguration.class})
@ActiveProfiles("test")
@ExtendWith(AuthenticatedAsAdmin.class)
// Test: Test methods may have similar structure but test different scenarios
@SuppressWarnings({
    // Test: Some tests are clearer as separate methods rather than parameterized
    "java:S5976", // parameterized test
    // Test: Multiple assertions on same object are acceptable in tests for clarity
    "java:S5853", // multiple assertions
    // Test: Test methods may have similar structure but test different scenarios
    "java:S4144" // similar methods
})
class UserListViewTest extends AbstractIntegrationTest {

    @Autowired
    private ViewNavigators viewNavigators;

    @Test
    void testUserListViewInit() {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();

        final View<?> view = getCurrentViewAsView();

        assertThat(view).isNotNull();
    }
}
