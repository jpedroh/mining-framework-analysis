package com.larvalabs.svgandroid;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;
import java.util.Stack;
import android.graphics.Shader.TileMode;

public class SVGParser {
  static final String TAG = "SVGAndroid";

  private static boolean DISALLOW_DOCTYPE_DECL = true;

  public static Path parsePath(String pathString) {
    return doPath(pathString);
  }

  static SVG parse(InputSource data, SVGHandler handler) throws SVGParseException {
    try {
      final Picture picture = new Picture();
      handler.setPicture(picture);
      SAXParserFactory spf = SAXParserFactory.newInstance();
      SAXParser sp = spf.newSAXParser();
      XMLReader xr = sp.getXMLReader();
      xr.setContentHandler(handler);
      xr.setFeature("http://xml.org/sax/features/validation", false);
      if (DISALLOW_DOCTYPE_DECL) {
        try {
          xr.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (SAXNotRecognizedException e) {
          DISALLOW_DOCTYPE_DECL = false;
        }
      }
      xr.parse(data);
      SVG result = new SVG(picture, handler.bounds);
      if (!Float.isInfinite(handler.limits.top)) {
        result.setLimits(handler.limits);
      }
      return result;
    } catch (Exception e) {
      throw new SVGParseException(e);
    }
  }

  private static NumberParse parseNumbers(String s) {
    int n = s.length();
    int p = 0;
    ArrayList<Float> numbers = new ArrayList<Float>();
    boolean skipChar = false;
    for (int i = 1; i < n; i++) {
      if (skipChar) {
        skipChar = false;
        continue;
      }
      char c = s.charAt(i);
      switch (c) {
        case 'M':
        case 'm':
        case 'Z':
        case 'z':
        case 'L':
        case 'l':
        case 'H':
        case 'h':
        case 'V':
        case 'v':
        case 'C':
        case 'c':
        case 'S':
        case 's':
        case 'Q':
        case 'q':
        case 'T':
        case 't':
        case 'a':
        case 'A':
        case ')':
        {
          String str = s.substring(p, i);
          if (str.trim().length() > 0) {
            Float f = Float.parseFloat(str);
            numbers.add(f);
          }
          p = i;
          return new NumberParse(numbers, p);
        }
        case '\n':
        case '\t':
        case ' ':
        case ',':
        case '-':
        {
          String str = s.substring(p, i);
          if (str.trim().length() > 0) {
            Float f = Float.parseFloat(str);
            numbers.add(f);
            if (c == '-') {
              p = i;
            } else {
              p = i + 1;
              skipChar = true;
            }
          } else {
            p++;
          }
          break;
        }
      }
    }
    String last = s.substring(p);
    if (last.length() > 0) {
      try {
        numbers.add(Float.parseFloat(last));
      } catch (NumberFormatException nfe) {
      }
      p = s.length();
    }
    return new NumberParse(numbers, p);
  }

  private static Matrix parseTransform(String s) {
    if (s.startsWith("matrix(")) {
      NumberParse np = parseNumbers(s.substring("matrix(".length()));
      if (np.numbers.size() == 6) {
        Matrix matrix = new Matrix();
        matrix.setValues(new float[] { np.numbers.get(0), np.numbers.get(2), np.numbers.get(4), np.numbers.get(1), np.numbers.get(3), np.numbers.get(5), 0, 0, 1 });
        return matrix;
      }
    } else {
      if (s.startsWith("translate(")) {
        NumberParse np = parseNumbers(s.substring("translate(".length()));
        if (np.numbers.size() > 0) {
          float tx = np.numbers.get(0);
          float ty = 0;
          if (np.numbers.size() > 1) {
            ty = np.numbers.get(1);
          }
          Matrix matrix = new Matrix();
          matrix.postTranslate(tx, ty);
          return matrix;
        }
      } else {
        if (s.startsWith("scale(")) {
          NumberParse np = parseNumbers(s.substring("scale(".length()));
          if (np.numbers.size() > 0) {
            float sx = np.numbers.get(0);
            float sy = sx;
            if (np.numbers.size() > 1) {
              sy = np.numbers.get(1);
            }
            Matrix matrix = new Matrix();
            matrix.postScale(sx, sy);
            return matrix;
          }
        } else {
          if (s.startsWith("skewX(")) {
            NumberParse np = parseNumbers(s.substring("skewX(".length()));
            if (np.numbers.size() > 0) {
              float angle = np.numbers.get(0);
              Matrix matrix = new Matrix();
              matrix.postSkew((float) Math.tan(angle), 0);
              return matrix;
            }
          } else {
            if (s.startsWith("skewY(")) {
              NumberParse np = parseNumbers(s.substring("skewY(".length()));
              if (np.numbers.size() > 0) {
                float angle = np.numbers.get(0);
                Matrix matrix = new Matrix();
                matrix.postSkew(0, (float) Math.tan(angle));
                return matrix;
              }
            } else {
              if (s.startsWith("rotate(")) {
                NumberParse np = parseNumbers(s.substring("rotate(".length()));
                if (np.numbers.size() > 0) {
                  float angle = np.numbers.get(0);
                  float cx = 0;
                  float cy = 0;
                  if (np.numbers.size() > 2) {
                    cx = np.numbers.get(1);
                    cy = np.numbers.get(2);
                  }
                  Matrix matrix = new Matrix();
                  matrix.postTranslate(-cx, -cy);
                  matrix.postRotate(angle);
                  matrix.postTranslate(cx, cy);
                  return matrix;
                }
              }
            }
          }
        }
      }
    }
    return null;
  }

  private static Path doPath(String s) {
    int n = s.length();
    ParserHelper ph = new ParserHelper(s, 0);
    ph.skipWhitespace();
    Path p = new Path();
    float lastX = 0;
    float lastY = 0;
    float lastX1 = 0;
    float lastY1 = 0;
    float subPathStartX = 0;
    float subPathStartY = 0;
    char prevCmd = 0;
    while (ph.pos < n) {
      char cmd = s.charAt(ph.pos);
      switch (cmd) {
        case '-':
        case '+':
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
        if (prevCmd == 'm' || prevCmd == 'M') {
          cmd = (char) (((int) prevCmd) - 1);
          break;
        } else {
          if (("lhvcsqta").indexOf(Character.toLowerCase(prevCmd)) >= 0) {
            cmd = prevCmd;
            break;
          } else {
            if (prevCmd == 'l' || prevCmd == 'L') {
              cmd = prevCmd;
              break;
            }
          }
        }
        default:
        {
          ph.advance();
          prevCmd = cmd;
        }
      }
      boolean wasCurve = false;
      switch (cmd) {
        case 'M':
        case 'm':
        {
          float x = ph.nextFloat();
          float y = ph.nextFloat();
          if (cmd == 'm') {
            subPathStartX += x;
            subPathStartY += y;
            p.rMoveTo(x, y);
            lastX += x;
            lastY += y;
          } else {
            subPathStartX = x;
            subPathStartY = y;
            p.moveTo(x, y);
            lastX = x;
            lastY = y;
          }
          break;
        }
        case 'Z':
        case 'z':
        {
          p.close();
          p.moveTo(subPathStartX, subPathStartY);
          lastX = subPathStartX;
          lastY = subPathStartY;
          lastX1 = subPathStartX;
          lastY1 = subPathStartY;
          wasCurve = true;
          break;
        }
        case 'T':
        case 't':
        case 'L':
        case 'l':
        {
          float x = ph.nextFloat();
          float y = ph.nextFloat();
          if (cmd == 'l') {
            p.rLineTo(x, y);
            lastX += x;
            lastY += y;
          } else {
            p.lineTo(x, y);
            lastX = x;
            lastY = y;
          }
          break;
        }
        case 'H':
        case 'h':
        {
          float x = ph.nextFloat();
          if (cmd == 'h') {
            p.rLineTo(x, 0);
            lastX += x;
          } else {
            p.lineTo(x, lastY);
            lastX = x;
          }
          break;
        }
        case 'V':
        case 'v':
        {
          float y = ph.nextFloat();
          if (cmd == 'v') {
            p.rLineTo(0, y);
            lastY += y;
          } else {
            p.lineTo(lastX, y);
            lastY = y;
          }
          break;
        }
        case 'C':
        case 'c':
        {
          wasCurve = true;
          float x1 = ph.nextFloat();
          float y1 = ph.nextFloat();
          float x2 = ph.nextFloat();
          float y2 = ph.nextFloat();
          float x = ph.nextFloat();
          float y = ph.nextFloat();
          if (cmd == 'c') {
            x1 += lastX;
            x2 += lastX;
            x += lastX;
            y1 += lastY;
            y2 += lastY;
            y += lastY;
          }
          p.cubicTo(x1, y1, x2, y2, x, y);
          lastX1 = x2;
          lastY1 = y2;
          lastX = x;
          lastY = y;
          break;
        }
        case 'Q':
        case 'q':
        case 'S':
        case 's':
        {
          wasCurve = true;
          float x2 = ph.nextFloat();
          float y2 = ph.nextFloat();
          float x = ph.nextFloat();
          float y = ph.nextFloat();
          if (Character.isLowerCase(cmd)) {
            x2 += lastX;
            x += lastX;
            y2 += lastY;
            y += lastY;
          }
          float x1 = 2 * lastX - lastX1;
          float y1 = 2 * lastY - lastY1;
          p.cubicTo(x1, y1, x2, y2, x, y);
          lastX1 = x2;
          lastY1 = y2;
          lastX = x;
          lastY = y;
          break;
        }
        case 'A':
        case 'a':
        {
          float rx = ph.nextFloat();
          float ry = ph.nextFloat();
          float theta = ph.nextFloat();
          int largeArc = (int) ph.nextFlag();
          int sweepArc = (int) ph.nextFlag();
          float x = ph.nextFloat();
          float y = ph.nextFloat();
          if (Character.isLowerCase(cmd)) {
            x += lastX;
            y += lastY;
          }
          drawArc(p, lastX, lastY, x, y, rx, ry, theta, largeArc, sweepArc);
          lastX = x;
          lastY = y;
          break;
        }
      }
      if (!wasCurve) {
        lastX1 = lastX;
        lastY1 = lastY;
      }
      ph.skipWhitespace();
    }
    return p;
  }

  private static void drawArc(Path p, float lastX, float lastY, float x, float y, float rx, float ry, float theta, int largeArc, int sweepArc) {
    if (rx == 0 || ry == 0) {
      p.lineTo(x, y);
      return;
    }
    if (x == lastX && y == lastY) {
      return;
    }
    rx = Math.abs(rx);
    ry = Math.abs(ry);
    final float thrad = theta * (float) Math.PI / 180;
    final float st = (float) Math.sin(thrad);
    final float ct = (float) Math.cos(thrad);
    final float xc = (lastX - x) / 2;
    final float yc = (lastY - y) / 2;
    final float x1t = ct * xc + st * yc;
    final float y1t = -st * xc + ct * yc;
    final float x1ts = x1t * x1t;
    final float y1ts = y1t * y1t;
    float rxs = rx * rx;
    float rys = ry * ry;
    float lambda = (x1ts / rxs + y1ts / rys) * 1.001f;
    if (lambda > 1) {
      float lambdasr = (float) Math.sqrt(lambda);
      rx *= lambdasr;
      ry *= lambdasr;
      rxs = rx * rx;
      rys = ry * ry;
    }
    final float R = (float) Math.sqrt((rxs * rys - rxs * y1ts - rys * x1ts) / (rxs * y1ts + rys * x1ts)) * ((largeArc == sweepArc) ? -1 : 1);
    final float cxt = R * rx * y1t / ry;
    final float cyt = -R * ry * x1t / rx;
    final float cx = ct * cxt - st * cyt + (lastX + x) / 2;
    final float cy = st * cxt + ct * cyt + (lastY + y) / 2;
    final float th1 = angle(1, 0, (x1t - cxt) / rx, (y1t - cyt) / ry);
    float dth = angle((x1t - cxt) / rx, (y1t - cyt) / ry, (-x1t - cxt) / rx, (-y1t - cyt) / ry);
    if (sweepArc == 0 && dth > 0) {
      dth -= 360;
    } else {
      if (sweepArc != 0 && dth < 0) {
        dth += 360;
      }
    }
    if ((theta % 360) == 0) {
      arcRectf.set(cx - rx, cy - ry, cx + rx, cy + ry);
      p.arcTo(arcRectf, th1, dth);
    } else {
      arcRectf.set(-rx, -ry, rx, ry);
      arcMatrix.reset();
      arcMatrix.postRotate(theta);
      arcMatrix.postTranslate(cx, cy);
      arcMatrix.invert(arcMatrix2);
      p.transform(arcMatrix2);
      p.arcTo(arcRectf, th1, dth);
      p.transform(arcMatrix);
    }
  }

  private static NumberParse getNumberParseAttr(String name, Attributes attributes) {
    int n = attributes.getLength();
    for (int i = 0; i < n; i++) {
      if (attributes.getLocalName(i).equals(name)) {
        return parseNumbers(attributes.getValue(i));
      }
    }
    return null;
  }

  private static String getStringAttr(String name, Attributes attributes) {
    int n = attributes.getLength();
    for (int i = 0; i < n; i++) {
      if (attributes.getLocalName(i).equals(name)) {
        return attributes.getValue(i);
      }
    }
    return null;
  }

  private static Float getFloatAttr(String name, Attributes attributes) {
    return getFloatAttr(name, attributes, null);
  }

  private static Float getFloatAttr(String name, Attributes attributes, Float defaultValue) {
    String v = getStringAttr(name, attributes);
    if (v == null) {
      return defaultValue;
    } else {
      if (v.endsWith("px")) {
        v = v.substring(0, v.length() - 2);
      } else {
        if (v.endsWith("%")) {
          v = v.substring(0, v.length() - 1);
          return Float.parseFloat(v) / 100;
        }
      }
    }
    return Float.parseFloat(v);
  }

  private static class NumberParse {
    private ArrayList<Float> numbers;

    private int nextCmd;

    public NumberParse(ArrayList<Float> numbers, int nextCmd) {
      this.numbers = numbers;
      this.nextCmd = nextCmd;
    }

    public int getNextCmd() {
      return nextCmd;
    }

    public float getNumber(int index) {
      return numbers.get(index);
    }
  }

  private static class Gradient {
    String id;

    String xlink;

    boolean isLinear;

    float x1, y1, x2, y2;

    float x, y, radius;

    ArrayList<Float> positions = new ArrayList<Float>();

    ArrayList<Integer> colors = new ArrayList<Integer>();

    Matrix matrix = null;

    public Gradient createChild(Gradient g) {
      Gradient child = new Gradient();
      child.id = g.id;
      child.xlink = id;
      child.isLinear = g.isLinear;
      child.x1 = g.x1;
      child.x2 = g.x2;
      child.y1 = g.y1;
      child.y2 = g.y2;
      child.x = g.x;
      child.y = g.y;
      child.radius = g.radius;
      child.positions = positions;
      child.colors = colors;
      child.matrix = matrix;
      if (g.matrix != null) {
        if (matrix == null) {
          child.matrix = g.matrix;
        } else {
          Matrix m = new Matrix(matrix);
          m.preConcat(g.matrix);
          child.matrix = m;
        }
      }
      child.boundingBox = g.boundingBox;
      child.shader = g.shader;
      child.tilemode = g.tilemode;
      return child;
    }

    public Shader shader = null;

    public boolean boundingBox = false;

    public TileMode tilemode;
  }

  private static class StyleSet {
    HashMap<String, String> styleMap = new HashMap<String, String>();

    private StyleSet(String string) {
      String[] styles = string.split(";");
      for (String s : styles) {
        String[] style = s.split(":");
        if (style.length == 2) {
          styleMap.put(style[0], style[1]);
        }
      }
    }

    public String getStyle(String name) {
      return styleMap.get(name);
    }
  }

  private static class Properties {
    StyleSet styles = null;

    Attributes atts;

    private Properties(Attributes atts) {
      this.atts = atts;
      String styleAttr = getStringAttr("style", atts);
      if (styleAttr != null) {
        styles = new StyleSet(styleAttr);
      }
    }

    public String getAttr(String name) {
      String v = null;
      if (styles != null) {
        v = styles.getStyle(name);
      }
      if (v == null) {
        v = getStringAttr(name, atts);
      }
      return v;
    }

    public String getString(String name) {
      return getAttr(name);
    }

    public Float getFloat(String name, float defaultValue) {
      Float v = getFloat(name);
      if (v == null) {
        return defaultValue;
      } else {
        return v;
      }
    }

    public Float getFloat(String name) {
      String v = getAttr(name);
      if (v == null) {
        return null;
      } else {
        try {
          return Float.parseFloat(v);
        } catch (NumberFormatException nfe) {
          return null;
        }
      }
    }

    private Integer rgb(int r, int g, int b) {
      return ((r & 0xff) << 16) | ((g & 0xff) << 8) | ((b & 0xff) << 0);
    }

    private int parseNum(String v) throws NumberFormatException {
      if (v.endsWith("%")) {
        v = v.substring(0, v.length() - 1);
        return Math.round(Float.parseFloat(v) / 100 * 255);
      }
      return Integer.parseInt(v);
    }

    public Integer getColor(String name) {
      String v = getAttr(name);
      if (v == null) {
        return null;
      } else {
        if (v.startsWith("#")) {
          try {
            int c = Integer.parseInt(v.substring(1), 16);
            if (v.length() == 4) {
              c = (c & 0x0f) * 0x11 + (c & 0xf0) * 0x110 + (c & 0xf00) * 0x1100;
            }
            return c;
          } catch (NumberFormatException nfe) {
            return null;
          }
        } else {
          if (v.startsWith("rgb(") && v.endsWith(")")) {
            String values[] = v.substring(4, v.length() - 1).split(",");
            try {
              return rgb(parseNum(values[0]), parseNum(values[1]), parseNum(values[2]));
            } catch (NumberFormatException nfe) {
              return null;
            } catch (ArrayIndexOutOfBoundsException e) {
              return null;
            }
          } else {
            return v.equalsIgnoreCase("black") ? rgb(0, 0, 0) : v.equalsIgnoreCase("green") ? rgb(0, 128, 0) : v.equalsIgnoreCase("silver") ? rgb(192, 192, 192) : v.equalsIgnoreCase("lime") ? rgb(0, 255, 0) : v.equalsIgnoreCase("gray") ? rgb(128, 128, 128) : v.equalsIgnoreCase("olive") ? rgb(128, 128, 0) : v.equalsIgnoreCase("white") ? rgb(255, 255, 255) : v.equalsIgnoreCase("yellow") ? rgb(255, 255, 0) : v.equalsIgnoreCase("maroon") ? rgb(128, 0, 0) : v.equalsIgnoreCase("navy") ? rgb(0, 0, 128) : v.equalsIgnoreCase("red") ? rgb(255, 0, 0) : v.equalsIgnoreCase("blue") ? rgb(0, 0, 255) : v.equalsIgnoreCase("purple") ? rgb(128, 0, 128) : v.equalsIgnoreCase("teal") ? rgb(0, 128, 128) : v.equalsIgnoreCase("fuchsia") ? rgb(255, 0, 255) : v.equalsIgnoreCase("aqua") ? rgb(0, 255, 255) : null;
          }
        }
      }
    }
  }

  static class SVGHandler extends DefaultHandler {
    Picture picture;

    Canvas canvas;

    Paint paint;

    RectF rect = new RectF();

    RectF bounds = null;

    RectF limits = new RectF(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

    Integer searchColor = null;

    Integer replaceColor = null;

    boolean whiteMode = false;

    HashMap<String, Gradient> gradientMap = new HashMap<String, Gradient>();

    Gradient gradient = null;

    public SVGHandler() {
      paint = new Paint();
      paint.setAntiAlias(true);
      matrixStack.push(new Matrix());
    }

    void setPicture(Picture picture) {
      this.picture = picture;
    }

    public void setColorSwap(Integer searchColor, Integer replaceColor) {
      this.searchColor = searchColor;
      this.replaceColor = replaceColor;
    }

    public void setWhiteMode(boolean whiteMode) {
      this.whiteMode = whiteMode;
    }

    @Override public void startDocument() throws SAXException {
    }

    @Override public void endDocument() throws SAXException {
    }

    private boolean doStroke(Properties atts) {
      if (whiteMode) {
        return false;
      }
      if ("none".equals(atts.getString("display"))) {
        return false;
      }
      Integer color = atts.getColor("stroke");
      if (color != null) {
        doColor(atts, color, false);
        Float width = atts.getFloat("stroke-width");
        if (width != null) {
          paint.setStrokeWidth(width);
        }
        String linecap = atts.getString("stroke-linecap");
        if ("round".equals(linecap)) {
          paint.setStrokeCap(Paint.Cap.ROUND);
        } else {
          if ("square".equals(linecap)) {
            paint.setStrokeCap(Paint.Cap.SQUARE);
          } else {
            if ("butt".equals(linecap)) {
              paint.setStrokeCap(Paint.Cap.BUTT);
            }
          }
        }
        String linejoin = atts.getString("stroke-linejoin");
        if ("miter".equals(linejoin)) {
          paint.setStrokeJoin(Paint.Join.MITER);
        } else {
          if ("round".equals(linejoin)) {
            paint.setStrokeJoin(Paint.Join.ROUND);
          } else {
            if ("bevel".equals(linejoin)) {
              paint.setStrokeJoin(Paint.Join.BEVEL);
            }
          }
        }
        paint.setStyle(Paint.Style.STROKE);
        return true;
      }
      return false;
    }

    private Gradient doGradient(boolean isLinear, Attributes atts) {
      Gradient gradient = new Gradient();
      gradient.id = getStringAttr("id", atts);
      gradient.isLinear = isLinear;
      if (isLinear) {
        gradient.x1 = getFloatAttr("x1", atts, 0f);
        gradient.x2 = getFloatAttr("x2", atts, 1f);
        gradient.y1 = getFloatAttr("y1", atts, 0f);
        gradient.y2 = getFloatAttr("y2", atts, 0f);
      } else {
        gradient.x = getFloatAttr("cx", atts, 0f);
        gradient.y = getFloatAttr("cy", atts, 0f);
        gradient.radius = getFloatAttr("r", atts, 0f);
      }
      String transform = getStringAttr("gradientTransform", atts);
      if (transform != null) {
        gradient.matrix = parseTransform(transform);
      }
      String spreadMethod = getStringAttr("spreadMethod", atts);
      if (spreadMethod == null) {
        spreadMethod = "pad";
      }
      gradient.tilemode = (spreadMethod.equals("reflect")) ? Shader.TileMode.MIRROR : (spreadMethod.equals("repeat")) ? Shader.TileMode.REPEAT : Shader.TileMode.CLAMP;
      String unit = getStringAttr("gradientUnits", atts);
      if (unit == null) {
        unit = "objectBoundingBox";
      }
      gradient.boundingBox = !unit.equals("userSpaceOnUse");
      String xlink = getStringAttr("href", atts);
      if (xlink != null) {
        if (xlink.startsWith("#")) {
          xlink = xlink.substring(1);
        }
        gradient.xlink = xlink;
      }
      return gradient;
    }

    private void doColor(Properties atts, Integer color, boolean fillMode) {
      int c = (0xFFFFFF & color) | 0xFF000000;
      if (searchColor != null && searchColor.intValue() == c) {
        c = replaceColor;
      }
      paint.setShader(null);
      paint.setColor(c);
      Float opacity = atts.getFloat("opacity");
      if (opacity == null) {
        opacity = atts.getFloat(fillMode ? "fill-opacity" : "stroke-opacity");
      }
      if (opacity == null) {
        paint.setAlpha(255);
      } else {
        paint.setAlpha((int) (255 * opacity));
      }
    }

    private boolean hidden = false;

    private int hiddenLevel = 0;

    private boolean boundsMode = false;

    private void pushTransform(Attributes atts) {
      final String transform = getStringAttr("transform", atts);
      boolean pushed = transform != null;
      transformStack.push(pushed);
      if (pushed) {
        final Matrix matrix = parseTransform(transform);
        canvas.save();
        canvas.concat(matrix);
        matrix.postConcat(matrixStack.peek());
        matrixStack.push(matrix);
      }
    }

    private void popTransform() {
      if (transformStack.pop()) {
        canvas.restore();
        matrixStack.pop();
      }
    }

    @Override public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
      paint.setAlpha(255);
      if (boundsMode) {
        if (localName.equals("rect")) {
          Float x = getFloatAttr("x", atts);
          if (x == null) {
            x = 0f;
          }
          Float y = getFloatAttr("y", atts);
          if (y == null) {
            y = 0f;
          }
          Float width = getFloatAttr("width", atts);
          Float height = getFloatAttr("height", atts);
          bounds = new RectF(x, y, x + width, y + width);
        }
        return;
      }
      if (localName.equals("svg")) {
        int width = (int) Math.ceil(getFloatAttr("width", atts));
        int height = (int) Math.ceil(getFloatAttr("height", atts));
        canvas = picture.beginRecording(width, height);
      } else {
        if (localName.equals("defs")) {
        } else {
          if (localName.equals("linearGradient")) {
            gradient = doGradient(true, atts);
          } else {
            if (localName.equals("radialGradient")) {
              gradient = doGradient(false, atts);
            } else {
              if (localName.equals("stop")) {
                if (gradient != null) {
                  Properties props = new Properties(atts);
                  float offset = props.getFloat("offset", 0);
                  int color = props.getColor("stop-color");
                  float alpha = props.getFloat("stop-opacity", 1);
                  int alphaInt = Math.round(255 * alpha);
                  color |= (alphaInt << 24);
                  gradient.positions.add(offset);
                  gradient.colors.add(color);
                }
              } else {
                if (localName.equals("g")) {
                  if ("bounds".equalsIgnoreCase(getStringAttr("id", atts))) {
                    boundsMode = true;
                  }
                  if (hidden) {
                    hiddenLevel++;
                  }
                  if ("none".equals(getStringAttr("display", atts))) {
                    if (!hidden) {
                      hidden = true;
                      hiddenLevel = 1;
                    }
                  }
                  pushTransform(atts);
                } else {
                  if (!hidden && localName.equals("rect")) {
                    Float x = getFloatAttr("x", atts);
                    if (x == null) {
                      x = 0f;
                    }
                    Float y = getFloatAttr("y", atts);
                    if (y == null) {
                      y = 0f;
                    }
                    Float width = getFloatAttr("width", atts);
                    Float height = getFloatAttr("height", atts);
                    pushTransform(atts);
                    Properties props = new Properties(atts);
                    rect.set(x, y, x + width, y + height);
                    if (doFill(props, rect)) {
                      canvas.drawRect(rect, paint);
                      doLimits(rect);
                    }
                    if (doStroke(props)) {
                      canvas.drawRect(rect, paint);
                      doLimits(rect, paint);
                    }
                    popTransform();
                  } else {
                    if (!hidden && localName.equals("line")) {
                      Float x1 = getFloatAttr("x1", atts);
                      Float x2 = getFloatAttr("x2", atts);
                      Float y1 = getFloatAttr("y1", atts);
                      Float y2 = getFloatAttr("y2", atts);
                      Properties props = new Properties(atts);
                      if (doStroke(props)) {
                        pushTransform(atts);
                        rect.set(x1, y1, x2, y2);
                        canvas.drawLine(x1, y1, x2, y2, paint);
                        doLimits(rect, paint);
                        popTransform();
                      }
                    } else {
                      if (!hidden && (localName.equals("circle") || localName.equals("ellipse"))) {
                        Float centerX = getFloatAttr("cx", atts), centerY, radiusX, radiusY;
                        centerX = getFloatAttr("cx", atts);
                        centerY = getFloatAttr("cy", atts);
                        if (localName.equals("ellipse")) {
                          radiusX = getFloatAttr("rx", atts);
                          radiusY = getFloatAttr("ry", atts);
                        } else {
                          radiusX = radiusY = getFloatAttr("r", atts);
                        }
                        if (centerX != null && centerY != null && radiusX != null && radiusY != null) {
                          pushTransform(atts);
                          Properties props = new Properties(atts);
                          rect.set(centerX - radiusX, centerY - radiusY, centerX + radiusX, centerY + radiusY);
                          if (doFill(props, rect)) {
                            canvas.drawOval(rect, paint);
                            doLimits(rect);
                          }
                          if (doStroke(props)) {
                            canvas.drawOval(rect, paint);
                            doLimits(rect, paint);
                          }
                          popTransform();
                        }
                      } else {
                        if (!hidden && (localName.equals("polygon") || localName.equals("polyline"))) {
                          NumberParse numbers = getNumberParseAttr("points", atts);
                          if (numbers != null) {
                            Path p = new Path();
                            ArrayList<Float> points = numbers.numbers;
                            if (points.size() > 1) {
                              pushTransform(atts);
                              Properties props = new Properties(atts);
                              p.moveTo(points.get(0), points.get(1));
                              for (int i = 2; i < points.size(); i += 2) {
                                float x = points.get(i);
                                float y = points.get(i + 1);
                                p.lineTo(x, y);
                              }
                              if (localName.equals("polygon")) {
                                p.close();
                              }
                              p.computeBounds(rect, false);
                              if (doFill(props, rect)) {
                                canvas.drawPath(p, paint);
                                doLimits(rect);
                              }
                              if (doStroke(props)) {
                                canvas.drawPath(p, paint);
                                doLimits(rect, paint);
                              }
                              popTransform();
                            }
                          }
                        } else {
                          if (!hidden && localName.equals("path")) {
                            Path p = doPath(getStringAttr("d", atts));
                            pushTransform(atts);
                            Properties props = new Properties(atts);
                            p.computeBounds(rect, false);
                            if (doFill(props, rect)) {
                              canvas.drawPath(p, paint);
                              doLimits(rect);
                            }
                            if (doStroke(props)) {
                              canvas.drawPath(p, paint);
                              doLimits(rect, paint);
                            }
                            popTransform();
                          } else {
                            if (!hidden) {
                              Log.d(TAG, "UNRECOGNIZED SVG COMMAND: " + localName);
                            } else {
                              if (!hidden) {
                                Log.d(TAG, "UNRECOGNIZED SVG COMMAND: " + localName);
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
          }
        }
      }
    }

    @Override public void characters(char[] ch, int start, int length) {
    }

    @Override public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
      if (localName.equals("svg")) {
        picture.endRecording();
      } else {
        if (localName.equals("linearGradient") || localName.equals("radialGradient")) {
          if (gradient.id != null) {
            if (gradient.xlink != null) {
              Gradient parent = gradientMap.get(gradient.xlink);
              if (parent != null) {
                gradient = parent.createChild(gradient);
              }
            }
            int[] colors = new int[gradient.colors.size()];
            for (int i = 0; i < colors.length; i++) {
              colors[i] = gradient.colors.get(i);
            }
            float[] positions = new float[gradient.positions.size()];
            for (int i = 0; i < positions.length; i++) {
              positions[i] = gradient.positions.get(i);
            }
            if (colors.length == 0) {
              Log.d("BAD", "BAD");
            }
            if (localName.equals("linearGradient")) {
              gradient.shader = new LinearGradient(gradient.x1, gradient.y1, gradient.x2, gradient.y2, colors, positions, gradient.tilemode);
            } else {
              gradient.shader = new RadialGradient(gradient.x, gradient.y, gradient.radius, colors, positions, gradient.tilemode);
            }
            gradientMap.put(gradient.id, gradient);
          }
        } else {
          if (localName.equals("g")) {
            if (boundsMode) {
              boundsMode = false;
            }
            if (hidden) {
              hiddenLevel--;
              if (hiddenLevel == 0) {
                hidden = false;
              }
            }
            popTransform();
          } else {
            if (localName.equals("g")) {
              if (boundsMode) {
                boundsMode = false;
              }
              if (hidden) {
                hiddenLevel--;
                if (hiddenLevel == 0) {
                  hidden = false;
                }
              }
              gradientMap.clear();
            }
          }
        }
      }
    }

    Stack<Boolean> transformStack = new Stack<Boolean>();

    Stack<Matrix> matrixStack = new Stack<Matrix>();

    private final Matrix gradMatrix = new Matrix();

    private boolean doFill(Properties atts, RectF bounding_box) {
      if ("none".equals(atts.getString("display"))) {
        return false;
      }
      if (whiteMode) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFFFFFFF);
        return true;
      }
      String fillString = atts.getString("fill");
      if (fillString != null && fillString.startsWith("url(#")) {
        String id = fillString.substring("url(#".length(), fillString.length() - 1);
        Gradient g = gradientMap.get(id);
        Shader shader = null;
        if (g != null) {
          shader = g.shader;
        }
        if (shader != null) {
          paint.setShader(shader);
          paint.setStyle(Paint.Style.FILL);
          gradMatrix.set(g.matrix);
          if (g.boundingBox) {
            gradMatrix.preTranslate(bounding_box.left, bounding_box.top);
            gradMatrix.preScale(bounding_box.width(), bounding_box.height());
          }
          shader.setLocalMatrix(gradMatrix);
          return true;
        } else {
          return false;
        }
      } else {
        paint.setShader(null);
        Integer color = atts.getColor("fill");
        if (color != null) {
          doColor(atts, color, true);
          paint.setStyle(Paint.Style.FILL);
          return true;
        } else {
          if (atts.getString("fill") == null && atts.getString("stroke") == null) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xFF000000);
            return true;
          }
        }
      }
      return false;
    }

    private void doLimits2(float x, float y) {
      if (x < limits.left) {
        limits.left = x;
      }
      if (x > limits.right) {
        limits.right = x;
      }
      if (y < limits.top) {
        limits.top = y;
      }
      if (y > limits.bottom) {
        limits.bottom = y;
      }
    }

    final private RectF limitRect = new RectF();

    private void doLimits(RectF box, Paint paint) {
      Matrix m = matrixStack.peek();
      m.mapRect(limitRect, box);
      float width2 = (paint == null) ? 0 : paint.getStrokeWidth() / 2;
      doLimits2(limitRect.left - width2, limitRect.top - width2);
      doLimits2(limitRect.right + width2, limitRect.bottom + width2);
    }

    private void doLimits(RectF box) {
      doLimits(box, null);
    }
  }

  private static float angle(float x1, float y1, float x2, float y2) {
    return (float) Math.toDegrees(Math.atan2(x1, y1) - Math.atan2(x2, y2)) % 360;
  }

  private static final RectF arcRectf = new RectF();

  private static final Matrix arcMatrix = new Matrix();

  private static final Matrix arcMatrix2 = new Matrix();
}