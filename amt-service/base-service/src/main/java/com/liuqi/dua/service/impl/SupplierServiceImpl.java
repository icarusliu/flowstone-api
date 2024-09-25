package com.liuqi.dua.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.dua.bean.dto.SupplierDTO;
import com.liuqi.dua.bean.query.SupplierQuery;
import com.liuqi.dua.domain.entity.SupplierEntity;
import com.liuqi.dua.domain.mapper.SupplierMapper;
import com.liuqi.dua.service.SupplierService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

/**
 * 接入方服务实现
 *
 * @author Coder Generator 2024-08-12 19:00:03
 **/
@Service
public class SupplierServiceImpl extends AbstractBaseService<SupplierEntity, SupplierDTO, SupplierMapper, SupplierQuery> implements SupplierService {
    @Override
    public SupplierDTO toDTO(SupplierEntity entity) {
        SupplierDTO dto = new SupplierDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public SupplierEntity toEntity(SupplierDTO dto) {
        SupplierEntity entity = new SupplierEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<SupplierEntity> queryToWrapper(SupplierQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq("deleted", false)
                .orderByDesc("create_time");
    }

    /**
     * 根据id查找记录
     *
     * @param id id
     * @return id对应的记录
     */
    @Override
    @Cacheable(cacheNames = "supplierInfo")
    public Optional<SupplierDTO> findById(String id) {
        return super.findById(id);
    }

    /**
     * 更新记录
     *
     * @param dto 待更新记录内容，id不能为空
     */
    @Override
    @CacheEvict(cacheNames = "supplierInfo", key = "#dto.id")
    public void update(SupplierDTO dto) {
        super.update(dto);
    }

    /**
     * 逻辑删除
     *
     * @param id 待删除记录id
     */
    @Override
    @CacheEvict(cacheNames = "supplierInfo", key = "#id")
    public void delete(String id) {
        super.delete(id);
    }

    /**
     * 批量逻辑删除
     *
     * @param ids 待删除记录id列表
     */
    @Override
    @CacheEvict(cacheNames = "supplierInfo", allEntries = true)
    public void delete(Collection<String> ids) {
        super.delete(ids);
    }

    /**
     * 物理删除
     *
     * @param id 记录id
     */
    @Override
    @CacheEvict(cacheNames = "supplierInfo", key = "#id")
    public void deletePhysical(String id) {
        super.deletePhysical(id);
    }

    /**
     * 物理删除
     *
     * @param ids 记录id列表
     */
    @Override
    @CacheEvict(cacheNames = "supplierInfo", allEntries = true)
    public void deletePhysical(Collection<String> ids) {
        super.deletePhysical(ids);
    }
}