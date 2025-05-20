<template>
    <div class="page-content">
        <entity-manager apiPrefix="/sys/dict" :fields="fields" ref="entityManagerRef" operationsWidth="160px" :withNew="false">
            <template #buttons>
                <el-button type="primary" icon="plus" @click="showNew">新增</el-button>
            </template>
            <template #rowButtons="{ row }">
                <el-link type="primary" @click="goDetail(row)" class="mr-2" v-perm="'update'">详情</el-link>
                <el-link type="danger" v-show="row.status" class="mr-2" @click="updateStatus(row, 0)" v-perm="'update'">停用</el-link>
                <el-link type="success" v-show="!row.status" class="mr-2" @click="updateStatus(row, 1)" v-perm="'update'">启用</el-link>
                <el-link type="danger" @click="doDelete(row)" class="mr-2" v-perm="'delete'">删除</el-link>
            </template>
        </entity-manager>

        <dict-detail v-model="detailVisible" v-if="detailVisible" class="full-panel" :item="editingItem" @reload="reload" />
    </div>
</template>

<script setup name="sysDict">
    import dictDetail from './dict-detail.vue'
    import * as sysApis from '@/apis/sys'
    import { ElMessage } from 'element-plus'
    import * as _ from 'lodash'
    import { useSysStore } from '@/store/index'

    const fields = [
        { label: '编码', prop: 'code', required: true },
        { label: '名称', prop: 'name', required: true },
        {
            label: '状态', prop: 'status', width: '80px', needNew: false, tagType: val => {
                return {
                    text: val ? '启用' : '停用',
                    type: val ? 'success' : 'danger'
                }
            }
        },
        { label: '备注', prop: 'remark' },
        { label: '创建时间', prop: 'createTime', width: '200px', needNew: false },
    ]
    const entityManagerRef = ref()
    const detailVisible = ref(false)
    const sysStore = useSysStore()

    function reload() {
        entityManagerRef.value.reload()
        hb.https.post('/sys/dict/query').then(resp => {
            let dictInfo = {}
            resp.forEach(item => {
                dictInfo[item.code] = item
            })

            sysStore.setDictInfo(dictInfo)
        })
    }

    function showNew() {
        editingItem.value = null
        detailVisible.value = true
    }

    const editingItem = ref()
    function goDetail(row) {
        editingItem.value = row
        detailVisible.value = true
    }

    function doDelete(row) {
        entityManagerRef.value.doDelete(row)
    }

    function updateStatus(row, status) {
        sysApis.updateDict({
            id: row.id,
            status
        }).then(() => {
            row.status = status
            ElMessage.success('操作成功')
        })
    }

</script>

<style lang="scss" scoped>
    .page-content {
        box-sizing: border-box;

        .full-panel {
            width: 100%;
            height: 100%;
            position: absolute;
            left: 0;
            top: 0;
            background: #fff;
            z-index: 100;
        }
    }
</style>