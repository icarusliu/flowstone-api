package com.liuqi.common.utils.enhancer;

import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 对象增强器
 *
 * @author LiuQi 2019/10/24-14:07
 * @version V1.0
 **/
public class ObjectEnhancer<T> {
    private T t;

    private ObjectEnhancer(T t) {
        this.t = t;
    }

    /**
     * 构建增强器对象
     *
     * @param t   对象值
     * @param <T> 对象类型
     * @return 增强器对象
     */
    public static <T> ObjectEnhancer<T> of(T t) {
        return new ObjectEnhancer<>(t);
    }

    /**
     * 获取增强器中存储的值
     *
     * @return 增强器对象
     */
    public T get() {
        return t;
    }

    /**
     * 获取增强器中值
     * 如果值为null，则返回默认值
     *
     * @param defValue 默认值
     * @return 值或者默认值
     */
    public T get(T defValue) {
        return null == t ? defValue : t;
    }

    /**
     * 获取增强器中值
     * 如果值为null，则返回默认值
     *
     * @param defValueSupplier 默认值生成器
     * @return 值或者默认值
     */
    public T get(Supplier<T> defValueSupplier) {
        return null == t ? defValueSupplier.get() : t;
    }

    /**
     * 获取值
     * 如果值满足指定条件，那么返回supplier提供的值
     * 否则返回原值
     *
     * @param predicate 条件
     * @param supplier  满足条件时的值生成器
     * @return 原值或者新值
     */
    public T get(
            Predicate<T> predicate,
            Supplier<T> supplier) {
        return predicate.test(t) ? supplier.get() : t;
    }

    /**
     * 如果存储的值为null，则执行指定函数
     *
     * @param runnable 需要执行的函数
     * @return 增强器对象
     */
    public ObjectEnhancer<T> ifNull(Runnable runnable) {
        if (null == t) {
            runnable.run();
        }

        return this;
    }

    /**
     * 如果存储的值为null，那么为其指定默认值
     *
     * @param defValue 默认值
     * @return 增强器对象
     */
    public ObjectEnhancer<T> ifNullSet(T defValue) {
        if (null == t) {
            t = defValue;
        }

        return this;
    }

    /**
     * 如果存储的是null，那么为其指定默认值
     *
     * @param supplier 默认值生成器
     * @return 增强器对象
     */
    public ObjectEnhancer<T> ifNullSet(Supplier<T> supplier) {
        if (null == t) {
            t = supplier.get();
        }

        return this;
    }

    /**
     * 如果值为null，则抛出异常
     *
     * @param supplier 异常提供者
     * @return 增强器对象
     */
    public ObjectEnhancer<T> ifNull(Supplier<? extends Throwable> supplier) throws Throwable {
        if (null == t) {
            throw supplier.get();
        }

        return this;
    }

    /**
     * 如果值不为null，则执行指定函数
     *
     * @param consumer 需要执行的函数
     * @return 增强器对象
     */
    public ObjectEnhancer<T> ifNotNull(Consumer<T> consumer) {
        if (null != t) {
            consumer.accept(t);
        }

        return this;
    }

    /**
     * 如果值满足某个条件，则执行指定函数
     *
     * @param predicate 需要满足的条件
     * @param consumer  需要执行的函数
     * @return 增强器对象
     */
    public ObjectEnhancer<T> ifTest(
            Predicate<T> predicate,
            Consumer<T> consumer) {
        if (predicate.test(t)) {
            consumer.accept(t);
        }

        return this;
    }

    /**
     * 值对象转换
     *
     * @param function 转换函数
     * @param <R>      转换后的对象类型
     * @return 新的转换器对象
     */
    public <R> ObjectEnhancer<R> map(Function<T, R> function) {
        if (null != t) {
            return ObjectEnhancer.of(function.apply(t));
        }

        return ObjectEnhancer.of(null);
    }

    /**
     * 当对象满足某个条件时进行转换
     *
     * @param predicate 条件函数
     * @param function  转换函数
     * @param <R>       转换后的对象类型
     * @return 新的转换器对象
     */
    public <R> ObjectEnhancer<R> mapIf(
            Predicate<T> predicate,
            Function<T, R> function) {
        if (predicate.test(t)) {
            return ObjectEnhancer.of(function.apply(t));
        }

        return ObjectEnhancer.of(null);
    }

    /**
     * 将当前对象添加到List中并返回
     */
    public List<T> toList() {
        if (null == t) {
            return Collections.emptyList();
        }
        return Collections.singletonList(t);
    }

    /**
     * 打印debug信息
     * 用于替换以下场景：
     * if (logger.isDebugEnabled()) {
     * logger.debug("测试对象：{}", t);
     * }
     * 如果message中不包含有{}，将会自动在其后增加: {}
     *
     * @param logger  日志对象
     * @param message 需要打印的消息
     * @return 增强器对象
     */
    public ObjectEnhancer<T> debug(
            Logger logger,
            String message) {
        if (logger.isDebugEnabled()) {
            if (!message.contains("{}")) {
                message = message + ": {}";
            }
            logger.debug(message, null == t ? "null" : JSONObject.toJSONString(t));
        }

        return this;
    }

    /**
     * 打印info信息
     *
     * @param logger  日志对象
     * @param message 需要打印的消息
     * @return 增强器对象
     */
    public ObjectEnhancer<T> info(
            Logger logger,
            String message) {
        if (!message.contains("{}")) {
            message = message + ": {}";
        }
        logger.info(message, null == t ? "null" : JSONObject.toJSONString(t));

        return this;
    }
}
