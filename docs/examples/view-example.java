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
package com.digtp.start.view.your;

// TODO: Replace package name with your package

import com.digtp.start.entity.YourEntity; // TODO: Replace with your entity
import com.digtp.start.service.YourEntityService; // TODO: Replace with your service
import com.digtp.start.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.EntityStates;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.MessageBundle;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.Subscribe;
import io.jmix.flowui.view.ViewComponent;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Detail view for [YourEntity] entity creation and editing.
 *
 * <p>Provides form-based editing of [YourEntity] properties.
 * Follows Jmix/Vaadin best practices.
 *
 * <p>Best Practices:
 * <ul>
 *   <li>@Route for URL routing</li>
 *   <li>@ViewController + @ViewDescriptor for XML layout</li>
 *   <li>@EditedEntityContainer for data binding</li>
 *   <li>@RequiredArgsConstructor for constructor injection</li>
 *   <li>@ViewComponent for UI components (fields, buttons)</li>
 *   <li>@Subscribe for lifecycle events (onInit, onReady, onBeforeSave, onAfterSave)</li>
 *   <li>@Slf4j for structured logging</li>
 *   <li>Parameterized logging (no string concatenation)</li>
 *   <li>Objects.equals() for null-safe comparisons</li>
 *   <li>String.formatted() for modern Java string formatting</li>
 *   <li>Static final fields for cached data (e.g., TimeZone IDs)</li>
 * </ul>
 *
 * <p>Reference: See UserDetailView.java for complete example
 */
@Route(value = "your-entities/:id", layout = MainView.class) // TODO: Replace with your route
@ViewController(id = "YourEntity.detail") // TODO: Replace with your view ID
@ViewDescriptor(path = "your-entity-detail-view.xml") // TODO: Replace with your XML path
@EditedEntityContainer("yourEntityDc") // TODO: Replace with your container ID
@Slf4j
@RequiredArgsConstructor
public class YourEntityDetailView extends StandardDetailView<YourEntity> {

    // TODO: Add your service dependencies (constructor injection)
    private final transient EntityStates entityStates;
    private final transient YourEntityService yourEntityService; // TODO: Replace with your service

    // TODO: Add your UI components (@ViewComponent)
    @ViewComponent
    private TypedTextField<String> nameField; // TODO: Replace with your fields

    @ViewComponent
    private MessageBundle messageBundle;

    // TODO: Add cached data as static final fields
    // Example:
    // private static final List<String> AVAILABLE_OPTIONS = List.of("option1", "option2");

    /**
     * Lifecycle event: Called when view is initialized.
     *
     * <p>Use for setting up UI components, loading data, etc.
     * Parameter name prefixed with _ if unused (convention).
     *
     * @param _event initialization event
     */
    @Subscribe
    public void onInit(final InitEvent _event) {
        // TODO: Initialize UI components
        // Example:
        // comboBoxField.setItems(AVAILABLE_OPTIONS);
        log.debug("YourEntity detail view initialized");
    }

    /**
     * Lifecycle event: Called when entity is initialized (new or existing).
     *
     * <p>Use for setting up entity-specific UI state.
     *
     * @param event entity initialization event
     */
    @Subscribe
    public void onInitEntity(final InitEntityEvent<YourEntity> event) {
        final YourEntity entity = event.getEntity();
        // TODO: Set up entity-specific UI
        // Example:
        // nameField.setReadOnly(!entityStates.isNew(entity));
        log.debug(
                "YourEntity initialized: isNew={}, name={}",
                entityStates.isNew(entity),
                entity.getName() != null ? entity.getName() : "not set");
    }

    /**
     * Lifecycle event: Called when view is ready (after all components are initialized).
     *
     * <p>Use for focusing fields, final setup, etc.
     *
     * @param _event ready event
     */
    @Subscribe
    public void onReady(final ReadyEvent _event) {
        final YourEntity entity = getEditedEntity();
        if (entityStates.isNew(entity)) {
            // TODO: Focus first field for new entities
            // Example:
            // nameField.focus();
            log.debug("New YourEntity detail view ready");
        } else {
            log.debug("YourEntity detail view ready: id={}, name={}", entity.getId(), entity.getName());
        }
    }

    /**
     * Lifecycle event: Called before entity is saved.
     *
     * <p>Use for validation, data preparation, etc.
     *
     * @param _event before save event
     */
    @Subscribe
    public void onBeforeSave(final BeforeSaveEvent _event) {
        final YourEntity entity = getEditedEntity();
        final boolean isNew = entityStates.isNew(entity);
        
        // TODO: Prepare entity for save
        // Example:
        // if (isNew) {
        //     yourEntityService.prepareEntityForSave(entity, true);
        //     log.debug("Preparing new YourEntity for save: name={}", entity.getName());
        // } else {
        //     yourEntityService.prepareEntityForSave(entity, false);
        //     log.debug("Preparing YourEntity update: id={}, name={}", entity.getId(), entity.getName());
        // }
    }

    /**
     * Lifecycle event: Called after entity is saved.
     *
     * <p>Use for notifications, navigation, etc.
     *
     * @param _event after save event
     */
    @Subscribe
    public void onAfterSave(final AfterSaveEvent _event) {
        final YourEntity entity = getEditedEntity();
        log.info("YourEntity saved successfully: id={}, name={}", entity.getId(), entity.getName());
        // TODO: Add notifications, navigation, etc.
    }
}

