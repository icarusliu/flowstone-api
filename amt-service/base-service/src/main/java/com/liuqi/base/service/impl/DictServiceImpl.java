package com.liuqi.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.liuqi.base.bean.DictItem;
import com.liuqi.base.bean.dto.DictDTO;
import com.liuqi.base.bean.query.DictQuery;
import com.liuqi.base.common.ErrorCodes;
import com.liuqi.base.domain.entity.DictEntity;
import com.liuqi.base.domain.mapper.DictMapper;
import com.liuqi.base.service.DictService;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.utils.DynamicSqlHelper;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DictServiceImpl extends AbstractBaseService<DictEntity, DictDTO, DictMapper, DictQuery> implements DictService {
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
                .in(null != query.getCodes(), "code", query.getCodes())
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
     * 查字典明细
     *
     * @param codes 字典编码列表
     * @return 字典项明细
     */
    @Override
    public List<DictDTO> findByCodes(List<String> codes) {
        DictQuery query = DictQuery.builder()
                .codes(codes)
                .build();
        List<DictDTO> list = this.query(query);

        // 如果是SQL类型的需要特别处理
        list.forEach(dict -> {
            String type = dict.getType();
            if ("sql".equals(type)) {
                String sql = MapUtils.getString(dict.getMetadata(), "sql");
                if (StringUtils.isBlank(sql)) {
                    return;
                }
                List<Map<String, Object>> dicts = (List<Map<String, Object>>) DynamicSqlHelper.executeSql("dict-query-" + dict.getId(), sql, new HashMap<>(16));
                dict.setItems(dicts.stream().map(m -> {
                    DictItem item = new DictItem();
                    item.setName(MapUtils.getString(m, "name"));
                    item.setValue(MapUtils.getString(m, "value"));
                    item.setRemark(MapUtils.getString(m, "remark"));
                    return item;
                }).toList());
            }
        });

        return list;
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
}