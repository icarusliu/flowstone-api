package com.liuqi.common.utils.enhancer;

import com.alibaba.fastjson2.JSONObject;
import com.liuqi.common.utils.value.BiValue;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * List增强器
 *
 * @author LiuQi 2019/10/24-11:17
 * @version V1.0
 **/
public class ListEnhancer<T> {
    private List<T> list;

    private ListEnhancer(List<T> list) {
        this.list = list;
    }

    /**
     * 构建增强器对象
     *
     * @param <T> 列表中存储的数据类型
     * @return 增强器对象
     */
    public static <T> ListEnhancer<T> create() {
        return new ListEnhancer<>(new ArrayList<>(16));
    }

    /**
     * 使用指定列表构建增强器对象
     * 如果传入的list为空，那么将创建一个新的List并进行构建
     *
     * @param list 指定列表
     * @param <T>  列表中存储的对象类型
     * @return 增强器对象
     */
    public static <T> ListEnhancer<T> of(List<T> list) {
        return new ListEnhancer<>(null == list ? new ArrayList<>(0) : list);
    }

    /**
     * 构建空的构建器对象
     *
     * @param <T> 列表中存储的对象类型
     * @return 增强器对象
     */
    public static <T> ListEnhancer<T> empty() {
        return create();
    }

    /**
     * 如果列表为空(null或者size为0）的时候执行指定函数
     *
     * @param runnable 为空时需要执行的函数
     * @return 增强器对象
     */
    public ListEnhancer<T> ifEmpty(Runnable runnable) {
        if (CollectionUtils.isEmpty(list)) {
            runnable.run();
        }

        return this;
    }

    public ListEnhancer<T> ifEmptySet(Supplier<List<T>> supplier) {
        if (CollectionUtils.isEmpty(list)) {
            list = supplier.get();
        }

        return this;
    }

    public ListEnhancer<T> ifEmptySet(List<T> defValue) {
        if (CollectionUtils.isEmpty(list)) {
            list = defValue;
        }

        return this;
    }

    /**
     * 如果列表不为空时执行指定函数（不为null并且size不为0）
     *
     * @param consumer 需要执行的函数
     * @return 增强器对象
     */
    public ListEnhancer<T> ifNotEmpty(Consumer<List<T>> consumer) {
        if (!CollectionUtils.isEmpty(list)) {
            consumer.accept(list);
        }

        return this;
    }

    /**
     * 打印列表对象
     * 如果传入的消息中不包含有{}，那么将会自动加上: {}；
     * 用于实现以下代码的快捷处理：
     * if (logger.isDebugEnabled()) {
     * logger.debug("测试处理结果：{}", list);
     * }
     *
     * @param logger  需要打印的日志对象
     * @param message 需要打印的消息
     * @return 增强器对象
     */
    public ListEnhancer<T> debug(
            Logger logger,
            String message) {
        if (logger.isDebugEnabled()) {
            if (!message.contains("{}")) {
                message = message + ": {}";
            }
            logger.debug(message, JSONObject.toJSONString(list));
        }

        return this;
    }

    /**
     * 遍历列表中的对象
     *
     * @param consumer 需要执行函数
     * @return 增强器对象
     */
    public ListEnhancer<T> forEach(Consumer<T> consumer) {
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(consumer);
        }

