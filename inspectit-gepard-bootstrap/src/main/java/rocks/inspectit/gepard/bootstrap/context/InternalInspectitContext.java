package rocks.inspectit.gepard.bootstrap.context;

import rocks.inspectit.gepard.bootstrap.instrumentation.IMethodHook;

public class InternalInspectitContext {

    private final IMethodHook hook;

    private final AutoCloseable spanScope;

    public InternalInspectitContext(IMethodHook hook, AutoCloseable spanScope) {
        this.spanScope = spanScope;
        this.hook = hook;
    }

    public IMethodHook getHook() {
        return hook;
    }

    public AutoCloseable getScope() {
        return spanScope;
    }
}
