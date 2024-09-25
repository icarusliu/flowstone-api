<template>
	<div class="page-title">客户端管理</div>
	<entity-manager :fields="fields" apiPrefix="/base/client" :tree="false">
		<template #prefixButtons="{ row }">
			<el-link class="mr-2" type="warning" v-if="!row.disabled" @click="toggleDisabled(row)">禁用</el-link>
			<el-link class="mr-2" type="success" v-else @click="toggleDisabled(row)">启用</el-link>
		</template>
	</entity-manager>
</template>
<script setup>
import { ref, h } from "vue"
import { ElMessage, ElLink, ElMessageBox } from "element-plus";
import * as entityApis from '@/apis/entity.js'
import * as managerApis from '@/apis/manager.js'
import useClipboard from 'vue-clipboard3'

const clipboard = useClipboard()

const fields = ref([
	{ label: "名称", prop: "name" },
	{ label: "id", prop: "id", system: true, width: "180px" },
	{
		label: '密钥', prop: 'secret', system: true, width: '240px', render: (val, row) => {
			return h('div', {
				class: 'v-center'
			}, [
				h('span', '******'),
				h(ElLink, {
					type: 'primary',
					class: 'mx-2',
					onClick: () => {
						clipboard.toClipboard(val).then(() => {
							ElMessage.success('复制成功')
						})
					}
				}, () => '复制'),
				h(ElLink, {
					type: 'primary',
					onClick: () => {
						ElMessageBox.confirm('确定重置当前密钥？').then(() => {
							managerApis.resetClientSecret(row.id).then(resp => {
								ElMessage.success('重置成功')
								row.secret = resp
							})
						})
					}
				}, () => "重置")
			])
		}
	},
	{
		label: "停用", prop: "disabled", type: 'checkbox', needNew: false, needUpdate: false, width: "60px", tagType: (val, row) => {
			if (val == 1) {
				return {
					type: 'warning',
					text: '是'
				}
			}

			return {
				type: 'success',
				text: '否'
			}
		}
	},
	{ label: "描述", prop: "remark" },
	// { label: "应用白名单", prop: "whiteIps" },
	{ label: "添加时间", prop: "createTime", width: "200px", system: true },
	{ label: "修改时间", prop: "updateTime", width: "200px", system: true },])

function toggleDisabled(row) {
	entityApis.save('/base/client', {
		id: row.id,
		disabled: !row.disabled
	}).then(resp => {
		ElMessage.success('操作成功')
		row.disabled = !row.disabled
	})
}
</script>