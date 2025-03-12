<template>
    <full-panel title="任务依赖配置" @close="close">
        <div class="base-info">
            <div>任务：{{ job.name }}</div>
            <div>任务编码：{{ job.code }}</div>
            <div>使用表：{{ job.usedTables?.join(',') || '无' }}</div>
            <div>更新表：{{ job.updatedTables?.join(',') || '无' }}</div>
        </div>
        <el-transfer v-model="form" :data="jobs" filterable :titles="['所有任务', '依赖任务']" :props="{ key: 'id' }"></el-transfer>
        
        <div class="text-center mt-4">
            <el-button type="primary" @click="autoParse">自动解析</el-button>
            <el-button type="primary" @click="doSave">保存</el-button>
            <el-button @click="close">关闭</el-button>
        </div>
    </full-panel>
</template>
<script setup>
import fullPanel from '@/components/full-panel.vue'
import https from '@/utils/https'
import {ElMessage} from 'element-plus'

const emits = defineEmits(['close'])
const props = defineProps({
    job: { type: Object }
})
const form = ref([])
const jobs = ref([])

onMounted(() => {
    https.post('/etl/job/query').then(resp => {
        jobs.value = resp.map(item => {
            item.label = item.name + '(' + item.code + ')'
            return item
        }).filter(item => {
            // 过滤掉自己
            return item.id != props.job.id
        });
    })

    https.post('/etl/job-depend/query', {jobId: props.job.id}).then(resp => {
        form.value = resp.map(item => item.parentJobId)
    })
})

function close() {
    emits('close')
}

function autoParse() {
    https.get('/etl/job/parse-depend', {jobId: props.job.id}).then(resp => {
        ElMessage.success('解析成功')
        if (!resp) {
            return
        }
        form.value = resp.map(item => item.id)
    })
}

function doSave() {
    https.put('/etl/job-depend/update', {
        jobId: props.job.id,
        dependJobIds: form.value
    }).then(() => {
        ElMessage.success('操作成功')
    })
}

</script>

<style lang='scss' scoped>
:deep() {
    .el-transfer-panel {
        width: 40%;
        height: 60vh;

        .el-transfer-panel__body {
            height: calc(100% - 60px);
        }
    }
}

.base-info {
    line-height: 30px;
    margin-bottom: 16px;
}
</style>