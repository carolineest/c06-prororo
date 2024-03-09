// package propensi.c06.sipp.security;

// import propensi.c06.sipp.security.jwt.JwtTokenFilter;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.annotation.Order;
// import org.springframework.security.config.Customizer;
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

// @Configuration
// @EnableWebSecurity
// public class WebSecurityConfig {
//     @Autowired
//     private UserDetailsService userDetailsService;

//     @Autowired
//     private JwtTokenFilter jwtTokenFilter;


//     @Bean
//     @Order(1)
//     public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {

//         http.securityMatcher("/api/**")
//                 .csrf(AbstractHttpConfigurer::disable)
//                 .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
//                         .requestMatchers("/api/user/add").hasAuthority("Admin")
//                         .requestMatchers("/api/auth/**").permitAll()
//                         .anyRequest().authenticated()
//                 )
//                 .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                 .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

//         return http.build();
//     }

