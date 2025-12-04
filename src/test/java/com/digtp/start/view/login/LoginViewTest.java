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
package com.digtp.start.view.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.digtp.start.StartApplication;
import com.digtp.start.testsupport.AbstractIntegrationTest;
import com.digtp.start.testsupport.AuthenticatedAsAdmin;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.component.loginform.JmixLoginForm;
import io.jmix.flowui.testassist.FlowuiTestAssistConfiguration;
import io.jmix.flowui.testassist.UiTest;
import io.jmix.flowui.testassist.UiTestUtils;
import io.jmix.flowui.view.View;
import java.lang.reflect.Method;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@UiTest
@SpringBootTest(classes = {StartApplication.class, FlowuiTestAssistConfiguration.class})
@ActiveProfiles("test")
@ExtendWith(AuthenticatedAsAdmin.class)
// Framework patterns suppressed via @SuppressWarnings (Palantir Baseline defaults):
// - PMD.CommentSize, PMD.CommentRequired, PMD.CommentDefaultAccessModifier, PMD.AtLeastOneConstructor
// - PMD.LongVariable, PMD.UnitTestContainsTooManyAsserts, PMD.UnitTestAssertionsShouldIncludeMessage
// - PMD.LawOfDemeter
@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
class LoginViewTest extends AbstractIntegrationTest {

    @Autowired
    ViewNavigators viewNavigators;

    @Test
    void testLoginViewInit() {
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();

        // Use Object to avoid ClassCastException with Jmix class loaders
        final Object currentView = UiTestUtils.getCurrentView();
        assertThat(currentView).isNotNull();
        assertThat(currentView.getClass().getSimpleName()).isEqualTo("LoginView");
    }

    @Test
    // PMD.ShortVariable suppressed via @SuppressWarnings (Palantir Baseline defaults)
    void testLocaleChange() throws ReflectiveOperationException {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        // Use View<?> to avoid ClassCastException with Jmix class loaders
        final View<?> loginView = getCurrentViewAsView();
        final LocaleChangeEvent event = mock(LocaleChangeEvent.class);
        when(event.getLocale()).thenReturn(Locale.FRENCH);

        try (MockedStatic<UI> uiMock = mockStatic(UI.class)) {
            final UI ui = mock(UI.class);
            final Page page = mock(Page.class);
            uiMock.when(UI::getCurrent).thenReturn(ui);
            when(ui.getPage()).thenReturn(page);

            // Act - use reflection to call localeChange method
            final Method method = loginView.getClass().getMethod("localeChange", LocaleChangeEvent.class);
            method.invoke(loginView, event);

            // Assert - method should complete without errors
            assertThat(loginView).isNotNull();
        }
    }

    @Test
    // PMD.AvoidAccessibilityAlteration suppressed via @SuppressWarnings (Baseline defaults)
    void testInitDefaultCredentialsWithEmptyValues() throws ReflectiveOperationException {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        // Use View<?> to avoid ClassCastException with Jmix class loaders
        final View<?> loginView = getCurrentViewAsView();
        final JmixLoginForm loginForm = UiTestUtils.getComponent(loginView, "login");

        // Act - invoke initDefaultCredentials through reflection
        // This tests the branches when defaultUsername/defaultPassword are empty
        final Method method = loginView.getClass().getDeclaredMethod("initDefaultCredentials");
        method.setAccessible(true);
        method.invoke(loginView);

        // Assert - method should complete without errors
        assertThat(loginForm).isNotNull();
    }

    @Test
    // PMD.AvoidAccessibilityAlteration suppressed via @SuppressWarnings (Baseline defaults)
    void testGetLoginFailureReasonUnknownError() throws ReflectiveOperationException {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        // Use View<?> to avoid ClassCastException with Jmix class loaders
        final View<?> loginView = getCurrentViewAsView();
        final RuntimeException unknownException = new RuntimeException("Unknown error");

        // Act - invoke getLoginFailureReason through reflection
        final Method method = loginView.getClass().getDeclaredMethod("getLoginFailureReason", Exception.class);
        method.setAccessible(true);
        final String reason = (String) method.invoke(loginView, unknownException);

        // Assert - should return "unknown error" for default case
        assertThat(reason).isEqualTo("unknown error");
    }
}
