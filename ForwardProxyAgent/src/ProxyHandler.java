import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class ProxyHandler implements Runnable {

    private Socket clientSocket;

    public ProxyHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        // Inside ProxyHandler or wherever SSL connections are initiated
        DisableSSLCertificateValidation.main(null);

    }

    private static boolean isSpecialFlag(byte[] flagBytes) {
        byte[] expectedFlag = {(byte) 0xF0, (byte) 0xF0, (byte) 0xF0, (byte) 0xF0,
                               (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE};
        return Arrays.equals(flagBytes, expectedFlag);
    }

    @Override
    public void run() {
        try (InputStream in = clientSocket.getInputStream();
             OutputStream out = clientSocket.getOutputStream()) {

            // Read the special flag from the client
            byte[] flagBytes = new byte[8];
            int bytesRead;
            int totalBytesRead = 0;

            while (totalBytesRead < 8) {
                bytesRead = in.read(flagBytes, totalBytesRead, 8 - totalBytesRead);
                if (bytesRead == -1) {
                    // Handle end of stream
                    break;
                }
                totalBytesRead += bytesRead;
            }

            if (totalBytesRead == 8 && isSpecialFlag(flagBytes)) {
                System.out.println("Received special flag: " + Arrays.toString(flagBytes));

                // Read the length of the URL
                int urlLength = in.read();

                // Read the URL
                byte[] urlBytes = new byte[urlLength];
                int bytesReadUrl = in.read(urlBytes);

                if (bytesReadUrl == urlLength) {
                    String url = new String(urlBytes);
                    System.out.println("Received request for URL: " + url);

                    // Fetch the webpage using the WebPageFetcher class
                    String webpage = WebPageFetcher.fetchWebPage(url);

                    // Send the webpage back to the client
                    out.write(webpage.getBytes());
                } else {
                    System.out.println("Failed to read the complete URL. Closing connection.");
                }
            } else {
                System.out.println("Invalid special flag received. Closing connection.");
                System.out.println("Received special flag: " + Arrays.toString(flagBytes));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
