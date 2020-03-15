import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import okhttp3.OkHttpClient;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

public class ProxyExample
{
    public static void main(String[] args) throws Exception {
        String proxyUrl = System.getenv("IPB_HTTP");
        String[] proxyValues = proxyUrl.split("[/(:\\/@)/]+");
        String proxyUser = proxyValues[1];
        String proxyPassword = proxyValues[2];
        String proxyHost = proxyValues[3];
        int proxyPort = Integer.parseInt(proxyValues[4]);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        Authenticator proxyAuthenticator = new Authenticator() {
          @Override public Request authenticate(Route route, Response response) throws IOException {
               String credential = Credentials.basic(proxyUser, proxyPassword);
               return response.request().newBuilder()
                   .header("Proxy-Authorization", credential)
                   .build();
          }
        };
        clientBuilder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort)))
        .proxyAuthenticator(proxyAuthenticator);

        OkHttpClient client = clientBuilder.build();
        Request request = new Request.Builder().url("http://www.example.com").build();
        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
    }
}