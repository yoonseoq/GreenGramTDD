package com.green.greengram.feed;

import com.green.greengram.common.MyFileUtils;
import com.green.greengram.common.exception.CustomException;
import com.green.greengram.config.security.AuthenticationFacade;
import com.green.greengram.feed.comment.FeedCommentMapper;
import com.green.greengram.feed.model.FeedPicDto;
import com.green.greengram.feed.model.FeedPostReq;
import com.green.greengram.feed.model.FeedPostRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    FeedMapper feedMapper;
    @Mock
    FeedPicMapper feedPicMapper;
    @Mock
    FeedCommentMapper feedCommentMapper;
    @Mock
    MyFileUtils myFileUtils;
    @Mock
    MultipartFile multipartFile;
    @Mock
    AuthenticationFacade authenticationFacade;
    @InjectMocks
    FeedService feedService;

    final long FEED_ID_10 = 10L;
    final long SIGNED_USER_ID = 3L;
    final String LOCATION = "location";

    @Test
    @DisplayName("Insert시 영향받은 행이 0일때 예외발생")
    void test() {
        given(authenticationFacade.getSignedUserId()).willReturn(SIGNED_USER_ID);

        FeedPostReq givenParam = new FeedPostReq();
        givenParam.setWriterUserId(SIGNED_USER_ID);
        givenParam.setLocation(LOCATION);
        given(feedMapper.insFeed(givenParam)).willReturn(0);

        FeedPostReq actualParam = new FeedPostReq();
        actualParam.setLocation(LOCATION);

        assertThrows(CustomException.class
                , () -> feedService.postFeed(null, actualParam)
        );
    }

    @Test
    @DisplayName("MyFileUtils의 transferTo 호출시 잘못된 경로면, 예외 발생")
    void test2() throws Exception {
        given(authenticationFacade.getSignedUserId()).willReturn(SIGNED_USER_ID);
        FeedPostReq givenParam = new FeedPostReq();
        givenParam.setWriterUserId(SIGNED_USER_ID);
        givenParam.setLocation(LOCATION);
        given(feedMapper.insFeed(givenParam)).will(invocation -> {
            FeedPostReq invocationParam = (FeedPostReq) invocation.getArgument(0);
            invocationParam.setFeedId(FEED_ID_10);
            return 1;
        });
        final String SAVED_PIC_NAME_1 = "abc.jpg";
        MultipartFile mpf1 = new MockMultipartFile("pics", "test1.txt", "text/plain", "This is test1 file".getBytes());
        given(myFileUtils.makeRandomFileName(mpf1)).willReturn(SAVED_PIC_NAME_1);
        final String UPLOAD_PATH = "/home/download";
        given(myFileUtils.getUploadPath()).willReturn(UPLOAD_PATH);
        String expectedMiddlePath = String.format("feed/%d", FEED_ID_10);
        String givenFilePath1 = String.format("%s/%s", expectedMiddlePath, SAVED_PIC_NAME_1);
        given(myFileUtils.makeRandomFileName(mpf1)).willReturn(SAVED_PIC_NAME_1);
        doAnswer(invoctaion -> {
            throw new IOException();
        }).when(myFileUtils).transferTo(mpf1, givenFilePath1);
        List<MultipartFile> pics = new ArrayList<>(1);
        pics.add(mpf1);
        assertAll(
                () -> {
                    FeedPostReq actualParam = new FeedPostReq();
                    actualParam.setLocation(LOCATION);
                    assertThrows(CustomException.class, () -> feedService.postFeed(pics, actualParam));
                }
                , () -> verify(myFileUtils).makeFolders(expectedMiddlePath)
                , () -> {
                    String expectedDelFolderPath = String.format("%s/%s", UPLOAD_PATH, expectedMiddlePath);
                    verify(myFileUtils).deleteFolder(expectedDelFolderPath, true);
                }
        );
    }


    @Test
    void test2_1() throws Exception {
        given(authenticationFacade.getSignedUserId()).willReturn(SIGNED_USER_ID);

        FeedPostReq givenParam = new FeedPostReq();
        givenParam.setWriterUserId(SIGNED_USER_ID);
        givenParam.setLocation(LOCATION);
        given(feedMapper.insFeed(givenParam)).will(invocation -> {
            FeedPostReq invocationParam = (FeedPostReq) invocation.getArgument(0);
            invocationParam.setFeedId(FEED_ID_10);
            return 1;
        });

        final List<String> SAVED_FILE_NAMES = Arrays.asList("abc.jpg","def.jpg");
        final List<MultipartFile> PICS = Arrays.asList(
                new MockMultipartFile("pics", "test1.txt", "text/plain", "This is test1 file".getBytes()),
                new MockMultipartFile("pics", "test2.txt", "text/plain", "This is test2 file".getBytes())
        );
        for (int i = 0; i < SAVED_FILE_NAMES.size(); i++) {
            String picName = SAVED_FILE_NAMES.get(i);
            MultipartFile mpf = PICS.get(i);
            given(myFileUtils.makeRandomFileName(mpf)).willReturn(picName);
        }

        FeedPicDto expectedPicDto = new FeedPicDto();
        expectedPicDto.setFeedId(FEED_ID_10);
        expectedPicDto.setPics(SAVED_FILE_NAMES);

        FeedPostRes expectedResult = Feed











    }


}