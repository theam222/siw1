import java.util.LinkedHashSet;
import java.util.Set;

public class Project {
    private String name;
    private Set<TaskItem> tasks;

    public Project(String name) {
        this.name = "Unnamed project";
        this.tasks = new LinkedHashSet<>();
        setName(name);
    }

    public String getName() {
        return name;
    }

    public Set<TaskItem> getTasks() {
        return tasks;
    }

    public void setName(String name) {
        if (name == null || name.trim().length() < 2) {
            System.out.println("!! Rejected project name");
            return;
        }
        this.name = name.trim();
    }

    public void addTask(TaskItem task) {
        if (task == null) {
            System.out.println("!! Cannot add empty task");
            return;
        }
        if (tasks.add(task)) {
            System.out.println("Task \"" + task.getTitle() + "\" added to project \"" + name + "\".");
        }
    }

    public void removeTask(TaskItem task) {
        if (tasks.remove(task)) {
            System.out.println("Task \"" + task.getTitle() + "\" removed from project \"" + name + "\".");
        }
    }

    public int countCompleted() {
        int count = 0;
        for (TaskItem task : tasks) {
            if (task.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    public void showTasks() {
        System.out.println("Project \"" + name + "\" has " + tasks.size() + " task(s):");
        for (TaskItem task : tasks) {
            System.out.println(" * " + task);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project other = (Project) o;
        return name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return name + " (" + tasks.size() + " tasks)";
    }
}
