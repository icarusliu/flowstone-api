function getFunc(str, context) {
    if (typeof str == 'function') {
        return str;
    }

    if (_.isEmpty(str)) {
        return null;
    }

    str = _.trim(str);

    // 移除前面的注释，只考虑前面是// 或者 是/** */的情况
    str = str.replace(/\/\/.*\n/, '')
        .replace(/\/\*.*\*\//, '')

    str = _.trim(str);

    return new Function(`"use strict"; return (${str})`)(context);
}


function callFunc(funcStr, context) {
    const args = [...arguments].splice(2);
    try {
        const func = getFunc(funcStr, context);
        if (!func) {
            return Promise.resolve(null);
        }

        return Promise.resolve(func(...args));
    } catch (ex) {
        console.error("解析函数失败", ex, funcStr);
    }
}

export default {
    getFunc,
    callFunc
}