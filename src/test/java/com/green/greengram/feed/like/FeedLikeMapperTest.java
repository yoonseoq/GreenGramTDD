package com.green.greengram.feed.like;

import com.green.greengram.TestUtils;
import com.green.greengram.feed.like.medel.FeedLikeVo;
import com.green.greengram.feed.like.model.FeedLikeReq;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") //yaml 적용되는 파일선택 (application-test.yaml)
@MybatisTest //Mybatis Mapper Test 이기 때문에 작성 >> Mapper 들이 전부 객체화 >> DI를 할수 있다
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//테스트를 기본적으로 매모리 데이터베이스 H2를 사용해서 하는데 메모리 데이터베이스로 교체하지 않는다
//즉 우리가 원래 쓰는 데이터베이스로 테스트를 진행하겠다

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
// 메소드마다 테스트 인스턴스를 만든다 , 클래스마다 객체 생성을 한다
class FeedLikeMapperTest {

    @Autowired // TODO : 스프링 컨테이너가 DI 해주는게 맞는지 확인
    FeedLikeMapper feedLikeMapper; //DI가 된다. 필드 주입방식의 DI가 된다.

    @Autowired
    FeedLikeTestMapper feedLikeTestMapper;

    static final long FEED_ID_1 = 1L;
    static final long FEED_ID_5 = 5L;
    static final long USER_ID_1 = 2L;
    static final long USER_ID_2 = 2L;

    static final FeedLikeReq existedData = new FeedLikeReq();
    static final FeedLikeReq notExistedData = new FeedLikeReq();
    /*
    @BeforeAll - 모든 테스트 실행전에 최초 한번 실행
    ---
    @BeforeEach - 테스트 메소드마다 테스트 메소드 실행전에 실행되는 before 메소드
    @Test
    @AfterEach - 각 테스트 실행후에 실행
    ---
    @AfterAll - 모든 테스트 실행 후에 최초 한번 실행
    */

    /*
    @BeforeAll - 테스트 메소드 실행되기 최초 딱 한번만 실행이 되는 메소드
    테스트 메소드 마다 테스트 객체가 만들어지면 beforeAll 메소드는 static 메소드여야 한다
    한 테스트 객체가 만들어지면 non-static 메소드일 수 있다
    */
    @BeforeAll
    static void initData(){ // 스태틱 메소드는 무조건 스태틱 멤버필드만 사용가능
        existedData.setFeedId(FEED_ID_1);
        existedData.setUserId(USER_ID_1);

        notExistedData.setFeedId(FEED_ID_5); //없는 데이터
        notExistedData.setUserId(USER_ID_2);
    }
    @Test // 메소드가 테스트화 된다

    //중복된 데이터 입력시 DuplicateKeyException 발생체크
    void insFeedLikeDuplicatedData() {
        //given (준비)
        FeedLikeReq givenParam = new FeedLikeReq();
        givenParam.setFeedId(FEED_ID_1);
        givenParam.setUserId(USER_ID_1);

        //when (실행)
        int actualAffectedRows = feedLikeMapper.insFeedLike(givenParam);

        //then (단언, 체크)
        assertThrows(DuplicateKeyException.class, () -> {
            feedLikeMapper.insFeedLike(existedData);
        },"데이터 중복시 에러 발생되지 않음 -> primary key(feed_id, user_id) 확인 바람");
    }


    @Test
    void insFeedLike(){

        //when
        List<FeedLikeVo> actualFeedLikeListBefore = feedLikeTestMapper.selFeedLikeAll(); // insert 전 튜플 수
        FeedLikeVo actualFeedLikeVoBefore = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(notExistedData);
        //좀더 디테일하게 하기 위해 where 절에 pk로 데이터를 가져온다
        // 진짜 null이 넘어오는지 체크
        int actualAffectedRows = feedLikeMapper.insFeedLike(notExistedData);
        FeedLikeVo actualFeedLikeVoAfter = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(notExistedData);
        //좀더 디테일하게 하기 위해 where 절에 pk로 데이터를 가져온다
        List<FeedLikeVo> actualFeedLikeListAfter = feedLikeTestMapper.selFeedLikeAll(); // insert 후 튜플수

        //then
        assertAll(
                () -> TestUtils.assertCurrentTimestamp(actualFeedLikeVoAfter.getCreatedAt()),() -> assertEquals(actualFeedLikeListBefore.size() + 1, actualFeedLikeListAfter.size())
               ,() -> assertNull(actualFeedLikeVoBefore) // 내가 insert하려고 하는 데이터가 없었는지 단언
               ,() -> assertNotNull(actualFeedLikeVoAfter) // 실제 insert가 내가 원하는 데이터로 되었는지 단언
               ,() -> assertEquals(1,actualAffectedRows)
               ,() -> assertEquals(notExistedData.getFeedId(),actualFeedLikeVoAfter.getFeedId()) // 내가 원하는 데이터로 insert되었는지 더블체크
               ,() -> assertEquals(notExistedData.getUserId(),actualFeedLikeVoAfter.getUserId()) // 내가 원하는 데이터로 insert되었는지 더블체크
        );

        // 검증전략 insert 가 원하는 대로 되었는지
    }


    @Test
    void insFeedLikeNormal() {


        //when (실행)
        int actualAffectedRows = feedLikeMapper.insFeedLike(notExistedData);
        // 존재하지 않는것을 하기에 예외가 없
        //then (단언, 체크)
        assertEquals(1, actualAffectedRows,"insert 문제 발생");

    }

    @Test
    void deleteFeedNoData() {

        int actualAffectedRows = feedLikeMapper.delFeedLike(notExistedData);
        assertEquals(0, actualAffectedRows);
    }

    @Test
    void delFeedLike() {
        //given
        feedLikeMapper.insFeedLike(existedData); // 삭제 테스트를 위해 데이터를 미리 삽입
        FeedLikeVo feedLikeBeforeDelete = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(existedData);
        assertNotNull(feedLikeBeforeDelete, "삭제 대상 데이터가 존재해야함");

        //when
        int actualAffectedRows = feedLikeMapper.delFeedLike(existedData);

        //then
        FeedLikeVo feedLikeAfterDelete = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(existedData);
        assertAll(
                () ->  assertEquals(1, actualAffectedRows,"영향받은 행의 수는 1이어야 한다"),
                () -> assertNull(feedLikeBeforeDelete,"데이터가 삭제되어야 한다")
                );
    }

    @Test
    void delFeedLikeDo() {
        FeedLikeVo actualFeedLikeVoBefore = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(existedData);
        int actualAffectedRows = feedLikeMapper.delFeedLike(existedData);
        FeedLikeVo actualFeedLikeVoAfter = feedLikeTestMapper.selFeedLikeByFeedIdAndUserId(existedData);

        assertAll(
                 () -> assertEquals(1, actualAffectedRows,"delete 문제 발생")
                ,() -> assertNotNull(actualFeedLikeVoBefore)
                ,() -> assertNull(actualFeedLikeVoAfter)
        );

    }
}