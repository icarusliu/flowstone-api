package com.liuqi.common.utils.enhancer;

import com.alibaba.fastjson2.JSONObject;
import com.liuqi.common.utils.value.BiValue;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.*;

/**
 * Map增强器
 *
 * @author LiuQi 2019/10/24-20:11
 * @version V1.0
 **/
public class MapEnhancer<K, V> {
    private Map<K, V> map;

    private MapEnhancer(Map<K, V> map) {
        this.map = null == map ? new HashMap<>(16) : map;
    }

    /**
     * 创建常规的<String, Object>的MapEnhancer对象
     */
    public static MapEnhancer<String, Object> createNormal() {
        return new MapEnhancer<>(new HashMap<>());
    }

    public static <K, V> MapEnhancer<K, V> create() {
        return new MapEnhancer<>(new HashMap<>(16));
    }

    public static <K, V> MapEnhancer<K, V> of(Map<K, V> map) {
        return new MapEnhancer<>(map);
    }

    /**
     * 根据Bean创建增强器对象
     *
     * @param t   Bean类
     * @param <T> Bean类型
     * @return 增强器对象
     */
    public static <T> MapEnhancer<String, Object> ofBean(T t) {
        MapEnhancer<String, Object> mapEnhancer = new MapEnhancer<>(new HashMap<>(16));
        if (null != t) {
            String jsonStr = JSONObject.toJSONString(t);
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            mapEnhancer.map.putAll(jsonObject);
        }

        return mapEnhancer;
    }

    public Map<K, V> get() {
        return map;
    }

    /**
     * 如果增强器中的Map为空，则将增强器中的值赋予提供器提供的Map
     *
     * @param supplier 提供器
     * @return 当前对象
     */
    public MapEnhancer<K, V> ifEmptySet(Supplier<Map<K, V>> supplier) {
        if (null == map || 0 == map.size()) {
            map = supplier.get();
        }

        return this;
    }

    /**
     * 如果增强器中的map为空，则将指定值赋予增强器中的值
     */
    public MapEnhancer<K, V> ifEmptySet(Map<K, V> devValue) {
        if (null == map || 0 == map.size()) {
            this.map = devValue;
        }

        return this;
    }

    /**
     * 如果map为空，执行指定函数
     */
    public MapEnhancer<K, V> ifEmpty(Runnable runnable) {
        if (null == map || 0 == map.size()) {
            runnable.run();
        }

        return this;
    }

    /**
     * 如果map为空，抛出指定异常
     */
    public MapEnhancer<K, V> ifEmpty(Supplier<RuntimeException> exceptionSupplier) {
        throw exceptionSupplier.get();
    }

    /**
     * 如果map不为空，进行指定消费
     */
    public MapEnhancer<K, V> ifNotEmpty(Consumer<Map<K, V>> consumer) {
        if (null == map || 0 == map.size()) {
            consumer.accept(map);
        }

        return this;
    }

    /**
     * 如果key对应的值不为空，则执行指定消费
     */
    public MapEnhancer<K, V> ifValueNotNull(K key, Consumer<V> consumer) {
        if (null != map.get(key)) {
            consumer.accept(map.get(key));
        }

        return this;
    }

    /**
     * 将健值对添加到map中
     *
     * @param k 健
     * @param v 值
     * @return 当前对象
     */
    public MapEnhancer<K, V> put(K k, V v) {
        this.map.put(k, v);
        return this;
    }

    /**
     * 如果键值均不为空则进行put操作
     *
     * @param k 键
     * @param v 值
     * @return 当前对象
     */
    public MapEnhancer<K, V> putNotNull(K k, V v) {
        if (null != k && null != v) {
            this.map.put(k, v);
        }

        return this;
    }

    /**
     * 如果k对应的值满足条件，则使用新值替换
     */
    public MapEnhancer<K, V> putIf(K k, V v, Predicate<V> predicate) {
        if (predicate.test(map.get(k))) {
            map.put(k, v);
        }

        return this;
    }

    /**
     * 如果key对应的Value不存在或者为空，则使用生成器生成新value，与key一起put到map中去
     *
     * @param key      健
     * @param supplier 值提供者
     * @return 当前对象
     */
    @SuppressWarnings("WeakerAccess")
    public MapEnhancer<K, V> putIfNull(K key, Supplier<V> supplier) {
        if (null == map.get(key)) {
            map.put(key, supplier.get());
        }

        return this;
    }

