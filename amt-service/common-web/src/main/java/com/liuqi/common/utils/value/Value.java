package com.liuqi.common.utils.value;

import java.util.function.Consumer;

/**
 * 单值对象
 *
 * @author  LiuQi 2024/8/30-14:02
 * @version V1.0
 **/
public class Value<T> {
    protected T value;

    public T getValue() {
        return value;
    }

    public Value<T> setValue(T t) {
        this.value = t;
        return this;
    }

    public boolean equal(T t) {
        return this.value == t || this.value.equals(t);
    }

    public void ifPresent(Consumer<T> consumer) {
        if (null == value) {
            return;
        }

        consumer.accept(value);
    }

    public boolean isPresent() {
        return value != null;
    }
}
