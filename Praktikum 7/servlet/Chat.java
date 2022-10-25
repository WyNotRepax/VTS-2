/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatsystem.servlet;

import chatsystem.ChatProxy;
import chatsystem.ChatServer;
import chatsystem.Main;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author steffen
 */
@WebServlet(name = "Chat", urlPatterns = {"/Chat"})
public class Chat extends HttpServlet {

    public static final String ACTION_PARAM = "action";
    public static final String NAME_PARAM = "name";
    public static final String MESSAGE_PARAM = "message";

    public static final String ACTION_INIT = "init";
    public static final String ACTION_SENDMESSAGE = "sendMessage";
    public static final String ACTION_UNSUBSCRIBE = "unsubscribe";
    public static final String ACTION_GETMESSAGE = "getMessage";

    public static final String CHAT_PROXY_ATTR = "chatProxy";
    public static final String CHAT_SERVER_ATTR = "chatServer";
    public static final String NAME_ATTR = "name";
    public static final String CLIENT_PROXY_ATTR = "clientProxy";


    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter(ACTION_PARAM);
        if (action == null) {
            request.getRequestDispatcher("/init.html").forward(request, response);
        }
        switch (action) {
            case ACTION_INIT:
                doInit(request, response);
                break;
            case ACTION_SENDMESSAGE:
                doSendMessage(request, response);
                break;
            case ACTION_UNSUBSCRIBE:
                doUnsubscribe(request, response);
                break;
            case ACTION_GETMESSAGE:
                doGetMessage(request, response);
                break;
            default:
                response.sendError(400, "Invalid Action");

        }

    }

    private void doInit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            String name = request.getParameter(NAME_PARAM);
            if (name == null) {
                response.sendError(400, "No Name Provided");
                return;
            }
            Registry registry = LocateRegistry.getRegistry(Main.PORT);

            ChatServer chatServer = (ChatServer) registry.lookup(Main.NAME);
            ClientProxyImpl clientProxy = new ClientProxyImpl();
            ChatProxy chatProxy = chatServer.subscribeUser(name, clientProxy);

            HttpSession session = request.getSession(true);
            session.setAttribute(CHAT_SERVER_ATTR, chatServer);
            session.setAttribute(CHAT_PROXY_ATTR, chatProxy);
            session.setAttribute(NAME_ATTR, name);
            session.setAttribute(CLIENT_PROXY_ATTR, clientProxy);

            request.getRequestDispatcher("/chat.html").forward(request, response);

        } catch (RemoteException | NotBoundException ex) {
            response.sendError(503, ex.getMessage());
        }
    }

    public void doSendMessage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        //request.getRequestDispatcher("/sendMessage.html").forward(request,response);
        HttpSession session = request.getSession();
        ChatProxy chatProxy = (ChatProxy) session.getAttribute(CHAT_PROXY_ATTR);
        if (chatProxy == null) {
            response.sendError(500, "ChatProxy for this Session is null");
            return;
        }
        String message = request.getParameter(MESSAGE_PARAM);
        if (message == null) {
            response.sendError(400, "No Message Provided");
            return;
        }
        if (message.length() > 0) {
            chatProxy.sendMessage(message);
        }
        request.getRequestDispatcher("/sendMessage.html").forward(request, response);
    }

    public void doUnsubscribe(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        ChatServer chatServer = (ChatServer) session.getAttribute(CHAT_SERVER_ATTR);
        if (chatServer == null) {
            response.sendError(500, "ChatServer for this Session is null");
            return;
        }
        String name = (String) session.getAttribute(NAME_ATTR);
        if (name == null) {
            response.sendError(500, "Name for this Session is null");
            return;
        }
        chatServer.unsubscribeUser(name);
        request.getRequestDispatcher("/init.html").forward(request, response);
    }

    public void doGetMessage(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.getRequestDispatcher("/getMessage.jsp").forward(request, response);
    }

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
        processRequest(request, response);
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
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
