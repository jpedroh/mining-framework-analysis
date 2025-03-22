<<<<<<< /usr/src/app/output/pagehelper/mybatis-pagehelper/964a5ebebae2922f93d80ac9403670905686c88c/src/main/java/com/github/pagehelper/PageRowBounds.java/left.java
fatal: path 'src/main/java/com/github/pagehelper/PageRowBounds.java' exists on disk, but not in '31fec6b6c8d9b71a5fc3f62e4c34c66f6ce3a2ea'
||||||| /usr/src/app/output/pagehelper/mybatis-pagehelper/964a5ebebae2922f93d80ac9403670905686c88c/src/main/java/com/github/pagehelper/PageRowBounds.java/base.java
package com.github.pagehelper;

import org.apache.ibatis.session.RowBounds;

/**
 * @author liuzenghui
 */
public class PageRowBounds extends RowBounds {
  private Long total;

  public PageRowBounds() {
  }

  public PageRowBounds(int offset, int limit) {
    super(offset, limit);
  }

  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }
}
=======
package com.github.pagehelper;

import org.apache.ibatis.session.RowBounds;

/**
 * @author liuzenghui
 */
public class PageRowBounds extends RowBounds {
    private Long total;

    public PageRowBounds(int offset, int limit) {
        super(offset, limit);
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
>>>>>>> /usr/src/app/output/pagehelper/mybatis-pagehelper/964a5ebebae2922f93d80ac9403670905686c88c/src/main/java/com/github/pagehelper/PageRowBounds.java/right.java
