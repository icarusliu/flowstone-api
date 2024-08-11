package com.liuqi.base.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@Builder
public class RoleDTO extends BaseDTO {
    private String code;
    private String name;
}
