package com.song.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.song.demo.entity.CommentPo;
import com.song.demo.entity.MessagePo;
import com.song.demo.vo.CommentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<MessagePo> {

    /**
     * 用户消息设为已读
     * @param ids
     * @return
     */
    @Update("update messages set is_read = 1 where id in (${ids})")
    int readMessage(@Param("ids") String ids);
}
