package com.liuqi.acode.base.common;

import com.liuqi.common.exception.BaseErrorCodes;

/**
 * 错误信息
 */
public enum ErrorCodes implements BaseErrorCodes {
    // ---------------------------机构相关异常
    // 机构编码已存在
    BASE_DEPT_CODE_EXISTS("base.dept.code.exists"),
    // 机构不存在
    BASE_DEPT_NOT_EXISTS("base.dept.not.exists"),
    BASE_DEPT_NAME_DUPLICATED_IN_PARENT("base.dept.name.parent.duplicated"),
    BASE_DEPT_HAS_CHIlDREN("base.dept.has.children"),

    // ---------------------------用户相关异常
    BASE_USER_USERNAME_OR_PHONE_EXISTS("base.user.username.or.phone.exists"),
    BASE_USER_PHONE_EXISTS("base.user.phone.exists"),

    // ----------------------------字典相关异常
    BASE_DICT_CODE_EXISTS("base.dict.code.exists"),

    BASE_DICT_NAME_EXISTS("base.dict.name.exists"),

    BASE_DICT_ITEM_EXISTS("base.dict.item.exists"),

    // -----------------------------菜单相关异常
    BASE_MENU_CODE_EXISTS("base.menu.code.exists"),

    // 同一主菜单下子菜单名称重复
    BASE_MENU_NAME_EXISTS("base.menu.name.exists"),

    // ------------------------------角色相关异常
    BASE_ROLE_EXISTS("base.role.exists"),
    BASE_ROLE_CODE_EXISTS("base.role.code.exists"),
    BASE_ROLE_NAME_EXISTS("base.role.name.exists"),

    // -------------------------------系统配置项相关异常
    BASE_CONFIG_CODE_EXISTS("base.config.code.exists"),

    ;

    private final String code;

    ErrorCodes(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
