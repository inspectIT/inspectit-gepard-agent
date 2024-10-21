/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.instrumentation;

import java.util.Objects;
import javax.annotation.Nullable;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;

/**
 * Stores the full name as well as the class loader of a specific type.<br>
 * Since the method {@link AgentBuilder.Transformer#transform} does not provide a {@code Class}
 * object of the transformed type, we need to use the type description and the class loader to store
 * our instrumented types.<br>
 * We cannot create a {@code Class} object during transformation, since the class's byte-code is
 * still modified and not "ready" yet. Thus, {@code Class.forName()} isn't working. However, we
 * somehow need to remember which classes have been instrumented, so we store all necessary
 * information to identify a specific class.
 */
public class InstrumentedType {

  private final TypeDescription typeDescription;

  /**
   * The classLoader to load the type. Might be {@code null} to represent the bootstrap classLoader
   */
  private final ClassLoader classLoader;

  public InstrumentedType(TypeDescription typeDescription, @Nullable ClassLoader classLoader) {
    this.typeDescription = typeDescription;
    this.classLoader = classLoader;
  }

  public InstrumentedType(Class<?> clazz, @Nullable ClassLoader classLoader) {
    this.typeDescription = TypeDescription.ForLoadedType.of(clazz);
    this.classLoader = classLoader;
  }

  /**
   * @return the ByteBuddy type description
   */
  public TypeDescription getTypeDescription() {
    return typeDescription;
  }

  @Override
  public boolean equals(Object other) {
    if (other instanceof InstrumentedType otherType) {
      if (withBootstrapClassLoader())
        return typeDescription.equals(otherType.typeDescription)
            && Objects.isNull(otherType.classLoader);
      return typeDescription.equals(otherType.typeDescription)
          && classLoader.equals(otherType.classLoader);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(typeDescription, classLoader);
  }

  /**
   * @return true, if this instrumented type uses the bootstrap classLoader
   */
  private boolean withBootstrapClassLoader() {
    return Objects.isNull(classLoader);
  }
}
