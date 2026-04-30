package com.stu.helloserver.service;

import com.stu.helloserver.common.Result;
import com.stu.helloserver.dto.UserDTO;
import com.stu.helloserver.dto.UserInfoDTO;
import com.stu.helloserver.vo.UserDetailVO;

public interface UserService {
    Result<String> register(UserDTO userDTO);
    Result<String> login(UserDTO userDTO);
    Result<String> getUserById(Long id);
    Result<Object> getUserPage(Integer pageNum, Integer pageSize);

    // ===== 任务7 新增方法 =====
    // 查询用户详情（多表联查 + Redis）
    Result<UserDetailVO> getUserDetail(Long userId);
    // 更新用户扩展信息
    Result<String> updateUserInfo(Long userId, UserInfoDTO userInfoDTO);
    // 删除用户（同时删除 sys_user 和 user_info）
    Result<String> deleteUser(Long userId);
}