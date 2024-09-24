package com.liuqi.common;

import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.bean.query.ApiQuery;
import com.liuqi.dua.service.ApiService;
import jakarta.el.MethodNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

/**
 * 游客接口
 *
 * @author  LiuQi 2024/9/24-17:51
 * @version V1.0
 **/
@Service
public class GuestApiRequestMatcher implements RequestMatcher {
    @Autowired
    private ApiService apiService;

    /**
     * Decides whether the rule implemented by the strategy matches the supplied request.
     *
     * @param request the request to check for a match
     * @return true if the request matches, false otherwise
     */
    @Override
    public boolean matches(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (!uri.startsWith("/dua/") && !uri.startsWith("/dua-test/")) {
            return false;
        }

        int idx = 5;
        if (uri.startsWith("/dua-test/")) {
            idx = 10;
        }

        String key = uri.substring(idx);
        ApiQuery q = new ApiQuery();
        q.setKey(key);
        ApiDTO api = apiService.findOne(q).orElseThrow(MethodNotFoundException::new);
        return api.getGuestMode();
    }
}
