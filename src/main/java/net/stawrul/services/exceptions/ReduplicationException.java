package net.stawrul.services.exceptions;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wyjątek sygnalizujący niedostępność towaru.
 *
 * Wystąpienie wyjątku z hierarchii RuntimeException w warstwie biznesowej
 * powoduje wycofanie transakcji (rollback).
 */
public class ReduplicationException extends RuntimeException {
    public ReduplicationException() {
        super("You are not allowed to order the same thing more than once in the same order!");
    }
}
