package com.digtp.start.view.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.digtp.start.test_support.AuthenticatedAsAdmin;
import com.vaadin.flow.component.combobox.ComboBox;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.testassist.FlowuiTestAssistConfiguration;
import io.jmix.flowui.testassist.UiTest;
import io.jmix.flowui.testassist.UiTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@UiTest
@SpringBootTest(classes = {com.digtp.start.StartApplication.class, FlowuiTestAssistConfiguration.class})
@ExtendWith({SpringExtension.class, AuthenticatedAsAdmin.class})
class UserDetailViewTest {

    @Autowired
    private ViewNavigators viewNavigators;

    @Test
    void testUserDetailViewInit() {
        viewNavigators.view(UiTestUtils.getCurrentView(), UserListView.class).navigate();
        final UserListView listView = UiTestUtils.getCurrentView();
        final JmixButton createButton = UiTestUtils.getComponent(listView, "createButton");
        createButton.click();

        final UserDetailView view = UiTestUtils.getCurrentView();

        assertThat(view).isNotNull();
        final ComboBox<String> timeZoneField = UiTestUtils.getComponent(view, "timeZoneField");
        assertThat(timeZoneField).isNotNull();
        assertThat(timeZoneField.getListDataView().getItems().count()).isGreaterThan(0);
    }
}
