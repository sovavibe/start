package com.digtp.start.view.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.digtp.start.StartApplication;
import com.digtp.start.test_support.AbstractIntegrationTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.component.loginform.JmixLoginForm;
import io.jmix.flowui.testassist.FlowuiTestAssistConfiguration;
import io.jmix.flowui.testassist.UiTest;
import io.jmix.flowui.testassist.UiTestUtils;
import java.lang.reflect.Method;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@UiTest
@SpringBootTest(classes = {StartApplication.class, FlowuiTestAssistConfiguration.class})
@ActiveProfiles("test")
class LoginViewTest extends AbstractIntegrationTest {

    @Autowired
    ViewNavigators viewNavigators;

    @Test
    void testLoginViewInit() {
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();

        final LoginView loginView = UiTestUtils.getCurrentView();
        assertThat(loginView).isNotNull();
    }

    @Test
    void testLocaleChange() throws ReflectiveOperationException {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        final LoginView loginView = UiTestUtils.getCurrentView();
        final LocaleChangeEvent event = org.mockito.Mockito.mock(LocaleChangeEvent.class);
        when(event.getLocale()).thenReturn(Locale.FRENCH);

        try (MockedStatic<UI> uiMock = mockStatic(UI.class)) {
            final UI ui = org.mockito.Mockito.mock(UI.class);
            final Page page = org.mockito.Mockito.mock(Page.class);
            uiMock.when(UI::getCurrent).thenReturn(ui);
            when(ui.getPage()).thenReturn(page);

            // Act
            loginView.localeChange(event);

            // Assert - method should complete without errors
            assertThat(loginView).isNotNull();
        }
    }

    @Test
    void testInitDefaultCredentialsWithEmptyValues() throws ReflectiveOperationException {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        final LoginView loginView = UiTestUtils.getCurrentView();
        final JmixLoginForm loginForm = UiTestUtils.getComponent(loginView, "login");

        // Act - invoke initDefaultCredentials through reflection
        // This tests the branches when defaultUsername/defaultPassword are empty
        final Method method = LoginView.class.getDeclaredMethod("initDefaultCredentials");
        method.setAccessible(true);
        method.invoke(loginView);

        // Assert - method should complete without errors
        assertThat(loginForm).isNotNull();
    }

    @Test
    void testGetLoginFailureReasonUnknownError() throws ReflectiveOperationException {
        // Arrange
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();
        final LoginView loginView = UiTestUtils.getCurrentView();
        final RuntimeException unknownException = new RuntimeException("Unknown error");

        // Act - invoke getLoginFailureReason through reflection
        final Method method = LoginView.class.getDeclaredMethod("getLoginFailureReason", Exception.class);
        method.setAccessible(true);
        final String reason = (String) method.invoke(loginView, unknownException);

        // Assert - should return "unknown error" for default case
        assertThat(reason).isEqualTo("unknown error");
    }
}
