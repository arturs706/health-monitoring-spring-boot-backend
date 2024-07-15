package uk.co.aipainappserver.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import uk.co.aipainappserver.users.domain_layer.entities.Users;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class JWTService {
    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);
    private static final String SECRET_KEY = "29B42BCF54C48DA866BFE1B6C589522F83242996E3E533E63C675E3B2CoNXk8SYA3KdSAISE7uc4B3IOVe9TRtoK";
    private static final Date JWT_TOKEN_VALIDITY = new Date(System.currentTimeMillis() + 1000 * 60 * 24);
    private static final Date JWT_REFRESH_TOKEN_VALIDITY = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7);
    // This method is used to extract the email from the token
    public String extractEmailFromToken(String token) {
        return extractClaimFromToken(token, Claims::getSubject);
    }


    public <T> T extractClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        // Log the claims
        logger.info("Claims: " + claims);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails){

        return generateToken(new HashMap<>(), userDetails, JWT_TOKEN_VALIDITY);
    }

    public String generateRefreshToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof Users) {
            claims.put("role", ((Users) userDetails).getUserrole().name());
        }
        return generateToken(new HashMap<>(), userDetails, JWT_REFRESH_TOKEN_VALIDITY);
    }


    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, Date JWT_TOKEN_VALIDITY) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(JWT_TOKEN_VALIDITY)
                .signWith(getSigningKey())
                .compact();
    }


    static Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private static SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String jwt, UserDetails userDetails) {
        final String email = extractEmailFromToken(jwt);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaimFromToken(token, Claims::getExpiration);
    }

}
