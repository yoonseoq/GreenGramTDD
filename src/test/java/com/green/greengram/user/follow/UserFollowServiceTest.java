package com.green.greengram.user.follow;

import com.green.greengram.config.security.AuthenticationFacade;
import com.green.greengram.user.follow.model.UserFollowReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

//Spring Test Context (컨테이너) 이용하는거 아님
@ExtendWith(MockitoExtension.class)
class UserFollowServiceTest {
    @InjectMocks
    UserFollowService userFollowService; //Mokito가 객체화를 직접 한다

    @Mock // 가짜객체 만듦
    UserFollowMapper userFollowMapper;

    @Mock
    AuthenticationFacade authenticationFacade;
    // 1 -> 2 follow한 내용은 없
    static final long fromUserId1 = 1L;
    static final long toUserId2 = 2L;

    // 2 -> 1 follow한 내용은 없다
    static final long fromUserId3 = 3L;
    static final long toUserId4 = 4L;

    static final UserFollowReq userFollowReq1_2 = new UserFollowReq(fromUserId1);
    static final UserFollowReq userFollowReq3_4 = new UserFollowReq(fromUserId3);

    @Test
    @DisplayName("postUserFollow Test")
    void postUserFollow() {
        //given
        //authenticationFacade Mock 객체의 getSignedUserId()메소드를 호출하면
        final int EXPECTED_RESULT = 13;
        given(authenticationFacade.getSignedUserId()).willReturn(fromUserId1);

        UserFollowReq givenParam1_2 = new UserFollowReq(toUserId2);
        givenParam1_2.setFromUserId(fromUserId1);
        given(userFollowMapper.insUserFollow(givenParam1_2)).willReturn(EXPECTED_RESULT);

        //when
        UserFollowReq actualParam0_2 = new UserFollowReq(toUserId2);
        int actualResult = userFollowService.postUserFollow(actualParam0_2);

        //then
        assertEquals(EXPECTED_RESULT,actualResult);


    }

    @Test
    @DisplayName("deleteUserFollow Test")
    void deleteUserFollow() {
        final int EXPECTED_RESULT = 14;
        final long FROM_USER_ID = fromUserId3;
        final long TO_USER_ID = toUserId4;
        given(authenticationFacade.getSignedUserId()).willReturn(FROM_USER_ID);

        UserFollowReq givenParam = new UserFollowReq(TO_USER_ID);
        givenParam.setFromUserId(FROM_USER_ID);
        given(userFollowMapper.delUserFollow(givenParam)).willReturn(EXPECTED_RESULT);

        UserFollowReq actualParam = new UserFollowReq(TO_USER_ID);
        int actualResult = userFollowService.deleteUserFollow(actualParam);

        assertEquals(EXPECTED_RESULT,actualResult);

    }

}