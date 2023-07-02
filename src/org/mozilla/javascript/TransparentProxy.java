package org.mozilla.javascript;

/**
 * This class provides a proxy around a Java value.
 *
 * <p>When the proxied value is accessed within the JavaScript context, the value is loaded as
 * defined by the implementing class (see {@link TransparentProxy#load()}). The access is then
 * performed on the loaded value.
 *
 * <p>This class may be used to implement some kind of lazy loading logic to lazily provide values
 * for the JavaScript context.
 *
 * @author Erik Strempel
 */
public abstract class TransparentProxy implements Scriptable {

    private Object value = null;
    private Scriptable scriptable = null;

    /**
     * Implementor logic that provides a proxied value
     *
     * @return the proxied value
     */
    public abstract Object load();

    @Override
    public final String getClassName() {
        loadIfNull();
        return scriptable.getClassName();
    }

    @Override
    public final Object get(String name, Scriptable start) {
        loadIfNull();
        return scriptable.get(name, start);
    }

    @Override
    public final Object get(int index, Scriptable start) {
        loadIfNull();
        return scriptable.get(index, start);
    }

    @Override
    public final boolean has(String name, Scriptable start) {
        loadIfNull();
        return scriptable.has(name, start);
    }

    @Override
    public final boolean has(int index, Scriptable start) {
        loadIfNull();
        return scriptable.has(index, start);
    }

    @Override
    public final void put(String name, Scriptable start, Object nvalue) {
        loadIfNull();
        scriptable.put(name, start, nvalue);
    }

    @Override
    public final void put(int index, Scriptable start, Object nvalue) {
        loadIfNull();
        scriptable.put(index, start, nvalue);
    }

    @Override
    public final void delete(String name) {
        loadIfNull();
        scriptable.delete(name);
    }

    @Override
    public final void delete(int index) {
        loadIfNull();
        scriptable.delete(index);
    }

    @Override
    public final Scriptable getPrototype() {
        loadIfNull();
        return scriptable.getPrototype();
    }

    @Override
    public final void setPrototype(Scriptable prototype) {
        loadIfNull();
        scriptable.setPrototype(prototype);
    }

    @Override
    public final Scriptable getParentScope() {
        loadIfNull();
        return scriptable.getParentScope();
    }

    @Override
    public final void setParentScope(Scriptable parent) {
        loadIfNull();
        scriptable.setParentScope(parent);
    }

    @Override
    public final Object[] getIds() {
        loadIfNull();
        return scriptable.getIds();
    }

    @Override
    public final Object getDefaultValue(Class<?> hint) {
        loadIfNull();
        return value;
    }

    @Override
    public final boolean hasInstance(Scriptable instance) {
        loadIfNull();
        return scriptable.hasInstance(instance);
    }

    public final Scriptable getProxiedScriptable() {
        loadIfNull();
        return scriptable;
    }

    private void loadIfNull() {
        if (scriptable == null) {
            value = load();
            scriptable = Context.toObject(value, Context.getCurrentContext().initStandardObjects());
        }
    }
}
