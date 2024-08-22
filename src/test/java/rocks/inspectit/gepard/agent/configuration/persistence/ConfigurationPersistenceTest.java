package rocks.inspectit.gepard.agent.configuration.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import rocks.inspectit.gepard.agent.internal.configuration.model.InspectitConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.InstrumentationConfiguration;
import rocks.inspectit.gepard.agent.internal.configuration.model.instrumentation.Scope;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedEvent;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedObserver;
import rocks.inspectit.gepard.agent.internal.configuration.observer.ConfigurationReceivedSubject;

@ExtendWith(MockitoExtension.class)
public class ConfigurationPersistenceTest {

  @Mock private ConfigurationFileReader reader;
  @Mock private ConfigurationFileWriter writer;
  @Mock private ConfigurationReceivedObserver observer;

  private ConfigurationPersistence persistence;

  private final ConfigurationReceivedSubject subject = ConfigurationReceivedSubject.getInstance();

  @BeforeEach
  void setUp() {
    persistence = new ConfigurationPersistence(reader, writer);
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
  }

  @Test
  void nullDoesNotNotifyObservers() {
    when(reader.readConfiguration()).thenReturn(null);

    persistence.loadLocalConfiguration();

    verify(observer, never()).handleConfiguration(any());
  }

  @Test
  void newConfigurationNotifiesObservers() {
    InspectitConfiguration config = createConfiguration();
    ConfigurationReceivedEvent event = new ConfigurationReceivedEvent(this, config);

    persistence.processConfiguration(config);

    ArgumentCaptor<ConfigurationReceivedEvent> eventCaptor =
        ArgumentCaptor.forClass(ConfigurationReceivedEvent.class);
    verify(observer).handleConfiguration(eventCaptor.capture());
    assertEquals(
        event.getInspectitConfiguration(), eventCaptor.getValue().getInspectitConfiguration());
  }

  private static InspectitConfiguration createConfiguration() {
    Scope scope = new Scope("com.example.Application", true);
    InstrumentationConfiguration instrumentationConfiguration =
        new InstrumentationConfiguration(List.of(scope));
    return new InspectitConfiguration(instrumentationConfiguration);
  }
}
