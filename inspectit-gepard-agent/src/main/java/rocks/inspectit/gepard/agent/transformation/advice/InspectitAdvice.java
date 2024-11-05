/* (C) 2024 */
package rocks.inspectit.gepard.agent.transformation.advice;

import java.lang.reflect.Method;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import rocks.inspectit.gepard.bootstrap.Instances;
import rocks.inspectit.gepard.bootstrap.context.InternalInspectitContext;
import rocks.inspectit.gepard.bootstrap.instrumentation.IMethodHook;

/**
 * Static code, which will be injected into target scopes (class methods). There are some rules for
 * advice classes:
 *
 * <ul>
 *   <li>They MUST only contain static methods
 *   <li>They MUST NOT contain any state (fields) whatsoever, static constants included. Only the
 *       advice methods' content is copied to the instrumented code, constants are not
 *   <li>They SHOULD NOT contain any methods other than @Advice-annotated method
 * </ul>
 */
@SuppressWarnings("unused")
public class InspectitAdvice {

  @Advice.OnMethodEnter(suppress = Throwable.class)
  public static InternalInspectitContext onEnter(
      @Advice.AllArguments Object[] args,
      @Advice.This Object thiz,
      @Advice.Origin("#t") Class<?> declaringClass,
      @Advice.Origin("#m") Method method,
      @Advice.Origin("#m#s") String signature) {
    System.out.println(
        "Executing ENTRY Advice in method: "
            + signature
            + " of class: "
            + thiz.getClass().getName());

    IMethodHook hook = Instances.hookManager.getHook(declaringClass, signature);
    return hook.onEnter(declaringClass, thiz, method, args);
  }

  @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
  public static void onExit(
      @Advice.Thrown Throwable throwable,
      @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue,
      @Advice.Enter InternalInspectitContext context,
      @Advice.Origin("#t") Class<?> declaringClass,
      @Advice.Origin("#m#s") String signature) {
    System.out.println(
        "Executing EXIT Advice in method: " + signature + " of class: " + declaringClass.getName());

    IMethodHook hook = context.getHook();
    hook.onExit(context, returnValue, throwable);
  }
}
