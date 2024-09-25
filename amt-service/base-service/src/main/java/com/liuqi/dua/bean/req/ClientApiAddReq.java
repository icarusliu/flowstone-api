package com.liuqi.dua.bean.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 客户端接口列表新增对象 
 * @author Coder Generator 2024-09-25 15:58:18 
 **/
@Data
public class ClientApiAddReq {
    @NotBlank
    private String clientId;

    @NotEmpty
    private List<String> apiIds;
}