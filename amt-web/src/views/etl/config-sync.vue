<template>
    <el-form :model="model" ref="formRef">
        <el-form-item label="高级模式">
            <el-switch v-model="model.advanced" :disabled="disabled" />
        </el-form-item>

        <template v-if="model.advanced == false">
            <div class="space-between sync-info">
                <div class="left mr-4">
                    <el-form-item label="来源数据库" required prop="sourceDs">
                        <el-select v-model="model.sourceDs" :disabled="disabled" @change="loadSourceTables">
                            <el-option v-for="ds in dses" :label="ds.name" :value="ds.code" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="来源表" required prop="sourceTable">
                        <el-select filterable v-model="model.sourceTable" :disabled="disabled" @change="sourceTableChanged">
                            <el-option v-for="sourceTable in sourceTables" :label="sourceTable" :value="sourceTable"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="查询条件" prop="sourceWhere">
                        <el-input v-model="model.sourceWhere" :disabled="disabled" placeholder="查询条件，以where开头" type="textarea" />
                    </el-form-item>
                </div>

                <div class="right ml-4">
                    <el-form-item label="目标数据源" required prop="destDs">
                        <el-select v-model="model.destDs" :disabled="disabled" @change="loadDestTables">
                            <el-option v-for="ds in dses" :label="ds.name" :value="ds.code" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="目标表" required prop="destTable">
                        <el-select v-model="model.destTable" :disabled="disabled" filterable @change="destTableChanged">
                            <el-option v-for="table in destTables" :label="table" :value="table" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="存储规则" prop="storeRule">
                        <el-radio-group v-model="model.storeRule" @change="storeRuleChanged" :disabled="disabled">
                            <el-radio-button value="normal" label="直接存储" />
                            <el-radio-button value="clear" label="清空存储" />
                            <el-radio-button value="dayPart" label="按天分区" />
                            <el-radio-button value="montPart" label="按月分区" />
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="预处理语句" prop="prepareSql" v-if="model.storeRule == 'normal' || !model.storeRule">
                        <el-input v-model="model.prepareSql" :disabled="disabled" placeholder="预处理语句，删除相关数据或清空表等" type="textarea" />
                    </el-form-item>
                </div>
            </div>

            <el-form-item label="字段映射" required prop="fieldReflect">
                <edit-table :fields="fieldReflectFields" v-model="model.fieldReflect" height="38vh" :showIndex="true"
                    :showOperations="false" :showNew="false" :readonly="disabled" />
            </el-form-item>
        </template>

        <div class="space-between sync-info" v-else>
            <!-- 数据同步配置 -->
            <div class="mr-4">
                <el-form-item label="来源数据源" required prop="sourceDs">
                    <el-select v-model="model.sourceDs" :disabled="disabled">
                        <el-option v-for="ds in dses" :label="ds.name" :value="ds.code" />
                    </el-select>
                </el-form-item>
                <el-form-item label="源表查询语句" required prop="sourceSql">
                    <monacoEditor height="60vh" v-model="model.sourceSql" language="sql" :editorOptions="{ readOnly: disabled }" />
                </el-form-item>
            </div>
            <div class="ml-4">
                <el-form-item label="目标数据源" required prop="destDs">
                    <el-select v-model="model.destDs" :disabled="disabled">
                        <el-option v-for="ds in dses" :label="ds.name" :value="ds.code" />
                    </el-select>
                </el-form-item>
                <el-form-item label="目标写入语句" required prop="destSql">
                    <monacoEditor height="34vh" v-model="model.destSql" language="sql" :editorOptions="{ readOnly: disabled }" />
                </el-form-item>
                <el-form-item label="预处理语句" prop="prepareSql">
                    <monacoEditor height="22vh" v-model="model.prepareSql" language="sql" :editorOptions="{ readOnly: disabled }" />
                </el-form-item>
            </div>
        </div>
    </el-form>
</template>
<script setup>
    import monacoEditor from '@/components/monaco-editor.vue'
    import editTable from '@/components/edit-table/index.vue'
    import { reactive, readonly } from 'vue'
    import https from '@/utils/https'
    import { ElRadioButton } from 'element-plus'

    const model = defineModel()
    const props = defineProps({
        dses: { type: Array, required: true },
        disabled: { type: Boolean }
    })
    const formRef = ref()
    const sourceTables = reactive([])
    const destTables = reactive([])
    const fieldReflectFields = reactive([
        { label: '来源字段', prop: 'source', type: 'select', options: [] },
        { label: '目标字段', prop: 'dest', type: 'select', options: [] }
    ])

    function loadSourceTables() {
        sourceTables.splice(0, sourceTables.length);
        https.get('/base/ds/tables', { ds: model.value.sourceDs }).then(resp => {
            if (resp && resp.length) {
                resp.forEach(item => sourceTables.push(item))
            }

            model.value.sourceTable = ''
            model.value.fieldReflect = []
        })
    }

    function loadDestTables() {
        destTables.splice(0, destTables.length);
        https.get('/base/ds/tables', { ds: model.value.destDs }).then(resp => {
            if (resp && resp.length) {
                resp.forEach(item => destTables.push(item))
            }

            model.value.destTable = ''
            model.value.fieldReflect && model.value.fieldReflect.forEach(item => {
                item.dest = ''
            })
        })
    }

    function sourceTableChanged() {
        // 查询目来源表字段
        https.get('/base/ds/table-fields', {
            ds: model.value.sourceDs,
            table: model.value.sourceTable
        }).then(resp => {
            fieldReflectFields[0].options = resp
            let fieldReflect = model.value.fieldReflect = []
            let destFields = fieldReflectFields[1].options || []

            resp.forEach(field => {
                fieldReflect.push({
                    source: field,
                    dest: destFields.includes(field) ? field : ''
                })
            })
        })
    }

    function destTableChanged() {
        https.get('/base/ds/table-fields', {
            ds: model.value.destDs,
            table: model.value.destTable
        }).then(resp => {
            fieldReflectFields[1].options = ['', ...resp]
            let fieldReflect = model.value.fieldReflect
            if (!fieldReflect) {
                return
            }

            fieldReflect.forEach(row => {
                let source = row.source
                if (resp.includes(source)) {
                    row.dest = source
                } else {
                    row.dest = ''
                }
            })
        })
    }

    function storeRuleChanged() {
        if (model.value.storeRule == 'clear') {
            model.value.prepareSql = 'truncate table ' + model.value.destTable
        } else {
            model.value.prepareSql = ''
        }
    }

    function validate(callback) {
        formRef.value.validate(resp => {
            callback(resp)
        })
    }

    defineExpose({
        validate
    })
</script>

<style lang='scss' scoped>
    .sync-info {
        >div {
            width: 50%;

        }
    }
</style>