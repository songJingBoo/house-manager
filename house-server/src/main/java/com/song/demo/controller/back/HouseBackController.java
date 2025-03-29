package com.song.demo.controller.back;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.song.demo.common.BizException;
import com.song.demo.dto.AuditDto;
import com.song.demo.dto.HouseDto;
import com.song.demo.dto.HouseFilterDto;
import com.song.demo.dto.LoginDto;
import com.song.demo.dto.query.HouseQueryDto;
import com.song.demo.entity.HouseFilterPo;
import com.song.demo.entity.HouseImagePo;
import com.song.demo.entity.HousePo;
import com.song.demo.mapper.HouseFilterMapper;
import com.song.demo.mapper.HouseImageMapper;
import com.song.demo.mapper.HouseMapper;
import com.song.demo.service.HouseService;
import com.song.demo.vo.HouseVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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

    /******************************** 房屋管理 start *****************************/
    @ApiOperation("获取房源列表")
    @PostMapping("/list")
    public List<HouseVo> getHouse(@RequestBody @Valid HouseQueryDto query) {
        return houseMapper.getHouse(query);
    }

    @ApiOperation("编辑房源")
    @PostMapping("/edit")
    public void updateHouse(@RequestBody @Valid HouseDto houseDto) {
        HousePo housePo = BeanUtil.copyProperties(houseDto, HousePo.class);
        int count = houseMapper.updateById(housePo);
        if (count == 0) {
            throw new BizException("房屋编辑失败");
        }

        houseImageMapper.delete(Wrappers.<HouseImagePo>lambdaQuery().eq(HouseImagePo::getHouseId, houseDto.getHouseId()));
        List<String> urls = houseDto.getFiles();
        if (urls != null && urls.size() > 0) {
            urls.forEach(url -> {
                HouseImagePo imagePo = new HouseImagePo();
                imagePo.setHouseId(houseDto.getHouseId());
                imagePo.setImageUrl(url);
                houseImageMapper.insert(imagePo);
            });
        }
    }

    @ApiOperation("获取过滤条件配置")
    @GetMapping("/filter/get")
    public List<HouseFilterPo> getFilterConfig() {
        return houseFilterMapper.selectList(null);
    }

    @ApiOperation("保存过滤条件配置")
    @PutMapping("/filter/config")
    public Boolean saveFilterConfig(@RequestBody @Valid HouseFilterDto houseFilterDto) {
        int count = houseFilterMapper.saveFilterConfig(houseFilterDto);
        return count == 1;
    }
    /******************************** 房屋管理 end *****************************/
    /******************************** 房屋审核 start *****************************/
    @ApiOperation("获取房源列表")
    @PostMapping("/audit/list")
    public List<HouseVo> getAuditHouse(@RequestBody @Valid HouseQueryDto query) {
        return houseMapper.getHouse(query);
    }

    @ApiOperation("编辑房源")
    @PostMapping("/audit/edit")
    public void editAuditHouse(@RequestBody @Valid HouseDto houseDto) {
        HousePo housePo = BeanUtil.copyProperties(houseDto, HousePo.class);
        int count = houseMapper.updateById(housePo);
        if (count == 0) {
            throw new BizException("房屋编辑失败");
        }

        houseImageMapper.delete(Wrappers.<HouseImagePo>lambdaQuery().eq(HouseImagePo::getHouseId, houseDto.getHouseId()));
        List<String> urls = houseDto.getFiles();
        if (urls != null && urls.size() > 0) {
            urls.forEach(url -> {
                HouseImagePo imagePo = new HouseImagePo();
                imagePo.setHouseId(houseDto.getHouseId());
                imagePo.setImageUrl(url);
                houseImageMapper.insert(imagePo);
            });
        }
    }

    @ApiOperation("修改房源状态")
    @PostMapping("/audit/status")
    public void changeStatus(@RequestBody @Valid AuditDto auditDto) {
        HousePo housePo = houseMapper.selectById(auditDto.getId());
        if (housePo == null) {
            throw new BizException("房屋不存在");
        }

        housePo.setStatus(auditDto.getStatus());
        int count = houseMapper.updateById(housePo);
        if (count == 0) {
            throw new BizException("房屋审核失败");
        }
    }
    /******************************** 房屋审核 end *****************************/
}
