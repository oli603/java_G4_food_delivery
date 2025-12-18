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

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
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
        out.println("<title>Register - Food Delivery</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println(getNavigation());
        out.println("<div class='container'>");
        out.println("<div class='form-container'>");
        out.println("<h2 style='text-align:center;color:var(--white);margin-bottom:2rem;'>Register New User</h2>");
        out.println("<form method='post'>");
        out.println("<div class='form-group'>");
        out.println("<label>Name *</label>");
        out.println("<input type='text' name='name' required placeholder='Enter your full name'>");
        out.println("</div>");
        out.println("<div class='form-group'>");
        out.println("<label>Email</label>");
        out.println("<input type='email' name='email' placeholder='Enter your email'>");
        out.println("</div>");
        out.println("<div class='form-group'>");
        out.println("<label>Phone</label>");
        out.println("<input type='text' name='phone' placeholder='Enter your phone number'>");
        out.println("</div>");
        out.println("<div class='form-group'>");
        out.println("<label>Password *</label>");
        out.println("<input type='password' name='password' required placeholder='Enter password'>");
        out.println("</div>");
        out.println("<div class='form-group'>");
        out.println("<label>Role</label>");
        out.println("<select name='role'>");
        out.println("<option value='CUSTOMER'>Customer</option>");
        out.println("<option value='RESTAURANT'>Restaurant Owner</option>");
        out.println("<option value='DELIVERY'>Delivery Person</option>");
        out.println("</select>");
        out.println("</div>");
        out.println("<button type='submit' class='btn' style='width:100%;'>Register</button>");
        out.println("</form>");
        out.println("<p style='text-align:center;margin-top:1rem;color:var(--white);'>");
        out.println("Already have an account? <a href='login' style='color:var(--white);font-weight:bold;'>Login here</a>");
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
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(password);
        user.setRole(role != null ? role : "CUSTOMER");
        user.setStatus("ACTIVE");
        
        boolean success = userDAO.create(user);
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Register Result - Food Delivery</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println(getNavigation());
        out.println("<div class='container'>");
        out.println("<div class='form-container'>");
        
        if (success) {
            out.println("<div class='alert alert-success'>");
            out.println("<h2>✅ Registration Successful!</h2>");
            out.println("<p><strong>User created: " + name + "</strong></p>");
            out.println("<p>Role: " + (role != null ? role : "CUSTOMER") + "</p>");
            out.println("</div>");
            out.println("<a href='login' class='btn'>Go to Login</a>");
        } else {
            out.println("<div class='alert alert-error'>");
            out.println("<h2>❌ Registration Failed</h2>");
            out.println("<p>Could not create user. Email or phone might already exist.</p>");
            out.println("</div>");
            out.println("<a href='register' class='btn'>Try Again</a>");
        }
        
        out.println("</div>");
        out.println("</div>");
        out.println("<script src='js/main.js'></script>");
        out.println("</body></html>");
    }
}

