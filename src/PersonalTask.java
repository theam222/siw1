public class PersonalTask extends TaskItem implements Reschedulable {
    private String location;
    private boolean reminderEnabled;

    public PersonalTask(int taskId, String title, int priority, String location, boolean reminderEnabled) {
        super(taskId, title, priority);
        this.location = "Home";
        setLocation(location);
        this.reminderEnabled = reminderEnabled;
    }

    public String getLocation() {
        return location;
    }

    public boolean isReminderEnabled() {
        return reminderEnabled;
    }

    public void setLocation(String location) {
        if (location == null || location.trim().isEmpty()) {
            System.out.println("!! Rejected location");
            return;
        }
        this.location = location.trim();
    }

    public void setReminderEnabled(boolean reminderEnabled) {
        this.reminderEnabled = reminderEnabled;
    }

    @Override
    public double calculatePriorityScore() {
        return getPriority() * 5;
    }

    @Override
    public String getCategory() {
        return "Personal Task";
    }

    @Override
    public void reschedule(String newDueDate) {
        System.out.println("\"" + getTitle() + "\" moved to " + newDueDate + ".");
    }

    @Override
    public void extendDeadline(int extraDays) {
        System.out.println("\"" + getTitle() + "\" pushed back by " + extraDays + " days.");
    }

    public void sendReminder() {
        if (reminderEnabled) {
            System.out.println("Reminder: \"" + getTitle() + "\" at " + location + ".");
        } else {
            System.out.println("Reminders are off for \"" + getTitle() + "\".");
        }
    }
}
