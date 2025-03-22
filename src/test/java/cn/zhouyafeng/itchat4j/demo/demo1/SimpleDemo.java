package cn.zhouyafeng.itchat4j.demo.demo1;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONObject;
import cn.zhouyafeng.itchat4j.api.WechatTools;
import cn.zhouyafeng.itchat4j.core.Core;
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

  @Override public String textMsgHandle(JSONObject msg) {
    if (!msg.getBoolean("groupMsg")) {
      String text = msg.getString("Text");
      LOG.info(text);
      if (text.equals("111")) {
        WechatTools.logout();
      }
      if (text.equals("222")) {
        WechatTools.remarkNameByNickName("yaphone", "Hello");
      }
      if (text.equals("333")) {
        System.out.print(WechatTools.getGroupNickNameList());
        System.out.print(WechatTools.getGroupIdList());
        System.out.print(Core.getInstance().getGroupMemeberMap());
      }
      return text;
    }
    return null;
  }

  @Override public String picMsgHandle(JSONObject msg) {
    String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
    String picPath = "D://itchat4j/pic" + File.separator + fileName + ".jpg";
    DownloadTools.getDownloadFn(msg, MsgTypeEnum.PIC.getType(), picPath);
    return "\u56fe\u7247\u4fdd\u5b58\u6210\u529f";
  }

  @Override public String voiceMsgHandle(JSONObject msg) {
    String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
    String voicePath = "D://itchat4j/voice" + File.separator + fileName + ".mp3";
    DownloadTools.getDownloadFn(msg, MsgTypeEnum.VOICE.getType(), voicePath);
    return "\u58f0\u97f3\u4fdd\u5b58\u6210\u529f";
  }

  @Override public String viedoMsgHandle(JSONObject msg) {
    String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
    String viedoPath = "D://itchat4j/viedo" + File.separator + fileName + ".mp4";
    DownloadTools.getDownloadFn(msg, MsgTypeEnum.VIEDO.getType(), viedoPath);
    return "\u89c6\u9891\u4fdd\u5b58\u6210\u529f";
  }

  @Override public String nameCardMsgHandle(JSONObject msg) {
    return "\u6536\u5230\u540d\u7247\u6d88\u606f";
  }

  @Override public void sysMsgHandle(JSONObject msg) {
    String text = msg.getString("Content");
    LOG.info(text);
  }
}