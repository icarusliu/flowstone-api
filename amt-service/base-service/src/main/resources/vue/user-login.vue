<template>
    <!-- 用户登录日志 -->
    <entity-manager :fields="fields" :queryFields="queryFields" apiPrefix="/base/user-login" :tree="false" ref="entityManagerRef" :params="params"
                    operationsWidth="230px">
    </entity-manager>
</template>

<script setup>

import { ref } from "vue"

const fields = [
	{label: "用户名", prop: "username", max: 255},
	{label: "登录时间", prop: "loginTime", },
	{label: "ip", prop: "ip", max: 255},
	{label: "操作系统", prop: "os", max: 255},
	{label: "浏览器", prop: "browser", max: 255},
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