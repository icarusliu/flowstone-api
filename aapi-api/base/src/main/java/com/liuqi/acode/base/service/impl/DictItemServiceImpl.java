package com.liuqi.acode.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.acode.base.bean.dto.DictItemDTO;
import com.liuqi.acode.base.bean.query.DictItemQuery;
import com.liuqi.acode.base.domain.entity.DictItemEntity;
import com.liuqi.acode.base.domain.mapper.DictItemMapper;
import com.liuqi.acode.base.service.DictItemService;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.common.exception.AppException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static com.liuqi.acode.base.common.ErrorCodes.BASE_DICT_ITEM_EXISTS;

@Service
public class DictItemServiceImpl extends AbstractBaseService<DictItemEntity, DictItemDTO, DictItemMapper, DictItemQuery> implements DictItemService {
    @Override
    public DictItemDTO toDTO(DictItemEntity entity) {
        DictItemDTO dto = new DictItemDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public DictItemEntity toEntity(DictItemDTO dto) {
        DictItemEntity entity = new DictItemEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<DictItemEntity> queryToWrapper(DictItemQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getDictId()), "dict_id", query.getDictId())
                .in(CollectionUtils.isNotEmpty(query.getDictIds()), "dict_id", query.getDictIds());
    }

    /**
     * 根据字典id列表删除字典项
     *
     * @param dictIds 字典id列表
     */
    @Override
    public void deleteByDicts(Collection<String> dictIds) {
        if (CollectionUtils.isEmpty(dictIds)) {
            return;
        }

        this.remove(this.queryToWrapper(DictItemQuery.builder().dictIds(dictIds).build()));
    }

    @Override
    public Collection<DictItemDTO> findByDict(String dictId) {
        return this.query(DictItemQuery.builder().dictId(dictId).build());
    }

    /**
     * 插入前处理
     * 可以执行相关参数校验，校验不通过时抛出异常
     *
     * @param dto 数据对象
     * @return false时不进行插入，true时进行插入
     */
    @Override
    protected boolean processBeforeInsert(DictItemDTO dto) {
        // 需要检查编码/名称及value是否重复（同一字典下）
        Collection<DictItemDTO> items = this.findByDict(dto.getDictId());
        if (!CollectionUtils.isEmpty(items)) {
            boolean exists = items.stream()
                    .anyMatch(item -> item.getCode().equals(dto.getCode()) || item.getName().equals(dto.getName()) || item.getValue().equals(dto.getValue()));
            if (exists) {
                throw AppException.of(BASE_DICT_ITEM_EXISTS);
            }
        }

        return super.processBeforeInsert(dto);
    }

    @Override
    protected boolean processBeforeUpdate(DictItemDTO dto) {
        String id = dto.getId();

        //  编码/名称与value在同一字典下不能重复
        String dictId = dto.getDictId();
        if (StringUtils.isBlank(dictId)) {
            dictId = this.getById(id).getDictId();
        }
        Collection<DictItemDTO> items = this.findByDict(dictId);
        if (CollectionUtils.isNotEmpty(items)) {
            boolean exists = items.stream().anyMatch(item -> !item.getId().equals(dto.getId()) && (
                    item.getCode().equals(dto.getCode()) || item.getName().equals(dto.getName()) || item.getValue().equals(dto.getValue())
                    ));
            if (exists) {
                throw AppException.of(BASE_DICT_ITEM_EXISTS);
            }
        }

        return super.processBeforeUpdate(dto);
    }
}