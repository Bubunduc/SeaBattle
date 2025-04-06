function showGameField() {
    const gameField = document.createElement('div');
    gameField.classList.add('container', 'mt-5');
    const playerField = createField('player');
    const opponentField = createField('opponent');

    gameField.innerHTML = `
        <h2 id="turn">Игровое поле</h2>
        <p id="players">Игра началась между: ${username} и ${opponent}</p>
        <div id="remaining-ships">
            <h3>Оставшиеся корабли:</h3>
            <ul>
                <li>Линкор (4 клетки): <span id="ship-4">1</span></li>
                <li>Крейсеры (3 клетки): <span id="ship-3">2</span></li>
                <li>Эсминцы (2 клетки): <span id="ship-2">3</span></li>
                <li>Подлодки (1 клетка): <span id="ship-1">4</span></li>
            </ul>
        </div>
        <div id="gameFields" class="d-flex justify-content-between">
            <div class="game-field">
                <h3>Ваше поле</h3>
                ${playerField}
            </div>
            <div class="game-field">
                <h3>Поле противника</h3>
                ${opponentField}
            </div>
        </div>
        <div id="ship-controls">
            <button id="directionBtn" onclick="toggleDirection()">Направление: Горизонтально</button>
        </div>
        <h1 id="shiplength">Длина текущего корабля: </h1>
        <div id="ship-selection">
            <h3>Выберите корабль для размещения:</h3>
            <button onclick="setCurrentShip(4)">Линкор (4 клетки)</button>
            <button onclick="setCurrentShip(3)">Крейсер (3 клетки)</button>
            <button onclick="setCurrentShip(2)">Эсминец (2 клетки)</button>
            <button onclick="setCurrentShip(1)">Подлодка (1 клетка)</button>
        </div>
        <button id="randomplace" class="btn btn-primary mt-3">Расставить корабли случайно</button>
        <div>
        <button id="sendShipsBtn" class="btn btn-primary mt-3">Отправить корабли</button>
           </div>

        
        <div id="chat" class="mt-5">
            <h3>Чат</h3>
            <div id="chat-messages" style="border: 1px solid #ccc; height: 200px; overflow-y: scroll; padding: 10px; background-color: #f9f9f9;">
                
            </div>
            <div class="input-group mt-2">
                <input type="text" id="chat-input" class="form-control" placeholder="Введите сообщение...">
                <button id="send-chat-btn" class="btn btn-secondary">Отправить</button>
            </div>
        </div>
    `;

    const footer = document.querySelector('footer');
    footer.parentNode.insertBefore(gameField, footer);

    addShipPlacementListeners();
    document.getElementById('sendShipsBtn').addEventListener('click', sendShipsToServer);
    document.getElementById('randomplace').addEventListener('click', randomPlace);
    // Обработчик отправки сообщений
    document.getElementById('send-chat-btn').addEventListener('click', () => {
        const input = document.getElementById('chat-input');
        const message = input.value.trim();
        if (message !== '') {
            appendChatMessage(username, message);
            data = {
                "action":"chat",
                "sender":username,
                "message":message,
                'game_owner':gameOwner
            }
            stompClient.send('/app/game/chat', {}, JSON.stringify(data));
            input.value = '';

        }
    });
}
function getRandomInt(max) {
    return Math.floor(Math.random() * max);
}
function getRandomDirection()
{
    var random_num = getRandomInt(2)
    if (random_num == 0)
    {
        return "horizontal";
    }
    else
    {
        return "vertical";
    }
}
function getRandomCoordinates() {
    const x = Math.floor(Math.random() * 10);
    const y = Math.floor(Math.random() * 10);
    return { "x": x, "y": y };


}

