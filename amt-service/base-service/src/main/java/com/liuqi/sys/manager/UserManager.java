package com.liuqi.sys.manager;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.liuqi.common.bean.UserContextHolder;
import com.liuqi.common.exception.AppException;
import com.liuqi.common.exception.UnauthorizedException;
import com.liuqi.sys.bean.dto.UserDTO;
import com.liuqi.sys.bean.dto.UserRoleDTO;
import com.liuqi.sys.bean.query.NoRoleUserQuery;
import com.liuqi.sys.bean.req.PwdUpdateReq;
import com.liuqi.sys.common.ErrorCodes;
import com.liuqi.sys.domain.mapper.UserMapper;
import com.liuqi.sys.service.UserRoleService;
import com.liuqi.sys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户业务处理
 *
 * @author LiuQi 2025/3/19-8:28
 * @version V1.0
 **/
@Service
public class UserManager {
    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 根据角色查找用户
     * @param roleId 角色id
     * @return 角色关联用户列表
     */
    public List<UserDTO> getUserByRole(String roleId) {
        List<UserRoleDTO> userRoles = userRoleService.findByRoles(List.of(roleId));
        if (CollectionUtils.isEmpty(userRoles)) {
            return new ArrayList<>(0);
        }

        List<String> userIds = userRoles.stream()
                .map(UserRoleDTO::getUserId)
                .toList();
        return userService.findByIds(userIds);
    }

    /**
     * 查询无对应角色用户列表
     * @param query 查询条件
     * @return 查询结果
     */
    public IPage<UserDTO> queryUserNoRole(NoRoleUserQuery query) {
        PageHelper.startPage(query.getPageNo().intValue(), query.getPageSize().intValue());
        List<UserDTO> users = userMapper.queryUserNoRole(query);
        PageInfo<UserDTO> pageInfo = new PageInfo<>(users);
        IPage<UserDTO> ipage = new Page<>();
        ipage.setTotal(pageInfo.getTotal());
        ipage.setRecords(pageInfo.getList());
        return ipage;
    }

    /**
     * 用户更新密码
     */
    public void updatePwd(PwdUpdateReq req) {
        // 校验原密码是否准确
        String oldPassword = req.getOldPassword();
        String userId = UserContextHolder.getUserId().orElseThrow(UnauthorizedException::new);
        UserDTO user = userService.findById(userId).orElseThrow(UnauthorizedException::new);
        String dbPassword = user.getPassword();
        if (!passwordEncoder.matches(oldPassword, dbPassword)) {
            throw AppException.of(ErrorCodes.BASE_PASSWORD_INVALID);
        }

        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userService.update(user);
    }
}
