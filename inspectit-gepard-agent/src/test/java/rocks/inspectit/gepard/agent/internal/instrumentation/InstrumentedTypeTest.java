/* (C) 2024 */
package rocks.inspectit.gepard.agent.internal.instrumentation;

import static org.junit.jupiter.api.Assertions.*;

import net.bytebuddy.description.type.TypeDescription;
import org.junit.jupiter.api.Test;

class InstrumentedTypeTest {

  private static final Class<?> TEST_CLASS = InstrumentedTypeTest.class;

  private static final TypeDescription TEST_TYPE = TypeDescription.ForLoadedType.of(TEST_CLASS);

  @Test
  void typeEqualsOtherType() {
    ClassLoader typeClassLoader = TEST_CLASS.getClassLoader();

    InstrumentedType type1 = new InstrumentedType(TEST_CLASS, typeClassLoader);
    InstrumentedType type2 = new InstrumentedType(TEST_TYPE, typeClassLoader);

    assertEquals(type2, type1);
  }

  @Test
  void typeDoesNotEqualOtherType() {
    ClassLoader typeClassLoader = TEST_CLASS.getClassLoader();

    InstrumentedType type1 = new InstrumentedType(TEST_CLASS, typeClassLoader);
    InstrumentedType type2 = new InstrumentedType(TEST_CLASS, null);

    assertNotEquals(type2, type1);
  }

  @Test
  void typeDoesNotEqualNull() {
    ClassLoader typeClassLoader = TEST_CLASS.getClassLoader();

    InstrumentedType type1 = new InstrumentedType(TEST_CLASS, typeClassLoader);

    assertNotEquals(null, type1);
  }

  @Test
  void typesHaveSameHashCode() {
    ClassLoader typeClassLoader = TEST_CLASS.getClassLoader();

    InstrumentedType type1 = new InstrumentedType(TEST_CLASS, typeClassLoader);
    InstrumentedType type2 = new InstrumentedType(TEST_TYPE, typeClassLoader);

    assertEquals(type2.hashCode(), type1.hashCode());
  }

  @Test
  void typesHaveDifferentHashCode() {
    ClassLoader typeClassLoader = TEST_CLASS.getClassLoader();

    InstrumentedType type1 = new InstrumentedType(TEST_CLASS, typeClassLoader);
    InstrumentedType type2 = new InstrumentedType(TEST_CLASS, null);

    assertNotEquals(type2.hashCode(), type1.hashCode());
  }
}
