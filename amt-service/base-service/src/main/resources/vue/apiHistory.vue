<template>
    <div class="page-title">接口历史管理</div>
    <entity-manager :fields="fields" apiPrefix="/base/api-history" :tree="false"></entity-manager>
</template>
<script setup> import { ref } from "vue"
const fields = ref([
	 {label: "", prop: "guestMode"},
	 {label: "", prop: "content"},
	 {label: "", prop: "name"},
	 {label: "", prop: "remark"},
	 {label: "", prop: "path"},
	 {label: "", prop: "method"},
	 {label: "", prop: "typeId"},
	 {label: "租户编号", prop: "tenantId"},
	 {label: "更新用户", prop: "updateUser"},
	 {label: "是否删除", prop: "deleted"},
	 {label: "添加时间", prop: "createTime", width: "200px", system: true},
 {label: "修改时间", prop: "updateTime", width: "200px", system: true}, ])
</script>