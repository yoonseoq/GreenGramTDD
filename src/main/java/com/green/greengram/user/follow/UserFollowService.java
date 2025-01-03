package com.green.greengram.user.follow;

import com.green.greengram.config.security.AuthenticationFacade;
import com.green.greengram.user.follow.model.UserFollowReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFollowService {
    private final UserFollowMapper mapper;
    private final AuthenticationFacade authenticationFacade; // 주소값만 넣어주게 가짜를 만듦

    public int postUserFollow(UserFollowReq p) {
        //테스트는 예외발생되면 종료됨
        p.setFromUserId(authenticationFacade.getSignedUserId());
        return mapper.insUserFollow(p);

    }

    public int deleteUserFollow(UserFollowReq p) {
        p.setFromUserId(authenticationFacade.getSignedUserId());
        return mapper.delUserFollow(p);
    }
}
