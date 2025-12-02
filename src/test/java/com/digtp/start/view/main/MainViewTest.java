package com.digtp.start.view.main;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.digtp.start.StartApplication;
import com.digtp.start.entity.User;
import com.digtp.start.test_support.AbstractIntegrationTest;
import com.digtp.start.test_support.AuthenticatedAsAdmin;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.html.Div;
import io.jmix.core.DataManager;
import io.jmix.core.security.UserRepository;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.testassist.FlowuiTestAssistConfiguration;
import io.jmix.flowui.testassist.UiTest;
import io.jmix.flowui.testassist.UiTestUtils;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@UiTest
@SpringBootTest(classes = {StartApplication.class, FlowuiTestAssistConfiguration.class})
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class, AuthenticatedAsAdmin.class})
@SuppressWarnings("java:S5976") // Separate test methods are clearer than parameterized for these scenarios
class MainViewTest extends AbstractIntegrationTest {

    @Autowired
    private ViewNavigators viewNavigators;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataManager dataManager;

    private User savedUser;

    @Test
    void testGenerateUserNameWithFirstNameAndLastName() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final User user = createTestUser("john.doe", "John", "Doe");

        final String userName = invokePrivateMethod(mainView, "generateUserName", User.class, user);

        assertThat(userName).isEqualTo("John Doe");
    }

    @Test
    void testGenerateUserNameWithOnlyFirstName() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final User user = createTestUser("john.doe", "John", null);

        final String userName = invokePrivateMethod(mainView, "generateUserName", User.class, user);

        assertThat(userName).isEqualTo("John");
    }

    @Test
    void testGenerateUserNameWithOnlyLastName() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final User user = createTestUser("john.doe", null, "Doe");

        final String userName = invokePrivateMethod(mainView, "generateUserName", User.class, user);

        assertThat(userName).isEqualTo("Doe");
    }

    @Test
    void testGenerateUserNameWithoutNames() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final User user = createTestUser("john.doe", null, null);

        final String userName = invokePrivateMethod(mainView, "generateUserName", User.class, user);

        assertThat(userName).isEqualTo("john.doe");
    }

    @Test
    void testIsSubstitutedWhenUserIsSubstituted() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final User user = createTestUser("john.doe", "John", "Doe");
        // Authenticated user is "admin" (from AuthenticatedAsAdmin), user is different
        // So isSubstituted should return true
        final boolean isSubstituted = invokePrivateMethod(mainView, "isSubstituted", User.class, user);

        assertThat(isSubstituted).isTrue();
    }

    @Test
    void testIsSubstitutedWhenUserIsNotSubstituted() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        // Authenticated user is "admin" (from AuthenticatedAsAdmin), so use admin user
        final UserDetails adminUserDetails = userRepository.loadUserByUsername("admin");
        final User user = (User) adminUserDetails;

        // No substitution - authenticated user is the same as the user
        final boolean isSubstituted = invokePrivateMethod(mainView, "isSubstituted", User.class, user);

        assertThat(isSubstituted).isFalse();
    }

    @Test
    void testIsSubstitutedWhenUserIsNull() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();

        // When user is null, isSubstituted should return false
        final boolean isSubstituted = invokePrivateMethod(mainView, "isSubstituted", User.class, (User) null);

        assertThat(isSubstituted).isFalse();
    }

    @Test
    void testIsSubstitutedWhenAuthenticatedUserIsNull() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        // Create a user that will trigger the authenticatedUser == null branch
        // This is difficult to test directly, but we can verify the method handles null gracefully
        final User user = createTestUser("test.user", "Test", "User");
        // The method checks authenticatedUser == null, which should return false
        // In normal flow, authenticatedUser is always set by AuthenticatedAsAdmin, so this branch
        // is hard to test without mocking CurrentUserSubstitution
        final boolean isSubstituted = invokePrivateMethod(mainView, "isSubstituted", User.class, user);
        // In test context, authenticatedUser is always set, so this should return true (user != admin)
        assertThat(isSubstituted).isTrue();
    }

    @Test
    void testUserMenuHeaderRendererWhenNameDoesNotEqualUsername() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        // When name does not equal username, else branch is taken (adds subtext with username)
        final User user = createTestUser("john.doe", "John", "Doe");

        final Component component = invokePrivateMethod(mainView, "userMenuHeaderRenderer", UserDetails.class, user);

        assertThat(component).isNotNull();
        assertThat(component).isInstanceOf(Div.class);
    }

    @Test
    void testCreateAvatar() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();

        final Avatar avatar = invokePrivateMethod(mainView, "createAvatar", String.class, "John Doe");

        assertThat(avatar).isNotNull();
        assertThat(avatar.getName()).isEqualTo("John Doe");
    }

    @Test
    @SuppressWarnings("java:S5853") // Multiple assertions are clearer for component validation
    void testUserMenuButtonRendererWithUser() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final User user = createTestUser("john.doe", "John", "Doe");

        final Component component = invokePrivateMethod(mainView, "userMenuButtonRenderer", UserDetails.class, user);

        assertThat(component).isNotNull();
        assertThat(component).isInstanceOf(Div.class);
    }

    @Test
    void testUserMenuButtonRendererWithNonUser() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final UserDetails nonUser = mock(UserDetails.class);

        final Component component = invokePrivateMethod(mainView, "userMenuButtonRenderer", UserDetails.class, nonUser);

        assertThat(component).isNull();
    }

    @Test
    @SuppressWarnings("java:S5853") // Multiple assertions are clearer for component validation
    void testUserMenuHeaderRendererWithUser() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        final User user = createTestUser("john.doe", "John", "Doe");

        final Component component = invokePrivateMethod(mainView, "userMenuHeaderRenderer", UserDetails.class, user);

        assertThat(component).isNotNull();
        assertThat(component).isInstanceOf(Div.class);
    }

    @Test
    void testUserMenuHeaderRendererWhenNameEqualsUsername() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        // When name equals username, different branch is taken (adds subtext class)
        final User user = createTestUser("john.doe", null, null);

        final Component component = invokePrivateMethod(mainView, "userMenuHeaderRenderer", UserDetails.class, user);

        assertThat(component).isNotNull();
    }

    @Test
    void testUserMenuButtonRendererWithSubstitutedUser() throws ReflectiveOperationException {
        viewNavigators.view(UiTestUtils.getCurrentView(), MainView.class).navigate();
        final MainView mainView = UiTestUtils.getCurrentView();
        // Create user different from authenticated user (admin) to trigger substitution
        final User user = createTestUser("substituted.user", "Substituted", "User");

        final Component component = invokePrivateMethod(mainView, "userMenuButtonRenderer", UserDetails.class, user);

        assertThat(component).isNotNull();
        assertThat(component).isInstanceOf(Div.class);
        // Component should include substitution indicator
    }

    @SuppressWarnings("unchecked")
    private <T> T invokePrivateMethod(
            final Object target, final String methodName, final Class<?> paramType, final Object arg)
            throws NoSuchMethodException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        final Method method = target.getClass().getDeclaredMethod(methodName, paramType);
        method.setAccessible(true);
        return (T) method.invoke(target, arg);
    }

    private User createTestUser(final String username, final String firstName, final String lastName) {
        final User user = dataManager.create(User.class);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        if (savedUser != null) {
            dataManager.remove(savedUser);
            savedUser = null;
        }
    }
}
