<template>
    <div class="page-title">客户端管理</div>
    <entity-manager :fields="fields" apiPrefix="/base/client" :tree="false"></entity-manager>
</template>
<script setup> import { ref } from "vue"
const fields = ref([
{label: "id", prop: "id"},
	 {label: "名称", prop: "name"},
	 {label: "停用", prop: "disabled"},
	 {label: "描述", prop: "remark"},
	 {label: "应用白名单", prop: "whiteIps"},
	 {label: "添加时间", prop: "createTime", width: "200px", system: true},
 {label: "修改时间", prop: "updateTime", width: "200px", system: true}, ])
</script>