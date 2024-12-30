package com.green.greengram.feed.like;

import com.green.greengram.feed.like.medel.FeedLikeVo;
import com.green.greengram.feed.like.model.FeedLikeReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public  interface FeedLikeTestMapper {
    @Select("SELECT * FROM feed_like WHERE feed_id = #{feedId} AND user_id = #{userId}")
    FeedLikeVo selFeedLikeByFeedIdAndUserId(FeedLikeReq p);

    @Select("SELECT * FROM feed_like")
    List<FeedLikeVo> selFeedLikeAll();
}
