package com.example.auth;

import com.example.user.domain.User;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Minimal HS256 JWT issuer/verifier (no external deps).
 *
 * <p>NOTE: This is intentionally small for demo purposes. In production, consider using a battle
 * tested JWT library + proper key management/rotation.
 */
@Service
public class JwtService {
  private static final Base64.Encoder B64_URL = Base64.getUrlEncoder().withoutPadding();
  private static final Base64.Decoder B64_URL_DEC = Base64.getUrlDecoder();

  private final String secret;
  private final String issuer;
  private final long ttlSeconds;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.issuer:blog-java-main}") String issuer,
      @Value("${app.jwt.ttl-seconds:3600}") long ttlSeconds) {
    this.secret = secret;
    this.issuer = issuer;
    this.ttlSeconds = ttlSeconds;
  }

  public String issue(User user) {
    var now = Instant.now().getEpochSecond();
    var exp = now + ttlSeconds;

    var headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    var payloadJson =
        "{"
            + "\"iss\":\""
            + escapeJson(issuer)
            + "\","
            + "\"sub\":\""
            + user.id()
            + "\","
            + "\"username\":\""
            + escapeJson(user.username())
            + "\","
            + "\"email\":\""
            + escapeJson(user.email())
            + "\","
            + "\"iat\":"
            + now
            + ","
            + "\"exp\":"
            + exp
            + "}";

    var header = b64Url(headerJson.getBytes(StandardCharsets.UTF_8));
    var payload = b64Url(payloadJson.getBytes(StandardCharsets.UTF_8));
    var signingInput = header + "." + payload;
    var sig = hmacSha256(signingInput, secret);

    return signingInput + "." + sig;
  }

  /** Verifies signature + exp and returns user id (sub). */
  public long verifyAndGetUserId(String jwt) {
    if (jwt == null || jwt.isBlank()) {
      throw new IllegalArgumentException("missing token");
    }
    var parts = jwt.split("\\.");
    if (parts.length != 3) {
      throw new IllegalArgumentException("invalid token format");
    }

    var signingInput = parts[0] + "." + parts[1];
    var expected = hmacSha256(signingInput, secret);
    if (!constantTimeEquals(expected, parts[2])) {
      throw new IllegalArgumentException("invalid token signature");
    }

    var payloadJson = new String(B64_URL_DEC.decode(parts[1]), StandardCharsets.UTF_8);
    var exp = readLongClaim(payloadJson, "exp");
    var now = Instant.now().getEpochSecond();
    if (exp <= now) {
      throw new IllegalArgumentException("token expired");
    }

    var sub = readStringClaim(payloadJson, "sub");
    try {
      return Long.parseLong(sub);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("invalid token sub");
    }
  }

  private static String b64Url(byte[] bytes) {
    return B64_URL.encodeToString(bytes);
  }

  private static String hmacSha256(String signingInput, String secret) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      byte[] sig = mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8));
      return b64Url(sig);
    } catch (Exception e) {
      throw new IllegalStateException("jwt signing failed", e);
    }
  }

  private static boolean constantTimeEquals(String a, String b) {
    if (a == null || b == null) return false;
    if (a.length() != b.length()) return false;
    int r = 0;
    for (int i = 0; i < a.length(); i++) {
      r |= a.charAt(i) ^ b.charAt(i);
    }
    return r == 0;
  }

  // Tiny JSON helpers (assumes flat JSON with quoted string values and numeric values).
  private static String escapeJson(String s) {
    if (s == null) return "";
    return s.replace("\\", "\\\\").replace("\"", "\\\"");
  }

  private static String readStringClaim(String json, String key) {
    var needle = "\"" + key + "\":\"";
    var idx = json.indexOf(needle);
    if (idx < 0) throw new IllegalArgumentException("missing claim: " + key);
    var start = idx + needle.length();
    var end = json.indexOf('"', start);
    if (end < 0) throw new IllegalArgumentException("invalid claim: " + key);
    return json.substring(start, end);
  }

  private static long readLongClaim(String json, String key) {
    var needle = "\"" + key + "\":";
    var idx = json.indexOf(needle);
    if (idx < 0) throw new IllegalArgumentException("missing claim: " + key);
    var start = idx + needle.length();
    int end = start;
    while (end < json.length()) {
      char c = json.charAt(end);
      if (c < '0' || c > '9') break;
      end++;
    }
    if (end == start) throw new IllegalArgumentException("invalid claim: " + key);
    return Long.parseLong(json.substring(start, end));
  }
}

