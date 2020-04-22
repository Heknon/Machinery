package me.oriharel.machinery.utilities;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectionUtils {

    public static final class Fields {
        /**
         * Easily get a field value
         *
         * @param instance  the instance of the object to get from
         * @param fieldName the field name
         * @param <R>       the type of the field
         * @return the value or null if not found
         */
        public static <R> R getFieldValueOfObject(Object instance, String fieldName) {
            try {
                Field field = instance.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                return (R) field.get(instance);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Easily get a field value. for cases with inheritance
         *
         * @param instance  the instance of the object to get from
         * @param fieldName the field name
         * @param <R>       the type of the field
         * @return the value or null if not found
         */
        public static <R, T> R getFieldValueOfObjectExact(T instance, String fieldName) {
            try {
                Field field = new TypeToken<T>(){}.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                return (R) field.get(instance);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Easily get a field value. if your object is an interface, use this.
         *
         * @param instance  the instance of the object to get from
         * @param clazz     the class that implements the interface and has the field
         * @param fieldName the field name
         * @param <R>       the type of the field
         * @return the value or null if not found
         */
        public static <R> R getFieldValueOfUnknownClass(Object instance, Class<?> clazz, String fieldName) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return (R) field.get(instance);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Easily get a field value. if your object is an interface, use this.
         *
         * @param instance  the instance of the object to get from
         * @param className the class that implements the interface and has the field
         * @param fieldName the field name
         * @param <R>       the type of the field
         * @return the value or null if not found
         */
        public static <R> R getFieldValueOfUnknownClass(Object instance, String className, String fieldName) {
            try {
                Field field = Class.forName(className).getDeclaredField(fieldName);
                field.setAccessible(true);
                return (R) field.get(instance);
            } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Get a Field
         *
         * @param instance  the instance of the object to get from
         * @param fieldName the field name
         * @return the value or null if not found
         */
        public static Field getField(Object instance, String fieldName) {
            try {
                return instance.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Get a Field
         *
         * @param clazz     the class to get the field from
         * @param fieldName the field name
         * @return the value or null if not found
         */
        public static Field getField(Class<?> clazz, String fieldName) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Easily get a field value
         *
         * @param className the class name to get the field from. for package-private classes
         * @param fieldName the field name
         * @return the value or null if not found
         */
        public static Field getField(String className, String fieldName) {
            try {
                return Class.forName(className).getDeclaredField(fieldName);
            } catch (NoSuchFieldException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public static final class Methods {
        /**
         * Easily execute a method
         *
         * @param instance   the instance of the object to get from
         * @param methodName the method name
         * @param parameters the parameters of the method
         * @param <R>        the type of return value
         * @return the return value or null
         */
        public static <R> R executeMethod(Object instance, String methodName, Class<?>... parameters) {
            try {
                Method method = instance.getClass().getDeclaredMethod(methodName, parameters);
                method.setAccessible(true);
                return (R) method.invoke(instance);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
