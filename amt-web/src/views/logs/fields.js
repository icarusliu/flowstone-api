import * as clientApis from '@/apis/client'

// 查询字段列表
export const queryFields = [
    {label: '接口关键字', prop: 'key', placeholder: '请输入接口关键字'},
    {label: '客户端', prop: 'clientId', type: 'select', options: () => {
        return clientApis.getAllClients().then(resp => {
            return resp.map(item => {
                return {
                    label: item.name,
                    value: item.id
                }
            })
        })
    }},
    {label: '状态', prop: 'status', type: 'select', options: [
        {label: '成功', value: '0'},
        {label: '失败', value: '1'}
    ]},
]


// 表字段列表
export const tableFields = [
    { label: '接口', prop: 'apiName', width: '300px' },
    { label: '地址', prop: 'apiPath', converter: val => '/dua/' + val },
    { label: '执行时间', prop: 'createTime', width: '150px' },
    { label: '执行状态', prop: 'status', width: '80px', type: 'tag', tagType: val => {
        if (val == 0) {
            return {
                type: 'success',
                text: '成功'
            } 
        } else {
            return {
                type: 'danger',
                text: '失败'
            }
        }
    }},
    { label: '执行耗时', prop: 'spentTime', width: '80px', converter: val => val + 'ms' },
    // { label: '异常信息', prop: 'errorMsg' },
    {
        label: '', type: 'expand', prop: 'result', render: (val, row) => {
            let arr = []
            if (row.result) {
                row.result.split('\r\n').forEach(line => {
                    arr.push(h('div', { class: line.startsWith('\t') ? 'ml-4' : '' }, line))
                })
            }

            return h('div', { class: 'p-4' }, [
                h('div', { class: 'page-title' }, '请求参数'),
                h('div', row.params),
                h('div', { class: 'page-title mt-8' }, row.status == 0 ? '返回结果' : '异常详情'),
                ...arr
            ])
        }
    }
]