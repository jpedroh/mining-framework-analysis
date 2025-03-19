package org.fenixedu.academic.domain.accounting.events;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.accounting.EntryType;
import org.fenixedu.academic.domain.accounting.EventType;
import org.fenixedu.academic.domain.accounting.PostingRule;
import org.fenixedu.academic.domain.administrativeOffice.AdministrativeOffice;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.util.Money;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class PastAdministrativeOfficeFeeAndInsuranceEvent extends PastAdministrativeOfficeFeeAndInsuranceEvent_Base {
  protected PastAdministrativeOfficeFeeAndInsuranceEvent() {
    super();
  }

  public PastAdministrativeOfficeFeeAndInsuranceEvent(AdministrativeOffice administrativeOffice, Person person, ExecutionYear executionYear, final Money pastAdministrativeOfficeFeeAndInsuranceAmount) {
    this();
    init(administrativeOffice, person, executionYear, pastAdministrativeOfficeFeeAndInsuranceAmount);
  }

  private void init(AdministrativeOffice administrativeOffice, Person person, ExecutionYear executionYear, Money pastAdministrativeOfficeFeeAndInsuranceAmount) {
    super.init(administrativeOffice, EventType.ADMINISTRATIVE_OFFICE_FEE_INSURANCE, person, executionYear);
    checkParameters(pastAdministrativeOfficeFeeAndInsuranceAmount);
    super.setPastAdministrativeOfficeFeeAndInsuranceAmount(pastAdministrativeOfficeFeeAndInsuranceAmount);
  }

  private void checkParameters(Money pastAdministrativeOfficeFeeAndInsuranceAmount) {
    if (pastAdministrativeOfficeFeeAndInsuranceAmount == null || pastAdministrativeOfficeFeeAndInsuranceAmount.isZero()) {
      throw new DomainException("error.org.fenixedu.academic.domain.accounting.events.PastAdministrativeOfficeFeeAndInsuranceEvent.pastAdministrativeOfficeFeeAndInsuranceAmount.cannot.be.null.and.must.be.greather.than.zero");
    }
  }

  @Override public Set<EntryType> getPossibleEntryTypesForDeposit() {
    return Collections.singleton(EntryType.ADMINISTRATIVE_OFFICE_FEE_INSURANCE);
  }

  @Override public boolean isInDebt() {
    return isOpen();
  }

  @Override public Map<LocalDate, Money> getDueDateAmountMap(PostingRule postingRule, DateTime when) {
    return Collections.singletonMap(getDueDateByPaymentCodes().toLocalDate(), postingRule.calculateTotalAmountToPay(this, when));
  }
}