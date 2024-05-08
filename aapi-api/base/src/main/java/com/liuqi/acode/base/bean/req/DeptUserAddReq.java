package com.liuqi.acode.base.bean.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class DeptUserAddReq {
    @NotBlank
    private String deptId;

    @NotEmpty
    private List<String> userIds;
}