<template>
    <el-date-picker
        v-model="finalModel"
        :type="type"
        :clearable="clearable"
        :value-format="format"
        :format="format"
        :disabledDate="disabledDate"
        @change="doChange"
        :disabled="disabled"
    />
</template>
<script setup>
import { computed, ref } from "vue";

const props = defineProps({
    type: {
        type: String,
        default: "date",
    },
    format: {
        type: String,
        default: "YYYY-MM-DD",
    },
    disabledDate: {
        type: Function,
    },
    disabled: { type: Boolean, default: false },
    clearable: { type: Boolean, default: true },
});
const beginDate = defineModel();
// 用于区间时的情况
const endDate = defineModel("endDate");
const emits = defineEmits(["change"]);

const finalModel = computed({
    get() {
        if (!beginDate.value && !endDate.value) {
            return;
        }

        if (!props.type.endsWith("range")) {
            // 非区间情况
            return beginDate.value;
        }

        // 区间情况
        return [beginDate.value, endDate.value];
    },

    set(val) {
        if (!val) {
            beginDate.value = null;
            endDate.value = null;
            return;
        }

        if (props.type.endsWith("range")) {
            if (!val.length) {
                beginDate.value = null;
                endDate.value = null;
                return;
            }

            beginDate.value = val[0];
            if (val.length == 2) {
                endDate.value = val[1];
            } else {
                endDate.value = null;
            }

            return;
        }

        // 非区间的情况
        beginDate.value = val;
    },
});

function doChange() {
    emits("change", beginDate.value, endDate.value);
}
</script>

<style lang="scss" scoped></style>
