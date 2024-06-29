package org.sopt.jaksim.socket;

import org.sopt.jaksim.global.exception.IOException;
import org.sopt.jaksim.global.message.ErrorMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException.*;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    // Web Session을 담아두기 위한 맵 형식의 공간
    private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<>();

    // 사용자가 웹 소켓 서버에 접속하면 동작
    // 세션의 고유값을 key, WebSocketSession 값을 value로 CLIENT 변수에 저장
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        CLIENTS.put(session.getId(), session);
    }

    // 웹 소켓 서버 접속이 끝났을 때 동작
    // CLIENT 변수에 있는 해당 세션을 제거
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        CLIENTS.remove(session.getId());
    }

    // 사용자의 메시지를 받게 되면 동작
    // CLIENT 변수에 담긴 세션 값들을 가져와서 반복문으로 돌려서 메세지를 발송하면, 본인 이외의 사용자에게 메시지를 보냄
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String id = session.getId();  //메시지를 보낸 아이디
        CLIENTS.entrySet().forEach( arg->{
            if(!arg.getKey().equals(id)) {  //같은 아이디가 아니면 메시지를 전달합니다.
                try {
                    arg.getValue().sendMessage(message);
                } catch (java.io.IOException e) {
                    throw new IOException(ErrorMessage.BAD_REQUEST);
                }
            }
        });
    }
}
