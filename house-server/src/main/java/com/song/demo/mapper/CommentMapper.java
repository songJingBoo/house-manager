package com.song.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.song.demo.entity.CommentPo;
import com.song.demo.vo.CommentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<CommentPo> {
    /**
     * 获取房屋评论
     * @return
     */
    List<CommentVo> getHouseComment(@Param("id") String id);
}
