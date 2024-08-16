package rocks.inspectit.gepard.agent.internal.instrumentation;

import net.bytebuddy.agent.builder.AgentBuilder;

import java.util.Objects;

/**
 * Stores the full name as well as the class loader of a specific type.<br>
 * Since the method {@link AgentBuilder.Transformer#transform} does not provide a {@code Class}
 * object of the transformed type, we need to use the class name and the class loader to store our
 * instrumented types.<br>
 * We cannot create a {@code Class} object during transformation, since the class's byte-code is
 * still modified and not "ready" yet. Thus, {@code Class.forName()} isn't working. However, we
 * somehow need to remember which classes have been instrumented, so we store all necessary
 * information to identify a specific class.
 */
public class InstrumentedType {

  private final String typeName;

  private final ClassLoader classLoader;

  public InstrumentedType(final String typeName, final ClassLoader classLoader) {
    this.typeName = typeName;
    this.classLoader = classLoader;
  }

  /**
   * Checks, if the provided class objects references this type
   *
   * @param clazz the class object
   * @return true, if the provided class objects references this type
   */
  public boolean isEqualTo(Class<?> clazz) {
    return typeName.equals(clazz.getName()) && classLoader.equals(clazz.getClassLoader());
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof InstrumentedType otherType)
      return typeName.equals(otherType.typeName) && classLoader.equals(otherType.classLoader);
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(typeName, classLoader);
  }
}
