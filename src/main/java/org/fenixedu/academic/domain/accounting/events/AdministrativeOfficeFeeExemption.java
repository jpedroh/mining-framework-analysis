package org.fenixedu.academic.domain.accounting.events;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.accounting.AdministrativeOfficeFeeAndInsuranceExemptionJustificationFactory;
import org.fenixedu.academic.domain.accounting.Event;
import org.fenixedu.academic.domain.accounting.Exemption;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.academic.util.Money;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.joda.time.DateTime;
import org.joda.time.YearMonthDay;
import pt.ist.fenixframework.dml.runtime.RelationAdapter;

public class AdministrativeOfficeFeeExemption extends AdministrativeOfficeFeeExemption_Base {
  static {
    getRelationExemptionEvent().addListener(new RelationAdapter<Exemption, Event>() {
      @Override public void beforeAdd(Exemption exemption, Event event) {
        if (exemption instanceof AdministrativeOfficeFeeAndInsuranceExemption && event != null) {
          final AdministrativeOfficeFeeAndInsuranceEvent administrativeOfficeFeeAndInsuranceEvent = (AdministrativeOfficeFeeAndInsuranceEvent) event;
          if (administrativeOfficeFeeAndInsuranceEvent.hasAdministrativeOfficeFeeAndInsuranceExemption()) {
            throw new DomainException("error.org.fenixedu.academic.domain.accounting.events.AdministrativeOfficeFeeAndInsuranceExemption.event.already.has.exemption");
          }
        }
      }
    });
  }

  protected AdministrativeOfficeFeeExemption() {
    super();
  }

  public AdministrativeOfficeFeeExemption(Person responsible, AdministrativeOfficeFeeAndInsuranceEvent administrativeOfficeFeeAndInsuranceEvent, AdministrativeOfficeFeeAndInsuranceExemptionJustificationType justificationType, String reason, YearMonthDay dispatchDate) {
    this();
    super.init(responsible, administrativeOfficeFeeAndInsuranceEvent, AdministrativeOfficeFeeAndInsuranceExemptionJustificationFactory.create(this, justificationType, reason, dispatchDate));
    administrativeOfficeFeeAndInsuranceEvent.recalculateState(new DateTime());
  }

  @Override public boolean isAdministrativeOfficeFeeExemption() {
    return true;
  }

  @Override public boolean isForAdministrativeOfficeFee() {
    return true;
  }

  public String getKindDescription() {
    return BundleUtil.getString(Bundle.ENUMERATION, this.getClass().getSimpleName() + ".kindDescription");
  }

  @Override public Money getExemptionAmount(Money money) {
    return ((AdministrativeOfficeFeeAndInsuranceEvent) getEvent()).getAdministrativeOfficeFeeAmount();
  }
}