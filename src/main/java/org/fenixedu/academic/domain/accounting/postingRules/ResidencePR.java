package org.fenixedu.academic.domain.accounting.postingRules;
import java.math.BigDecimal;
import java.util.Map;
import org.fenixedu.academic.domain.accounting.EntryType;
import org.fenixedu.academic.domain.accounting.Event;
import org.fenixedu.academic.domain.accounting.EventType;
import org.fenixedu.academic.domain.accounting.ResidenceEvent;
import org.fenixedu.academic.domain.accounting.ServiceAgreementTemplate;
import org.fenixedu.academic.util.Money;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import edu.emory.mathcs.backport.java.util.Collections;

public class ResidencePR extends ResidencePR_Base {
  public ResidencePR(final DateTime startDate, final DateTime endDate, final ServiceAgreementTemplate serviceAgreementTemplate, Money penaltyPerDay) {
    super.init(EntryType.RESIDENCE_FEE, EventType.RESIDENCE_PAYMENT, startDate, endDate, serviceAgreementTemplate);
    setPenaltyPerDay(penaltyPerDay);
  }

  @Override public Map<LocalDate, Money> getDueDatePenaltyAmountMap(Event event, DateTime when) {
    ResidenceEvent residenceEvent = (ResidenceEvent) event;
    if (residenceEvent.getPaymentLimitDate().isAfter(when)) {
      return Collections.emptyMap();
    }
    final BigDecimal daysBetween = BigDecimal.valueOf(Days.daysBetween(
<<<<<<< /usr/src/app/output/fenixedu/fenixedu-academic/5b7496ed218ab8234f16d55853dd5af5f4dc86f9/src/main/java/org/fenixedu/academic/domain/accounting/postingRules/ResidencePR.java/left.java
    residenceEvent.getPaymentLimitDate().toLocalDate()
=======
    residenceEvent.getPaymentLimitDate()
>>>>>>> /usr/src/app/output/fenixedu/fenixedu-academic/5b7496ed218ab8234f16d55853dd5af5f4dc86f9/src/main/java/org/fenixedu/academic/domain/accounting/postingRules/ResidencePR.java/right.java
    , 
<<<<<<< /usr/src/app/output/fenixedu/fenixedu-academic/5b7496ed218ab8234f16d55853dd5af5f4dc86f9/src/main/java/org/fenixedu/academic/domain/accounting/postingRules/ResidencePR.java/left.java
    when.toLocalDate()
=======
    when
>>>>>>> /usr/src/app/output/fenixedu/fenixedu-academic/5b7496ed218ab8234f16d55853dd5af5f4dc86f9/src/main/java/org/fenixedu/academic/domain/accounting/postingRules/ResidencePR.java/right.java
    ).getDays());
    final Money amount = getPenaltyPerDay().multiply(daysBetween);
    return Collections.singletonMap(residenceEvent.getPaymentLimitDate().toLocalDate(), amount);
  }

  @Override protected Money doCalculationForAmountToPay(Event event, DateTime when) {
    return ((ResidenceEvent) event).getRoomValue();
  }
}