import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final TaskService service = new TaskService();

    public static void main(String[] args) {
        fillTestData();
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("======= TASK MANAGER =======");
            System.out.println("1 - Manager (create and manage tasks)");
            System.out.println("2 - Worker (process the queue)");
            System.out.println("3 - Reports and search");
            System.out.println("0 - Exit");
            int choice = readInt("Your choice: ");
            switch (choice) {
                case 1 -> managerMenu();
                case 2 -> workerMenu();
                case 3 -> reportsMenu();
                case 0 -> running = false;
                default -> System.out.println("No such menu item");
            }
        }
        System.out.println("Bye!");
    }

    private static void fillTestData() {
        service.addTask(new WorkTask(service.nextId(), "Prepare quarterly report", 5, "Finance", "Amir Salamov"));
        service.addTask(new PersonalTask(service.nextId(), "Book dentist appointment", 3, "Clinic", true));
        service.addTask(new ShoppingTask(service.nextId(), "Buy groceries", 2, "Frunze Market", 8));
        service.addTask(new WorkTask(service.nextId(), "Fix login bug", 4, "Development", "Aigerim T."));
        service.createProject("Website Launch");
        service.addTaskToProject("Website Launch", 1);
        service.addTaskToProject("Website Launch", 4);
    }

    private static void managerMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Manager ---");
            System.out.println("1  - Add task");
            System.out.println("2  - Update task (title, priority)");
            System.out.println("3  - Remove task");
            System.out.println("4  - Send task to the queue");
            System.out.println("5  - Create project");
            System.out.println("6  - Add task to project");
            System.out.println("7  - Remove task from project");
            System.out.println("8  - Remove project");
            System.out.println("9  - Assign task");
            System.out.println("10 - Reschedule task");
            System.out.println("11 - Add comment");
            System.out.println("0  - Back");
            int choice = readInt("Your choice: ");
            try {
                switch (choice) {
                    case 1 -> addTask();
                    case 2 -> updateTask();
                    case 3 -> removeTask();
                    case 4 -> sendToQueue();
                    case 5 -> createProject();
                    case 6 -> addToProject();
                    case 7 -> removeFromProject();
                    case 8 -> removeProject();
                    case 9 -> assignTask();
                    case 10 -> rescheduleTask();
                    case 11 -> addComment();
                    case 0 -> back = true;
                    default -> System.out.println("No such menu item");
                }
            } catch (NotFoundException | DuplicateException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void workerMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Worker ---");
            System.out.println("1 - Show waiting queue");
            System.out.println("2 - Take next task and complete it");
            System.out.println("3 - Show completed tasks");
            System.out.println("4 - Add comment");
            System.out.println("5 - Show comments of a task");
            System.out.println("0 - Back");
            int choice = readInt("Your choice: ");
            try {
                switch (choice) {
                    case 1 -> printList(service.getQueueSnapshot());
                    case 2 -> takeNext();
                    case 3 -> printList(service.getDoneLog());
                    case 4 -> addComment();
                    case 5 -> showComments();
                    case 0 -> back = true;
                    default -> System.out.println("No such menu item");
                }
            } catch (NotFoundException | DuplicateException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void reportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("--- Reports and search ---");
            System.out.println("1 - All tasks (choose sorting)");
            System.out.println("2 - Find task by id");
            System.out.println("3 - Tasks by category");
            System.out.println("4 - Tasks with priority at least ...");
            System.out.println("5 - Search by title");
            System.out.println("6 - Projects");
            System.out.println("7 - Projects of one task");
            System.out.println("8 - Summary report");
            System.out.println("0 - Back");
            int choice = readInt("Your choice: ");
            try {
                switch (choice) {
                    case 1 -> showAllTasks();
                    case 2 -> findTask();
                    case 3 -> tasksByCategory();
                    case 4 -> tasksByPriority();
                    case 5 -> searchByTitle();
                    case 6 -> showProjects();
                    case 7 -> projectsOfTask();
                    case 8 -> showSummary();
                    case 0 -> back = true;
                    default -> System.out.println("No such menu item");
                }
            } catch (NotFoundException | DuplicateException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void addTask() {
        System.out.println("1 - Work, 2 - Personal, 3 - Shopping");
        int type = readInt("Type: ");
        if (type < 1 || type > 3) {
            System.out.println("No such type");
            return;
        }
        String title = readLine("Title: ");
        int priority = readInt("Priority (1-5): ");
        TaskItem task;
        if (type == 1) {
            String department = readLine("Department: ");
            String assignedTo = readLine("Assigned to: ");
            task = new WorkTask(service.nextId(), title, priority, department, assignedTo);
        } else if (type == 2) {
            String location = readLine("Location: ");
            boolean reminder = readLine("Reminder (y/n): ").trim().equalsIgnoreCase("y");
            task = new PersonalTask(service.nextId(), title, priority, location, reminder);
        } else {
            String store = readLine("Store: ");
            int items = readInt("Number of items: ");
            task = new ShoppingTask(service.nextId(), title, priority, store, items);
        }
        service.addTask(task);
        System.out.println("Added: " + task);
    }

    private static void updateTask() {
        int id = readInt("Task id: ");
        service.findTask(id);
        String title = readLine("New title: ");
        int priority = readInt("New priority (1-5): ");
        service.updateTask(id, title, priority);
        System.out.println("Now: " + service.findTask(id));
    }

    private static void removeTask() {
        int id = readInt("Task id: ");
        service.removeTask(id);
        System.out.println("Task removed everywhere");
    }

    private static void sendToQueue() {
        int id = readInt("Task id: ");
        service.submitToQueue(id);
        System.out.println("Task is in the queue. Waiting: " + service.waitingCount());
    }

    private static void createProject() {
        String name = readLine("Project name: ");
        Project project = service.createProject(name);
        System.out.println("Created: " + project);
    }

    private static void addToProject() {
        String name = readLine("Project name: ");
        int id = readInt("Task id: ");
        service.addTaskToProject(name, id);
    }

    private static void removeFromProject() {
        String name = readLine("Project name: ");
        int id = readInt("Task id: ");
        service.removeTaskFromProject(name, id);
    }

    private static void removeProject() {
        String name = readLine("Project name: ");
        service.removeProject(name);
        System.out.println("Project removed, its tasks are still alive");
    }

    private static void assignTask() {
        int id = readInt("Task id: ");
        TaskItem task = service.findTask(id);
        if (task instanceof Assignable) {
            String person = readLine("Person: ");
            ((Assignable) task).assignTo(person);
            ((Assignable) task).notifyAssignee();
        } else {
            System.out.println("This type of task can't be assigned");
        }
    }

    private static void rescheduleTask() {
        int id = readInt("Task id: ");
        TaskItem task = service.findTask(id);
        if (task instanceof Reschedulable) {
            String date = readLine("New date: ");
            ((Reschedulable) task).reschedule(date);
        } else {
            System.out.println("This type of task can't be rescheduled");
        }
    }

    private static void addComment() {
        int id = readInt("Task id: ");
        TaskItem task = service.findTask(id);
        String author = readLine("Author: ");
        String text = readLine("Comment: ");
        task.addComment(author, text);
    }

    private static void showComments() {
        int id = readInt("Task id: ");
        service.findTask(id).showComments();
    }

    private static void takeNext() {
        TaskItem task = service.takeNext();
        if (task == null) {
            System.out.println("Queue is empty");
        } else {
            System.out.println("Done: " + task);
        }
    }

    private static void showAllTasks() {
        System.out.println("1 - by id (natural order)");
        System.out.println("2 - by title");
        System.out.println("3 - by priority");
        System.out.println("4 - by score");
        System.out.println("5 - by category, then score");
        int sort = readInt("Sort: ");
        switch (sort) {
            case 1 -> printList(service.getAllNaturalOrder());
            case 2 -> printList(service.getAllSorted(TaskComparators.BY_TITLE));
            case 3 -> printList(service.getAllSorted(TaskComparators.BY_PRIORITY_DESC));
            case 4 -> printList(service.getAllSorted(TaskComparators.BY_SCORE_DESC));
            case 5 -> printList(service.getAllSorted(TaskComparators.BY_CATEGORY_THEN_SCORE));
            default -> System.out.println("No such sort");
        }
    }

    private static void findTask() {
        int id = readInt("Task id: ");
        service.findTask(id).displayInfo();
    }

    private static void tasksByCategory() {
        System.out.println("1 - Work Task, 2 - Personal Task, 3 - Shopping Task");
        int c = readInt("Category: ");
        switch (c) {
            case 1 -> printList(service.getByCategory("Work Task"));
            case 2 -> printList(service.getByCategory("Personal Task"));
            case 3 -> printList(service.getByCategory("Shopping Task"));
            default -> System.out.println("No such category");
        }
    }

    private static void tasksByPriority() {
        int min = readInt("Minimum priority (1-5): ");
        printList(service.getWithMinPriority(min));
    }

    private static void searchByTitle() {
        String text = readLine("Text: ");
        printList(service.searchByTitle(text));
    }

    private static void showProjects() {
        List<Project> projects = service.getProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects yet");
            return;
        }
        for (Project project : projects) {
            project.showTasks();
        }
    }

    private static void projectsOfTask() {
        int id = readInt("Task id: ");
        printList(service.getProjectsOfTask(id));
    }

    private static void showSummary() {
        System.out.println("Tasks total      : " + service.taskCount());
        System.out.println("Completed        : " + service.completedCount());
        System.out.println("Waiting in queue : " + service.waitingCount());
        System.out.println("Projects         : " + service.getProjects().size());
        System.out.printf("Average priority : %.2f%n", service.averagePriority());
        System.out.println("Total score      : " + service.totalScore());
        System.out.println("Tasks by category:");
        for (Map.Entry<String, Integer> entry : service.countByCategory().entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }

    private static <T> void printList(List<T> list) {
        if (list.isEmpty()) {
            System.out.println("Nothing to show");
            return;
        }
        for (T item : list) {
            System.out.println(item);
        }
    }

    private static String readLine(String text) {
        System.out.print(text);
        if (!scanner.hasNextLine()) {
            System.exit(0);
        }
        return scanner.nextLine();
    }

    private static int readInt(String text) {
        while (true) {
            String line = readLine(text);
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number");
            }
        }
    }
}
