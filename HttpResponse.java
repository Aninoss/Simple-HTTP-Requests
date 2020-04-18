import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpResponse {

    private String content;
    private final int responseCode;
    private Map<String, List<String>> headerFields;

    public HttpResponse(String content, Map<String, List<String>> headerFields, int responseCode) {
        this.content = content;
        this.headerFields = headerFields;
        this.responseCode = responseCode;
    }

    public HttpResponse(int responseCode) {
        this.responseCode = responseCode;
    }

    public Optional<String> getContent() {
        return Optional.ofNullable(content);
    }

    public Optional<Map<String, List<String>>> getHeaderFields() {
        return Optional.ofNullable(headerFields);
    }

    public Optional<List<String>> getCookies() {
        if (headerFields == null) return Optional.empty();
        return Optional.ofNullable(headerFields.get("Set-Cookie"));
    }

    public int getResponseCode() {
        return responseCode;
    }
}
