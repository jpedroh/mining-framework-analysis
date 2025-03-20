package org.scribble.ext.go.core.codegen.statetype3;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.scribble.ast.Module;
import org.scribble.ast.ProtocolDecl;
import org.scribble.ext.go.core.model.endpoint.RPCoreEState;
import org.scribble.ext.go.core.type.RPInterval;
import org.scribble.ext.go.core.type.RPRoleVariant;
import org.scribble.ext.go.type.index.RPIndexVar;
import org.scribble.model.endpoint.EGraph;
import org.scribble.model.endpoint.EStateKind;
import org.scribble.type.kind.Global;
import org.scribble.type.name.GProtocolName;
import org.scribble.type.name.Role;
import org.scribble.util.Pair;

public class RPCoreSTSessionApiBuilder {
  private final RPCoreSTApiGenerator apigen;

  protected final Map<RPRoleVariant, Set<RPCoreEState>> reachable = new HashMap<>();

  protected final Map<RPRoleVariant, Map<Integer, String>> stateChanNames;

  private static final Comparator<RPIndexVar> IVAR_COMP = new Comparator<RPIndexVar>() {
    @Override public int compare(RPIndexVar i1, RPIndexVar i2) {
      return i1.toString().compareTo(i2.toString());
    }
  };

  private static final Comparator<RPCoreEState> ESTATE_COMP = new Comparator<RPCoreEState>() {
    @Override public int compare(RPCoreEState o1, RPCoreEState o2) {
      return new Integer(o1.id).compareTo(o2.id);
    }
  };

  public RPCoreSTSessionApiBuilder(RPCoreSTApiGenerator apigen) {
    this.apigen = apigen;
    this.stateChanNames = Collections.unmodifiableMap(makeStateChanNames().entrySet().stream().collect(Collectors.toMap((e) -> e.getKey(), (e) -> Collections.unmodifiableMap(e.getValue()))));
  }

  private Map<RPRoleVariant, Map<Integer, String>> makeStateChanNames() {
    Map<RPRoleVariant, Map<Integer, String>> names = new HashMap<>();
    for (Entry<RPRoleVariant, EGraph> e : (Iterable<Entry<RPRoleVariant, EGraph>>) this.apigen.selfs.stream().map((r) -> this.apigen.variants.get(r)).flatMap((v) -> v.entrySet().stream())::iterator) {
      int[] counter = { 2 };
      RPRoleVariant v = e.getKey();
      EGraph g = e.getValue();
      Map<Integer, String> curr = new HashMap<>();
      names.put(v, curr);
      Set<RPCoreEState> rs = RPCoreEState.getReachableStates((RPCoreEState) g.init);
      rs.add((RPCoreEState) g.init);
      this.reachable.put(v, Collections.unmodifiableSet(new HashSet<>(rs)));
      for (RPCoreEState s : new HashSet<>(rs)) {
        if (s.hasNested()) {
          RPCoreEState nested = s.getNested();
          curr.put(nested.id, "Init_" + nested.id);
          rs.remove(nested);
        }
      }
      curr.put(g.init.id, "Init");
      rs.remove(g.init);
      if (g.term != null && g.term.id != g.init.id) {
        rs.remove(g.term);
        curr.put(g.term.id, "End");
      }
      rs.forEach((s) -> {
        String n = s.isTerminal() ? (s.hasNested() ? "End_" + s.id : "End") : "State" + counter[0]++;
        curr.put(s.id, n);
      });
    }
    return names;
  }

  public Map<String, String> build() {
    Module mod = this.apigen.job.getContext().getModule(this.apigen.proto.getPrefix());
    ProtocolDecl<Global> gpd = mod.getProtocolDecl(this.apigen.proto.getSimpleName());
    Map<String, String> res = new HashMap<>();
    buildProtocolApi(gpd, res);
    buildEndpointKindApi(gpd, res);
    return res;
  }

