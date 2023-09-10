class Toy {
    private int id;
    private String name;
    private int quantity;
    private double probability;

    public Toy(int id, String name, int quantity, double probability) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.probability = probability;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getProbability() {
        return probability;
    }

    public void decreaseQuantity() {
        if (quantity > 0) {
            quantity--;
        }
    }
}