function addFightElements() {

    const chat = document.getElementById("chat");

    const button = document.createElement("button");
    button.classList.add("btn", "btn-primary", "mt-3");
    button.id = "attackButton";
    button.textContent = "Атаковать";
    button.onclick = sendAttackToServer;
    chat.parentNode.insertBefore(button, chat);
}
function updateTurn(name)
{
    const turn = document.getElementById("turn");
    if (isyourturn == true)
    {
        turn.textContent = `В данный момент ходите Вы`;
    }
    else {

        turn.textContent = `В данный момент ходит ${name}`;
    }
}
function addOpponentHandleClick(){
    const opponentCells = document.querySelectorAll('#gameFields .game-field:nth-child(2) td');
    opponentCells.forEach(cell => {
        cell.addEventListener('click', handleOpponentFieldClick);
    });
}

function handleOpponentFieldClick() {

    if (previousCross) {
        previousCross.innerHTML = ""; // Убираем содержимое из предыдущей ячейки
    }
    var cross = "❌";
    if(this.innerHTML){
        alert("Крестик на этой клетке уже стоит!!");
    }
    else
    {
        this.innerHTML = cross; //
        //cell.style.color = "red"; // Устанавливаем цвет (по желанию)
        previousCross = this;
        clickedrow = parseInt(this.getAttribute("data-row"));
        clickedcol = parseInt(this.getAttribute("data-col"));
    }

}
function markSurroundingCells(ship, fieldPrefix) {
    const directions = [-1, 0, 1]; // для смещения по row и col

    for (const cell of ship.cells) {
        for (let dRow of directions) {
            for (let dCol of directions) {
                const newRow = cell.row + dRow;
                const newCol = cell.col + dCol;

                // Проверяем, что координаты в пределах поля
                if (newRow >= 0 && newRow < 10 && newCol >= 0 && newCol < 10) {
                    const neighborCell = document.getElementById(`${fieldPrefix}-cell-${newRow}-${newCol}`);

                    // Проверяем, что клетка не часть корабля и не уже отмечена
                    if (neighborCell && !neighborCell.classList.contains("ship") && !neighborCell.innerHTML) {
                        neighborCell.innerHTML = "❌";
                    }
                }
            }
        }
    }
}
function paintDestroyedShip(ship,fieldPrefix)
{
    var ship_len = ship.length
    var count = 0
    console.log(ship)
    if (fieldPrefix == "opponent"){

        cell.classList.replace(`${fieldPrefix}-ship`,`ship`)
    }
    if(ship_len>1) {
        for (const cell of ship.cells) {
            var ship_cell = document.getElementById(`${fieldPrefix}-cell-${cell.row}-${cell.col}`)
            if (ship_cell.innerHTML =="❌" ) {
                count += 1;
            }
        }
    }
    else
    {
        count = 1;
    }
    if (count==ship_len)
    {
        alert("Корабль с длиной палуб "+ship_len+" потоплен!")
        markSurroundingCells(ship,fieldPrefix)
    }
    else {
        if (fieldPrefix == "opponent")
        {
            alert("Попадание!!!");
        }
        else
        {
            alert("Ваш корабль подбит!");
        }

    }
}
function findShipByCell(ships, targetRow, targetCol) {
    console.log("Ищем корабль по координатам:", targetRow, targetCol);

    for (const ship of ships) {
        for (const cell of ship.cells) {
            console.log(`Проверяем ячейку корабля: row=${cell.row}, col=${cell.col}`);
            if (cell.row === targetRow && cell.col === targetCol) {
                console.log("Найдено совпадение!");
                return ship;
            }
        }
    }
    console.log("Совпадений не найдено.");
    return null;
}

function sendAttackToServer() {
    if(isyourturn==false){
        alert("Дождитесь своего хода")
        return;
    }
    if (previousCross == null){
        alert("Выберите клетку для атаки")
    }
    else {
        previousCross = null;
        const cell = document.getElementById(`opponent-cell-${clickedrow}-${clickedcol}`)
        var hit = false;
        if(cell.classList.contains("opponent-ship"))
        {
            console.log(shipsleft)

            console.log(playerShips)
            var ship = findShipByCell(opponentShips,clickedrow,clickedcol)
            var ship_len = ship.length
            var count = 0
            console.log(ship)
             cell.classList.replace("opponent-ship","ship")
            if(ship_len>1) {
                for (const cell of ship.cells) {
                    var ship_cell = document.getElementById(`opponent-cell-${cell.row}-${cell.col}`)
                    if (ship_cell.classList.contains("ship")) {
                        count += 1;
                    }
                }
            }
            else
            {
                count = 1;
            }
            if (count==ship_len)
            {
                alert("Корабль с длиной палуб "+ship_len+" потоплен!")
                markSurroundingCells(ship,"opponent")
            }
            else {
                alert("Попадание!!!");
            }
             isyourturn = true;
             hit = true
        }
        else {
            alert("Промах!");
            isyourturn = false;
        }

    const attackData = {
        'action': "attack",
        'row': clickedrow,
        'col': clickedcol,
        'gameOwner': gameOwner,
        'opponent': opponent,
        'hit':hit
    };

    stompClient.send('/app/game/attack', {}, JSON.stringify(attackData));
    console.log("Координаты атаки отправлены на сервер:", attackData);

    updateTurn(opponent)
    }
}
function updateField(row,col,opponentShips)
{
    const cell = document.getElementById(`player-cell-${row}-${col}`);
    cell.innerHTML = "❌";
    if (cell.classList.contains("ship"))
    {
        shipsleft--;
        if (shipsleft==0)
        {
            alert(`Игра окончена, вы проиграли`);
            turn = document.getElementById("turn").textContent = "Поражение";
            const winnerData = {
                'action': "result",
                'winner' : opponent,
                'username' : username,
                'opponent':opponent,
                'gameOwner': gameOwner
            };
            stompClient.send('/app/game/result', {}, JSON.stringify(winnerData));
        }
        else {
            var ship = findShipByCell(playerShips,row,col)
            console.log(ship)
            paintDestroyedShip(ship,"player")
        }

    }
    else {
        alert("Противник не попал!");
    }

}
function gameEnd()
{
    var btn = document.getElementById("attackButton");
    btn.classList.add("hidden");
}