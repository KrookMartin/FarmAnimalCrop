import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Entity implements Serializable {
    private int id;

    public Entity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return "ID: " + id;
    }
}

class Animal extends Entity implements Serializable{
    // Animal och Crop utökar Entity och implementerar Serializable

    private String name;

    public Animal(int id, String name) {
        super(id);
        this.name = name;
    }

    public String getDescription() {
        return super.getDescription() + ", Name: " + name;
    }

    public void feed(String cropType) {
        System.out.println(name + " has been fed with " + cropType);
    }
}

class Crop extends Entity implements Serializable{
    private String name;
    private int quantity;

    public Crop(int id, String name) {
        super(id);
        this.name = name;
        this.quantity = 0;
    }

    public String getDescription() {
        return super.getDescription() + ", Name: " + name + ", Quantity: " + quantity;
    }

    public void addQuantity(int amount) {
        quantity += amount;
    }

    public boolean takeQuantity(int amount) {
        if (quantity >= amount) {
            quantity -= amount;
            return true;
        }
        return false;
    }
}

class Farm implements Serializable {
    private List<Animal> animals;
    private List<Crop> crops;

    public Farm() {
        animals = new ArrayList<>();
        crops = new ArrayList<>();

        initializeFarm();
    }

    private void initializeFarm() {
        // Lägger till några förbestämda djur och grödor
        animals.add(new Animal(1, "Cow"));
        animals.add(new Animal(2, "Chicken"));


        crops.add(new Crop(1, "Wheat"));
        crops.add(new Crop(2, "Corn"));
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public void addCrop(Crop crop) {
        crops.add(crop);
    }


    public void viewAnimals() {
        System.out.println("Animals on the farm:");
        for (Animal animal : animals) {
            System.out.println(animal.getDescription());
        }
    }

    public void viewCrops() {
        System.out.println("Crops on the farm:");
        for (Crop crop : crops) {
            System.out.println(crop.getDescription());
        }
    }

    public void addCropQuantity(int id, int amount) {
        for (Crop crop : crops) {
            if (crop.getId() == id) {
                crop.addQuantity(amount);
                System.out.println("Crop quantity increased successfully!");
                return;
            }
        }
        System.out.println("Crop with ID " + id + " not found.");
    }

    public void removeAnimal(int id) {
        for (Animal animal : animals) {
            if (animal.getId() == id) {
                animals.remove(animal);
                System.out.println("Animal with ID " + id + " removed successfully!");
                return;
            }
        }
        System.out.println("Animal with ID " + id + " not found.");
    }

    public void removeCrop(int id) {
        for (Crop crop : crops) {
            if (crop.getId() == id) {
                crops.remove(crop);
                System.out.println("Crop with ID " + id + " removed successfully!");
                return;
            }
        }
        System.out.println("Crop with ID " + id + " not found.");
    }

    public void saveToFile() {
        try (ObjectOutputStream animalStream = new ObjectOutputStream(new FileOutputStream("animals.ser"));
             ObjectOutputStream cropStream = new ObjectOutputStream(new FileOutputStream("crops.ser"))) {

            animalStream.writeObject(animals);
            cropStream.writeObject(crops);

            System.out.println("Farm data saved to files.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile() {
        try (ObjectInputStream animalStream = new ObjectInputStream(new FileInputStream("animals.ser"));
             ObjectInputStream cropStream = new ObjectInputStream(new FileInputStream("crops.ser"))) {

            animals = (List<Animal>) animalStream.readObject();
            crops = (List<Crop>) cropStream.readObject();

            System.out.println("Farm data loaded from files.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No previous farm data found. Creating a new farm.");
            // Initialisera gården om det inte finns några tidigare data.
            if (animals.isEmpty() && crops.isEmpty()) {
                initializeFarm();
            }
        }
    }

    public void feedAnimal(Scanner scanner) {
        System.out.println("Select a Crop to feed:");
        viewCrops();
        int cropId = scanner.nextInt();

        System.out.println("Select an Animal to feed:");
        viewAnimals();
        int animalId = scanner.nextInt();

        for (Crop crop : crops) {
            if (crop.getId() == cropId) {
                for (Animal animal : animals) {
                    if (animal.getId() == animalId) {
                        if (crop.takeQuantity(1)) {
                            animal.feed(crop.getDescription());
                            return;
                        } else {
                            System.out.println("Not enough quantity of " + crop.getDescription() + " to feed.");
                            return;
                        }
                    }
                }
            }
        }

        System.out.println("Invalid Crop or Animal ID.");
    }
}

public class Main {
    public static void main(String[] args) {
        Farm farm = new Farm();
        Scanner scanner = new Scanner(System.in);

        farm.loadFromFile();

        while (true) {
            System.out.println("Main Menu:");
            System.out.println("1. View Animals");
            System.out.println("2. View Crops");
            System.out.println("3. Add Animal");
            System.out.println("4. Add Crop");
            System.out.println("5. Add Crop Quantity");
            System.out.println("6. Remove Animal");
            System.out.println("7. Remove Crop");
            System.out.println("8. Feed Animal");
            System.out.println("9. Save to File");
            System.out.println("10. Exit");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    farm.viewAnimals();
                    break;
                case 2:
                    farm.viewCrops();
                    break;
                case 3:
                    addAnimal(scanner, farm);
                    break;
                case 4:
                    addCrop(scanner, farm);
                    break;
                case 5:
                    addCropQuantity(scanner, farm);
                    break;
                case 6:
                    removeAnimal(scanner, farm);
                    break;
                case 7:
                    removeCrop(scanner, farm);
                    break;
                case 8:
                    farm.feedAnimal(scanner);
                    break;
                case 9:
                    farm.saveToFile();
                    break;
                case 10:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please choose a number from 1 to 10.");
            }
        }
    }
    private static void addAnimal(Scanner scanner, Farm farm) {
        System.out.println("Current Animals on the farm:");
        farm.viewAnimals();

        System.out.println("Enter Animal ID:");
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid integer for Animal ID:");
            scanner.next();
        }
        int id = scanner.nextInt();

        System.out.println("Enter Animal Name:");
        String name = scanner.next();

        Animal newAnimal = new Animal(id, name);
        farm.addAnimal(newAnimal);

        System.out.println("Animal added successfully!");
    }

    private static void addCrop(Scanner scanner, Farm farm) {
        System.out.println("Current Crops in the farm storage:");
        farm.viewCrops();

        System.out.println("Enter Crop ID:");
        int id = scanner.nextInt();
        System.out.println("Enter Crop Name:");
        String name = scanner.next();

        Crop newCrop = new Crop(id, name);
        farm.addCrop(newCrop);

        System.out.println("Crop added successfully!");
    }

    private static void addCropQuantity(Scanner scanner, Farm farm) {
        System.out.println("Current Crops in the farm storage:");
        farm.viewCrops();
        System.out.println("Enter Crop ID:");
        int id = scanner.nextInt();
        System.out.println("Enter Quantity to Add:");
        int quantity = scanner.nextInt();

        farm.addCropQuantity(id, quantity);
    }

    private static void removeAnimal(Scanner scanner, Farm farm) {

        System.out.println("Current Animals on the farm:");
        farm.viewAnimals();

        System.out.println("Enter Animal ID to remove:");
        int id = scanner.nextInt();

        farm.removeAnimal(id);
    }

    private static void removeCrop(Scanner scanner, Farm farm) {
        System.out.println("Current Crops in the farm storage:");
        farm.viewCrops();

        System.out.println("Enter Crop ID to remove:");
        int id = scanner.nextInt();
        farm.removeCrop(id);
    }
}
