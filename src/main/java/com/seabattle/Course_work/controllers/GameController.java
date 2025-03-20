package com.seabattle.Course_work.controllers;
import com.seabattle.Course_work.components.GameSessionManager;
import com.seabattle.Course_work.models.Result;
import com.seabattle.Course_work.services.ResultService;
import com.seabattle.Course_work.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.seabattle.Course_work.repositories.ResultRepository;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Контроллер овечающий за логику игры, создание и присоединение к игровой сессии
 * Для сесси создается отдельный топик, который формируется по формату
 * /topic/game/+"Имя владельца игры"
 */
@Controller
public class GameController {
    @Autowired
    private ResultService resultService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private GameSessionManager gameSessionManager;
    @Autowired
    private UserService userService;

    public GameController(GameSessionManager gameSessionManager) {
        this.gameSessionManager = gameSessionManager;
    }

    /**
     * Сообщение в топик, что игрок ожидает присоединения
     * @param gameOwner - ник владельца игры
     * @return
     */
    @MessageMapping("/game/wait")
    @SendTo("/topic/game-waiting")
    public String notifyPlayerJoined(@Payload String gameOwner) {
        return gameOwner + " ожидает второго игрока.";
    }

    /**
     * Обработка перехода на страницу для создания или присоединения к игре
     * @param model - модель сайта, позволяет добавлять аттрибуты
     * @return "game/game"
     */
    @GetMapping("/game/game")
    public String game(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("username", null);
        } else {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", authentication.getName());
        }

