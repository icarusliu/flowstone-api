<template>
    <div class="page-title">ETL已发布任务管理</div>
    <entity-manager :fields="fields" apiPrefix="/base/etl-job-published" :tree="false"></entity-manager>
</template>
<script setup> import { ref } from "vue"
const fields = ref([
	 {label: "", prop: "type"},
	 {label: "", prop: "code"},
	 {label: "", prop: "name"},
	 {label: "", prop: "autoTrigger"},
	 {label: "", prop: "config"},
	 {label: "", prop: "metadata"},
	 {label: "", prop: "cron"},
	 {label: "", prop: "publishedVersion"},
	 {label: "租户编号", prop: "tenantId"},
	 {label: "更新用户", prop: "updateUser"},
	 {label: "是否删除", prop: "deleted"},
	 {label: "添加时间", prop: "createTime", width: "200px", system: true},
 {label: "修改时间", prop: "updateTime", width: "200px", system: true}, ])
</script>