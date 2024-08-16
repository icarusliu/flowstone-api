<template>
    <!-- SQL节点配置 -->
    <el-descriptions :column="1" border>
        <el-descriptions-item label="数据源">
            <ds-select v-model="model.ds" :disabled="!editing" />
        </el-descriptions-item>
        <el-descriptions-item label="SQL语句">
            <el-link type="primary" @click="showDialog" class="text-normal">{{ editing ? '编辑' : '查看' }}</el-link>
        </el-descriptions-item>
    </el-descriptions>

    <!-- SQL参数配置 -->
    <div class="p-4">
        <NodeParams title="参数配置" v-model="model.params" :dag="dag" :readonly="!editing" :inputParams="inputParams">
        </NodeParams>
    </div>

    <div class="remark-panel m-4">
        <div class="page-title title">
            配置帮助：
        </div>
        <div>
            <p>
                1、SQL语句支持mybatis语法，比如foreach/if/where等，参数使用#{}或者${}来表示；
            </p>
            <p>2、可以不配置任何参数，此时默认会将请求的输入参数、上级节点的输出参数，在执行时传递给SQL处理，其中请求参数会存储在名称为request的对象中，比如通过#{request.name}可以获取请求参数name的值；
                而对于上级节点来说，其输出会存储在名称为其节点编码的对象中，比如存在一个上级节点编码是node1，其中有一个参数名为name，那么使用这个参数可以写成：#{node1.name}
            </p>
            <p>3、如果配置了参数，则会以配置的参数为准，输入参数、上级节点的输出参数不会自动传给SQL使用</p>
            <p>4、如果配置了参数且只有一个类型是列表的参数，且SQL语句是进行插入操作时，会自动分批进行处理；防止一次插入数据量过大导致异常 </p>
        </div>
    </div>

    <!-- SQL编辑弹窗 -->
    <el-dialog v-model="visible" title="脚本编辑" width="80%">
        <template #header>
            <div class="d-flex v-center">
                <div>脚本编辑</div>
                <el-popover title="说明" :width="600" trigger="hover" placement="bottom-end">
                    <div class="text-left">
                        <p>
                            1、SQL语句支持mybatis语法，比如foreach/if/where等，参数使用#{}或者${}来表示；
                        </p>
                        <p>2、传参：如果未配置参数，默认会将请求的输入参数、上级节点的输出参数当成SQL执行时的输入参数，比如#{request.a}可以从接口请求中获取名称为a的参数值，又如：#{node1.a}将会从node1这个节点上取名称为a的参数值
                        </p>
                        <p>3、如果配置了参数，则会以配置的参数为准，输入参数、上级节点的输出参数不会自动传给SQL使用</p>
                    </div>
                    <template #reference>
                        <el-icon class="cursor-pointer ml-4">
                            <QuestionFilled />
                        </el-icon>
                    </template>
                </el-popover>
            </div>
        </template>
        <monacoEditor height="600px" v-model="script" language="sql" :editorOptions="{readOnly: !editing}"/>
        <template #footer>
            <div>
                <el-link type="primary" class="mr-4" @click="visible = false">取消</el-link>
                <el-button type="primary" @click="doSaveSql" :disabled="!editing">保存</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup>
import dsSelect from './components/ds-select.vue';
import monacoEditor from '@/components/monaco-editor.vue'
import NodeParams from './components/node-params.vue';

const visible = ref(false)
const script = ref()
const props = defineProps(["editing", "dag", "inputParams"])
const model = defineModel()

// 保存SQL语句
function doSaveSql() {
    model.value.sql = script.value
    visible.value = false
}

function showDialog() {
    script.value = model.value.sql
    visible.value = true
}

</script>