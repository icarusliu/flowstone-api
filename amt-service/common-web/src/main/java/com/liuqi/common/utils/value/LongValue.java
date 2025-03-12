package com.liuqi.common.utils.value;

/**
 * 长整数值
 *
 * @author LiuQi 2024/8/30-14:04
 * @version V1.0
 **/
public class LongValue extends Value<Long> {
    public static LongValue of(Long val) {
        LongValue value = new LongValue();
        value.value = val;
        return value;
    }

    /**
     * 自增长
     *
     * @return 当前对象
     */
    public LongValue inc() {
        this.value++;
        return this;
    }

    /**
     * 取值后自增
     *
     * @return 自增前的值
     */
    public long getAndInc() {
        return this.value++;
    }

    /**
     * 自增后返回
     *
     * @return 自增后的值
     */
    public long incAndGet() {
        return ++this.value;
    }

    /**
     * 自减
     *
     * @return 当前对象
     */
    public LongValue dec() {
        this.value--;
        return this;
    }

    /**
     * 相减并返回相减后的结果
     *
     * @return 最新结果
     */
    public long decAndGet() {
        return --this.value;
    }

    /**
     * 返回结果并相减
     *
     * @return 相减前的结果
     */
    public long getAndDec() {
        return this.value--;
    }

    /**
     * 将值新增一个指定的增量值
     *
     * @param i 增量值
     * @return 当前对象
     */
    public LongValue inc(long i) {
        this.value += i;
        return this;
    }

    /**
     * 将值增加增量值并返回
     *
     * @param i 增量值
     * @return 增量后的结果
     */
    public long incAndGet(long i) {
        this.value += i;
        return this.value;
    }

    /**
     * 将值新增增量，并返回增量前的结果
     *
     * @param i 增量值
     * @return 增加增量前的结果
     */
    public long getAndInc(long i) {
        long result = this.value;
        this.value += i;
        return result;
    }

    public boolean isNull() {
        return this.value == null;
    }

    public LongValue nvl(long i) {
        if (this.value == null) {
            this.value = i;
        }

        return this;
    }
}
