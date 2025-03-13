<template>
    <div class="tree-panel">
        <div class="space-between top-bar">
            <div v-if="title && title.length" class="title">
                {{ title }}
            </div>
            <div>
                <slot name="buttons" />
            </div>
        </div>
        <el-tree class="tree" :data="data" :props="treeProps" node-key="id" :expand-on-click-node="false" default-expand-all
            @current-change="currentChange">
            <template #default="{node,data}">
                <slot :node="node" :data="data"></slot>
            </template>
        </el-tree>
    </div>
</template>

<script setup>
import * as entityApis from '@/apis/entity'

const emits = defineEmits(["currentChange"])
const props = defineProps({
    apiPrefix: {type: String},
    title: {type: String},
    showRoot: {type: Boolean, default: true}
})
const data = ref([{ id: 0, name: '全部' }])
const treeProps = reactive({
    label: 'name'
})

onMounted(() => {
    load()
})

function load() {
    entityApis.tree(props.apiPrefix).then(resp => {
        if (props.showRoot) {
            data.value = [{ id: '', name: '全部', children: resp }]
        } else {
            data.value = resp
        }

    })
}

function currentChange(data) {
    emits('currentChange', data)
}

defineExpose({ reload: load })
</script>

<style lang="scss" scoped>
.tree-panel {
    background-color: #fefefe;
    border: 1px solid #f1f1f1;
    height: 100%;
    overflow: auto;
    margin-right: 16px;
    border-radius: 5px;
}

.top-bar {
    align-items: center;
    border-bottom: 1px solid #eee;
    padding: 8px;
    margin-bottom: 4px;

    .title {
        font-weight: bold;
    }
    
}

.tree {
    --el-tree-node-content-height: 36px;
    background: #fefefe;

    :deep() {
        .el-tree-node {
            &.is-current {
                color: var(--primary_color);
            }
        }


    }
}
</style>