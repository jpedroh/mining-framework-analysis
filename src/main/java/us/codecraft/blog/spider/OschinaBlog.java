package us.codecraft.blog.spider;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.AfterExtractor;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.Formatter;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import java.util.Date;

/**
 * @author code4crafter@gmail.com
 */
@TargetUrl(value = "http://my.oschina.net/flashsword/blog/\\d+") @HelpUrl(value = "http://my.oschina.net/flashsword/blog\\?disp=1&catalog=0&sort=time&p=\\d+") public class OschinaBlog implements Comparable<OschinaBlog>, AfterExtractor {
  @ExtractBy(value = "//title/regex(\'>(.+?)\\s+\\-\',1)") private String title;

  @ExtractBy(value = "//div[@class=BlogContent]") private String content;

  @ExtractBy(value = "//span[@class=\"catalogs\"]//a/text()") private String category;

  @Formatter(value = "yyyy-MM-dd HH:mm") @ExtractBy(value = "//div[@class=\'BlogStat\']/regex(\'\\d+-\\d+-\\d+\\s+\\d+:\\d+\')") private Date date;

  public static void main(String[] args) {
    JstermJsonPipleine jstermJsonPipleine = new JstermJsonPipleine(
<<<<<<< /usr/src/app/output/code4craft/termblog/794189f4401f172745bbc2ab1e17edf70a476b66/src/main/java/us/codecraft/blog/spider/OschinaBlog.java/left.java
    "/data/oschinablog/"
=======
    "/Users/yihua/codecraft/blog/json"
>>>>>>> /usr/src/app/output/code4craft/termblog/794189f4401f172745bbc2ab1e17edf70a476b66/src/main/java/us/codecraft/blog/spider/OschinaBlog.java/right.java
    );
    OOSpider.create(Site.me().setSleepTime(100).setUserAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)"), jstermJsonPipleine, OschinaBlog.class).addUrl("http://my.oschina.net/flashsword/blog").thread(1).run();
    jstermJsonPipleine.flush();
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public String getCategory() {
    return category;
  }

  public Date getDate() {
    return date;
  }

  @Override public int compareTo(OschinaBlog o) {
    return date.compareTo(o.date);
  }

  @Override public void afterProcess(Page page) {
    title = title.replaceAll("\\s+", "_");
    category = category.replaceAll("\\s+", "_");
  }
}