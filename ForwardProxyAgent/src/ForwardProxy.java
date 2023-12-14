import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ForwardProxy {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(60000)) {
            System.out.println("Forward Proxy listening on port 60000");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ProxyHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
