package com.seabattle.Course_work.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Файл для конфигурации Spring Security, в нем прописываются бины
 * для работы с Security, а именно доступы на страницы по ролям,
 */
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /**
         * .requestMatchers - метод, который позволяет ограничить доступ по ролям
         */
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login").permitAll()

                        // Доступ только для незарегистрированных пользователей
                        .requestMatchers("/register").not().fullyAuthenticated()
                        // Доступ только для пользователей с ролью Администратор
                        .requestMatchers("/changelog/create").hasRole("ADMIN")
                        .requestMatchers("/game/**").authenticated()
                        .requestMatchers("/profile").authenticated()
                        // Доступ разрешён всем
                        .requestMatchers("/", "/changelog").permitAll()

                        // Все остальные страницы требуют аутентификации
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureUrl("/login?error=true")
                        .defaultSuccessUrl("/",true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .permitAll()
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                );


        return http.build();
    }

    /**
     * Метод, который вызывет менеджер управления авторизацией
     * @param authenticationConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Метод возвращает класс для шифромки пароля
     * @return BCryptPasswordEncoder() - объект класса, который позволяет шифровать пароли перед их записью в бд
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
