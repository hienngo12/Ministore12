package Project.Ministore.service.impl;

import Project.Ministore.Entity.AccountEntity;
import Project.Ministore.Entity.CartEntity;
import Project.Ministore.Entity.ProductEntity;
import Project.Ministore.repository.AccountRepository;
import Project.Ministore.repository.CartRepository;
import Project.Ministore.repository.ProductRepository;
import Project.Ministore.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
     private CartRepository cartRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProductRepository productRepository;
    @Override
    public CartEntity saveCart(int productId, int accountId) {
        AccountEntity user = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("User not found"));
        ProductEntity product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        CartEntity cartStatus = cartRepository.findByProductEntity_IdAndAccountEntity_Id(productId, accountId);

        CartEntity cart;
        if (ObjectUtils.isEmpty(cartStatus)) {
            // Tạo mới giỏ hàng
            cart = new CartEntity();
            cart.setProductEntity(product);
            cart.setAccountEntity(user);
            cart.setQuantity(1);  // Thiết lập số lượng mặc định là 1
            // Kiểm tra discount_price có null không trước khi tính toán
            Long discountPrice = product.getDiscount_price() != null ? product.getDiscount_price() : product.getPrice();
            cart.setTotal_price(1 * discountPrice);  // Tính giá cho 1 sản phẩm
        } else {
            // Cập nhật giỏ hàng hiện có
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);  // Tăng số lượng lên 1
            // Kiểm tra discount_price có null không trước khi tính toán
            Long discountPrice = cart.getProductEntity().getDiscount_price() != null ? cart.getProductEntity().getDiscount_price() : cart.getProductEntity().getPrice();
            cart.setTotal_price(cart.getQuantity() * discountPrice);  // Cập nhật tổng giá
        }

        return cartRepository.save(cart);
    }

    @Override
    public List<CartEntity> getCartByUser(int accountId) {
        List<CartEntity> carts = cartRepository.findByAccountEntity_Id(accountId);
        Long total_orderPrice = 0L;

        for (CartEntity c : carts) {
            // Sử dụng giá gốc nếu giá giảm giá là null
            Long price = c.getProductEntity().getDiscount_price() != null ? c.getProductEntity().getDiscount_price() : c.getProductEntity().getPrice();
            Long total_price = price * c.getQuantity();  // Tính tổng giá cho mỗi sản phẩm
            c.setTotal_price(total_price);
            total_orderPrice += total_price;  // Cộng dồn tổng giá đơn hàng
            c.setTotal_orderPrice(total_orderPrice);
        }
        return carts;
    }

    @Override
    public int getCountCart(int accountId) {
        return cartRepository.countByAccountEntity_Id(accountId);
    }

    @Override
    public void updateQuantity(String sy, int cid) {
        CartEntity cart = cartRepository.findById(cid).orElseThrow(() -> new RuntimeException("Cart not found"));

        int updateQuantity;
        if (sy.equalsIgnoreCase("de")) {
            updateQuantity = cart.getQuantity() - 1;
            if (updateQuantity <= 0) {
                cartRepository.delete(cart);
            } else {
                cart.setQuantity(updateQuantity);
                // Cập nhật tổng giá khi số lượng thay đổi
                Long discountPrice = cart.getProductEntity().getDiscount_price() != null ? cart.getProductEntity().getDiscount_price() : cart.getProductEntity().getPrice();
                cart.setTotal_price(updateQuantity * discountPrice);
                cartRepository.save(cart);
            }
        } else {
            updateQuantity = cart.getQuantity() + 1;
            cart.setQuantity(updateQuantity);
            // Cập nhật tổng giá khi số lượng thay đổi
            Long discountPrice = cart.getProductEntity().getDiscount_price() != null ? cart.getProductEntity().getDiscount_price() : cart.getProductEntity().getPrice();
            cart.setTotal_price(updateQuantity * discountPrice);
            cartRepository.save(cart);
        }
    }

    @Override
    public Long getTotalCart(int accountId) {
        List<CartEntity> carts = getCartByUser(accountId);
        Long totalPrice = 0L;

        if (!carts.isEmpty()) {
            totalPrice = carts.get(carts.size() - 1).getTotal_orderPrice() + 25000;  // Cộng phí vận chuyển (giả sử là 25000)
        }

        return totalPrice;
    }

    @Override
    public void clearCart(int accountId) {
        List<CartEntity> carts = cartRepository.findByAccountEntity_Id(accountId);
        for (CartEntity cart : carts) {
            cartRepository.delete(cart);
        }
    }
}
