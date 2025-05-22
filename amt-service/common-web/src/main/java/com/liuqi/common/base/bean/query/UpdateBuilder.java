package com.liuqi.common.base.bean.query;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * 更新构建器
 *
 * @author LiuQi 2025/5/6-19:48
 * @version V1.0
 **/
public class UpdateBuilder<D> {
    private final ServiceImpl<?, D> serviceImpl;
    private final UpdateWrapper<D> updateWrapper;

    public UpdateBuilder(ServiceImpl<?, D> serviceImpl) {
        this.serviceImpl = serviceImpl;
        updateWrapper = new UpdateWrapper<>();
    }

    public static <D> UpdateBuilder<D> create(ServiceImpl<?, D> baseService) {
        return new UpdateBuilder<>(baseService);
    }

    public UpdateBuilder<D> eq(String key, Object value) {
        if (null == value) {
            return this;
        }
        updateWrapper.eq(key, value);
        return this;
    }

    public UpdateBuilder<D> in(String key, Collection<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return this;
        }
        updateWrapper.in(key, list);
        return this;
    }

    public UpdateBuilder<D> notIn(String key, Collection<?> list) {
        if (CollectionUtils.isEmpty(list)) {
            return this;
        }
        updateWrapper.notIn(key, list);
        return this;
    }

    public UpdateBuilder<D> like(String key, String val) {
        if (StringUtils.isBlank(val)) {
            return this;
        }
        updateWrapper.like(key, val);
        return this;
    }

    public UpdateBuilder<D> notLike(String key, String val) {
        if (StringUtils.isBlank(val)) {
            return this;
        }
        updateWrapper.notLike(key, val);
        return this;
    }

    public UpdateBuilder<D> lt(String key, Object val) {
        if (null == val) {
            return this;
        }
        updateWrapper.lt(key, val);
        return this;
    }

    public UpdateBuilder<D> le(String key, Object val) {
        if (null == val) {
            return this;
        }
        updateWrapper.le(key, val);
        return this;
    }

    public UpdateBuilder<D> gt(String key, Object val) {
        if (null == val) {
            return this;
        }
        updateWrapper.gt(key, val);
        return this;
    }

    public UpdateBuilder<D> ge(String key, Object val) {
        if (null == val) {
            return this;
        }
        updateWrapper.ge(key, val);
        return this;
    }

    public UpdateBuilder<D> ne(String key, Object val) {
        if (null == val) {
            return this;
        }
        updateWrapper.ne(key, val);
        return this;
    }

    public UpdateBuilder<D> isNull(String key) {
        updateWrapper.isNull(key);
        return this;
    }

    public UpdateBuilder<D> isNotNull(String key) {
        updateWrapper.isNotNull(key);
        return this;
    }

    public UpdateBuilder<D> notNull(String key) {
        updateWrapper.isNotNull(key);
        return this;
    }

    /**
     * 增加or条件
     * @param consumer 消费函数
     * @return 当前对象
     */
    public UpdateBuilder<D> or(Consumer<UpdateWrapper<D>> consumer) {
        this.updateWrapper.or(consumer);
        return this;
    }

    public UpdateBuilder<D> set(String key, Object val) {
        updateWrapper.set(key, val);
        return this;
    }

    public UpdateBuilder<D> setSql(String sql) {
        updateWrapper.setSql(sql);
        return this;
    }

    public boolean update() {
        return serviceImpl.update(updateWrapper);
    }
}
