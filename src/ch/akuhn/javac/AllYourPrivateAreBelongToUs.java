/* This file is part of "Printf.jar".
 *
 * "Printf.jar" is free software: you can redistribute it and/or modify
 * it under the terms of the Affero GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Printf.jar" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Affero GNU General Public License for more details.
 *
 * You should have received a copy of the Affero GNU General Public License
 * along with "Printf.jar".  If not, see <http://www.gnu.org/licenses/>.
 *
 * 
 */
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
