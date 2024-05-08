package com.liuqi.acode.base.bean.dto;

import java.util.Arrays;

/**
 * 菜单类型
 */
public enum MenuType {
    // 内部相对路径
    PATH("path"),

    // 外部路径
    URL("url");

    MenuType(String code) {
        this.code = code;
    }

    private final String code;

    public String getCode() {
        return code;
    }

    /**
     * 根据编码解析菜单类型
     *
     * @param code 类型编码
     * @return 菜单类型
     */
    public static MenuType parse(String code) {
        return Arrays.stream(MenuType.values()).filter(item -> item.getCode().equals(code))
                .findAny()
                .orElse(PATH);
    }

    /**
     * 根据原值解析菜单类型
     *
     * @param value 原值
     * @return 菜单类型
     */
    public static MenuType parse(Integer value) {
        return Arrays.stream(MenuType.values())
                .filter(item -> item.ordinal() == value)
                .findAny()
                .orElse(PATH);
    }
}
