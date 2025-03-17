package com.liuqi.common.utils;

import com.liuqi.common.base.bean.dto.TreeNode;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 实体类辅助工具
 *
 * @author LiuQi 2024/8/10-8:53
 * @version V1.0
 **/
public class TreeUtils {
    /**
     * 遍历树
     *
     * @param list     树
     * @param consumer 处理函数
     * @param <T>      节点类型
     */
    public static <T extends TreeNode<T>> void loopTree(List<T> list, Consumer<T> consumer) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        list.forEach(item -> {
            consumer.accept(item);
            if (!CollectionUtils.isEmpty(item.getChildren())) {
                loopTree(item.getChildren(), consumer);
            }
        });
    }

    /**
     * 将对象转换成树
     * 树节点本身就是对象，只是在其基础上增加children字段
     */
    public static <T extends TreeNode<T>> List<T> toTree(List<T> list) {
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
     * 树类型转换
     *
     * @param list 原树
     * @param func 转换函数
     * @param <T>  原树节点类型
     * @param <R>  新树节点类型
     * @return 新树
     */
    public static <T extends TreeNode<T>, R extends TreeNode<R>> List<R> map(List<T> list, Function<T, R> func) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>(16);
        }

        return list.stream().map(item -> map(item, func)).toList();
    }

    /**
     * 树节点类型转换
     *
     * @param t    节点
     * @param func 转换函数
     * @param <T>  原节点类型
     * @param <R>  转换后节点类型
     * @return 转换结果
     */
    private static <T extends TreeNode<T>, R extends TreeNode<R>> R map(T t, Function<T, R> func) {
        R r = func.apply(t);
        if (CollectionUtils.isEmpty(t.getChildren())) {
            return r;
        }

        r.setChildren(t.getChildren().stream().map(sub -> map(sub, func)).toList());
        return r;
    }
}
