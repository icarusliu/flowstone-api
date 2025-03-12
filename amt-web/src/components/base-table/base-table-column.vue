<template>
    <!-- 操作列 -->
    <template v-if="type == 'operations'">
        <template v-for="button in field.buttons">
            <el-link :type="button.type" v-if="!button.visible || button.visible(row)" @click="button.action(row, index)"
                :class="clazz" v-perm="button.code" class="mr-1">
                {{ button.label }}
            </el-link>
        </template>
    </template>

    <!-- 可展开列，一般放第一列 -->
    <base-render v-else-if="type == 'expand'" :content="field.render(value, row)" :class="clazz" :key="row.id"/>

    <!-- 带有渲染函数的列表 -->
    <base-render v-else-if="field.render" :content="field.render(value, row)" :class="clazz" :key="row.id + '-' + field.prop"/>

    <!-- 内容为html的列 -->
    <div v-else-if="type == 'html'" v-html="value" :class="clazz" />

    <!-- 标签 -->
    <el-tag v-else-if="field.tagType" :type="tagType.type" :class="clazz">{{ tagType.text }}</el-tag>

    <!-- 复选 -->
    <el-checkbox v-else-if="type == 'checkbox'" :modelValue="value" @change="change" />

    <!-- 图标 -->
    <el-icon v-else-if="type == 'iconSelector' || type == 'icon'">
        <component :is="value" />
    </el-icon>

    <!-- 带有转换函数的列 -->
    <span :class="clazz" v-else-if="field.converter">{{ field.converter(value, row) }}</span>

    <span :class="clazz" v-else>{{ value == 0 ? value : value || '--' }}</span>
</template>

<script setup>
import BaseRender from '../base-render.js'
import * as _ from 'lodash'

const props = defineProps(["field", "row", "index"])
const value = computed(() => {
    let prop = props.field.prop
    return _.get(props.row, prop)
})
const type = computed(() => props.field?.type)
const tagType = computed(() => {
    return props.field.tagType(props.row[props.field.prop], props.row)
})

const clazz = computed(() => {
    if (!props.field.class) {
        return
    }

    let clazz = props.field.class
    if (clazz instanceof Function) {
        return clazz(props.row[props.field.prop], props.row, { field: props.field })
    } else {
        return clazz
    }
})

function change(val) {
    let change = props.field.changeInTable || props.field.change
    change && change(val, props.row)
}
</script>