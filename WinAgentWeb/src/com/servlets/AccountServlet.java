package com.servlets;

import com.entity.Account;
import com.entity.User;
import com.service.AccountService;
import com.service.PayRecordService;
import com.serviceImpl.AccountServiceImpl;
import com.serviceImpl.PayRecordServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AccountServlet")
public class AccountServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op=request.getParameter("op");
        if("pay".equals(op)){
            pay(request,response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    private void pay(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }
}
