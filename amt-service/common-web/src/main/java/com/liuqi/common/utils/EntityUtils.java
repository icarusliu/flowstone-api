package com.liuqi.common.utils;

import com.liuqi.common.base.bean.dto.BaseDTO;
import com.liuqi.common.base.bean.dto.Tree;
import com.liuqi.common.base.bean.dto.TreeNode;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 实体类辅助工具
 *
 * @author LiuQi 2024/8/10-8:53
 * @version V1.0
 **/
public class EntityUtils {
    /**
     * 将对象转换成树
     * 树节点本身就是对象，只是在其基础上增加children字段
     */
    public static <T extends TreeNode> List<T> toTableTree(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }

        // 根据parentId进行归并
        Map<String, List<T>> childrenMap = list.stream()
                .collect(Collectors.groupingBy(t -> Optional.ofNullable(t.getParentId()).orElse("-1")));

        // 设置每一项的子元素
        list.forEach(item -> {
            String id = item.getId();
            List<T> children = Optional.ofNullable(childrenMap.get(id)).orElse(new ArrayList<>(0));
            item.setChildren(children);
        });


        // 取父元素为顶层元素的列表（即父元素为-1的列表）
        return Optional.ofNullable(childrenMap.get("-1")).orElse(new ArrayList<>(0));
    }

    /**
     * 实体对象转成树形对象
     *
     * @param list         实体对象列表
     * @param labelFunc    树标签获取函数
     * @param parentIdFunc 父级id获取函数
     * @param <T>          对象类型
     * @return 树形列表
     */
    public static <T extends BaseDTO> List<Tree<T>> toTree(List<T> list, Function<T, String> labelFunc,
                                                           Function<T, String> parentIdFunc) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(0);
        }
        
        // 根据parentId进行归并
        List<Tree<T>> items = new ArrayList<>(16);
        Map<String, List<Tree<T>>> childrenMap = list.stream()
                .map(d -> Tree.<T>builder().data(d).id(d.getId()).label(labelFunc.apply(d)).build())
                .peek(items::add)
                .collect(Collectors.groupingBy(t -> Optional.ofNullable(parentIdFunc.apply(t.getData())).orElse("-1")));

        // 设置每一项的子元素
        items.forEach(item -> {
            String id = item.getId();
            List<Tree<T>> children = Optional.ofNullable(childrenMap.get(id)).orElse(new ArrayList<>(0));
            item.setChildren(children);
        });


        // 取父元素为顶层元素的列表（即父元素为-1的列表）
        return Optional.ofNullable(childrenMap.get("-1")).orElse(new ArrayList<>(0));
    }
}
