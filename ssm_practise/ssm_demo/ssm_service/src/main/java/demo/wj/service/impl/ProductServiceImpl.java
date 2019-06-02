package demo.wj.service.impl;

import com.github.pagehelper.PageHelper;
import demo.wj.dao.ProductDao;
import demo.wj.domain.Product;
import demo.wj.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductDao productDao;

    @Override
    public List<Product> findAll(int pageNum, int pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        return productDao.findAll();
    }

    @Override
    public void save(Product product) {
        productDao.save(product);
    }

    @Override
    public void deleteById(String id) {
        productDao.deleteById(id);
    }
}
