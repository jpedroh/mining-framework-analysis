package org.fenixedu.academic.domain.studentCurriculum;
import java.util.Comparator;
import java.util.Set;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.fenixedu.academic.domain.DomainObjectUtil;
import org.fenixedu.academic.domain.ExecutionSemester;
import org.fenixedu.academic.domain.ExecutionYear;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicAccessRule;
import org.fenixedu.academic.domain.accessControl.academicAdministration.AcademicOperationType;
import org.fenixedu.academic.domain.degreeStructure.BranchCourseGroup;
import org.fenixedu.academic.domain.degreeStructure.BranchType;
import org.fenixedu.academic.domain.degreeStructure.CourseGroup;
import org.fenixedu.academic.domain.degreeStructure.CycleCourseGroup;
import org.fenixedu.academic.domain.degreeStructure.CycleType;
import org.fenixedu.academic.domain.degreeStructure.DegreeModule;
import org.fenixedu.academic.domain.exceptions.DomainException;
import org.fenixedu.academic.domain.person.RoleType;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.joda.time.YearMonthDay;

/**
 * 
 * @author - Shezad Anavarali (shezad@ist.utl.pt)
 * 
 */
public class CycleCurriculumGroup extends CycleCurriculumGroup_Base {
  static final private Comparator<CycleCurriculumGroup> COMPARATOR_BY_CYCLE_TYPE = new Comparator<CycleCurriculumGroup>() {
    @Override final public int compare(final CycleCurriculumGroup o1, final CycleCurriculumGroup o2) {
      return CycleType.COMPARATOR_BY_LESS_WEIGHT.compare(o1.getCycleType(), o2.getCycleType());
    }
  };

  static final public Comparator<CycleCurriculumGroup> COMPARATOR_BY_CYCLE_TYPE_AND_ID = new Comparator<CycleCurriculumGroup>() {
    @Override final public int compare(final CycleCurriculumGroup o1, final CycleCurriculumGroup o2) {
      final ComparatorChain comparatorChain = new ComparatorChain();
      comparatorChain.addComparator(CycleCurriculumGroup.COMPARATOR_BY_CYCLE_TYPE);
      comparatorChain.addComparator(DomainObjectUtil.COMPARATOR_BY_ID);
      return comparatorChain.compare(o1, o2);
    }
  };

  protected CycleCurriculumGroup() {
    super();
  }

  public CycleCurriculumGroup(RootCurriculumGroup rootCurriculumGroup, CycleCourseGroup cycleCourseGroup, ExecutionSemester executionSemester) {
    this();
    init(rootCurriculumGroup, cycleCourseGroup, executionSemester);
  }

  public CycleCurriculumGroup(RootCurriculumGroup rootCurriculumGroup, CycleCourseGroup cycleCourseGroup) {
    this();
    init(rootCurriculumGroup, cycleCourseGroup);
  }

  @Override protected void init(CurriculumGroup curriculumGroup, CourseGroup courseGroup) {
    checkInitConstraints((RootCurriculumGroup) curriculumGroup, (CycleCourseGroup) courseGroup);
    super.init(curriculumGroup, courseGroup);
  }

  @Override protected void init(CurriculumGroup curriculumGroup, CourseGroup courseGroup, ExecutionSemester executionSemester) {
    checkInitConstraints((RootCurriculumGroup) curriculumGroup, (CycleCourseGroup) courseGroup);
    super.init(curriculumGroup, courseGroup, executionSemester);
  }

  private void checkInitConstraints(final RootCurriculumGroup rootCurriculumGroup, final CycleCourseGroup cycleCourseGroup) {
    if (rootCurriculumGroup.getCycleCurriculumGroup(cycleCourseGroup.getCycleType()) != null) {
      throw new DomainException("error.studentCurriculum.RootCurriculumGroup.cycle.course.group.already.exists.in.curriculum", cycleCourseGroup.getName());
    }
  }

  @Override public void setCurriculumGroup(CurriculumGroup curriculumGroup) {
    if (curriculumGroup != null && !(curriculumGroup instanceof RootCurriculumGroup)) {
      throw new DomainException("error.curriculumGroup.CycleParentCanOnlyBeRootCurriculumGroup");
    }
    super.setCurriculumGroup(curriculumGroup);
  }

