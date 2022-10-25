package de.hsos.vts.ourworkstack.server;

import de.hsos.vts.ourworkstack.common.Effort;
import de.hsos.vts.ourworkstack.common.Priority;
import de.hsos.vts.ourworkstack.common.Server;
import de.hsos.vts.ourworkstack.common.Task;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ServerImpl extends UnicastRemoteObject implements Server, Serializable {
    private List<TaskImpl> tasks;


    public ServerImpl() throws RemoteException {
        this(new ArrayList<>());
    }

    public ServerImpl(List<TaskImpl> tasks) throws RemoteException {
        super();
        this.tasks = tasks;
    }

    @Override
    public void createTask(String name, LocalDate dueDate, Effort effort, float duration, Priority priority) throws RemoteException {
        tasks.add(new TaskImpl(name, dueDate, effort, duration, priority));
    }

    @Override
    public List<Task> getAllTasks() {
        return Collections.unmodifiableList(this.tasks.stream().filter(task -> !task.isDeleted()).collect(Collectors.toList()));
    }

    @Override
    public List<Task> getCompletedTasks() {
        return Collections.unmodifiableList(this.tasks.stream().filter(task -> !task.isDeleted()).filter(task -> task.isCompleted()).collect(Collectors.toList()));
    }

    @Override
    public List<Task> getOpenTasks() {
        return Collections.unmodifiableList(this.tasks.stream().filter(task -> !task.isDeleted()).filter(task -> !task.isCompleted()).collect(Collectors.toList()));
    }

    public void save(){
        try {
            ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream("TestFile"));
            stream.writeObject(this);
            FileInputStream stream1 = new FileInputStream("TestFile");
            ObjectInputStream stream2 = new ObjectInputStream(stream1);
            Object object  = stream2.readObject();
            ServerImpl server = (ServerImpl) object;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
        }
    }
}
