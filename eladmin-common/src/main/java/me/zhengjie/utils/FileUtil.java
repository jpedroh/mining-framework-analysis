package me.zhengjie.utils;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelUtil;
import me.zhengjie.exception.BadRequestException;
import org.springframework.web.multipart.MultipartFile;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * File工具类，扩展 hutool 工具包
 *
 * @author Zheng Jie
 * @date 2018-12-27
 */
public class FileUtil extends cn.hutool.core.io.FileUtil {
  /**
     * 定义GB的计算常量
     */
  private static final int GB = 1024 * 1024 * 1024;

  /**
     * 定义MB的计算常量
     */
  private static final int MB = 1024 * 1024;

  /**
     * 定义KB的计算常量
     */
  private static final int KB = 1024;

  /**
     * 格式化小数
     */
  private static final DecimalFormat DF = new DecimalFormat("0.00");

  /**
     * MultipartFile转File
     *
     * @param multipartFile
     * @return
     */
  public static File toFile(MultipartFile multipartFile) {
    String fileName = multipartFile.getOriginalFilename();
    String prefix = "." + getExtensionName(fileName);
    File file = null;
    try {
      file = File.createTempFile(IdUtil.simpleUUID(), prefix);
      multipartFile.transferTo(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return file;
  }

  /**
     * 删除
     *
     * @param files
     */
  public static void deleteFile(File... files) {
    for (File file : files) {
      if (file.exists()) {
        file.delete();
      }
    }
  }

  /**
     * 获取文件扩展名
     *
     * @param filename
     * @return
     */
  public static String getExtensionName(String filename) {
    if ((filename != null) && (filename.length() > 0)) {
      int dot = filename.lastIndexOf('.');
      if ((dot > -1) && (dot < (filename.length() - 1))) {
        return filename.substring(dot + 1);
      }
    }
    return filename;
  }

  /**
     * Java文件操作 获取不带扩展名的文件名
     *
     * @param filename
     * @return
     */
  public static String getFileNameNoEx(String filename) {
    if ((filename != null) && (filename.length() > 0)) {
      int dot = filename.lastIndexOf('.');
      if ((dot > -1) && (dot < (filename.length()))) {
        return filename.substring(0, dot);
      }
    }
    return filename;
  }

  /**
     * 文件大小转换
     *
     * @param size
     * @return
     */
  public static String getSize(long size) {
    String resultSize = "";
    if (size / GB >= 1) {
      resultSize = DF.format(size / (float) GB) + "GB   ";
    } else {
      if (size / MB >= 1) {
        resultSize = DF.format(size / (float) MB) + "MB   ";
      } else {
        if (size / KB >= 1) {
          resultSize = DF.format(size / (float) KB) + "KB   ";
        } else {
          resultSize = size + "B   ";
        }
      }
    }
    return resultSize;
  }

  /**
     * inputStream 转 File
     *
     * @param ins
     * @param name
     * @return
     * @throws Exception
     */
  public static File inputStreamToFile(InputStream ins, String name) throws Exception {
    File file = new File(System.getProperty("java.io.tmpdir") + File.separator + name);
    if (file.exists()) {
      return file;
    }
    OutputStream os = new FileOutputStream(file);
    int bytesRead = 0;
    byte[] buffer = new byte[8192];
    while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
      os.write(buffer, 0, bytesRead);
    }
    os.close();
    ins.close();
    return file;
  }

  /**
     * 将文件名解析成文件的上传路径
     *
     * @param file
     * @param filePath
     * @return 上传到服务器的文件名
     */
  public static File upload(MultipartFile file, String filePath) {
    Date date = new Date();
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmssS");
    String name = getFileNameNoEx(file.getOriginalFilename());
    String suffix = getExtensionName(file.getOriginalFilename());
    String nowStr = "-" + format.format(date);
    try {
      String fileName = name + nowStr + "." + suffix;
      String path = filePath + File.separator + fileName;
      File dest = new File(path).getCanonicalFile();
      if (!dest.getParentFile().exists()) {
        dest.getParentFile().mkdirs();
      }
      file.transferTo(dest);
      return dest;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String fileToBase64(File file) throws Exception {
    FileInputStream inputFile = new FileInputStream(file);
    String base64 = null;
    byte[] buffer = new byte[(int) file.length()];
    inputFile.read(buffer);
    inputFile.close();
    base64 = new Base64().encode(buffer);
    String encoded = base64.replaceAll("[\\s*\t\n\r]", "");
    return encoded;
  }

  /**
     * 导出excel
     *
     * @param list
     * @return
     * @throws Exception
     */
  public static void downloadExcel(List<Map<String, Object>> list, HttpServletResponse response) throws IOException {
    String tempPath = System.getProperty("java.io.tmpdir") + IdUtil.fastSimpleUUID() + ".xlsx";
    File file = new File(tempPath);
    BigExcelWriter writer = ExcelUtil.getBigWriter(file);
    writer.write(list, true);
    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
    response.setHeader("Content-Disposition", "attachment;filename=file.xlsx");
    ServletOutputStream out = response.getOutputStream();
    file.deleteOnExit();
    writer.flush(out, true);
    IoUtil.close(out);
  }

  public static String getFileType(String type) {
    String documents = "txt doc pdf ppt pps xlsx xls";
    String music = "mp3 wav wma mpa ram ra aac aif m4a";
    String video = "avi mpg mpe mpeg asf wmv mov qt rm mp4 flv m4v webm ogv ogg";
    String image = "bmp dib pcp dif wmf gif jpg tif eps psd cdr iff tga pcd mpt png jpeg";
    if (image.indexOf(type) != -1) {
      return "\u56fe\u7247";
    } else {
      if (documents.indexOf(type) != -1) {
        return "\u6587\u6863";
      } else {
        if (music.indexOf(type) != -1) {
          return "\u97f3\u4e50";
        } else {
          if (video.indexOf(type) != -1) {
            return "\u89c6\u9891";
          } else {
            return "\u5176\u4ed6";
          }
        }
      }
    }
  }

  public static String getFileTypeByMimeType(String type) {
    String mimeType = new MimetypesFileTypeMap().getContentType("." + type);
    return mimeType.split("\\/")[0];
  }

  public static void checkSize(long maxSize, long size) {
    if (size > (maxSize * 1024 * 1024)) {
      throw new BadRequestException("\u6587\u4ef6\u8d85\u51fa\u89c4\u5b9a\u5927\u5c0f");
    }
  }
}