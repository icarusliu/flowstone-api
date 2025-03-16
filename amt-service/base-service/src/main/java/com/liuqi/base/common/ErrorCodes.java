package com.liuqi.base.common;

import com.liuqi.common.exception.BaseErrorCodes;

/**
 * 错误信息
 */
public enum ErrorCodes implements BaseErrorCodes {
    // ---------------------------机构相关异常
    // 机构编码已存在
    // ---------------------------机构相关异常
    // 机构编码已存在
    BASE_DEPT_CODE_EXISTS("C01001", "机构编码重复"),
    // 机构不存在
    BASE_DEPT_NOT_EXISTS("C01002", "机构不存在"),
    BASE_DEPT_NAME_DUPLICATED_IN_PARENT("C01003", "机构名称重复"),
    BASE_DEPT_HAS_CHIlDREN("C01004", "机构下存在子机构"),

    // ---------------------------用户相关异常
    BASE_USER_USERNAME_OR_PHONE_EXISTS("C02001", "用户名或手机号重复"),
    BASE_USER_PHONE_EXISTS("C02002", "手机号已存在"),

    // ----------------------------字典相关异常
    BASE_DICT_CODE_EXISTS("C03001", "字典编码重复"),

    BASE_DICT_NAME_EXISTS("C03002", "字典名称重复"),

    BASE_DICT_ITEM_EXISTS("C03003", "字典项重复"),

    // -----------------------------菜单相关异常
    BASE_MENU_CODE_EXISTS("C04001", "菜单编码重复"),

    // 同一主菜单下子菜单名称重复
    BASE_MENU_NAME_EXISTS("C04002", "菜单名称重复"),

    // ------------------------------角色相关异常
    BASE_ROLE_EXISTS("C05001", "角色重复"),
    BASE_ROLE_CODE_EXISTS("C05002", "角色编码重复"),
    BASE_ROLE_NAME_EXISTS("C05003", "角色名称重复"),

    // -------------------------------系统配置项相关异常
    BASE_CONFIG_CODE_EXISTS("C06001", "配置项编码重复"),

    // -------------------------------业务相关异常
    BUSI_WORK_TYPE_NAME_DUPLICATED("D01001", "工作分类名称重复"),
    BUSI_PROJECT_DELETE_FAILED("D01002", "该项目已填报日/月计划，需要先删除项目关联的日、月数据再进行操作"),
    BUSI_PARAM_GROUP_HAS_PARAMS("D01003", "要删除的分组存在关联关系，不可直接删除");

    private final String code;
    private final String msg;

    ErrorCodes(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }


}
