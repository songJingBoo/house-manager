package com.song.demo.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.song.demo.common.BizException;
import com.song.demo.dto.resetPasswordDto;
import com.song.demo.entity.UserPo;
import com.song.demo.enums.RoleEn;
import com.song.demo.mapper.UserMapper;
import com.song.demo.dto.LoginDto;
import com.song.demo.dto.UserDto;
import com.song.demo.util.JwtUtils;
import com.song.demo.common.RedisService;
import com.song.demo.util.SecurityUtils;
import com.song.demo.util.UUUtil;
import com.song.demo.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserPo> {
    @Autowired
    private AuthenticationManager authenticationManager; // Could not autowire. No beans of 'AuthenticationManager' type found.时，securityConfig需要加配置@Bean声明

    @Autowired
    private RedisService redisService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    /**
     * 登录
     * @param loginDto
     * @return
     */
    public LoginVo login(LoginDto loginDto) {
        // 1 创建UsernamePasswordAuthenticationToken
        UsernamePasswordAuthenticationToken usernameAuthentication = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        // 2 认证
        Authentication authentication;
        try {
            authentication = this.authenticationManager.authenticate(usernameAuthentication);
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new BizException("用户名或密码错误");
        }
        // 3 保存认证信息
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 放入缓存
        CustomUserDetails userDetail = (CustomUserDetails) authentication.getPrincipal();
        redisService.set(userDetail.getUsername(), userDetail);
        // 4 生成自定义token
        return JwtUtils.createToken(loginDto.getUsername(), userDetail.getUserId(), userDetail.getRole());
    }

    /**
     * 注册
     * @param userDto
     * @return
     */
    public boolean register(UserDto userDto) {
        // 重名重手机号校验
        UserPo sameUserPo = userMapper.selectOne(Wrappers.<UserPo>lambdaQuery()
                .eq(UserPo::getUsername, userDto.getUsername()));
        if (sameUserPo != null) {
            throw new BizException("用户名已被注册！");
        }

        // 2.MD5密码 内部加盐加密处理
        String finalPassword = passwordEncoder.encode(userDto.getPassword());

        // 3.账号入库
        UserPo userPo = new UserPo();
        userPo.setUserId(UUUtil.getId());
        userPo.setUsername(userDto.getUsername());
        userPo.setPassword(finalPassword);
//        userPo.setPhone(userDto.getPhone());
        userPo.setRole(RoleEn.CUSTOMER.getRole());
        userPo.setCreator("SELF");
        int result = userMapper.insert(userPo);
        return result == 1;
    }

    /**
     * 登出
     * @return
     */
    public boolean logout() {
        redisService.delete(SecurityUtils.getUsername());
        return true;
    }

    /**
     * 修改密码
     * @return
     */
    public void resetPassword(resetPasswordDto resetPasswordDto) {
        UserPo userPo = userMapper.selectOne(Wrappers.<UserPo>lambdaQuery().eq(UserPo::getUserId, SecurityUtils.getUserId()));
        if (userPo == null) {
            throw new BizException("用户不存在");
        }

        boolean match = passwordEncoder.matches(resetPasswordDto.getPassword(), userPo.getPassword());
        if (!match) {
            throw new BizException("旧密码错误");
        }

        userPo.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        userMapper.updateById(userPo);
    }
}
