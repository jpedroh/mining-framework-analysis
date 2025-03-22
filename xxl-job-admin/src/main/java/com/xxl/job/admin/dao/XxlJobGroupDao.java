package com.xxl.job.admin.dao;
import com.xxl.job.admin.core.model.XxlJobGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
@Repository public interface XxlJobGroupDao extends JpaRepository<XxlJobGroup, Long> {
  @Override @Query(value = "select g from XxlJobGroup g ORDER BY g.order ASC") public List<XxlJobGroup> findAll();

  @Query(value = "select g from XxlJobGroup g where g.addressType = :addressType ORDER BY g.order ASC") public List<XxlJobGroup> findByAddressType(@Param(value = "addressType") int addressType);

  @Transactional @Modifying @Query(value = "update XxlJobGroup g set g.appName = :#{#xxlJobGroup.appName}, g.title = :#{#xxlJobGroup.title}, " + "g.order = :#{#xxlJobGroup.order}, g.addressType = :#{#xxlJobGroup.addressType}, g.addressList = :#{#xxlJobGroup.addressList} " + "where g.id = :#{#xxlJobGroup.id}") public int update(@Param(value = "xxlJobGroup") XxlJobGroup xxlJobGroup);

  @Transactional @Modifying @Query(value = "delete from XxlJobGroup g where g.id = :id") public int remove(@Param(value = "id") long id);

  @Query(value = "select g from XxlJobGroup g where g.id = :id") public XxlJobGroup load(@Param(value = "id") long id);

  public List<XxlJobGroup> pageList(@Param(value = "offset") int offset, @Param(value = "pagesize") int pagesize, @Param(value = "appname") String appname, @Param(value = "title") String title);

  public int pageListCount(@Param(value = "offset") int offset, @Param(value = "pagesize") int pagesize, @Param(value = "appname") String appname, @Param(value = "title") String title);
}