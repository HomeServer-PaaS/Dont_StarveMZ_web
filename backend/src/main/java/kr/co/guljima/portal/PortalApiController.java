package kr.co.guljima.portal;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import kr.co.guljima.portal.auth.JwtService;
import kr.co.guljima.portal.content.Content;
import kr.co.guljima.portal.content.ContentMapper;
import kr.co.guljima.portal.user.FavoriteMapper;
import kr.co.guljima.portal.user.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PortalApiController {
    private static final long DEMO_KAKAO_USER_ID = 990000000000L;

    private final ContentMapper contentMapper;
    private final FavoriteMapper favoriteMapper;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final boolean demoAuthEnabled;
    private final boolean cookieSecure;

    public PortalApiController(
        ContentMapper contentMapper,
        FavoriteMapper favoriteMapper,
        UserMapper userMapper,
        JwtService jwtService,
        @Value("${app.demo-auth.enabled:true}") boolean demoAuthEnabled,
        @Value("${app.jwt.cookie-secure:false}") boolean cookieSecure
    ) {
        this.contentMapper = contentMapper;
        this.favoriteMapper = favoriteMapper;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.demoAuthEnabled = demoAuthEnabled;
        this.cookieSecure = cookieSecure;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken csrfToken) {
        return Map.of("token", csrfToken.getToken());
    }

    @GetMapping("/contents")
    public List<Content> contents() {
        return contentMapper.findPublished();
    }

    @GetMapping("/contents/{id}")
    public ResponseEntity<Content> content(@PathVariable long id) {
        Content content = contentMapper.findPublishedById(id);
        return content == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(content);
    }

    @GetMapping("/auth/me")
    public Map<String, Object> authMe(Authentication authentication) {
        boolean authenticated = authentication != null
            && authentication.isAuthenticated()
            && !"anonymousUser".equals(authentication.getPrincipal());
        return Map.of(
            "authenticated", authenticated,
            "displayName", authenticated ? "로컬 데모 사용자" : "",
            "mode", authenticated ? "LOCAL_DEMO" : "ANONYMOUS"
        );
    }

    @PostMapping("/auth/demo-login")
    public ResponseEntity<Map<String, Object>> demoLogin(HttpServletResponse response) {
        if (!demoAuthEnabled) return ResponseEntity.notFound().build();

        userMapper.createDemoUserIfMissing(DEMO_KAKAO_USER_ID);
        Long userId = userMapper.findUserIdByKakaoId(DEMO_KAKAO_USER_ID);
        if (userId == null) return ResponseEntity.internalServerError().build();

        String token = jwtService.issue(userId);
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie(token, Duration.ofMinutes(30)).toString());
        return ResponseEntity.ok(Map.of(
            "authenticated", true,
            "displayName", "로컬 데모 사용자",
            "mode", "LOCAL_DEMO"
        ));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie("", Duration.ZERO).toString());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/favorites")
    public List<Content> favorites(Authentication authentication) {
        return favoriteMapper.findForUser(currentUserId(authentication));
    }

    @PostMapping("/me/favorites/{contentId}")
    public ResponseEntity<Void> addFavorite(
        @PathVariable long contentId,
        Authentication authentication
    ) {
        Content content = contentMapper.findPublishedById(contentId);
        if (content == null) return ResponseEntity.notFound().build();
        favoriteMapper.addIfPublished(currentUserId(authentication), contentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/favorites/{contentId}")
    public ResponseEntity<Void> removeFavorite(
        @PathVariable long contentId,
        Authentication authentication
    ) {
        favoriteMapper.remove(currentUserId(authentication), contentId);
        return ResponseEntity.noContent().build();
    }

    private long currentUserId(Authentication authentication) {
        return Long.parseLong(authentication.getPrincipal().toString());
    }

    private ResponseCookie jwtCookie(String value, Duration maxAge) {
        return ResponseCookie.from("portal_jwt", value)
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite("Lax")
            .path("/")
            .maxAge(maxAge)
            .build();
    }
}
