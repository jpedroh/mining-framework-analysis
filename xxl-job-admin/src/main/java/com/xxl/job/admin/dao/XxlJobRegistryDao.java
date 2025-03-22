package com.xxl.job.admin.dao;
import com.xxl.job.admin.core.model.XxlJobRegistry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
@Mapper public interface XxlJobRegistryDao {
  public List<Integer> findDead(@Param(value = "timeout") int timeout, @Param(value = "nowTime") Date nowTime);

  public int removeDead(@Param(value = "ids") List<Integer> ids);

  public List<XxlJobRegistry> findAll(@Param(value = "timeout") int timeout, @Param(value = "nowTime") Date nowTime);

  public int registryUpdate(@Param(value = "registryGroup") String registryGroup, @Param(value = "registryKey") String registryKey, @Param(value = "registryValue") String registryValue, @Param(value = "updateTime") Date updateTime);

  public int registrySave(@Param(value = "registryGroup") String registryGroup, @Param(value = "registryKey") String registryKey, @Param(value = "registryValue") String registryValue, @Param(value = "updateTime") Date updateTime);

  public int registryDelete(@Param(value = "registryGroup") String registryGroup, @Param(value = "registryKey") String registryKey, @Param(value = "registryValue") String registryValue);
}