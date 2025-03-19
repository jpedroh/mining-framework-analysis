package org.fenixedu.academic.domain.reports;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.fenixedu.academic.domain.Enrolment;
import org.fenixedu.academic.domain.ExecutionDegree;
import org.fenixedu.academic.domain.ExecutionInterval;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.GrantOwnerType;
import org.fenixedu.academic.domain.Person;
import org.fenixedu.academic.domain.StudentCurricularPlan;
import org.fenixedu.academic.domain.candidacy.IngressionType;
import org.fenixedu.academic.domain.candidacy.PersonalInformationBean;
import org.fenixedu.academic.domain.candidacyProcess.mobility.MobilityAgreement;
import org.fenixedu.academic.domain.degree.DegreeType;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.mobility.outbound.OutboundMobilityCandidacySubmission;
import org.fenixedu.academic.domain.raides.DegreeDesignation;
import org.fenixedu.academic.domain.student.Registration;
import org.fenixedu.academic.domain.student.StudentStatute;
import org.fenixedu.academic.domain.student.registrationStates.RegistrationState;
import org.fenixedu.academic.domain.studentCurriculum.BranchCurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.Credits;
import org.fenixedu.academic.domain.studentCurriculum.CurriculumLine;
import org.fenixedu.academic.domain.studentCurriculum.CycleCurriculumGroup;
import org.fenixedu.academic.domain.studentCurriculum.ExtraCurriculumGroup;
import org.fenixedu.academic.util.Bundle;
import org.fenixedu.bennu.core.i18n.BundleUtil;
import org.fenixedu.commons.spreadsheet.Spreadsheet;
import org.fenixedu.commons.spreadsheet.Spreadsheet.Row;
import org.joda.time.DateTime;
import org.joda.time.YearMonthDay;

