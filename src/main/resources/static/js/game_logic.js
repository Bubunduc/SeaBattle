function addFightElements() {

    const footer = document.querySelector("footer");

    const button = document.createElement("button");
    button.classList.add("btn", "btn-primary", "mt-3");
    button.id = "attackButton";
    button.textContent = "Атаковать";
    button.onclick = sendAttackToServer;
    footer.parentNode.insertBefore(button, footer);
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
             alert("Попадание!!!");
             cell.classList.replace("opponent-ship","ship")
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
function updateField(row,col)
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
            alert("Ваш корабль подбит!");
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