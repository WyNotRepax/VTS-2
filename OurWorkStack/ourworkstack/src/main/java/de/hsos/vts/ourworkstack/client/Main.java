package de.hsos.vts.ourworkstack.client;

import de.hsos.vts.ourworkstack.common.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;


public class Main {
    private static Server server;

    private static final String[] MAIN_OPT = {
            "alle Aufgaben anzeigen",
            "offene Aufgaben anzeigen",
            "fertige Aufgaben anzeigen",
            "neue Aufgabe anlegen"
    };

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(Util.PORT);
            server = (Server) registry.lookup(Util.NAME);
            mainLoop();
        } catch (RemoteException | NotBoundException | ClassCastException e) {
            e.printStackTrace();
        }
    }

    private static void mainLoop() {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Username: ");
        //String username = scanner.nextLine();

        try {
            while (true) {


                System.out.println("Was soll passieren?");
                for (int i = 0; i < MAIN_OPT.length; i++) {
                    System.out.printf("(%d) %s\n", i, MAIN_OPT[i]);
                }

                int i = IO.readInt("Auswahl: ");
                switch (i) {
                    case 0:
                        showAllLoop();
                        break;
                    case 1:
                        showOpenLoop();
                        break;
                    case 2:
                        showClosedLoop();
                        break;
                    case 3:
                        createTaskLoop();
                        break;
                    default:
                }

            }
/*
            while (true) {
                String input = scanner.nextLine();
                if (input.equals("/exit")) {
                    System.out.println("Bye");
                    System.exit(0);
                }
                System.out.println("new or delete ");
                switch (input) {
                    case "new":
                        System.out.println("String name, LocalDate dueDate, Effort effort, float duration, Priority priority angeben");
                        System.out.println("name");
                        String name = scanner.nextLine();
                        System.out.println("LocalDate");
                        LocalDate localDate = LocalDate.of(2005, 10, 4);
                        Effort effort = Effort.MEDIUM;
                        float duration = 5;
                        Priority priority = Priority.EXTREME;
                        server.createTask(name, localDate, effort, duration, priority);
                        break;
                    case "delete":
                        System.out.println("Welches Objekt ");
                        String value = scanner.nextLine();
                        int zahl = Integer.valueOf(value);
                        server.getOpenTasks().get(zahl).deleteTask();

                }
            }*/

        } catch (RemoteException e) {
            System.err.println("Remote Exception occured");
            System.exit(1);
        }
    }

    private static void createTaskLoop() throws RemoteException {

    }

    private static void showClosedLoop() throws RemoteException {
    displayTasks(server.getCompletedTasks());
    }

    private static void showOpenLoop() throws RemoteException {
displayTasks(server.getOpenTasks());
    }

    private static void showAllLoop() throws RemoteException {
        displayTasks(server.getAllTasks());
    }

    private static void displayTasks(List<Task> tasks) throws RemoteException {
        int maxNameLength = tasks.stream().mapToInt(task ->
        {
            try {
                return task.getName().length();
            } catch (RemoteException e) {
                return 0;
            }
        }).max().orElse(0);

        System.out.printf("%" + (maxNameLength + 1) +"s| %10s | %10s | %5s | %10s\n", "Name", "Due Date", "Effort" , "Duration", "Priority");
        for (Task t : tasks) {
            System.out.printf("%" + (maxNameLength + 1) +"s| %10s | %10s | %5f | %10s\n", t.getName(), t.getDueDate(), t.getEffort(), t.getDuration(), t.getPriority());
        }
    }

}

