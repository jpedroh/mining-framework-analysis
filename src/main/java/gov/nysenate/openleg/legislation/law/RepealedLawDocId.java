package gov.nysenate.openleg.legislation.law;

import java.time.LocalDate;

public class RepealedLawDocId extends LawDocId {
    private final LocalDate repealedDate;

    public RepealedLawDocId(LawDocId other, LocalDate repealedDate) {
        super(other);
        this.repealedDate = repealedDate;
    }

    public LocalDate getRepealedDate() {
        return repealedDate;
    }
}