//случайно размещение кораблей на поле
function randomPlace()
{
    clearShips('player')
    remainingShips = {
        4: 1, // Линкор (1 корабль длиной 4 клетки)
        3: 2, // Крейсеры (2 корабля длиной 3 клетки)
        2: 3, // Эсминцы (3 корабля длиной 2 клетки)
        1: 4  // Подлодки (4 корабля длиной 1 клетка)
    };
    playerShips = [];
    renderRemainingShips();
    var length = 1
    while (playerShips.length!=10)
    {
        if (remainingShips[length]!=0) {
            var coords = getRandomCoordinates();
            var direction = getRandomDirection();
            console.log(coords,direction,length)
            if (canPlaceShip(coords["x"],coords["y"],length,direction)==true)
            {
                placeShip(coords["x"],coords["y"],length,direction)
            }
        }
        if (remainingShips[length]==0)
        {
            length+=1;
        }
        if (length>4)
        {
            break;
        }
    }
}
// Функция для отображения сообщения в чате
function appendChatMessage(sender, message) {
    const chatBox = document.getElementById('chat-messages');
    const msgElement = document.createElement('div');
    msgElement.innerHTML = `<strong>${sender}:</strong> ${message}`;
    chatBox.appendChild(msgElement);
    chatBox.scrollTop = chatBox.scrollHeight;
}

function sendShipsToServer() {

    if (playerShips.length==10)
    {
        const data = {
            'gameOwner':gameOwner,
            'username': opponent,
            'ships': playerShips
        };
        // Отправляем данные о размещенных кораблях на сервер через WebSocket
        stompClient.send('/app/game/place-ships', {}, JSON.stringify(data));
        document.getElementById('sendShipsBtn').classList.add("hidden")
        document.getElementById('ship-selection').classList.add("hidden")
        document.getElementById('remaining-ships').classList.add("hidden")
        document.getElementById('shiplength').classList.add("hidden")
        document.getElementById('directionBtn').classList.add("hidden")
        document.getElementById('randomplace').classList.add("hidden")
        alert("Корабли отправлены противнику");
    }
    else {
        alert("Вы еще не разместили все корабли");
    }

}
function clearShips(type) {
    const cells = document.querySelectorAll(`#${type}-field .game-cell.ship`);
    cells.forEach(cell => cell.classList.remove('ship'));
}
function createField(type) {
    let fieldHTML = `<table id ="${type}-field" class="game-table">`;

    // Создаем таблицу 10x10
    for (let i = 0; i < 10; i++) {
        fieldHTML += '<tr>';
        for (let j = 0; j < 10; j++) {
            if (type === 'player') {
                fieldHTML += `<td class="game-cell editable" id="${type}-cell-${i}-${j}" data-row="${i}" data-col="${j}"></td>`;
            } else {
                fieldHTML += `<td class="game-cell" id="${type}-cell-${i}-${j}" data-row="${i}" data-col="${j}"></td>`;
            }
        }
        fieldHTML += '</tr>';
    }

    fieldHTML += '</table>';
    return fieldHTML;
}
function addShipPlacementListeners() {
    const playerCells = document.querySelectorAll('#gameFields .game-field:nth-child(1) td');
    playerCells.forEach(cell => {
        cell.addEventListener('click', addHandleCellClick);
    });
}
function addHandleCellClick()
{
    const row = parseInt(this.getAttribute('data-row'));
    const col = parseInt(this.getAttribute('data-col'));

    // Проверяем, есть ли корабль в указанной ячейке
    const shipIndex = playerShips.findIndex(ship =>
        ship.cells.some(cell => cell.row === row && cell.col === col)
    );

    if (shipIndex !== -1) {
        // Если в ячейке уже есть корабль — убираем его
        removeShip(shipIndex);
    } else if (currentShip) {
        // Если ячейка пуста — пытаемся разместить корабль
        if (!placeShip(row, col, currentShip, shipDirection)) {
            alert("Невозможно разместить корабль здесь!");
        }
    } else {
        alert("Выберите корабль перед размещением!");
    }
}

