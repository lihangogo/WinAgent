package com.servlets;

import com.daoImpl.StudentDaoImpl;
import com.entity.Account;
import com.entity.Feedback;
import com.entity.User;
import com.service.AccountService;
import com.service.AdminService;
import com.service.FeedbackService;
import com.service.UserService;
import com.serviceImpl.AccountServiceImpl;
import com.serviceImpl.AdminServiceImpl;
import com.serviceImpl.FeedbackServiceImpl;
import com.serviceImpl.UserServiceImpl;
import com.utils.RandomIDUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@WebServlet(name = "UserServlet")
public class UserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String op=request.getParameter("op");
        if("register".equals(op))
            register(request,response);
        else if("login".equals(op))
            login(request,response);
        else if("logout".equals(op))
            logout(request,response);
        else if("retrievePassword".equals(op))
            retrievePassword(request,response);
        else if("update".equals(op))
            update(request,response);
        else if("delete".equals(op))
            delete(request,response);
        else if("theClear".equals(op))
            theClear(request,response);
        else if("payment".equals(op))
            payment(request,response);
        else if("pay".equals(op))
            pay(request,response);
        else if("addFeedback".equals(op))
            addFeedback(request,response);
        else if(op.equals("downloadExe"))
            downloadExe(request,response);
        else if("feedbackInfor".equals(op))
            searchFeedback(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }

    /**
     * 查看所有的反馈信息
     * @param request
     * @param response
     * @throws IOException
     */
    private void searchFeedback(HttpServletRequest request,HttpServletResponse response)
            throws IOException{
        FeedbackService feedbackService=new FeedbackServiceImpl();
        List list=feedbackService.searchFeedback();
        request.getSession().setAttribute("feedbackList",list);
        response.sendRedirect(request.getContextPath() + "/feedbackInfor.jsp");
    }

    /**
     * 下载安装包
     * @param request
     * @param response
     * @throws IOException
     */
    private void downloadExe(HttpServletRequest request, HttpServletResponse response)
            throws IOException{
        String fileName=request.getParameter("res");
        response.setContentType(getServletContext().getMimeType(fileName));
        response.setHeader("Content-Disposition", "attachment;filename="+fileName);
        InputStream in=this.getClass().getResource("/resource/"+fileName).openStream();
        OutputStream out = response.getOutputStream();
        int b;
        while((b=in.read())!= -1) {
            out.write(b);
        }
        in.close();
        out.close();
    }

    /**
     * 用户注册
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void register(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        String userName=request.getParameter("userName");
        String nickName=request.getParameter("nickName");
        String phone=request.getParameter("phone");
        String email=request.getParameter("email");
        String password=request.getParameter("password");
        String realName=request.getParameter("real_name");
        String studentID=request.getParameter("studentID");
        String ident=request.getParameter("ident");

        User user=new User();
        user.setUserName(userName);
        user.setNickName(nickName);
        user.setPhone(phone);
        user.setEmail(email);
        user.setPassword(password);
        user.setRealName(realName);
        user.setStudentID(studentID);
        user.setIdent(ident);

        StudentDaoImpl studentDao=new StudentDaoImpl();

        // 在名单中或者  身份是老师并且工号以201081开头
        if(studentDao.getStudentByName(studentID,realName)||(ident.equals("teacher")&&studentID.startsWith("201081"))){
            UserService userService=new UserServiceImpl();
            if(userService.addUser(user)){
                if(request.getSession().getAttribute("user")==null){
                    AccountService accountService=new AccountServiceImpl();
                    User user1=userService.getUser(userName,password);
                    int uid=user1.getUserID();
                    Account account=accountService.getAccount(uid);
                    request.getSession().setAttribute("account",account);
                    request.getSession().setAttribute("user",user1);
                }
                response.getWriter().print("suc");
            }
            else{
                response.getWriter().print("failed");
            }
        }else{
            response.getWriter().print("noStudent");
        }
    }

    /**
     * 用户登录
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void login(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        String userName=request.getParameter("userName");
        String password=request.getParameter("password");
        UserService userService=new UserServiceImpl();
        User user=userService.getUser(userName,password);
        if(user!=null){
            AccountService accountService=new AccountServiceImpl();
            Account account=accountService.getAccount(user.getUserID());
            request.getSession().setAttribute("user", user);
            request.getSession().setAttribute("account",account);
            AdminService adminService=new AdminServiceImpl();
            if(adminService.searchAdmin(user.getUserID()))
                request.getSession().setAttribute("admin",true);
            response.getWriter().print("suc");
        }else{
            response.getWriter().print("failed");
        }
    }

    /**
     * 判断是否已登录，跳转至登录界面或 获取账户及余额
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void payment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            AccountService accountService = new AccountServiceImpl();
            Account account = accountService.getAccount(user.getUserID());
            request.getSession().setAttribute("account", account);
            response.sendRedirect(request.getContextPath() + "/payment.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/userLogin.jsp");
        }
    }

    /**
     * 支付
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void pay(HttpServletRequest request, HttpServletResponse response)
            throws IOException{
        Account account=(Account)request.getSession().getAttribute("account");
        if(account.getBalance()>2000){
            response.getWriter().print("enough");
        }else{
            int addMoney=Integer.valueOf(request.getParameter("addMoney"));
            account.setBalance(account.getBalance()+addMoney);
            request.getSession().setAttribute("account",account);
            if(new AccountServiceImpl().pay(account,addMoney))
                response.getWriter().print("suc");
            else
                response.getWriter().print("failed");
        }
    }

    /**
     * 反馈信息
     * @param request
     * @param response
     * @throws Exception
     */
    private void addFeedback(HttpServletRequest request, HttpServletResponse response)
        throws IOException{
        Object object=request.getSession().getAttribute("user");
        if(object!=null){
            User user=(User)object;
            Feedback feedback=new Feedback();
            feedback.setId(RandomIDUtils.getRandomID());
            feedback.setUid(user.getUserID());
            feedback.setCategory(request.getParameter("category"));
            feedback.setContent(request.getParameter("content").trim());
            FeedbackService feedbackService=new FeedbackServiceImpl();
            if(feedbackService.addFeedback(feedback))
                response.getWriter().print("suc");
            else
                response.getWriter().print("failed");
        }else{
            response.getWriter().print("loginFirst");
        }
    }

    /**
     * 登出
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void logout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        request.getSession().removeAttribute("user");
        request.getSession().removeAttribute("account");
        request.getSession().removeAttribute("admin");
        response.sendRedirect(request.getContextPath()+"/index.jsp");
    }

    /**
     * 获取密码
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void retrievePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        String userName=request.getParameter("userName");
        UserService userService=new UserServiceImpl();
        if(userService.retrievePassword(userName)){
            request.getSession().setAttribute("msg_Login","请登录注册邮箱查询");
        }else{
            request.getSession().setAttribute("msg_Login","找回失败...");
        }
        response.sendRedirect(request.getContextPath()+"/loginReg.jsp");
    }

    /**
     * 更新账户
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void update(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        User user=(User)request.getSession().getAttribute("user");
        User user1=new User();
        user1.setUserID(user.getUserID());
        String nickName=new String(request.getParameter("up_userNickName").getBytes("ISO-8859-1"),"utf-8");
        String phone=request.getParameter("up_userTel");
        String password=request.getParameter("up_psw");
        if(password.equals("password")||password.equals("PASSWORD"))
            password=user.getPassword();
        user1.setPassword(password);
        user1.setPhone(phone);
        user1.setNickName(nickName);
        user1.setEmail(user.getEmail());
        UserService userService=new UserServiceImpl();
        if(userService.updateUser(user1)){
            user=userService.getUser(user.getUserName(),password);
            request.getSession().setAttribute("user",user);
            request.getSession().setAttribute("msg_Update","更新成功！");
        }else{
            request.getSession().setAttribute("msg_Update","更新失败...");
        }
        response.sendRedirect(request.getContextPath()+"/updateInfor.jsp");
    }


    private void delete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

    }

    /**
     * 清空
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void theClear(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        Object msg_Login=request.getSession().getAttribute("msg_Login");
        Object msg_Reg=request.getSession().getAttribute("msg_Reg");
        Object msg_Update=request.getSession().getAttribute("msg_Update");
        Object msg_Pay=request.getSession().getAttribute("msg_Pay");
        if(msg_Login!=null){
            request.getSession().removeAttribute("msg_Login");
        }
        if(msg_Reg!=null){
            request.getSession().removeAttribute("msg_Reg");
        }
        if(msg_Update!=null){
            request.getSession().removeAttribute("msg_Update");
        }
        if(msg_Pay!=null){
            request.getSession().removeAttribute("msg_Pay");
        }
        response.sendRedirect(request.getContextPath()+"/index.jsp");
    }

}
