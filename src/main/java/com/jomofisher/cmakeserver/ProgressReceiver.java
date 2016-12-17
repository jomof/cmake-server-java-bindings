package com.jomofisher.cmakeserver;

import com.jomofisher.cmakeserver.model.BaseMessage;

/**
 * Handles messages received interactively during configure, etc.
 * BaseMessage types: "message", "progress"
 */
public interface ProgressReceiver {
  void receive(BaseMessage baseMessage);
}
