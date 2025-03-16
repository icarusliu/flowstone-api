package com.liuqi.etl.service.impl;

import cn.hutool.core.lang.Pair;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liuqi.common.ErrorCodes;
import com.liuqi.common.base.service.AbstractBaseService;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.utils.SqlParserUtils;
import com.liuqi.etl.bean.dto.EtlJobDTO;
import com.liuqi.etl.bean.dto.EtlJobDependDTO;
import com.liuqi.etl.bean.dto.EtlJobHistoryDTO;
import com.liuqi.etl.bean.dto.EtlJobPublishedDTO;
import com.liuqi.etl.bean.query.EtlJobQuery;
import com.liuqi.etl.bean.resp.BloodTree;
import com.liuqi.etl.domain.entity.EtlJobEntity;
import com.liuqi.etl.domain.mapper.EtlJobMapper;
import com.liuqi.etl.service.EtlJobDependService;
import com.liuqi.etl.service.EtlJobHistoryService;
import com.liuqi.etl.service.EtlJobPublishedService;
import com.liuqi.etl.service.EtlJobService;
import com.liuqi.etl.service.executors.EtlJobScheduler;
import com.liuqi.etl.service.executors.config.EtlNodeInfo;
import com.liuqi.etl.service.executors.job.EtlMqJobService;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ETL任务服务实现 
 * @author Coder Generator 2025-03-10 16:36:10 
 **/
@Service
public class EtlJobServiceImpl extends AbstractBaseService<EtlJobEntity, EtlJobDTO, EtlJobMapper, EtlJobQuery> implements EtlJobService {
    @Autowired
    private EtlJobPublishedService publishedService;

    @Autowired
    @Lazy
    private EtlJobScheduler etlJobScheduler;

    @Autowired
    @Lazy
    private EtlJobDependService dependService;

    @Autowired
    @Lazy
    private EtlMqJobService mqService;

    @Autowired
    private EtlJobHistoryService historyService;

