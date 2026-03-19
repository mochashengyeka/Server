package com.stu.helloserver.controller;

import com.stu.helloserver.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口控制器：统一返回Result格式响应
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 统一响应结果
     */
    @GetMapping("/{id}")
    public Result<String> getUser(@PathVariable("id") Long id) {
        // 模拟查询结果，包装到统一响应体中
        String userInfo = "ID为" + id + "的用户信息：姓名=张三，年龄=20";
        return Result.success(userInfo);
    }
}