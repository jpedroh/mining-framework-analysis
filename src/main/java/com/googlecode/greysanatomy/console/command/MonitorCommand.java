package com.googlecode.greysanatomy.console.command;
import com.googlecode.greysanatomy.agent.GreysAnatomyClassFileTransformer.TransformResult;
import com.googlecode.greysanatomy.console.command.annotation.*;
import com.googlecode.greysanatomy.console.command.annotation.RiscCmd;
import com.googlecode.greysanatomy.console.command.annotation.RiscIndexArg;
import com.googlecode.greysanatomy.console.command.annotation.RiscNamedArg;
import com.googlecode.greysanatomy.console.server.ConsoleServer;
import com.googlecode.greysanatomy.probe.Advice;
import com.googlecode.greysanatomy.probe.AdviceListenerAdapter;
import com.googlecode.greysanatomy.util.GaStringUtils;
import org.apache.commons.lang.StringUtils;
import java.lang.instrument.Instrumentation;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import static com.googlecode.greysanatomy.agent.GreysAnatomyClassFileTransformer.transform;
import static com.googlecode.greysanatomy.console.server.SessionJobsHolder.registJob;
import static com.googlecode.greysanatomy.probe.ProbeJobs.activeJob;

/**
 * �����������<br/>
 * ��������ݸ�ʽΪ:<br/>
 * <style type="text/css">
 * table, th, td {
 * border:1px solid #cccccc;
 * border-collapse:collapse;
 * }
 * </style>
 * <table>
 * <tr>
 * <th>ʱ���</th>
 * <th>ͳ������(s)</th>
 * <th>��ȫ·��</th>
 * <th>������</th>
 * <th>�����ܴ���</th>
 * <th>�ɹ�����</th>
 * <th>ʧ�ܴ���</th>
 * <th>ƽ����ʱ(ms)</th>
 * <th>ʧ����</th>
 * </tr>
 * <tr>
 * <td>2012-11-07 05:00:01</td>
 * <td>120</td>
 * <td>com.taobao.item.ItemQueryServiceImpl</td>
 * <td>queryItemForDetail</td>
 * <td>1500</td>
 * <td>1000</td>
 * <td>500</td>
 * <td>15</td>
 * <td>30%</td>
 * </tr>
 * <tr>
 * <td>2012-11-07 05:00:01</td>
 * <td>120</td>
 * <td>com.taobao.item.ItemQueryServiceImpl</td>
 * <td>queryItemById</td>
 * <td>900</td>
 * <td>900</td>
 * <td>0</td>
 * <td>7</td>
 * <td>0%</td>
 * </tr>
 * </table>
 *
 * @author vlinux
 */
@RiscCmd(named = "monitor", sort = 5, desc = "Buried point method for monitoring the operation.") public class MonitorCommand extends Command {
  @RiscIndexArg(index = 0, name = "class-regex", description = "regex match of classpath.classname") private String classRegex;

  @RiscIndexArg(index = 1, name = "method-regex", description = "regex match of methodname") private String methodRegex;

  @RiscNamedArg(named = "c", hasValue = true, description = "the cycle of output") private int cycle = 120;

  private Timer timer;

  private ConcurrentHashMap<Key, AtomicReference<Data>> monitorDatas = new ConcurrentHashMap<Key, AtomicReference<Data>>();

  private static class Key {
    private final String className;

    private final String behaviorName;

    private Key(String className, String behaviorName) {
      this.className = className;
      this.behaviorName = behaviorName;
    }

    @Override public int hashCode() {
      return className.hashCode() + behaviorName.hashCode();
    }

    @Override public boolean equals(Object obj) {
      if (null == obj || !(obj instanceof Key)) {
        return false;
      }
      Key okey = (Key) obj;
      return StringUtils.equals(okey.className, className) && StringUtils.equals(okey.behaviorName, behaviorName);
    }
  }

  private static class Data {
    private int total;

    private int success;

    private int failed;

    private long cost;
  }

