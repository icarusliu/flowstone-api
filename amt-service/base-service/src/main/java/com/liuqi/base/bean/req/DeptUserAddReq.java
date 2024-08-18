package com.liuqi.base.bean.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class DeptUserAddReq {
    @NotBlank
    private String deptId;

    @NotEmpty
    private List<String> userIds;
}