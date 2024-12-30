package com.green.greengram.feed;

import com.green.greengram.feed.like.FeedLikeTestMapper;
import com.green.greengram.feed.model.FeedPicDto;
import com.green.greengram.feed.model.FeedPicVo;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.MyBatisSystemException;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FeedPicMapperTest {
    @Autowired
    FeedPicMapper feedPicMapper;
    FeedPicTestMapper feedPicTestMapper;

    @Test
    void insFeedPicNoFeedThrowForeignKeyException() {
        FeedPicDto givenParam = new FeedPicDto();
        givenParam.setFeedId(10L);
        givenParam.setPics(new ArrayList<>());
        givenParam.getPics().add("a.jpg");

        assertThrows(DataIntegrityViolationException.class, () ->
                feedPicMapper.insFeedPic(givenParam));
    }

    @Test
    void insFeedPicNoPicThrowNotNullException() {
        FeedPicDto givenParam = new FeedPicDto();
        givenParam.setFeedId(1L);
        givenParam.setPics(new ArrayList<>());
        assertThrows(BadSqlGrammarException.class, () -> {
            feedPicMapper.insFeedPic(givenParam);
        });
    }

    @Test
    void insFeedPicLongPicStringLengthMoreThan50ThrowException() {
        FeedPicDto givenParam = new FeedPicDto();
        givenParam.setFeedId(1L);
        givenParam.setPics(new ArrayList<>());
        givenParam.getPics().add("_543554545544564646464646356344434344534_2542544464_564584634635463463");
        assertThrows(BadSqlGrammarException.class, () -> {
            feedPicMapper.insFeedPic(givenParam);
        });
    }


    @Test
    void insFeedPic() {
        String[] pics = {"a.jpg", "b.jpg", "c.jpg"};
        String[] pics2 = {"a.jpg", "b.jpg", "c.jpg", "d.jpg"};
        FeedPicDto givenParam = new FeedPicDto();
        givenParam.setFeedId(5L);
        for (String pic : pics) {
            givenParam.getPics().add(pic);
        }
        List<FeedPicVo> feedPicListBefore = feedPicTestMapper.selectFeedPicByFeedId(givenParam.getFeedId());
        int actualAffectedRows = feedPicMapper.insFeedPic(givenParam);
        List<FeedPicVo> feedPicListAfter = feedPicTestMapper.selectFeedPicByFeedId(givenParam.getFeedId());

        List<String> picList = Arrays.asList(pics);
        for (int i = 0; i < pics.length; i++) {
            String pic = picList.get(i);
            System.out.printf("%s - contains: %b\n", pic, feedPicListAfter.contains(pic));
        }
        assertAll(
                () -> assertEquals(givenParam.getPics().size(), actualAffectedRows)
                , () -> assertEquals(0, feedPicListBefore.size()) // 데이터가 비어있는지 검증
                , () -> assertEquals(givenParam.getPics().size(), feedPicListAfter.size())
                , () -> assertTrue(feedPicListAfter.containsAll(Arrays.asList(pics)))
        );
    }

    // 빼먹은 부분 created_at

}