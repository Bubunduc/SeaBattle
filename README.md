# Морской бой с друзьями  

Этот сайт предназначен для игры в **Морской бой** с друзьями.  

## Как играть  

1. Укажите название вашей базы данных и пароль в application.properties.Там же в первый раз замените spring.jpa.hibernate.ddl-auto=update на spring.jpa.hibernate.ddl-auto=create
2. Для таблицы role введите следующий запрос(он необходим для работы с security): INSERT INTO sea_battledb.role (id,name) VALUES (1,'ROLE_USER');
   INSERT INTO sea_battledb.role (id,name) VALUES (2,'ROLE_ADMIN'); 
3. Запустите проект.
4. Раздайте интернет или подключитесь с другом к одной сети.
5. Зарегистрируйтесь.
6. Войдите в свою учётную запись.
7. Перейдите в раздел **"Бой"**:  
   - Один игрок создаёт игру.  
   - Второй игрок присоединяется.
8. Расставьте корабли и начинайте сражение!  

ℹ **Нажав на свой ник, можно посмотреть статистику игр.**  

---

# Sea Battle with Friends  

This site is designed for playing **Sea Battle** with friends.  

## How to Play  

1. Enter your database name and password in the application.properties file.  
2. Launch the project.  
3. Share the Internet or connect to the same network with a friend.  
4. Complete the registration process.  
5. Log in to your account.  
6. Go to the **"Battle"** section:  
   - One player creates a game.  
   - The other joins.  
7. Place your ships and start the battle!  

ℹ **Click on your nickname to view your game statistics.**  
