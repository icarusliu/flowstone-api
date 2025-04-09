<template>
    <div class="d-flex-col config-panel">
        <!-- 节点公共配置 -->
        <el-descriptions :column="1" border>
            <el-descriptions-item label="节点编码">
                <el-input v-model="currentNode.code" @change="val => updateField('code', val)" :disabled="!editing" />
            </el-descriptions-item>
            <el-descriptions-item label="节点名称">
                <el-input v-model="currentNode.name" @change="val => updateField('name', val)" :disabled="!editing" />
            </el-descriptions-item>
            <el-descriptions-item label="节点类型">{{ currentNode.typeName || currentNode.type }}</el-descriptions-item>
            <el-descriptions-item label="节点执行条件">
                <el-input v-model="currentNode.config.condition" placeholder="请输入节点执行条件，OGNL表达式" :disabled="!editing">
                    <template #prefix>
                        <el-tooltip>
                            <el-icon class="cursor-pointer">
                                <QuestionFilled />
                            </el-icon>

                            <template #content>
                                说明：使用OGNL表达式返回节点是否需要执行标志，当返回false或1时，当前节点不执行；当返回2时，当前节点及后续节点均不执行；其它情况无影响<br />
                                假设请求参数中包含有参数a，上一节点编码为node1，输出参数为b，
                                <ul>
                                    <li>表达式：request.a > 1 表示当请求参数a大于1时执行，其它情况不执行</li>
                                    <li>表达式：node1.b > 100表示当节点node1输出参数b值大于100时执行，其它情况不执行</li>
                                    <li>表达式：node1.b > 100 ? 1 : 2，表示节点node1输出参数b值大于100时，当前节点不执行，其它情况时，当前及后续节点均不执行</li>
                                    <li>表达式：request.a > 1 and node1.b > 100，表示当请求参数a大于1并且node1的输出参数b大于100时执行，其它情况不执行</li>
                                    <li>表达式：request.a > 1 or node1.b > 100，表示当请求参数a大于1或者node1的输出参数b大于100时执行，其它情况不执行</li>
                                </ul>
                            </template>
                        </el-tooltip>
                    </template>
                </el-input>
                <div class="remark">
                </div>
            </el-descriptions-item>
        </el-descriptions>

        <!-- 表节点配置 -->
        <table-config v-if="currentNode.type == 'table'" :dag="dag" v-model="currentNode.config" :inputParams="inputParams"
            :editing="editing">
        </table-config>

        <!-- SQL节点配置 -->
        <sql-config v-else-if="currentNode.type == 'sql'" v-model="currentNode.config" :inputParams="inputParams" :dag="dag"
            :editing="editing">
        </sql-config>

        <!-- JS节点配置 -->
        <js-config v-else-if="currentNode.type == 'js'" :readonly="!editing" v-model="currentNode.config" />

        <!-- Groovy节点配置 -->
        <groovyConfig v-else-if="currentNode.type == 'groovy'" v-model="currentNode.config" :readonly="!editing"
            :inputParams="inputParams" />

        <!-- Http节点配置 -->
        <httpConfig v-else-if="currentNode.type == 'http'" v-model="currentNode.config" :dag="dag" :readonly="!editing"
            :inputParams="inputParams" />

        <ScriptDialog lang="javascript" v-model:visible="visible" v-model="currentNode.config.condition" :readonly="!editing">
        </ScriptDialog>
    </div>
</template>

<script setup>
    import { computed } from 'vue'
    import $ from 'jquery'
    import TableConfig from './node-config/table-config.vue';
    import SqlConfig from './node-config/sql-config.vue';
    import jsConfig from './node-config/js-config.vue';
    import groovyConfig from './node-config/groovy-config.vue';
    import httpConfig from './node-config/http-config.vue';
    import ScriptDialog from '@/components/script-dialog.vue';

    const props = defineProps(["dag", "editing", "inputParams"])
    const visible = ref(false)

    const currentNode = computed(() => {
        return props.dag.currentNode?.options || {
            config: {}
        }
    })

    function updateField(field, val) {
        props.dag.currentNode.options[field] = val
        if (field == 'name') {
            $(props.dag.currentNode.dom).html(val)
        }
    }

    function showCondition() {
        if (!currentNode.value.config.condition) {
            currentNode.value.config.condition = `function func(params) {
    // 节点及后续节点是否执行标志，为0时不做限制；为1时当前节点不执行；为2时后续节点不执行；
    return 0; 
}`
        }

        visible.value = true
    }
</script>

<style lang="scss" scoped>
    :deep() {
        .el-descriptions__label.el-descriptions__cell.is-bordered-label {
            width: 160px;
        }
    }

    .config-panel {
        overflow-y: auto;
    }
</style>
