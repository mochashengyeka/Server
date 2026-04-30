package com.stu.helloserver.controller;

import com.stu.helloserver.common.Result;
import com.stu.helloserver.dto.UserDTO;
import com.stu.helloserver.dto.UserInfoDTO;
import com.stu.helloserver.service.UserService;
import com.stu.helloserver.vo.UserDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ========== POST 请求 ==========

    // 1. 注册
    @PostMapping
    public Result<String> register(@RequestBody UserDTO userDTO) {
        return userService.register(userDTO);
    }

    // 2. 登录
    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDTO userDTO) {
        return userService.login(userDTO);
    }

    // ========== GET 请求 ==========

    // 3. 分页查询
    @GetMapping("/page")
    public Result<Object> getUserPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize) {
        return userService.getUserPage(pageNum, pageSize);
    }

    // 4. 查询用户详情（多表联查 + Redis）
    @GetMapping("/{id}/detail")
    public Result<UserDetailVO> getUserDetail(@PathVariable("id") Long userId) {
        return userService.getUserDetail(userId);
    }

    // 5. 根据 id 查询用户（注意：通配路径放在具体路径之后）
    @GetMapping("/{id}")
    public Result<String> getUser(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    // ========== PUT 请求 ==========

    // 6. 更新用户扩展信息
    @PutMapping("/{id}/detail")
    public Result<String> updateUserInfo(@PathVariable("id") Long userId,
                                         @RequestBody UserInfoDTO userInfoDTO) {
        return userService.updateUserInfo(userId, userInfoDTO);
    }

    // ========== DELETE 请求 ==========

    // 7. 删除用户
    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable("id") Long userId) {
        return userService.deleteUser(userId);
    }
}