/* (C) 2024 */
package rocks.inspectit.gepard.agent.configuration.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.inspectit.gepard.agent.configuration.persistence.file.ConfigurationFileReader;
import rocks.inspectit.gepard.agent.configuration.persistence.file.ConfigurationFileWriter;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedObserver;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedSubject;
import rocks.inspectit.gepard.config.model.InspectitConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.config.model.instrumentation.ScopeConfiguration;

@ExtendWith(MockitoExtension.class)
public class ConfigurationPersistenceTest {

  @Mock private ConfigurationFileReader reader;
  @Mock private ConfigurationFileWriter writer;
  @Mock private ConfigurationReceivedObserver observer;

  private ConfigurationPersistence persistence;

  private final ConfigurationReceivedSubject subject = ConfigurationReceivedSubject.getInstance();

  @BeforeEach
  void setUp() {
    persistence = ConfigurationPersistence.create(reader, writer);
    subject.addObserver(observer);
  }

  @AfterEach
  void clear() {
    subject.removeObserver(observer);
  }

  @Test
  void configurationLoadNotifiesObservers() {
    InspectitConfiguration configuration = createConfiguration();
    when(reader.readConfiguration()).thenReturn(configuration);
    ConfigurationReceivedEvent event = new ConfigurationReceivedEvent(this, configuration);

    persistence.loadLocalConfiguration();

    ArgumentCaptor<ConfigurationReceivedEvent> eventCaptor =
        ArgumentCaptor.forClass(ConfigurationReceivedEvent.class);
    verify(observer).handleConfiguration(eventCaptor.capture());
    assertEquals(
        event.getInspectitConfiguration(), eventCaptor.getValue().getInspectitConfiguration());
    // writer should not be notified to prevent unnecessary write operation
    verifyNoInteractions(writer);
  }

  @Test
  void nullDoesNotNotifyObservers() {
    when(reader.readConfiguration()).thenReturn(null);

    persistence.loadLocalConfiguration();

    verifyNoInteractions(observer);
    verifyNoInteractions(writer);
  }

  @Test
  void newConfigurationNotifiesObservers() {
    InspectitConfiguration configuration = createConfiguration();
    ConfigurationReceivedEvent event = new ConfigurationReceivedEvent(this, configuration);

    persistence.handleConfiguration(event);

    verify(writer).writeConfiguration(configuration);
  }

  private static InspectitConfiguration createConfiguration() {
    ScopeConfiguration scope =
        new ScopeConfiguration(true, "com.example.Application", Collections.emptyList());
    InstrumentationConfiguration instrumentationConfiguration =
        new InstrumentationConfiguration(List.of(scope));
    return new InspectitConfiguration(instrumentationConfiguration);
  }
}
