package com.digtp.start.view.main;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.digtp.start.entity.User;
import com.digtp.start.test_support.AuthenticatedAsAdmin;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import io.jmix.core.Messages;
import io.jmix.core.usersubstitution.CurrentUserSubstitution;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.testassist.FlowuiTestAssistConfiguration;
import io.jmix.flowui.testassist.UiTest;
import io.jmix.flowui.testassist.UiTestUtils;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@UiTest
@SpringBootTest(classes = {com.digtp.start.StartApplication.class, FlowuiTestAssistConfiguration.class})
@ExtendWith({SpringExtension.class, AuthenticatedAsAdmin.class})
class MainViewTest {

    @Autowired
    private ViewNavigators viewNavigators;

    @MockBean
    private Messages messages;

    @MockBean
    private CurrentUserSubstitution currentUserSubstitution;

    @Test
    void testGenerateUserNameWithFirstNameAndLastName() throws Exception {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final User user = createTestUser("john.doe", "John", "Doe");

        final String userName = invokePrivateMethod(mainView, "generateUserName", User.class, user);

        assertThat(userName).isEqualTo("John Doe");
    }

    @Test
    void testGenerateUserNameWithOnlyFirstName() throws Exception {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final User user = createTestUser("john.doe", "John", null);

        final String userName = invokePrivateMethod(mainView, "generateUserName", User.class, user);

        assertThat(userName).isEqualTo("John");
    }

    @Test
    void testGenerateUserNameWithOnlyLastName() throws Exception {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final User user = createTestUser("john.doe", null, "Doe");

        final String userName = invokePrivateMethod(mainView, "generateUserName", User.class, user);

        assertThat(userName).isEqualTo("Doe");
    }

    @Test
    void testGenerateUserNameWithoutNames() throws Exception {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final User user = createTestUser("john.doe", null, null);

        final String userName = invokePrivateMethod(mainView, "generateUserName", User.class, user);

        assertThat(userName).isEqualTo("john.doe");
    }

    @Test
    void testIsSubstitutedWhenUserIsSubstituted() throws Exception {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final User user = createTestUser("john.doe", "John", "Doe");
        final User authenticatedUser = createTestUser("admin", "Admin", "User");

        when(currentUserSubstitution.getAuthenticatedUser()).thenReturn(authenticatedUser);

        final boolean isSubstituted = invokePrivateMethod(mainView, "isSubstituted", User.class, user);

        assertThat(isSubstituted).isTrue();
    }

    @Test
    void testIsSubstitutedWhenUserIsNotSubstituted() throws Exception {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final User user = createTestUser("john.doe", "John", "Doe");

        when(currentUserSubstitution.getAuthenticatedUser()).thenReturn(user);

        final boolean isSubstituted = invokePrivateMethod(mainView, "isSubstituted", User.class, user);

        assertThat(isSubstituted).isFalse();
    }

    @Test
    void testIsSubstitutedWhenUserIsNull() throws Exception {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final User authenticatedUser = createTestUser("admin", "Admin", "User");

        when(currentUserSubstitution.getAuthenticatedUser()).thenReturn(authenticatedUser);

        final boolean isSubstituted = invokePrivateMethod(mainView, "isSubstituted", User.class, (User) null);

        assertThat(isSubstituted).isFalse();
    }

    @Test
    void testCreateAvatar() throws Exception {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();

        final Avatar avatar = invokePrivateMethod(mainView, "createAvatar", String.class, "John Doe");

        assertThat(avatar).isNotNull();
        assertThat(avatar.getName()).isEqualTo("John Doe");
    }

    @Test
    void testUserMenuButtonRendererWithUser() throws Exception {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final User user = createTestUser("john.doe", "John", "Doe");
        final User authenticatedUser = createTestUser("admin", "Admin", "User");

        when(currentUserSubstitution.getAuthenticatedUser()).thenReturn(authenticatedUser);

        final Component component = invokePrivateMethod(mainView, "userMenuButtonRenderer", UserDetails.class, user);

        assertThat(component).isNotNull();
        assertThat(component).isInstanceOf(Div.class);
    }

    @Test
    void testUserMenuButtonRendererWithNonUser() throws Exception {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final UserDetails nonUser = mock(UserDetails.class);

        final Component component = invokePrivateMethod(mainView, "userMenuButtonRenderer", UserDetails.class, nonUser);

        assertThat(component).isNull();
    }

    @Test
    void testUserMenuHeaderRendererWithUser() throws Exception {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final User user = createTestUser("john.doe", "John", "Doe");
        final User authenticatedUser = createTestUser("admin", "Admin", "User");

        when(currentUserSubstitution.getAuthenticatedUser()).thenReturn(authenticatedUser);

        final Component component = invokePrivateMethod(mainView, "userMenuHeaderRenderer", UserDetails.class, user);

        assertThat(component).isNotNull();
        assertThat(component).isInstanceOf(Div.class);
    }

    @Test
    void testUserMenuHeaderRendererWhenNameEqualsUsername() throws Exception {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final User user = createTestUser("john.doe", null, null);

        when(currentUserSubstitution.getAuthenticatedUser()).thenReturn(user);

        final Component component = invokePrivateMethod(mainView, "userMenuHeaderRenderer", UserDetails.class, user);

        assertThat(component).isNotNull();
    }

    @SuppressWarnings("unchecked")
    private <T> T invokePrivateMethod(
            final Object target, final String methodName, final Class<?> paramType, final Object arg) throws Exception {
        final Method method = target.getClass().getDeclaredMethod(methodName, paramType);
        method.setAccessible(true);
        return (T) method.invoke(target, arg);
    }

    private User createTestUser(final String username, final String firstName, final String lastName) {
        final User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }
}
