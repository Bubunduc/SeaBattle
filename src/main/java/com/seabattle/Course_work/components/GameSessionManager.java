package com.seabattle.Course_work.components;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * В данном классе прописана логика сессий, а именно созадние сессии, присоединение к сессии
 */
@Component
public class GameSessionManager {
    /**@param activeGames - таблица ключ значение, в которой хранятся игры, которые активны в данный момент */
    private final ConcurrentHashMap<String, String> activeGames = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, String> getActiveGames() {
        return activeGames;
    }

    /**
     * Метод для создания игры, то есть добавление нового значение в таблицу
     *
     * @param gameOwner - имя владельца игры, оно является ключом, значение это его противник
     * Пока противник не подключился, значание у таблицы является "waiting"
     */
    public void createGame(String gameOwner) {
        if (activeGames == null) {
            throw new IllegalStateException("activeGames is null!");
        }
        activeGames.put(gameOwner, "waiting");
        System.out.println("Game created by: " + gameOwner);
    }

    /**
     * Метод для присоединение к игре, он меняет значение с "waiting" на имя игрока
     * @param gameOwner - имя хоста игры
     * @param currentPlayer - имя игрока, желающего присоединиться
     */
    public void joinGame(String gameOwner, String currentPlayer) {
        if (activeGames.containsKey(gameOwner) && activeGames.get(gameOwner).equals("waiting")) {
            activeGames.put(gameOwner, currentPlayer); // Присоединяем второго игрока

        }
        else {

        }
    }

    /**
     * Метод, который проверяет, присоединен ли игрок к игре
     * @param gameOwner - хост игры
     * @param currentPlayer - присоединяющийся игрок
     * Вернет true, если да, иначе нет
     */
    public boolean isjoined(String gameOwner, String currentPlayer){
        if (activeGames.containsKey(gameOwner) && !activeGames.get(gameOwner).equals("waiting")) {
            activeGames.put(gameOwner, currentPlayer); // Присоединяем второго игрока
            return true;
        }
        return false;
    }

    /**
     * Геттер для получения имени оппонента из таблицы по имени игрока, если ввести имя
     * владельца, то будет выведено имя присоединившегося и наоборот
     * @param username - имя игрока, противника которого мы хотим получить
     * @return
     */
    public String getOpponent(String username) {
        for (var entry : activeGames.entrySet()) {
            if (entry.getKey().equals(username)) {
                return entry.getValue(); // Возвращаем второго игрока
            } else if (entry.getValue() != null && entry.getValue().equals(username)) {
                return entry.getKey(); // Возвращаем первого игрока
            }
        }
        return null;
    }

    /**
     * Сеттер для оппонента, чтобы менять "waiting" на имя игрока
     * @param username - имя владельца
     * @param opponent - имя противника
     */
    public void setOpponent(String username,String opponent) {
        activeGames.put(username, opponent);
    }

}