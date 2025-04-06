// Функция для генерации случайных кораблей компьютера
function generateComputerShips() {
    opponentShips = [];
    let tempRemainingShips = {
        4: 1,
        3: 2,
        2: 3,
        1: 4
    };

    let length = 4;
    while (opponentShips.length < 10) {
        if (tempRemainingShips[length] > 0) {
            const coords = getRandomCoordinates();
            const direction = getRandomDirection();

            if (canComputerPlaceShip(coords.x, coords.y, length, direction)) {
                const cells = [];
                for (let i = 0; i < length; i++) {
                    const r = direction === "horizontal" ? coords.x : coords.x + i;
                    const c = direction === "horizontal" ? coords.y + i : coords.y;
                    cells.push({ row: r, col: c });
                }

                opponentShips.push({ length, cells });
                tempRemainingShips[length]--;
            }
        } else {
            length--;
            if (length < 1) length = 4;
        }
    }

    console.log("Корабли компьютера:", opponentShips);
    opponentfieldfull = true;
}

// Проверка возможности размещения корабля для компьютера
function canComputerPlaceShip(row, col, length, direction) {
    for (let i = 0; i < length; i++) {
        const r = direction === "horizontal" ? row : row + i;
        const c = direction === "horizontal" ? col + i : col;

        if (r < 0 || r >= 10 || c < 0 || c >= 10 || !isComputerCellEmpty(r, c)) {
            return false;
        }
    }
    return true;
}

// Проверка пустоты клетки для компьютера
function isComputerCellEmpty(row, col) {
    for (let r = row - 1; r <= row + 1; r++) {
        for (let c = col - 1; c <= col + 1; c++) {
            if (r >= 0 && r < 10 && c >= 0 && c < 10) {
                if (opponentShips.some(ship => ship.cells.some(cell => cell.row === r && cell.col === c))) {
                    return false;
                }
            }
        }
    }
    return true;
}

// Ход компьютера
function computerMove() {
    if (computerShipsLeft <= 0) return;

    let row, col;
    do {
        row = Math.floor(Math.random() * 10);
        col = Math.floor(Math.random() * 10);
    } while (computerAttackedCells.some(cell => cell.row === row && cell.col === col));

    computerAttackedCells.push({ row, col });

    setTimeout(() => {
        const cell = document.getElementById(`player-cell-${row}-${col}`);
        if (cell.innerHTML) {
            computerMove(); // Если клетка уже атакована, пробуем еще раз
            return;
        }

        cell.innerHTML = "❌";

        if (cell.classList.contains("ship")) {
            shipsleft--;
            if (shipsleft === 0) {
                alert("Игра окончена, компьютер победил");
                document.getElementById("turn").textContent = "Поражение";
                gameEnd();
                return;
            }

            const ship = findShipByCell(playerShips, row, col);
            if (isShipDestroyed(ship, "player")) {
                markSurroundingCells(ship, "player");
                alert("Компьютер уничтожил ваш корабль длиной " + ship.length + "!");
            } else {
                alert("Компьютер попал в ваш корабль!");
            }

            computerMove(); // Компьютер ходит снова после попадания
        } else {
            cell.classList.add("miss");
            alert("Компьютер промахнулся!");
            isyourturn = true;
            updateTurn(username);
        }
    }, 1000);
}

function isShipDestroyed(ship, fieldPrefix) {
    for (const cell of ship.cells) {
        const cellElement = document.getElementById(`${fieldPrefix}-cell-${cell.row}-${cell.col}`);
        if (!cellElement || !cellElement.innerHTML.includes("❌")) {
            return false;
        }
    }
    return true;
}

var attackedOpponentCells = []; // Массив для хранения атакованных клеток противника

function handleOpponentFieldClick() {
    if (!isyourturn) {
        alert("Дождитесь своего хода!");
        return;
    }

    // Убираем крестик с предыдущей выбранной клетки
    if (selectedAttackCell) {
        selectedAttackCell.innerHTML = "";
    }

    // Помечаем новую выбранную клетку крестиком
    this.innerHTML = "❌";
    selectedAttackCell = this;

    // Сохраняем координаты
    clickedrow = parseInt(this.getAttribute('data-row'));
    clickedcol = parseInt(this.getAttribute('data-col'));
}

