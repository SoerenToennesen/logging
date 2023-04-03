package com.maersk.logging.models;

import java.util.Arrays;
import java.util.Optional;

public enum Severity {
    TRACE(0),
    DEBUG(1),
    INFO(2),
    WARN(3),
    ERROR(4),
    FATAL(5);
    private int numVal;
    Severity(int numVal) {this.numVal = numVal;}
    public int getNumVal() {return numVal;}
    public static Severity valueOf(int numVal) {
        return Arrays.stream(values())
                .filter(legNo -> legNo.numVal == numVal)
                .findFirst().get();
    }
}
