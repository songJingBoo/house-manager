package com.song.demo.controller.back;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.song.demo.common.BizException;
import com.song.demo.dto.*;
import com.song.demo.dto.query.HouseBackQueryDto;
import com.song.demo.entity.HouseFilterPo;
import com.song.demo.entity.HouseImagePo;
import com.song.demo.entity.HousePo;
import com.song.demo.entity.MessagePo;
import com.song.demo.enums.AppointEn;
import com.song.demo.enums.HouseStatusEn;
import com.song.demo.mapper.HouseFilterMapper;
import com.song.demo.mapper.HouseImageMapper;
import com.song.demo.mapper.HouseMapper;
import com.song.demo.mapper.MessageMapper;
import com.song.demo.service.HouseService;
import com.song.demo.util.SecurityUtils;
import com.song.demo.vo.HouseImageVo;
import com.song.demo.vo.HouseVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/back")
public class HouseBackController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private HouseFilterMapper houseFilterMapper;

    @Autowired
    private HouseImageMapper houseImageMapper;

    @Autowired
    private MessageMapper messageMapper;

    /******************************** 房屋管理 start *****************************/
    @ApiOperation("获取房屋列表")
    @Secured({ "ROLE_Agent", "ROLE_Admin" })
    @PostMapping("/list")
    public List<HouseVo> getHouse(@RequestBody @Valid HouseBackQueryDto query) {
        List<HouseVo> houseVos = houseMapper.getHouse(query);
        for (HouseVo vo : houseVos) {
            List<HouseImageVo> houseImageVos = houseImageMapper.queryImageList(vo.getHouseId());
            vo.setImages(houseImageVos);
        }
        return houseVos;
    }

    @ApiOperation("编辑房屋")
    @Secured({ "ROLE_Agent", "ROLE_Admin" })
    @PostMapping("/edit")
    public void updateHouse(@RequestBody @Valid HouseDto houseDto) {
        HousePo housePo = BeanUtil.copyProperties(houseDto, HousePo.class);
        int count = houseMapper.updateById(housePo);
        if (count == 0) {
            throw new BizException("房屋不存在");
        }

        houseImageMapper.delete(Wrappers.<HouseImagePo>lambdaQuery().eq(HouseImagePo::getHouseId, houseDto.getHouseId()));
        List<HouseImageDto> houseImageDtos = houseDto.getFiles();
        if (houseImageDtos != null && houseImageDtos.size() > 0) {
            houseImageDtos.forEach(houseImageDto -> {
                HouseImagePo imagePo = new HouseImagePo();
                imagePo.setHouseId(houseDto.getHouseId());
                imagePo.setImageUrl(houseImageDto.getPath());
                imagePo.setIsCover(houseImageDto.getIsCover());
                houseImageMapper.insert(imagePo);
            });
        }
    }

    @ApiOperation("更新房屋交易状态")
    @Secured({ "ROLE_Agent", "ROLE_Admin" })
    @PostMapping("/dealHouse")
    public void dealHouse(@RequestBody @Valid HouseDealDto houseDto) {
        HousePo housePo = houseMapper.selectOne(Wrappers.<HousePo>lambdaQuery().eq(HousePo::getHouseId, houseDto.getHouseId()));
        if (housePo == null) {
            throw new BizException("房屋不存在");
        }

        houseMapper.dealHouse(houseDto.getHouseId(), houseDto.getFinalPrice());

        // 发送通知 - 交易完成
        MessagePo messagePo = new MessagePo();
        messagePo.setHouseId(housePo.getHouseId());
        messagePo.setUserId(housePo.getUserId());
        messagePo.setTitle("房屋交易完成");
        messagePo.setContent("恭喜！您发布的房屋已完成交易，感谢您的信任~");
        messagePo.setCreator(SecurityUtils.getUserId());
        messageMapper.insert(messagePo);
    }

    @ApiOperation("房屋发布/下架")
    @Secured({ "ROLE_Agent", "ROLE_Admin" })
    @PutMapping("/changeStatus")
    public void REMOVEDHouse(@RequestParam("houseId") String houseId,
                             @RequestParam("status") String status) {
        HousePo housePo = houseMapper.selectOne(Wrappers.<HousePo>lambdaQuery().eq(HousePo::getHouseId, houseId));
        if (housePo == null) {
            throw new BizException("房屋不存在");
        }

        if (status.equals(HouseStatusEn.PUBLISHED.toString())) { // 发布人就是代理人
            housePo.setAgent(SecurityUtils.getUserId());
        }
        housePo.setStatus(HouseStatusEn.valueOf(status));
        houseMapper.updateById(housePo);
//        houseMapper.changeStatus(houseId, status);


        // 发送通知
        MessagePo messagePo = new MessagePo();
        messagePo.setHouseId(housePo.getHouseId());
        messagePo.setUserId(housePo.getUserId());
        if (status.equals(HouseStatusEn.PUBLISHED.toString())) {
            messagePo.setTitle("房屋上线通知");
            messagePo.setContent("您发布的房屋已完成最终审核，正式发布上线啦~");
        } else {
            messagePo.setTitle("房屋下架通知");
            messagePo.setContent("您发布的房屋已被下架，如有疑问请联系房屋对应代理人~");
        }
        messagePo.setCreator(SecurityUtils.getUserId());
        messageMapper.insert(messagePo);
    }

    @ApiOperation("获取过滤条件配置")
    @GetMapping("/filter/get")
    public List<HouseFilterPo> getFilterConfig() {
        return houseFilterMapper.selectList(null);
    }

    @ApiOperation("保存过滤条件配置")
    @Secured({ "ROLE_Admin" })
    @PostMapping("/filter/config")
    public void saveFilterConfig(@RequestBody @Valid List<HouseFilterConfigDto> houseFilterConfigDtos) {
        houseFilterMapper.delete(Wrappers.<HouseFilterPo>lambdaQuery());
        houseFilterConfigDtos.forEach(houseFilterDto -> {
            HouseFilterPo houseFilterPo = new HouseFilterPo();
            BeanUtil.copyProperties(houseFilterDto, houseFilterPo);
            houseFilterMapper.insert(houseFilterPo);
        });
    }
    /******************************** 房屋管理 end *****************************/
    /******************************** 房屋审核 start *****************************/
    @ApiOperation("获取审核房屋列表")
    @Secured({ "ROLE_Reviewer", "ROLE_Admin" })
    @PostMapping("/audit/list")
    public List<HouseVo> getAuditHouse(@RequestBody @Valid HouseBackQueryDto query) {
        List<HouseVo> houseVos = houseMapper.getAuditHouse(query);
        for (HouseVo vo : houseVos) {
            List<HouseImageVo> houseImageVos = houseImageMapper.queryImageList(vo.getHouseId());
            vo.setImages(houseImageVos);
        }
        return houseVos;
    }

    @ApiOperation("编辑审核房屋")
    @Secured({ "ROLE_Reviewer", "ROLE_Admin" })
    @PostMapping("/audit/edit")
    public void editAuditHouse(@RequestBody @Valid HouseDto houseDto) {
        HousePo housePo = BeanUtil.copyProperties(houseDto, HousePo.class);
        int count = houseMapper.updateById(housePo);
        if (count == 0) {
            throw new BizException("房屋不存在");
        }

        houseImageMapper.delete(Wrappers.<HouseImagePo>lambdaQuery().eq(HouseImagePo::getHouseId, houseDto.getHouseId()));
        List<HouseImageDto> houseImageDtos = houseDto.getFiles();
        if (houseImageDtos != null && houseImageDtos.size() > 0) {
            houseImageDtos.forEach(houseImageDto -> {
                HouseImagePo imagePo = new HouseImagePo();
                imagePo.setHouseId(houseDto.getHouseId());
                imagePo.setImageUrl(houseImageDto.getPath());
                imagePo.setIsCover(houseImageDto.getIsCover());
                houseImageMapper.insert(imagePo);
            });
        }
    }

    @ApiOperation("修改房屋审核状态")
    @Secured({ "ROLE_Reviewer", "ROLE_Admin" })
    @PostMapping("/audit/status")
    public void changeStatus(@RequestBody @Valid AuditDto auditDto) {
        HousePo housePo = houseMapper.selectById(auditDto.getId());
        if (housePo == null) {
            throw new BizException("房屋不存在");
        }

        housePo.setAuditStatus(auditDto.getStatus());
        int count = houseMapper.updateById(housePo);
        if (count == 0) {
            throw new BizException("房屋审核失败");
        }
    }
    /******************************** 房屋审核 end *****************************/
}
