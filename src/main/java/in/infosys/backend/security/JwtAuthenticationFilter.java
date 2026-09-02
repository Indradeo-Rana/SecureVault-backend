package in.infosys.backend.security;

import in.infosys.backend.entity.User;
import in.infosys.backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService customUserDetailsService
    ) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Step 1: Read Authorization header
        String authHeader = request.getHeader("Authorization");


        // Step 2: Check if JWT exists
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            // No JWT → continue request
            filterChain.doFilter(request, response);
            return;
        }


       try{
           // Step 3: Extract JWT
           String jwt = authHeader.substring(7);


           // Step 4: Extract username from JWT
           String username = jwtService.extractUsername(jwt);


           // Step 5: Authenticate only if user is not already authenticated
           if (
                   username != null &&
                           SecurityContextHolder
                                   .getContext()
                                   .getAuthentication() == null
           ) {

               // Step 6: Load user from database
               UserDetails userDetails =
                       customUserDetailsService
                               .loadUserByUsername(username);


               // Step 7: Validate JWT
               if (
                       jwtService.validateToken(
                               jwt,
                               userDetails.getUsername()
                       )
               ) {

                   // Step 8: Create authentication object
                   UsernamePasswordAuthenticationToken authentication =
                           new UsernamePasswordAuthenticationToken(
                                   userDetails,
                                   null,
                                   userDetails.getAuthorities()
                           );


                   // Step 9: Tell Spring Security
                   // that this user is authenticated
                   SecurityContextHolder
                           .getContext()
                           .setAuthentication(authentication);
               }
               }
           } catch (Exception e) {

               // Invalid/expired JWT
               // Don't authenticate the user
               SecurityContextHolder.clearContext();
           }


        // Step 10: IMPORTANT
        // Always continue the filter chain
        filterChain.doFilter(request, response);
    }

    @Service
    public static class CustomUserDetailsService implements UserDetailsService {

        private final UserRepository userRepository;
        public CustomUserDetailsService(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() ->
                            new UsernameNotFoundException("User not found")
                    );
            return new CustomUserDetails(user);
        }
    }
}
