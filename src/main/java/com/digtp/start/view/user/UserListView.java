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
@SuppressWarnings(
        "java:S110") // Framework pattern: Jmix views extend multiple classes. Required for Gradle SonarLint plugin.
public class UserListView extends StandardListView<User> {

    @Subscribe
    public void onInit(final InitEvent event) {
        log.debug("User list view initialized");
    }
}
