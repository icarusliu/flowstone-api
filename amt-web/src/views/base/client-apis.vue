<template>
	<div class="page-title">客户端接口授权——{{ clientName }}</div>
	<div class="mb-2">
		<el-button type="primary" @click="showAdd">添加接口</el-button>
		<el-button type="warning" @click="doDelete">删除</el-button>
	</div>
	<base-table :fields="fields" :dataSupplier="dataSupplier" :showIndex="false" ref="tableRef">
	</base-table>

	<el-dialog v-model="visible" title="添加接口">
		<base-table :fields="fields" :dataSupplier="loadNotBindApis" :showIndex="false" ref="selectRef"/>

		<div class="align-center">
			<el-button type="primary" @click="doBind">保存</el-button>
			<el-button @click="visible = false">关闭</el-button>
		</div>
	</el-dialog>
</template>
<script setup>
import { ref, h } from "vue"
import * as clientApis from '@/apis/client.js'
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";

const router = useRouter()
const clientId = computed(() => {
	return router.currentRoute.value.params.id
})
const clientName = computed(() => {
	return router.currentRoute.value.query.name
})
const fields = ref([
	{label: '选择', type: 'selection'},
	{ label: "接口id", prop: "id", },
	{ label: "接口名称", prop: "name" },
	{ label: "添加时间", prop: "createTime", width: "160px" }
])
const visible = ref(false)
const selectRef = ref()
const tableRef = ref()

function dataSupplier(params) {
	return clientApis.loadClientApis({
		clientId: clientId.value,
		...params
	});
}

// 加载未绑定的接口列表
function loadNotBindApis(params) {
	return clientApis.loadClientNotBindApis({
		clientId: clientId.value,
		...params
	})
}

function showAdd() {
	visible.value = true
	selectRef.value && selectRef.value.reload()
}

// 绑定客户端与接口
function doBind() {
	let selection = selectRef.value.getSelectionRows()
	if (!selection || !selection.length) {
		ElMessage.warning("请选择接口")
		return
	}

	let apiIds = selection.map(item => item.id)
	clientApis.bindClientAndApis({
		clientId: clientId.value,
		apiIds
	}).then(() => {
		ElMessage.success('保存成功')
		tableRef.value.reload()
		selectRef.value.reload()
	})
}

// 删除客户端与接口绑定关系
function doDelete() {
	let selection = tableRef.value.getSelectionRows()

	if (!selection || !selection.length) {
		ElMessage.warning("请选择要删除的记录")
		return
	}
	let apiIds = selection.map(item => item.id)
	ElMessageBox.confirm('确定删除所选择的记录？').then(() => {
		clientApis.deleteClientAndApis({
			clientId: clientId.value,
			apiIds
		}).then(() => {
			ElMessage.success("删除成功")
			tableRef.value.reload()
		})
	})
}
</script>