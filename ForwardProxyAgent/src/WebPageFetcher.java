import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.stream.Collectors;

public class WebPageFetcher {
    public static String fetchWebPage(String url) {
        try {
            // Construct URI using the standard URI class
            URI webpageURI = new URI(url);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) webpageURI.toURL().openConnection();
            int responseCode = connection.getResponseCode();

            // Check for redirection (HTTP status code 3xx)
            if (responseCode >= 300 && responseCode < 400) {
                String newLocation = connection.getHeaderField("Location");
                connection.disconnect(); // Disconnect current connection

                // Follow the redirect by recursively calling fetchWebPage with the new location
                return fetchWebPage(newLocation);
            }

            // Read the content of the webpage
            try (InputStream inputStream = connection.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                String webpageContent = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                return webpageContent;
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            // Return an error message or handle the exception as needed
            return "Error fetching webpage";
        }
    }
}
