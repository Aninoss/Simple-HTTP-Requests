import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class HttpRequest {

    private static final String USER_AGENT = "Java Application";

    public static CompletableFuture<HttpResponse> getData(String urlString, HttpHeader... headers) {
        return getData(urlString, "GET", null, headers);
    }

    public static CompletableFuture<HttpResponse> getData(String urlString, String body, HttpHeader... headers) {
        return getData(urlString, "POST", body, headers);
    }

    public static CompletableFuture<HttpResponse> getData(String urlString, String method, String body, HttpHeader... headers) {
        CompletableFuture<HttpResponse> future = new CompletableFuture<>();
        Thread t = new Thread(() -> download(future, urlString, method, body, headers), "download_url");
        t.start();
        return future;
    }

    private static void download(CompletableFuture<HttpResponse> future, String urlString, String method, String body, HttpHeader... headers) {
        try {
            BufferedReader br;
            String line;
            StringBuilder text = new StringBuilder();
            HttpsURLConnection connection = (HttpsURLConnection) (new URL(urlString)).openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setRequestMethod(method);

            connection.setRequestProperty("User-Agent", USER_AGENT);
            for (HttpHeader property : headers) {
                connection.setRequestProperty(property.getKey(), property.getValue());
            }

            boolean hasBody = body != null && body.length() > 0;
            connection.setDoOutput(hasBody);

            if (hasBody) {
                byte[] out = body.getBytes(StandardCharsets.UTF_8);
                int length = out.length;
                connection.setFixedLengthStreamingMode(length);

                connection.connect();
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(out);
                }
            } else connection.connect();

            int code = connection.getResponseCode();
            if (code / 100 != 2) {
                future.complete(new HttpResponse(code));
                return;
            }

            InputStream in = connection.getInputStream();
            br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
                text.append(line).append("\n");
            }

            br.close();
            in.close();

            future.complete(new HttpResponse(text.toString(), connection.getHeaderFields(), code));
        } catch (Throwable e) {
            future.completeExceptionally(e);
        }
    }

}
