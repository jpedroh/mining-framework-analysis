package org.fenixedu.academic.domain.accounting.postingRules;
import java.util.Optional;
import org.fenixedu.academic.domain.accounting.EntryType;
import org.fenixedu.academic.domain.accounting.Event;
import org.fenixedu.academic.domain.accounting.EventType;
import org.fenixedu.academic.domain.accounting.ServiceAgreementTemplate;
import org.fenixedu.academic.domain.accounting.events.administrativeOfficeFee.IAdministrativeOfficeFeeEvent;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.util.Money;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.YearMonthDay;

public class AdministrativeOfficeFeePR extends AdministrativeOfficeFeePR_Base implements IAdministrativeOfficeFeeAndInsurancePR {
  protected AdministrativeOfficeFeePR() {
    super();
  }

  public AdministrativeOfficeFeePR(DateTime startDate, DateTime endDate, ServiceAgreementTemplate serviceAgreementTemplate, Money fixedAmount, Money fixedAmountPenalty, YearMonthDay whenToApplyFixedAmountPenalty) {
    this();
    init(EntryType.ADMINISTRATIVE_OFFICE_FEE, EventType.ADMINISTRATIVE_OFFICE_FEE, startDate, endDate, serviceAgreementTemplate, fixedAmount, fixedAmountPenalty, whenToApplyFixedAmountPenalty);
  }

  @Override protected Optional<LocalDate> getPenaltyDueDate(Event event) {
    final 
<<<<<<< /usr/src/app/output/fenixedu/fenixedu-academic/5b7496ed218ab8234f16d55853dd5af5f4dc86f9/src/main/java/org/fenixedu/academic/domain/accounting/postingRules/AdministrativeOfficeFeePR.java/left.java
    IAdministrativeOfficeFeeEvent
=======
    AdministrativeOfficeFeeAndInsuranceEvent
>>>>>>> /usr/src/app/output/fenixedu/fenixedu-academic/5b7496ed218ab8234f16d55853dd5af5f4dc86f9/src/main/java/org/fenixedu/academic/domain/accounting/postingRules/AdministrativeOfficeFeePR.java/right.java
     
<<<<<<< /usr/src/app/output/fenixedu/fenixedu-academic/5b7496ed218ab8234f16d55853dd5af5f4dc86f9/src/main/java/org/fenixedu/academic/domain/accounting/postingRules/AdministrativeOfficeFeePR.java/left.java
    administrativeOfficeFeeEvent = (IAdministrativeOfficeFeeEvent) event
=======
    administrativeOfficeFeeAndInsuranceEvent = (AdministrativeOfficeFeeAndInsuranceEvent) event
>>>>>>> /usr/src/app/output/fenixedu/fenixedu-academic/5b7496ed218ab8234f16d55853dd5af5f4dc86f9/src/main/java/org/fenixedu/academic/domain/accounting/postingRules/AdministrativeOfficeFeePR.java/right.java
    ;
    final YearMonthDay paymentEndDate = 
<<<<<<< /usr/src/app/output/fenixedu/fenixedu-academic/5b7496ed218ab8234f16d55853dd5af5f4dc86f9/src/main/java/org/fenixedu/academic/domain/accounting/postingRules/AdministrativeOfficeFeePR.java/left.java
    administrativeOfficeFeeEvent
=======
    administrativeOfficeFeeAndInsuranceEvent
>>>>>>> /usr/src/app/output/fenixedu/fenixedu-academic/5b7496ed218ab8234f16d55853dd5af5f4dc86f9/src/main/java/org/fenixedu/academic/domain/accounting/postingRules/AdministrativeOfficeFeePR.java/right.java
    .getPaymentEndDate() != null ? 
<<<<<<< /usr/src/app/output/fenixedu/fenixedu-academic/5b7496ed218ab8234f16d55853dd5af5f4dc86f9/src/main/java/org/fenixedu/academic/domain/accounting/postingRules/AdministrativeOfficeFeePR.java/left.java
    administrativeOfficeFeeEvent
=======
    administrativeOfficeFeeAndInsuranceEvent
>>>>>>> /usr/src/app/output/fenixedu/fenixedu-academic/5b7496ed218ab8234f16d55853dd5af5f4dc86f9/src/main/java/org/fenixedu/academic/domain/accounting/postingRules/AdministrativeOfficeFeePR.java/right.java
    .getPaymentEndDate() : getWhenToApplyFixedAmountPenalty();
    return Optional.of(paymentEndDate.toLocalDate());
  }

  public AdministrativeOfficeFeePR edit(DateTime startDate, Money fixedAmount, Money penaltyAmount, YearMonthDay whenToApplyFixedAmountPenalty) {
    if (!startDate.isAfter(getStartDate())) {
      throw new DomainException("error.AdministrativeOfficeFeePR.startDate.is.before.then.start.date.of.previous.posting.rule");
    }
    deactivate(startDate);
    return new AdministrativeOfficeFeePR(startDate.minus(1000), null, getServiceAgreementTemplate(), fixedAmount, penaltyAmount, whenToApplyFixedAmountPenalty);
  }

  @Override public YearMonthDay getAdministrativeOfficeFeePaymentLimitDate(DateTime startDate, DateTime endDate) {
    return getWhenToApplyFixedAmountPenalty();
  }

  @Override public Money getInsuranceAmount(DateTime startDate, DateTime endDate) {
    return Money.ZERO;
  }

  @Override public Money getAdministrativeOfficeFeeAmount(Event event, DateTime startDate, DateTime endDate) {
    return getFixedAmount();
  }

  @Override public Money getAdministrativeOfficeFeePenaltyAmount(Event event, DateTime startDate, DateTime endDate) {
    return getFixedAmountPenalty();
  }
}