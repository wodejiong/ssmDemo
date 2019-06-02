package demo.wj.dao;

import demo.wj.domain.Member;
import org.apache.ibatis.annotations.Select;

public interface MemberDao {
    @Select("select * from Member where id = #{id}")
    Member findById(String id);
}
