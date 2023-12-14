import java.io.*;
import java.net.Socket;

public class ProxyClient {

    public static void main(String[] args) {
        try (Socket socket = new Socket("127.0.0.1", 60000);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {

            // Construct a special command string with a valid URL
            String url = "http://www.brockport.edu";
            
            // Convert the length to a byte array
            byte[] lengthBytes = {(byte) url.length()};

            // Write the special flag, length, and URL to the output stream
            out.write(new byte[]{(byte) 0xF0, (byte) 0xF0, (byte) 0xF0, (byte) 0xF0,
                    (byte) 0xFE, (byte) 0xFE, (byte) 0xFE, (byte) 0xFE});
            out.write(lengthBytes);
            out.write(url.getBytes());

            // Receive and print the response from the forward proxy agent
            byte[] buffer = new byte[1024];
            int bytesRead;
            StringBuilder responseBuilder = new StringBuilder();

            while ((bytesRead = in.read(buffer)) != -1) {
                responseBuilder.append(new String(buffer, 0, bytesRead));
            }

            System.out.println("Received response from server:\n" + responseBuilder.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
