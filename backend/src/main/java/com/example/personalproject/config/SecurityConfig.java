package com.example.personalproject.config;

import com.example.personalproject.entity.User;
import com.example.personalproject.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 的自訂設定。
 *
 * 這個設定會告訴 Spring Security：
 * 1. 哪些 API 不需要登入。
 * 2. 其他 API 要用資料庫裡的使用者驗證。
 * 3. API 使用 JWT，前端登入後帶 Bearer Token。
 */
@Configuration
public class SecurityConfig {

    @Value("${app.cors.allowed-origin:http://localhost:4200}")
    private String allowedOrigin;

    /**
     * SecurityFilterChain 來自 Spring Security。
     *
     * 它可以想成 API 前面的檢查站：
     * 每一個請求進入 Controller 以前，先經過這裡判斷能不能通過。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        http
                // REST API 目前不使用瀏覽器表單，因此先關閉 CSRF 檢查。
                .csrf(csrf -> csrf.disable())

                // 允許設定好的前端來源呼叫 API。
                .cors(Customizer.withDefaults())

                // 設定哪些網址可以直接進入，其他網址都必須通過登入驗證。
                .authorizeHttpRequests(auth -> auth
                        // 註冊和登入必須讓尚未登入的人使用。
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/users",
                                "/api/users/login",
                                "/api/users/password-reset/request",
                                "/api/users/password-reset/confirm"
                        ).permitAll()

                        // 「我的問卷」需要登入；這個規則要放在公開問卷規則前面。
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/quiz/mine"
                        ).authenticated()

                        // 查看問卷內容可以公開使用。
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/quiz",
                                "/api/quiz/*"
                        ).permitAll()

                        // 只要登入就可以建立問卷。
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/quiz"
                        ).authenticated()

                        // 修改權限交給 QuizService 檢查建立者或 ADMIN。
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/quiz/*"
                        ).authenticated()

                        // 刪除單筆問卷由 QuizService 再檢查建立者或 ADMIN。
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/quiz",
                                "/api/quiz/*"
                        ).authenticated()

                        // 填寫問卷需要登入，但一般 USER 也可以填寫。
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/quiz/*/submit"
                        ).authenticated()

                        // 查看所有人的提交紀錄和答案明細，只給管理員。
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/quiz/*/responses",
                                "/api/quiz/*/details"
                        ).hasRole("ADMIN")

                        // 統計資料允許問卷建立者或 ADMIN，細節由 QuizService 檢查。
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/quiz/*/statistics"
                        ).authenticated()

                        // 其他沒有另外列出的 API 仍然需要登入。
                        .anyRequest().authenticated()
                )

                // JWT API 不保存登入 Session，每次請求都自行帶 Token。
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 在 Spring Security 的帳密驗證流程前先檢查 JWT。
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        // build() 會把上面的設定組成真正的 Security Filter Chain。
        return http.build();
    }

    /** CORS 是讓瀏覽器允許 Angular 呼叫另一個 Port 的設定。 */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigin));
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );
        configuration.setAllowedHeaders(
                List.of("Authorization", "Content-Type")
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * UserDetailsService 來自 Spring Security。
     *
     * Spring 收到登入資料後，會用 email 呼叫這個方法，
     * 再拿回傳的加密密碼和使用者輸入的密碼比對。
     */
    @Bean
    public UserDetailsService userDetailsService(
            UserRepository userRepository) {

        return email -> userRepository.findByEmail(email)
                .map(this::toSpringSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "使用者不存在"
                ));
    }

    /**
     * 把我們自己的 User Entity 轉成 Spring Security 看得懂的 UserDetails。
     */
    private org.springframework.security.core.userdetails.UserDetails
    toSpringSecurityUser(User user) {

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                // 從資料庫讀取這個帳號真正的角色，例如 USER 或 ADMIN。
                .roles(user.getRole())
                .build();
    }
}
