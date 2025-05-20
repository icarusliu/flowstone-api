<template>
    <!-- 多标签导航 -->
    <el-scrollbar class="tags-tab">
        <router-link v-for="tab, index in tabs" :to="tab.path" class="tab-item v-center" :class="{ active: tab.path == route.path }">
            <span>{{ tab.label }}</span>
            <el-icon class="ml-1 close" @click.prevent.stop="closeTab(tab, index)">
                <Close />
            </el-icon>
        </router-link>
    </el-scrollbar>
</template>
<script setup>
    import { ref } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
import { useSysStore } from '../store';

    const tabs = ref([])
    const route = useRoute()
    const router = useRouter()
    const sysStore = useSysStore()

    watch(route, () => {
        let exists = tabs.value.find(item => item.path == route.path);
        if (!exists) {
            tabs.value.push({
                label: route.meta.title,
                path: route.path,
                fullPath: route.fullPath,
                name: route.name
            })

            sysStore.addRoute(route.name)
        }
    }, {
        immediate: true
    })

    function closeTab(tab, idx) {
        // 如果是当前页签，需要切换到上一页签
        let item = tabs.value.splice(idx, 1)
        sysStore.removeRoute(item.name)
        if (tab.path == route.path) {
            if (tabs.value.length > 0) {
                const path = tabs.value[tabs.value.length - 1].fullPath;
                router.push(path)
            }
        }
    }
</script>

<style lang='scss' scoped>
    .tags-tab {
        height: auto;
        padding: 6px 10px;
        background: #fff;
        box-shadow: 0 0 5px #aaa;
        z-index: 1;

        .tab-item {
            float: left;
            height: 30px;
            background: #fff;
            padding: 0px 10px;
            box-sizing: border-box;
            border: 1px solid #eee;
            margin-right: 8px;
            text-decoration: none;
            color: #666;

            &.active {
                background: var(--sub_color);
                color: #fff;

                &::before {
                    content: '';
                    width: 8px;
                    height: 8px;
                    background: #fff;
                    border-radius: 5px;
                    margin-right: 4px;
                    top: 8px;
                }
            }

            .close:hover {
                background: #aaa;
                border-radius: 50%;
                color: #fff;
            }
        }
    }
</style>