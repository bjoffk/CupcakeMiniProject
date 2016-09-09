/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * 
 */
package servlets;

import data.Db;
import entity.Cupcake;
import entity.Layer;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author bearu
 */
@WebServlet(name = "Front", urlPatterns = {"/Front"})
public class Front extends HttpServlet {

    ArrayList<Layer> theBottoms = new ArrayList();
    ArrayList<Layer> theToppings = new ArrayList();
    ArrayList<Cupcake> theCupcakes = new ArrayList();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String origin = request.getParameter("origin");

        switch (origin) {

            case "login":

                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String sql = "SELECT idUser from user where binary username like \"" + username + "\" and binary password like \"" + password + "\"";
                 {
                    try {
                        PreparedStatement pstmt = Db.getConnection().prepareStatement(sql);
                        ResultSet rs = pstmt.executeQuery();

                        if (rs.next()) {
                            String sqlBalance = "SELECT balance FROM user where idUser = " + rs.getInt(1) + ";";
                            PreparedStatement sqlBalpstmt = Db.getConnection().prepareStatement(sqlBalance);
                            ResultSet balance = sqlBalpstmt.executeQuery();
                            balance.last();
                            int userBalance = balance.getInt("balance");

                            request.getSession().setAttribute("balance", userBalance);
                            request.getSession().setAttribute("username", username);

                            refreshCupcakes(1);
                            request.getSession().setAttribute("toppings", theToppings);
                            request.getSession().setAttribute("bottoms", theBottoms);
                            request.getSession().setAttribute("cupcakes", theCupcakes);

                            response.sendRedirect("theshop.jsp");
                        } else {
                            response.sendRedirect("login.jsp#");
                        }

                    } catch (SQLException ex) {
                        Logger.getLogger(Front.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                break;

            case "logout":

                request.getSession().invalidate();
                response.sendRedirect("login.jsp#");

                break;

            case "userwhatever":
                List<User> users = new ArrayList();
                try {
                    ResultSet rs = Db.getConnection().prepareStatement("SELECT * FROM login").executeQuery();
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        String userName = rs.getString(2);
                        String passwordWhatEver = rs.getString(3);
                        int balance = rs.getInt(4);
                        users.add(new User(id, userName, passwordWhatEver, balance));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                request.getSession().setAttribute("users", users);
                response.sendRedirect("theshop.jsp");

                break;

            case "register":
                try {
                    String usernameRegister = request.getParameter("username");
                    String passwordRegister = request.getParameter("password");
                    String sqlRegister = "INSERT INTO login (username, password) VALUES (?, ?)";
                    PreparedStatement pstmt = Db.getConnection().prepareStatement(sqlRegister);
                    pstmt.setString(1, usernameRegister);
                    pstmt.setString(2, passwordRegister);
                    pstmt.executeUpdate();
                    request.getSession().setAttribute("username", usernameRegister);
                    request.getSession().setAttribute("password", passwordRegister);
                    request.getRequestDispatcher("result.jsp").forward(request, response);
                    response.getWriter().print("SUCCESS! the user is added");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                break;

            default:
                break;
        }

    }

    protected void refreshCupcakes(int type) {

        if (type == 1) {

            try {
                //Populate theToppings! 
                ResultSet rs = Db.getConnection().prepareStatement("SELECT * FROM cupcaketopping").executeQuery();
                while (rs.next()) {
                    int topId = rs.getInt(1);
                    String topCupcakeLayerPiece = rs.getString(2);
                    int topPrice = rs.getInt(3);
                    theToppings.add(new Layer(topId, topCupcakeLayerPiece, topPrice));
                }
                //Populate theBottoms! 
                rs = Db.getConnection().prepareStatement("SELECT * FROM cupcakebottom").executeQuery();
                while (rs.next()) {
                    int topId = rs.getInt(1);
                    String topCupcakeLayerPiece = rs.getString(2);
                    int topPrice = rs.getInt(3);
                    theBottoms.add(new Layer(topId, topCupcakeLayerPiece, topPrice));
                }
                //Populate theCupcakes! 
                rs = Db.getConnection().prepareStatement("SELECT * FROM cupcake").executeQuery();
                while (rs.next()) {
                    int cupcakeId = rs.getInt(1);
                    String cupcakeName = rs.getString(2);
                    int cupcakeIdTopping = rs.getInt(3);
                    int cupcakeIdBottom = rs.getInt(4);
                    theCupcakes.add(new Cupcake(cupcakeId, cupcakeName, cupcakeIdTopping, cupcakeIdBottom));
                }
                
                
                
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

        } else if (type == 2) {

        }

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
