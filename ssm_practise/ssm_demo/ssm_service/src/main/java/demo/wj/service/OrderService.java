package demo.wj.service;

import demo.wj.domain.Orders;

import java.util.List;

public interface OrderService {

    List<Orders> findAll(int currentPage, int size);

    Orders findById(String id);
}
