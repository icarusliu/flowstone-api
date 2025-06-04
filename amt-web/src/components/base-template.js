/**
 * Html渲染器
 * 使用示例：
 * 1. 传入字符串模板直接使用：
 * ```js
 * <base-template template="<el-tag>测试信息</el-tag>"/>
 * ```
 *
 * 2. 传入字符串模板及数据:
 * ```js
 * <base-template template="<el-tag v-for='item in items'>{{item}}</el-tag>" :data="['1', '2','3']"/>
 * ```
 *
 * 3. 传入模板函数：
 * ```js
 * <base-template :template="() => '<el-tag>test</el-tag>'"/>
 * ```
 *
 * 4. 传入异步模板函数：
 * ```js
 * <base-template :template="() => Promise.resolve('<el-tag>test</el-tag>')"/>
 * ```
 *
 * 5. 传入异步对象
 * ```js
 * <base-template :template="Promise.resolve('<el-tag>test</el-tag>')"/>
 * ```
 *
 * 6. 传入生成模板及数据的函数（或异步函数）
 * ```js
 * <base-template :template="() => {return {template: '<el-tag>{{title}}</el-tag>', props: {title: 'test'}}}"/>
 * ```
 */
import { h } from "vue";
export default {
    name: "base-template",
    props: [
        "template", // 模板信息，模板可以是直接的字符串，也可以是异步函数，
        "data", // 传入的数据
    ],
    data() {
        return {
            finalHtml: "--",
            templateProps: null,
            methods: {},
            mounted: null,
            beforeDestroy: null,
            updated: null,

            // 结果缓存，避免数据变化时，render重复执行
            result: { template: "<div>--</div>" },
        };
    },
    render() {
        // 注意，如果数据是动态变化的，render函数会重新执行
        // 实际渲染实现，根据传入的
        return h(this.result, {
            class: "header-left-slot",
        });
    },
    watch: {
        template: {
            // 监听html内容，计算最终展示的模板
            immediate: true,
            handler() {
                if (!this.template) {
                    this.finalHtml = "--";
                    return;
                }

                if (typeof this.template === "function" || rc.getFunc(this.template)) {
                    // 本身就是函数
                    const result = rc.callFunc(this.template, this, this.data);
                    if (result.then) {
                        // 函数返回的是异步对象
                        result.then(this.parseResult);
                        return;
                    }

                    // 函数返回的是直接对象
                    this.parseResult(result);
                    return;
                }

                // 模板本身是异步函数
                if (this.template.then) {
                    this.template.then(this.parseResult);
                    return;
                }

                // 其它情况，当串处理
                this.parseResult(this.template);
                return;
            },
        },
        templateProps: {
            deep: true,
            handler() {
                this.$forceUpdate();
            },
        },
    },
    methods: {
        // 解析结果，如果结果是字符串，则直接当成模板；否则如果结果中有template/props，则传入模板组件进行动态数据渲染
        parseResult(result) {
            if (!result) {
                return;
            }

            if (result.template) {
                this.finalHtml = result.template;
                this.templateProps = result.props || result.data;
                this.methods = result.methods || {};
                this.mounted = result.mounted;
                this.beforeDestroy = result.beforeDestroy;
                this.updated = result.updated;
            } else {
                this.finalHtml = result;
            }

            var self = this;
            this.result = {
                template: this.finalHtml,
                data() {
                    return self.templateProps || self.data || {};
                },
                methods: this.methods,
                mounted: this.mounted,
                updated: this.updated,
                beforeDestroy: this.beforeDestroy,
            };
        },
    },
};
