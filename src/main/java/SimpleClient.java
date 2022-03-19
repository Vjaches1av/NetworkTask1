import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class SimpleClient {

    public SimpleClient() {
        try (Socket socket = new Socket("localhost", 8080);
             Scanner incomingMessage = new Scanner(socket.getInputStream(), Charset.forName("cp866"));
             PrintWriter outgoingMessage = new PrintWriter(socket.getOutputStream(), true, Charset.forName("cp866"));
             Scanner userInput = new Scanner(System.in, StandardCharsets.UTF_8)) {

            createThread(incomingMessage).start();

            final String stopWord = "Пока";
            while (true) {
                if (userInput.hasNext()) {
                    String line = userInput.nextLine();
                    outgoingMessage.println(line);
                    if (stopWord.equalsIgnoreCase(line)) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка установки соединения!");
        }
    }

    private Thread createThread(Scanner scanner) {
        return new Thread(() -> {
            while (true) {
                try {
                    if (scanner.hasNext()) {
                        System.out.println(scanner.nextLine());
                    }
                } catch (IllegalStateException e) {
                    break;
                }
            }
        });
    }

    public static void main(String[] args) {
        new SimpleClient();
    }
}
