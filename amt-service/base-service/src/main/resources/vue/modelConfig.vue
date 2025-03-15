<template>
    <div class="page-title">模型配置管理</div>
    <entity-manager :fields="fields" apiPrefix="/base/model-config" :tree="false"></entity-manager>
</template>
<script setup> import { ref } from "vue"
const fields = ref([
	 {label: "", prop: "listConfig"},
	 {label: "", prop: "listFields"},
	 {label: "", prop: "formConfig"},
	 {label: "", prop: "formFields"},
	 {label: "租户编号", prop: "tenantId"},
	 {label: "更新用户", prop: "updateUser"},
	 {label: "是否删除", prop: "deleted"},
	 {label: "添加时间", prop: "createTime", width: "200px", system: true},
 {label: "修改时间", prop: "updateTime", width: "200px", system: true}, ])
</script>