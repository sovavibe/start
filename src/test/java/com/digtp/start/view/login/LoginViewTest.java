package com.digtp.start.view.login;

import static org.assertj.core.api.Assertions.assertThat;

import com.digtp.start.StartApplication;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.testassist.FlowuiTestAssistConfiguration;
import io.jmix.flowui.testassist.UiTest;
import io.jmix.flowui.testassist.UiTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@UiTest
@SpringBootTest(classes = {StartApplication.class, FlowuiTestAssistConfiguration.class})
class LoginViewTest {

    @Autowired
    ViewNavigators viewNavigators;

    @Test
    void testLoginViewInit() {
        viewNavigators.view(UiTestUtils.getCurrentView(), LoginView.class).navigate();

        final LoginView loginView = UiTestUtils.getCurrentView();
        assertThat(loginView).isNotNull();
    }
}
