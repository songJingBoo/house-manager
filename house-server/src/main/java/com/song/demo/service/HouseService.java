package com.song.demo.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.song.demo.common.BizException;
import com.song.demo.dto.*;
import com.song.demo.dto.query.HouseBackQueryDto;
import com.song.demo.dto.query.HouseQueryDto;
import com.song.demo.entity.CommentPo;
import com.song.demo.entity.HouseFilterPo;
import com.song.demo.entity.HousePo;
import com.song.demo.entity.LikePo;
import com.song.demo.mapper.*;
import com.song.demo.util.SecurityUtils;
import com.song.demo.util.UUUtil;
import com.song.demo.vo.CommentVo;
import com.song.demo.vo.HouseImageVo;
import com.song.demo.vo.HouseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseService extends ServiceImpl<HouseMapper, HousePo> {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private HouseFilterMapper houseFilterMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private HouseImageMapper houseImageMapper;

    @Autowired
    private LikeMapper likeMapper;

    /**
     * 获取房源列表
     * @param query
     * @return
     */
    public List<HouseVo> getHouse(HouseQueryDto query) {
        query.setUserId(SecurityUtils.getUserId());
        return houseMapper.queryHouse(query);
    }

    /**
     * 创建房源
     * @param houseDto
     * @return
     */
    public Boolean createHouse(HousePublishDto houseDto) {
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
        HouseVo houseVo = houseMapper.getHouseById(houseId, SecurityUtils.getUserId());
        List<HouseImageVo> houseImageVos = houseImageMapper.queryImageList(houseId);
        houseVo.setImages(houseImageVos);
        return houseVo;
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
    public List<CommentVo> getHouseComment(String houseId) {
        return commentMapper.getHouseComment(houseId);
//        return commentMapper.selectList(Wrappers.<CommentPo>lambdaQuery().eq(CommentPo::getHouseId, houseId));
    }


    /**
     * 发表评论
     * @param commentDto
     * @return
     */
    public Boolean saveComment(CommentDto commentDto) {
        CommentPo commentPo = new CommentPo();
        BeanUtils.copyProperties(commentDto, commentPo);
        commentPo.setUserId(SecurityUtils.getUserId());
        int count = commentMapper.insert(commentPo);
        return count == 1;
    }

    /**
     * 关注房屋
     * @param houseId
     * @param status
     * @return
     */
    public void saveLike(String houseId, Integer status) {
        if (status == 1) {
            LikePo likePo = new LikePo();
            likePo.setHouseId(houseId);
            likePo.setUserId(SecurityUtils.getUserId());
            likeMapper.insert(likePo);
        } else {
            likeMapper.delete(Wrappers.<LikePo>lambdaQuery()
                    .eq(LikePo::getHouseId, houseId)
                    .eq(LikePo::getUserId, SecurityUtils.getUserId()));
        }
    }
}
