package demo.wj.service;

import demo.wj.domain.SysLog;

import java.util.List;

public interface SysLogService {
    void save(SysLog sysLog);

    List<SysLog> findAll(Integer pageNum, Integer pageSize);
}
