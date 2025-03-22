package cn.zhouyafeng.itchat4j.demo.demo1;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSON;
import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum;
import cn.zhouyafeng.itchat4j.utils.tools.DownloadTools;

/**
 * 简单示例程序，收到文本信息自动回复原信息，收到图片、语音、小视频后根据路径自动保存
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年4月25日 上午12:18:09
 * @version 1.0
 *
 */
public class SimpleDemo implements IMsgHandlerFace {
  Logger LOG = Logger.getLogger(SimpleDemo.class);

  @Override public String textMsgHandle(BaseMsg msg) {
    LOG.info(JSON.toJSON(msg));
    if (!msg.isGroupMsg()) {
      String text = msg.getText();
      return text;
    }
    return null;
  }

  @Override public String picMsgHandle(BaseMsg msg) {
    LOG.info(JSON.toJSON(msg));
    String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
    String picPath = "D://itchat4j/pic" + File.separator + fileName + ".jpg";
    DownloadTools.getDownloadFn(msg, MsgTypeEnum.PIC.getType(), picPath);
    return "\u56fe\u7247\u4fdd\u5b58\u6210\u529f";
  }

  @Override public String voiceMsgHandle(BaseMsg msg) {
    LOG.info(JSON.toJSON(msg));
    String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
    String voicePath = "D://itchat4j/voice" + File.separator + fileName + ".mp3";
    DownloadTools.getDownloadFn(msg, MsgTypeEnum.VOICE.getType(), voicePath);
    return "\u58f0\u97f3\u4fdd\u5b58\u6210\u529f";
  }

  @Override public String viedoMsgHandle(BaseMsg msg) {
    LOG.info(JSON.toJSON(msg));
    String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
    String viedoPath = "D://itchat4j/viedo" + File.separator + fileName + ".mp4";
    DownloadTools.getDownloadFn(msg, MsgTypeEnum.VIEDO.getType(), viedoPath);
    return "\u89c6\u9891\u4fdd\u5b58\u6210\u529f";
  }

  @Override public String nameCardMsgHandle(BaseMsg msg) {
    LOG.info(JSON.toJSON(msg));
    return "\u6536\u5230\u540d\u7247\u6d88\u606f";
  }

  @Override public void sysMsgHandle(BaseMsg msg) {
    LOG.info(JSON.toJSON(msg));
    String text = msg.getContent();
    LOG.info(text);
  }

  @Override public String verifyAddFriendMsgHandle(JSONObject msg) {
    MessageTools.addFriend(msg, true);
    JSONObject recommendInfo = msg.getJSONObject("RecommendInfo");
    String nickName = recommendInfo.getString("NickName");
    String province = recommendInfo.getString("Province");
    String city = recommendInfo.getString("City");
    String text = "\u4f60\u597d\uff0c\u6765\u81ea" + province + city + "\u7684" + nickName + "\uff0c \u6b22\u8fce\u6dfb\u52a0\u6211\u4e3a\u597d\u53cb\uff01";
    return text;
  }
}