package com.liuqi.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.ognl.Ognl;
import org.apache.ibatis.ognl.OgnlContext;
import org.apache.ibatis.ognl.OgnlException;

import java.util.Map;

/**
 * Ognl表达式辅助工具
 *
 * @author  LiuQi 2025/3/25-21:49
 * @version V1.0
 **/
@Slf4j
public class OgnlUtils {
    public static Object execute(String expression, Object obj) {
        OgnlContext context = Ognl.createDefaultContext(obj);
        try {
            return Ognl.getValue(Ognl.parseExpression(expression), context, context.getRoot());
        } catch (OgnlException e) {
            log.error("表达式执行失败", e);
            return null;
        }
    }
}
