package com.jomofisher.cmake.modelv1;

/**
 * Handles messages received interactively during configure, etc.
 */
public interface MessageReceiver {
    void receive(InteractiveMessage message);
}
