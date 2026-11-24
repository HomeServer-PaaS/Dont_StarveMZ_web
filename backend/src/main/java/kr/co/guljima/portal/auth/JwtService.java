package kr.co.guljima.portal.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private static final String HEADER = Base64.getUrlEncoder().withoutPadding()
        .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));

    private final byte[] secret;
    private final ObjectMapper objectMapper;
    private final long lifetimeSeconds;

    public JwtService(
        @Value("${app.jwt.secret}") String secret,
        @Value("${app.jwt.expiration-seconds:1800}") long lifetimeSeconds,
        ObjectMapper objectMapper
    ) {
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        if (this.secret.length < 32) {
            throw new IllegalStateException("JWT_SECRET must contain at least 32 bytes");
        }
        this.lifetimeSeconds = lifetimeSeconds;
        this.objectMapper = objectMapper;
    }

    public String issue(long userId) {
        try {
            String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(
                objectMapper.writeValueAsBytes(Map.of(
                    "sub", Long.toString(userId),
                    "iat", Instant.now().getEpochSecond(),
                    "exp", Instant.now().plusSeconds(lifetimeSeconds).getEpochSecond()
                ))
            );
            String unsigned = HEADER + "." + payload;
            return unsigned + "." + sign(unsigned);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not create JWT", exception);
        }
    }

    public Long subjectFrom(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;
            String unsigned = parts[0] + "." + parts[1];
            byte[] expected = sign(unsigned).getBytes(StandardCharsets.US_ASCII);
            byte[] provided = parts[2].getBytes(StandardCharsets.US_ASCII);
            if (!MessageDigest.isEqual(expected, provided)) return null;

            JsonNode claims = objectMapper.readTree(Base64.getUrlDecoder().decode(parts[1]));
            if (claims.path("exp").asLong(0) <= Instant.now().getEpochSecond()) return null;
            return Long.parseLong(claims.path("sub").asText());
        } catch (Exception ignored) {
            return null;
        }
    }

    private String sign(String value) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return Base64.getUrlEncoder().withoutPadding()
            .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.US_ASCII)));
    }
}