// Обновленная функция sendAttackToServer
function sendAttackToServer() {
    if (!isyourturn) {
        alert("Дождитесь своего хода");
        return;
    }

    if (clickedrow === null || clickedcol === null) {
        alert("Выберите клетку для атаки");
        return;
    }

    const cell = document.getElementById(`opponent-cell-${clickedrow}-${clickedcol}`);

    // Проверяем, не атаковали ли уже эту клетку (дополнительная проверка)
    if (attackedOpponentCells.some(c => c.row === clickedrow && c.col === clickedcol)) {
        alert("Вы уже атаковали эту клетку!");
        return;
    }

    // Добавляем клетку в список атакованных
    attackedOpponentCells.push({ row: clickedrow, col: clickedcol });

    // Проверяем, есть ли корабль компьютера в этой клетке
    const ship = findShipByCell(opponentShips, clickedrow, clickedcol);
    if (ship) {
        cell.innerHTML = "❌";
        cell.classList.add("ship");

        computerShipsLeft--;
        if (computerShipsLeft === 0) {
            alert("Поздравляем, вы победили компьютер!");
            document.getElementById("turn").textContent = "Победа";
            gameEnd();
            return;
        }

        if (isShipDestroyed(ship, "opponent")) {
            markSurroundingCells(ship, "opponent");
            alert("Вы уничтожили корабль компьютера длиной " + ship.length + "!");
        } else {
            alert("Попадание!");
        }
    } else {
        cell.innerHTML = "❌";
        alert("Промах!");
        isyourturn = false;
        updateTurn(opponent);
        setTimeout(computerMove, 500);
    }

    // Сбрасываем выбранные координаты
    clickedrow = null;
    clickedcol = null;
}

function sendAttackToServer() {
    if (!isyourturn) {
        alert("Дождитесь своего хода");
        return;
    }

    if (!selectedAttackCell) {
        alert("Выберите клетку для атаки, кликнув по полю противника");
        return;
    }

    const row = parseInt(selectedAttackCell.getAttribute('data-row'));
    const col = parseInt(selectedAttackCell.getAttribute('data-col'));

    // Проверяем, не атаковали ли уже эту клетку
    if (attackedOpponentCells.some(c => c.row === row && c.col === col)) {
        alert("Вы уже атаковали эту клетку!");
        selectedAttackCell.innerHTML = "";
        selectedAttackCell = null;
        return;
    }

    // Добавляем клетку в список атакованных
    attackedOpponentCells.push({ row, col });

    const cell = selectedAttackCell;
    selectedAttackCell = null;

    // Проверяем, есть ли корабль компьютера в этой клетке
    const ship = findShipByCell(opponentShips, row, col);
    if (ship) {
        cell.classList.add("ship");

        computerShipsLeft--;
        if (computerShipsLeft === 0) {
            alert("Поздравляем, вы победили компьютер!");
            document.getElementById("turn").textContent = "Победа";
            gameEnd();
            return;
        }

        if (isShipDestroyed(ship, "opponent")) {
            markSurroundingCells(ship, "opponent");
            alert("Вы уничтожили корабль компьютера длиной " + ship.length + "!");
        } else {
            alert("Попадание!");
        }
    } else {
        cell.innerHTML = "❌";
        alert("Промах!");
        isyourturn = false;
        updateTurn(opponent);
        setTimeout(computerMove, 500);
    }
}
window.onload = function () {
    showGameField();

    // Модифицируем функцию отправки кораблей для игры с компьютером
    document.getElementById('sendShipsBtn').addEventListener('click', function() {
        if (playerShips.length == 10) {
            document.getElementById('sendShipsBtn').classList.add("hidden");
            document.getElementById('ship-selection').classList.add("hidden");
            document.getElementById('remaining-ships').classList.add("hidden");
            document.getElementById('shiplength').classList.add("hidden");
            document.getElementById('directionBtn').classList.add("hidden");
            document.getElementById('randomplace').classList.add("hidden");

            yourfieldfull = true;
            generateComputerShips();

            alert("Игра началась! Ваш ход.");
            updateTurn(username);
            addFightElements();
            addOpponentHandleClick();
            removeShipPlacementListeners();
        } else {
            alert("Вы еще не разместили все корабли");
        }
    });
}