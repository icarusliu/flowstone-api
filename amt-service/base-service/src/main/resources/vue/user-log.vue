<template>
    <!-- 用户操作日志 -->
    <entity-manager :fields="fields" :queryFields="queryFields" apiPrefix="/base/user-log" :tree="false" ref="entityManagerRef" :params="params"
                    operationsWidth="230px">
    </entity-manager>
</template>

<script setup>

import { ref } from "vue"

const fields = [
	{label: "用户id", prop: "userId", max: 64},
	{label: "用户名", prop: "username", max: 255},
	{label: "模块", prop: "module", max: 255},
	{label: "操作类型，CRUD", prop: "type", max: 64},
	{label: "参数", prop: "params", max: 255},
	{label: "IP", prop: "ip", max: 255},
	{label: "是否成功", prop: "success", },
	{label: "耗时，ms", prop: "spentTime", },
	{label: "结果", prop: "result", max: 255},
    {label: "添加时间", prop: "createTime", width: "160px", system: true},
    {label: "修改时间", prop: "updateTime", width: "160px", system: true}
]
const queryFields = []
const entityManagerRef = ref()
const params = ref({})

function reload() {
    entityManagerRef.value.reload()
}
</script>