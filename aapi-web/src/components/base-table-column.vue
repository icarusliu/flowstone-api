<template>
    <!-- 操作列 -->
    <template v-if="field.type == 'operations'">
        <template v-for="button in field.buttons">
            <el-link :type="button.type" v-if="!button.visible || button.visible(row)" @click="button.action(row)"
                class="mr-1">
                {{ button.label }}
            </el-link>
        </template>
    </template>

    <!-- 可展开列，一般放第一列 -->
    <base-render  v-else-if="field.type == 'expand'" :content="field.render(row[field.prop], row)" />

    <!-- 带有渲染函数的列表 -->
    <base-render  v-else-if="field.render" :content="field.render(row[field.prop], row)" />

    <!-- 内容为html的列 -->
    <div  v-else-if="field.type == 'html'" v-html="row[field.prop]"></div>

    <!-- 标签 -->
    <el-tag v-else-if="field.type == 'tag'" :type="tagType.type">{{ tagType.text }}</el-tag>

    <!-- 带有转换函数的列 -->
    <template v-else-if="field.converter">
        {{ field.converter(row[field.prop], row) }}
    </template>
    <template v-else>
        {{ row[field.prop] }}
    </template>
</template>

<script setup>
import BaseRender from '@/components/base-render.js'

const props = defineProps(["field", "row"])

const tagType = computed(() => {
    return props.field.tagType(props.row[props.field.prop])
})
</script>