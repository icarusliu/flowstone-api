<template>
    <div>
        <span class="mr-2">数据日期：</span>
        <el-date-picker v-model="params.dataDate" value-format="YYYY-MM-DD"></el-date-picker>
        <el-button type="primary" class="ml-2" @click="doTest">测试</el-button>
    </div>

    <div class="result-info">
        <div>测试结果：{{ result }}</div>
        <div>
            <div>异常信息：</div>
            <pre>{{ errorMsg }}</pre>
        </div>
    </div>
</template>
<script setup>
import baseForm from '@/components/base-form/index.vue';
import https from '@/utils/https';
import { ElMessage } from 'element-plus';
import moment from 'moment'

const props = defineProps({
    job: { type: Object }
})
const params = ref({
    dataDate: moment().subtract(1, 'days').format('YYYY-MM-DD')
})
const errorMsg = ref('')
const result = ref('成功')

function doTest() {
    https.get('/etl/test', { jobId: props.job.id, dataDate: params.value.dataDate })
        .then(resp => {
            if (resp == '成功') {
                ElMessage.success('测试成功')
                result.value = '成功'
                errorMsg.value = ''
            } else {
                ElMessage.error('任务执行失败')
                result.value = '失败';
                errorMsg.value = resp
            }

        }).catch(err => {
            errorMsg.value = err
            result.value = '失败'
        })
}
</script>

<style lang='scss' scoped>
.result-info {
    line-height: 30px;
    margin-top: 20px;
}

pre {
    background: #222;
    color: #eee;
    padding: 0 8px;
}
</style>