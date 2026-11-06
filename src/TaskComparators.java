import java.util.Comparator;

public class TaskComparators {

    public static final Comparator<TaskItem> BY_TITLE =
            Comparator.comparing(TaskItem::getTitle, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<TaskItem> BY_PRIORITY_DESC =
            (a, b) -> Integer.compare(b.getPriority(), a.getPriority());

    public static final Comparator<TaskItem> BY_SCORE_DESC =
            (a, b) -> Double.compare(b.calculatePriorityScore(), a.calculatePriorityScore());

    public static final Comparator<TaskItem> BY_CATEGORY_THEN_SCORE =
            Comparator.comparing(TaskItem::getCategory).thenComparing(BY_SCORE_DESC);

    public static final Comparator<TaskItem> QUEUE_ORDER =
            BY_PRIORITY_DESC.thenComparingInt(TaskItem::getTaskId);
}