    /**
     * 如果key对应的Value不存在或者为空，则进行put操作
     *
     * @param key 健
     * @param v   值
     * @return 当前对象
     */
    public MapEnhancer<K, V> putIfNull(K key, V v) {
        return putIfNull(key, () -> v);
    }

    /**
     * 修改key对应的值
     *
     * @param k              key
     * @param changeFunction 修改函数
     * @return 当前对象
     */
    public MapEnhancer<K, V> change(K k, Function<V, V> changeFunction) {
        V v = map.get(k);
        V newV = changeFunction.apply(v);
        map.put(k, newV);

        return this;
    }

    /**
     * 删除指定值
     */
    public MapEnhancer<K, V> remove(K key) {
        map.remove(key);
        return this;
    }

    /**
     * 如果满足某个条件则删除指定值
     */
    public MapEnhancer<K, V> remove(K key, Predicate<V> predicate) {
        V v = map.get(key);
        if (predicate.test(v)) {
            map.remove(key);
        }

        return this;
    }

    /**
     * 元素过滤
     */
    public MapEnhancer<K, V> filter(Predicate<V> predicate) {
        map.keySet().removeIf(k -> predicate.test(map.get(k)));

        return this;
    }

    /**
     * 元素过滤
     */
    public MapEnhancer<K, V> filter(BiPredicate<K, V> predicate) {
        map.keySet().removeIf(k -> predicate.test(k, map.get(k)));
        return this;
    }

    /**
     * 清空值
     */
    public MapEnhancer<K, V> clear() {
        map.clear();
        return this;
    }

    /**
     * 遍历值
     */
    public MapEnhancer<K, V> forEach(BiConsumer<K, V> consumer) {
        map.forEach(consumer);
        return this;
    }

    /**
     * 根据Key获取字符串值并进行处理
     */
    public MapEnhancer<K, V> peekString(K k, Consumer<String> consumer) {
        consumer.accept(MapUtils.getString(map, k));
        return this;
    }

    /**
     * 根据Key获取字符串值并进行处理
     * 如果map中值为null时将使用默认值进行处理
     */
    public MapEnhancer<K, V> peekString(K k, Consumer<String> consumer, String defValue) {
        consumer.accept(MapUtils.getString(map, k, defValue));
        return this;
    }

    /**
     * 根据Key获取值并进行处理
     */
    public MapEnhancer<K, V> peekInteger(K k, Consumer<Integer> consumer) {
        consumer.accept(MapUtils.getInteger(map, k));
        return this;
    }

    /**
     * 根据Key获取值并进行处理
     * 如果key对应的值是null时将使用默认值
     */
    public MapEnhancer<K, V> peekInteger(K k, Consumer<Integer> consumer, Integer defValue) {
        consumer.accept(MapUtils.getInteger(map, k, defValue));
        return this;
    }

    /**
     * 根据Key获取值并进行处理
     * 如果key对应的值是null时将使用0
     */
    public MapEnhancer<K, V> peekIntValue(K k, Consumer<Integer> consumer) {
        consumer.accept(MapUtils.getIntValue(map, k));
        return this;
    }

    /**
     * 根据Key获取值并进行处理
     */
    public MapEnhancer<K, V> peekLong(K k, Consumer<Long> consumer) {
        consumer.accept(MapUtils.getLong(map, k));
        return this;
    }

    /**
     * 根据Key获取值并进行处理
     * 如果key对应的值是null时将使用默认值
     */
    public MapEnhancer<K, V> peekLong(K k, Consumer<Long> consumer, Long defValue) {
        consumer.accept(MapUtils.getLong(map, k, defValue));
        return this;
    }

    /**
     * 根据Key获取值并进行处理
     * 如果key对应的值是null时将使用0
     */
    public MapEnhancer<K, V> peekLongValue(K k, Consumer<Long> consumer) {
        consumer.accept(MapUtils.getLongValue(map, k));
        return this;
    }

    /**
     * 根据Key获取值并进行处理
     */
    public MapEnhancer<K, V> peekFloat(K k, Consumer<Float> consumer) {
        consumer.accept(MapUtils.getFloat(map, k));
        return this;
    }

