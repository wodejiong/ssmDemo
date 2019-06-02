package demo.wj.service.impl;

import com.github.pagehelper.PageHelper;
import demo.wj.dao.OrderDao;
import demo.wj.domain.Orders;
import demo.wj.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;
    @Override
    public List<Orders> findAll(int currentPage, int size) {

        PageHelper.startPage(currentPage, size);
        return orderDao.findAll();
    }

    @Override
    public Orders findById(String id) {
        Orders order = orderDao.findById(id);
        return order;
    }
}
