package rocks.inspectit.gepard.agent.transformation.advice;

import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;

/** Static code, which should be injected into target scopes (class methods) */
@SuppressWarnings("unused")
public class InspectitAdvice {

  @Advice.OnMethodEnter(suppress = Throwable.class)
  public static void onEnter(@Advice.AllArguments Object[] args, @Advice.This Object thiz, @Advice.Origin String method) {
    System.out.println("HELLO GEPARD from: " + thiz.getClass().getName() + " in method: " + method);
  }

  @Advice.OnMethodExit(suppress = Throwable.class, onThrowable = Throwable.class)
  public static void onExit(
      @Advice.AllArguments Object[] args,
      @Advice.This Object thiz,
      @Advice.Thrown Throwable throwable,
      @Advice.Return(typing = Assigner.Typing.DYNAMIC) Object returnValue,
      @Advice.Origin String method) {
    System.out.println("BYE GEPARD from: " + thiz.getClass().getName() + " in method: " + method);
  }
}
