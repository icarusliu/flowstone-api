package com.liuqi.common.utils.value;

/**
 * 三元对象
 *
 * @author  LiuQi 2019/9/25-15:19
 * @version V1.0
 **/
public class TripleValue<T1, T2, T3> {
    private T1 firstValue;
    private T2 secondValue;
    private T3 thirdValue;

    public TripleValue firstValue(T1 firstValue) {
        this.firstValue = firstValue;
        return this;
    }

    public void setFirstValue(T1 firstValue) {
        this.firstValue = firstValue;
    }

    public T1 getFirstValue() {
        return this.firstValue;
    }

    public TripleValue secondValue(T2 secondValue) {
        this.secondValue = secondValue;
        return this;
    }

    public void setSecondValue(T2 secondValue) {
        this.secondValue = secondValue;
    }

    public T2 getSecondValue() {
        return this.secondValue;
    }

    public TripleValue thirdValue(T3 thirdValue) {
        this.thirdValue = thirdValue;
        return this;
    }

    public void setThirdValue(T3 thirdValue) {
        this.thirdValue = thirdValue;
    }

    public T3 getThirdValue() {
        return this.thirdValue;
    }
}