    @Override
    public EtlJobDTO toDTO(EtlJobEntity entity) {
        EtlJobDTO dto = new EtlJobDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public EtlJobEntity toEntity(EtlJobDTO dto) {
        EtlJobEntity entity = new EtlJobEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    @Override
    protected QueryWrapper<EtlJobEntity> queryToWrapper(EtlJobQuery query) {
        return this.createQueryWrapper()
                .eq(StringUtils.isNotBlank(query.getId()), "id", query.getId())
                .in(null != query.getIds(), "id", query.getIds())
                .eq(null != query.getAutoTrigger(), "auto_trigger", query.getAutoTrigger())
                .eq(StringUtils.isNotBlank(query.getTypeId()), "type_id", query.getTypeId())
                .and(StringUtils.isNotBlank(query.getKey()), w -> {
                    w.like("name", query.getKey())
                            .or()
                            .like("code", query.getKey());
                })
                .and(!CollectionUtils.isEmpty(query.getUpdatedTables()), q -> {
                    for (String updatedTable : query.getUpdatedTables()) {
                        q.or().like("updated_tables", updatedTable);
                    }
                })
                .eq(StringUtils.isNotBlank(query.getCode()), "code", query.getCode())
                .eq(StringUtils.isNotBlank(query.getName()), "name", query.getName())
                .orderByDesc("create_time");
    }

    /**
     * 根据脚本更新使用的表清单及更新的表清单
     */
    private void updateTableRefs(EtlJobDTO dto) {
        String type = dto.getType();

        if ("sync".equals(type)) {
            // 数据同步任务，根据目标表写入语句查询更新的表清单
            Map<String, Object> config = dto.getConfig();
            String destSql = MapUtils.getString(config, "destSql");
            if (StringUtils.isBlank(destSql)) {
                return;
            }

            // 根据insert into table_name(来解析表名;
            // 将连续的空格、换行等替换成一个空格
            destSql = destSql.replaceAll("\\s+", " ");
            Pattern p = Pattern.compile("insert\\s+into\\s+(\\w+)", Pattern.CASE_INSENSITIVE);
            List<String> tables = new ArrayList<>(16);
            Matcher m = p.matcher(destSql);
            while (m.find()) {
                tables.add(m.group(1));
            }
            dto.setUpdatedTables(tables);
        } else {
            // 数据加工任务，需要分析查询与更新的表
            JSONObject obj = JSON.parseObject(JSON.toJSONString(dto.getConfig()));
            List<EtlNodeInfo> nodes = obj.getJSONArray("nodes").toJavaList(EtlNodeInfo.class);
            if (CollectionUtils.isEmpty(nodes)) {
                return;
            }

            List<String> usedTables = new ArrayList<>(16);
            List<String> updatedTables = new ArrayList<>(16);
            nodes.forEach(node -> {
                Object sqlObj = node.getConfig().get("sql");
                if (null == sqlObj) {
                    return;
                }

                String sql = sqlObj.toString().replaceAll("\\s+", " ");
                Pair<Set<String>, Set<String>> pair = SqlParserUtils.parseTableNames(sql);
                usedTables.addAll(pair.getKey());
                updatedTables.addAll(pair.getValue());
            });

            dto.setUsedTables(usedTables.stream().distinct().toList());
            dto.setUpdatedTables(updatedTables.stream().distinct().toList());
        }
    }

    /**
     * 更新前处理
     *
     * @return false时不再进行更新，true时进行更新
     */
    @Override
    protected boolean processBeforeUpdate(EtlJobDTO dto) {
        this.updateTableRefs(dto);
        return super.processBeforeUpdate(dto);
    }

    /**
     * 分页查询
     *
     * @param etlJobQuery 查询对象
     * @return 查询结果
     */
    @Override
    public IPage<EtlJobDTO> pageQuery(EtlJobQuery etlJobQuery) {
        IPage<EtlJobDTO> etlJobDTOIPage = super.pageQuery(etlJobQuery);
        List<EtlJobDTO> list = etlJobDTOIPage.getRecords();
        if (!CollectionUtils.isEmpty(list)) {
            // 补充依赖信息
            List<String> jobIds = list.stream()
                    .map(EtlJobDTO::getId)
                    .toList();
            Map<String, List<EtlJobDependDTO>> dependMap = dependService.findByJobs(jobIds)
                    .stream()
                    .collect(Collectors.groupingBy(EtlJobDependDTO::getJobId));
            list.forEach(job -> job.setDepends(dependMap.get(job.getId())));
        }
        return etlJobDTOIPage;
    }

    /**
     * 自动解析任务依赖
     *
     * @param jobId 任务id
     * @return 依赖的直接任务清单
     */
    @Override
    public List<EtlJobDTO> parseDepends(String jobId) {
        EtlJobDTO job = this.findById(jobId).orElseThrow(() -> AppException.of(ErrorCodes.ETL_JOB_NOT_EXISTS));
        List<String> usedTables = job.getUsedTables();
        if (CollectionUtils.isEmpty(usedTables)) {
            return List.of();
        }

        // 根据使用的表，来查询操作这些表的记录清单
        EtlJobQuery query = new EtlJobQuery();
        query.setUpdatedTables(usedTables);
        return this.query(query)
                .stream()
                .filter(item -> {
                    // 因为使用的like匹配，可能会查出多余的数据，需要处理
                    return item.getUpdatedTables().stream().anyMatch(usedTables::contains);
                })
                .toList();
    }

    /**
     * 发布任务
     *
     * @param jobId 任务id
     */
    @Override
    @Transactional
    public void publish(String jobId) {
        // 发布时，更新发布表，并且更新版本号
        EtlJobDTO job = this.findById(jobId).orElseThrow(() -> AppException.of(ErrorCodes.ETL_JOB_NOT_EXISTS));

        if (null == job.getVersion() || 0 == job.getVersion()) {
            job.setVersion(1);
        }
        job.setPublishedVersion(job.getVersion());
        this.update(job);

        EtlJobPublishedDTO publishedDTO = new EtlJobPublishedDTO();
        BeanUtils.copyProperties(job, publishedDTO);
        publishedService.deletePhysical(jobId);
        publishedService.insert(publishedDTO);

        // 如果是mq类型，需要启动mq进行监听
        if (job.getType().equals("mq")) {
            mqService.startJob(job);
        }

        // 如果是定时任务，提交定时执行
        if (!publishedDTO.getAutoTrigger() && StringUtils.isNotBlank(publishedDTO.getCron())) {
            etlJobScheduler.restartJob(publishedDTO);
        }

        // 保存历史记录
        EtlJobHistoryDTO history = new EtlJobHistoryDTO();
        BeanUtils.copyProperties(job, history);
        history.setJobId(job.getId());
        history.setId(null);
        history.setCreateTime(LocalDateTime.now());
        historyService.insert(history);
    }

    /**
     * 任务下线
     *
     * @param jobId 任务id
     */
    @Override
    public void offline(String jobId) {
        this.findById(jobId).ifPresent(item -> {
            item.setPublishedVersion(null);
            UpdateWrapper<EtlJobEntity> updateWrapper = this.createUpdateWrapper().eq("id", jobId)
                    .set("published_version", null);
            baseMapper.update(updateWrapper);
        });

        publishedService.deletePhysical(jobId);
        etlJobScheduler.stopJob(jobId);

        // 停止实时任务
        mqService.stopJob(jobId);
    }

    /**
     * 血缘分析获取结果树
     *
     * @param id 任务id
     */
    @Override
    public List<BloodTree> getBloodRelation(String id) {
        EtlJobDTO job = this.findById(id).orElseThrow(AppException.supplier(ErrorCodes.ETL_JOB_NOT_EXISTS));

        // 需要根据使用的表及写入的表，将其父任务及子任务查询出来
        // 依赖关系的本身不太准确，因为用户可能未配置相应作业的依赖关系
        // 分析时需要将所有作业都拉出来
        List<EtlJobDTO> jobs = this.findAll();

        // 先将每个作业的父作业查找到，如果某个任务使用表来源于另外一个任务的输出表，则认为是其后代
        Map<String, List<EtlJobDTO>> usedTableJobs = new HashMap<>(16);
        Map<String, List<EtlJobDTO>> updatedTableJobs = new HashMap<>(16);
        jobs.forEach(item -> {
            List<String> usedTables = item.getUsedTables();
            if (!CollectionUtils.isEmpty(usedTables)) {
                usedTables.forEach(usedTable -> {
                    usedTableJobs.computeIfAbsent(usedTable, n -> new ArrayList<>(16)).add(item);
                });
            }

            List<String> updatedTables = item.getUpdatedTables();
            if (!CollectionUtils.isEmpty(updatedTables)) {
                updatedTables.forEach(updatedTable -> {
                    updatedTableJobs.computeIfAbsent(updatedTable, n -> new ArrayList<>(16)).add(item);
                });
            }
        });

        Map<String, List<EtlJobDTO>> parentMap = new HashMap<>(16);
        Map<String, List<EtlJobDTO>> childrenMap = new HashMap<>(16);
        jobs.forEach(item -> {
            List<String> usedTables = item.getUsedTables();
            if (!CollectionUtils.isEmpty(usedTables)) {
                usedTables.forEach(usedTable -> {

                });
            }
        });


        return List.of();
    }
}