  @Override public void setDegreeModule(DegreeModule degreeModule) {
    if (degreeModule != null && !(degreeModule instanceof CycleCourseGroup)) {
      throw new DomainException("error.curriculumGroup.CycleParentDegreeModuleCanOnlyBeCycleCourseGroup");
    }
    super.setDegreeModule(degreeModule);
  }

  @Override public CycleCourseGroup getDegreeModule() {
    return (CycleCourseGroup) super.getDegreeModule();
  }

  @Override public boolean isCycleCurriculumGroup() {
    return true;
  }

  public boolean isCycle(final CycleType cycleType) {
    return getCycleType() == cycleType;
  }

  public boolean isFirstCycle() {
    return isCycle(CycleType.FIRST_CYCLE);
  }

  public CycleCourseGroup getCycleCourseGroup() {
    return getDegreeModule();
  }

  public CycleType getCycleType() {
    return getCycleCourseGroup().getCycleType();
  }

  @Override public RootCurriculumGroup getCurriculumGroup() {
    return (RootCurriculumGroup) super.getCurriculumGroup();
  }

  @Override public void delete() {
    checkRulesToDelete();
    super.delete();
  }

  @Override public void deleteRecursive() {
    for (final CurriculumModule child : getCurriculumModulesSet()) {
      child.deleteRecursive();
    }
    super.delete();
  }

  private void checkRulesToDelete() {
    if (isFirstCycle()) {
      if (getRegistration().getIngressionType().isDirectAccessFrom1stCycle() || getRegistration().getIngressionType().isInternal2ndCycleAccess()) {
        final User userView = Authenticate.getUser();
        if (AcademicAccessRule.isProgramAccessibleToFunction(AcademicOperationType.STUDENT_ENROLMENTS, getRegistration().getDegree(), userView.getPerson().getUser())) {
          return;
        }
      }
    }
    if (getCurriculumGroup().getDegreeType().isIntegratedMasterDegree()) {
      if (getCurriculumGroup().getRootCurriculumGroup().getCycleCurriculumGroups().size() == 1) {
        throw new DomainException("error.studentCurriculum.CycleCurriculumGroup.degree.type.requires.this.cycle.to.exist", getName().getContent());
      }
    }
  }

  public boolean isExternal() {
    return false;
  }

  @Override public YearMonthDay calculateConclusionDate() {
    YearMonthDay result = super.calculateConclusionDate();
    if (getRegistration().getWasTransition()) {
      final ExecutionSemester firstBolonhaTransitionExecutionPeriod = ExecutionSemester.readFirstBolonhaTransitionExecutionPeriod();
      final YearMonthDay begin = firstBolonhaTransitionExecutionPeriod.getBeginDateYearMonthDay();
      if (result == null || result.isBefore(begin)) {
        result = begin;
      }
    }
    return result;
  }

  public Double getCurrentDefaultEcts() {
    return getDegreeModule().getCurrentDefaultEcts();
  }

  public Double getDefaultEcts(final ExecutionYear executionYear) {
    return getDegreeModule().getDefaultEcts(executionYear);
  }

  @Override public CycleCurriculumGroup getParentCycleCurriculumGroup() {
    return this;
  }

  /**
     * 
     * Cycle can have only one branch by type
     * 
     * @param branchType
     */
  public BranchCurriculumGroup getBranchCurriculumGroup(final BranchType branchType) {
    final Set<BranchCurriculumGroup> groups = getBranchCurriculumGroups(branchType);
    return groups.isEmpty() ? null : groups.iterator().next();
  }

  public BranchCurriculumGroup getMajorBranchCurriculumGroup() {
    return getBranchCurriculumGroup(BranchType.MAJOR);
  }

  public BranchCurriculumGroup getMinorBranchCurriculumGroup() {
    return getBranchCurriculumGroup(BranchType.MINOR);
  }

  public BranchCourseGroup getBranchCourseGroup(final BranchType branchType) {
    final Set<BranchCourseGroup> groups = getBranchCourseGroups(branchType);
    return groups.isEmpty() ? null : groups.iterator().next();
  }

  public BranchCourseGroup getMajorBranchCourseGroup() {
    return getBranchCourseGroup(BranchType.MAJOR);
  }

  public BranchCourseGroup getMinorBranchCourseGroup() {
    return getBranchCourseGroup(BranchType.MINOR);
  }
}