package de.hsos.vts.ourworkstack.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalDate;

public interface Task extends Remote {
    String getName() throws RemoteException;

    void setName(String name) throws RemoteException;

    LocalDate getDueDate() throws RemoteException;

    void setDueDate(LocalDate dueDate) throws RemoteException;

    Effort getEffort() throws RemoteException;

    void setEffort(Effort effort) throws RemoteException;

    float getDuration() throws RemoteException;

    void setDuration(float duration) throws RemoteException;

    Priority getPriority() throws RemoteException;

    void setPriority(Priority priority) throws RemoteException;

    void completeTask() throws RemoteException;

    void deleteTask() throws RemoteException;
}