    /**
     * 根据Key获取值并进行处理
     * 如果key对应的值是null时将使用默认值
     */
    public MapEnhancer<K, V> peekFloat(K k, Consumer<Float> consumer, Float defValue) {
        consumer.accept(MapUtils.getFloat(map, k, defValue));
        return this;
    }

    /**
     * 根据Key获取值并进行处理
     * 如果key对应的值是null时将使用0
     */
    public MapEnhancer<K, V> peekFloatValue(K k, Consumer<Float> consumer) {
        consumer.accept(MapUtils.getFloatValue(map, k));
        return this;
    }

    /**
     * 根据Key获取值并进行处理
     */
    public MapEnhancer<K, V> peekDouble(K k, Consumer<Double> consumer) {
        consumer.accept(MapUtils.getDouble(map, k));
        return this;
    }

    /**
     * 根据Key获取值并进行处理
     * 如果key对应的值是null时将使用默认值
     */
    public MapEnhancer<K, V> peekDouble(K k, Consumer<Double> consumer, Double defValue) {
        consumer.accept(MapUtils.getDouble(map, k, defValue));
        return this;
    }

    /**
     * 根据Key获取值并进行处理
     * 如果key对应的值是null时将使用0
     */
    public MapEnhancer<K, V> peekDoubleValue(K k, Consumer<Double> consumer) {
        consumer.accept(MapUtils.getDoubleValue(map, k));
        return this;
    }

    /**
     * 根据Key获取值并进行处理
     */
    public MapEnhancer<K, V> peekList(K k, Consumer<List<?>> consumer) {
        List<?> list = (List<?>) map.get(k);
        consumer.accept(list);

        return this;
    }

    /**
     * 根据Key获取值并进行处理
     * 如果key对应的值是null时将使用空的列表
     */
    public MapEnhancer<K, V> peekListValue(K k, Consumer<List<?>> consumer) {
        List<?> list = (List<?>) map.get(k);
        consumer.accept(null == list ? new ArrayList<>(0) : list);

        return this;
    }

    /**
     * 根据Key获取值并进行处理
     */
    public MapEnhancer<K, V> peekMap(K k, Consumer<Map<?, ?>> consumer) {
        Map<?, ?> mapValue = (Map<?, ?>) map.get(k);
        consumer.accept(mapValue);

        return this;
    }

    /**
     * 根据Key获取值并进行处理
     * 如果key对应的值是null时将使用空的Map
     */
    public MapEnhancer<K, V> peekMapValue(K k, Consumer<Map<?, ?>> consumer) {
        Map<?, ?> mapValue = (Map<?, ?>) map.get(k);
        consumer.accept(null == mapValue ? new HashMap<>(0) : mapValue);

        return this;
    }

//    /**
//     * 获取LocalDate类型的值并进行消费
//     */
//    public MapEnhancer<K, V> peekLocalDate(K k, Consumer<LocalDate> consumer) {
//        // 从map中获取日期
//        V value = map.get(k);
//        Date date = TypeUtils.castToDate(value);
//        LocalDate localDate = DateUtils.toLocalDate(date);
//        consumer.accept(localDate);
//        return this;
//    }

//    /**
//     * 获取LocalDate类型的值并进行消费
//     * 如果值为空则不进行消费
//     */
//    public MapEnhancer<K, V> peekLocalDateIf(K k, Consumer<LocalDate> consumer) {
//        V value = map.get(k);
//        Date date = TypeUtils.castToDate(value);
//        if (null != date) {
//            LocalDate localDate = DateUtils.toLocalDate(date);
//            consumer.accept(localDate);
//        }
//        return this;
//    }

//    /**
//     * 获取LocalDateTime的值并进行消费
//     */
//    public MapEnhancer<K, V> peekLocalDateTime(K k, Consumer<LocalDateTime> consumer) {
//        V value = map.get(k);
//        Date date = TypeUtils.castToDate(value);
//        LocalDateTime time = DateUtils.toLocalDateTime(date);
//        consumer.accept(time);
//
//        return this;
//    }
//
//    /**
//     * 获取LocalDateTime的值并进行消费
//     * 值为空时不进行消费
//     */
//    public MapEnhancer<K, V> peekLocalDateTimeIf(K k, Consumer<LocalDateTime> consumer) {
//        V value = map.get(k);
//        Date date = TypeUtils.castToDate(value);
//        if (null != date) {
//            LocalDateTime time = DateUtils.toLocalDateTime(date);
//            consumer.accept(time);
//        }
//
//        return this;
//    }
//
//    /**
//     * 获取Date值并进行消费
//     */
//    public MapEnhancer<K, V> peekDate(K k, Consumer<Date> consumer) {
//        V value = map.get(k);
//        Date date = TypeUtils.castToDate(value);
//        consumer.accept(date);
//        return this;
//    }
//
//    /**
//     * 获取Date值并进行消费
//     * 如果值为空不时行消费
//     */
//    public MapEnhancer<K, V> peekDateIf(K k, Consumer<Date> consumer) {
//        V value = map.get(k);
//        Date date = TypeUtils.castToDate(value);
//        if (null != date) {
//            consumer.accept(date);
//        }
//        return this;
//    }

