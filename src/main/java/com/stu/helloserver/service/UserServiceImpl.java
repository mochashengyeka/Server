package com.stu.helloserver.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stu.helloserver.Entity.User;
import com.stu.helloserver.Entity.UserInfo;
import com.stu.helloserver.common.Result;
import com.stu.helloserver.common.ResultCode;
import com.stu.helloserver.dto.UserDTO;
import com.stu.helloserver.dto.UserInfoDTO;
import com.stu.helloserver.mapper.UserInfoMapper;
import com.stu.helloserver.mapper.UserMapper;
import com.stu.helloserver.vo.UserDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import com.stu.helloserver.util.JwtUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    private static final String CACHE_KEY_PREFIX = "user:detail:";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    // ==================== 原有方法（保持不变） ====================
    @Override
    public Result<String> register(UserDTO userDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);
        if (dbUser != null) {
            return Result.error(ResultCode.USER_HAS_EXISTED);
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        userMapper.insert(user);

        return Result.success("注册成功!");
    }

    @Override
    public Result<String> login(UserDTO userDTO) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, userDTO.getUsername());
        User dbUser = userMapper.selectOne(queryWrapper);

        if (dbUser == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        if (!dbUser.getPassword().equals(userDTO.getPassword())) {
            return Result.error(ResultCode.PASSWORD_ERROR);
        }
        String jwt = jwtUtil.generateToken(userDTO.getUsername());
        return Result.success(jwt);
    }

    @Override
    public Result<String> getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }
        return Result.success("查询成功，用户ID: " + user.getId() + ", 用户名: " + user.getUsername());
    }

    @Override
    public Result<Object> getUserPage(Integer pageNum, Integer pageSize) {
        Page<User> pageParam = new Page<>(pageNum, pageSize);
        Page<User> resultPage = userMapper.selectPage(pageParam, null);
        return Result.success(resultPage);
    }

    // ==================== 任务7 新增方法 ====================

    /**
     * 查询用户详情（多表联查 + Redis 缓存）
     */
    @Override
    public Result<UserDetailVO> getUserDetail(Long userId) {
        String key = CACHE_KEY_PREFIX + userId;

        // 1. 先查缓存
        String json = redisTemplate.opsForValue().get(key);
        if (json != null && !json.isEmpty()) {
            try {
                UserDetailVO cacheVO = JSONUtil.toBean(json, UserDetailVO.class);
                return Result.success(cacheVO);
            } catch (Exception e) {
                // 缓存数据异常，删除脏缓存，继续查数据库
                redisTemplate.delete(key);
            }
        }

        // 2. 查数据库（多表联查）
        UserDetailVO detail = userInfoMapper.getUserDetail(userId);
        if (detail == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        // 3. 写入缓存，过期时间 10 分钟
        redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(detail), 10, TimeUnit.MINUTES);
        return Result.success(detail);
    }

    /**
     * 更新用户扩展信息，并删除缓存
     */
    @Override
    @Transactional
    public Result<String> updateUserInfo(Long userId, UserInfoDTO userInfoDTO) {
        if (userMapper.selectById(userId) == null) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        LambdaUpdateWrapper<UserInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserInfo::getUserId, userId)
                .set(userInfoDTO.getPhone() != null, UserInfo::getPhone, userInfoDTO.getPhone())
                .set(userInfoDTO.getAddress() != null, UserInfo::getAddress, userInfoDTO.getAddress());
        userInfoMapper.update(null, updateWrapper);

        // 删除缓存
        redisTemplate.delete(CACHE_KEY_PREFIX + userId);

        return Result.success("更新成功");
    }

    /**
     * 删除用户（同时删除 sys_user 和 user_info 记录），并清除缓存
     */
    @Override
    @Transactional
    public Result<String> deleteUser(Long userId) {
        int rows = userMapper.deleteById(userId);

        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUserId, userId);
        userInfoMapper.delete(queryWrapper);

        if (rows == 0) {
            return Result.error(ResultCode.USER_NOT_EXIST);
        }

        redisTemplate.delete(CACHE_KEY_PREFIX + userId);

        return Result.success("删除成功");
    }
}