        return this;
    }

    /**
     * 执行指定函数
     *
     * @param consumer 函数
     * @return 当前对象
     */
    public ListEnhancer<T> peek(Consumer<List<T>> consumer) {
        if (null== consumer || CollectionUtils.isEmpty(list)) {
            return this;
        }

        consumer.accept(list);

        return this;
    }

    public Optional<T> findFirst() {
        if (CollectionUtils.isEmpty(list)) {
            return Optional.empty();
        }

        return Optional.of(list.get(0));
    }

    public Optional<T> findLast() {
        if (CollectionUtils.isEmpty(list)) {
            return Optional.empty();
        }

        return Optional.of(list.get(list.size() - 1));
    }

    /**
     * 获取分页数据
     * 如果page或者size为空，则获取所有数据
     */
    public List<T> getPageList(Integer page, Integer size) {
        if (null == page || null == size) {
            return list;
        }

        int start = (page - 1) * size,
                end = page * size;
        if (start >= list.size()) {
            return new ArrayList<>(0);
        } else {
            end = Math.min(list.size(), end);
            return list.subList(start, end);
        }
    }

    /**
     * 增加元素
     *
     * @param t 需要增加的元素，如果为空则不会增加
     * @return 增强器对象
     */
    public ListEnhancer<T> add(T t) {
        if (null != t) {
            list.add(t);
        }

        return this;
    }

    /**
     * 如果对象在列表中不存在，那么新增指定对象到列表中去
     *
     * @param t 需要增加的对象
     * @return 增强器对象
     */
    public ListEnhancer<T> addWhenNotContains(T t) {
        if (!list.contains(t)) {
            list.add(t);
        }

        return this;
    }

    /**
     * 对列表中的数据进行排序
     *
     * @param comparator 排序规则
     * @return 增强器对象
     */
    public ListEnhancer<T> sort(Comparator<T> comparator) {
        list.sort(comparator);
        return this;
    }

    /**
     * 去重
     */
    public ListEnhancer<T> distinct() {
        list = list.stream().distinct().collect(Collectors.toList());
        return this;
    }

    /**
     * 清空列表数据
     *
     * @return 增强器对象
     */
    public ListEnhancer<T> clear() {
        list.clear();

        return this;
    }

    /**
     * 根据条件删除元素
     *
     * @param predicate 条件
     * @return 增强器对象
     */
    public ListEnhancer<T> remove(Predicate<T> predicate) {
        list.removeIf(predicate);
        return this;
    }

    /**
     * 如果对象在列表中存在，那么将其从列表中删除
     *
     * @param t 对象
     * @return 增强器对象
     */
    public ListEnhancer<T> remove(T t) {
        list.remove(t);

        return this;
    }

    /**
     * 过滤列表中的元素
     *
     * @param predicate 检查函数
     * @return 当前对象
     */
    public ListEnhancer<T> filter(Predicate<T> predicate) {
        if (!CollectionUtils.isEmpty(list)) {
            list.removeIf(t -> !predicate.test(t));
        }

        return this;
    }

    /**
     * 转换增强器列表中的对象
     *
     * @param function 转换函数
     * @param <R>      转换后的列表中存储的对象类型
     * @return 增强器对象
     */
    public <R> ListEnhancer<R> map(Function<T, R> function) {
        return new ListEnhancer<>(list.stream().map(function).collect(Collectors.toList()));
    }

    /**
     * 将列表按照对应的分类函数进行指定的归并<br/>
     * 处理归并后对象与原对象相同的情况，不能做归并的对象仍旧会保存到结果集合中；如果某元素能做归并，那么在结果集中将只会有归并后的对象而不会留存原有的归并前的对象
     *
     * @param toClassify       元素是否要做归并，不需要做归并的元素将直接被添加到结果集中
     * @param classifyFunction 分类函数
     * @param reduceFunction   归并函数
     * @return 分类归并后的新的增强器对象
     */
    public ListEnhancer<T> classify(Predicate<T> toClassify, Function<T, String> classifyFunction, BiFunction<T, T, T> reduceFunction) {
        Map<String, T> map = new HashMap<>(16);

        // 先按classifyFunction获取key，并将key相同的做归并
        list.forEach(item -> {
            if (null == toClassify || !toClassify.test(item)) {
                return;
            }

            String key = classifyFunction.apply(item);
            if (map.containsKey(key)) {
                map.put(key, reduceFunction.apply(item, map.get(key)));
            } else {
                map.put(key, item);
            }
        });

        // 获取最终的结果
        List<T> resultList = new ArrayList<>(16);
        list.forEach(item -> {
            String key = classifyFunction.apply(item);
            key = null == key ? "" : key;

            T resultItem = map.get(key);
            if (null != resultItem && !resultList.contains(resultItem)) {
                // 需要做归并，将归并后的结果添加到结果集中
                resultList.add(resultItem);
            } else if (null == resultItem) {
                // 表明不需要做归并，那么直接添加到结果集中
                resultList.add(item);
            }
        });

        return ListEnhancer.of(resultList);
    }

    /**
     * 将列表按照对应的分类函数进行指定的归并
     *
     * @param classifyFunction 分类函数
     * @param function         归并函数
     * @return 分类归并后的新的增强器对象
     */
    public ListEnhancer<T> classify(Function<T, String> classifyFunction, BiFunction<T, T, T> function) {
        return classify(null, classifyFunction, function);
    }

    /**
     * 将列表数据按关键字做归类
     *
     * @param keyFunction 关键字生成函数
     * @param <K>         关键字类型
     * @return 归并结果，每个关键字在结果中对应一个关键字相同的列表
     */
    public <K> Map<K, List<T>> classify(Function<T, K> keyFunction) {
        Map<K, List<T>> resultMap = new HashMap<>(16);
        list.forEach(t -> {
            K key = keyFunction.apply(t);
            resultMap.computeIfAbsent(key, n -> new ArrayList<>(16))
                    .add(t);
        });

        return resultMap;
    }

    /**
     * 批量转换
     *
     * @param function  批量转换函数
     * @param batchSize 每次处理记录数
     * @param <R>       处理结果对象类型
     * @return 处理后新的增强器对象
     */
    public <R> ListEnhancer<R> batchMap(Function<List<T>, List<R>> function, int batchSize) {
        int size = list.size(), start = 0, end = batchSize;
        List<R> resultList = new ArrayList<>(16);
        while (start < size) {
            end = Math.min(size, end);
            List<R> subResultList = function.apply(list.subList(start, end));
            if (!CollectionUtils.isEmpty(subResultList)) {
                resultList.addAll(subResultList);
            }

            start += batchSize;
            end = start + batchSize;
        }

        return ListEnhancer.of(resultList);
    }

    /**
     * 批量执行某个函数
     *
     * @param consumer  需要批量执行的函数
     * @param batchSize 每次执行的记录数
     * @return 当前增强器对象
     */
    public ListEnhancer<T> batchRun(Consumer<List<T>> consumer, int batchSize) {
        int size = list.size(), start = 0, end = batchSize;
        while (start < size) {
            end = Math.min(size, end);
            consumer.accept(list.subList(start, end));

            start += batchSize;
            end = start + batchSize;
        }

        return this;
    }

    /**
     * 执行收集函数
     *
     * @param consumer 收集函数
     * @param <R>      收集后的数据对象类型
     * @return 收集结果
     */
    public <R> R collect(Collector<T, T, R> consumer) {
        return list.stream()
                .collect(consumer);
    }

    /**
     * 执行reduce
     *
     * @param operator reduce函数
     * @return 结果
     */
    public Optional<T> reduce(BinaryOperator<T> operator) {
        return list.stream()
                .reduce(operator);
    }

    /**
     * 将List转换成Map
     * 如[{"a": 1, "b": 2}, {"a": 2, "b": 3}]可转换成{"1": "2", "2": "3"}
     *
     * @param <K>      Map中的键类型
     * @param <V>      Map中的值类型
     * @param function 转换函数
     * @return 转换后的Map
     */
    @SuppressWarnings("WeakerAccess")
    public <K, V> Map<K, V> toMap(Function<T, BiValue<K, V>> function) {
        Map<K, V> map = new HashMap<>(16);
        list.forEach(item -> {
            BiValue<K, V> biValue = function.apply(item);
            if (null != biValue) {
                map.put(biValue.getKey(), biValue.getValue());
            }
        });

        return map;
    }

    /**
     * 将List转换成Value为List类型的Map
     *
     * @param function 转换函数
     * @param <K>      Map中的Key类型
     * @param <V>      Map中的Value元素List中包含有元素类型
     * @return 转换结果
     */
    public <K, V> Map<K, List<V>> toListMap(Function<T, BiValue<K, V>> function) {
        Map<K, List<V>> map = new HashMap<>(16);
        list.forEach(item -> {
            BiValue<K, V> biValue = function.apply(item);
            if (null == biValue) {
                return;
            }

            map.computeIfAbsent(biValue.getKey(), n -> new ArrayList<>(16))
                    .add(biValue.getValue());
        });

        return map;
    }

    /**
     * List增强器转换成Map增强器
     */
    public <K, V> MapEnhancer<K, V> toMapEnhancer(Function<T, BiValue<K, V>> function) {
        return MapEnhancer.of(toMap(function));
    }

    /**
     * 获取增强器中存储的列表
     *
     * @return 增强器中存储的列表
     */
    public List<T> get() {
        return this.list;
    }

    /**
     * 获取增强器中值，如果值为空，则返回默认值
     *
     * @param list 默认值
     * @return 增强中的值或者默认值
     */
    public List<T> orElse(List<T> list) {
        return orElse(() -> list);
    }

    /**
     * 获取增强器中值，如果值为空，则返回默认值
     *
     * @param supplier 默认值提供者
     * @return 增强中的值或者默认值
     */
    @SuppressWarnings("WeakerAccess")
    public List<T> orElse(Supplier<List<T>> supplier) {
        if (0 == this.list.size()) {
            return supplier.get();
        }

        return this.list;
    }
}
