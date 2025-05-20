<template>
    <el-upload
        list-type="picture-card"
        accept=".png,.jpeg,.jpg,.webp"
        action="/api/base/sys-file/upload"
        v-model:fileList="images"
        withCredentials
        :before-upload="beforeAvatarUpload"
        :disabled="disabled"
        :on-success="uploadSuccess"
        :on-remove="remove"
        :multiple="multiple"
        :limit="limit"
        :class="images.length >= limit ? 'hide-upload' : ''"
        :on-exceed="onExceed"
    >
        <el-icon class="icon" v-if="images.length < limit">
            <Plus />
        </el-icon>
    </el-upload>
</template>
<script setup>
import { onMounted } from "vue";

const props = defineProps({
    disabled: Boolean,
    multiple: { type: Boolean, default: false },
    limit: { type: Number, default: 1 },
});
const model = defineModel({});
const images = ref([]);

onMounted(() => {
    if (model.value) {
        if (props.multiple) {
            model.value.forEach((image) => {
                images.value.push({ url: image });
            });
        } else {
            images.value.push({
                url: model.value,
            });
        }
    }
});

function beforeAvatarUpload(rawFile) {
    if (rawFile.size / 1024 / 1024 > 10) {
        ElMessage.error("图片大小不能超出10M");
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

function onExceed(val) {
    hb.error("上传数量超出限制，最多只能上传" + props.limit + "张图片")
}
</script>

<style lang="scss" scoped>
.hide-upload {
    :deep() {
        .el-upload {
            display: none;
        }
    }
}
</style>
