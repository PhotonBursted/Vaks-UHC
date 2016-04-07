package com.photonburst.VaksUHC;

public class OptionNotConfiguredException extends Exception {
    public OptionNotConfiguredException() {
        super();
    }

    public OptionNotConfiguredException(String msg) {
        super("Couldn't find "+ msg);
    }
}