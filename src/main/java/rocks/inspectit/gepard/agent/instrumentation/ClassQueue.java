package rocks.inspectit.gepard.agent.instrumentation;

import java.util.HashSet;
import java.util.Set;

//TODO: Make Class Queue Thread SAFE!
public class ClassQueue {
  private final Set<Class<?>> pendingClasses;

  public ClassQueue() {
    this.pendingClasses = new HashSet<>();
  }

  public void add(Class<?> clazz) {
    pendingClasses.add(clazz);
  }

  public void addAll(Set<Class<?>> classes) {
    pendingClasses.addAll(classes);
  }

  public boolean isEmpty() {
    return pendingClasses.isEmpty();
  }

  public int size() {
    return pendingClasses.size();
  }

  public Set<Class<?>> getPendingClasses() {
    return pendingClasses;
  }
}
