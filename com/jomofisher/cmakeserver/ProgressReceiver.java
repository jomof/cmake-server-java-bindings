package com.jomofisher.cmakeserver;

/**
 * Handles messages received interactively during configure, etc.
 * Message types: "message", "progress"
 */
public interface ProgressReceiver {
  void receive(Message message);
}
