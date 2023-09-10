import java.io.*;
import java.util.*;
import java.time.LocalDateTime;

public class ToyLottery {
    private static final String FILENAME = "toys.txt";
    private static final String WINNERS_FILENAME = "winners.txt";
    private List<Toy> toys;

    public ToyLottery() {
        toys = new ArrayList<>();
    }

    public void addToy(Toy toy) {
        toys.add(toy);
        saveToFile();
    }

    public void removeToy(int id) {
        toys.removeIf(toy -> toy.getId() == id);
        saveToFile();
    }

    public void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILENAME))) {
            for (Toy toy : toys) {
                writer.println(toy.getId() + "," + toy.getName() + "," + toy.getQuantity() + "," + toy.getProbability());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile() {
        toys.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    int quantity = Integer.parseInt(parts[2]);
                    double probability = Double.parseDouble(parts[3]);
                    Toy toy = new Toy(id, name, quantity, probability);
                    toys.add(toy);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Toy drawToy() {
        Random random = new Random();
        double totalProbability = toys.stream().mapToDouble(Toy::getProbability).sum();
        double randomValue = random.nextDouble() * totalProbability;

        double currentProbability = 0;
        for (Toy toy : toys) {
            currentProbability += toy.getProbability();
            if (randomValue <= currentProbability) {
                if (toy.getQuantity() > 0) {
                    toy.decreaseQuantity(); // Уменьшаем количество игрушек

                    // Записываем информацию о выигрыше вместе с отметкой времени
                    LocalDateTime currentTime = LocalDateTime.now();
                    try (PrintWriter writer = new PrintWriter(new FileWriter(WINNERS_FILENAME, true))) {
                        writer.println(toy.getName() + " (ID: " + toy.getId() + ") - " + currentTime);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    saveToFile(); // Обновляем файл с игрушками после розыгрыша
                    return toy;
                }
            }
        }

        return null; // Если не удалось выбрать игрушку
    }

    public void printToyList() {
        System.out.println("Список игрушек:");
        for (Toy toy : toys) {
            System.out.println(toy.getId() + ". " + toy.getName() + " (Количество: " + toy.getQuantity() + ", Вероятность: " + toy.getProbability() + "%)");
        }
    }

    public static void main(String[] args) {
        ToyLottery lottery = new ToyLottery();
        lottery.loadFromFile();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nМеню:");
            System.out.println("1. Розыгрыш");
            System.out.println("2. Список игрушек");
            System.out.println("3. Добавить игрушку");
            System.out.println("4. Удалить игрушку");
            System.out.println("5. Выход");
            System.out.print("Выберите действие: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    Toy winner = lottery.drawToy();
                    if (winner != null) {
                        System.out.println("Победитель: " + winner.getName());
                    } else {
                        System.out.println("Все игрушки разыграны.");
                    }
                    break;
                case 2:
                    lottery.printToyList();
                    break;
                case 3:
                    System.out.print("Введите ID новой игрушки: ");
                    int newId = scanner.nextInt();
                    scanner.nextLine(); // Чтение перевода строки после числа
                    System.out.print("Введите название новой игрушки: ");
                    String newName = scanner.nextLine();
                    System.out.print("Введите количество новой игрушки: ");
                    int newQuantity = scanner.nextInt();
                    System.out.print("Введите вероятность выпадения новой игрушки (%): ");
                    double newProbability = scanner.nextDouble();

                    Toy newToy = new Toy(newId, newName, newQuantity, newProbability);
                    lottery.addToy(newToy);
                    System.out.println("Игрушка добавлена.");
                    break;
                case 4:
                    System.out.print("Введите ID игрушки, которую хотите удалить: ");
                    int removeId = scanner.nextInt();
                    lottery.removeToy(removeId);
                    System.out.println("Игрушка удалена.");
                    break;
                case 5:
                    System.out.println("Выход из программы.");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Неверный выбор. Пожалуйста, выберите действие из меню.");
                    break;
            }
        }
    }
}