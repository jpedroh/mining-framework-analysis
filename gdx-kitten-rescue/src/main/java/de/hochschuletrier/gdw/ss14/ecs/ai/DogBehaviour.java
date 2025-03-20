package de.hochschuletrier.gdw.ss14.ecs.ai;
import java.util.ArrayList;
import com.badlogic.gdx.math.Vector2;
import de.hochschuletrier.gdw.commons.ai.behaviourtree.engine.Behaviour;
import com.badlogic.gdx.utils.Array;
import de.hochschuletrier.gdw.commons.ai.behaviourtree.nodes.*;
import de.hochschuletrier.gdw.commons.ai.behaviourtree.nodes.decorators.Invert;
import de.hochschuletrier.gdw.ss14.ecs.EntityManager;
import de.hochschuletrier.gdw.ss14.ecs.components.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DogBehaviour extends Behaviour {
  private static final Logger logger = LoggerFactory.getLogger(DogBehaviour.class);

  int dogID;

  float x, y;

  DogBlackboard bb;

  InputComponent ic;

  PhysicsComponent pc;

  Array<Integer> cat;

  PhysicsComponent cpc;

  public DogBehaviour(String name, Object localBlackboard, boolean isLooping, int dogID) {
    super(name, localBlackboard, isLooping);
    this.dogID = dogID;
    bb = (DogBlackboard) localBlackboard;
    ic = bb.em.getComponent(dogID, InputComponent.class);
    pc = bb.em.getComponent(dogID, PhysicsComponent.class);
    setName("Catch the Cat oder patroullieren");
    BaseNode root = new Selector(this);
    Sequence jageKatze = new Sequence(root);
    Sequence patroulliere = new Sequence(root);
    new DogSeesCat(jageKatze);
    new ChaseCat(jageKatze);
    Invert nichtsSehend = new Invert(patroulliere);
    new DogSeesCat(nichtsSehend);
    new Patroullieren(patroulliere);
    setLooping(true);
  }

  class KIAus extends BaseCondition {
    public KIAus(BaseNode parent) {
      super(parent);
    }

    @Override public State onEvaluate(float delta) {
      State result = State.SUCCESS;
      return result;
    }
  }

  class HundHaengt extends BaseCondition {
    float timer;

    float MAX_TIME = 2;

    Vector2 currentPos;

    Vector2 oldPos;

    boolean nochNichtGefeuert;

    PhysicsComponent pcNext;

    boolean zeitpunkt;

    public HundHaengt(BaseNode parent) {
      super(parent);
      nochNichtGefeuert = true;
      timer = MAX_TIME;
      currentPos = new Vector2();
      oldPos = new Vector2();
    }

    @Override public State onEvaluate(float delta) {
      State rueckgabe = State.FAILURE;
      currentPos = new Vector2(pc.getPosition());
      pc = bb.em.getComponent(dogID, PhysicsComponent.class);
      if (oldPos.epsilonEquals(currentPos, 1f)) {
        if (timer < 0f) {
          rueckgabe = State.SUCCESS;
        } else {
          rueckgabe = State.FAILURE;
        }
        timer -= delta;
      } else {
        timer = MAX_TIME;
        rueckgabe = State.FAILURE;
      }
      oldPos.set(currentPos);
      return rueckgabe;
    }
  }

  class DogIsChasing extends BaseCondition {
    /** gibt zurück ob der Hund am Jagen ist.*/
    boolean hundJagt;

    public DogIsChasing(BaseNode parent) {
      super(parent);
    }

    @Override public State onEvaluate(float delta) {
      DogPropertyComponent dpc = bb.em.getComponent(dogID, DogPropertyComponent.class);
      hundJagt = dpc.dogIsChasing;
      State rueckgabe = State.FAILURE;
      if (hundJagt) {
        rueckgabe = State.SUCCESS;
      } else {
        rueckgabe = State.FAILURE;
      }
      return rueckgabe;
    }
  }

  class DogSeesCat extends BaseCondition {
    public DogSeesCat(BaseNode parent) {
      super(parent);
    }

    @Override public State onEvaluate(float delta) {
      boolean hundSiehtKatze;
      EnemyComponent ec = bb.em.getComponent(dogID, EnemyComponent.class);
      hundSiehtKatze = ec.seeCat;
      State rueckgabe;
      if (hundSiehtKatze) {
        rueckgabe = State.SUCCESS;
      } else {
        rueckgabe = State.FAILURE;
      }
      return rueckgabe;
    }
  }

  class Patroullieren extends BaseTask {
    DogPropertyComponent proper;

    ArrayList<Vector2> patrolPunkte;

    int naechsterPatrolpunktIndex;

    Vector2 currentPos;

    public Patroullieren(BaseNode parent) {
      super(parent);
      naechsterPatrolpunktIndex = 0;
    }

    @Override public State onRun(float delta) {
      proper = bb.em.getComponent(dogID, DogPropertyComponent.class);
      patrolPunkte = proper.patrolspots;
      int laenge = patrolPunkte.size();
      if (laenge <= 0) {
      } else {
        ic.whereToGo = patrolPunkte.get(naechsterPatrolpunktIndex);
        currentPos = new Vector2(pc.getPosition());
        if (patrolPunkte.get(naechsterPatrolpunktIndex).epsilonEquals(currentPos, 1f)) {
          if (patrolPunkte.size() < naechsterPatrolpunktIndex + 1) {
            naechsterPatrolpunktIndex += 1;
          } else {
            naechsterPatrolpunktIndex = 0;
          }
        }
        ic.whereToGo = patrolPunkte.get(naechsterPatrolpunktIndex);
      }
      return State.SUCCESS;
    }

    @Override public void onActivate() {
    }

    @Override public void onDeactivate() {
    }
  }

  class HundInRandomRichtung extends BaseTask {
    double high, low;

    CatPhysicsComponent cpc;

    DogPhysicsComponent dpc;

    float timer;

    boolean timerLaeuft;

    float MAX_TIME = 2;

    public HundInRandomRichtung(BaseNode parent) {
      super(parent);
      timer = MAX_TIME;
      timerLaeuft = false;
      x = (float) (Math.random() * (high - low) + low);
      if (pc instanceof CatPhysicsComponent) {
        cpc = (CatPhysicsComponent) pc;
        low = cpc.height;
        high = cpc.height * 3;
      }
      if (pc instanceof DogPhysicsComponent) {
        dpc = (DogPhysicsComponent) pc;
        low = dpc.mHeight;
        high = dpc.mHeight * 3;
      }
    }

    @Override public State onRun(float delta) {
      Vector2 aktuellerZielpunkt;
      Vector2 positionDesHundes;
      Vector2 neuesZiel = new Vector2();
      DogPropertyComponent dprc = bb.em.getComponent(dogID, DogPropertyComponent.class);
      if (timerLaeuft) {
        positionDesHundes = pc.getPosition();
        aktuellerZielpunkt = ic.whereToGo;
        Vector2 entfernung;
        entfernung = new Vector2((positionDesHundes.x - aktuellerZielpunkt.x), (positionDesHundes.y - aktuellerZielpunkt.y));
        high = 10;
        low = 0;
        double zufall = (Math.random() * (high - low) + low);
        if (zufall < 5) {
          neuesZiel.x = aktuellerZielpunkt.x + entfernung.y;
          neuesZiel.y = aktuellerZielpunkt.y + entfernung.x;
        } else {
          neuesZiel.x = aktuellerZielpunkt.x - entfernung.y;
          neuesZiel.y = aktuellerZielpunkt.y - entfernung.x;
        }
        if (pc instanceof CatPhysicsComponent) {
          low = cpc.height;
          high = cpc.height * 3;
        }
        if (pc instanceof DogPhysicsComponent) {
          low = dpc.mHeight;
          high = dpc.mHeight * 3;
        }
        float verlaengerung = (float) (Math.random() * (high - low) + low);
        if (entfernung.x < entfernung.y) {
          neuesZiel.x += verlaengerung;
        } else {
          neuesZiel.y += verlaengerung;
        }
      }
      ic.whereToGo = neuesZiel;
      if (timer < 0f) {
        timer = MAX_TIME;
        ic.whereToGo = neuesZiel;
        timerLaeuft = true;
        dprc.dogIsChasing = false;
      } else {
        timerLaeuft = false;
        dprc.dogIsChasing = true;
      }
      timer -= delta;
      return State.SUCCESS;
    }

    @Override public void onActivate() {
    }

    @Override public void onDeactivate() {
    }
  }

  class ChaseCat extends BaseTask {
    public ChaseCat(BaseNode parent) {
      super(parent);
    }

    @Override public State onRun(float delta) {
      cat = bb.em.getAllEntitiesWithComponents(PlayerComponent.class, PhysicsComponent.class);
      cpc = bb.em.getComponent(cat.first(), PhysicsComponent.class);
      Vector2 pos = cpc.getPosition();
      InputComponent dog = bb.em.getComponent(dogID, InputComponent.class);
      DogPropertyComponent dpc = bb.em.getComponent(dogID, DogPropertyComponent.class);
      dpc.dogIsChasing = true;
      dog.whereToGo.set(pos);
      return State.SUCCESS;
    }

    @Override public void onActivate() {
    }

    @Override public void onDeactivate() {
    }
  }

  public static class DogBlackboard {
    EntityManager em;

    public DogBlackboard(EntityManager em) {
      this.em = em;
    }
  }
}