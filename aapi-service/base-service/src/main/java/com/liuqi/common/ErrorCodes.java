package com.liuqi.common;

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

    // -------------------------------接口配置相关异常
    API_CONTENT_EMPTY("D01001", "接口配置为空"),


    API_NODES_EMPTY("D01002", "接口节点配置为空"),
    API_INVALID_NODE_TYPE("D01003", "接口节点类型无效"),
    API_CALL_ERROR("D01004", "接口调用异常"),
    API_PATH_DUPLIATED("D01005", "接口路径重复"),
    API_NOT_EXISTS("D01006", "接口不存在"),
    DS_TABLE_NOT_EXISTS("D01007", "表不存在"),

    API_SUPPLIER_NOT_EXISTS("D01008", "接入方不存在");

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
