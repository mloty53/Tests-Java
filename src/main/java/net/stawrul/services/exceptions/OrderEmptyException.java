package net.stawrul.services.exceptions;

public class OrderEmptyException extends RuntimeException {
    public OrderEmptyException() {
        super("Your oreder is empty!");
    }
}