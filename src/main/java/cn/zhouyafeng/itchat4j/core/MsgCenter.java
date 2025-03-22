package cn.zhouyafeng.itchat4j.core;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import cn.zhouyafeng.itchat4j.utils.MsgKeywords;
import cn.zhouyafeng.itchat4j.utils.enums.MsgCodeEnum;
import cn.zhouyafeng.itchat4j.utils.enums.MsgTypeEnum;
import cn.zhouyafeng.itchat4j.utils.tools.CommonTools;

/**
 * 消息处理中心
 * 
 * @author https://github.com/yaphone
 * @date 创建时间：2017年5月14日 下午12:47:50
 * @version 1.0
 *
 */
public class MsgCenter {
  private static Logger LOG = LoggerFactory.getLogger(MsgCenter.class);

  private static Core core = Core.getInstance();

  /**
	 * 接收消息，放入队列
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年4月23日 下午2:30:48
	 * @param msgList
	 * @return
	 */
  public static JSONArray produceMsg(JSONArray msgList) {
    JSONArray result = new JSONArray();
    for (int i = 0; i < msgList.size(); i++) {
      JSONObject msg = new JSONObject();
      JSONObject m = msgList.getJSONObject(i);
      m.put("groupMsg", false);
      if (m.getString("FromUserName").contains("@@") || m.getString("ToUserName").contains("@@")) {
        if (m.getString("FromUserName").contains("@@") && !core.getGroupIdList().contains(m.getString("FromUserName"))) {
          core.getGroupIdList().add((m.getString("FromUserName")));
        } else {
          if (m.getString("ToUserName").contains("@@") && !core.getGroupIdList().contains(m.getString("ToUserName"))) {
            core.getGroupIdList().add((m.getString("ToUserName")));
          }
        }
        if (m.getString("Content").contains("<br/>")) {
          String content = m.getString("Content").substring(m.getString("Content").indexOf("<br/>") + 5);
          m.put("fasongzheID", m.getString("Content").substring(0, m.getString("Content").indexOf("<br/>") - 1));
          m.put("Content", content);
          m.put("groupMsg", true);
        }
      } else {
        CommonTools.msgFormatter(m, "Content");
      }
      if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_TEXT.getCode())) {
        if (m.getString("Url").length() != 0) {
          String regEx = "(.+?\\(.+?\\))";
          Matcher matcher = CommonTools.getMatcher(regEx, m.getString("Content"));
          String data = "Map";
          if (matcher.find()) {
            data = matcher.group(1);
          }
          msg.put("Type", "Map");
          msg.put("Text", data);
        } else {
          msg.put("Type", MsgTypeEnum.TEXT.getType());
          msg.put("Text", m.getString("Content"));
        }
        m.put("Type", msg.getString("Type"));
        m.put("Text", msg.getString("Text"));
      } else {
        if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_IMAGE.getCode()) || m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_EMOTICON.getCode())) {
          m.put("Type", MsgTypeEnum.PIC.getType());
        } else {
          if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_VOICE.getCode())) {
            m.put("Type", MsgTypeEnum.VOICE.getType());
          } else {
            if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_VERIFYMSG.getCode())) {
              m.put("Type", MsgTypeEnum.VERIFYMSG.getType());
            } else {
              if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_SHARECARD.getCode())) {
                m.put("Type", MsgTypeEnum.NAMECARD.getType());
              } else {
                if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_VIDEO.getCode()) || m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_MICROVIDEO.getCode())) {
                  m.put("Type", MsgTypeEnum.VIEDO.getType());
                } else {
                  if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_APP.getCode())) {
                  } else {
                    if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_STATUSNOTIFY.getCode())) {
                    } else {
                      if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_SYS.getCode())) {
                        m.put("Type", MsgTypeEnum.SYS.getType());
                      } else {
                        if (m.getInteger("MsgType").equals(MsgCodeEnum.MSGTYPE_RECALLED.getCode())) {
                        } else {
                          LOG.info("Useless msg");
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      LOG.info("\u6536\u5230\u6d88\u606f\u4e00\u6761\uff0c\u6765\u81ea: " + m.getString("FromUserName"));
      result.add(m);
    }
    return result;
  }

  /**
	 * 消息处理
	 * 
	 * @author https://github.com/yaphone
	 * @date 2017年5月14日 上午10:52:34
	 * @param msgHandler
	 */
  public static void handleMsg(IMsgHandlerFace msgHandler) {
    while (true) {
      if (core.getMsgList().size() > 0 && core.getMsgList().get(0).getString("Content") != null) {
        if (core.getMsgList().get(0).getString("Content").length() > 0) {
          JSONObject msg = core.getMsgList().get(0);
          if (msg.getString("Type") != null) {
            try {
              if (msg.getString("Type").equals(MsgTypeEnum.TEXT.getType())) {
                String text = msg.getString("Text");
                if (text.contains(MsgKeywords.newFriendStr)) {
                  JSONObject userInfo = msg.getJSONObject("userInfo");
                  core.getContactList().add(userInfo);
                } else {
                  String result = msgHandler.textMsgHandle(msg);
                  MessageTools.sendMsgById(result, core.getMsgList().get(0).getString("FromUserName"));
                }
              } else {
                if (msg.getString("Type").equals(MsgTypeEnum.PIC.getType())) {
                  String result = msgHandler.picMsgHandle(msg);
                  MessageTools.sendMsgById(result, core.getMsgList().get(0).getString("FromUserName"));
                } else {
                  if (msg.getString("Type").equals(MsgTypeEnum.VOICE.getType())) {
                    String result = msgHandler.voiceMsgHandle(msg);
                    MessageTools.sendMsgById(result, core.getMsgList().get(0).getString("FromUserName"));
                  } else {
                    if (msg.getString("Type").equals(MsgTypeEnum.VIEDO.getType())) {
                      String result = msgHandler.viedoMsgHandle(msg);
                      MessageTools.sendMsgById(result, core.getMsgList().get(0).getString("FromUserName"));
                    } else {
                      if (msg.getString("Type").equals(MsgTypeEnum.NAMECARD.getType())) {
                        String result = msgHandler.nameCardMsgHandle(msg);
                        MessageTools.sendMsgById(result, core.getMsgList().get(0).getString("FromUserName"));
                      } else {
                        if (msg.getString("Type").equals(MsgTypeEnum.SYS.getType())) {
                          msgHandler.sysMsgHandle(msg);
                        } else {
                          if (msg.getString("Type").equals(MsgTypeEnum.VERIFYMSG.getType())) {
                            String result = msgHandler.verifyAddFriendMsgHandle(msg);
                            MessageTools.sendMsgById(result, core.getMsgList().get(0).getJSONObject("RecommendInfo").getString("UserName"));
                          }
                        }
                      }
                    }
                  }
                }
              }
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
        core.getMsgList().remove(0);
      }
      try {
        TimeUnit.MILLISECONDS.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}