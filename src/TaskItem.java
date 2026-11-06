import java.util.ArrayList;
import java.util.List;

public abstract class TaskItem implements Comparable<TaskItem> {
    private int taskId;
    private String title;
    private int priority;
    private boolean completed;
    private List<Comment> comments;

    public TaskItem(int taskId, String title, int priority) {
        this.taskId = 1;
        this.title = "Untitled task";
        this.priority = 1;
        this.completed = false;
        this.comments = new ArrayList<>();
        setTaskId(taskId);
        setTitle(title);
        setPriority(priority);
    }

    public int getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getCommentCount() {
        return comments.size();
    }

    public void setTaskId(int taskId) {
        if (taskId < 1) {
            System.out.println("!! Rejected taskId " + taskId);
            return;
        }
        this.taskId = taskId;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().length() < 2) {
            System.out.println("!! Rejected title");
            return;
        }
        this.title = title.trim();
    }

    public void setPriority(int priority) {
        if (priority < 1 || priority > 5) {
            System.out.println("!! Rejected priority " + priority + " (allowed 1-5)");
            return;
        }
        this.priority = priority;
    }

    public void markCompleted() {
        completed = true;
        System.out.println("\"" + title + "\" marked as completed.");
    }

    public void addComment(String author, String text) {
        Comment comment = new Comment(author, text);
        if (!comment.isValid()) {
            System.out.println("!! Comment was not added to \"" + title + "\"");
            return;
        }
        comments.add(comment);
        System.out.println("Comment added to \"" + title + "\" by " + comment.getAuthor() + ".");
    }

    public void showComments() {
        System.out.println("Comments of \"" + title + "\" (" + comments.size() + "):");
        for (Comment comment : comments) {
            comment.display();
        }
    }

    public abstract double calculatePriorityScore();

    public abstract String getCategory();

    public void displayInfo() {
        System.out.println("Task ID       : " + taskId);
        System.out.println("Title         : " + title);
        System.out.println("Priority      : " + priority);
        System.out.println("Completed     : " + (completed ? "Yes" : "No"));
        System.out.println("Category      : " + getCategory());
        System.out.println("Priority score: " + calculatePriorityScore());
    }

    @Override
    public int compareTo(TaskItem other) {
        return Integer.compare(taskId, other.taskId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskItem)) return false;
        TaskItem other = (TaskItem) o;
        return taskId == other.taskId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(taskId);
    }

    @Override
    public String toString() {
        return String.format("#%-3d %-28s %-14s priority=%d score=%-5.1f %s",
                taskId, title, "[" + getCategory() + "]", priority,
                calculatePriorityScore(), completed ? "DONE" : "open");
    }
}
