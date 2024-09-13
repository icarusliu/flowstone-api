package com.liuqi.dua.executor.tasks.http;

import com.liuqi.dua.executor.bean.NodeParam;
import lombok.Data;

/**
 * 认证参数
 *
 * @author  LiuQi 2024/9/13-14:41
 * @version V1.0
 **/
@Data
public class AuthParam extends NodeParam {
    private String id;
    private String target;
    private String prefix;
}
