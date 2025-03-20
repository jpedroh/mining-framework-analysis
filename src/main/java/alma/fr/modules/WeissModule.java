package alma.fr.modules;
import java.math.BigInteger;
import alma.fr.basecomponents.BaseSimple;
import alma.fr.basecomponents.Basebase;
import alma.fr.basecomponents.IBase;
import alma.fr.strategiescomponents.BeginningBoundaryIdProvider;
import alma.fr.strategiescomponents.IIdProviderStrategy;
import alma.fr.strategiescomponents.boundary.BoundaryValue;
import alma.fr.strategiescomponents.boundary.ConstantBoundary;
import alma.fr.strategiescomponents.boundary.IBoundary;
import alma.fr.strategychoicecomponents.IStrategyChoice;
import alma.fr.strategychoicecomponents.SingleStrategyChoice;
import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Simple Weiss module with a boundary+ ; base 1024 ; boundary 10;
 */
public class WeissModule implements Module {
  public void configure(Binder binder) {
    BigInteger baseBase = 
<<<<<<< /usr/src/app/output/chat-wane/lseq/916d8a5efc2654df3f31715ed05eea52f7d09957/src/main/java/alma/fr/modules/WeissModule.java/left.java
    new Integer(64)
=======
    new BigInteger("2").pow(64)
>>>>>>> /usr/src/app/output/chat-wane/lseq/916d8a5efc2654df3f31715ed05eea52f7d09957/src/main/java/alma/fr/modules/WeissModule.java/right.java
    ;
    BigInteger boundary = new BigInteger("1000000");
    binder.bind(BigInteger.class).annotatedWith(Basebase.class).toInstance(baseBase);
    binder.bind(IBase.class).to(BaseSimple.class);
    binder.bind(IBoundary.class).to(ConstantBoundary.class);
    binder.bind(BigInteger.class).annotatedWith(BoundaryValue.class).toInstance(boundary);
    binder.bind(IIdProviderStrategy.class).to(BeginningBoundaryIdProvider.class);
    binder.bind(IStrategyChoice.class).to(SingleStrategyChoice.class);
  }
}