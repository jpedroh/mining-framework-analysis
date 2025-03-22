package com.xxl.job.controller;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.xxl.job.client.handler.HandlerRepository;
import com.xxl.job.client.util.HttpUtil;
import com.xxl.job.client.util.HttpUtil.RemoteCallBack;
import com.xxl.job.client.util.JacksonUtil;
import com.xxl.job.core.constant.Constants.JobGroupEnum;
import com.xxl.job.core.model.ReturnT;
import com.xxl.job.core.model.XxlJobLog;
import com.xxl.job.dao.IXxlJobLogDao;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller @RequestMapping(value = "/joblog") public class JobLogController {
  @Resource public IXxlJobLogDao xxlJobLogDao;

  @RequestMapping public String index(Model model, String jobGroup, String jobName) {
    model.addAttribute("jobGroup", jobGroup);
    model.addAttribute("jobName", jobName);
    model.addAttribute("JobGroupList", JobGroupEnum.values());
    return "joblog/index";
  }

  @RequestMapping(value = "/pageList") @ResponseBody public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start, @RequestParam(required = false, defaultValue = "10") int length, String jobGroup, String jobName, String filterTime) {
    Date triggerTimeStart = null;
    Date triggerTimeEnd = null;
    if (StringUtils.isNotBlank(filterTime)) {
      String[] temp = filterTime.split(" - ");
      if (temp != null && temp.length == 2) {
        try {
          triggerTimeStart = DateUtils.parseDate(temp[0], new String[] { "yyyy-MM-dd HH:mm:ss" });
          triggerTimeEnd = DateUtils.parseDate(temp[1], new String[] { "yyyy-MM-dd HH:mm:ss" });
        } catch (ParseException e) {
        }
      }
    }
    List<XxlJobLog> list = xxlJobLogDao.pageList(start, length, jobGroup, jobName, triggerTimeStart, triggerTimeEnd);
    int list_count = xxlJobLogDao.pageListCount(start, length, jobGroup, jobName, triggerTimeStart, triggerTimeEnd);
    Map<String, Object> maps = new HashMap<String, Object>();
    maps.put("recordsTotal", list_count);
    maps.put("recordsFiltered", list_count);
    maps.put("data", list);
    return maps;
  }

  @RequestMapping(value = "/save") @ResponseBody public RemoteCallBack triggerLog(int trigger_log_id, String status, String msg) {
    RemoteCallBack callBack = new RemoteCallBack();
    callBack.setStatus(RemoteCallBack.FAIL);
    XxlJobLog log = xxlJobLogDao.load(trigger_log_id);
    if (log != null) {
      log.setHandleTime(new Date());
      log.setHandleStatus(status);
      log.setHandleMsg(msg);
      xxlJobLogDao.updateHandleInfo(log);
      callBack.setStatus(RemoteCallBack.SUCCESS);
      return callBack;
    }
    return callBack;
  }

  @RequestMapping(value = "/logDetail") @ResponseBody public ReturnT<String> logDetail(int id) {
    XxlJobLog log = xxlJobLogDao.load(id);
    if (log == null) {
      return new ReturnT<String>(500, "\u53c2\u6570\u5f02\u5e38");
    }
    @SuppressWarnings(value = { "unchecked" }) Map<String, String> jobDataMap = JacksonUtil.readValue(log.getJobData(), Map.class);
    String handler_address = jobDataMap.get(HandlerRepository.HANDLER_ADDRESS);
    if (!handler_address.startsWith("http")) {
      handler_address = "http://" + handler_address + "/";
    }
    Map<String, String> reqMap = new HashMap<String, String>();
    reqMap.put(HandlerRepository.NAMESPACE, HandlerRepository.NameSpaceEnum.LOG.name());
    reqMap.put(HandlerRepository.TRIGGER_LOG_ID, String.valueOf(id));
    reqMap.put(HandlerRepository.TRIGGER_TIMESTAMP, String.valueOf(log.getTriggerTime().getTime()));
    RemoteCallBack callBack = HttpUtil.post(handler_address, reqMap);
    if (HttpUtil.RemoteCallBack.SUCCESS.equals(callBack.getStatus())) {
      return new ReturnT<String>(callBack.getMsg());
    } else {
      return new ReturnT<String>(500, callBack.getMsg());
    }
  }

  @RequestMapping(value = "/logDetailPage") public String logDetailPage(int id, Model model) {
    ReturnT<String> data = logDetail(id);
    model.addAttribute("result", data);
    return "joblog/logdetail";
  }

  @RequestMapping(value = "/logKill") @ResponseBody public ReturnT<String> logKill(int id) {
    XxlJobLog log = xxlJobLogDao.load(id);
    if (log == null) {
      return new ReturnT<String>(500, "\u53c2\u6570\u5f02\u5e38");
    }
    @SuppressWarnings(value = { "unchecked" }) Map<String, String> jobDataMap = JacksonUtil.readValue(log.getJobData(), Map.class);
    String handler_address = jobDataMap.get(HandlerRepository.HANDLER_ADDRESS);
    if (!handler_address.startsWith("http")) {
      handler_address = "http://" + handler_address + "/";
    }
    String handler_name = jobDataMap.get(HandlerRepository.HANDLER_NAME);
    Map<String, String> reqMap = new HashMap<String, String>();
    reqMap.put(HandlerRepository.NAMESPACE, HandlerRepository.NameSpaceEnum.KILL.name());
    reqMap.put(HandlerRepository.HANDLER_NAME, handler_name);
    reqMap.put(HandlerRepository.TRIGGER_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
    RemoteCallBack callBack = HttpUtil.post(handler_address, reqMap);
    if (HttpUtil.RemoteCallBack.SUCCESS.equals(callBack.getStatus())) {
      log.setHandleStatus(HttpUtil.RemoteCallBack.FAIL);
      log.setHandleMsg("\u4eba\u4e3a\u64cd\u4f5c\u4e3b\u52a8\u7ec8\u6b62");
      log.setHandleTime(new Date());
      xxlJobLogDao.updateHandleInfo(log);
      return new ReturnT<String>(callBack.getMsg());
    } else {
      return new ReturnT<String>(500, callBack.getMsg());
    }
  }
}