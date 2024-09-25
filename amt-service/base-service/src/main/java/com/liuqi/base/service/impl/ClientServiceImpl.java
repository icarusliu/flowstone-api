package com.liuqi.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuqi.base.bean.dto.ClientDTO;
import com.liuqi.base.bean.query.ClientQuery;
import com.liuqi.base.domain.entity.ClientEntity;
import com.liuqi.base.domain.mapper.ClientMapper;
import com.liuqi.base.service.ClientService;
import com.liuqi.common.base.service.AbstractBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 客户端服务实现 
 * @author Coder Generator 2024-09-25 09:04:45 
 **/
@Service
public class ClientServiceImpl extends AbstractBaseService<ClientEntity, ClientDTO, ClientMapper, ClientQuery> implements ClientService {
    @Override
    public ClientDTO toDTO(ClientEntity entity) {
        ClientDTO dto = new ClientDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ClientEntity toEntity(ClientDTO dto) {
        ClientEntity entity = new ClientEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<ClientEntity> queryToWrapper(ClientQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq("deleted", false)
                .orderByDesc("create_time");
    }

    /**
     * 实体插入操作
     *
     * @param dto 插入的数据对象
     * @return 插入完成后的对象，包含有生成的id
     */
    @Override
    public ClientDTO insert(ClientDTO dto) {
        return super.insert(dto);
    }

    @Override
    public void insert(List<ClientDTO> dtos) {
        super.insert(dtos);
    }

    /**
     * 更新记录
     *
     * @param dto 待更新记录内容，id不能为空
     */
    @Override
    @CacheEvict(cacheNames = "clientInfo", key = "#dto.id")
    public void update(ClientDTO dto) {
        super.update(dto);
    }

    /**
     * 逻辑删除
     *
     * @param id 待删除记录id
     */
    @Override
    @CacheEvict(cacheNames = "clientInfo")
    public void delete(String id) {
        super.delete(id);
    }

    /**
     * 批量逻辑删除
     *
     * @param ids 待删除记录id列表
     */
    @Override
    @CacheEvict(cacheNames = "clientInfo", allEntries = true)
    public void delete(Collection<String> ids) {
        super.delete(ids);
    }

    /**
     * 物理删除
     *
     * @param id 记录id
     */
    @Override
    @CacheEvict(cacheNames = "clientInfo")
    public void deletePhysical(String id) {
        super.deletePhysical(id);
    }

    /**
     * 物理删除
     *
     * @param ids 记录id列表
     */
    @Override
    @CacheEvict(cacheNames = "clientInfo", allEntries = true)
    public void deletePhysical(Collection<String> ids) {
        super.deletePhysical(ids);
    }

    /**
     * 根据id查找记录
     *
     * @param id id
     * @return id对应的记录
     */
    @Override
    @Cacheable(cacheNames = "clientInfo")
    public Optional<ClientDTO> findById(String id) {
        return super.findById(id);
    }
}