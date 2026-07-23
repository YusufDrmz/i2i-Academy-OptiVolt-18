package com.i2i.optivolt.rules;

public interface AlertPublisher {
    void publish(AlertEvent event);
}
