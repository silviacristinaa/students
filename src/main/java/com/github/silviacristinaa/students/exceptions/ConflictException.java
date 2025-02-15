package com.github.silviacristinaa.students.exceptions;

public class ConflictException extends Exception{

    private static final long serialVersionUID = 1L;

    public ConflictException(final String error) {
        super(error);
    }
}
