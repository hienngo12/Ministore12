package Project.Ministore.controller;

import Project.Ministore.Entity.AccountEntity;
import Project.Ministore.service.AccountService;
import Project.Ministore.service.CartService;
import Project.Ministore.service.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
public class PaymentController {
    @Autowired
    VNPayService vnPayService;
    @Autowired
    CartService cartService;
    @Autowired
    AccountService accountService;

    public AccountEntity getLoggedInUserDetails(Principal principal) {
        String email = principal.getName();
        return accountService.getUserByEmail(email);
    }
    @GetMapping("/vnpay-payment")
    public String GetMapping(HttpServletRequest request, Model model, HttpSession session, Principal principal){
        int paymentStatus =vnPayService.orderReturn(request);

        // Đặt thông báo thành công trong session

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);

        if (paymentStatus == 1) {
            // Xóa giỏ hàng sau khi thanh toán thành công
            AccountEntity user = getLoggedInUserDetails(principal); // Lấy thông tin người dùng
            cartService.clearCart(user.getId()); // Xóa giỏ hàng của người dùng

            // Đặt thông báo thành công trong session
            session.setAttribute("succMsg", "Thanh toán thành công! Giỏ hàng của bạn đã được xử lý.");
            return "user/ordersuccess";
        } else {
            return "user/orderfail";
        }
    }



}
