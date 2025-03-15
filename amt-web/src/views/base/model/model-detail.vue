<template>
    <full-panel title="模型" @close="close">
        <el-tabs>
            <el-tab-pane label="基本信息">
                <base-info v-model="form" v-model:editing="editing" @close="close" />
            </el-tab-pane>

            <el-tab-pane label="数据维护" v-if="!editing && form.id">
                <data-explore :model="form" />
            </el-tab-pane>
        </el-tabs>
    </full-panel>
</template>
<script setup>
import dataExplore from './data-explore.vue'
import baseInfo from './base-info.vue'

import https from '@/utils/https'

import * as _ from 'lodash'
import fullPanel from '@/components/full-panel.vue'
import { onMounted } from 'vue'

const props = defineProps({
    item: { type: Object },
    type: { type: Object }
})
const emits = defineEmits(['close'])
const form = ref({})
const editing = ref(false)
const innerFields = ['createTime', 'createUser', 'updateTime', 'updateUser']

onMounted(() => {
    if (props.item) {
        const model = _.cloneDeep(props.item)

        // 需要查找模型对应的字段
        https.post('/dua/model-field/query', { modelId: model.id }).then(resp => {
            model.fields = resp.map(item => {
                if (item.code == 'id') {
                    item.primaryKey = true
                    item.readonly = true
                } else if (innerFields.includes(item.code)) {
                    item.readonly = true
                }

                return item
            })
            form.value = model
        })
    } else {
        form.value = {
            status: 0,
            fields: [
                { code: 'id', name: 'id', primaryKey: true, dataType: 'varchar', dataConfig: '(64)', nullable: false, readonly: true, listConfig: {show: false} },
                { code: 'createTime', name: '创建时间', dataType: 'datetime', dataConfig: '', defaultValue: 'current_timestamp()', readonly: true, nullable: true, listConfig: {width: '160px'} },
                { code: 'createUser', name: '创建用户', dataType: 'varchar', dataConfig: '(255)', readonly: true, nullable: true, listConfig: {show: false} },
                { code: 'updateTime', name: '更新时间', dataType: 'datetime', dataConfig: '', defaultValue: 'current_timestamp()', readonly: true, nullable: true, listConfig: {width: '160px'} },
                { code: 'updateUser', name: '更新用户', dataType: 'varchar', dataConfig: '(255)', readonly: true, nullable: true, listConfig: {show: false}},
            ]
        }
        editing.value = true
    }
})

function close() {
    emits('close')
}
</script>

<style lang='scss' scoped></style>