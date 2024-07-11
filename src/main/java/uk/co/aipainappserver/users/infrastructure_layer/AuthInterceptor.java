package uk.co.aipainappserver.users.infrastructure_layer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class AuthInterceptor implements HandlerInterceptor {
    public final Logger logger = LogManager.getLogger();
    
    @Value("${token.signing.key}")
    private String secret_key;
    
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        String jwtToken = req.getHeader("authorization");
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
            jwtToken = jwtToken.substring(7);
            String secretKey = "89552cfd25b378f191d4dd784b20aacd38697f50c5cc856f97cb15907e9c9f5fb50f42c87135cf7baca4908f9bd1dcbd76881e3181091bbf3a79a2015f998065";
            try {
                String[] parts = jwtToken.split("\\.");
                String unsignedToken = parts[0] + "." + parts[1];
                byte[] signatureBytes = Base64.getUrlDecoder().decode(parts[2]);
                byte[] hash = secretKey.getBytes(StandardCharsets.UTF_8);
                Mac sha256Hmac = Mac.getInstance("HmacSHA256");
                SecretKeySpec secretKeyTwo = new SecretKeySpec(hash, "HmacSHA256");
                sha256Hmac.init(secretKeyTwo);
                byte[] signedBytes = sha256Hmac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8));
                if (!java.util.Arrays.equals(signedBytes, signatureBytes)) {
                    logger.error("Error: " + "Invalid JWT Token");
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                    return false; 
                } else {
                    return true;
                }
            
    } catch (Exception e) {
            logger.error("Error: " + e.getMessage());
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                return false; 
            } 
        } else {
            logger.error("Error: " + "Missing or invalid Authorization header");
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView model) throws Exception {
        final Logger logger = LogManager.getLogger();
        logger.info("AuthInterceptor::postHandle() called");
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) throws Exception {
        final Logger logger = LogManager.getLogger();
        logger.info("AuthInterceptor::afterCompletion() called");
    }

    private static String decode(String encodedString) {
    return new String(Base64.getUrlDecoder().decode(encodedString));
}
}
