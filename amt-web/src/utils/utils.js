import * as _ from "lodash";

// 遍历对象及子元素
export function loop(item, func, parent) {
    if (!item || !func) {
        return;
    }

    if (_.isArray(item)) {
        for (var i in item) {
            let subItem = item[i];
            let b = loop(subItem, func, parent);
            if (b) {
                // 中断执行
                return true;
            }
        }
        return;
    }

    let b = func(item, parent);
    if (b) {
        // 中断执行
        return true;
    }
    return loop(item.children, func, item);
}

// 从树中删除符合条件的元素
export function removeFromTree(tree, predicate) {
    let removed = _.remove(tree, predicate);
    if (removed && removed.length) {
        return true;
    }

    // 当前层级没找到，则继续处理下一层级的数据
    for (var i in tree) {
        let sub = tree[i];
        if (!sub.children) {
            continue;
        }

        removed = removeFromTree(sub.children, predicate);
        if (removed) {
            return true;
        }
    }

    return false;
}

export function formatXml(xmlString) {
    try {
        let formatted = "";
        let indent = "";
        const indentChar = "  ";

        // 处理 XML 声明
        const xmlDeclarationMatch = xmlString.match(/<\?xml[^?]*\?>/);
        if (xmlDeclarationMatch) {
            formatted += xmlDeclarationMatch[0] + "\n";
            xmlString = xmlString.replace(xmlDeclarationMatch[0], "");
        }

        // 处理 CDATA 部分，临时替换以避免处理标签
        const cdataSections = [];
        xmlString = xmlString.replace(/<!\[CDATA\[(.*?)\]\]>/gs, (match) => {
            const id = `__CDATA_${cdataSections.length}__`;
            cdataSections.push(match);
            return id;
        });

        // 分割标签和文本节点
        const nodes = xmlString.match(/(<[^>]*>)|([^<]+)/g) || [];
        let prevWasOpeningTag = false;
        let currentIndent = "";

        for (let i = 0; i < nodes.length; i++) {
            const node = nodes[i].trim();
            if (!node) continue;

            // 恢复 CDATA
            if (node.startsWith("__CDATA_") && node.endsWith("__")) {
                const cdataId = parseInt(node.match(/__CDATA_(\d+)__/)[1]);
                formatted += currentIndent + cdataSections[cdataId] + "\n";
                continue;
            }

            // 判断节点类型
            const isOpeningTag = /^<[^/!]/.test(node);
            const isClosingTag = /^<\//.test(node);
            const isSelfClosing = /\/>$/.test(node);
            const isComment = /^<!--/.test(node);

            if (isOpeningTag && !isSelfClosing) {
                formatted += currentIndent + node + "\n";
                currentIndent += indentChar;
                prevWasOpeningTag = true;
            } else if (isClosingTag) {
                currentIndent = currentIndent.slice(0, -indentChar.length);

                // 如果前一个节点是文本节点，则合并到同一行
                if (i > 0 && /^[^<]/.test(nodes[i - 1])) {
                    // 去除文本节点末尾的空白
                    formatted = formatted.replace(/\s+$/, "") + node + "\n";
                } else {
                    formatted += currentIndent + node + "\n";
                }

                prevWasOpeningTag = false;
            } else if (isSelfClosing || isComment) {
                formatted += currentIndent + node + "\n";
                prevWasOpeningTag = false;
            } else {
                // 文本节点处理
                if (prevWasOpeningTag) {
                    // 追加文本内容，保留内部空格但去除首尾空白
                    formatted = formatted.trim() + " " + node.trim() + " ";
                } else {
                    // 否则作为新行处理
                    formatted += currentIndent + node + "\n";
                }
                prevWasOpeningTag = false;
            }
        }

        return formatted;
    } catch (err) {
        return xmlString;
    }
}
