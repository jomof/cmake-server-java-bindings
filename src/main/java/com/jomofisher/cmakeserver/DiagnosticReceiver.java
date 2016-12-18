package com.jomofisher.cmakeserver;

/**
 * Receives diagnostic logging messages from this library.
 */
interface DiagnosticReceiver {
    void receive(String diagnosticMessage);
}