function removeShip(shipIndex) {
    const ship = playerShips[shipIndex];

    // Удаляем клетки корабля с игрового поля (визуально)
    ship.cells.forEach(cell => {
        const cellElement = document.getElementById(`player-cell-${cell.row}-${cell.col}`);
        cellElement.classList.remove("ship");
    });


    playerShips.splice(shipIndex, 1);


    updateRemainingShipsOnRemove(ship.length);
    renderRemainingShips();
    console.log("Корабль удалён", playerShips);
}


function updateRemainingShipsOnRemove(length) {
    if (remainingShips[length] !== undefined) {
        remainingShips[length]++;
    }
}
function removeShipPlacementListeners()
{
    const playerCells = document.querySelectorAll('#gameFields .game-field:nth-child(1) td');
    playerCells.forEach(cell => {
        cell.removeEventListener('click', addHandleCellClick);
    });
}
function placeShip(row, col, length, direction) {
    if (!canPlaceShip(row, col, length, direction)) {
        return false;
    }

    const cells = [];
    for (let i = 0; i < length; i++) {
        const r = direction === "horizontal" ? row : row + i;
        const c = direction === "horizontal" ? col + i : col;

        // Отмечаем ячейку на поле
        const cell = document.getElementById(`player-cell-${r}-${c}`);
        cell.classList.add("ship");
         // Визуальная индикация
        cells.push({ row: r, col: c });

    }

    playerShips.push({ length, cells });
    console.log(playerShips)
    updateRemainingShips(length);
    renderRemainingShips();
    return true;
}
function toggleDirection() {
    shipDirection = shipDirection === "horizontal" ? "vertical" : "horizontal";
    document.getElementById("directionBtn").textContent = `Направление: ${shipDirection === "horizontal" ? "Горизонтально" : "Вертикально"}`;
}
function canPlaceShip(row, col, length, direction) {
    if (remainingShips[length] <= 0) {
        alert("Все корабли этого типа уже размещены!");
        return false;
    }

    for (let i = 0; i < length; i++) {
        const r = direction === "horizontal" ? row : row + i;
        const c = direction === "horizontal" ? col + i : col;

        if (r < 0 || r >= 10 || c < 0 || c >= 10 || !isCellEmpty(r, c)) {
            return false;
        }
    }
    return true;
}
// Проверяет, пуста ли ячейка
function isCellEmpty(row, col) {
    // Проверяем саму ячейку и соседние
    for (let r = row - 1; r <= row + 1; r++) {
        for (let c = col - 1; c <= col + 1; c++) {
            if (r >= 0 && r < 10 && c >= 0 && c < 10) {
                if (playerShips.some(ship => ship.cells.some(cell => cell.row === r && cell.col === c))) {
                    return false;
                }
            }
        }
    }
    return true;
}
function setCurrentShip(length) {
    if (remainingShips[length] > 0) {
        currentShip = length;
        document.getElementById(`shiplength`).textContent = `Длина текущего корабля: ${length}`;

    } else {
        alert("Все корабли этого типа уже размещены!");
    }
}
function renderRemainingShips() {
    for (let length in remainingShips) {
        console.log(remainingShips)
        document.getElementById(`ship-${length}`).textContent = remainingShips[length];
    }
}

function updateOpponentField(ships) {
    ships.forEach(ship => {
        const cells = ship.cells; // Массив клеток корабля
        console.log(cells)
        // Перебираем каждую клетку корабля
        cells.forEach(cell => {
            const row = cell.row; // Координата строки клетки
            const col = cell.col; // Координата столбца клетки

            // Находим соответствующую ячейку на поле противника
            const cellElement = document.getElementById(`opponent-cell-${row}-${col}`);
            if (cellElement) {
                cellElement.classList.add("opponent-ship")
            }

        });
    });
}
function updateRemainingShips(length) {
    if (remainingShips[length] > 0) {
        remainingShips[length]--;
    }
}
function notifyServerAboutConnection() {

    var localname = username;
    if(username==gameOwner){
        localname = '';
    }

    stompClient.send('/app/game/connect', {}, JSON.stringify({
        'isCreated': isCreated,
        'username': localname,
        'gameOwner': gameOwner
    }));
}
