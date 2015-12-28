package com.github.p4535992.util.database.jooq.spring.todo.exception;

/**
 * @author Petri Kainulainen
 */
public class TodoNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 123758347L;

    public TodoNotFoundException(String message) {
        super(message);
    }
}
