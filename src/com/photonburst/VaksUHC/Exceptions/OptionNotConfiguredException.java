package com.photonburst.VaksUHC.Exceptions;

/**
 * Exception designating a misconfigured setting
 */
public class OptionNotConfiguredException extends Exception {
    /**
     * Constructor for the exception
     */
    public OptionNotConfiguredException() {
        super();
    }

    /**
     * Constructor for the exception, allows for sending a message
     * @param msg           Message to be sent alongside the error
     */
    public OptionNotConfiguredException(String msg) {
        super("Couldn't find "+ msg);
    }
}