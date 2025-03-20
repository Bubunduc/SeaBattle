package com.seabattle.Course_work.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 *Модель для создания и работы с таблицей, отвечающей за сохранение результатов игры
 * Long id - айди игры
 * String winner - Имя победителя
 * String loser - Имя проигравшего
 * private LocalDateTime gameDateTime - время окончания игры
 * Автор - Румянцев Данила бИЦ-221
 */
@Entity
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Генерация уникального ID
    private Long id;

    @Column(name = "winner", nullable = false) // Владелец игры
    private String winner;



    @Column(name = "loser", nullable = false) // Оппонент
    private String loser;

    @Column(name = "game_date_time", nullable = false) // Дата и время игры
    private LocalDateTime gameDateTime;

    // Конструкторы
    public Result() {
    }

    public Result(String winner, String loser, LocalDateTime gameDateTime) {
        this.winner = winner;
        this.loser = loser;
        this.gameDateTime = gameDateTime;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String gameOwner) {
        this.winner = gameOwner;
    }
    public String getLoser() {
        return loser;
    }

    public void setLoser(String loser) {
        this.loser = loser;
    }

    public LocalDateTime getGameDateTime() {
        return gameDateTime;
    }

    public void setGameDateTime(LocalDateTime gameDateTime) {
        this.gameDateTime = gameDateTime;
    }
}
