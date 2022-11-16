package org.worldcubeassociation.tnoodle.scrambles;

import java.lang.reflect.Constructor;

class LazySupplier<T> {
    private T instance;

    public Class<T> supplyingClass;
    protected Object[] ctorArgs;

    public LazySupplier(Class<T> supplyingClass, Object... ctorArgs) {
        this.supplyingClass = supplyingClass;
        this.ctorArgs = ctorArgs;
    }

    public T getInstance() {
        if (this.instance == null) {
            this.instance = this.provideInstance();
        }

        return this.instance;
    }

    private T provideInstance() {
        try {
            Class<?>[] classes = this.getCtorArgClasses();
            Constructor<T> constructor = this.supplyingClass.getConstructor(classes);
            return constructor.newInstance(this.ctorArgs);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Class<?>[] getCtorArgClasses() {
        Class<?>[] ctorArgTypes = new Class[this.ctorArgs.length];

        for (int i = 0; i < ctorArgTypes.length; i++) {
            Object arg = this.ctorArgs[i];

            if (arg instanceof Integer) {
                ctorArgTypes[i] = int.class;
            } else {
                ctorArgTypes[i] = this.ctorArgs[i].getClass();
            }
        }

        return ctorArgTypes;
    }
}
