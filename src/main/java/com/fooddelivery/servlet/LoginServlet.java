package com.fooddelivery.servlet;

import com.fooddelivery.dao.UserDAO;
import com.fooddelivery.dao.impl.UserDAOImpl;
import com.fooddelivery.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    private UserDAO userDAO = new UserDAOImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Login - Food Delivery</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println(getNavigation());
        out.println("<div class='container'>");
        out.println("<div class='form-container'>");
        out.println("<h2 style='text-align:center;color:var(--white);margin-bottom:2rem;'>Login</h2>");
        out.println("<form method='post'>");
        out.println("<div class='form-group'>");
        out.println("<label>Email/Phone</label>");
        out.println("<input type='text' name='emailOrPhone' required placeholder='Enter email or phone'>");
        out.println("</div>");
        out.println("<div class='form-group'>");
        out.println("<label>Password</label>");
        out.println("<input type='password' name='password' required placeholder='Enter password'>");
        out.println("</div>");
        out.println("<button type='submit' class='btn' style='width:100%;'>Login</button>");
        out.println("</form>");
        out.println("<p style='text-align:center;margin-top:1rem;color:var(--white);'>");
        out.println("Don't have an account? <a href='register' style='color:var(--white);font-weight:bold;'>Register here</a>");
        out.println("</p>");
        out.println("</div>");
        out.println("</div>");
        out.println("<script src='js/main.js'></script>");
        out.println("</body></html>");
    }
    
    private String getNavigation() {
        return "<nav class='navbar'>" +
               "<div class='nav-container'>" +
               "<div class='nav-brand'>🍕 Food Delivery</div>" +
               "<ul class='nav-menu'>" +
               "<li><a href='index.html'>Home</a></li>" +
               "<li><a href='dashboard'>Dashboard</a></li>" +
               "<li><a href='restaurants'>Restaurants</a></li>" +
               "<li><a href='register'>Register</a></li>" +
               "<li><a href='login'>Login</a></li>" +
               "</ul>" +
               "</div>" +
               "</nav>";
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String emailOrPhone = request.getParameter("emailOrPhone");
        String password = request.getParameter("password");
        
        User user = userDAO.findByEmailOrPhone(emailOrPhone);
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Login Result - Food Delivery</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println(getNavigation());
        out.println("<div class='container'>");
        out.println("<div class='form-container'>");
        
        if (user != null && user.getPasswordHash().equals(password)) {
            out.println("<div class='alert alert-success'>");
            out.println("<h2>✅ Login Successful!</h2>");
            out.println("<p><strong>Welcome, " + user.getName() + "!</strong></p>");
            out.println("<p>Role: " + user.getRole() + "</p>");
            out.println("</div>");
            out.println("<a href='dashboard' class='btn'>Go to Dashboard</a>");
        } else {
            out.println("<div class='alert alert-error'>");
            out.println("<h2>❌ Login Failed</h2>");
            out.println("<p>Invalid email/phone or password.</p>");
            out.println("</div>");
            out.println("<a href='login' class='btn'>Try Again</a>");
        }
        
        out.println("</div>");
        out.println("</div>");
        out.println("<script src='js/main.js'></script>");
        out.println("</body></html>");
    }
}

