package com.seabattle.Course_work.models;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
/**
*Модель для обработки пользователей
* Поля:
* Long id - соответственно id пользователя
* String login - логин пользователя
* String password - пароль
* int wins - Количество побед
* int defeats - Количество поражений
* roles - Роли пользователя, необходимо для Spring Security
* Автор - Румянцев Данила бИЦ-221
 */
@Entity
public class User implements UserDetails {
    public User() {
    }
    public User(String username, String password) {
        this.login = username;
        this.password = password;
    }

    @Id
    @GeneratedValue(strategy =GenerationType.AUTO)
    private Long id;


    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min=2, message = "Не меньше 5 знаков")
    private String login;
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min=2, message = "Не меньше 5 знаков")
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;
    private int wins,defeats;
    //Геттеры и сеттеры
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }



    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getDefeats() {
        return defeats;
    }

    public void setDefeats(int defeats) {
        this.defeats = defeats;
    }
    //Перегрузки методов для осуществления работы Spring Security
    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public String getPassword() {
        return password;
    }
}
