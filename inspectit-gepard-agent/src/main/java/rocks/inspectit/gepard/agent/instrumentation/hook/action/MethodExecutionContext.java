/* (C) 2024 */
package rocks.inspectit.gepard.agent.instrumentation.hook.action;

import java.lang.reflect.Method;

/** Stores information about the currently executed method from the target application. */
public final class MethodExecutionContext {

  private final Class<?> declaringClass;

  private final Method method;

  private final Object[] arguments;

  public MethodExecutionContext(Class<?> declaringClass, Method method, Object[] arguments) {
    this.declaringClass = declaringClass;
    this.method = method;
    this.arguments = arguments;
  }

  /**
   * @return the class of the method
   */
  public Class<?> getDeclaringClass() {
    return declaringClass;
  }

  /**
   * @return the method object
   */
  public Method getMethod() {
    return method;
  }

  /**
   * @return the passed method arguments during execution
   */
  public Object[] getArguments() {
    return arguments;
  }
}
