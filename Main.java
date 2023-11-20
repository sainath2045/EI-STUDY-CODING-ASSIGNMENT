import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

// Task class with Builder Pattern
class Task {
    private String description;
    private boolean completed;
    private String dueDate;

    private Task(Builder builder) {
        this.description = builder.description;
        this.completed = false;
        this.dueDate = builder.dueDate;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void complete() {
        this.completed = true;

    }

    // Builder class for Task
    public static class Builder {
        private String description;
        private boolean completed;
        private String dueDate;

        public Builder(String description) {
            this.description = description;
        }

        public Builder setCompleted(boolean completed) {
            this.completed = completed;
            return this;
        }

        public Builder setDueDate(String dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Task build() {
            return new Task(this);
        }
    }
}

// TaskListManager class
class TaskListManager {
    private List<Task> tasks;
    private Stack<List<Task>> undoStack;
    private Stack<List<Task>> redoStack;

    public TaskListManager() {
        this.tasks = new ArrayList<>();
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
        saveToHistory();
    }

    public void markTaskAsCompleted(String description) {
        for (Task task : tasks) {
            if (task.getDescription().equals(description)) {
                task.complete();
                return;
            }
        }
        System.out.println("Task not found: " + description);
    }

    public void deleteTask(String description) {
        boolean taskFound = false;
        for (Task task : tasks) {
            if (task.getDescription().equals(description)) {
                tasks.remove(task);
                taskFound = true;
                break;
            }
        }

        if (taskFound) {
            saveToHistory();
            System.out.println("Task deleted: " + description);
        } else {
            System.out.println("Task not found: " + description);
        }
    }

    public void viewTasks(String filter) {
        switch (filter.toLowerCase()) {
            case "all":
                displayTasks(tasks);
                break;
            case "completed":
                displayTasks(filterTasks(true));
                break;
            case "pending":
                displayTasks(filterTasks(false));
                break;
            default:
                System.out.println("Invalid filter: " + filter);
        }
    }

    private List<Task> filterTasks(boolean completed) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.isCompleted() == completed) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    private void displayTasks(List<Task> taskList) {
        for (Task task : taskList) {
            System.out.println(task.getDescription() + " - " +
                    (task.isCompleted() ? "Completed" : "Pending") +
                    (task.getDueDate() != null ? ", Due: " + task.getDueDate() : ""));
        }
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(new ArrayList<>(tasks));
            tasks = undoStack.pop();
            System.out.println("Undo successful.");
        } else {
            System.out.println("Nothing to undo.");
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(new ArrayList<>(tasks));
            tasks = redoStack.pop();
            System.out.println("Redo successful.");
        } else {
            System.out.println("Nothing to redo.");
        }
    }

    private void saveToHistory() {
        // Only save to history if the current state is different from the top of
        // undoStack
        if (undoStack.isEmpty() || !undoStack.peek().equals(tasks)) {
            undoStack.push(new ArrayList<>(tasks));
        }
        // Clear redoStack when a new change is made
        redoStack.clear();
    }
}

// Main class
public class Main {
    public static void main(String[] args) {
        TaskListManager taskListManager = new TaskListManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println(
                    "Options: 1:Add Task \n 2:Mark Completed \n 3:Delete Task \n 4:View Tasks \n 5:Undo \n 6:Redo \n 7:Exit");
            System.out.print("Enter your option: ");
            String option = scanner.nextLine();

            switch (option.toLowerCase()) {
                case "1":
                    System.out.print("Enter task description: ");
                    String description = scanner.nextLine();
                    System.out.print("Enter due date: ");
                    String dueDate = scanner.nextLine();
                    Task task = new Task.Builder(description)
                            .setDueDate(dueDate.isEmpty() ? null : dueDate)
                            .build();
                    taskListManager.addTask(task);
                    break;

                case "2":
                    System.out.print("Enter task description to mark as completed: ");
                    String completedTask = scanner.nextLine();
                    taskListManager.markTaskAsCompleted(completedTask);
                    break;

                case "3":
                    System.out.print("Enter task description to delete: ");
                    String deletedTask = scanner.nextLine();
                    taskListManager.deleteTask(deletedTask);
                    break;

                case "4":
                    System.out.print("Enter filter (All, Completed, Pending): ");
                    String filter = scanner.nextLine();
                    taskListManager.viewTasks(filter);
                    break;

                case "5":
                    taskListManager.undo();
                    break;

                case "6":
                    taskListManager.redo();
                    break;

                case "7":
                    System.out.println("Exiting the program.");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}

