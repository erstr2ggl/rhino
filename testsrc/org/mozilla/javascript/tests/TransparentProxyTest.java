package org.mozilla.javascript.tests;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.TransparentProxy;
import org.mozilla.javascript.Undefined;

public class TransparentProxyTest {

    @Test
    public void testWithNumber() {
        test(1.23, 6.23, Undefined.instance, "1.23", "1.23", 1.0, 11.23);
    }

    @Test
    public void testWithString() {
        test("string", "string5", 6, "string", "\"string\"", Double.NaN, "string10");
    }

    private void test(
            Object value,
            Object add5Result,
            Object lengthResult,
            Object toStringResult,
            Object stringifyResult,
            Object parseIntResult,
            Object addAndSet10Result) {
        Utils.runWithAllOptimizationLevels(
                cx -> {
                    Scriptable scope = cx.initStandardObjects();
                    TransparentProxyImpl transparentProxy = new TransparentProxyImpl(value);
                    scope.put("test", scope, transparentProxy);

                    Object result;

                    result = cx.evaluateString(scope, "test", "test", 1, null);
                    assertThat(result, instanceOf(TransparentProxy.class));
                    assertFalse(transparentProxy.loaded());

                    result = cx.evaluateString(scope, "test + 5", "test", 1, null);
                    assertEquals(add5Result, result);
                    assertTrue(transparentProxy.loaded());

                    result = cx.evaluateString(scope, "test.length", "test", 1, null);
                    if (lengthResult == Undefined.instance) {
                        assertTrue(Undefined.isUndefined(result));
                    } else {
                        assertEquals(lengthResult, result);
                    }

                    result = cx.evaluateString(scope, "test.toString()", "test", 1, null);
                    assertEquals(toStringResult, result);

                    result = cx.evaluateString(scope, "JSON.stringify(test)", "test", 1, null);
                    assertEquals(stringifyResult, result);

                    result = cx.evaluateString(scope, "parseInt(test)", "test", 1, null);
                    assertEquals(parseIntResult, result);

                    result =
                            cx.evaluateString(
                                    scope,
                                    "(function() { test += 10; return test; })()",
                                    "test",
                                    1,
                                    null);
                    assertEquals(addAndSet10Result, result);

                    return null;
                });
    }

    @Test
    public void testWithScriptable() {
        Utils.runWithAllOptimizationLevels(
                cx -> {
                    Scriptable scope = cx.initStandardObjects();
                    scope.put("test", scope, new TransparentProxyImpl(new TestScriptable()));

                    Object result;

                    result = cx.evaluateString(scope, "test", "test", 1, null);
                    assertThat(result, instanceOf(TransparentProxy.class));

                    result = cx.evaluateString(scope, "test.i", "test", 1, null);
                    assertEquals(0, result);

                    result = cx.evaluateString(scope, "test.i", "test", 1, null);
                    assertEquals(1, result);

                    result = cx.evaluateString(scope, "test.i", "test", 1, null);
                    assertEquals(2, result);

                    result = cx.evaluateString(scope, "JSON.stringify(test)", "test", 1, null);
                    assertEquals("{\"i\":3}", result);

                    return null;
                });
    }

    private static class TransparentProxyImpl extends TransparentProxy {

        private final Object value;
        private boolean verifyLoaded;

        public TransparentProxyImpl(Object value) {
            this.value = value;
            verifyLoaded = false;
        }

        @Override
        public Object load() {
            verifyLoaded = true;
            return value;
        }

        public boolean loaded() {
            return verifyLoaded;
        }
    }

    private static class TestScriptable implements Scriptable {

        private int i = 0;

        @Override
        public String getClassName() {
            return "TestScriptable";
        }

        @Override
        public Object get(String name, Scriptable start) {
            if (name.equals("i")) {
                return i++;
            }
            return NOT_FOUND;
        }

        @Override
        public Object get(int index, Scriptable start) {
            return NOT_FOUND;
        }

        @Override
        public boolean has(String name, Scriptable start) {
            return name.equals("i");
        }

        @Override
        public boolean has(int index, Scriptable start) {
            return false;
        }

        @Override
        public Object[] getIds() {
            return new Object[] {"i"};
        }

        @Override
        public void put(String name, Scriptable start, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void put(int index, Scriptable start, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void delete(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void delete(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Scriptable getPrototype() {
            return null;
        }

        @Override
        public void setPrototype(Scriptable prototype) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Scriptable getParentScope() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setParentScope(Scriptable parent) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getDefaultValue(Class<?> hint) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasInstance(Scriptable instance) {
            throw new UnsupportedOperationException();
        }
    }
}
