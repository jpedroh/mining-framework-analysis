package com.github.pagehelper.test.basic;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.mapper.CountryMapper;
import com.github.pagehelper.model.Country;
import com.github.pagehelper.util.MybatisHelper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class ArgumentsMapTest {
  /**
     * 使用Mapper接口调用时，使用PageHelper.startPage效果更好，不需要添加Mapper接口参数
     */
  @Test public void testArgumentsMap() {
    SqlSession sqlSession = MybatisHelper.getSqlSession();
    CountryMapper countryMapper = sqlSession.getMapper(CountryMapper.class);
    try {
      List<Country> list = countryMapper.selectByPageNumSizeOrderBy(1, 10, "id desc");
      assertEquals(10, list.size());
      assertEquals(183, ((Page<?>) list).getTotal());
      list = countryMapper.selectByPageNumSize(2, 10);
      assertEquals(10, list.size());
      assertEquals(183, ((Page<?>) list).getTotal());
      list = countryMapper.selectByPageNumSize(3, 20);
      assertEquals(20, list.size());
      assertEquals(183, ((Page<?>) list).getTotal());
    }  finally {
      sqlSession.close();
    }
  }
}