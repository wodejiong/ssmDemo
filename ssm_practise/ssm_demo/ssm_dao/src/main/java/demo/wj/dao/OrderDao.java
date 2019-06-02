package demo.wj.dao;

import demo.wj.domain.Member;
import demo.wj.domain.Orders;
import demo.wj.domain.Product;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface OrderDao {
    @Select("select * from orders")
    @Results({
            @Result(id=true,property="id",column = "id"),
            @Result(property ="orderNum",column = "orderNum"),
            @Result(property = "orderTime",column="orderTime"),
            @Result(property = "orderStatus",column = "orderStatus"),
            @Result(property = "peopleCount",column = "peopleCount"),
            @Result(property = "product",javaType = Product.class,column = "productId",
                    one=@One(select = "demo.wj.dao.ProductDao.findById"))
    })
    List<Orders> findAll();

    @Select("select * from orders where id=#{id}")
    @Results({
            @Result(id=true,property="id",column = "id"),
            @Result(property ="orderNum",column = "orderNum"),
            @Result(property = "orderTime",column="orderTime"),
            @Result(property = "orderStatus",column = "orderStatus"),
            @Result(property = "peopleCount",column = "peopleCount"),
            @Result(property = "product",javaType = Product.class,column = "productId",
                    one=@One(select = "demo.wj.dao.ProductDao.findById")),
            @Result(property = "member",javaType = Member.class,column = "memberId",
                    one=@One(select = "demo.wj.dao.MemberDao.findById")),
            @Result(property = "travellers",javaType=java.util.List.class,column = "id",
                    many= @Many(select = "demo.wj.dao.TravellerDao.findById"))
    })
    Orders findById(String Id);
}