    /**
     * 消费值集合
     */
    public MapEnhancer<K, V> peek(Consumer<Map<K, V>> consumer) {
        consumer.accept(map);
        return this;
    }

    /**
     * 值转换
     */
    public <K1, V1> MapEnhancer<K1, V1> map(Function<Map<K, V>, Map<K1, V1>> function) {
        return of(function.apply(map));
    }

    /**
     * 将当前Map转换成一个一个元素的列表
     *
     * @return 转换结果
     */
    @SuppressWarnings("WeakerAccess")
    public List<Map<K, V>> toList() {
        List<Map<K, V>> list = new ArrayList<>(1);
        list.add(map);
        return list;
    }

    /**
     * 将当前的Map增强器转换成一个包含一个元素的List增强器
     */
    public ListEnhancer<Map<K, V>> toListEnhancer() {
        return ListEnhancer.of(toList());
    }

    /**
     * 将当前Map转换成List
     * 如：{"A": "a", "B": "b", "C": "c"}转换成[{"code": "A", "name": "a"}, {"code": "B", "name": "B"}, {"code": "C", "name": "c"}]
     *
     * @param function 转换函数
     * @param <NK>     转换后的List中存储的Map元素的健类型
     * @param <NV>     转换后的List中存储的Map元素的值类型
     * @return 转换结果
     */
    @SuppressWarnings("WeakerAccess")
    public <NK, NV> List<Map<NK, NV>> toList(Function<BiValue<K, V>, Map<NK, NV>> function) {
        List<Map<NK, NV>> list = new ArrayList<>(16);
        map.forEach((k, v) -> {
            BiValue<K, V> biValue = new BiValue<K, V>()
                    .setKey(k)
                    .setValue(v);
            list.add(function.apply(biValue));
        });

        return list;
    }

    /**
     * 将当前的Map增强器转换成List增强器
     * 按指定的函数时行转换
     */
    public <NK, NV> ListEnhancer<Map<NK, NV>> toListEnhancer(Function<BiValue<K, V>, Map<NK, NV>> function) {
        return ListEnhancer.of(toList(function));
    }

    /**
     * 将当前的Map转换成List<Map<String, Object>>
     * 每个Map的键值对对应于List中的一个Map元素，每个Map元素中，
     * key存储在keyField对应的值中
     * value存储在valueField对应的值中
     */
    @SuppressWarnings("WeakerAccess")
    public List<Map<String, Object>> toList(String keyField, String valueField) {
        List<Map<String, Object>> list = new ArrayList<>(16);
        map.forEach((k, v) -> {
            Map<String, Object> itemMap = new HashMap<>(16);
            itemMap.put(keyField, k);
            itemMap.put(valueField, v);
            list.add(itemMap);
        });

        return list;
    }

    /**
     * 将当前的MapEnhancer转换成ListEnhancer<Map<String, Object>>
     * 每个Map的键值对对应于List中的一个Map元素，每个Map元素中，
     * key存储在keyField对应的值中
     * value存储在valueField对应的值中
     */
    public ListEnhancer<Map<String, Object>> toListEnhancer(String keyField, String valueField) {
        return ListEnhancer.of(toList(keyField, valueField));
    }

    public MapEnhancer<K, V> debug(Logger logger, String message) {
        if (logger.isDebugEnabled()) {
            if (!message.contains("{}")) {
                message = message + ": {}";
            }
            logger.debug(message, map);
        }

        return this;
    }
}
