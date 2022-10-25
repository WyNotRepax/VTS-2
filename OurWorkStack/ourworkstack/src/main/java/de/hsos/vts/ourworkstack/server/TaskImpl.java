package de.hsos.vts.ourworkstack.server;

import de.hsos.vts.ourworkstack.common.Effort;
import de.hsos.vts.ourworkstack.common.Priority;
import de.hsos.vts.ourworkstack.common.Task;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;

public class TaskImpl extends UnicastRemoteObject implements Task {

    private String name;
    private LocalDate dueDate;
    private Effort effort;
    private float duration;
    private Priority priority;
    private boolean completed;
    private boolean deleted;

    public TaskImpl(String name, LocalDate dueDate, Effort effort, float duration, Priority priority) throws RemoteException {
        super();
        this.name = name;
        this.dueDate = dueDate;
        this.effort = effort;
        this.duration = duration;
        this.priority = priority;
        this.completed = false;
        this.deleted = false;
    }

    @Override
    public String getName() {
       return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public LocalDate getDueDate() {
        return this.dueDate;
    }

    @Override
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public Effort getEffort() {
        return this.effort;
    }

    @Override
    public void setEffort(Effort effort) {
        this.effort = effort;
    }

    @Override
    public float getDuration() {
        return this.duration;
    }

    @Override
    public void setDuration(float duration) {
        this.duration = duration;
    }

    @Override
    public Priority getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public void completeTask() {
        this.completed = true;
    }

    public boolean isCompleted(){
        return this.completed;
    }

    @Override
    public void deleteTask() {
        this.deleted = true;
    }

    public boolean isDeleted(){
        return this.deleted;
    }
}
