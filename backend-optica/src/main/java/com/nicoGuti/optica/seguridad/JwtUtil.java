package com.nicoGuti.optica.seguridad;

import com.nicoGuti.optica.modelo.UsuarioAdministrador;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.time.temporal.*;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "T1t03lB@MB1N03LP@TR0nC00m1ngS00nD1OZL0sB3nd1g@";

    public String generateToken(UsuarioAdministrador usuario) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", usuario.getId());
        claims.put("username", usuario.getUsername());
        claims.put("opticaId", usuario.getOptica() != null ? usuario.getOptica().getId() : null);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getUsername())
                .setIssuer("optica-ia")
                .setIssuedAt(new Date())
                .setExpiration(calendar.getTime())
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}