  @Override public Action getAction() {
    return new Action() {
      @Override public void action(final ConsoleServer consoleServer, final Info info, final Sender sender) throws Throwable {
        final Instrumentation inst = info.getInst();
        final TransformResult result = transform(inst, classRegex, methodRegex, new AdviceListenerAdapter() {
          private final ThreadLocal<Long> beginTimestamp = new ThreadLocal<Long>();

          @Override public void onBefore(Advice p) {
            beginTimestamp.set(System.currentTimeMillis());
          }

          @Override public void onFinish(Advice p) {
            final Long startTime = beginTimestamp.get();
            if (null == startTime) {
              return;
            }
            final long cost = System.currentTimeMillis() - startTime;
            final Key key = new Key(p.getTarget().getTargetClassName(), p.getTarget().getTargetBehaviorName());
            while (true) {
              AtomicReference<Data> value = monitorDatas.get(key);
              if (null == value) {
                monitorDatas.putIfAbsent(key, new AtomicReference<Data>(new Data()));
                continue;
              }
              while (true) {
                Data oData = value.get();
                Data nData = new Data();
                nData.cost = oData.cost + cost;
                if (p.isThrowException()) {
                  nData.failed = oData.failed + 1;
                }
                if (p.isReturn()) {
                  nData.success = oData.success + 1;
                }
                nData.total = oData.total + 1;
                if (value.compareAndSet(oData, nData)) {
                  break;
                }
              }
              break;
            }
          }

          @Override public void create() {
            timer = new Timer("Timer-for-greys-monitor-" + info.getJobId(), true);
            timer.scheduleAtFixedRate(new TimerTask() {
              @Override public void run() {
                if (monitorDatas.isEmpty()) {
                  return;
                }
                final String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                final StringBuilder monitorSB = new StringBuilder();
                final Iterator<Map.Entry<Key, AtomicReference<Data>>> it = monitorDatas.entrySet().iterator();
                while (it.hasNext()) {
                  final Map.Entry<Key, AtomicReference<Data>> entry = it.next();
                  final AtomicReference<Data> value = entry.getValue();
                  Data data = null;
                  while (true) {
                    data = value.get();
                    if (value.compareAndSet(data, new Data())) {
                      break;
                    }
                  }
                  if (null != data) {
                    monitorSB.append(timestamp).append("\t");
                    monitorSB.append(entry.getKey().className).append("\t");
                    monitorSB.append(entry.getKey().behaviorName).append("\t");
                    monitorSB.append(data.total).append("\t");
                    monitorSB.append(data.success).append("\t");
                    monitorSB.append(data.failed).append("\t");
                    final DecimalFormat df = new DecimalFormat("0.00");
                    monitorSB.append(df.format(div(data.cost, data.total))).append("\t");
                    monitorSB.append(df.format(100.0d * div(data.failed, data.total))).append("%");
                    monitorSB.append("\n");
                  }
                }
                sender.send(false, tableFormat(monitorSB.toString()));
              }
            }, 0, cycle * 1000);
          }

          /**
                     * �ƹ�0�ĳ���
                     * @param a
                     * @param b
                     * @return
                     */
          private double div(double a, double b) {
            if (b == 0) {
              return 0;
            }
            return a / b;
          }

          @Override public void destroy() {
            if (null != timer) {
              timer.cancel();
            }
          }
        }, info);
        registJob(info.getSessionId(), result.getId());
        activeJob(result.getId());
        final StringBuilder message = new StringBuilder();
        message.append(GaStringUtils.LINE);
        message.append(String.format("done. probe:c-Cnt=%s,m-Cnt=%s\n", result.getModifiedClasses().size(), result.getModifiedBehaviors().size()));
        message.append(GaStringUtils.ABORT_MSG).append("\n");
        sender.send(false, message.toString());
      }
    };
  }

  /**
     * ����ʽ��
     *
     * @param output
     * @return
     */
  private String tableFormat(String output) {
    final StringBuilder outputSB = new StringBuilder();
    final List<String> outputs = new ArrayList<String>();
    final List<StringBuilder> lines = new ArrayList<StringBuilder>();
    final StringBuilder titleSB = new StringBuilder();
    final List<String[]> datas = new ArrayList<String[]>();
    final int[] colMaxWidths = new int[] { 9, 5, 8, 5, 7, 4, 2, 9 };
    datas.add(new String[] { "timestamp", "class", "behavior", "total", "success", "fail", "rt", "fail-rate" });
    lines.add(new StringBuilder());
    final Scanner scan = new Scanner(output);
    while (scan.hasNextLine()) {
      final String[] strs = scan.nextLine().split("\\s+");
      final String[] cols = new String[] { strs[0] + " " + strs[1], strs[2], strs[3], strs[4], strs[5], strs[6], strs[7], strs[8] };
      for (int c = 0; c < 8; c++) {
        colMaxWidths[c] = Math.max(colMaxWidths[c], cols[c].length());
      }
      datas.add(cols);
      lines.add(new StringBuilder());
    }
    for (int c = 0; c < 8; c++) {
      titleSB.append("+");
      GaStringUtils.rightFill(titleSB, colMaxWidths[c] + 2, "-");
    }
    titleSB.append("+").append("\n");
    for (int i = 0; i < lines.size(); i++) {
      final StringBuilder lineSB = lines.get(i);
      final String[] cols = datas.get(i);
      for (int c = 0; c < 8; c++) {
        lineSB.append("|");
        int diff = colMaxWidths[c] - cols[c].length() + 1;
        GaStringUtils.rightFill(lineSB, diff, " ");
        lineSB.append(cols[c]).append(" ");
      }
      lineSB.append("|");
      outputs.add(lineSB.toString());
    }
    outputSB.append(titleSB.toString());
    boolean isTitle = true;
    for (String o : outputs) {
      outputSB.append(o).append("\n");
      if (isTitle) {
        outputSB.append(titleSB.toString());
        isTitle = false;
      }
    }
    outputSB.append(titleSB.toString());
    return outputSB.toString();
  }
}