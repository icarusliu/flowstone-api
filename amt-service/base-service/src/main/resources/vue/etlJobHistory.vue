<template>
    <div class="page-title">ETL任务历史记录管理</div>
    <entity-manager :fields="fields" apiPrefix="/base/etl-job-history" :tree="false"></entity-manager>
</template>
<script setup> import { ref } from "vue"
const fields = ref([
	 {label: "", prop: "jobId"},
	 {label: "", prop: "typeId"},
	 {label: "", prop: "type"},
	 {label: "", prop: "code"},
	 {label: "", prop: "name"},
	 {label: "", prop: "enabled"},
	 {label: "", prop: "sort"},
	 {label: "", prop: "autoTrigger"},
	 {label: "", prop: "config"},
	 {label: "", prop: "metadata"},
	 {label: "", prop: "cron"},
	 {label: "", prop: "version"},
	 {label: "", prop: "publishedVersion"},
	 {label: "租户编号", prop: "tenantId"},
	 {label: "更新用户", prop: "updateUser"},
	 {label: "是否删除", prop: "deleted"},
	 {label: "添加时间", prop: "createTime", width: "200px", system: true},
 {label: "修改时间", prop: "updateTime", width: "200px", system: true}, ])
</script>