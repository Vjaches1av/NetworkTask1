import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Scanner;

public final class SimpleServer {

    public SimpleServer() {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Сервер успешно запущен!");

            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Установлено соединение: " + getIPAndPort(socket));

                    Scanner scanner = new Scanner(socket.getInputStream(), Charset.forName("cp866"));
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true, Charset.forName("cp866"));

                    createThread(socket, scanner, writer).start();
                } catch (IOException e) {
                    System.err.println("Ошибка установки соединения!");
                }
            }
        } catch (IOException e) {
            System.err.println("Не удалось запустить сервер!");
        }
    }

    private @NotNull String getIPAndPort(@NotNull Socket socket) {
        return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    private Thread createThread(Socket socket, Scanner scanner, @NotNull PrintWriter writer) {
        final String stopWord = "Пока";
        writer.println("Привет, я Сервер! Напиши мне что-нибудь. Для завершения работы - \"" + stopWord + "\".");
        return new Thread(() -> {
            while (true) {
                if (scanner.hasNext()) {
                    String input = scanner.nextLine();
                    System.out.println(getIPAndPort(socket) + ": " + input);
                    writer.println("Отправляю тебе эхо-сообщение: " + input);
                    if (stopWord.equalsIgnoreCase(input)) {
                        try {
                            socket.close();
                            System.out.println("Соединение разорвано: " + getIPAndPort(socket));
                            break;
                        } catch (IOException e) {
                            writer.println("Ты отличный собеседник. Пообщаемся еще?");
                        }
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        new SimpleServer();
    }
}
