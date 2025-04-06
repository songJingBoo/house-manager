package com.song.demo.controller;

import com.song.demo.dto.CommentDto;
import com.song.demo.dto.HouseDto;
import com.song.demo.dto.HousePublishDto;
import com.song.demo.dto.LoginDto;
import com.song.demo.dto.query.HouseBackQueryDto;
import com.song.demo.dto.query.HouseQueryDto;
import com.song.demo.entity.HouseFilterPo;
import com.song.demo.service.HouseService;
import com.song.demo.vo.CommentVo;
import com.song.demo.vo.HouseVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/house")
public class HouseController {

    @Autowired
    private HouseService houseService;

    @ApiOperation("获取房源列表")
    @PostMapping("/list")
    public List<HouseVo> getHouse(@RequestBody @Valid HouseQueryDto query) {
        return houseService.getHouse(query);
    }

    @ApiOperation("发布房源")
    @PostMapping("/create")
    public Boolean createHouse(@RequestBody @Valid HousePublishDto houseDto) {
        return houseService.createHouse(houseDto);
    }

    @ApiOperation("修改房源")
    @PostMapping("/update")
    public void updateHouse(@RequestBody @Valid HouseDto houseDto) {
        houseService.updateHouse(houseDto);
    }

    @ApiOperation("查询房源想详情")
    @GetMapping("/detail")
    public HouseVo getHouseDetail(@RequestParam String id) {
        return houseService.getHouseDetail(id);
    }

    @ApiOperation("修改房源状态")
    @PutMapping("/status")
    public void changeStatus(@RequestBody @Valid LoginDto LoginDto) {

    }

    @ApiOperation("获取过滤条件配置")
    @GetMapping("/filter/get")
    public List<HouseFilterPo> getFilterConfig() {
        return houseService.getFilterConfig();
    }

    @ApiOperation("获取房屋评论")
    @GetMapping("/comment/get")
    public List<CommentVo> getHouseComment(@RequestParam("id") String houseId) {
        return houseService.getHouseComment(houseId);
    }

    @ApiOperation("发表评论")
    @PostMapping("/comment/send")
    public Boolean saveComment(@RequestBody @Valid CommentDto commentDto) {
        return houseService.saveComment(commentDto);
    }

    @ApiOperation("点赞")
    @GetMapping("/like")
    public void saveLike(@RequestParam("id") String id, @RequestParam("status") Integer status) {
        houseService.saveLike(id, status);
    }
}
