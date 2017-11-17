package obp.services;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import obp.dao.AuthenticationResponse;
import obp.security.SerializableUser;
import obp.security.UserRoles;

@Service
public class JwtService {

    @Value("${security.oauth2.resource.jwt.key-value}")
    private String signingKey;

    public AuthenticationResponse retrieveAccessToken(Authentication auth) {
        SerializableUser user = (SerializableUser) auth.getPrincipal();
        Claims claims = Jwts.claims();

        claims.setSubject(user.getUsername());
        List<String> oldAuthorities = user.getAuthorities()
                .stream().map(authority -> authority.getAuthority())
                .collect(Collectors.toList());

        Set<String> authorities = new HashSet<>();
        for(String authority : oldAuthorities) {
            UserRoles userRole = UserRoles.valueOf(authority.toUpperCase());
            List<String> allRoles = userRole.getAllRoles()
                    .stream().map(role -> role.toString())
                    .collect(Collectors.toList());
            authorities.addAll(allRoles);
        }

        claims.put("scope", authorities);

        Calendar expiration = Calendar.getInstance();
        expiration.add(Calendar.MINUTE, 120);

        Calendar currentTime = Calendar.getInstance();

        String token = Jwts.builder()
                .setClaims(claims)
                .setHeaderParam("typ", "JWT")
                .setIssuer(user.getClient_secret())
                .setIssuedAt(currentTime.getTime())
                .setExpiration(expiration.getTime())
                .signWith(SignatureAlgorithm.HS256, signingKey.getBytes())
                .compact();

        AuthenticationResponse response = new AuthenticationResponse(token);
        response.setExpires_in(120 * 60);
        response.setToken_type("jwt");

        return response;
    }
}
