<template>
    <el-upload
        :accept="accept"
        action="/api/base/sys-file/upload"
        v-model:fileList="files"
        withCredentials
        :before-upload="beforeUpload"
        :disabled="disabled"
        :multiple="multiple"
        :on-success="uploadSuccess"
        :on-remove="remove"
        :limit="limit"
    >
        <el-link type="primary" v-if="files.length < limit">选择文件...</el-link>
    </el-upload>
</template>
<script setup>
import { onMounted } from "vue";
import * as _ from "lodash";

const props = defineProps({
    accept: String,
    disabled: Boolean,
    multiple: { type: Boolean, default: false },
    limit: { type: Number, default: 1 },
});
const model = defineModel();
const files = ref([]);

onMounted(() => {
    if (model.value) {
        if (props.multiple) {
            model.value.forEach((item) =>
                files.value.push({
                    url: item,
                })
            );
        } else {
            files.value.push({
                url: model.value,
            });
        }
    }
});

function beforeUpload(rawFile) {
    if (rawFile.size / 1024 / 1024 > 500) {
        ElMessage.error("大小不能超出500M");
        return false;
    }
    return true;
}

function remove({ response }) {
    if (!props.multiple) {
        model.value = null;
        return;
    }

    let idx = model.value.indexOf(response.visitUrl);
    model.value.splice(idx, 1);
}

function uploadSuccess(res) {
    if (!props.multiple) {
        model.value = res.visitUrl;
        return;
    }

    model.value.push(res.visitUrl);
}
</script>

<style lang="scss" scoped></style>
