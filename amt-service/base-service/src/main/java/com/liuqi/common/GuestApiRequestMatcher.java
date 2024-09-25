package com.liuqi.common;

import com.liuqi.base.service.ClientService;
import com.liuqi.dua.bean.dto.ApiDTO;
import com.liuqi.dua.service.ApiDraftService;
import com.liuqi.dua.service.ApiService;
import com.liuqi.dua.service.ClientApiService;
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

    @Autowired
    private ApiDraftService apiDraftService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientApiService clientApiService;

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

        // 游客模式处理
        ApiDTO api = ApiHolder.get();
        if (null == api) {
            return false;
        }
        return api.getGuestMode();
    }
}
