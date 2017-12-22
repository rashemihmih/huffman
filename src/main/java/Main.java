import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        Huffman huffman = new Huffman();
        int mode = 0;
        do {
            try {
                System.out.println("Имя файла:");
                String source = reader.readLine();
                String destination = StringUtils.substringBeforeLast(source, ".") + "_huffman" + "." +
                        StringUtils.substringAfterLast(source, ".");
                System.out.println("1 - сжать\n2 - распаковать\n3 - выход");
                mode = Integer.parseInt(reader.readLine());
                switch (mode) {
                    case 1:
                        huffman.encode(source, destination);
                        System.out.println("Файл сжат: " + destination);
                        break;
                    case 2:
                        huffman.decode(source, destination);
                        System.out.println("Файл распакован: " + destination);
                        break;
                    case 3:
                        break;
                    default:
                        System.out.println("Введить цифру от 1 до 3");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат числа");
            } catch (IOException e) {
                System.out.println("Ошибка ввода: " + e.getMessage());
            }
        } while (mode != 3);
    }
}
