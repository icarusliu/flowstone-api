package com.liuqi.dua.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.bean.query.ApiQuery;
import com.liuqi.dua.domain.entity.ApiEntity;
import com.liuqi.dua.domain.mapper.ApiMapper;
import com.liuqi.dua.service.ApiService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * 接口服务实现
 *
 * @author Coder Generator 2024-07-08 22:32:36
 **/
@Service
public class ApiServiceImpl extends AbstractBaseService<ApiEntity, ApiDTO, ApiMapper, ApiQuery> implements ApiService {
    @Override
    public ApiDTO toDTO(ApiEntity entity) {
        ApiDTO dto = new ApiDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ApiEntity toEntity(ApiDTO dto) {
        ApiEntity entity = new ApiEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<ApiEntity> queryToWrapper(ApiQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .and(StringUtils.isNotBlank(query.getKey()), q -> {
                    q.eq("id", query.getKey())
                            .or().eq("path", query.getKey());
                })
                .notIn(CollectionUtils.isNotEmpty(query.getIdsNot()), "id", query.getIdsNot())
                .orderByDesc("create_time");
    }

    /**
     * 更新记录
     *
     * @param dto 待更新记录内容，id不能为空
     */
    @CacheEvict(cacheNames = "apiInfo", allEntries = true)
    @Override
    public void update(ApiDTO dto) {
        super.update(dto);
    }


    /**
     * 逻辑删除
     *
     * @param id 待删除记录id
     */
    @Override
    @CacheEvict(cacheNames = "apiInfo", allEntries = true)
    public void delete(String id) {
        super.delete(id);
    }

    /**
     * 批量逻辑删除
     *
     * @param ids 待删除记录id列表
     */
    @Override
    @CacheEvict(cacheNames = "apiInfo", allEntries = true)
    public void delete(Collection<String> ids) {
        super.delete(ids);
    }

    /**
     * 物理删除
     *
     * @param id 记录id
     */
    @Override
    @CacheEvict(cacheNames = "apiInfo", allEntries = true)
    public void deletePhysical(String id) {
        super.deletePhysical(id);
    }

    /**
     * 物理删除
     *
     * @param ids 记录id列表
     */
    @Override
    @CacheEvict(cacheNames = "apiInfo", allEntries = true)
    public void deletePhysical(Collection<String> ids) {
        super.deletePhysical(ids);
    }

    /**
     * 通过路径查找接口
     *
     * @param path 路径
     * @return 接口信息
     */
    @Override
    @Cacheable(cacheNames = "apiInfo")
    public Optional<ApiDTO> findByPath(String path) {
        ApiQuery query = new ApiQuery();
        query.setKey(path);
        return this.findOne(query);
    }
}