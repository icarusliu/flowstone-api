package com.liuqi.dua.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.ErrorCodes;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.common.exception.AppException;
import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.bean.dto.ApiDraftDTO;
import com.liuqi.dua.bean.query.ApiDraftQuery;
import com.liuqi.dua.domain.entity.ApiDraftEntity;
import com.liuqi.dua.domain.mapper.ApiDraftMapper;
import com.liuqi.dua.service.ApiDraftService;
import com.liuqi.dua.service.ApiService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * 接口草稿服务实现
 *
 * @author Coder Generator 2024-07-08 22:33:46
 **/
@Service
public class ApiDraftServiceImpl extends AbstractBaseService<ApiDraftEntity, ApiDraftDTO, ApiDraftMapper, ApiDraftQuery> implements ApiDraftService {
    @Autowired
    private ApiService apiService;

    @Override
    public ApiDraftDTO toDTO(ApiDraftEntity entity) {
        ApiDraftDTO dto = new ApiDraftDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ApiDraftEntity toEntity(ApiDraftDTO dto) {
        ApiDraftEntity entity = new ApiDraftEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<ApiDraftEntity> queryToWrapper(ApiDraftQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq("deleted", false)
                .eq(StringUtils.isNotBlank(query.getPath()), "path", query.getPath())
                .in(CollectionUtils.isNotEmpty(query.getTypeIds()), "type_id", query.getTypeIds())
                .and(StringUtils.isNotBlank(query.getKeyLike()), q -> q.like("name", query.getKeyLike())
                        .or().like("path", query.getKeyLike()))
                .and(StringUtils.isNotBlank(query.getKey()), q -> {
                    q.eq("id", query.getKey())
                            .or().eq("path", query.getKey());
                })
                .orderByDesc("create_time");
    }

    /**
     * 插入前处理
     * 可以执行相关参数校验，校验不通过时抛出异常
     *
     * @param dto
     * @return false时不进行插入，true时进行插入
     */
    @Override
    protected boolean processBeforeInsert(ApiDraftDTO dto) {
        // 判断接口路径是否存在
        String path = dto.getPath();
        this.findByPath(path).ifPresent(d -> {
            throw AppException.of(ErrorCodes.API_PATH_DUPLIATED);
        });

        return super.processBeforeInsert(dto);
    }

    /**
     * 更新前处理
     *
     * @param dto
     * @return false时不再进行更新，true时进行更新
     */
    @Override
    protected boolean processBeforeUpdate(ApiDraftDTO dto) {
        // 判断接口路径是否存在
        String path = dto.getPath();
        this.findByPath(path).ifPresent(d -> {
            if (!d.getId().equals(dto.getId())) {
                throw AppException.of(ErrorCodes.API_PATH_DUPLIATED);
            }
        });

        return super.processBeforeUpdate(dto);
    }

    /**
     * 发布接口
     *
     * @param id 接口id
     */
    @Override
    public void publish(String id) {
        ApiDraftDTO apiDraftDTO = this.findById(id).orElseThrow(() -> AppException.of(ErrorCodes.API_NOT_EXISTS));
        apiDraftDTO.setStatus(1);

        // 保存发布后的表
        ApiDTO apiDTO = new ApiDTO();
        BeanUtils.copyProperties(apiDraftDTO, apiDTO);
        boolean exists = apiService.findById(id).isPresent();
        if (exists) {
            apiService.update(apiDTO);
        } else {
            apiService.insert(apiDTO);
        }

        this.update(apiDraftDTO);
    }

    /**
     * 接口下线
     *
     * @param id 接口id
     */
    @Override
    public void offline(String id) {
        ApiDraftDTO apiDraftDTO = this.findById(id).orElseThrow(() -> AppException.of(ErrorCodes.API_NOT_EXISTS));
        apiDraftDTO.setStatus(3);
        apiService.deletePhysical(id);
        this.update(apiDraftDTO);
    }
}