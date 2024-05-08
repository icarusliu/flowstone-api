package com.liuqi.acode.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.liuqi.acode.base.bean.dto.DictDTO;
import com.liuqi.acode.base.bean.query.DictQuery;
import com.liuqi.acode.base.domain.entity.DictEntity;
import com.liuqi.acode.base.domain.mapper.DictMapper;
import com.liuqi.acode.base.service.DictItemService;
import com.liuqi.acode.base.service.DictService;
import com.liuqi.acode.base.common.ErrorCodes;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.common.exception.AppException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class DictServiceImpl extends AbstractBaseService<DictEntity, DictDTO, DictMapper, DictQuery> implements DictService {
    @Autowired
    private DictItemService dictItemService;

    @Override
    public DictDTO toDTO(DictEntity entity) {
        DictDTO dto = new DictDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public DictEntity toEntity(DictDTO dto) {
        DictEntity entity = new DictEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<DictEntity> queryToWrapper(DictQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getCode()), "code", query.getCode())
                .eq(StringUtils.isNotBlank(query.getName()), "name", query.getName())
                .and(StringUtils.isNotBlank(query.getKey()), q -> q.like("code", query.getKey())
                        .or(l -> l.like("name", query.getKey()))
                        .or(l -> l.like("remark", query.getKey())));
    }

    /**
     * 根据编码查找字典项
     *
     * @param code 字典编码
     * @return 字典信息
     */
    @Override
    public Optional<DictDTO> findByCode(String code) {
        return this.findOne(DictQuery.builder().code(code).build());
    }

    /**
     * 根据名称查找字典信息
     *
     * @param name 字典名称
     * @return 字典信息
     */
    private Optional<DictDTO> findByName(String name) {
        return this.findOne(DictQuery.builder().name(name).build());
    }

    /**
     * 插入前处理
     * 可以执行相关参数校验，校验不通过时抛出异常
     *
     * @param dto 数据对象
     * @return false时不进行插入，true时进行插入
     */
    @Override
    protected boolean processBeforeInsert(DictDTO dto) {
        // 编码不能重复
        if (this.findByCode(dto.getCode()).isPresent()) {
            throw AppException.of(ErrorCodes.BASE_DICT_CODE_EXISTS);
        }

        // 名称不能重复
        if (this.findByName(dto.getName()).isPresent()) {
            throw AppException.of(ErrorCodes.BASE_DICT_NAME_EXISTS);
        }

        return super.processBeforeInsert(dto);
    }

    /**
     * 删除后处理
     *
     * @param ids
     */
    @Override
    protected void processAfterDelete(Collection<String> ids) {
        super.processAfterDelete(ids);

        // 删除对应的字典项
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        dictItemService.deleteByDicts(ids);
    }
}