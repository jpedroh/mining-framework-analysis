package org.fenixedu.academic.domain.accounting;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.util.Money;
import org.fenixedu.bennu.core.domain.Bennu;
import org.joda.time.DateTime;
import pt.ist.fenixframework.Atomic;

public class Discount extends Discount_Base {
  private Discount() {
    super();
    setRootDomainObject(Bennu.getInstance());
    setWhenCreated(new DateTime());
  }

  Discount(final Event event, final Person person, final Money amount) {
    this();
    checkEvent(event);
    checkAmount(amount);
    setAmount(amount);
    setEvent(event);
    if (person != null) {
      setUsername(person.getUsername());
    }
  }

  public void checkEvent(Event event) {
    final List<String> operationsAfter = event.getOperationsAfter(getWhenCreated());
    if (!operationsAfter.isEmpty()) {
      throw new DomainException("error.accounting.Discount.cannot.create.operations.after", operationsAfter.stream().collect(Collectors.joining(",")));
    }
  }

  private void checkAmount(Money amount) {
    if (amount == null || !amount.isPositive()) {
      throw new DomainException("error.Discount.invalid.amount");
    }
  }

  @Override protected void checkForDeletionBlockers(Collection<String> blockers) {
    super.checkForDeletionBlockers(blockers);
    blockers.addAll(getEvent().getOperationsAfter(getWhenCreated()));
  }

  @Atomic public void delete() {
    DomainException.throwWhenDeleteBlocked(getDeletionBlockers());
    setRootDomainObject(null);
    final Event event = getEvent();
    super.setEvent(null);
    event.recalculateState(new DateTime());
    super.deleteDomainObject();
  }
}