<template>
    <el-tree class="tree" :data="data" :props="treeProps" node-key="id" :expand-on-click-node="false"
        :default-expanded-keys="['']"
        @current-change="currentChange">
    </el-tree>
</template>

<script setup>
import * as entityApis from '@/apis/entity'

const emits = defineEmits(["currentChange"])
const props = defineProps(['apiPrefix'])
const data = ref([{id: 0, name: '全部'}])
const treeProps = reactive({
    label: 'name'
})

onMounted(() => {
    entityApis.tree(props.apiPrefix).then(resp => {
        data.value = [{id: '', name: '全部', children: resp}]
    })
})

function currentChange(data) {
    emits('currentChange', data)
}
</script>

<style lang="scss" scoped>
.tree {
    --el-tree-node-content-height: 36px;

    :deep() {
        .el-tree-node {
            &.is-current {
                color: var(--primary_color);
            }
        }


    }
}
</style>