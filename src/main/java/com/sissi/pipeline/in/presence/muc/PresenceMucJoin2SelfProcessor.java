package com.sissi.pipeline.in.presence.muc;
import com.sissi.context.JID;
import com.sissi.context.JIDContext;
import com.sissi.pipeline.in.ProxyProcessor;
import com.sissi.protocol.Protocol;
import com.sissi.protocol.muc.Item;
import com.sissi.protocol.muc.XUser;
import com.sissi.protocol.presence.Presence;
import com.sissi.ucenter.MucGroupContext;
import com.sissi.ucenter.MucStatusCollector;
import com.sissi.ucenter.Relation;
import com.sissi.ucenter.RelationMuc;
import com.sissi.ucenter.RelationMucMapping;

/**
 * @author kim 2014年2月11日
 */
public class PresenceMucJoin2SelfProcessor extends ProxyProcessor {
  private final RelationMucMapping relationMucMapping;

  private final MucStatusCollector mucStatusCollector;

  private final MucGroupContext mucGroupContext;

  public PresenceMucJoin2SelfProcessor(RelationMucMapping relationMucMapping, MucStatusCollector mucStatusCollector, MucGroupContext mucGroupContext) {
    super();
    this.relationMucMapping = relationMucMapping;
    this.mucStatusCollector = mucStatusCollector;
    this.mucGroupContext = mucGroupContext;
  }

  @Override public boolean input(JIDContext context, Protocol protocol) {
    JID group = super.build(protocol.getTo());
    Presence presence = new Presence();
    for (Relation each : super.myRelations(group)) {
      RelationMuc muc = RelationMuc.class.cast(each);
      context.write(presence.clear().add(new XUser(context.jid().asString()).
<<<<<<< /usr/src/app/output/kimshen/sissi/c00c2e6554f121dc4bbb4fb2549738e1b1ec639f/src/main/java/com/sissi/pipeline/in/presence/muc/PresenceMucJoin2SelfProcessor.java/left.java
      setItem(new Item(group, muc, this.mucGroupContext).setJid(super.build(muc.getJID())), this.mucStatusCollector)
=======
      add(new Item(group, muc, this.mucGroupContext).setJid(super.build(muc.getJID())))
>>>>>>> /usr/src/app/output/kimshen/sissi/c00c2e6554f121dc4bbb4fb2549738e1b1ec639f/src/main/java/com/sissi/pipeline/in/presence/muc/PresenceMucJoin2SelfProcessor.java/right.java
      ).clauses(super.findOne(this.relationMucMapping.mapping(group.resource(muc.getName()))).status().clauses()).setFrom(group));
    }
    return true;
  }
}