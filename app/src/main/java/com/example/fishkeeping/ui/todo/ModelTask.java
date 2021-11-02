package com.example.fishkeeping.ui.todo;

public class ModelTask {
    private int todoid;
    private String todotitle, deadline, notification, todotime, todonotes;

    public ModelTask(int todoid, String todotitle, String deadline, String notification, String todotime, String todonotes) {
        this.todoid = todoid;
        this.todotitle = todotitle;
        this.deadline = deadline;
        this.notification = notification;
        this.todotime = todotime;
        this.todonotes = todonotes;
    }

    public int getTodoid() {
        return todoid;
    }

    public void setTodoid(int todoid) {
        this.todoid = todoid;
    }

    public String getTodotitle() {
        return todotitle;
    }

    public void setTodotitle(String todotitle) {
        this.todotitle = todotitle;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getTodotime() {
        return todotime;
    }

    public void setTodotime(String todotime) {
        this.todotime = todotime;
    }

    public String getTodonotes() {
        return todonotes;
    }

    public void setTodonotes(String todonotes) {
        this.todonotes = todonotes;
    }
}
