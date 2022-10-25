package de.hsos.vts.ourworkstack.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.List;

public interface Server extends Remote {
    void createTask(String name, LocalDate dueDate, Effort effort, float duration, Priority priority) throws RemoteException;
    List<Task> getAllTasks() throws RemoteException;
    List<Task> getCompletedTasks() throws RemoteException;
    List<Task> getOpenTasks() throws RemoteException;
}
