<template>
    <div class="page-title">客户端接口列表管理</div>
    <entity-manager :fields="fields" apiPrefix="/base/client-api" :tree="false"></entity-manager>
</template>
<script setup> import { ref } from "vue"
const fields = ref([
	 {label: "客户端id", prop: "clientId"},
	 {label: "接口id", prop: "apiId"},
	 {label: "添加时间", prop: "createTime", width: "200px", system: true},
 {label: "修改时间", prop: "updateTime", width: "200px", system: true}, ])
</script>