        return "game/game";
    }

    /**
     * Мапинг, обрабатывающий присоединение к существующей игре
     * @param model
     * @return "/game/connect"
     */
    @GetMapping("/game/connect")
    public String connect_game(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("username", null);
        } else {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", authentication.getName());
        }
        Map<String, String> activeGames = gameSessionManager.getActiveGames();
        List<String> availableGames = activeGames.entrySet().stream()
                .filter(entry -> "waiting".equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        model.addAttribute("availableGames", availableGames);
        return "game/connect";
    }

    /**
     * Обработка мапинга на страницу, с которой можно подключиться к существующей игре
     * @param model
     * @return "/game/create"
     */
    @GetMapping("/game/create")
    public String createGame(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("username", null);
        } else {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", authentication.getName());
        }
        model.addAttribute("iscreate", true);
        String username = authentication.getName();

        return "redirect:/game/play?iscreate=true&gameOwner=" + username;  // Если не готова, остаемся на странице создания игры
    }

    /**
     * Мапинг обрабатывающий переход на страницу, где реализована сам игра
     * @param model -модель сайта для внедрения аттрибутов
     * @param principal - объект с помощью которого можно получить актуальную информацию о текущем пользователе
     * @param iscreate - Флаг, который определяет, игрок является создавшим комнату или присоединившимся
     * @param gameOwner
     * @return
     */
    @GetMapping("/game/play")
    public String playGame(Model model, Principal principal,@RequestParam boolean iscreate,@RequestParam String gameOwner) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("username", null);
        } else {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", authentication.getName());
        }
        model.addAttribute("username", principal.getName());
        String currentPlayer = principal.getName();

        if(iscreate){
            gameSessionManager.createGame(currentPlayer);
            String opponent = gameSessionManager.getOpponent(currentPlayer);
            model.addAttribute("gameOwner", currentPlayer);
            model.addAttribute("opponent", opponent);
        }
        else {
            String opponent = gameOwner;
            model.addAttribute("gameOwner", opponent);
            model.addAttribute("opponent",currentPlayer );
            gameSessionManager.setOpponent(gameOwner,currentPlayer);
        }
        model.addAttribute("iscreate", iscreate);

        return "game/play";

    }

    /**
     * Мапинг, обрабатывающий отправку данных со страницы подключения
     * @param gameOwner -имя владельца игры, оно передается для подключения к игре
     * @param principal - объект, который дает нам возможность получить информацию о пользователе
     * @param model - модель сайта
     * @return Переход на станицу, если не произошло ошибок
     */
    @PostMapping("/game/connect")
    public String connectToGame(@RequestParam String gameOwner, Principal principal, Model model) {
        // Логика подключения к игре
        model.addAttribute("gameOwner", gameOwner);
        model.addAttribute("username", principal.getName());
        String currentPlayer = principal.getName();
        gameSessionManager.joinGame(gameOwner, currentPlayer); // Подключаемся к игре
        if (gameSessionManager.isjoined(gameOwner, currentPlayer)) {
            model.addAttribute("gameOwner", gameOwner);
            model.addAttribute("username", currentPlayer);
            model.addAttribute("iscreate", false);
            return "redirect:/game/play?iscreate=false&gameOwner=" + gameOwner;
        } else {
            model.addAttribute("error", "Не удалось подключиться к игре. Проверьте имя создателя.");
            return "game/connect";
        }
    }

    /**
     * Обработчик отправки сообщения о подключении игрока
     * @param message - json, который обрабатывается как обычная хэш таблица
     * @param principal
     * @return сообщение по подключении
     */
    @MessageMapping("/game/connect")
    public Map<String, Object> handlePlayerConnection(@Payload Map<String, String> message, Principal principal) {
        String username = principal.getName();
        boolean isCreated = Boolean.parseBoolean(message.get("isCreated"));
        String gameOwner = message.get("gameOwner");
        if (!isCreated) {

            // Уведомить всех подписчиков на игру через topic (например, оба игрока)
            simpMessagingTemplate.convertAndSend("/topic/game/" + gameOwner, Map.of("connected", true, "opponent", username,"action","connect"));
        }


        // Отправить ответ игроку
        return Map.of("connected", true);
    }
    /*
    {"action":"placeShips","opponent":"User",
    "ships":[
    {"length":4,"cells":[{"row":0,"col":5},{"row":0,"col":6},{"row":0,"col":7},{"row":0,"col":8}]},
    {"length":3,"cells":[{"row":2,"col":5},{"row":2,"col":6},{"row":2,"col":7}]},
    {"length":3,"cells":[{"row":2,"col":1},{"row":2,"col":2},{"row":2,"col":3}]},
    {"length":2,"cells":[{"row":5,"col":7},{"row":6,"col":7}]},
    {"length":2,"cells":[{"row":5,"col":5},{"row":6,"col":5}]},
    {"length":2,"cells":[{"row":5,"col":3},{"row":6,"col":3}]},
    {"length":1,"cells":[{"row":8,"col":8}]},
    {"length":1,"cells":[{"row":8,"col":3}]},
    {"length":1,"cells":[{"row":6,"col":1}]},
    {"length":1,"cells":[{"row":4,"col":1}]}]}
    */

    /**
     * Сообщение для обработки отправки кораблей противнику, чтобы разместить вражеские корабли на
     * вражеском пол. json имеет вид
     * {"action":"placeShips","opponent":"User",
     *     "ships":[
     *     {"length":4,"cells":[{"row":0,"col":5},{"row":0,"col":6},{"row":0,"col":7},{"row":0,"col":8}]},
     *     {"length":3,"cells":[{"row":2,"col":5},{"row":2,"col":6},{"row":2,"col":7}]},
     *     {"length":3,"cells":[{"row":2,"col":1},{"row":2,"col":2},{"row":2,"col":3}]},
     *     {"length":2,"cells":[{"row":5,"col":7},{"row":6,"col":7}]},
     *     {"length":2,"cells":[{"row":5,"col":5},{"row":6,"col":5}]},
     *     {"length":2,"cells":[{"row":5,"col":3},{"row":6,"col":3}]},
     *     {"length":1,"cells":[{"row":8,"col":8}]},
     *     {"length":1,"cells":[{"row":8,"col":3}]},
     *     {"length":1,"cells":[{"row":6,"col":1}]},
     *     {"length":1,"cells":[{"row":4,"col":1}]}]}
     * Где action - это действие для обработки отправки(действия задаются в javascript на странице и
     * обрабатываются на сервере)
     * length - Палубность корабля
     * cells - массив с координатами этого корабля
     * opponent - никнейм противника
     * @param data
     */
    @MessageMapping("/game/place-ships")
    public void handlePlaceShips(@Payload Map<String, Object> data) {
        // Извлечение данных из входного JSON
        List<Map<String, Integer>> ships = (List<Map<String, Integer>>) data.get("ships");
        String gameOwner = (String) data.get("gameOwner");
        String opponent = (String) data.get("username");

        // Проверка на null
        if (ships == null || gameOwner == null || opponent == null) {
            throw new IllegalArgumentException("Некоторые данные отсутствуют: ships, gameOwner или opponent равны null");
        }

        // Отправка данных противнику
        Map<String, Object> response = new HashMap<>();
        response.put("ships", ships);
        response.put("opponent", opponent);
        response.put("action", "placeShips");

        simpMessagingTemplate.convertAndSend("/topic/game/" + gameOwner, response);
    }
    /*const attackData = {
        'action': "attack",
        'row': clickedrow,
        'col': clickedrow,
        'gameOwner': gameOwner,
        'opponent': username
    };*/

    /**
     * Метод для отправки информации об атаке на сервер, обработка этого сообщения происходит
     * в javascript, где в переменной у клиента хранится информация о расположении своих кораблей
     * action - действие, которое сделал пользователь
     * row - столбец клетки
     * col - колонка клетки
     * gameOwner - имя владельца, необходимо для отправки запроса на нужный топик
     * opponent - имя пользователя
     * @param data
     */
    @MessageMapping("/game/attack")
    public void attack(@Payload Map<String, Object> data)
    {
        String gameOwner = (String) data.get("gameOwner");
        simpMessagingTemplate.convertAndSend("/topic/game/" + gameOwner, data);
    }

    /**
     * Обработчик сообщения, что игра окончена
     *
     * @param data
     */
    @MessageMapping("/game/result")
    public void result(@Payload Map<String, Object> data)
    {
        /**
         * @param gameOwner - имя владельца
         * @param username - имя проигравшего
         * @param winner - имя победителя
         */
        String gameOwner = (String) data.get("gameOwner");
        String username =  data.get("username").toString();
        String winner = data.get("winner").toString();
        /** обновление данных о пользователе, победитель увеличивает счетик подед,
         * а проигравший, соответственно, счетчик поражений*/
        userService.updateWins(winner);
        userService.updateLoses(username);
        /** Сохранение результата об игры в бд*/
        resultService.saveResult(winner,username);



        simpMessagingTemplate.convertAndSend("/topic/game/" + gameOwner, data);
    }





}
