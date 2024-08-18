// 基础渲染器，content为渲染内容，比如：
// h('div', 'test')
export default {
  name: "base-render",
  props: [
    "content",
  ],
  render() {
    return this.content;
  },
}
