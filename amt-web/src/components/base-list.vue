<template>
    <!-- 列表，与树形不一样的在于只有一级 -->
    <div class="base-list d-flex-col">
        <div class="top px-2 space-between v-center">
            <label class="title text-bold">{{ title }}</label>
            <slot name="topButtons"> </slot>
        </div>
        <div class="content flex-auto mt-2">
            <draggable :list="model" :itemKey="itemKey" @end="dragEnd">
                <div class="list-item cursor-pointer" @click="selectItem()" :class="{ active: !current }" v-if="showAll">全部</div>
                <template #item="{ element: item, index }">
                    <div class="item cursor-pointer space-between mb-2" :class="{ active: current && current.id == item.id }" @click="selectItem(item)">
                        <slot :item="item" :index="index">
                            <label class="cursor-pointer">{{ item[labelField] }}</label>
                        </slot>
                        <div class="icons">
                            <slot name="itemIcons" :item="item" :index="index"></slot>
                        </div>
                    </div>
                </template>
            </draggable>
        </div>
    </div>
</template>

<script setup>
import draggable from "vuedraggable";

const props = defineProps({
    labelField: {
        type: String,
        default: "label",
    },
    title: {
        type: String,
    },
    itemKey: {
        type: String,
        default: "id",
    },
    showAll: {
        type: Boolean,
        default: false,
    },
});

const model = defineModel({
    default: () => {
        return [];
    },
});

const current = defineModel("current");
const emits = defineEmits(["select"]);

function selectItem(item) {
    current.value = item;
    emits("select", item);
}

function dragEnd() {
    emits("sortChange");
}
</script>

<style lang="scss" scoped>
.base-list {
    border: 1px solid var(--follow_border_color);

    .top {
        border-bottom: 1px solid var(--sub_border_color);
        padding: 12px 12px;

        .title {
            font-size: 14px;
            font-weight: bold;
        }
    }

    .content {
        line-height: 44px;
        height: calc(100% - 44px);
        overflow-y: auto;
    }

    .item {
        line-height: 34px;
        padding: 0 16px;
        border-radius: 5px;

        .icons {
            display: none;
        }

        &:hover {
            background-color: #fff;

            .icons {
                display: block;
            }
        }

        &.active {
            font-weight: bold;
            color: var(--primary_color);
        }
    }
}
</style>
