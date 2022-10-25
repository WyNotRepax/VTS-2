package de.hsos.vs;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementierung des BillBoard-Servers. In dieser Version unterstützt er
 * asynchrone Aufrufe. Damit wird die Implementierung von Long Polling möglich:
 * Anfragen (HTTP GET) werden nicht sofort wie bei zyklischem Polling
 * beantwortet sondern verbleiben so lange im System, bis eine Änderung an den
 * Client gemeldet werden kann.
 *
 * @author heikerli
 */
@WebServlet(asyncSupported = true, urlPatterns = {"/BillBoardServer"})
public class BillBoardServlet extends HttpServlet {

    private final Set<Thread> waiting = new HashSet<>();

    //private final BillBoardHtmlAdapter bb = new BillBoardHtmlAdapter ("BillBoardServer");
    private final BillBoardJSONAdapter bb = new BillBoardJSONAdapter("BillBoardServer");

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String caller_ip = request.getRemoteAddr();
        /* Ausgabe des gesamten Boards */
        System.out.println("BillBoardServer - GET (" + caller_ip + "): full output");
        response.setContentType("application/json;charset=UTF-8");

        boolean wait = Boolean.parseBoolean(request.getParameter("wait"));
        if (wait) {
            try {
                waiting.add(Thread.currentThread());
                System.out.printf("%s STARTED SLEEPING\n",Thread.currentThread().getName());
                System.out.printf("%d sleeping\n", waiting.size());
                Thread.sleep(10000);
                waiting.remove(Thread.currentThread());
            } catch (InterruptedException e) {
                System.out.printf("%s INTERRUPTED\n",Thread.currentThread().getName());
            }

        }

        PrintWriter out = response.getWriter();
        String table = bb.readEntries(caller_ip);
        try {
            out.println(table);
        } finally {
            out.close();
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String caller_ip = request.getRemoteAddr();
        System.out.println("BillBoardServer - POST (" + caller_ip + ")");
        // TODO implementation of doPost()!

        String message = request.getParameter("message");
        if (message != null) {
            bb.createEntry(message, caller_ip);
        }
        interrupt();
        response.getWriter().close();
    }

    /**
     * Handles the HTTP <code>DELETE</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String caller_ip = request.getRemoteAddr();
        System.out.println("BillBoardServer - DELETE (" + caller_ip + ")");

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            if (bb.getEntry(id).owner_ip.equals(caller_ip)) {
                bb.deleteEntry(id);
            } else {
                response.sendError(403, "Owner falsch!");
            }

        } catch (NumberFormatException e) {
            response.sendError(400, "Malformed Parameter id!");
        }
        interrupt();
        response.getWriter().close();
    }

    /**
     * Handles the HTTP <code>PUT</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String caller_ip = request.getRemoteAddr();
        System.out.println("BillBoardServer - PUT (" + caller_ip + ")");

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String message = request.getParameter("message");
            if (message != null) {
                if (bb.getEntry(id).owner_ip.equals(caller_ip)) {
                    bb.updateEntry(id, message, caller_ip);
                } else {
                    response.sendError(403, "Owner falsch!");
                }
            } else {

                response.sendError(400, "Parameter message missing!");
            }
        } catch (NumberFormatException e) {
            response.sendError(400, "Malformed Parameter id!");
        }
        interrupt();
        response.getWriter().close();
    }
    
    private void interrupt(){
        System.out.println(waiting.size());
        waiting.forEach(new Consumer<Thread>() {
            @Override
            public void accept(Thread t) {
                
                t.interrupt();
                
            }
        });
        waiting.clear();
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "BillBoard Servlet";
    }// </editor-fold>
}