public class RaidesCommonReportFieldsWrapper {
  public static void createHeaders(final Spreadsheet spreadsheet) {
    spreadsheet.setHeader("ciclo");
    spreadsheet.setHeader("conclu\u00eddo (ano anterior)?");
    spreadsheet.setHeader("m\u00e9dia do ciclo");
    spreadsheet.setHeader("Data de conclus\u00e3o");
    spreadsheet.setHeader("Data de in\u00edcio");
    spreadsheet.setHeader("n\u00famero aluno");
    spreadsheet.setHeader("nome de utilizador");
    spreadsheet.setHeader("tipo identifica\u00e7\u00e3o");
    spreadsheet.setHeader("n\u00famero identifica\u00e7\u00e3o");
    spreadsheet.setHeader("digitos controlo");
    spreadsheet.setHeader("vers\u00e3o doc identifica\u00e7\u00e3o");
    spreadsheet.setHeader("nome");
    spreadsheet.setHeader("g\u00e9nero");
    spreadsheet.setHeader("data nascimento");
    spreadsheet.setHeader("pa\u00eds nascimento");
    spreadsheet.setHeader("pa\u00eds nacionalidade");
    spreadsheet.setHeader("tipo curso");
    spreadsheet.setHeader("nome curso");
    spreadsheet.setHeader("sigla curso");
    spreadsheet.setHeader("Ramo Principal");
    spreadsheet.setHeader("Ramo Secund\u00e1ro");
    spreadsheet.setHeader("ano curricular");
    spreadsheet.setHeader("ano ingresso curso actual");
    spreadsheet.setHeader("n\u00ba. anos lectivos inscri\u00e7\u00e3o curso actual");
    spreadsheet.setHeader("\u00daltimo ano inscrito neste curso");
    spreadsheet.setHeader("regime frequ\u00eancia curso");
    spreadsheet.setHeader("tipo aluno");
    spreadsheet.setHeader("regime ingresso (c\u00f3digo)");
    spreadsheet.setHeader("regime ingresso (designa\u00e7\u00e3o)");
    spreadsheet.setHeader("estabelecimento do grau preced. (qd aplic\u00e1vel)");
    spreadsheet.setHeader("curso grau preced. (qd aplic\u00e1vel)");
    spreadsheet.setHeader("estabelec. curso habl anterior compl");
    spreadsheet.setHeader("curso habl anterior compl");
    spreadsheet.setHeader("n\u00ba inscri\u00e7\u00f5es no curso preced.");
    spreadsheet.setHeader("nota ingresso");
    spreadsheet.setHeader("op\u00e7\u00e3o ingresso");
    spreadsheet.setHeader("estado civil");
    spreadsheet.setHeader("pa\u00eds resid\u00eancia permanente");
    spreadsheet.setHeader("distrito resid\u00eancia permanente");
    spreadsheet.setHeader("concelho resid\u00eancia permanente");
    spreadsheet.setHeader("deslocado resid\u00eancia permanente");
    spreadsheet.setHeader("n\u00edvel escolaridade pai");
    spreadsheet.setHeader("n\u00edvel escolaridade m\u00e3e");
    spreadsheet.setHeader("condi\u00e7\u00e3o perante profiss\u00e3o pai");
    spreadsheet.setHeader("condi\u00e7\u00e3o perante profiss\u00e3o m\u00e3e");
    spreadsheet.setHeader("profiss\u00e3o pai");
    spreadsheet.setHeader("profiss\u00e3o m\u00e3e");
    spreadsheet.setHeader("profiss\u00e3o aluno");
    spreadsheet.setHeader("Data preenchimento dados RAIDES");
    spreadsheet.setHeader("estatuto trabalhador estudante introduzido (info. RAIDES)");
    spreadsheet.setHeader("estatuto trabalhador 1\u00ba semestre ano (info. oficial)");
    spreadsheet.setHeader("estatuto trabalhador 2\u00ba semestre ano (info. oficial)");
    spreadsheet.setHeader("bolseiro (info. RAIDES)");
    spreadsheet.setHeader("institui\u00e7\u00e3o que atribuiu a bolsa (qd aplic\u00e1vel)");
    spreadsheet.setHeader("bolseiro (info. oficial)");
    spreadsheet.setHeader("Grau Precedente");
    spreadsheet.setHeader("Outro Grau Precedente");
    spreadsheet.setHeader("grau habl anterior compl");
    spreadsheet.setHeader("Codigo do grau habl anterior");
    spreadsheet.setHeader("Outro grau habl anterior compl");
    spreadsheet.setHeader("pa\u00eds habilita\u00e7\u00e3o anterior");
    spreadsheet.setHeader("pa\u00eds habilita\u00e7\u00e3o 12\u00ba ano ou equivalente");
    spreadsheet.setHeader("ano de conclus\u00e3o da habilita\u00e7\u00e3o anterior");
    spreadsheet.setHeader("nota da habilita\u00e7\u00e3o anterior");
    spreadsheet.setHeader("Programa mobilidade");
    spreadsheet.setHeader("Pa\u00eds mobilidade");
    spreadsheet.setHeader("Dura\u00e7\u00e3o programa mobilidade");
    spreadsheet.setHeader("tipo estabelecimento ensino secund\u00e1rio");
    spreadsheet.setHeader("total ECTS inscritos no ano");
    spreadsheet.setHeader("total ECTS conclu\u00eddos fim ano lectivo anterior");
    spreadsheet.setHeader("n\u00ba. disciplinas inscritas ano lectivo anterior dados");
    spreadsheet.setHeader("n\u00ba. disciplinas aprovadas ano lectivo anterior dados");
    spreadsheet.setHeader("n\u00ba. inscri\u00e7\u00f5es externas ano dados");
    spreadsheet.setHeader("estado matr\u00edcula ano anterior dados");
    spreadsheet.setHeader("estado matr\u00edcula ano dados");
    spreadsheet.setHeader("data do estado de matr\u00edcula");
    spreadsheet.setHeader("n\u00ba. ECTS 1\u00ba ciclo conclu\u00eddos fim ano lectivo anterior");
    spreadsheet.setHeader("n\u00ba. ECTS 2\u00ba ciclo conclu\u00eddos fim ano lectivo anterior");
    spreadsheet.setHeader("n\u00ba. ECTS extra 1\u00ba ciclo conclu\u00eddos fim ano lectivo anterior");
    spreadsheet.setHeader("n\u00ba. ECTS extracurriculares conclu\u00eddos fim ano lectivo anterior");
    spreadsheet.setHeader("n\u00ba. ECTS Propedeuticas conclu\u00eddos fim ano lectivo anterior");
    spreadsheet.setHeader("n\u00ba. ECTS inscritos em Propedeut e extra-curriculares");
    spreadsheet.setHeader("n\u00ba. ECTS equival\u00eancia/substitui\u00e7\u00e3o/dispensa");
    spreadsheet.setHeader("Tem situa\u00e7\u00e3o de propinas no lectivo dos dados?");
  }

