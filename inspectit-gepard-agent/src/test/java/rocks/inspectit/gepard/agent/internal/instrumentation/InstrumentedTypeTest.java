/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.instrumentation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InstrumentedTypeTest {

  private static final Class<?> TEST_CLASS = InstrumentedTypeTest.class;

  @Test
  void typeIsEqualToClass() {
    String typeName = TEST_CLASS.getName();
    ClassLoader typeClassLoader = TEST_CLASS.getClassLoader();
    InstrumentedType type = new InstrumentedType(typeName, typeClassLoader);

    assertTrue(type.isEqualTo(TEST_CLASS));
  }

  @Test
  void typeWithDifferentNameIsNotEqualToClass() {
    String typeName = TEST_CLASS.getSimpleName();
    ClassLoader typeClassLoader = TEST_CLASS.getClassLoader();
    InstrumentedType type = new InstrumentedType(typeName, typeClassLoader);

    assertFalse(type.isEqualTo(TEST_CLASS));
  }

  @Test
  void typeWithDifferentClassLoaderIsNotEqualToClass() {
    String typeName = TEST_CLASS.getName();
    InstrumentedType type = new InstrumentedType(typeName, null);

    assertFalse(type.isEqualTo(TEST_CLASS));
  }

  @Test
  void typeEqualsOtherType() {
    String typeName = TEST_CLASS.getName();
    ClassLoader typeClassLoader = TEST_CLASS.getClassLoader();

    InstrumentedType type1 = new InstrumentedType(typeName, typeClassLoader);
    InstrumentedType type2 = new InstrumentedType(typeName, typeClassLoader);

    assertEquals(type2, type1);
  }

  @Test
  void typeDoesNotEqualOtherType() {
    String typeName = TEST_CLASS.getName();
    ClassLoader typeClassLoader = TEST_CLASS.getClassLoader();

    InstrumentedType type1 = new InstrumentedType(typeName, typeClassLoader);
    InstrumentedType type2 = new InstrumentedType(typeName, null);

    assertNotEquals(type2, type1);
  }

  @Test
  void typeDoesNotEqualNull() {
    String typeName = TEST_CLASS.getName();
    ClassLoader typeClassLoader = TEST_CLASS.getClassLoader();

    InstrumentedType type1 = new InstrumentedType(typeName, typeClassLoader);

    assertNotEquals(null, type1);
  }

  @Test
  void typesHaveSameHashCode() {
    String typeName = TEST_CLASS.getName();
    ClassLoader typeClassLoader = TEST_CLASS.getClassLoader();

    InstrumentedType type1 = new InstrumentedType(typeName, typeClassLoader);
    InstrumentedType type2 = new InstrumentedType(typeName, typeClassLoader);

    assertEquals(type2.hashCode(), type1.hashCode());
  }

  @Test
  void typesHaveDifferentHashCode() {
    String typeName = TEST_CLASS.getName();
    ClassLoader typeClassLoader = TEST_CLASS.getClassLoader();

    InstrumentedType type1 = new InstrumentedType(typeName, typeClassLoader);
    InstrumentedType type2 = new InstrumentedType(typeName, null);

    assertNotEquals(type2.hashCode(), type1.hashCode());
  }
}
