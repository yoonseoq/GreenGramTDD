package com.green.greengram.feed;

import com.green.greengram.TestUtils;
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
        FeedPicDto givenParam = new FeedPicDto();
        givenParam.setFeedId(5L);
        givenParam.setPics(Arrays.asList(pics));
        for (String pic : pics) {
            givenParam.getPics().add(pic);
        }
        List<FeedPicVo> feedPicListBefore = feedPicTestMapper.selectFeedPicByFeedId(givenParam.getFeedId());
        int actualAffectedRows = feedPicMapper.insFeedPic(givenParam);
        List<FeedPicVo> feedPicListAfter = feedPicTestMapper.selectFeedPicByFeedId(givenParam.getFeedId());


        //feedPicListAfter 에서 사진만 뽑아내서 이전처럼 List<String>변형한 다음 체크
        //일단 사이즈 0
        List<String> feedOnlyPicList = new ArrayList<>(feedPicListAfter.size());
        feedPicListAfter.forEach(feedPicVo -> {
            feedOnlyPicList.add(feedPicVo.getPic());
        });

        //스트링 이용
        List<String> picList = Arrays.asList(pics);
        for (int i = 0; i < pics.length; i++) {
            String pic = picList.get(i);
            System.out.printf("%s - contains: %b\n", pic, feedOnlyPicList.contains(pic));
        }

        //Predicate 리턴타입 boolean , 파라미터 0 (FeedPicVo)

        String[] pics2 = {"a.jpg", "b.jpg", "c.jpg", "d.jpg"};
        List<String> piclist = Arrays.asList(pics2);
        feedPicListAfter.stream().allMatch(feedPicVo -> feedOnlyPicList.contains(feedPicVo.getPic()));

        assertAll(
                () -> feedPicListAfter.forEach(feedPicVo -> TestUtils.assertCurrentTimestamp(feedPicVo.getCreatedAt()))
                , () -> {
                    for (FeedPicVo feedPicVo : feedPicListAfter) {
                        TestUtils.assertCurrentTimestamp(feedPicVo.getCreatedAt());
                    }
                }
                , () -> assertEquals(givenParam.getPics().size(), actualAffectedRows)
                , () -> assertEquals(0, feedPicListBefore.size()) // 데이터가 비어있는지 검증
                , () -> assertEquals(givenParam.getPics().size(), feedPicListAfter.size())
                , () -> assertTrue(feedOnlyPicList.containsAll(Arrays.asList(pics)))
                , () -> assertTrue(Arrays.asList(pics).containsAll(feedOnlyPicList))
                , () -> assertTrue(feedPicListAfter.stream().allMatch(feedPicVo -> picList.contains(feedPicVo.getPic())))

                , () -> assertTrue(feedPicListAfter.stream().map(FeedPicVo::getPic)
                        .filter(pic -> picList.contains(pic))
                        .limit(picList.size())
                        .count() == picList.size())
                , () -> assertTrue(feedPicListAfter.stream().map(FeedPicVo::getPic).toList().containsAll(Arrays.asList(pics)))
                , () -> assertTrue(feedPicListAfter.stream() //스트림 생성
                        .map(feedPicVo -> feedPicVo.getPic()) // 똑같은 크기의 새로운 반환 Stream<String> ["a.jpg","b.jpg","c.jpg"]
                        .toList() // 스트링 > list
                        .containsAll(Arrays.asList(pics)))
        );
    }

    // 빼먹은 부분 created_at

}
