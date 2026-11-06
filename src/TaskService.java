import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

public class TaskService {
    private final Set<TaskItem> tasks = new LinkedHashSet<>();
    private final Map<Integer, TaskItem> taskById = new HashMap<>();
    private final Map<String, List<TaskItem>> tasksByCategory = new TreeMap<>();
    private final Map<String, Project> projectsByName = new LinkedHashMap<>();
    private final Map<Integer, Set<Project>> projectsOfTask = new HashMap<>();
    private final Queue<TaskItem> waitingQueue = new PriorityQueue<>(TaskComparators.QUEUE_ORDER);
    private final Queue<TaskItem> doneLog = new ArrayDeque<>();
    private int nextId = 1;

    public int nextId() {
        return nextId++;
    }

    public void addTask(TaskItem task) {
        if (taskById.containsKey(task.getTaskId())) {
            throw new DuplicateException("Task with id " + task.getTaskId() + " already exists");
        }
        tasks.add(task);
        taskById.put(task.getTaskId(), task);
        tasksByCategory.computeIfAbsent(task.getCategory(), k -> new ArrayList<>()).add(task);
    }

    public TaskItem findTask(int id) {
        TaskItem task = taskById.get(id);
        if (task == null) {
            throw new NotFoundException("Task with id " + id + " not found");
        }
        return task;
    }

    public void updateTask(int id, String newTitle, int newPriority) {
        TaskItem task = findTask(id);
        boolean wasWaiting = waitingQueue.remove(task);
        task.setTitle(newTitle);
        task.setPriority(newPriority);
        if (wasWaiting) {
            waitingQueue.offer(task);
        }
    }

    public void removeTask(int id) {
        TaskItem task = findTask(id);
        tasks.remove(task);
        taskById.remove(id);
        List<TaskItem> group = tasksByCategory.get(task.getCategory());
        if (group != null) {
            group.remove(task);
            if (group.isEmpty()) {
                tasksByCategory.remove(task.getCategory());
            }
        }
        Set<Project> projects = projectsOfTask.remove(id);
        if (projects != null) {
            for (Project project : projects) {
                project.getTasks().remove(task);
            }
        }
        waitingQueue.remove(task);
        doneLog.remove(task);
    }

    public Project createProject(String name) {
        if (name == null || name.trim().length() < 2) {
            throw new IllegalArgumentException("Project name is too short");
        }
        String key = name.trim().toLowerCase();
        if (projectsByName.containsKey(key)) {
            throw new DuplicateException("Project \"" + name.trim() + "\" already exists");
        }
        Project project = new Project(name);
        projectsByName.put(key, project);
        return project;
    }

    public Project findProject(String name) {
        Project project = projectsByName.get(name.trim().toLowerCase());
        if (project == null) {
            throw new NotFoundException("Project \"" + name.trim() + "\" not found");
        }
        return project;
    }

    public void addTaskToProject(String projectName, int taskId) {
        Project project = findProject(projectName);
        TaskItem task = findTask(taskId);
        if (project.getTasks().contains(task)) {
            throw new DuplicateException("Task is already in this project");
        }
        project.addTask(task);
        projectsOfTask.computeIfAbsent(taskId, k -> new LinkedHashSet<>()).add(project);
    }

    public void removeTaskFromProject(String projectName, int taskId) {
        Project project = findProject(projectName);
        TaskItem task = findTask(taskId);
        if (!project.getTasks().contains(task)) {
            throw new NotFoundException("This task is not in the project");
        }
        project.removeTask(task);
        Set<Project> projects = projectsOfTask.get(taskId);
        if (projects != null) {
            projects.remove(project);
            if (projects.isEmpty()) {
                projectsOfTask.remove(taskId);
            }
        }
    }

    public void removeProject(String name) {
        Project project = findProject(name);
        for (TaskItem task : project.getTasks()) {
            Set<Project> projects = projectsOfTask.get(task.getTaskId());
            if (projects != null) {
                projects.remove(project);
                if (projects.isEmpty()) {
                    projectsOfTask.remove(task.getTaskId());
                }
            }
        }
        projectsByName.remove(name.trim().toLowerCase());
    }

    public List<Project> getProjectsOfTask(int taskId) {
        findTask(taskId);
        Set<Project> projects = projectsOfTask.get(taskId);
        if (projects == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(projects);
    }

    public List<Project> getProjects() {
        return new ArrayList<>(projectsByName.values());
    }

    public void submitToQueue(int id) {
        TaskItem task = findTask(id);
        if (task.isCompleted()) {
            throw new IllegalArgumentException("Task is already completed");
        }
        if (waitingQueue.contains(task)) {
            throw new DuplicateException("Task is already in the queue");
        }
        waitingQueue.offer(task);
    }

    public TaskItem takeNext() {
        TaskItem task = waitingQueue.poll();
        if (task == null) {
            return null;
        }
        task.markCompleted();
        doneLog.offer(task);
        return task;
    }

    public List<TaskItem> getQueueSnapshot() {
        List<TaskItem> copy = new ArrayList<>(waitingQueue);
        copy.sort(TaskComparators.QUEUE_ORDER);
        return copy;
    }

    public List<TaskItem> getDoneLog() {
        return new ArrayList<>(doneLog);
    }

    public List<TaskItem> getAllNaturalOrder() {
        List<TaskItem> copy = new ArrayList<>(tasks);
        copy.sort(null);
        return copy;
    }

    public List<TaskItem> getAllSorted(Comparator<TaskItem> comparator) {
        List<TaskItem> copy = new ArrayList<>(tasks);
        copy.sort(comparator);
        return copy;
    }

    public List<TaskItem> getByCategory(String category) {
        List<TaskItem> group = tasksByCategory.get(category);
        if (group == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(group);
    }

    public List<TaskItem> getWithMinPriority(int minPriority) {
        List<TaskItem> result = new ArrayList<>();
        for (TaskItem task : tasks) {
            if (task.getPriority() >= minPriority) {
                result.add(task);
            }
        }
        return result;
    }

    public List<TaskItem> searchByTitle(String text) {
        List<TaskItem> result = new ArrayList<>();
        String lower = text.trim().toLowerCase();
        for (TaskItem task : tasks) {
            if (task.getTitle().toLowerCase().contains(lower)) {
                result.add(task);
            }
        }
        return result;
    }

    public Map<String, Integer> countByCategory() {
        Map<String, Integer> result = new TreeMap<>();
        for (Map.Entry<String, List<TaskItem>> entry : tasksByCategory.entrySet()) {
            result.put(entry.getKey(), entry.getValue().size());
        }
        return result;
    }

    public double totalScore() {
        double total = 0;
        for (TaskItem task : tasks) {
            total += task.calculatePriorityScore();
        }
        return total;
    }

    public double averagePriority() {
        if (tasks.isEmpty()) {
            return 0;
        }
        double sum = 0;
        for (TaskItem task : tasks) {
            sum += task.getPriority();
        }
        return sum / tasks.size();
    }

    public int completedCount() {
        int count = 0;
        for (TaskItem task : tasks) {
            if (task.isCompleted()) {
                count++;
            }
        }
        return count;
    }

    public int taskCount() {
        return tasks.size();
    }

    public int waitingCount() {
        return waitingQueue.size();
    }
}
