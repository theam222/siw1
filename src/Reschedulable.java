public interface Reschedulable {
    void reschedule(String newDueDate);

    void extendDeadline(int extraDays);
}
