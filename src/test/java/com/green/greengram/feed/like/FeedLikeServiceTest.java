package com.green.greengram.feed.like;

import com.green.greengram.config.security.AuthenticationFacade;
import com.green.greengram.feed.like.model.FeedLikeReq;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FeedLikeServiceTest {

    @InjectMocks
    private FeedLikeService feedLikeService;

    @Mock
    private FeedLikeMapper feedLikeMapper;


    @Mock
    private AuthenticationFacade authenticationFacade;

    private static final long SIGNED_USER_ID_3 = 3L;
    private static final long SIGNED_USER_ID_4 = 4L;
    private static final long FEED_ID_7 = 7L;
    private static final long FEED_ID_8 = 8L;

    @BeforeEach
    void setUpAuthenticationFacade() {
        given(authenticationFacade.getSignedUserId()).willReturn(SIGNED_USER_ID_3);

    }

    @Test
    void feedLikeToggleIns() {
        FeedLikeReq givenParam = new FeedLikeReq();
        givenParam.setUserId(SIGNED_USER_ID_3);
        givenParam.setFeedId(FEED_ID_8);
        given(feedLikeMapper.delFeedLike(givenParam)).willReturn(0);
        given(feedLikeMapper.insFeedLike(givenParam)).willReturn(1);

        FeedLikeReq actualParam = new FeedLikeReq();
        actualParam.setFeedId(FEED_ID_8);
        int actualResult = feedLikeService.feedLikeToggle(actualParam);

        assertEquals(1, actualResult);
    }
    @Test
    void feedLikeToggleIns2() {
        FeedLikeReq givenParam = new FeedLikeReq();
        givenParam.setUserId(SIGNED_USER_ID_4);
        givenParam.setFeedId(FEED_ID_8);
        given(feedLikeMapper.insFeedLike(givenParam)).willReturn(1);

        FeedLikeReq actualParam = new FeedLikeReq();
        int actualResult = feedLikeService.feedLikeToggle(actualParam);
        assertEquals(1, actualResult);

    }

    @Test
    void feedLikeToggleDel() {
        FeedLikeReq givenParam = new FeedLikeReq();
        givenParam.setUserId(SIGNED_USER_ID_3);
        givenParam.setFeedId(FEED_ID_7);
        given(feedLikeMapper.delFeedLike(givenParam)).willReturn(1);
        given(feedLikeMapper.insFeedLike(givenParam)).willReturn(0);

        FeedLikeReq actualParam = new FeedLikeReq();
        actualParam.setFeedId(FEED_ID_7);
        int actualResult = feedLikeService.feedLikeToggle(actualParam);
        assertEquals(0, actualResult);


    }
}