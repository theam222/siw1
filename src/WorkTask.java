public class WorkTask extends TaskItem implements Reschedulable, Assignable {
    private String department;
    private String assignedTo;

    public WorkTask(int taskId, String title, int priority, String department, String assignedTo) {
        super(taskId, title, priority);
        this.department = "General";
        this.assignedTo = "Nobody";
        setDepartment(department);
        setAssignedTo(assignedTo);
    }

    public String getDepartment() {
        return department;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            System.out.println("!! Rejected department name");
            return;
        }
        this.department = department.trim();
    }

    public void setAssignedTo(String assignedTo) {
        if (assignedTo == null || assignedTo.trim().isEmpty()) {
            System.out.println("!! Rejected assignee name");
            return;
        }
        this.assignedTo = assignedTo.trim();
    }

    @Override
    public double calculatePriorityScore() {
        return getPriority() * 10;
    }

    @Override
    public String getCategory() {
        return "Work Task";
    }

    @Override
    public void reschedule(String newDueDate) {
        System.out.println("\"" + getTitle() + "\" rescheduled to " + newDueDate + ".");
    }

    @Override
    public void extendDeadline(int extraDays) {
        System.out.println("\"" + getTitle() + "\" deadline extended by " + extraDays + " days.");
    }

    @Override
    public void assignTo(String person) {
        setAssignedTo(person);
        System.out.println("\"" + getTitle() + "\" assigned to " + assignedTo + ".");
    }

    @Override
    public void notifyAssignee() {
        System.out.println("Notification sent to " + assignedTo + " about \"" + getTitle() + "\".");
    }

    public void escalate() {
        System.out.println("\"" + getTitle() + "\" escalated to " + department + " lead.");
    }
}