  private void buildProtocolApi(ProtocolDecl<Global> gpd, Map<String, String> res) {
    GProtocolName simpname = this.apigen.proto.getSimpleName();
    List<Role> rolenames = this.apigen.selfs;
    String protoFile = "// Package " + this.apigen.getApiRootPackageName() + " is the generated API for the " + this.apigen.proto.getPrefix().getSimpleName().toString() + "." + this.apigen.getApiRootPackageName() + " protocol.\n" + "// Use functions in this package to create instances of role variants.\n" + "package " + this.apigen.getApiRootPackageName() + "\n" + "\n" + (rolenames.stream().map((rname) -> {
      Set<RPRoleVariant> variants = this.apigen.variants.get(rname).keySet();
      return variants.stream().map((v) -> {
        String epkindPackName = RPCoreSTApiGenerator.getEndpointKindPackageName(v);
        boolean isCommonEndpointKind = this.apigen.isCommonEndpointKind(v);
        return isCommonEndpointKind ? "import " + epkindPackName + " \"" + this.apigen.packpath + "/" + this.apigen.getApiRootPackageName() + "/" + epkindPackName + "\"\n" : this.apigen.families.keySet().stream().filter((f) -> f.left.contains(v)).map((f) -> {
          return "import " + this.apigen.getFamilyPackageName(f) + "_" + epkindPackName + " \"" + this.apigen.packpath + "/" + this.apigen.getApiRootPackageName() + "/" + this.apigen.getFamilyPackageName(f) + "/" + epkindPackName + "\"\n";
        }).collect(Collectors.joining(""));
      }).collect(Collectors.joining(""));
    }).collect(Collectors.joining("")));
    protoFile += "\n" + "// " + simpname + " is an instance of the " + this.apigen.proto.getPrefix().getSimpleName().toString() + "." + this.apigen.getApiRootPackageName() + " protocol.\n" + "type " + simpname + " struct {\n" + "}\n" + "\n" + "func (*" + simpname + ") IsProtocol() {\n" + "}\n" + "\n" + "// New returns a new instance of the protocol.\n" + "func New() *" + simpname + " {\n" + "return &" + simpname + "{ }\n" + "}\n";
    for (Role rname : rolenames) {
      for (RPRoleVariant variant : this.apigen.variants.get(rname).keySet()) {
        boolean isCommonEndpointKind = this.apigen.isCommonEndpointKind(variant);
        Set<Pair<Set<RPRoleVariant>, Set<RPRoleVariant>>> families = isCommonEndpointKind ? Stream.of((Pair<Set<RPRoleVariant>, Set<RPRoleVariant>>) null).collect(Collectors.toSet()) : this.apigen.families.keySet().stream().filter((f) -> f.left.contains(variant)).collect(Collectors.toSet());
        for (Pair<Set<RPRoleVariant>, Set<RPRoleVariant>> family : families) {
          List<RPIndexVar> ivars = getParameters(variant);
          String epkindTypeName = RPCoreSTApiGenerator.getEndpointKindTypeName(simpname, variant);
          String fnName = "New_" + (isCommonEndpointKind ? "" : this.apigen.getFamilyPackageName(family) + "_") + epkindTypeName;
          String tmp = "// " + fnName + " returns a new instance of " + epkindTypeName + " role variant.\n" + "func (p *" + simpname + ") " + fnName + "(" + ivars.stream().filter((x) -> !x.name.equals("self")).map((v) -> v + " int, ").collect(Collectors.joining("")) + "self int" + ")" + " *" + (isCommonEndpointKind ? "" : this.apigen.getFamilyPackageName(family) + "_") + RPCoreSTApiGenerator.getGeneratedRoleVariantName(variant) + "." + epkindTypeName + " {\n" + "return " + (isCommonEndpointKind ? "" : this.apigen.getFamilyPackageName(family) + "_") + RPCoreSTApiGenerator.getEndpointKindPackageName(variant) + ".New" + "(p" + ivars.stream().filter((x) -> !x.name.equals("self")).map((x) -> ", " + x).collect(Collectors.joining("")) + ", self)\n" + "}\n";
          protoFile += "\n" + tmp;
        }
      }
    }
    res.put(getProtocolFilePath() + simpname + ".go", protoFile);
  }

