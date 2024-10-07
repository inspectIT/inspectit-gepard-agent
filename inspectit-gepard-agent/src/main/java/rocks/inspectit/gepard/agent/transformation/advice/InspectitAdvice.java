/* (C) 2024 */
package rocks.inspectit.gepard.agent.transformation.advice;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import rocks.inspectit.gepard.bootstrap.Instances;
import rocks.inspectit.gepard.bootstrap.context.InternalInspectitContext;
import rocks.inspectit.gepard.bootstrap.instrumentation.IMethodHook;

/** Static code, which should be injected into target scopes (class methods) */
@SuppressWarnings("unused")
public class InspectitAdvice {

  @Advice.OnMethodEnter(suppress = Throwable.class)
  public static InternalInspectitContext onEnter(
      @Advice.AllArguments Object[] args,
      @Advice.This Object thiz,
      @Advice.Origin("#t") Class<?> declaringClass,
      @Advice.Origin("#m#s") String signature) {
    System.out.println(
        "Executing ENTRY Advice in method: "
            + signature
            + " of class: "
            + thiz.getClass().getName());

    IMethodHook hook = Instances.hookManager.getHook(declaringClass, signature);
    return hook.onEnter(args, thiz);
  }

  @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
  public static void onExit(
      @Advice.AllArguments Object[] args,
      @Advice.This Object thiz,
      @Advice.Thrown Throwable throwable,
      @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue,
      @Advice.Enter InternalInspectitContext context,
      @Advice.Origin("#m#s") String signature) {
    System.out.println(
        "Executing EXIT Advice in method: "
            + signature
            + " of class: "
            + thiz.getClass().getName());

    IMethodHook hook = context.getHook();
    hook.onExit(context, args, thiz, returnValue, throwable);
  }
}
