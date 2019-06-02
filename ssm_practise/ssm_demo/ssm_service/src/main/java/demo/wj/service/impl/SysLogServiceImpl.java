package demo.wj.service.impl;

import com.github.pagehelper.PageHelper;
import demo.wj.dao.SysLogDao;
import demo.wj.domain.SysLog;
import demo.wj.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysLogServiceImpl implements SysLogService {
    @Autowired
    private SysLogDao sysLogDao;
    @Override
    public void save(SysLog sysLog) {
        sysLogDao.save(sysLog);
    }

    @Override
    public List<SysLog> findAll(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return sysLogDao.findAll();
    }

}
