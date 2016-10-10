package com.caij.emore.bean.event;

import java.io.Serializable;

/**
 * Created by Caij on 2016/10/8.
 */

public class Event implements Serializable {

    public String type;

    public Event() {

    }

    public Event(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        Event e = (Event) o;
        return type.equals(e.type);
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
