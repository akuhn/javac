package ch.akuhn.javac;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Provides access to private members.
 * 
 * @author Adrian Kuhn, Mar 16, 2009
 */
public class AllYourPrivateAreBelongToUs {

    @SuppressWarnings("unchecked")
    public static final <T> T getField(Object object, String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }
    
    @SuppressWarnings("unchecked")
    public static final <T> T invokeMethod(Object object, String name, Class<?> parameter, Object argument) {
        try {
            Method method = object.getClass().getDeclaredMethod(name, parameter);
            method.setAccessible(true);
            return (T) method.invoke(object, argument);
        } catch (InvocationTargetException ex) {
            throw rethrow(ex.getTargetException());
        } catch (Exception ex) {
            throw rethrow(ex);
        }
    }

    @SuppressWarnings("deprecation")
    private static RuntimeException rethrow(Throwable throwable) {
        Thread.currentThread().stop(throwable);
        throw null;
    }
    
    @SuppressWarnings("unchecked")
    public static final <T> T invokeMethod(Object object, String name) {
        try {
            Method method = object.getClass().getDeclaredMethod(name);
            method.setAccessible(true);
            return (T) method.invoke(object);
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }
    }
    
}
