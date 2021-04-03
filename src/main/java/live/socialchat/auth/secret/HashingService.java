package live.socialchat.auth.secret;

public interface HashingService {
    String hash(String data);
    boolean matches(String data, String hash);
}
