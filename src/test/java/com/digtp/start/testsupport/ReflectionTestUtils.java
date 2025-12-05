/*
 * Copyright 2025 Digital Technologies and Platforms LLC
 * Licensed under the Apache License, Version 2.0
 */
package com.digtp.start.testsupport;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Utility class for reflection operations in tests.
 *
 * <p>Provides helper methods to invoke private/protected methods and constructors
 * in tests using reflection. This allows testing internal logic without making
 * methods/constructors package-private.
 */
public final class ReflectionTestUtils {

    private ReflectionTestUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Invokes a method on an object using reflection.
     *
     * <p>Helper method to invoke private/protected methods in tests using reflection
     * to avoid ClassCastException with Jmix class loaders. Uses type-safe cast via
     * {@code Class.cast()} to avoid unchecked warnings.
     *
     * @param <T> return type of the method
     * @param returnType class of the return type (for type-safe casting)
     * @param object object on which to invoke the method
     * @param methodName name of the method to invoke
     * @param paramTypes parameter types of the method
     * @param args arguments to pass to the method
     * @return result of method invocation
     * @throws ReflectiveOperationException if method cannot be found or invoked
     */
    public static <T> T invokeMethod(
            final Class<T> returnType,
            final Object object,
            final String methodName,
            final Class<?>[] paramTypes,
            final Object... args)
            throws ReflectiveOperationException {
        // Try getDeclaredMethod first (for private/protected methods), then getMethod (for public/inherited methods)
        Method method;
        try {
            method = object.getClass().getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
        } catch (NoSuchMethodException e) {
            method = object.getClass().getMethod(methodName, paramTypes);
        }
        final Object result = method.invoke(object, args);
        return returnType.cast(result);
    }

    /**
     * Invokes a constructor using reflection.
     *
     * <p>Helper method to invoke private/protected constructors in tests using reflection.
     * Useful for testing utility classes that have private constructors.
     *
     * @param <T> type of the class to instantiate
     * @param clazz class to instantiate
     * @param paramTypes parameter types of the constructor
     * @param args arguments to pass to the constructor
     * @return new instance of the class
     * @throws ReflectiveOperationException if constructor cannot be found or invoked
     */
    public static <T> T invokeConstructor(
            final Class<T> clazz, final Class<?>[] paramTypes, final Object... args)
            throws ReflectiveOperationException {
        final Constructor<T> constructor = clazz.getDeclaredConstructor(paramTypes);
        constructor.setAccessible(true);
        return constructor.newInstance(args);
    }
}

