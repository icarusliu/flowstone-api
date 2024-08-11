<template>
    <!-- 基础数据表格 -->
    <el-table :data="rows" row-key="id" default-expand-all border>
        <el-table-column v-for="field in fields" :prop="field.prop" :key="field.prop" :label="field.label"
            :fixed="field.fixed"
            :width="field.width"
            :type="field.type == 'expand' ? 'expand' : 'default'"
            :min-width="field.minWidth">
            
            <!-- 操作列 -->
            <template v-if="field.type == 'operations'" #default="{ row }">
                <template v-for="button in field.buttons">
                    <el-link :type="button.type" v-if="!button.visible || button.visible(row)" @click="button.action(row)" class="mr-1">
                        {{ button.label }}
                    </el-link>
                </template>
            </template>

            <!-- 可展开列，一般放第一列 -->
            <template v-else-if="field.type == 'expand'" #default="{row}">
                <base-render :content="field.render(row[field.prop], row)"/>
            </template>

            <!-- 带有渲染函数的列表 -->
            <template v-else-if="field.render" #default="{row}">
                <base-render :content="field.render(row[field.prop], row)"/>
            </template>

            <!-- 内容为html的列 -->
            <template v-else-if="field.type == 'html'" #default="{row}">
                <div v-html="row[field.prop]"></div>
            </template>

            <!-- 带有转换函数的列 -->
            <template v-else-if="field.converter" #default="{ row }">
                {{ field.converter(row[field.prop], row) }}
            </template>
        </el-table-column>
    </el-table>

    <el-pagination 
        v-if="pageable != false"
        :total="total" :pageNo="pageNo" layout="prev, pager, next" @change="pageChanged" background size="small" class="mt-2"/>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import BaseRender from '@/components/base-render.js'

const props = defineProps(["dataSupplier", "fields", "params", "pageable"])
const total = ref(0)
const rows = ref([])
const pageNo = ref(0)
const pageSize = ref(10)

onMounted(() => {
    loadData()
})

function pageChanged(currentPage, size) {
    pageNo.value = currentPage
    pageSize.value = size

    loadData()
}

// 加载数据
function loadData() {
    if (!props.dataSupplier) {
        return;
    }

    if (typeof props.dataSupplier != 'function') {
        // 直接数据
        rows.value = props.dataSupplier
        total.value = row.value.length
        return
    }

    const queryParams = {}
    if (props.params) {
        for (let k in props.params) {
            const v = props.params[k]
            if (!v) {
                continue
            }

            queryParams[k] = v
        }
    }

    queryParams.pageNo = pageNo.value
    queryParams.pageSize = pageSize.value

    let result = props.dataSupplier(queryParams)
    if (!result.then) {
        result = Promise.resolve(result)
    }

    result.then(resp => {
        if (resp.records) {
            rows.value = resp.records
            total.value = resp.total
        } else {
            rows.value = resp
            total.value = resp?.length || 0
        }
    })
}

function reload() {
    pageNo.value = 0
    loadData()
}

defineExpose({
    reload
})
</script>