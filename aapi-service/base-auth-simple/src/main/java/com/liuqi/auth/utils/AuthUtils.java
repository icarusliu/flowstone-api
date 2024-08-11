package com.liuqi.auth.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.AlgorithmUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.liuqi.common.exception.UnauthorizedException;
import com.liuqi.common.utils.UserContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 鉴权辅助类
 *
 * @author LiuQi 19:57
 **/
public class AuthUtils {
    private static final String key = "liuqi";

    public static String generateToken(UserContext userContext) {
        Map<String, Object> payload = new HashMap<>(15);
        payload.put("id", userContext.getUserId());
        payload.put("username", userContext.getUsername());
        payload.put("displayName", userContext.getDisplayName());
        payload.put("tenantId", userContext.getTenantId());
        payload.put("super", userContext.getIsSuperAdmin());
        return JWTUtil.createToken(payload, key.getBytes());
    }

    private static JWTSigner getSigner() {
        return JWTSignerUtil.createSigner(AlgorithmUtil.getAlgorithm("RMD5"), key.getBytes());
    }

    public static UserContext parse(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        if (!JWTUtil.verify(token, key.getBytes())) {
            throw new UnauthorizedException();
        }
        JSONObject payload = jwt.getPayloads();
        String userId = payload.getStr("id");
        String username = payload.getStr("username");
        String displayName = payload.getStr("displayName");
        String tenantId = payload.getStr("tenantId");
        boolean isSuperAdmin = payload.getBool("super");
        UserContext userContext = new UserContext(tenantId, userId, username, displayName, isSuperAdmin);
        userContext.setIsSuperAdmin(isSuperAdmin);
        return userContext;
    }
}
