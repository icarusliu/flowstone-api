package com.liuqi.base.bean.dto;

import com.liuqi.common.base.bean.dto.BaseDTO;
import com.liuqi.common.base.bean.dto.TreeNode;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;


@Data
public class DeptDTO extends TreeNode<DeptDTO>{
    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String parentId;

    private Integer sort;

    private List<DeptDTO> children;
}