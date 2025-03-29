package com.song.demo.controller.back;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.song.demo.common.BizException;
import com.song.demo.dto.LoginDto;
import com.song.demo.dto.ManagerDto;
import com.song.demo.dto.UserDto;
import com.song.demo.entity.UserPo;
import com.song.demo.enums.RoleEn;
import com.song.demo.mapper.UserMapper;
import com.song.demo.service.impl.UserServiceImpl;
import com.song.demo.util.SecurityUtils;
import com.song.demo.util.UUUtil;
import com.song.demo.vo.LoginVo;
import com.song.demo.vo.UserVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/back")
public class UserBackController {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @ApiOperation("获取用户列表")
    @GetMapping("/user/list")
    public List<UserVo> getUserList(@RequestParam("keyword") String keyword) {
        List<UserPo> userPos = userMapper.selectList(Wrappers.<UserPo>lambdaQuery()
                .eq(UserPo::getRole, RoleEn.CUSTOMER.getRole())
                .eq(StringUtils.isNotEmpty(keyword), UserPo::getUsername, keyword)
                .or()
                .eq(StringUtils.isNotEmpty(keyword),UserPo::getPhone, keyword));
        List<UserVo> userVos = userPos.stream().map(userPo -> {
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(userPo, userVo);
            return userVo;
        }).collect(Collectors.toList());
        return userVos;
    }

    @ApiOperation("获取管理员列表")
    @GetMapping("/manager/list")
    public List<UserVo> getManagerList(@RequestParam("keyword") String keyword) {
        List<UserPo> userPos = userMapper.selectList(Wrappers.<UserPo>lambdaQuery()
                .ne(UserPo::getRole, RoleEn.CUSTOMER.getRole())
                .eq(StringUtils.isNotEmpty(keyword), UserPo::getUsername, keyword)
                .or()
                .eq(StringUtils.isNotEmpty(keyword),UserPo::getPhone, keyword));
        List<UserVo> userVos = userPos.stream().map(userPo -> {
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(userPo, userVo);
            return userVo;
        }).collect(Collectors.toList());
        return userVos;
    }

    @ApiOperation("新增/修改管理员")
    @PostMapping("/manager/save")
    public void saveManager(@RequestBody @Valid ManagerDto dto) {
        // 重名重手机号校验
        UserPo sameUserPo = userMapper.selectOne(Wrappers.<UserPo>lambdaQuery()
                .eq(UserPo::getUsername, dto.getUsername()));

        if (dto.getUserId() != null) {
            if (!Objects.equals(sameUserPo.getUserId(), dto.getUserId())) {
                throw new BizException("用户名已被注册！");
            }

            // 账号入库
            UserPo userPo = new UserPo();
            userPo.setId(dto.getId());
            userPo.setUsername(dto.getUsername());
            if (dto.getNewPassword() != null) {
                userPo.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            }
            userPo.setPhone(dto.getPhone());
            userPo.setRole(dto.getRole());
            userPo.setUpdater(SecurityUtils.getUsername());
            userMapper.updateById(userPo);
        } else {
            if (sameUserPo != null) {
                throw new BizException("用户名已被注册！");
            }

            // 账号入库
            UserPo userPo = new UserPo();
            userPo.setUserId(UUUtil.getId());
            userPo.setUsername(dto.getUsername());
            if (dto.getPassword() != null) {
                userPo.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            userPo.setPhone(dto.getPhone());
            userPo.setRole(dto.getRole());
            userPo.setCreator(SecurityUtils.getUsername());
            userMapper.insert(userPo);
        }
    }

    @ApiOperation("注册")
    @PostMapping("/permit/register")
    public boolean register(@RequestBody @Valid UserDto userDto) {
        return userServiceImpl.register(userDto);
    }

    @ApiOperation("登出")
    @GetMapping("/logout")
    public boolean logout() {
        return userServiceImpl.logout();
    }

    @ApiOperation("获取用户详情")
    @Secured("ROLE_abc")
    @GetMapping("/detail")
    public String detail() {
        throw new BizException("hahha");
//        return "detail";
    }
}
