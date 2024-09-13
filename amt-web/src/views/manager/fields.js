const options = [
    { label: 'cookie', value: 'cookie' },
    { label: '请求头', value: 'header' },
    { label: '请求体', value: 'body' },
    { label: '查询参数', value: 'query' },
];

export const dsFields = [
    { label: '编码', prop: 'code', required: true, width: '80px', maxLength: 16 },
    { label: '名称', prop: 'name', required: true, width: '160px' },
    {
        label: '类型', prop: 'type', required: true,
        default: 'mysql',
        type: 'radio', options: [
            { label: 'mysql', value: 'mysql' }
        ],
        width: '80px'
    },
    { label: 'URL', prop: 'url', required: true },
    { label: '用户名', prop: 'username', required: true, width: '80px' },
    { label: '添加时间', prop: 'createTime', width: '150px', system: true },
    { label: '修改时间', prop: 'updateTime', width: '150px', system: true },
];

export const getAuthTableFields = ({ editAction, deleteAction }) => {
    return [
        { label: '名称', prop: 'name' },
        {
            label: '类型', prop: 'type', converter: val => {
                if (val == 'static') {
                    return '静态'
                }

                return '动态'
            }
        },
        {
            label: '操作', type: 'operations', width: '200px', buttons: [
                { label: '编辑', type: 'primary', action: editAction },
                { label: '删除', type: 'warning', action: deleteAction }
            ]
        }
    ]
};

const paramOptions = [
    { label: '直接值', value: 'value' },
    { label: 'cookie', value: 'cookie' },
    { label: '请求头', value: 'headers' },
    { label: '请求参数', value: 'params' },
    { label: 'JS', value: 'js' },
    { label: 'groovy', value: 'groovy' }
]

export const authFormFields = [
    { label: '名称', prop: 'name', required: true },
    {
        label: '类型', prop: 'type', type: 'radioGroup', default: 'static', options: [
            { label: '静态', value: 'static' },
            { label: '动态', value: 'dynamic' }
        ]
    },
    {
        label: '参数', prop: 'params', type: 'editTable', required: true, default: {
            target: 'cookie',
            source: 'value'
        }, display: form => form.type == 'static', fields: [
            { label: '参数名', prop: 'key' },
            {
                label: '参数来源', prop: 'source', type: 'select', options: paramOptions
            },
            {
                label: '参数配置', prop: 'value', script: (row) => {
                    if (row.source == 'js') {
                        return 'javascript'
                    } else if (row.source == 'groovy') {
                        return 'groovy'
                    }
                }
            },
            { label: '目标方', prop: 'target', type: 'select', options },
        ], validation: {
            validator: (rule, val, callback) => {
                if (!val || !val.length) {
                    callback(new Error("参数不能为空"))
                    return
                }

                callback()
            }
        }
    },
    { label: '认证地址', prop: 'authUrl', display: form => form.type == 'dynamic', required: true },
    {
        label: '认证类型', prop: 'dynamicType', display: form => form.type == 'dynamic', type: 'radioGroup', options: [
            { label: 'GET', value: 'get' },
            { label: 'POST', value: 'post' }
        ]
    },
    {
        label: '认证参数', prop: 'dynamicParams', display: form => form.type == 'dynamic', type: 'editTable', fields: [
            { label: '参数', prop: 'key' },
            { label: '值', prop: 'value' }
        ],
    },
    {
        label: '目标参数', prop: 'targetParams', display: form => form.type == 'dynamic', required: true, type: 'editTable', default: {
            target: 'cookie',
            source: 'body'
        }, fields: [
            { label: '参数名', prop: 'key' },
            {
                label: '参数来源', prop: 'source', type: 'select', options: [
                    { label: '直接值', value: 'value' },
                    { label: '返回Cookie', value: 'cookie' },
                    { label: '返回体', value: 'body' },
                    { label: '返回头', value: 'headers' },
                    { label: 'JS', value: 'js' },
                    { label: 'groovy', value: 'groovy' }
                ]
            },
            {
                label: '参数配置', prop: 'value', script: (row) => {
                    if (row.source == 'js') {
                        return 'javascript'
                    } else if (row.source == 'groovy') {
                        return 'groovy'
                    }
                }
            },
            { label: '目标', prop: 'target', type: 'select', options },
            { label: '前缀', prop: 'prefix' }
        ],
        validation: {
            validator: (rule, val, callback) => {
                if (!val || !val.length) {
                    callback(new Error("目标参数不能为空"))
                    return
                }

                callback()
            }
        }
    }
]