package cn.zhouyafeng.itchat4j.demo.demo3;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import cn.zhouyafeng.itchat4j.Wechat;
import cn.zhouyafeng.itchat4j.api.WechatTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.core.Core;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import cn.zhouyafeng.itchat4j.utils.MyHttpClient;
import cn.zhouyafeng.itchat4j.utils.enums.StorageLoginInfoEnum;

/**
 * 此示例演示如何获取所有好友的头像
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年6月26日 下午11:27:46
 * @version 1.0
 *
 */
public class PicYourFriends implements IMsgHandlerFace {
  private static Logger LOG = LoggerFactory.getLogger(PicYourFriends.class);

  private static final Core core = Core.getInstance();

  private static final MyHttpClient myHttpClient = core.getMyHttpClient();

  private static final String path = "D://itchat4j//head";

  @Override public String textMsgHandle(BaseMsg msg) {
    if (!msg.isGroupMsg()) {
      String text = msg.getText();
      String baseUrl = "https://" + core.getIndexUrl();
      String skey = (String) core.getLoginInfo().get(StorageLoginInfoEnum.skey.getKey());
      if (text.equals("111")) {
        LOG.info("\u5f00\u59cb\u4e0b\u8f7d\u597d\u53cb\u5934\u50cf");
        List<JSONObject> friends = WechatTools.getContactList();
        for (int i = 0; i < friends.size(); i++) {
          JSONObject friend = friends.get(i);
          String url = baseUrl + friend.getString("HeadImgUrl") + skey;
          String headPicPath = path + File.separator + i + ".jpg";
          HttpEntity entity = myHttpClient.doGet(url, null, true, null);
          try {
            OutputStream out = new FileOutputStream(headPicPath);
            byte[] bytes = EntityUtils.toByteArray(entity);
            out.write(bytes);
            out.flush();
            out.close();
          } catch (Exception e) {
            LOG.info(e.getMessage());
          }
        }
      }
    }
    return null;
  }

  @Override public String picMsgHandle(BaseMsg msg) {
    return null;
  }

  @Override public String voiceMsgHandle(BaseMsg msg) {
    return null;
  }

  @Override public String viedoMsgHandle(BaseMsg msg) {
    return null;
  }

  @Override public String nameCardMsgHandle(BaseMsg msg) {
    return null;
  }

  @Override public void sysMsgHandle(BaseMsg msg) {
  }

  public static void main(String[] args) {
    String qrPath = "D://itchat4j//login";
    IMsgHandlerFace msgHandler = new PicYourFriends();
    Wechat wechat = new Wechat(msgHandler, qrPath);
    wechat.start();
  }

  @Override public String verifyAddFriendMsgHandle(JSONObject msg) {
    return null;
  }
}