package com.song.demo.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.song.demo.common.BizException;
import com.song.demo.dto.AuditDto;
import com.song.demo.dto.CommentDto;
import com.song.demo.dto.HouseDto;
import com.song.demo.dto.HouseFilterDto;
import com.song.demo.dto.query.HouseQueryDto;
import com.song.demo.entity.CommentPo;
import com.song.demo.entity.HouseFilterPo;
import com.song.demo.entity.HousePo;
import com.song.demo.mapper.CommentMapper;
import com.song.demo.mapper.HouseFilterMapper;
import com.song.demo.mapper.HouseMapper;
import com.song.demo.util.SecurityUtils;
import com.song.demo.util.UUUtil;
import com.song.demo.vo.HouseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class HouseService extends ServiceImpl<HouseMapper, HousePo> {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private HouseFilterMapper houseFilterMapper;

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 获取房源列表
     * @param query
     * @return
     */
    public List<HouseVo> getHouse(HouseQueryDto query) {
        query.setStatus("AVAILABLE");
        return houseMapper.getHouse(query);
    }

    /**
     * 创建房源
     * @param houseDto
     * @return
     */
    public Boolean createHouse(HouseDto houseDto) {
        HousePo housePo = BeanUtil.copyProperties(houseDto, HousePo.class);
        housePo.setUserId(SecurityUtils.getUserId());
        housePo.setHouseId(UUUtil.getId());
        housePo.setCreator(SecurityUtils.getUserId());
        int count = houseMapper.insert(housePo);
        return count == 1;
    }

    /**
     * 修改房源
     * @param houseDto
     * @return
     */
    public void updateHouse(HouseDto houseDto) {
        HousePo housePo = BeanUtil.copyProperties(houseDto, HousePo.class);
        int count = houseMapper.updateById(housePo);
        if (count == 0) {
            throw new BizException("房屋编辑失败");
        }
    }

    /**
     * 审核
     * @param auditDto
     */
    public void changeStatus(AuditDto auditDto) {
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

    /**
     * 获取房屋详情
     * @param houseId
     * @return
     */
    public HouseVo getHouseDetail(String houseId) {
        return houseMapper.getHouseById(houseId);
    }

    /**
     * 获取过滤条件配置
     * @return
     */
    public List<HouseFilterPo> getFilterConfig() {
        return houseFilterMapper.selectList(null);
    }

    /**
     * 保存过滤条件配置
     * @param houseFilterDto
     * @return
     */
    public Boolean saveFilterConfig(HouseFilterDto houseFilterDto) {
        int count = houseFilterMapper.saveFilterConfig(houseFilterDto);
        return count == 1;
    }

    /**
     * 获取房屋评论
     * @param houseId
     * @return
     */
    public List<CommentPo> getHouseComment(String houseId) {
        return commentMapper.selectList(Wrappers.<CommentPo>lambdaQuery().eq(CommentPo::getHouseId, houseId));
    }

    /**
     * 发表评论
     * @param commentDto
     * @return
     */
    public Boolean saveComment(CommentDto commentDto) {
        if (commentDto.getCommentId() == null) { // 评论

        } else { // 回复

        }
        return true;
    }

}
