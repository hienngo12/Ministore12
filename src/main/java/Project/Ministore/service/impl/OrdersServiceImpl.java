package Project.Ministore.service.impl;

import Project.Ministore.Dto.OrdersAddressEntityDto;
import Project.Ministore.Entity.CartEntity;
import Project.Ministore.Entity.OrdersAddressEntity;
import Project.Ministore.Entity.OrdersEntity;
import Project.Ministore.repository.CartRepository;
import Project.Ministore.repository.OrdersRepository;
import Project.Ministore.service.OrdersService;
import Project.Ministore.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrdersServiceImpl implements OrdersService {
@Autowired
private OrdersRepository ordersRepository;
@Autowired
   private CartRepository cartRepository;
    @Override
    public void saveOrder(int accountId, OrdersAddressEntityDto ordersAddressEntityDto) {
        List<CartEntity> carts = cartRepository.findByAccountEntity_Id(accountId);
        for (CartEntity cart : carts){
        OrdersEntity orders = new OrdersEntity();
        orders.setOrderId(UUID.randomUUID().toString());
        orders.setOrderDate(new Date());
        orders.setProductEntity(cart.getProductEntity());
        orders.setPrice(cart.getProductEntity().getPrice());
        orders.setQuantity(cart.getQuantity());
        orders.setAccountEntity(cart.getAccountEntity());
        orders.setStatus(OrderStatus.IN_PROGRESS.getName());
        orders.setPayment_type(ordersAddressEntityDto.getPayment_type());
        OrdersAddressEntity address = new OrdersAddressEntity();
        address.setName(ordersAddressEntityDto.getName());
        address.setEmail(ordersAddressEntityDto.getEmail());
        address.setPhone(ordersAddressEntityDto.getPhone());
        address.setAddress(ordersAddressEntityDto.getAddress());
        address.setCity(ordersAddressEntityDto.getCity());
        address.setProvince(ordersAddressEntityDto.getProvince());
        orders.setOrdersAddressEntity(address);
        ordersRepository.save(orders);
        }
    }

    @Override
    public List<OrdersEntity> getOrdersByUser(int accountId) {
        List<OrdersEntity> orders = ordersRepository.findByAccountEntityId(accountId);
        return orders;
    }

    @Override
    public Boolean updateOrderStatus(int id, String status) {
        Optional<OrdersEntity> findById = ordersRepository.findById(id);
        if (findById.isPresent()){
            OrdersEntity ordersEntity = findById.get();
            ordersEntity.setStatus(status);
            ordersRepository.save(ordersEntity);
            return true;
        }
        return false;
    }

    @Override
    public List<OrdersEntity> getAllOrders() {
        return ordersRepository.findAll();
    }

}
