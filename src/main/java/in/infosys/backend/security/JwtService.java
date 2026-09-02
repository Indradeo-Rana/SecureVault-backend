package in.infosys.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

//        └── Generate token
    public String generateToken(String username){ // later username changed to UserDetails
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

//        ├── Extract all claims from the token
            private Claims extractAllClaims(String token){
                return Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
            }

//        ├── Extract username for verification
            public String extractUsername(String token){
                return extractAllClaims(token).getSubject();
            }

//         ├── Extract expiration date for verification
            public Date extractExpiration(String token){
                return extractAllClaims(token).getExpiration();
            }

//        ├── Check expiration
            public boolean isTokenExpired(String token){
                return extractAllClaims(token).getExpiration().before(new Date());
            }
//        └── Validate token
            public boolean validateToken(String token, String username){
                String extractedUsername = extractUsername(token);
                return (extractedUsername.equals(username)
                        && !isTokenExpired(token));
            }
}
