<template>
    <div>
        <base-tree apiPrefix="/etl/job-type" class="tree mr-4" title="任务分类" ref="treeRef" :showRoot="true" @currentChange="selectType">
            <template #buttons>
                <el-link type="primary" @click="showNewType">新增分类</el-link>
            </template>

            <template #default="{ node, data }">
                <div class="item">
                    <span class="ellipsis-1 label">
                        <ElIcon>
                            <Document v-if="node.isLeaf" />
                            <Folder v-else-if="!node.expanded" />
                            <FolderOpened v-else />
                        </ElIcon>
                        <span>{{ node.label }}</span>
                    </span>
                    <span class="icons" v-if="data.id">
                        <el-icon class="icon color-primary" @click="goEdit(data)">
                            <Edit />
                        </el-icon>
                        <el-icon class="icon color-red" @click="doDelete(data)">
                            <Delete />
                        </el-icon>
                    </span>
                </div>
            </template>
        </base-tree>

        <new-type v-model:visible="visible" v-model="editingRow" @change="reloadTree" :parent="currentType"></new-type>
    </div>
</template>
<script setup>
import BaseTree from '@/components/base-tree.vue'
import newType from './new-type.vue'
import https from '@/utils/https'
import * as _ from 'lodash'
import { ElMessageBox, ElMessage } from 'element-plus'

const currentType = defineModel()
const visible = ref(false)
const emits = defineEmits(['select'])
const editingRow = ref({})
const treeRef = ref()

function showNewType() {
    editingRow.value = {}
    visible.value = true
}

function selectType(type) {
    currentType.value = type
    emits('select', type)
}

function reloadTree() {
    treeRef.value.reload()
}

function doDelete(row) {
    ElMessageBox.confirm('确定删除当前记录？').then(() => {
        https.del('/etl/job-type/delete/' + row.id).then(() => {
            ElMessage.success('删除成功')
            reloadTree()
        })
    })
}

function goEdit(row) {
    editingRow.value = _.cloneDeep(row)
    visible.value = true
}
</script>

<style lang='scss' scoped>
.item {
    width: 100%;
    display: flex;
    position: relative;
    height: 36px;
    line-height: 36px;

    .label {
        display: flex;
        align-items: center;
    }

    &:hover {
        .icons {
            display: inline;
        }
    }

    .icons {
        position: absolute;
        right: 10px;
        display: none;

        .icon {
            margin-left: 8px;
            display: inline-block;
        }
    }
}
</style>