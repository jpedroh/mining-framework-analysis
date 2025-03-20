package org.cts.parser.prj;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cts.parser.proj.ProjKeyParameters;
import org.cts.parser.proj.ProjValueParameters;

/**
 * This class is used to get values from parameter in the prj file.
 * @author Antoine Gourlay, Erwan Bocher, Jules Party
 */
public final class PrjMatcher {
  private static final double TOL = 1.0E-100;

  private PrjMatcher() {
  }

  private Map<String, String> params = new HashMap<String, String>();

  static Map<String, String> match(PrjElement el) {
    PrjMatcher m = new PrjMatcher();
    return m.doMatch(el);
  }

  /**
     * This class is used to find the key and value in the WKT
     */
  private PrjNodeMatcher[] projCSmatchers = new PrjNodeMatcher[] { new PrjNodeMatcher() {
    @Override public String getName() {
      return PrjKeyParameters.GEOGCS;
    }

    @Override public void run(List<PrjElement> list) {
      parseGeogcs(list, false);
    }
  }, new PrjNodeMatcher() {
    @Override public String getName() {
      return PrjKeyParameters.UNIT;
    }

    @Override public void run(List<PrjElement> list) {
      parseUnit(list);
    }
  }, new PrjNodeMatcher() {
    @Override public String getName() {
      return PrjKeyParameters.PROJECTION;
    }

    @Override public void run(List<PrjElement> list) {
      parseProjection(list);
    }
  }, new PrjNodeMatcher() {
    @Override public String getName() {
      return PrjKeyParameters.PARAMETER;
    }

    @Override public void run(List<PrjElement> list) {
      parseParameter(list);
    }
  }, new PrjNodeMatcher() {
    @Override public String getName() {
      return PrjKeyParameters.AUTHORITY;
    }

    @Override public void run(List<PrjElement> list) {
      parseAuthority(list);
    }
  } };

  private Map<String, String> doMatch(PrjElement el) {
    List<PrjElement> ll = matchNode(el, PrjKeyParameters.PROJCS, false);
    if (ll == null) {
      ll = matchNode(el, PrjKeyParameters.GEOGCS);
      parseGeogcs(ll, true);
    }
    parseString(ll.get(0), PrjKeyParameters.NAME);
    for (int i = 1; i < ll.size(); i++) {
      matchAnyNode(ll.get(i), projCSmatchers);
    }
    String unit = params.remove(PrjKeyParameters.UNITVAL);
    if (unit == null || Double.valueOf(unit) - 1.0 < TOL) {
      params.put(ProjKeyParameters.units, ProjValueParameters.M);
    } else {
      params.put(ProjKeyParameters.to_meter, unit);
    }
    String auth = params.remove(PrjKeyParameters.REFAUTHORITY);
    if (auth != null) {
      String code = params.remove(PrjKeyParameters.REFCODE);
      params.put(PrjKeyParameters.REFNAME, auth + ':' + code);
    }
    if (!params.containsKey(ProjKeyParameters.proj)) {
      params.put(ProjKeyParameters.proj, ProjValueParameters.LONGLAT);
    }
    return params;
  }

  private void parseGeogcs(List<PrjElement> ll, boolean rootElement) {
    parseString(ll.get(0), PrjKeyParameters.GEOGCS);
    PrjNodeMatcher[] matchers;
    if (rootElement) {
      matchers = new PrjNodeMatcher[3];
    } else {
      matchers = new PrjNodeMatcher[2];
    }
    matchers[0] = new PrjNodeMatcher() {
      @Override public String getName() {
        return ProjKeyParameters.datum;
      }

      @Override public void run(List<PrjElement> list) {
        parseDatum(list);
      }
    };
    matchers[1] = new PrjNodeMatcher() {
      @Override public String getName() {
        return PrjKeyParameters.PRIMEM;
      }

      @Override public void run(List<PrjElement> list) {
        parsePrimeM(list);
      }
    };
    if (rootElement) {
      matchers[2] = new PrjNodeMatcher() {
        @Override public String getName() {
          return PrjKeyParameters.AUTHORITY;
        }

        @Override public void run(List<PrjElement> list) {
          parseAuthority(list);
        }
      };
    }
    for (int i = 1; i < ll.size(); i++) {
      matchAnyNode(ll.get(i), matchers);
    }
  }

  private void parseAuthority(List<PrjElement> ll) {
    parseString(ll.get(0), PrjKeyParameters.REFAUTHORITY);
    parseString(ll.get(1), PrjKeyParameters.REFCODE);
  }

