package org.lantern;

import org.apache.log4j.spi.LoggingEvent;

public class LogglyEvent {
    private long time;
    private Object message;
    private String country;

    public LogglyEvent(LoggingEvent le, String country) {
        super();
        this.time = le.getTimeStamp();
        this.message = le.getMessage();
        this.country = country;
    }

    public long getTime() {
        return time;
    }

    public Object getMessage() {
        return message;
    }

    public String getCountry() {
        return country;
    }

}
