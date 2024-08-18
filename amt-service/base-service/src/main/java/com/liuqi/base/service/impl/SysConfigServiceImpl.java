package com.liuqi.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.liuqi.base.service.SysConfigService;
import com.liuqi.base.bean.dto.SysConfigDTO;
import com.liuqi.base.bean.query.SysConfigQuery;
import com.liuqi.common.ErrorCodes;
import com.liuqi.base.domain.entity.SysConfigEntity;
import com.liuqi.base.domain.mapper.SysConfigMapper;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.common.exception.AppException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 系统配置项服务
 *
 * @author 不空军 2023/12/26 11:07
 **/
@Service
public class SysConfigServiceImpl extends AbstractBaseService<SysConfigEntity, SysConfigDTO, SysConfigMapper, SysConfigQuery> implements SysConfigService {
    @Override
    public SysConfigDTO toDTO(SysConfigEntity entity) {
        SysConfigDTO dto = new SysConfigDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public SysConfigEntity toEntity(SysConfigDTO dto) {
        SysConfigEntity entity = new SysConfigEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<SysConfigEntity> queryToWrapper(SysConfigQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getCode()), "code", query.getCode());
    }

    /**
     * 根据编码查找配置项
     *
     * @param code 配置项编码
     * @return 配置项，不存在时返回空
     */
    @Override
    public Optional<SysConfigDTO> findByCode(String code) {
        return this.findOne(SysConfigQuery.builder().code(code).build());
    }

    /**
     * 插入前处理
     * 可以执行相关参数校验，校验不通过时抛出异常
     *
     * @param dto 配置项信息
     * @return false时不进行插入，true时进行插入
     */
    @Override
    protected boolean processBeforeInsert(SysConfigDTO dto) {
        // 配置项编码不能重复
        String code = dto.getCode();
        if (this.findByCode(code).isPresent()) {
            throw AppException.of(ErrorCodes.BASE_CONFIG_CODE_EXISTS);
        }

        return super.processBeforeInsert(dto);
    }

    /**
     * 更新前处理
     *
     * @param dto 配置项信息
     * @return false时不再进行更新，true时进行更新
     */
    @Override
    protected boolean processBeforeUpdate(SysConfigDTO dto) {
        String code = dto.getCode();
        if (!StringUtils.isEmpty(code)) {
            boolean b = this.findByCode(code).map(item -> !item.getId().equals(dto.getId())).isPresent();
            if (b) {
                throw AppException.of(ErrorCodes.BASE_CONFIG_CODE_EXISTS);
            }
        }

        return super.processBeforeUpdate(dto);
    }
}