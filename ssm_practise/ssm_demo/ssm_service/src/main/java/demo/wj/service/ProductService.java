package demo.wj.service;

import demo.wj.domain.Product;

import java.util.List;

public interface ProductService {

    List<Product> findAll(int pageNum, int pageSize);

    void save(Product product);

    void deleteById(String id);
}
