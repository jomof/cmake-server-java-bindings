package com.jomofisher.cmakeserver;

import com.jomofisher.cmakeserver.modelv1.InteractiveMessage;

/**
 * Handles messages received interactively during configure, etc.
 */
public interface MessageReceiver {
    void receive(InteractiveMessage message);
}
