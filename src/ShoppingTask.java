public class ShoppingTask extends TaskItem implements Assignable {
    private String store;
    private int itemCount;

    public ShoppingTask(int taskId, String title, int priority, String store, int itemCount) {
        super(taskId, title, priority);
        this.store = "Any store";
        this.itemCount = 1;
        setStore(store);
        setItemCount(itemCount);
    }

    public String getStore() {
        return store;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setStore(String store) {
        if (store == null || store.trim().isEmpty()) {
            System.out.println("!! Rejected store name");
            return;
        }
        this.store = store.trim();
    }

    public void setItemCount(int itemCount) {
        if (itemCount < 1) {
            System.out.println("!! Rejected item count " + itemCount);
            return;
        }
        this.itemCount = itemCount;
    }

    @Override
    public double calculatePriorityScore() {
        return getPriority() * 2 + itemCount;
    }

    @Override
    public String getCategory() {
        return "Shopping Task";
    }

    @Override
    public void assignTo(String person) {
        System.out.println("\"" + getTitle() + "\" assigned to " + person + " for pickup at " + store + ".");
    }

    @Override
    public void notifyAssignee() {
        System.out.println("Reminder sent: buy " + itemCount + " item(s) at " + store + ".");
    }

    public void addItem() {
        itemCount++;
        System.out.println("Item added to \"" + getTitle() + "\". Total items: " + itemCount);
    }
}
