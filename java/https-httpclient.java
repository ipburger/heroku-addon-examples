import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class ProxyExample
{
    public static void main(String[] args) throws Exception {
        String proxyUrl = System.getenv("IPB_HTTP");

        String[] proxyValues = proxyUrl.split("[/(:\\/@)/]+");
        String proxyUser = proxyValues[1];
        String proxyPassword = proxyValues[2];
        String proxyHost = proxyValues[3];
        int proxyPort = Integer.parseInt(proxyValues[4]);

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(proxyHost, proxyPort),
                new UsernamePasswordCredentials(proxyUser, proxyPassword));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider).build();
        try {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            RequestConfig config = RequestConfig.custom()
                .setProxy(proxy)
                .build();

            HttpHost target = new HttpHost("www.example.com", 80, "http");
            HttpGet httpget = new HttpGet("/");
            httpget.setConfig(config);

            CloseableHttpResponse response = httpclient.execute(target, httpget);
            try {
                System.out.println(EntityUtils.toString(response.getEntity()));
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }
}
