package com.seabattle.Course_work.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class BattleService {
    private final SimpMessagingTemplate messagingTemplate;

    public BattleService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Отправка сообщения другому игроку
    public void sendMessageToOpponent(String opponentSessionId, String message) {
        messagingTemplate.convertAndSendToUser(opponentSessionId, "/topic/game", message);
    }

    // Пример логики для обработки выстрела
    public void handleShot(String playerSessionId, String shotCoordinates) {
        // Логика выстрела
        String message = "Игрок с координатами " + shotCoordinates + " выстрелил!";
        sendMessageToOpponent(playerSessionId, message);  // Отправка сообщения противнику
    }
}
