package com.jomofisher.cmake.modelv1;

/**
 * Used to monitor deserialization. Can be used to check for missing fields in receiving class.
 */
public interface DeserializationMonitor {
    <T> void receive(String message, Class<T> clazz);
}