  public static Row reportRaidesFields(final Spreadsheet sheet, final Registration registration, StudentCurricularPlan studentCurricularPlan, List<Registration> registrationPath, ExecutionYear executionYear, final CycleType cycleType, final boolean concluded, final YearMonthDay conclusionDate, BigDecimal average, boolean graduation) {
    final Row row = sheet.addRow();
    final Person graduate = registration.getPerson();
    Registration sourceRegistration = registrationPath.iterator().next();
    final PersonalInformationBean personalInformationBean = registration.getPersonalInformationBean(executionYear);
    row.setCell(cycleType.getDescription());
    row.setCell(String.valueOf(concluded));
    if (graduation) {
      row.setCell(concluded ? printBigDecimal(average.setScale(0, BigDecimal.ROUND_HALF_EVEN)) : printBigDecimal(average));
    } else {
      row.setCell(concluded ? studentCurricularPlan.getCycle(cycleType).getCurriculum().getRawGrade().getValue() : "n/a");
    }
    row.setCell(conclusionDate != null ? conclusionDate.toString("dd-MM-yyyy") : "");
    row.setCell(registration.getStartDate() != null ? registration.getStartDate().toString("dd-MM-yyyy") : "");
    row.setCell(registration.getNumber());
    row.setCell(registration.getPerson().getUsername());
    row.setCell(graduate.getIdDocumentType().getLocalizedName());
    row.setCell(graduate.getDocumentIdNumber());
    row.setCell(graduate.getIdentificationDocumentExtraDigitValue());
    row.setCell(graduate.getIdentificationDocumentSeriesNumberValue());
    row.setCell(registration.getName());
    row.setCell(graduate.getGender().toString());
    row.setCell(graduate.getDateOfBirthYearMonthDay() != null ? graduate.getDateOfBirthYearMonthDay().toString("dd-MM-yyyy") : "n/a");
    row.setCell(graduate.getCountryOfBirth() != null ? graduate.getCountryOfBirth().getName() : "n/a");
    row.setCell(graduate.getCountry() != null ? graduate.getCountry().getName() : "n/a");
    row.setCell(registration.getDegreeType().getName().getContent());
    row.setCell(registration.getDegree().getNameI18N().getContent());
    row.setCell(registration.getDegree().getSigla());
    final StringBuilder majorBranches = new StringBuilder();
    final StringBuilder minorBranches = new StringBuilder();
    for (final BranchCurriculumGroup group : studentCurricularPlan.getBranchCurriculumGroups()) {
      if (group.isMajor()) {
        majorBranches.append(group.getName().toString()).append(",");
      } else {
        if (group.isMinor()) {
          minorBranches.append(group.getName().toString()).append(",");
        }
      }
    }
    if (majorBranches.length() > 0) {
      row.setCell(majorBranches.deleteCharAt(majorBranches.length() - 1).toString());
    } else {
      row.setCell("");
    }
    if (minorBranches.length() > 0) {
      row.setCell(minorBranches.deleteCharAt(minorBranches.length() - 1).toString());
    } else {
      row.setCell("");
    }
    row.setCell(registration.getCurricularYear(executionYear));
    row.setCell(sourceRegistration.getStartExecutionYear().getName());
    int numberOfEnrolmentYears = 0;
    for (Registration current : registrationPath) {
      numberOfEnrolmentYears += current.getNumberOfYearsEnrolledUntil(executionYear);
    }
    row.setCell(numberOfEnrolmentYears);
    row.setCell(registration.getLastEnrolmentExecutionYear() != null ? registration.getLastEnrolmentExecutionYear().getName() : "");
    row.setCell(registration.getRegimeType(executionYear) != null ? registration.getRegimeType(executionYear).getName() : "");
    row.setCell(registration.getRegistrationProtocol() != null ? registration.getRegistrationProtocol().getCode() : "");
    IngressionType ingressionType = sourceRegistration.getIngressionType();
    if (ingressionType == null && sourceRegistration.getStudentCandidacy() != null) {
      ingressionType = sourceRegistration.getStudentCandidacy().getIngressionType();
    }
    row.setCell(ingressionType != null ? ingressionType.getCode() : "");
    row.setCell(ingressionType != null ? ingressionType.getDescription().getContent() : "");
    row.setCell(personalInformationBean.getPrecedentInstitution() != null ? personalInformationBean.getPrecedentInstitution().getName() : "");
    row.setCell(personalInformationBean.getPrecedentDegreeDesignation() != null ? personalInformationBean.getPrecedentDegreeDesignation() : "");
    row.setCell(personalInformationBean.getInstitution() != null ? personalInformationBean.getInstitution().getName() : "");
    row.setCell(personalInformationBean.getDegreeDesignation());
    row.setCell(personalInformationBean.getNumberOfPreviousYearEnrolmentsInPrecedentDegree() != null ? personalInformationBean.getNumberOfPreviousYearEnrolmentsInPrecedentDegree().toString() : "");
    Double entryGrade = null;
    if (registration.getStudentCandidacy() != null) {
      entryGrade = registration.getStudentCandidacy().getEntryGrade();
    }
    row.setCell(printDouble(entryGrade));
    Integer placingOption = null;
    if (registration.getStudentCandidacy() != null) {
      placingOption = registration.getStudentCandidacy().getPlacingOption();
    }
    row.setCell(placingOption);
    row.setCell(personalInformationBean.getMaritalStatus() != null ? personalInformationBean.getMaritalStatus().toString() : registration.getPerson().getMaritalStatus().toString());
    if (personalInformationBean.getCountryOfResidence() != null) {
      row.setCell(personalInformationBean.getCountryOfResidence().getName());
    } else {
      row.setCell(registration.getStudent().getPerson().getCountryOfResidence() != null ? registration.getStudent().getPerson().getCountryOfResidence().getName() : "");
    }
    if (personalInformationBean.getDistrictSubdivisionOfResidence() != null) {
      row.setCell(personalInformationBean.getDistrictSubdivisionOfResidence().getDistrict().getName());
    } else {
      row.setCell(registration.getStudent().getPerson().getDistrictOfResidence());
    }
    if (personalInformationBean.getDistrictSubdivisionOfResidence() != null) {
      row.setCell(personalInformationBean.getDistrictSubdivisionOfResidence().getName());
    } else {
      row.setCell(registration.getStudent().getPerson().getDistrictSubdivisionOfResidence());
    }
    if (personalInformationBean.getDislocatedFromPermanentResidence() != null) {
      row.setCell(personalInformationBean.getDislocatedFromPermanentResidence().toString());
    } else {
      row.setCell("");
    }
    if (personalInformationBean.getFatherSchoolLevel() != null) {
      row.setCell(personalInformationBean.getFatherSchoolLevel().getName());
    } else {
      row.setCell("");
    }
    if (personalInformationBean.getMotherSchoolLevel() != null) {
      row.setCell(personalInformationBean.getMotherSchoolLevel().getName());
    } else {
      row.setCell("");
    }
    if (personalInformationBean.getFatherProfessionalCondition() != null) {
      row.setCell(personalInformationBean.getFatherProfessionalCondition().getName());
    } else {
      row.setCell("");
    }
    if (personalInformationBean.getMotherProfessionalCondition() != null) {
      row.setCell(personalInformationBean.getMotherProfessionalCondition().getName());
    } else {
      row.setCell("");
    }
    if (personalInformationBean.getFatherProfessionType() != null) {
      row.setCell(personalInformationBean.getFatherProfessionType().getName());
    } else {
      row.setCell("");
    }
    if (personalInformationBean.getMotherProfessionType() != null) {
      row.setCell(personalInformationBean.getMotherProfessionType().getName());
    } else {
      row.setCell("");
    }
    if (personalInformationBean.getProfessionType() != null) {
      row.setCell(personalInformationBean.getProfessionType().getName());
    } else {
      row.setCell("");
    }
    if (personalInformationBean.getLastModifiedDate() != null) {
      DateTime dateTime = personalInformationBean.getLastModifiedDate();
      row.setCell(dateTime.getYear() + "-" + dateTime.getMonthOfYear() + "-" + dateTime.getDayOfMonth());
    } else {
      row.setCell("");
    }
    if (personalInformationBean.getProfessionalCondition() != null) {
      row.setCell(personalInformationBean.getProfessionalCondition().getName());
    } else {
      row.setCell("");
    }
    boolean working1Found = false;
    for (StudentStatute statute : registration.getStudent().getStudentStatutesSet()) {
      if (statute.getType().isWorkingStudentStatute() && statute.isValidInExecutionPeriod(executionYear.getFirstExecutionPeriod())) {
        working1Found = true;
        break;
      }
    }
    row.setCell(String.valueOf(working1Found));
    boolean working2Found = false;
    for (StudentStatute statute : registration.getStudent().getStudentStatutesSet()) {
      if (statute.getType().isWorkingStudentStatute() && statute.isValidInExecutionPeriod(executionYear.getLastExecutionPeriod())) {
        working2Found = true;
        break;
      }
    }
    row.setCell(String.valueOf(working2Found));
    if (personalInformationBean.getGrantOwnerType() != null) {
      row.setCell(personalInformationBean.getGrantOwnerType().getName());
    } else {
      row.setCell("");
    }
    if (personalInformationBean.getGrantOwnerType() != null && personalInformationBean.getGrantOwnerType().equals(GrantOwnerType.OTHER_INSTITUTION_GRANT_OWNER)) {
      row.setCell(personalInformationBean.getGrantOwnerProviderName());
    } else {
      row.setCell("");
    }
    boolean sasFound = false;
    for (StudentStatute statute : registration.getStudent().getStudentStatutesSet()) {
      if (statute.getType().isGrantOwnerStatute() && statute.isValidInExecutionPeriod(executionYear.getFirstExecutionPeriod())) {
        sasFound = true;
        break;
      }
    }
    row.setCell(String.valueOf(sasFound));
    row.setCell(personalInformationBean.getPrecedentSchoolLevel() != null ? personalInformationBean.getPrecedentSchoolLevel().getName() : "");
    row.setCell(personalInformationBean.getOtherPrecedentSchoolLevel());
    row.setCell(personalInformationBean.getSchoolLevel() != null ? personalInformationBean.getSchoolLevel().getName() : "");
    DegreeDesignation designation = DegreeDesignation.readByNameAndSchoolLevel(personalInformationBean.getDegreeDesignation(), personalInformationBean.getPrecedentSchoolLevel());
    row.setCell(designation != null ? designation.getDegreeClassification().getCode() : "");
    row.setCell(personalInformationBean.getOtherSchoolLevel());
    row.setCell(personalInformationBean.getCountryWhereFinishedPreviousCompleteDegree() != null ? personalInformationBean.getCountryWhereFinishedPreviousCompleteDegree().getName() : "");
    row.setCell(personalInformationBean.getCountryWhereFinishedHighSchoolLevel() != null ? personalInformationBean.getCountryWhereFinishedHighSchoolLevel().getName() : "");
    row.setCell(personalInformationBean.getConclusionYear());
    row.setCell(personalInformationBean.getConclusionGrade() != null ? personalInformationBean.getConclusionGrade() : "");
    MobilityAgreement mobilityAgreement = null;
    ExecutionInterval chosenCandidacyInterval = null;
    for (OutboundMobilityCandidacySubmission outboundCandidacySubmission : registration.getOutboundMobilityCandidacySubmissionSet()) {
      if (outboundCandidacySubmission.getSelectedCandidacy() != null && outboundCandidacySubmission.getSelectedCandidacy().getSelected()) {
        ExecutionInterval candidacyInterval = outboundCandidacySubmission.getOutboundMobilityCandidacyPeriod().getExecutionInterval();
        if (candidacyInterval.getAcademicInterval().isBefore(executionYear.getAcademicInterval())) {
          if (mobilityAgreement != null) {
            if (!candidacyInterval.getAcademicInterval().isAfter(chosenCandidacyInterval.getAcademicInterval())) {
              continue;
            }
          }
          mobilityAgreement = outboundCandidacySubmission.getSelectedCandidacy().getOutboundMobilityCandidacyContest().getMobilityAgreement();
          chosenCandidacyInterval = candidacyInterval;
        }
      }
    }
    row.setCell(mobilityAgreement != null ? mobilityAgreement.getMobilityProgram().getName().getContent() : "");
    row.setCell(mobilityAgreement != null ? mobilityAgreement.getUniversityUnit().getCountry().getName() : "");
    row.setCell(personalInformationBean.getMobilityProgramDuration() != null ? BundleUtil.getString(Bundle.ENUMERATION, personalInformationBean.getMobilityProgramDuration().name()) : "");
    if (personalInformationBean.getHighSchoolType() != null) {
      row.setCell(personalInformationBean.getHighSchoolType().getName());
    } else {
      row.setCell("");
    }
    int totalEnrolmentsInPreviousYear = 0;
    int totalEnrolmentsApprovedInPreviousYear = 0;
    double totalEctsConcludedUntilPreviousYear = 0d;
    for (final CycleCurriculumGroup cycleCurriculumGroup : studentCurricularPlan.getInternalCycleCurriculumGrops()) {
      totalEctsConcludedUntilPreviousYear += cycleCurriculumGroup.getCreditsConcluded(executionYear.getPreviousExecutionYear());
      totalEnrolmentsInPreviousYear += cycleCurriculumGroup.getEnrolmentsBy(executionYear.getPreviousExecutionYear()).size();
      for (final Enrolment enrolment : cycleCurriculumGroup.getEnrolmentsBy(executionYear.getPreviousExecutionYear())) {
        if (enrolment.isApproved()) {
          totalEnrolmentsApprovedInPreviousYear++;
        }
      }
    }
    double totalCreditsEnrolled = 0d;
    for (Enrolment enrollment : studentCurricularPlan.getEnrolmentsByExecutionYear(executionYear)) {
      totalCreditsEnrolled += enrollment.getEctsCredits();
    }
    row.setCell(printDouble(totalCreditsEnrolled));
    double totalCreditsDismissed = 0d;
    for (Credits credits : studentCurricularPlan.getCreditsSet()) {
      if (credits.isEquivalence()) {
        totalCreditsDismissed += credits.getEnrolmentsEcts();
      }
    }
    row.setCell(printDouble(totalEctsConcludedUntilPreviousYear));
    row.setCell(totalEnrolmentsInPreviousYear);
    row.setCell(totalEnrolmentsApprovedInPreviousYear);
    ExtraCurriculumGroup extraCurriculumGroup = studentCurricularPlan.getExtraCurriculumGroup();
    int extraCurricularEnrolmentsCount = extraCurriculumGroup != null ? extraCurriculumGroup.getEnrolmentsBy(executionYear).size() : 0;
    for (final CycleCurriculumGroup cycleCurriculumGroup : studentCurricularPlan.getExternalCurriculumGroups()) {
      extraCurricularEnrolmentsCount += cycleCurriculumGroup.getEnrolmentsBy(executionYear).size();
    }
    if (studentCurricularPlan.hasPropaedeuticsCurriculumGroup()) {
      extraCurricularEnrolmentsCount += studentCurricularPlan.getPropaedeuticCurriculumGroup().getEnrolmentsBy(executionYear).size();
    }
    row.setCell(extraCurricularEnrolmentsCount);
    SortedSet<RegistrationState> states = new TreeSet<RegistrationState>(RegistrationState.DATE_COMPARATOR);
    for (Registration current : registrationPath) {
      states.addAll(current.getRegistrationStatesSet());
    }
    RegistrationState previousYearState = null;
    RegistrationState currentYearState = null;
    for (RegistrationState state : states) {
      if (!state.getStateDate().isAfter(executionYear.getPreviousExecutionYear().getEndDateYearMonthDay().toDateTimeAtMidnight())) {
        previousYearState = state;
      }
      if (!state.getStateDate().isAfter(executionYear.getEndDateYearMonthDay().toDateTimeAtMidnight())) {
        currentYearState = state;
      }
    }
    row.setCell(previousYearState != null ? previousYearState.getStateType().getDescription() : "n/a");
    row.setCell(currentYearState != null ? currentYearState.getStateType().getDescription() : "n/a");
    row.setCell(currentYearState != null ? currentYearState.getStateDate().toString("dd-MM-yyyy") : "n/a");
    final CycleCurriculumGroup firstCycleCurriculumGroup = getStudentCurricularPlan(registration, CycleType.FIRST_CYCLE).getCycle(CycleType.FIRST_CYCLE);
    row.setCell(firstCycleCurriculumGroup != null ? printBigDecimal(firstCycleCurriculumGroup.getCurriculum(executionYear).getSumEctsCredits()) : "");
    final CycleCurriculumGroup secondCycleCurriculumGroup = getStudentCurricularPlan(registration, CycleType.SECOND_CYCLE).getCycle(CycleType.SECOND_CYCLE);
    row.setCell(secondCycleCurriculumGroup != null && !secondCycleCurriculumGroup.isExternal() ? printBigDecimal(secondCycleCurriculumGroup.getCurriculum(executionYear).getSumEctsCredits()) : "");
    Double extraFirstCycleEcts = 0d;
    for (final CycleCurriculumGroup cycleCurriculumGroup : studentCurricularPlan.getExternalCurriculumGroups()) {
      for (final CurriculumLine curriculumLine : cycleCurriculumGroup.getAllCurriculumLines()) {
        if (!curriculumLine.getExecutionYear().isAfter(executionYear.getPreviousExecutionYear())) {
          extraFirstCycleEcts += curriculumLine.getCreditsConcluded(executionYear.getPreviousExecutionYear());
        }
      }
    }
    row.setCell(printDouble(extraFirstCycleEcts));
    Double extraCurricularEcts = 0d;
    Double allExtraCurricularEcts = 0d;
    if (extraCurriculumGroup != null) {
      for (final CurriculumLine curriculumLine : extraCurriculumGroup.getAllCurriculumLines()) {
        if (curriculumLine.isApproved() && curriculumLine.hasExecutionPeriod() && !curriculumLine.getExecutionYear().isAfter(executionYear.getPreviousExecutionYear())) {
          extraCurricularEcts += curriculumLine.getEctsCreditsForCurriculum().doubleValue();
        }
        if (curriculumLine.hasExecutionPeriod() && curriculumLine.getExecutionYear() == executionYear) {
          allExtraCurricularEcts += curriculumLine.getEctsCreditsForCurriculum().doubleValue();
        }
      }
    }
    row.setCell(printDouble(extraCurricularEcts));
    Double propaedeuticEcts = 0d;
    Double allPropaedeuticEcts = 0d;
    if (studentCurricularPlan.getPropaedeuticCurriculumGroup() != null) {
      for (final CurriculumLine curriculumLine : studentCurricularPlan.getPropaedeuticCurriculumGroup().getAllCurriculumLines()) {
        if (curriculumLine.isApproved() && curriculumLine.hasExecutionPeriod() && !curriculumLine.getExecutionYear().isAfter(executionYear.getPreviousExecutionYear())) {
          propaedeuticEcts += curriculumLine.getEctsCreditsForCurriculum().doubleValue();
        }
        if (curriculumLine.hasExecutionPeriod() && curriculumLine.getExecutionYear() == executionYear) {
          allPropaedeuticEcts += curriculumLine.getEctsCreditsForCurriculum().doubleValue();
        }
      }
    }
    row.setCell(printDouble(propaedeuticEcts));
    row.setCell(printDouble(allPropaedeuticEcts + allExtraCurricularEcts));
    row.setCell(printDouble(totalCreditsDismissed));
    row.setCell(String.valueOf(studentCurricularPlan.hasAnyGratuityEventFor(executionYear)));
    return row;
  }