  private void buildEndpointKindApi(ProtocolDecl<Global> gpd, Map<String, String> res) {
    GProtocolName simpname = this.apigen.proto.getSimpleName();
    List<Role> roles = this.apigen.selfs;
    String epkindImports = "\n" + "import \"" + RPCoreSTApiGenConstants.GO_SCRIBBLERUNTIME_SESSION_PACKAGE + "\"\n" + "import \"" + RPCoreSTApiGenConstants.GO_SCRIBBLERUNTIME_TRANSPORT_PACKAGE + "\"\n";
    for (Role rname : roles) {
      for (RPRoleVariant variant : this.apigen.variants.get(rname).keySet()) {
        for (Pair<Set<RPRoleVariant>, Set<RPRoleVariant>> family : (Iterable<Pair<Set<RPRoleVariant>, Set<RPRoleVariant>>>) this.apigen.families.keySet().stream().filter((f) -> f.left.contains(variant))::iterator) {
          List<RPIndexVar> ivars = getParameters(variant);
          String epkindTypeName = RPCoreSTApiGenerator.getEndpointKindTypeName(simpname, variant);
          String epkindFile = "//" + family.left.toString() + "\n\n";
          epkindFile += epkindImports + "\n" + "type " + epkindTypeName + " struct {\n" + RPCoreSTApiGenConstants.GO_MPCHAN_PROTO + " " + RPCoreSTApiGenConstants.GO_PROTOCOL_TYPE + "\n" + "Self int\n" + "*" + RPCoreSTApiGenConstants.GO_LINEARRESOURCE_TYPE + "\n" + "lin uint64\n" + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + " *" + RPCoreSTApiGenConstants.GO_MPCHAN_TYPE + "\n" + ivars.stream().map((x) -> x + " int\n").collect(Collectors.joining("")) + "Params map[string]int\n" + this.reachable.get(variant).stream().sorted(ESTATE_COMP).flatMap((s) -> {
            String n = this.stateChanNames.get(variant).get(s.id);
            return s.hasNested() ? Stream.of(n, s.isTerminal() ? "End" : n + "_") : Stream.of(n);
          }).distinct().map((n) -> {
            return "_" + n + " *" + n + "\n";
          }).collect(Collectors.joining()) + "}\n" + "\n" + "func New(p " + RPCoreSTApiGenConstants.GO_PROTOCOL_TYPE + ", " + ivars.stream().filter((x) -> !x.name.equals("self")).map((x) -> x + " int, ").collect(Collectors.joining("")) + "self int) *" + epkindTypeName + " {\n" + "ep := &" + epkindTypeName + "{\n" + "p,\n" + "self,\n" + "&" + RPCoreSTApiGenConstants.GO_LINEARRESOURCE_TYPE + "{},\n" + "1,\n" + RPCoreSTApiGenConstants.GO_MPCHAN_CONSTRUCTOR + "(self, " + "[]string{" + roles.stream().map((x) -> "\"" + x + "\"").collect(Collectors.joining(", ")) + "}),\n" + ivars.stream().map((x) -> x + ",\n").collect(Collectors.joining("")) + "make(map[string]int),\n" + this.reachable.get(variant).stream().sorted(ESTATE_COMP).flatMap((s) -> {
            String n = this.stateChanNames.get(variant).get(s.id);
            return s.hasNested() ? Stream.of(n, s.isTerminal() ? "End" : n + "_") : Stream.of(n);
          }).distinct().map((k) -> "nil,\n").collect(Collectors.joining()) + "}\n" + this.reachable.get(variant).stream().sorted(ESTATE_COMP).flatMap((s) -> {
            String n = this.stateChanNames.get(variant).get(s.id);
            return s.hasNested() ? Stream.of(n, s.isTerminal() ? "End" : n + "_") : Stream.of(n);
          }).distinct().map((n) -> {
            return (n.equals("End")) ? "ep._End = &End{ nil, 0, ep }\n" : "ep._" + n + " = &" + n + "{ nil," + (n.equals("Init") ? " 1," : " 0,") + " ep }\n";
          }).collect(Collectors.joining()) + "return ep\n";
          epkindFile += "}\n";
          Pair<Set<RPRoleVariant>, Set<RPRoleVariant>> orig = (this.apigen.subsum.containsKey(family)) ? this.apigen.subsum.get(family) : family;
          Set<RPRoleVariant> peers = this.apigen.peers.get(variant).get(orig);
          RPRoleVariant subbdbyus = null;
          for (RPRoleVariant subbd : this.apigen.aliases.keySet()) {
            Map<Pair<Set<RPRoleVariant>, Set<RPRoleVariant>>, RPRoleVariant> ais = this.apigen.aliases.get(subbd);
            if (ais.containsKey(orig) && ais.get(orig).equals(variant)) {
              subbdbyus = subbd;
              break;
            }
          }
          for (RPRoleVariant v : peers) {
            RPRoleVariant pp = v;
            if (this.apigen.aliases.containsKey(v)) {
              Map<Pair<Set<RPRoleVariant>, Set<RPRoleVariant>>, RPRoleVariant> ali = this.apigen.aliases.get(v);
              if (ali.containsKey(orig)) {
                pp = ali.get(orig);
                if (peers.contains(pp)) {
                  continue;
                }
              }
            }
            String r = pp.getLastElement();
            String vname = RPCoreSTApiGenerator.getGeneratedRoleVariantName(pp);
            epkindFile += "\n" + "func (ini *" + epkindTypeName + ") " + vname + "_Accept(id int" + ", ss " + RPCoreSTApiGenConstants.GO_SCRIB_LISTENER_TYPE + ", sfmt " + RPCoreSTApiGenConstants.GO_FORMATTER_TYPE + ") error {\n" + "defer ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + "." + RPCoreSTApiGenConstants.GO_MPCHAN_CONN_WG + ".Done()\n" + "ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + "." + RPCoreSTApiGenConstants.GO_MPCHAN_CONN_WG + ".Add(1)\n" + "c, err := ss.Accept()\n" + "if err != nil {\n" + "return err\n" + "}\n" + "\n" + "sfmt.Wrap(c)\n" + "ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + "." + RPCoreSTApiGenConstants.GO_MPCHAN_CONN_MAP + "[\"" + r + "\"][id] = c\n" + "ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + "." + RPCoreSTApiGenConstants.GO_MPCHAN_FORMATTER_MAP + "[\"" + r + "\"][id] = sfmt\n" + "return err\n" + "}\n" + "\n" + "func (ini *" + epkindTypeName + ") " + vname + "_Dial(id int" + ", host string, port int" + ", dialler func (string, int) (" + RPCoreSTApiGenConstants.GO_SCRIB_BINARY_CHAN_TYPE + ", error)" + ", sfmt " + RPCoreSTApiGenConstants.GO_FORMATTER_TYPE + ") error {\n" + "defer ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + "." + RPCoreSTApiGenConstants.GO_MPCHAN_CONN_WG + ".Done()\n" + "ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + "." + RPCoreSTApiGenConstants.GO_MPCHAN_CONN_WG + ".Add(1)\n" + "c, err := dialler(host, port)\n" + "if err != nil {\n" + "return err\n" + "}\n" + "\n" + "sfmt.Wrap(c)\n" + "ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + "." + RPCoreSTApiGenConstants.GO_MPCHAN_CONN_MAP + "[\"" + r + "\"][id] = c\n" + "ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + "." + RPCoreSTApiGenConstants.GO_MPCHAN_FORMATTER_MAP + "[\"" + r + "\"][id] = sfmt\n" + "return err\n" + "}\n";
          }
          if (subbdbyus != null) {
            RPRoleVariant pp = subbdbyus;
            String r = pp.getLastElement();
            String vname = RPCoreSTApiGenerator.getGeneratedRoleVariantName(pp);
            epkindFile += "\n" + "func (ini *" + epkindTypeName + ") " + vname + "_Accept(id int" + ", ss " + RPCoreSTApiGenConstants.GO_SCRIB_LISTENER_TYPE + ", sfmt " + RPCoreSTApiGenConstants.GO_FORMATTER_TYPE + ") error {\n" + "defer ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + "." + RPCoreSTApiGenConstants.GO_MPCHAN_CONN_WG + ".Done()\n" + "ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + "." + RPCoreSTApiGenConstants.GO_MPCHAN_CONN_WG + ".Add(1)\n" + "c, err := ss.Accept()\n" + "if err != nil {\n" + "return err\n" + "}\n" + "\n" + "sfmt.Wrap(c)\n" + "ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + "." + RPCoreSTApiGenConstants.GO_MPCHAN_CONN_MAP + "[\"" + r + "\"][id] = c\n" + "ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + "." + RPCoreSTApiGenConstants.GO_MPCHAN_FORMATTER_MAP + "[\"" + r + "\"][id] = sfmt\n" + "return err\n" + "}\n" + "\n" + "func (ini *" + epkindTypeName + ") " + vname + "_Dial(id int" + ", host string, port int" + ", dialler func (string, int) (" + RPCoreSTApiGenConstants.GO_SCRIB_BINARY_CHAN_TYPE + ", error)" + ", sfmt " + RPCoreSTApiGenConstants.GO_FORMATTER_TYPE + ") error {\n" + "defer ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + "." + RPCoreSTApiGenConstants.GO_MPCHAN_CONN_WG + ".Done()\n" + "ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + "." + RPCoreSTApiGenConstants.GO_MPCHAN_CONN_WG + ".Add(1)\n" + "c, err := dialler(host, port)\n" + "if err != nil {\n" + "return err\n" + "}\n" + "\n" + "sfmt.Wrap(c)\n" + "ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + "." + RPCoreSTApiGenConstants.GO_MPCHAN_CONN_MAP + "[\"" + r + "\"][id] = c\n" + "ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + "." + RPCoreSTApiGenConstants.GO_MPCHAN_FORMATTER_MAP + "[\"" + r + "\"][id] = sfmt\n" + "return err\n" + "}\n";
          }
          String endName = "End";
          String init = "Init";
          epkindFile += "\n" + "func (ini *" + epkindTypeName + ") Run(f func(*" + init + ") " + endName + ") " + endName + " {\n" + "defer ini.Close()\n" + "end := f(ini.Init())\n" + "if end." + RPCoreSTApiGenConstants.GO_MPCHAN_ERR + " != nil {\n" + "panic(end." + RPCoreSTApiGenConstants.GO_MPCHAN_ERR + ")\n" + "}\n" + "return end\n" + "}";
          epkindFile += "\n\n" + "func (ini *" + epkindTypeName + ") Init() *Init {\n" + "ini.Use()\n" + "ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + ".CheckConnection()\n" + "return " + ((this.apigen.job.selectApi && this.apigen.variants.get(rname).get(variant).init.getStateKind() == EStateKind.POLY_INPUT) ? "newBranch" + init + "(ini)" : "ini._" + init) + "\n" + "}";
          epkindFile += "\n\n" + "func (ini *" + epkindTypeName + ") Close() {\n" + "defer ini." + RPCoreSTApiGenConstants.GO_MPCHAN_SESSCHAN + ".Close()\n" + "}";
          res.put(getEndpointKindFilePath(family, variant) + "/" + RPCoreSTApiGenerator.getEndpointKindTypeName(simpname, variant) + ".go", "// Generated API for the " + variant.getName() + humanReadableName(variant) + " role variant.\n" + "package " + RPCoreSTApiGenerator.getEndpointKindPackageName(variant) + "\n" + epkindFile);
        }
      }
    }
  }

  private String humanReadableName(RPRoleVariant variant) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (RPInterval iv : variant.intervals) {
      sb.append("{");
      if (iv.isSingleton()) {
        sb.append(iv.start.toString());
      } else {
        sb.append(iv.start.toString());
        sb.append(",..,");
        sb.append(iv.end.toString());
      }
      sb.append("}");
      sb.append("\u2229");
    }
    sb.deleteCharAt(sb.length() - 1);
    if (variant.cointervals.size() > 0) {
      sb.append(" - ");
      for (RPInterval iv : variant.cointervals) {
        sb.append("{");
        if (iv.isSingleton()) {
          sb.append(iv.start.toString());
        } else {
          sb.append(iv.start.toString());
          sb.append(",..,");
          sb.append(iv.end.toString());
        }
        sb.append("}");
        sb.append("\u222a");
      }
      sb.deleteCharAt(sb.length() - 1);
    }
    sb.append("]");
    return sb.toString();
  }

  private List<RPIndexVar> getParameters(RPRoleVariant variant) {
    List<RPIndexVar> ivars = this.apigen.projections.get(variant.getName()).get(variant).getIndexVars().stream().collect(Collectors.toList());
    ivars.addAll(variant.getIndexVars().stream().filter((x) -> !ivars.contains(x)).collect(Collectors.toList()));
    return ivars.stream().sorted(IVAR_COMP).collect(Collectors.toList());
  }

  public String getProtocolFilePath() {
    String basedir = this.apigen.proto.toString().replaceAll("\\.", "/") + "/";
    return basedir;
  }

  public String getEndpointKindFilePath(Pair<Set<RPRoleVariant>, Set<RPRoleVariant>> family, RPRoleVariant variant) {
    boolean isCommonEndpointKind = this.apigen.isCommonEndpointKind(variant);
    String basedir = this.apigen.proto.toString().replaceAll("\\.", "/") + "/";
    return basedir + (isCommonEndpointKind ? "" : "/" + this.apigen.getFamilyPackageName(family)) + "/" + RPCoreSTApiGenerator.getEndpointKindPackageName(variant);
  }
}