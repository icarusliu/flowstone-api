package com.liuqi.base.bean.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus {
    INVALID(0),
    VALID(1);

    @EnumValue
    @JsonValue
    private final int value;

    UserStatus(int i) {
        this.value = i;
    }

    public int value() {
        return this.value;
    }
}