  private static String printDouble(Double value) {
    return value == null ? "" : value.toString().replace('.', ',');
  }

  private static String printBigDecimal(BigDecimal value) {
    return value == null ? "" : value.toPlainString().replace('.', ',');
  }

  @Deprecated public static StudentCurricularPlan getStudentCurricularPlan(Registration registration, CycleType cycleType) {
    return registration.getStudentCurricularPlan(cycleType);
  }

  public static Set<Registration> getRegistrationsToProcess(final ExecutionYear executionYear, final DegreeType degreeType) {
    final Set<Registration> result = new HashSet<Registration>();
    collectStudentCurricularPlansFor(executionYear, result, degreeType);
    if (executionYear.getPreviousExecutionYear() != null) {
      collectStudentCurricularPlansFor(executionYear.getPreviousExecutionYear(), result, degreeType);
    }
    return result;
  }

  public static void collectStudentCurricularPlansFor(final ExecutionYear executionYear, final Set<Registration> result, final DegreeType degreeType) {
    for (final ExecutionDegree executionDegree : executionYear.getExecutionDegreesByType(degreeType)) {
      for (StudentCurricularPlan studentCurricularPlan : executionDegree.getDegreeCurricularPlan().getStudentCurricularPlansSet()) {
        if (!studentCurricularPlan.getStartDateYearMonthDay().isAfter(executionYear.getEndDateYearMonthDay())) {
          result.add(studentCurricularPlan.getRegistration());
        }
      }
    }
  }
}