package com.green.greengram.feed.like.medel;


import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/* Setter 대신에 드렁옴 immutable(불변성)하게 만들고 싶
 (그래서 클래스명을 Vo : 값 그 자체흘 표현)
private 한 멤버필드에 값 넣는 방식은 생성자와 setter
setter 를 뺌으로서 남은 선택지는 생성자
생성자를 이용해서 객체생성을 하는데 멤버필드 세팅하는 경우의 수가 많을 수 있다

    Getter는 무적권 있어야 한다.
 */


/*
두개의 객체 그리고 똑같은 값을 가짐 동일성인가? -> true false
-> false 염연히 다른 것  동일성은 같은 객체를 가리키는지

@EqualsAndHashCode 이 애노테이션을 쓰면 오버라이딩이 (부모가 가지고 있는 메소드를 선언부를 똑같이 적어서 재정의) 됨
Equals 메소드랑 HashCode 메소드 재정의



 */


@Builder
@Getter
@EqualsAndHashCode
public class FeedLikeVo {
    private long feedId;
    private long userId;
    private String createdAt;
}
