<template>
    <!-- 操作列 -->
    <template v-if="type == 'operations'">
        <template v-for="button in field.buttons">
            <el-link
                :type="button.type"
                v-if="!button.visible || button.visible(row)"
                @click="button.action(row, index)"
                :class="clazz"
                v-perm="button.code"
                class="mr-1"
            >
                {{ button.label }}
            </el-link>
        </template>
    </template>

    <el-image v-else-if="type == 'image'" :src="value" />

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
const props = defineProps(["field", "row", "index"]);
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
            return value
        }
        return value + props.field.unit;
    }
}

function change(val) {
    let change = props.field.changeInTable || props.field.change;
    change && change(val, props.row);
}
</script>