  private void parseDatum(List<PrjElement> ll) {
    String datum = getString(ll.get(0));
    datum = datum.replaceAll("[^a-zA-Z0-9]", "");
    String datm = PrjValueParameters.DATUMNAMES.get(datum.toLowerCase());
    if (datm != null) {
      params.put(ProjKeyParameters.datum, datm);
    } else {
      List<PrjElement> nn = matchNode(ll.get(1), PrjKeyParameters.SPHEROID);
      String ellps = getString(nn.get(0));
      ellps = ellps.replaceAll("[^a-zA-Z0-9]", "");
      String elps = PrjValueParameters.ELLIPSOIDNAMES.get(ellps.toLowerCase());
      if (elps != null) {
        params.put(ProjKeyParameters.ellps, elps);
      } else {
        parseNumber(nn.get(1), ProjKeyParameters.a);
        parseNumber(nn.get(2), ProjKeyParameters.rf);
      }
      if (ll.size() > 2) {
        List<PrjElement> els = matchNode(ll.get(2), ProjKeyParameters.towgs84, false);
        if (els != null) {
          StringBuilder b = new StringBuilder();
          b.append(getNumber(els.get(0)));
          for (int i = 1; i < els.size(); i++) {
            b.append(',').append(getNumber(els.get(i)));
          }
          params.put(ProjKeyParameters.towgs84, b.toString());
        }
      }
    }
  }

  private void parseUnit(List<PrjElement> ll) {
    parseNumber(ll.get(1), PrjKeyParameters.UNITVAL);
  }

  private void parseProjection(List<PrjElement> ll) {
    String proj = getString(ll.get(0));
    proj = proj.replaceAll("[^a-zA-Z0-9]", "");
    String prj = PrjValueParameters.PROJNAMES.get(proj.toLowerCase());
    if (prj != null) {
      params.put(ProjKeyParameters.proj, prj);
    }
  }

  private void parsePrimeM(List<PrjElement> ll) {
    String pm = getString(ll.get(0));
    pm = pm.replaceAll("[^a-zA-Z0-9]", "");
    String prm = PrjValueParameters.PRIMEMERIDIANNAMES.get(pm.toLowerCase());
    if (prm != null) {
      params.put(ProjKeyParameters.pm, prm);
    } else {
      parseNumber(ll.get(1), ProjKeyParameters.pm);
    }
  }

  private void parseParameter(List<PrjElement> ll) {
    String param = getString(ll.get(0));
    param = param.replaceAll("[^a-zA-Z0-9]", "");
    String parm = PrjValueParameters.PARAMNAMES.get(param.toLowerCase());
    if (parm != null) {
      parseNumber(ll.get(1), parm);
    }
  }

  private void matchAnyNode(PrjElement e, PrjNodeMatcher[] nn) {
    matchAnyNode(e, nn, false);
  }

  private void matchAnyNode(PrjElement e, PrjNodeMatcher[] nn, boolean strict) {
    if (e instanceof PrjNodeElement) {
      PrjNodeElement ne = (PrjNodeElement) e;
      for (PrjNodeMatcher m : nn) {
        if (ne.getName().equalsIgnoreCase(m.getName())) {
          m.run(ne.getChildren());
          return;
        }
      }
    }
    if (strict) {
      throw new PrjParserException("Failed to parse PRJ. Found \'" + e + "\', completely unexpected!");
    }
  }

  private List<PrjElement> matchNode(PrjElement e, String name) {
    return matchNode(e, name, true);
  }

  private List<PrjElement> matchNode(PrjElement e, String name, boolean strict) {
    if (e instanceof PrjNodeElement) {
      PrjNodeElement n = (PrjNodeElement) e;
      if (n.getName().equalsIgnoreCase(name)) {
        return n.getChildren();
      }
    }
    if (strict) {
      throw new PrjParserException("Failed to parse PRJ. Found \'" + e + "\', expected PrjNodeElement[" + name + "].");
    } else {
      return null;
    }
  }

  /**
     * Return the name of the projection
     *
     * @param e
     * @param name
     */
  private void parseString(PrjElement e, String name) {
    if (e instanceof PrjStringElement) {
      PrjStringElement s = (PrjStringElement) e;
      params.put(name, s.getValue().trim());
    } else {
      throw new PrjParserException("Failed to parse PRJ. Found \'" + e + "\', expected PrjStringElement with " + name + " in it.");
    }
  }

  private String getString(PrjElement e) {
    if (e instanceof PrjStringElement) {
      PrjStringElement s = (PrjStringElement) e;
      return s.getValue().trim();
    }
    throw new PrjParserException("Failed to parse PRJ. Found \'" + e + "\', expected some PrjStringElement.");
  }

  private void parseNumber(PrjElement e, String name) {
    if (e instanceof PrjNumberElement) {
      PrjNumberElement n = (PrjNumberElement) e;
      params.put(name, String.valueOf(n.getValue()));
    } else {
      throw new PrjParserException("Failed to parse PRJ. Found \'" + e + "\', expected PrjNumberElement with " + name + " in it.");
    }
  }

  private double getNumber(PrjElement e) {
    if (e instanceof PrjNumberElement) {
      PrjNumberElement n = (PrjNumberElement) e;
      return n.getValue();
    } else {
      throw new PrjParserException("Failed to parse PRJ. Found \'" + e + "\', expected PrjNumberElement.");
    }
  }
}