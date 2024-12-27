package com.green.greengram.feed;

import com.green.greengram.feed.model.FeedPicDto;
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

import static org.junit.jupiter.api.Assertions.*;
@ActiveProfiles("test")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FeedPicMapperTest {
    @Autowired
    FeedPicMapper feedPicMapper;

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
    void insFeedPic(){
        String[] pics = {"a.jpg","b.jpg","c.jpg"};
        FeedPicDto givenParam = new FeedPicDto();
        givenParam.setFeedId(1L);
        for (String pic : pics) {
            givenParam.getPics().add(pic);
        }

        int actualAffectedRows = feedPicMapper.insFeedPic(givenParam);
        assertEquals(givenParam.getPics().size(), actualAffectedRows);
    }

}