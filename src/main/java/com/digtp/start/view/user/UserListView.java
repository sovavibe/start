package com.digtp.start.view.user;

import com.digtp.start.entity.User;
import com.digtp.start.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.DialogMode;
import io.jmix.flowui.view.LookupComponent;
import io.jmix.flowui.view.StandardListView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import lombok.extern.slf4j.Slf4j;

@Route(value = "users", layout = MainView.class)
@ViewController(id = "User.list")
@ViewDescriptor(path = "user-list-view.xml")
@LookupComponent("usersDataGrid")
@DialogMode(width = "64em")
@Slf4j
@SuppressWarnings("java:S110") // Framework pattern: Jmix views extend multiple framework classes
// Suppressed globally in sonar-project.properties (e8),
// but required for Gradle SonarLint plugin
public class UserListView extends StandardListView<User> {
    // User deletion is handled by Jmix framework through DataManager.remove()
    // Logging is done at DataManager level or through entity lifecycle callbacks
}
