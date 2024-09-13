<template>
    <!-- 操作列 -->
    <template v-if="field.type == 'operations'">
        <template v-for="button in field.buttons">
            <el-link :type="button.type" v-if="!button.visible || button.visible(row)" @click="button.action(row, index)" :class="clazz"
                class="mr-1">
                {{ button.label }}
            </el-link>
        </template>
    </template>

    <!-- 可展开列，一般放第一列 -->
    <base-render  v-else-if="field.type == 'expand'" :content="field.render(row[field.prop], row)"  :class="clazz"/>

    <!-- 带有渲染函数的列表 -->
    <base-render  v-else-if="field.render" :content="field.render(row[field.prop], row)"  :class="clazz"/>

    <!-- 内容为html的列 -->
    <div  v-else-if="field.type == 'html'" v-html="row[field.prop]"  :class="clazz"/>

    <!-- 标签 -->
    <el-tag v-else-if="field.tagType" :type="tagType.type"  :class="clazz">{{ tagType.text }}</el-tag>

    <!-- 带有转换函数的列 -->
    <template v-else-if="field.converter">
        <span :class="clazz">{{ field.converter(row[field.prop], row) }}</span>
    </template>
    <template v-else>
        <span :class="clazz">{{ row[field.prop] }}</span>
    </template>
</template>

<script setup>
import BaseRender from '@/components/base-render.js'

const props = defineProps(["field", "row", "index"])

const tagType = computed(() => {
    return props.field.tagType(props.row[props.field.prop], props.row)
})

const clazz = computed(() => {
    if (!props.field.class) {
        return
    }

    let clazz = props.field.class
    if (clazz instanceof Function) {
        return clazz(props.row[props.field.prop], props.row, {field: props.field})
    } else {
        return clazz
    }
})
</script>