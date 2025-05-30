<template>
    <!-- 操作列 -->
    <template v-if="type == 'operations'">
        <template v-for="button in field.buttons">
            <el-link
                :type="button.type"
                v-if="!button.visible || button.visible(row)"
                @click="button.action(row, index)"
                :disabled="readonly"
                :class="clazz"
                :icon="button.icon"
                v-perm="button.code"
                class="mr-2"
            >
                {{ button.label }}
            </el-link>
        </template>
    </template>

    <!-- 图片 -->
    <template v-else-if="type == 'image' || type == 'imageUploader'">
        <span v-if="!value"></span>
        <template v-else-if="_.isArray(value)">
            <el-image v-for="item in value" :src="item" class="img" />
        </template>
        <el-image v-else :src="value" class="img"></el-image>
    </template>

    <!-- 可展开列，一般放第一列 -->
    <base-render v-else-if="type == 'expand'" :content="field.render(value, row)" :class="clazz" :key="row.id" />

    <!-- 带有渲染函数的列表 -->
    <base-render v-else-if="field.render" :content="field.render(value, row)" :class="clazz" :key="row.id + '-' + field.prop" />

    <!-- 内容为html的列 -->
    <div v-else-if="type == 'html'" v-html="value" :class="clazz" />

    <!-- 标签 -->
    <el-tag v-else-if="field.tagType" :type="tagType.type" :class="clazz">{{ tagType.text }}</el-tag>

    <!-- 复选 -->
    <el-checkbox v-else-if="type == 'checkbox'" :modelValue="value" @change="change" />

    <!-- switch -->
    <el-switch v-else-if="type == 'switch'" :modelValue="value" @change="change"/>

    <!-- 图标 -->
    <el-icon v-else-if="type == 'iconSelector' || type == 'icon'">
        <component :is="value" />
    </el-icon>

    <span :class="clazz" v-else>{{ defaultShow() }}</span>
</template>

<script setup>
import BaseRender from "../base-render.js";
import * as _ from "lodash";
import { useSysStore } from "@/store/index.js";

const sysStore = useSysStore();
const props = defineProps(["field", "row", "index", "readonly"]);
const value = computed(() => {
    let prop = props.field.prop;
    return _.get(props.row, prop);
});
const type = computed(() => props.field?.type);
const tagType = computed(() => {
    return props.field.tagType(props.row[props.field.prop], props.row);
});

const clazz = computed(() => {
    if (!props.field.class) {
        return;
    }

    let clazz = props.field.class;
    if (clazz instanceof Function) {
        return clazz(props.row[props.field.prop], props.row, { field: props.field });
    } else {
        return clazz;
    }
});

function defaultShow() {
    let prop = props.field.prop;
    let value = _.get(props.row, prop);
    let converter = props.field.converter;
    if (converter) {
        return converter(value, props.row);
    }

    // 如果是select且有options，需要进行转换
    let options = props.field.options;
    if (options && _.isArray(options)) {
        for (var i in options) {
            let option = options[i];
            if (option.value == value) {
                return option.label;
            }
        }
    }

    // 如果是字典项，则需要进行转换
    if (props.field.dict) {
        let dictCode = props.field.dict;
        let dictInfo = sysStore.getDictInfo();
        let dictItems = dictInfo[dictCode]?.items || [];
        for (var i in dictItems) {
            let item = dictItems[i];
            if (item.value == value) {
                return item.name;
            }
        }
    }

    if (value == 0) {
        return 0;
    } else if (!value) {
        return "--";
    } else {
        if (!props.field.unit) {
            return value;
        }
        return value + props.field.unit;
    }
}

function change(val) {
    let change = props.field.changeInTable || props.field.change;
    change && change(val, props.row);
}
</script>

<style lang="scss" scoped>
.img {
    width: 60px;
    height: 60px;
    object-fit: contain;
}
</style>
