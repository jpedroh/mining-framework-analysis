package org.uma.jmetal.problem.multiobjective.ebes;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class representing problem Ebes
 * Spatial Bars Structure (Estructuras de Barras Espaciales)
 */
@SuppressWarnings(value = { "serial" }) public class Ebes extends AbstractDoubleProblem implements ConstrainedProblem<DoubleSolution> {
  /**
   * Stores the number of Bar Groups
   */
  protected int numberOfEval_;

  protected int numberOfNodes;

  public void setNumberOfNodes(int numberOfNodes) {
    this.numberOfNodes = numberOfNodes;
  }

  public int getNumberOfNodes() {
    return numberOfNodes;
  }

  /**
   * Stores the number of Nodes of the problem
   */
  protected int numberOfLibertyDegree_ = 6;

  protected int numberOfNodesRestricts_;

  public void numberOfNodesRestricts(int numberOfNodesRestricts) {
    numberOfNodesRestricts_ = numberOfNodesRestricts;
  }

  public int getNumberOfNodesRestricts() {
    return numberOfNodesRestricts_;
  }

  /**
   * Stores the number of Nodes of the problem
   */
  protected double[][] nodeCheck_;

  public double nodeCheck(int i, int j) {
    return nodeCheck_[i][j];
  }

  protected int[][] geometryCheck_;

  public int geometryCheck(int i, int j) {
    return geometryCheck_[i][j];
  }

  /**
   * Stores the number of Bar Groups
   */
  protected int numberOfGroupElements_;

  public void setnumberOfGroupElements(int i) {
    numberOfGroupElements_ = i;
  }

  public int getnumberOfGroupElements() {
    return numberOfGroupElements_;
  }

  /**
   * Stores the number of Bar of the problem
   */
  protected int numberOfElements_;

  public void setNumberOfElements(int numberOfElements) {
    numberOfElements_ = numberOfElements;
  }

  public int getNumberOfElements() {
    return numberOfElements_;
  }

  public boolean lLoadsOwnWeight;

  public boolean lSecondOrderGeometric;

  public boolean lBuckling;

  /**
   * Stores the Elements Between Difference Greatest
   */
  protected int elementsBetweenDiffGreat_;

  public void setElementsBetweenDiffGreat(int elementsBetweenDiffGreat) {
    elementsBetweenDiffGreat_ = elementsBetweenDiffGreat;
  }

  public int getElementsBetweenDiffGreat() {
    return elementsBetweenDiffGreat_;
  }

  /**
   * Stores the number of Load in Nodes of the problem
   */
  protected int numberOfWeigthsNodes_;

  public void setNumberOfWeigthsNodes(int numberOfWeigthsNodes) {
    numberOfWeigthsNodes_ = numberOfWeigthsNodes;
  }

  public int getNumberOfWeigthsNodes() {
    return numberOfWeigthsNodes_;
  }

  /**
   * Stores the number of Load in ElementsNodes of the problem
   */
  protected int numberOfWeigthsElements_;

  public void setNumberOfWeigthsElements(int numberOfWeigthsElements) {
    numberOfWeigthsElements_ = numberOfWeigthsElements;
  }

  public int getNumberOfWeigthsElements() {
    return numberOfWeigthsElements_;
  }

  /**
   * Stores the number a wide the diagonal matrix
   */
  protected int matrixWidthBand_;

  public void setMatrixWidthBand(int matrixWidthBand) {
    matrixWidthBand_ = matrixWidthBand;
  }

  public int getMatrixWidthBand() {
    return matrixWidthBand_;
  }

  protected int numberOfWeigthHypothesis_;

  public void setNumberOfWeigthHypothesis(int numberOfWeigthHypothesis) {
    numberOfWeigthHypothesis_ = numberOfWeigthHypothesis;
  }

  public int getNumberOfWeigthHypothesis() {
    return numberOfWeigthHypothesis_;
  }

  public int numberOfConstraintsGeometric_;

  public void setnumberOfConstraintsGeometric(int i) {
    numberOfConstraintsGeometric_ = i;
  }

  public int getnumberOfConstraintsGeometric() {
    return numberOfConstraintsGeometric_;
  }

  protected int numberOfConstraintsNodes_;

  protected int numberOfGroupsToCheckGeometry_;

  public void setNumberOfConstraintsNodes(int numberOfConstraintsNodes) {
    numberOfConstraintsNodes_ = numberOfConstraintsNodes;
  }

  public int getNumberOfConstraintsNodes() {
    return numberOfWeigthHypothesis_;
  }

  /**
   * Stores the Node
   */
  protected double[][] Node_;

  public double getNode(int i, int j) {
    return Node_[i][j];
  }

  /**
   * Stores the NodeRestrict
   */
  protected double[][] NodeRestrict_;

  public double getNodeRestrict(int i, int j) {
    return NodeRestrict_[i][j];
  }

  /**
   * Stores the Groups
   */
  protected double[][] Groups_;

  public double getGroups(int i) {
    return Groups_[i][MAX_COLUMN];
  }

  /**
   * Stores the Element
   */
  protected double[][] Element_;

  public double getElement(int i, int j) {
    return Element_[i][j];
  }

  /**
   * Stores the Load on Nodes
   */
  protected double[][] WeightNode_;

  public double getWeightNode(int i, int j) {
    return WeightNode_[i][j];
  }

  /**
   * Stores the OverLoad on Elements
   */
  protected double[][] OverloadInElement_;

  public double getWeightElement(int i, int j) {
    return OverloadInElement_[i][j];
  }

  /**
   * Stores the Load on Elements Itself
   */
  protected double[][] WeightElement_;

  public double getWeightElementItself(int i, int j) {
    return WeightElement_[i][j];
  }

  /**
   * Stores the k
   */
  protected double[] MatrixStiffness_;

  public double MatrixStiffness(int i) {
    return MatrixStiffness_[i];
  }

  /**
   * Stores the k displacement
   */
  protected double[][] DisplacementNodes_;

  public double DisplacementNodes(int node, int hi) {
    return DisplacementNodes_[node][hi];
  }

  /**
   * Stores the Effort in node i
   */
  protected double[][][] Efforti_;

  public double Efforti(int i, int element, int hypothesis) {
    return Efforti_[i][element][hypothesis];
  }

  /**
   * Stores the Effort in node j
   */
  protected double[][][] Effortj_;

  public double Effortj(int i, int element, int hypothesis) {
    return Effortj_[i][element][hypothesis];
  }

  /**
   * Stores the Axial force in node i
   */
  protected double[] AxialForcei_;

  public double AxialForcei_(int element) {
    return AxialForcei_[element];
  }

  /**
   * Stores the Axial force in node j
   */
  protected double[] AxialForcej_;

  public double AxialForcej_(int element) {
    return AxialForcej_[element];
  }

  protected int strainAdmissibleCut_;

  public void setStrainAdmissibleCut(int strainAdmissibleCut) {
    strainAdmissibleCut_ = strainAdmissibleCut;
  }

  public int getStrainAdmissibleCut() {
    return strainAdmissibleCut_;
  }

  /**
   * Stores the Strain in node i
   */
  protected double[][][] Straini_;

  public double Straini(int i, int element, int hypothesis) {
    return Straini_[i][element][hypothesis];
  }

  /**
   * Stores the Strain in node j
   */
  protected double[][][] Strainj_;

  public double getStrainj(int i, int element, int hypothesis) {
    return Strainj_[i][element][hypothesis];
  }

  /**
   * Stores the max omega for groups
   */
  protected double[][] omegaMax_;

  public double getOmegaMax(int group, int hypothesis) {
    return omegaMax_[group][hypothesis];
  }

  /**
   * Stores the max Nxx for groups
   */
  protected double[][] NxxMax_;

  public double getNxxMax(int group, int hypothesis) {
    return NxxMax_[group][hypothesis];
  }

  /**
   * Stores the min Nxx for groups
   */
  protected double[][] NxxMin_;

  public double getNxxMin(int group, int hypothesis) {
    return NxxMin_[group][hypothesis];
  }

  /**
   * Stores the max Mxz for groups
   */
  protected double[][] MxzMax_;

  public double getMxzMax(int group, int hypothesis) {
    return MxzMax_[group][hypothesis];
  }

  /**
   * Stores the max Mxz for groups
   */
  protected double[][] MxzMin_;

  public double getMxzMin(int group, int hypothesis) {
    return MxzMin_[group][hypothesis];
  }

  /**
   * Stores the max Mxy for groups
   */
  protected double[][] MxyMax_;

  public double getMxyMax(int group, int hypothesis) {
    return MxyMax_[group][hypothesis];
  }

  /**
   * Stores the min Mxy for groups
   */
  protected double[][] MxyMin_;

  public double getMxyMin(int group, int hypothesis) {
    return MxyMin_[group][hypothesis];
  }

  /**
   * Stores the max Nxx Strain for groups
   */
  protected double[][] StrainNxxMax_;

  public double getStrainNxxMax(int group, int hypothesis) {
    return StrainNxxMax_[group][hypothesis];
  }

  protected double[][] StrainNxxMin_;

  public double getStrainNxxMin(int group, int hypothesis) {
    return StrainNxxMin_[group][hypothesis];
  }

  /**
   * Stores the max Mxz Strain for groups
   */
  protected double[][] StrainMxzMax_;

  public double getStrainMxzMax(int group, int hypothesis) {
    return StrainMxzMax_[group][hypothesis];
  }

  /**
   * Stores the max Mxz Strain for groups
   */
  protected double[][] StrainMxzMin_;

  public double getStrainMxzMin(int group, int hypothesis) {
    return StrainMxzMin_[group][hypothesis];
  }

  /**
   * Stores the max Mxz Strain for groups
   */
  protected double[][] StrainMxyMax_;

  public double getStrainMxyMax(int group, int hypothesis) {
    return StrainMxyMax_[group][hypothesis];
  }

  /**
   * Stores the max Mxz Strain for groups
   */
  protected double[][] StrainMxyMin_;

  public double getStrainMxyMin(int group, int hypothesis) {
    return StrainMxyMin_[group][hypothesis];
  }

  /**
   * Stores the max Strain for elements
   */
  protected double[][] StrainMax_;

  public double getStrainMax(int group, int hypothesis) {
    return StrainMax_[group][hypothesis];
  }

  protected double[][] StrainMin_;

  public double getStrainMin(int group, int hypothesis) {
    return StrainMin_[group][hypothesis];
  }

  /**
   * Stores the max Strain for elements
   */
  protected double[][] OldStrainMax_;

  public double getOldStrainMax(int group, int hypothesis) {
    return OldStrainMax_[group][hypothesis];
  }

  protected double[][] OldStrainMin_;

  public double getOldStrainMin(int group, int hypothesis) {
    return OldStrainMin_[group][hypothesis];
  }

  /**
   * Stores the max Strain for elements
   */
  protected double[][] StrainCutMax_;

  public double getStrainCutMax(int group, int hypothesis) {
    return StrainCutMax_[group][hypothesis];
  }

  /**
   * Stores the min Strain for elements
   */
  protected double[] StrainResidualMin_;

  public double getStrainResidualMin(int hypothesis) {
    return StrainResidualMin_[hypothesis];
  }

  /**
   * Stores the max Strain for elements
   */
  protected double[] StrainResidualMax_;

  public double getStrainResidualMax(int hypothesis) {
    return StrainResidualMax_[hypothesis];
  }

  /**
   * Stores the Cut Strain Residual for elements
   */
  protected double[] StrainResidualCut_;

  public double getStrainResidualCut(int hypothesis) {
    return StrainResidualCut_[hypothesis];
  }

  public int getGroupShape(int groupId) {
    return (int) Groups_[groupId][SHAPE];
  }

  public int getVariablePosition(int groupId) {
    return (int) Groups_[groupId][VAR_POSITION];
  }

  String GravitationalAxis_;

  double g_ = 9.81;

  double[][][] cbi;

  double[][][] cbj;

  double[] Qi = new double[numberOfLibertyDegree_];

  double[] Qj = new double[numberOfLibertyDegree_];

  double[] pi = new double[numberOfLibertyDegree_];

  double[] pj = new double[numberOfLibertyDegree_];

  double[][] PQ;

  double Reaction_[][];

  double[][] Kii = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] Kij = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] Kji = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] Kjj = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] KGii = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] KGij = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] KGji = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] KGjj = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] Rij = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] Rji = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] RTij = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] RTji = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] Rpij = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] Rpji = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] RpTij = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] RpTji = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] KiiSOG = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] KijSOG = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] KjiSOG = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  double[][] KjjSOG = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];

  int INDEX_ = 0;

  int GROUP_ = 1;

  int SHAPE = 2;

  int BETA = 3;

  int AREA = 4;

  int Az_ = 5;

  int Ay_ = 6;

  int Iz_ = 7;

  int Iy_ = 8;

  int It_ = 9;

  int Iw_ = 10;

  int TypeMaterial_ = 11;

  int E_ = 12;

  int G_ = 13;

  int BLijY_ = 14;

  int BLijZ_ = 15;

  int Fyz_ = 16;

  int Li_ = 17;

  int Lj_ = 18;

  int VARIABLES = 19;

  int Y_ = 20;

  int Z_ = 21;

  int eY_ = 22;

  int eZ_ = 23;

  int uY_ = 24;

  int dY_ = 25;

  int lZ_ = 26;

  int rZ_ = 27;

  int CONSTRAINT = 28;

  int RATIO_YZ = 29;

  int SPECIFIC_WEIGHT = 30;

  int STRESS = 31;

  int COMPRESSION = 32;

  int STRESS_CUT = 33;

  int ELONGATION_POS = 34;

  int ELONGATION_NEG = 35;

  int VAR_Y_LOWER_LIMIT = 36;

  int VAR_Y_UPPER_LIMIT = 37;

  int VAR_Z_LOWER_LIMIT = 38;

  int VAR_Z_UPPER_LIMIT = 39;

  int VAR_eY_LOWER_LIMIT = 40;

  int VAR_eY_UPPER_LIMIT = 41;

  int VAR_eZ_LOWER_LIMIT = 42;

  int VAR_eZ_UPPER_LIMIT = 43;

  int VAR_POSITION = 44;

  int DESCRIPTION = 45;

  int MAX_COLUMN = 45;

  int CARGA_UNIFORME_TOTAL = 0;

  int CARGA_PUNTUAL = 1;

  int CARGA_UNIFORME_PARCIAL = 2;

  int CARGA_TRIANGULAR_I = 3;

  int CARGA__TRIANGULAR_J = 4;

  int CARGA_PARABOLICA = 5;

  int CARGA_MOMENTO_PUNTUAL = 8;

  int CARGA_MOMENTO_DISTRIBUIDO = 6;

  int CARGA_TEMPERATURA = 10;

  int aX_ = 0;

  int aY_ = 1;

  int aZ_ = 2;

  int gX_ = 3;

  int gY_ = 4;

  int gZ_ = 5;

  public static final int CIRCLE = 0;

  public static final int HOLE_CIRCLE = 1;

  public static final int RECTANGLE = 2;

  public static final int HOLE_RECTANGLE = 3;

  public static final int I_SINGLE = 4;

  public static final int I_DOUBLE = 5;

  public static final int H_SINGLE = 6;

  public static final int H_DOUBLE = 7;

  public static final int L_SINGLE = 8;

  public static final int L_DOUBLE = 9;

  public static final int T_SINGLE = 10;

  public static final int T_DOUBLE = 11;

  int RIG_RIG = 0;

  int RIG_ART = 1;

  int ART_RIG = 10;

  int ART_ART = 11;

  int i_ = 1;

  int j_ = 2;

  int L_ = 3;

  int Vij_ = 4;

  int Ei_ = 5;

  int Ej_ = 6;

  int QH_ = 0;

  int QE_ = 1;

  int QT_ = 2;

  int QAx_ = 3;

  int QAy_ = 4;

  int QAz_ = 5;

  int Qa_ = 6;

  int Qb_ = 7;

  int STRAIN_COMPRESS = 0;

  int STRAIN_TRACTION = 1;

  int STRAIN_CUT = 2;

  int selectedOF = 12;

  String[] OF_;

  public OverallConstraintViolation<DoubleSolution> overallConstraintViolationDegree;

  public Ebes() throws FileNotFoundException {
    overallConstraintViolationDegree = new OverallConstraintViolation<DoubleSolution>();
    String file = EBEsReadProblems() + ".ebe";
    EBEsInitialize(file);
  }

  /**
   * Constructor
   * @throws FileNotFoundException
   */
  public Ebes(String ebesFileName, String[] objectiveList) throws FileNotFoundException {
    overallConstraintViolationDegree = new OverallConstraintViolation<DoubleSolution>();
    OF_ = objectiveList;
    EBEsInitialize(ebesFileName);
  }

  public void EBEsInitialize(String file) throws FileNotFoundException {
    setName("Ebes");
    numberOfEval_ = 1;
    try {
      EBEsReadDataFile(file);
    } catch (JMetalException ex) {
      Logger.getLogger(Ebes.class.getName()).log(Level.SEVERE, null, ex);
    }
    int numberOfConstraints_ = 0;
    setNumberOfVariables(Variable_Position());
    numberOfConstraints_ = numberOfConstraintsGeometric_;
    numberOfConstraints_ += numberOfGroupElements_ * 3;
    numberOfConstraints_ += numberOfConstraintsNodes_;
    setNumberOfConstraints(numberOfConstraints_);
    setNumberOfObjectives(OF_.length);
    System.out.println("Structure");
    System.out.println("  file: " + file);
    System.out.println("  Number of Nodes: " + numberOfNodes);
    System.out.println("  Number of Bars: " + numberOfElements_);
    System.out.println("  Number of Groups: " + numberOfGroupElements_);
    System.out.println("Optimization multi-objective: ");
    System.out.println("  Number of objective function: " + getNumberOfObjectives());
    String txt = "";
    for (int i = 0; i < getNumberOfObjectives(); i++) {
      txt = txt + OF_[i] + " ";
    }
    System.out.println("  " + txt);
    System.out.println("  Number of Variables: " + getNumberOfVariables());
    System.out.println("  Number of constraints for Geometric: " + numberOfConstraintsGeometric_);
    System.out.println("  Number of constraints for Stress: " + (numberOfGroupElements_ * 3));
    System.out.println("  Number of constraints for Deflection: " + numberOfConstraintsNodes_);
    System.out.println("  Number of Constraints: " + numberOfConstraints_);
    System.out.println("  Number of groups to check geometry: " + numberOfGroupsToCheckGeometry_);
    System.out.println("Algorithm configuration: ");
    Double[] lowerLimit_ = new Double[getNumberOfVariables()];
    Double[] upperLimit_ = new Double[getNumberOfVariables()];
    int var = 0;
    for (int gr = 0; gr < numberOfGroupElements_; gr++) {
      var += Groups_[gr][VARIABLES];
      if (Groups_[gr][SHAPE] == CIRCLE) {
        lowerLimit_[var - 1] = Groups_[gr][VAR_Y_LOWER_LIMIT];
        upperLimit_[var - 1] = Groups_[gr][VAR_Y_UPPER_LIMIT];
      } else {
        if (Groups_[gr][SHAPE] == HOLE_CIRCLE) {
          lowerLimit_[var - 2] = Groups_[gr][VAR_Y_LOWER_LIMIT];
          lowerLimit_[var - 1] = Groups_[gr][VAR_eY_LOWER_LIMIT];
          upperLimit_[var - 2] = Groups_[gr][VAR_Y_UPPER_LIMIT];
          upperLimit_[var - 1] = Groups_[gr][VAR_eY_UPPER_LIMIT];
        } else {
          if (Groups_[gr][SHAPE] == RECTANGLE) {
            lowerLimit_[var - 2] = Groups_[gr][VAR_Y_LOWER_LIMIT];
            lowerLimit_[var - 1] = Groups_[gr][VAR_Z_LOWER_LIMIT];
            upperLimit_[var - 2] = Groups_[gr][VAR_Y_UPPER_LIMIT];
            upperLimit_[var - 1] = Groups_[gr][VAR_Z_UPPER_LIMIT];
          } else {
            if (Groups_[gr][SHAPE] == HOLE_RECTANGLE) {
              lowerLimit_[var - 4] = Groups_[gr][VAR_Y_LOWER_LIMIT];
              lowerLimit_[var - 3] = Groups_[gr][VAR_Z_LOWER_LIMIT];
              lowerLimit_[var - 2] = Groups_[gr][VAR_eY_LOWER_LIMIT];
              lowerLimit_[var - 1] = Groups_[gr][VAR_eZ_LOWER_LIMIT];
              upperLimit_[var - 4] = Groups_[gr][VAR_Y_UPPER_LIMIT];
              upperLimit_[var - 3] = Groups_[gr][VAR_Z_UPPER_LIMIT];
              upperLimit_[var - 2] = Groups_[gr][VAR_eY_UPPER_LIMIT];
              upperLimit_[var - 1] = Groups_[gr][VAR_eZ_UPPER_LIMIT];
            } else {
              if (Groups_[gr][SHAPE] == I_SINGLE) {
                lowerLimit_[var - 4] = Groups_[gr][VAR_Y_LOWER_LIMIT];
                lowerLimit_[var - 3] = Groups_[gr][VAR_Z_LOWER_LIMIT];
                lowerLimit_[var - 2] = Groups_[gr][VAR_eY_LOWER_LIMIT];
                lowerLimit_[var - 1] = Groups_[gr][VAR_eZ_LOWER_LIMIT];
                upperLimit_[var - 4] = Groups_[gr][VAR_Y_UPPER_LIMIT];
                upperLimit_[var - 3] = Groups_[gr][VAR_Z_UPPER_LIMIT];
                upperLimit_[var - 2] = Groups_[gr][VAR_eY_UPPER_LIMIT];
                upperLimit_[var - 1] = Groups_[gr][VAR_eZ_UPPER_LIMIT];
              } else {
                if (Groups_[gr][SHAPE] == I_DOUBLE) {
                  lowerLimit_[var - 4] = Groups_[gr][VAR_Y_LOWER_LIMIT];
                  lowerLimit_[var - 3] = Groups_[gr][VAR_Z_LOWER_LIMIT];
                  lowerLimit_[var - 2] = Groups_[gr][VAR_eY_LOWER_LIMIT];
                  lowerLimit_[var - 1] = Groups_[gr][VAR_eZ_LOWER_LIMIT];
                  upperLimit_[var - 4] = Groups_[gr][VAR_Y_UPPER_LIMIT];
                  upperLimit_[var - 3] = Groups_[gr][VAR_Z_UPPER_LIMIT];
                  upperLimit_[var - 2] = Groups_[gr][VAR_eY_UPPER_LIMIT];
                  upperLimit_[var - 1] = Groups_[gr][VAR_eZ_UPPER_LIMIT];
                } else {
                  if (Groups_[gr][SHAPE] == H_SINGLE) {
                    lowerLimit_[var - 4] = Groups_[gr][VAR_Y_LOWER_LIMIT];
                    lowerLimit_[var - 3] = Groups_[gr][VAR_Z_LOWER_LIMIT];
                    lowerLimit_[var - 2] = Groups_[gr][VAR_eY_LOWER_LIMIT];
                    lowerLimit_[var - 1] = Groups_[gr][VAR_eZ_LOWER_LIMIT];
                    upperLimit_[var - 4] = Groups_[gr][VAR_Y_UPPER_LIMIT];
                    upperLimit_[var - 3] = Groups_[gr][VAR_Z_UPPER_LIMIT];
                    upperLimit_[var - 2] = Groups_[gr][VAR_eY_UPPER_LIMIT];
                    upperLimit_[var - 1] = Groups_[gr][VAR_eZ_UPPER_LIMIT];
                  } else {
                    if (Groups_[gr][SHAPE] == H_DOUBLE) {
                      lowerLimit_[var - 4] = Groups_[gr][VAR_Y_LOWER_LIMIT];
                      lowerLimit_[var - 3] = Groups_[gr][VAR_Z_LOWER_LIMIT];
                      lowerLimit_[var - 2] = Groups_[gr][VAR_eY_LOWER_LIMIT];
                      lowerLimit_[var - 1] = Groups_[gr][VAR_eZ_LOWER_LIMIT];
                      upperLimit_[var - 4] = Groups_[gr][VAR_Y_UPPER_LIMIT];
                      upperLimit_[var - 3] = Groups_[gr][VAR_Z_UPPER_LIMIT];
                      upperLimit_[var - 2] = Groups_[gr][VAR_eY_UPPER_LIMIT];
                      upperLimit_[var - 1] = Groups_[gr][VAR_eZ_UPPER_LIMIT];
                    } else {
                      if (Groups_[gr][SHAPE] == L_SINGLE) {
                        lowerLimit_[var - 4] = Groups_[gr][VAR_Y_LOWER_LIMIT];
                        lowerLimit_[var - 3] = Groups_[gr][VAR_Z_LOWER_LIMIT];
                        lowerLimit_[var - 2] = Groups_[gr][VAR_eY_LOWER_LIMIT];
                        lowerLimit_[var - 1] = Groups_[gr][VAR_eZ_LOWER_LIMIT];
                        upperLimit_[var - 4] = Groups_[gr][VAR_Y_UPPER_LIMIT];
                        upperLimit_[var - 3] = Groups_[gr][VAR_Z_UPPER_LIMIT];
                        upperLimit_[var - 2] = Groups_[gr][VAR_eY_UPPER_LIMIT];
                        upperLimit_[var - 1] = Groups_[gr][VAR_eZ_UPPER_LIMIT];
                      } else {
                        if (Groups_[gr][SHAPE] == L_DOUBLE) {
                          lowerLimit_[var - 4] = Groups_[gr][VAR_Y_LOWER_LIMIT];
                          lowerLimit_[var - 3] = Groups_[gr][VAR_Z_LOWER_LIMIT];
                          lowerLimit_[var - 2] = Groups_[gr][VAR_eY_LOWER_LIMIT];
                          lowerLimit_[var - 1] = Groups_[gr][VAR_eZ_LOWER_LIMIT];
                          upperLimit_[var - 4] = Groups_[gr][VAR_Y_UPPER_LIMIT];
                          upperLimit_[var - 3] = Groups_[gr][VAR_Z_UPPER_LIMIT];
                          upperLimit_[var - 2] = Groups_[gr][VAR_eY_UPPER_LIMIT];
                          upperLimit_[var - 1] = Groups_[gr][VAR_eZ_UPPER_LIMIT];
                        } else {
                          if (Groups_[gr][SHAPE] == T_SINGLE) {
                            lowerLimit_[var - 4] = Groups_[gr][VAR_Y_LOWER_LIMIT];
                            lowerLimit_[var - 3] = Groups_[gr][VAR_Z_LOWER_LIMIT];
                            lowerLimit_[var - 2] = Groups_[gr][VAR_eY_LOWER_LIMIT];
                            lowerLimit_[var - 1] = Groups_[gr][VAR_eZ_LOWER_LIMIT];
                            upperLimit_[var - 4] = Groups_[gr][VAR_Y_UPPER_LIMIT];
                            upperLimit_[var - 3] = Groups_[gr][VAR_Z_UPPER_LIMIT];
                            upperLimit_[var - 2] = Groups_[gr][VAR_eY_UPPER_LIMIT];
                            upperLimit_[var - 1] = Groups_[gr][VAR_eZ_UPPER_LIMIT];
                          } else {
                            if (Groups_[gr][SHAPE] == T_DOUBLE) {
                              lowerLimit_[var - 4] = Groups_[gr][VAR_Y_LOWER_LIMIT];
                              lowerLimit_[var - 3] = Groups_[gr][VAR_Z_LOWER_LIMIT];
                              lowerLimit_[var - 2] = Groups_[gr][VAR_eY_LOWER_LIMIT];
                              lowerLimit_[var - 1] = Groups_[gr][VAR_eZ_LOWER_LIMIT];
                              upperLimit_[var - 4] = Groups_[gr][VAR_Y_UPPER_LIMIT];
                              upperLimit_[var - 3] = Groups_[gr][VAR_Z_UPPER_LIMIT];
                              upperLimit_[var - 2] = Groups_[gr][VAR_eY_UPPER_LIMIT];
                              upperLimit_[var - 1] = Groups_[gr][VAR_eZ_UPPER_LIMIT];
                            } else {
                              System.out.println("Error in LIMITES LOWER/UPPER: transversal section not considerated for: " + gr + " group");
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    setLowerLimit(new ArrayList<Double>(Arrays.<Double>asList(lowerLimit_)));
    setUpperLimit(new ArrayList<Double>(Arrays.<Double>asList(upperLimit_)));
    elementsBetweenDiffGreat_ = 0;
    for (int ba = 0; ba < numberOfElements_; ba++) {
      int i = (int) Element_[ba][i_];
      int j = (int) Element_[ba][j_];
      if (Math.abs(j - i) > elementsBetweenDiffGreat_) {
        elementsBetweenDiffGreat_ = Math.abs(j - i);
      }
    }
    matrixWidthBand_ = (elementsBetweenDiffGreat_ + 1) * numberOfLibertyDegree_;
  }

  @Override public DoubleSolution createSolution() {
    return new DefaultDoubleSolution(this);
  }

  /**
   * Evaluates a solution
   * @param solution The solution to evaluate
   */
  @Override public void evaluate(DoubleSolution solution) {
    int hi = 0;
    double[] fx = new double[getNumberOfObjectives()];
    EBEsElementsTopology(solution);
    EBEsCalculus();
    for (int j = 0; j < getNumberOfObjectives(); j++) {
      if (OF_[j].equals("W")) {
        fx[j] = 0.0;
        for (int ba = 0; ba < numberOfElements_; ba++) {
          int idx = (int) Element_[ba][INDEX_];
          fx[j] += Groups_[idx][AREA] * Element_[ba][L_] * Groups_[idx][SPECIFIC_WEIGHT];
        }
        solution.setObjective(j, fx[j]);
      } else {
        if (OF_[j].equals("D")) {
          fx[j] = 0.0;
          for (int i = 0; i < nodeCheck_.length; i++) {
            double xn = DisplacementNodes_[numberOfLibertyDegree_ * (int) nodeCheck_[i][0] + aX_][hi];
            double yn = DisplacementNodes_[numberOfLibertyDegree_ * (int) nodeCheck_[i][0] + aY_][hi];
            double zn = DisplacementNodes_[numberOfLibertyDegree_ * (int) nodeCheck_[i][0] + aZ_][hi];
            fx[j] += Math.sqrt(Math.pow(xn, 2.0) + Math.pow(yn, 2.0) + Math.pow(zn, 2.0));
          }
          solution.setObjective(j, fx[j]);
        } else {
          if (OF_[j].equals("SSAE")) {
            fx[j] = StrainResidualMin_[hi] + StrainResidualMax_[hi];
            solution.setObjective(j, fx[j]);
          } else {
            if (OF_[j].equals("ENS")) {
              fx[j] = FunctionENS(0);
              solution.setObjective(j, fx[j]);
            } else {
              if (OF_[j].equals("MDV")) {
                fx[j] = FunctionsMahalanobis_Distance_With_Variance(0);
                solution.setObjective(j, fx[j]);
              } else {
                System.out.println("Error: not considerate START OBJECTIVES FUNCTION ");
              }
            }
          }
        }
      }
    }
    numberOfEval_++;
    if ((numberOfEval_ % 1000) == 0) {
      System.out.println(numberOfEval_);
    }
  }

  /**
   * Evaluates the constraint overhead of a solution
   * @param solution The solution
   * @throws JMetalException
   */
  @Override public void evaluateConstraints(DoubleSolution solution) {
    double[] constraint = new double[this.getNumberOfConstraints()];
    double[] x = new double[getNumberOfVariables()];
    for (int i = 0; i < getNumberOfVariables(); i++) {
      x[i] = solution.getVariableValue(i);
    }
    double x1, x2, x3, x4;
    int var = 0;
    int con = 0;
    for (int gr = 0; gr < numberOfGroupElements_; gr++) {
      var += Groups_[gr][VARIABLES];
      con += Groups_[gr][CONSTRAINT];
      if (Groups_[gr][SHAPE] == CIRCLE) {
        x1 = x[var - 1];
      } else {
        if (Groups_[gr][SHAPE] == HOLE_CIRCLE) {
          x1 = x[var - 2];
          x2 = x[var - 1];
          double ratio = x1 / x2;
          if (ratio < x2 / x1) {
            ratio = x2 / x1;
          }
          constraint[con - 1] = -ratio + Groups_[gr][RATIO_YZ];
        } else {
          if (Groups_[gr][SHAPE] == RECTANGLE) {
            x1 = x[var - 2];
            x2 = x[var - 1];
            double ratio = x1 / x2;
            constraint[con - 2] = +ratio - Groups_[gr][RATIO_YZ] * 0.75;
            constraint[con - 1] = -ratio + Groups_[gr][RATIO_YZ] * 1.5;
          } else {
            if (Groups_[gr][SHAPE] == HOLE_RECTANGLE) {
              x1 = x[var - 4];
              x2 = x[var - 3];
              x3 = x[var - 2];
              x4 = x[var - 1];
              double ratio = x1 / x2;
              constraint[con - 4] = +ratio - Groups_[gr][RATIO_YZ] * 0.75;
              constraint[con - 3] = -ratio + Groups_[gr][RATIO_YZ] * 1.25;
              double tb1 = -x3 * 15 + x1;
              double tb2 = +x3 * 30 - x1;
              double ta1 = -x4 * 10 + x2;
              double ta2 = +x4 * 20 - x2;
              constraint[con - 2] = Math.min(tb1, tb2);
              constraint[con - 1] = Math.min(ta1, ta2);
            } else {
              if (Groups_[gr][SHAPE] == I_SINGLE) {
                x1 = x[var - 4];
                x2 = x[var - 3];
                x3 = x[var - 2];
                x4 = x[var - 1];
                double ratio = x1 / x2;
                constraint[con - 4] = +ratio - Groups_[gr][RATIO_YZ] * 0.75;
                constraint[con - 3] = -ratio + Groups_[gr][RATIO_YZ] * 1.25;
                double tb1 = -x3 * 15 + x1;
                double tb2 = +x3 * 30 - x1;
                double ta1 = -x4 * 10 + x2;
                double ta2 = +x4 * 20 - x2;
                constraint[con - 2] = Math.min(tb1, tb2);
                constraint[con - 1] = Math.min(ta1, ta2);
              } else {
                if (Groups_[gr][SHAPE] == I_DOUBLE) {
                  x1 = x[var - 4];
                  x2 = x[var - 3];
                  x3 = x[var - 2];
                  x4 = x[var - 1];
                  double ratio = x1 / x2;
                  constraint[con - 4] = -ratio + Groups_[gr][RATIO_YZ];
                  constraint[con - 3] = +ratio - Groups_[gr][RATIO_YZ] * 0.85;
                  constraint[con - 2] = -x1 / x3 + 1.12 * Math.sqrt(Groups_[gr][E_] / (Groups_[gr][STRESS]));
                  constraint[con - 1] = -x2 / x4 + 0.35 * Math.sqrt(Groups_[gr][E_] / (Groups_[gr][STRESS_CUT] * 2.0));
                } else {
                  if (Groups_[gr][SHAPE] == H_SINGLE) {
                    x1 = x[var - 4];
                    x2 = x[var - 3];
                    x3 = x[var - 2];
                    x4 = x[var - 1];
                    double ratio = x1 / x2;
                    constraint[con - 4] = -ratio + Groups_[gr][RATIO_YZ] * 0.85;
                    constraint[con - 3] = +ratio - Groups_[gr][RATIO_YZ];
                    constraint[con - 2] = -x1 / x3 + 0.35 * Math.sqrt(Groups_[gr][E_] / (Groups_[gr][STRESS_CUT] * 2.0));
                    constraint[con - 1] = -x2 / x4 + 1.12 * Math.sqrt(Groups_[gr][E_] / (Groups_[gr][STRESS] * 2.0));
                  } else {
                    if (Groups_[gr][SHAPE] == H_DOUBLE) {
                      x1 = x[var - 4];
                      x2 = x[var - 3];
                      x3 = x[var - 2];
                      x4 = x[var - 1];
                      double ratio = x1 / x2;
                      constraint[con - 4] = -ratio + Groups_[gr][RATIO_YZ] * 0.1;
                      constraint[con - 3] = +ratio - Groups_[gr][RATIO_YZ];
                      constraint[con - 2] = -x1 / x3 + 1.12 * Math.sqrt(Groups_[gr][E_] / (Groups_[gr][STRESS_CUT] * 2.0));
                      constraint[con - 1] = -x2 / x4 + 1.12 * Math.sqrt(Groups_[gr][E_] / (Groups_[gr][STRESS] * 2.0));
                    } else {
                      if (Groups_[gr][SHAPE] == L_SINGLE) {
                        x1 = x[var - 4];
                        x2 = x[var - 3];
                        x3 = x[var - 2];
                        x4 = x[var - 1];
                        double ratio = x1 / x2;
                        constraint[con - 4] = -ratio + Groups_[gr][RATIO_YZ];
                        constraint[con - 3] = +ratio - Groups_[gr][RATIO_YZ];
                        constraint[con - 2] = -x1 / x3 + 1.12 * Math.sqrt(Groups_[gr][E_] / (Groups_[gr][STRESS] * 2.0));
                        constraint[con - 1] = -x2 / x4 + 1.12 * Math.sqrt(Groups_[gr][E_] / (Groups_[gr][STRESS] * 2.0));
                      } else {
                        if (Groups_[gr][SHAPE] == L_DOUBLE) {
                          x1 = x[var - 4];
                          x2 = x[var - 3];
                          x3 = x[var - 2];
                          x4 = x[var - 1];
                          double ratio = x1 / x2;
                          constraint[con - 4] = -ratio + Groups_[gr][RATIO_YZ];
                          constraint[con - 3] = +ratio - Groups_[gr][RATIO_YZ];
                          constraint[con - 2] = -x1 / x3 + 1.12 * Math.sqrt(Groups_[gr][E_] / (Groups_[gr][STRESS] * 2.0));
                          constraint[con - 1] = -x2 / x4 + 1.12 * Math.sqrt(Groups_[gr][E_] / (Groups_[gr][STRESS] * 2.0));
                        } else {
                          if (Groups_[gr][SHAPE] == T_SINGLE) {
                            x1 = x[var - 4];
                            x2 = x[var - 3];
                            x3 = x[var - 2];
                            x4 = x[var - 1];
                            double ratio = x1 / x2;
                            constraint[con - 4] = -ratio + Groups_[gr][RATIO_YZ];
                            constraint[con - 3] = +ratio - Groups_[gr][RATIO_YZ] * 0.9;
                            constraint[con - 2] = -x1 / x3 + 1.12 * Math.sqrt(Groups_[gr][E_] / (Groups_[gr][STRESS] * 2.0));
                            constraint[con - 1] = -x2 / x4 + 1.12 * Math.sqrt(Groups_[gr][E_] / (Groups_[gr][STRESS] * 2.0));
                          } else {
                            if (Groups_[gr][SHAPE] == T_DOUBLE) {
                              x1 = x[var - 4];
                              x2 = x[var - 3];
                              x3 = x[var - 2];
                              x4 = x[var - 1];
                              double ratio = x1 / x2;
                              constraint[con - 4] = -ratio + Groups_[gr][RATIO_YZ];
                              constraint[con - 3] = +ratio - Groups_[gr][RATIO_YZ] * 0.9;
                              constraint[con - 2] = -x1 / x3 + 1.12 * Math.sqrt(Groups_[gr][E_] / (Groups_[gr][STRESS] * 2.0));
                              constraint[con - 1] = -x2 / x4 + 1.12 * Math.sqrt(Groups_[gr][E_] / (Groups_[gr][STRESS] * 2.0));
                            } else {
                              System.out.println("Error in constraint: transverse section not considerated for: " + gr + " group");
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    for (int hi = 0; hi < numberOfWeigthHypothesis_; hi++) {
      for (int gr = 0; gr < numberOfGroupElements_; gr++) {
        constraint[con] = (-StrainMax_[gr][hi] + Groups_[gr][STRESS]);
        con += 1;
        constraint[con] = (+StrainMin_[gr][hi] - Groups_[gr][COMPRESSION]);
        con += 1;
        constraint[con] = (-StrainCutMax_[gr][hi] + Groups_[gr][STRESS_CUT]);
        con += 1;
      }
    }
    for (int hi = 0; hi < numberOfWeigthHypothesis_; hi++) {
      double deltaN = 0;
      for (int i = 0; i < nodeCheck_.length; i++) {
        double xn = DisplacementNodes_[numberOfLibertyDegree_ * (int) nodeCheck_[i][0] + aX_][hi];
        double yn = DisplacementNodes_[numberOfLibertyDegree_ * (int) nodeCheck_[i][0] + aY_][hi];
        double zn = DisplacementNodes_[numberOfLibertyDegree_ * (int) nodeCheck_[i][0] + aZ_][hi];
        deltaN = Math.sqrt(Math.pow(xn, 2) + Math.pow(yn, 2) + Math.pow(zn, 2));
        constraint[con] = (-deltaN + nodeCheck_[i][1]);
        con += 1;
      }
    }
    double total = 0.0;
    for (int i = 0; i < this.getNumberOfConstraints(); i++) {
      if (constraint[i] < 0.0) {
        total += constraint[i];
      }
    }
    overallConstraintViolationDegree.setAttribute(solution, total);
  }

  public void EBEsElementsTopology(DoubleSolution solution) throws JMetalException {
    double[] x = new double[solution.getNumberOfVariables()];
    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
      x[i] = solution.getVariableValue(i);
    }
    double x1, x2, x3, x4;
    int var = 0;
    for (int gr = 0; gr < numberOfGroupElements_; gr++) {
      var += Groups_[gr][VARIABLES];
      if (Groups_[gr][SHAPE] == CIRCLE) {
        x1 = x[var - 1];
        EBEsTransversalSectionCircular(gr, x1);
      } else {
        if (Groups_[gr][SHAPE] == HOLE_CIRCLE) {
          x1 = x[var - 2];
          x2 = x[var - 1];
          EBEsTransversalSectionHoleCircular(gr, x1, x2);
        } else {
          if (Groups_[gr][SHAPE] == RECTANGLE) {
            x1 = x[var - 2];
            x2 = x[var - 1];
            EBEsTransversalSectionRectangle(gr, x1, x2);
          } else {
            if (Groups_[gr][SHAPE] == HOLE_RECTANGLE) {
              x1 = x[var - 4];
              x2 = x[var - 3];
              x3 = x[var - 2];
              x4 = x[var - 1];
              EBEsTransversalSectionHoleRectangle(gr, x1, x2, x3, x4);
            } else {
              if (Groups_[gr][SHAPE] == I_SINGLE) {
                x1 = x[var - 4];
                x2 = x[var - 3];
                x3 = x[var - 2];
                x4 = x[var - 1];
                EBEsTransversalSection_I_Single(gr, x1, x2, x3, x4);
              } else {
                if (Groups_[gr][SHAPE] == I_DOUBLE) {
                  x1 = x[var - 4];
                  x2 = x[var - 3];
                  x3 = x[var - 2];
                  x4 = x[var - 1];
                  EBEsTransversalSection_I_Double(gr, x1, x2, x3, x4);
                } else {
                  if (Groups_[gr][SHAPE] == H_SINGLE) {
                    x1 = x[var - 4];
                    x2 = x[var - 3];
                    x3 = x[var - 2];
                    x4 = x[var - 1];
                    EBEsTransversalSection_H_Single(gr, x1, x2, x3, x4);
                  } else {
                    if (Groups_[gr][SHAPE] == H_DOUBLE) {
                      x1 = x[var - 4];
                      x2 = x[var - 3];
                      x3 = x[var - 2];
                      x4 = x[var - 1];
                      EBEsTransversalSection_H_Double(gr, x1, x2, x3, x4);
                    } else {
                      if (Groups_[gr][SHAPE] == L_SINGLE) {
                        x1 = x[var - 4];
                        x2 = x[var - 3];
                        x3 = x[var - 2];
                        x4 = x[var - 1];
                        EBEsTransversalSection_L_Single(gr, x1, x2, x3, x4);
                      } else {
                        if (Groups_[gr][SHAPE] == L_DOUBLE) {
                          x1 = x[var - 4];
                          x2 = x[var - 3];
                          x3 = x[var - 2];
                          x4 = x[var - 1];
                          EBEsTransversalSection_L_Double(gr, x1, x2, x3, x4);
                        } else {
                          if (Groups_[gr][SHAPE] == T_SINGLE) {
                            x1 = x[var - 4];
                            x2 = x[var - 3];
                            x3 = x[var - 2];
                            x4 = x[var - 1];
                            EBEsTransversalSection_T_Single(gr, x1, x2, x3, x4);
                          } else {
                            if (Groups_[gr][SHAPE] == T_DOUBLE) {
                              x1 = x[var - 4];
                              x2 = x[var - 3];
                              x3 = x[var - 2];
                              x4 = x[var - 1];
                              EBEsTransversalSection_T_Double(gr, x1, x2, x3, x4);
                            } else {
                              System.out.println("Error in VARIABLES: transversal section not considerated for: " + gr + " group");
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  public void EBEsWeigthElement() throws JMetalException {
    for (int el = 0; el < numberOfElements_; el++) {
      int idx = (int) Element_[el][INDEX_];
      if (GravitationalAxis_ == "Z") {
        WeightElement_[el][QH_] = 0;
        WeightElement_[el][QE_] = el;
        WeightElement_[el][QT_] = CARGA_UNIFORME_TOTAL;
        WeightElement_[el][QAx_] = 0.0;
        WeightElement_[el][QAy_] = 0.0;
        WeightElement_[el][QAz_] = -Groups_[idx][AREA] * Groups_[idx][SPECIFIC_WEIGHT];
        WeightElement_[el][Qa_] = 0.0;
        WeightElement_[el][Qb_] = 0.0;
      } else {
        WeightElement_[el][QH_] = 0;
        WeightElement_[el][QE_] = el;
        WeightElement_[el][QT_] = CARGA_UNIFORME_TOTAL;
        WeightElement_[el][QAx_] = 0.0;
        WeightElement_[el][QAy_] = -Groups_[idx][AREA] * Groups_[idx][SPECIFIC_WEIGHT];
        WeightElement_[el][QAz_] = 0.0;
        WeightElement_[el][Qa_] = 0.0;
        WeightElement_[el][Qb_] = 0.0;
      }
      Qi = new double[numberOfLibertyDegree_];
      Qj = new double[numberOfLibertyDegree_];
      pi = new double[numberOfLibertyDegree_];
      pj = new double[numberOfLibertyDegree_];
      EBEsWeightDistributedUniformly(el, WeightElement_[el]);
      int hi = 0;
      int ni = (int) Element_[el][i_];
      int nj = (int) Element_[el][j_];
      PQ[numberOfLibertyDegree_ * ni + aX_][hi] += Qi[aX_];
      PQ[numberOfLibertyDegree_ * ni + aY_][hi] += Qi[aY_];
      PQ[numberOfLibertyDegree_ * ni + aZ_][hi] += Qi[aZ_];
      PQ[numberOfLibertyDegree_ * ni + gX_][hi] += Qi[gX_];
      PQ[numberOfLibertyDegree_ * ni + gY_][hi] += Qi[gY_];
      PQ[numberOfLibertyDegree_ * ni + gZ_][hi] += Qi[gZ_];
      PQ[numberOfLibertyDegree_ * nj + aX_][hi] += Qj[aX_];
      PQ[numberOfLibertyDegree_ * nj + aY_][hi] += Qj[aY_];
      PQ[numberOfLibertyDegree_ * nj + aZ_][hi] += Qj[aZ_];
      PQ[numberOfLibertyDegree_ * nj + gX_][hi] += Qj[gX_];
      PQ[numberOfLibertyDegree_ * nj + gY_][hi] += Qj[gY_];
      PQ[numberOfLibertyDegree_ * nj + gZ_][hi] += Qj[gZ_];
      cbi[aX_][el][hi] += pi[aX_];
      cbi[aY_][el][hi] += pi[aY_];
      cbi[aZ_][el][hi] += pi[aZ_];
      cbi[gX_][el][hi] += pi[gX_];
      cbi[gY_][el][hi] += pi[gY_];
      cbi[gZ_][el][hi] += pi[gZ_];
      cbj[aX_][el][hi] += pj[aX_];
      cbj[aY_][el][hi] += pj[aY_];
      cbj[aZ_][el][hi] += pj[aZ_];
      cbj[gX_][el][hi] += pj[gX_];
      cbj[gY_][el][hi] += pj[gY_];
      cbj[gZ_][el][hi] += pj[gZ_];
    }
  }

  public void EBEsCalculus() throws JMetalException {
    Efforti_ = new double[numberOfLibertyDegree_][numberOfElements_][numberOfWeigthHypothesis_];
    Effortj_ = new double[numberOfLibertyDegree_][numberOfElements_][numberOfWeigthHypothesis_];
    DisplacementNodes_ = new double[numberOfLibertyDegree_ * numberOfNodes][numberOfWeigthHypothesis_];
    Straini_ = new double[3][numberOfElements_][numberOfWeigthHypothesis_];
    Strainj_ = new double[3][numberOfElements_][numberOfWeigthHypothesis_];
    OldStrainMin_ = StrainMin_;
    StrainMin_ = new double[numberOfGroupElements_][numberOfWeigthHypothesis_];
    OldStrainMax_ = StrainMax_;
    StrainMax_ = new double[numberOfGroupElements_][numberOfWeigthHypothesis_];
    StrainCutMax_ = new double[numberOfGroupElements_][numberOfWeigthHypothesis_];
    StrainResidualMin_ = new double[numberOfWeigthHypothesis_];
    StrainResidualMax_ = new double[numberOfWeigthHypothesis_];
    StrainResidualCut_ = new double[numberOfWeigthHypothesis_];
    MatrixStiffness_ = new double[numberOfLibertyDegree_ * numberOfLibertyDegree_ * numberOfNodes * (elementsBetweenDiffGreat_ + 1)];
    WeightElement_ = new double[numberOfElements_][8];
    cbi = new double[numberOfLibertyDegree_][numberOfElements_][numberOfWeigthHypothesis_];
    cbj = new double[numberOfLibertyDegree_][numberOfElements_][numberOfWeigthHypothesis_];
    PQ = new double[numberOfLibertyDegree_ * numberOfNodes][numberOfWeigthHypothesis_];
    Reaction_ = new double[numberOfLibertyDegree_ * numberOfNodes][numberOfWeigthHypothesis_];
    EBEsWeightNodes();
    if (lLoadsOwnWeight) {
      EBEsWeigthElement();
    }
    EBEsOverloadWeightElement();
    int NumIter = 0;
    if (lSecondOrderGeometric) {
      NumIter = 1;
    }
    for (int hi = 0; hi < numberOfWeigthHypothesis_; hi++) {
      for (int countIter = 0; countIter <= NumIter; countIter++) {
        EBEsMatrixWeight(hi);
        EBEsMatrixGlobalFactory(countIter);
        EBEsMatrixGlobalPenalization();
        EBEsEcuationSolution(hi);
        EBEsEffortsElements3D(hi, countIter, DisplacementNodes_);
        EBEsEffortsTotal3D(hi);
        if (lSecondOrderGeometric && countIter == 0) {
          EBEsAssignAxialForces(hi);
          EBEsSteelingResults(hi);
        }
      }
    }
    Straini_ = EBEsStrainNode(Efforti_);
    Strainj_ = EBEsStrainNode(Effortj_);
    EBEsStrainMaxWhitGroup();
    EBEsStrainMinWhitGroup();
    EBEsStrainResidualVerication();
  }

  public void EBEsAssignAxialForces(int hi) {
    AxialForcei_ = new double[numberOfElements_];
    AxialForcej_ = new double[numberOfElements_];
    for (int el = 0; el < numberOfElements_; el++) {
      AxialForcei_[el] = Efforti_[aX_][el][hi];
      AxialForcej_[el] = Effortj_[aX_][el][hi];
    }
  }

  public void EBEsSteelingResults(int hi) {
    for (int m = 0; m < numberOfLibertyDegree_ * numberOfLibertyDegree_ * numberOfNodes * (elementsBetweenDiffGreat_ + 1); m++) {
      MatrixStiffness_[m] = 0.0;
    }
    for (int no = 0; no < numberOfLibertyDegree_ * numberOfNodes; no++) {
      DisplacementNodes_[no][hi] = 0.0;
    }
    for (int el = 0; el < Element_.length; el++) {
      Efforti_[aX_][el][hi] = 0.0;
      Efforti_[aY_][el][hi] = 0.0;
      Efforti_[aZ_][el][hi] = 0.0;
      Efforti_[gX_][el][hi] = 0.0;
      Efforti_[gY_][el][hi] = 0.0;
      Efforti_[gZ_][el][hi] = 0.0;
      Effortj_[aX_][el][hi] = 0.0;
      Effortj_[aY_][el][hi] = 0.0;
      Effortj_[aZ_][el][hi] = 0.0;
      Effortj_[gX_][el][hi] = 0.0;
      Effortj_[gY_][el][hi] = 0.0;
      Effortj_[gZ_][el][hi] = 0.0;
    }
  }

  public void EBEsMatrixWeight(int hi) {
    for (int j = 0; j < Node_.length; j++) {
      DisplacementNodes_[numberOfLibertyDegree_ * j + aX_][hi] = PQ[numberOfLibertyDegree_ * j + aX_][hi];
      DisplacementNodes_[numberOfLibertyDegree_ * j + aY_][hi] = PQ[numberOfLibertyDegree_ * j + aY_][hi];
      DisplacementNodes_[numberOfLibertyDegree_ * j + aZ_][hi] = PQ[numberOfLibertyDegree_ * j + aZ_][hi];
      DisplacementNodes_[numberOfLibertyDegree_ * j + gX_][hi] = PQ[numberOfLibertyDegree_ * j + gX_][hi];
      DisplacementNodes_[numberOfLibertyDegree_ * j + gY_][hi] = PQ[numberOfLibertyDegree_ * j + gY_][hi];
      DisplacementNodes_[numberOfLibertyDegree_ * j + gZ_][hi] = PQ[numberOfLibertyDegree_ * j + gZ_][hi];
    }
  }

  public void EBEsMatrixGlobalFactory(int countIter) throws JMetalException {
    for (int el = 0; el < numberOfElements_; el++) {
      switch ((int) Element_[el][Vij_]) {
        case 00:
        EBEsMat3DL_iRig_jRig(el);
        break;
        case 01:
        EBEsMat3DL_iRig_jArt(el);
        break;
        case 10:
        EBEsMat3DL_iArt_jRig(el);
        break;
        case 11:
        EBEsMat3DL_iArt_jArt(el);
        break;
        default:
        System.out.println("invalid link");
        return;
      }
      if (lSecondOrderGeometric && countIter == 1) {
        EBEsMat3DL_SOG(el);
        Kii = EBEsMatrixAdd(Kii, KiiSOG);
        Kij = EBEsMatrixAdd(Kij, KijSOG);
        Kji = EBEsMatrixAdd(Kji, KjiSOG);
        Kjj = EBEsMatrixAdd(Kjj, KjjSOG);
      }
      EBEsMatRot3DLpSaL(el);
      EBEsMatRot3DLaG(el);
      EBEsMat3DGij();
      EBEsMat3DG(el);
    }
  }

  public void EBEsMatrixGlobalPenalization() {
    for (int i = 0; i < numberOfNodesRestricts_; i++) {
      int no = (int) NodeRestrict_[i][0];
      String strCxyz = String.valueOf((int) NodeRestrict_[i][1]);
      String str = "";
      for (int j = numberOfLibertyDegree_; j > strCxyz.length(); j--) {
        str += "0";
      }
      strCxyz = str + strCxyz;
      char w0 = strCxyz.charAt(aX_);
      if (w0 == '1') {
        MatrixStiffness_[matrixWidthBand_ * (numberOfLibertyDegree_ * no + aX_)] = 1.0E+35;
      }
      w0 = strCxyz.charAt(aY_);
      if (w0 == '1') {
        MatrixStiffness_[matrixWidthBand_ * (numberOfLibertyDegree_ * no + aY_)] = 1.0E+35;
      }
      w0 = strCxyz.charAt(aZ_);
      if (w0 == '1') {
        MatrixStiffness_[matrixWidthBand_ * (numberOfLibertyDegree_ * no + aZ_)] = 1.0E+35;
      }
      w0 = strCxyz.charAt(gX_);
      if (w0 == '1') {
        MatrixStiffness_[matrixWidthBand_ * (numberOfLibertyDegree_ * no + gX_)] = 1.0E+35;
      }
      w0 = strCxyz.charAt(gY_);
      if (w0 == '1') {
        MatrixStiffness_[matrixWidthBand_ * (numberOfLibertyDegree_ * no + gY_)] = 1.0E+35;
      }
      w0 = strCxyz.charAt(gZ_);
      if (w0 == '1') {
        MatrixStiffness_[matrixWidthBand_ * (numberOfLibertyDegree_ * no + gZ_)] = 1.0E+35;
      }
    }
  }

  public void EBEsEffortsTotal3D(int hi) {
    for (int el = 0; el < Element_.length; el++) {
      Efforti_[aX_][el][hi] += -cbi[aX_][el][hi];
      Efforti_[aY_][el][hi] += -cbi[aY_][el][hi];
      Efforti_[aZ_][el][hi] += -cbi[aZ_][el][hi];
      Efforti_[gX_][el][hi] += -cbi[gX_][el][hi];
      Efforti_[gY_][el][hi] += -cbi[gY_][el][hi];
      Efforti_[gZ_][el][hi] += -cbi[gZ_][el][hi];
      Effortj_[aX_][el][hi] += -cbj[aX_][el][hi];
      Effortj_[aY_][el][hi] += -cbj[aY_][el][hi];
      Effortj_[aZ_][el][hi] += -cbj[aZ_][el][hi];
      Effortj_[gX_][el][hi] += -cbj[gX_][el][hi];
      Effortj_[gY_][el][hi] += -cbj[gY_][el][hi];
      Effortj_[gZ_][el][hi] += -cbj[gZ_][el][hi];
    }
  }

  public void EBEsWeightNodes() {
    for (int j = 0; j < numberOfWeigthsNodes_; j++) {
      int hi = (int) WeightNode_[j][0];
      int no = (int) WeightNode_[j][1];
      DisplacementNodes_[numberOfLibertyDegree_ * no + aX_][hi] = WeightNode_[j][2];
      DisplacementNodes_[numberOfLibertyDegree_ * no + aY_][hi] = WeightNode_[j][3];
      DisplacementNodes_[numberOfLibertyDegree_ * no + aZ_][hi] = WeightNode_[j][4];
      DisplacementNodes_[numberOfLibertyDegree_ * no + gX_][hi] = WeightNode_[j][5];
      DisplacementNodes_[numberOfLibertyDegree_ * no + gY_][hi] = WeightNode_[j][6];
      DisplacementNodes_[numberOfLibertyDegree_ * no + gZ_][hi] = WeightNode_[j][7];
      PQ[numberOfLibertyDegree_ * no + aX_][hi] = WeightNode_[j][2];
      PQ[numberOfLibertyDegree_ * no + aY_][hi] = WeightNode_[j][3];
      PQ[numberOfLibertyDegree_ * no + aZ_][hi] = WeightNode_[j][4];
      PQ[numberOfLibertyDegree_ * no + gX_][hi] = WeightNode_[j][5];
      PQ[numberOfLibertyDegree_ * no + gY_][hi] = WeightNode_[j][6];
      PQ[numberOfLibertyDegree_ * no + gZ_][hi] = WeightNode_[j][7];
    }
  }

  public void EBEsOverloadWeightElement() throws JMetalException {
    for (int i = 0; i < numberOfWeigthsElements_; i++) {
      Qi = new double[numberOfLibertyDegree_];
      Qj = new double[numberOfLibertyDegree_];
      pi = new double[numberOfLibertyDegree_];
      pj = new double[numberOfLibertyDegree_];
      int el = (int) OverloadInElement_[i][QE_];
      switch ((int) OverloadInElement_[i][QT_]) {
        case 0:
        EBEsWeightDistributedUniformly(el, OverloadInElement_[i]);
        break;
        default:
        System.out.println("invalid link");
        return;
      }
      int hi = (int) OverloadInElement_[i][QH_];
      int ni = (int) Element_[el][i_];
      int nj = (int) Element_[el][j_];
      PQ[numberOfLibertyDegree_ * ni + aX_][hi] += Qi[aX_];
      PQ[numberOfLibertyDegree_ * ni + aY_][hi] += Qi[aY_];
      PQ[numberOfLibertyDegree_ * ni + aZ_][hi] += Qi[aZ_];
      PQ[numberOfLibertyDegree_ * ni + gX_][hi] += Qi[gX_];
      PQ[numberOfLibertyDegree_ * ni + gY_][hi] += Qi[gY_];
      PQ[numberOfLibertyDegree_ * ni + gZ_][hi] += Qi[gZ_];
      PQ[numberOfLibertyDegree_ * nj + aX_][hi] += Qj[aX_];
      PQ[numberOfLibertyDegree_ * nj + aY_][hi] += Qj[aY_];
      PQ[numberOfLibertyDegree_ * nj + aZ_][hi] += Qj[aZ_];
      PQ[numberOfLibertyDegree_ * nj + gX_][hi] += Qj[gX_];
      PQ[numberOfLibertyDegree_ * nj + gY_][hi] += Qj[gY_];
      PQ[numberOfLibertyDegree_ * nj + gZ_][hi] += Qj[gZ_];
      cbi[aX_][el][hi] += pi[aX_];
      cbi[aY_][el][hi] += pi[aY_];
      cbi[aZ_][el][hi] += pi[aZ_];
      cbi[gX_][el][hi] += pi[gX_];
      cbi[gY_][el][hi] += pi[gY_];
      cbi[gZ_][el][hi] += pi[gZ_];
      cbj[aX_][el][hi] += pj[aX_];
      cbj[aY_][el][hi] += pj[aY_];
      cbj[aZ_][el][hi] += pj[aZ_];
      cbj[gX_][el][hi] += pj[gX_];
      cbj[gY_][el][hi] += pj[gY_];
      cbj[gZ_][el][hi] += pj[gZ_];
    }
  }

  public void EBEsWeightDistributedUniformly(int el, double[] LoadInElement_) throws JMetalException {
    int vi, vj;
    double xi, xj, yi, yj, zi, zj;
    double[][] R = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];
    switch ((int) Element_[el][Vij_]) {
      case 00:
      {
        vi = 0;
        vj = 0;
        break;
      }
      case 01:
      {
        vi = 0;
        vj = 1;
        break;
      }
      case 10:
      {
        vi = 1;
        vj = 0;
        break;
      }
      case 11:
      {
        vi = 1;
        vj = 1;
        break;
      }
      default:
      System.out.println("invalid link");
      return;
    }
    int ni = (int) Element_[el][i_];
    int nj = (int) Element_[el][j_];
    xi = Node_[ni][aX_];
    yi = Node_[ni][aY_];
    zi = Node_[ni][aZ_];
    xj = Node_[nj][aX_];
    yj = Node_[nj][aY_];
    zj = Node_[nj][aZ_];
    double A1 = Math.asin((xi - xj) / Element_[el][L_]);
    double lx = Element_[el][L_] * Math.cos(A1);
    double B1 = Math.asin((yi - yj) / Element_[el][L_]);
    double ly = Element_[el][L_] * Math.cos(B1);
    double G1 = Math.asin((zi - zj) / Element_[el][L_]);
    double lz = Element_[el][L_] * Math.cos(G1);
    if (vi == 0 && vj == 0) {
      if (Math.abs(lx) < 0.0000001 && ly != 0 && lz != 0.0) {
        Qi[aX_] = LoadInElement_[QAx_] * Math.abs((xi - xj)) / 2.0;
        Qj[aX_] = Qi[aX_];
      } else {
        Qi[aX_] = LoadInElement_[QAx_] * lx / 2.0;
        Qj[aX_] = Qi[aX_];
      }
      if ((xi - xj) == 0 && (zi - zj) == 0.0) {
        Qi[aY_] = LoadInElement_[QAy_] * Math.abs((yi - yj)) / 2.0;
        Qj[aY_] = Qi[aY_];
      } else {
        Qi[aY_] = LoadInElement_[QAy_] * ly / 2.0;
        Qj[aY_] = Qi[aY_];
      }
      if (Math.abs(lz) < 0.0000001 && lx != 0 && ly != 0.0) {
        Qi[aZ_] = LoadInElement_[QAz_] * Math.abs((zi - zj)) / 2.0;
        Qj[aZ_] = Qi[aZ_];
      } else {
        Qi[aZ_] = LoadInElement_[QAz_] * lz / 2.0;
        Qj[aZ_] = Qi[aZ_];
      }
      Qi[gX_] = (LoadInElement_[QAy_] * ly * (zi - zj) - LoadInElement_[QAz_] * lz * (yi - yj)) / 12.0;
      Qj[gX_] = -Qi[gX_];
      Qi[gY_] = (LoadInElement_[QAz_] * lz * (xi - xj) - LoadInElement_[QAx_] * lx * (zi - zj)) / 12.0;
      Qj[gY_] = -Qi[gY_];
      Qi[gZ_] = (LoadInElement_[QAx_] * lx * (yi - yj) - LoadInElement_[QAy_] * ly * (xi - xj)) / 12.0;
      Qj[gZ_] = -Qi[gZ_];
    } else {
      if (vi == 1 && vj == 1) {
        if (Math.abs(lx) < 0.0000001 && ly != 0 && lz != 0.0) {
          Qi[aX_] = LoadInElement_[QAx_] * Math.abs((xi - xj)) / 2.0;
          Qj[aX_] = Qi[aX_];
        } else {
          Qi[aX_] = LoadInElement_[QAx_] * lx / 2.0;
          Qj[aX_] = Qi[aX_];
        }
        if ((xi - xj) == 0 && (zi - zj) == 0.0) {
          Qi[aY_] = LoadInElement_[QAy_] * Math.abs((yi - yj)) / 2.0;
          Qj[aY_] = Qi[aY_];
        } else {
          Qi[aY_] = LoadInElement_[QAy_] * ly / 2.0;
          Qj[aY_] = Qi[aY_];
        }
        if (Math.abs(lz) < 0.0000001 && lx != 0 && ly != 0.0) {
          Qi[aZ_] = LoadInElement_[QAz_] * Math.abs((zi - zj)) / 2.0;
          Qj[aY_] = Qi[aY_];
        } else {
          Qi[aY_] = LoadInElement_[QAz_] * lz / 2.0;
          Qj[aY_] = Qi[aY_];
        }
        Qi[gX_] = 0.0;
        Qj[gX_] = 0.0;
        Qi[gY_] = 0.0;
        Qj[gY_] = 0.0;
        Qi[gZ_] = 0.0;
        Qj[gZ_] = 0.0;
      } else {
        if (vi == 1 && vj == 0) {
          if (Math.abs(lx) < 0.0000001 && ly != 0 && lz != 0) {
            Qi[aX_] = LoadInElement_[QAx_] * Math.abs((xi - xj)) / 2.0;
            Qj[aX_] = Qi[aX_];
          } else {
            Qi[aX_] = LoadInElement_[QAx_] * lx / 2.0;
            Qj[aX_] = Qi[aX_];
          }
          if ((xi - xj) == 0.0 && (zi - zj) == 0.0) {
            Qi[aY_] = 3.0 / 8.0 * LoadInElement_[QAy_] * Math.abs((yi - yj));
            Qj[aY_] = 5.0 / 8.0 * LoadInElement_[QAy_] * Math.abs((yi - yj));
          } else {
            Qi[aY_] = 3.0 / 8.0 * LoadInElement_[QAy_] * ly;
            Qj[aY_] = 5.0 / 8.0 * LoadInElement_[QAy_] * ly;
          }
          if (Math.abs(lz) < 0.0000001 && lx != 0 && ly != 0.0) {
            Qi[aZ_] = LoadInElement_[QAz_] * Math.abs((zi - zj)) / 2.0;
            Qj[aZ_] = Qi[2];
          } else {
            Qi[aZ_] = 3.0 / 8.0 * LoadInElement_[QAz_] * lz;
            Qj[aZ_] = 5.0 / 8.0 * LoadInElement_[QAz_] * lz;
          }
          Qi[gX_] = 0.0;
          Qj[gX_] = -(LoadInElement_[QAy_] * ly * (zi - zj) - LoadInElement_[QAz_] * lz * (yi - yj)) / 8.0;
          Qi[gY_] = 0.0;
          Qj[gY_] = -(LoadInElement_[QAz_] * lz * (xi - xj) - LoadInElement_[QAx_] * lx * (zi - zj)) / 8.0;
          Qi[gZ_] = 0.0;
          Qj[gZ_] = -(LoadInElement_[QAx_] * lx * (yi - yj) - LoadInElement_[QAy_] * ly * (xi - xj)) / 8.0;
        } else {
          if (vi == 0 && vj == 1) {
            if (Math.abs(lx) < 0.0000001 && ly != 0 && lz != 0) {
              Qi[aX_] = LoadInElement_[QAx_] * Math.abs((xi - xj)) / 2.0;
              Qj[aX_] = Qi[aX_];
            } else {
              Qi[aX_] = LoadInElement_[QAx_] * lx / 2.0;
              Qj[aX_] = Qi[aX_];
            }
            if ((xi - xj) == 0.0 && (zi - zj) == 0.0) {
              Qi[aY_] = 5.0 / 8.0 * LoadInElement_[QAy_] * Math.abs((yi - yj));
              Qj[aY_] = 3.0 / 8.0 * LoadInElement_[QAy_] * Math.abs((yi - yj));
            } else {
              Qi[aY_] = 5.0 / 8.0 * LoadInElement_[QAy_] * ly;
              Qj[aY_] = 3.0 / 8.0 * LoadInElement_[QAy_] * ly;
            }
            if (Math.abs(lz) < 0.0000001 && lx != 0.0 && ly != 0.0) {
              Qi[aZ_] = LoadInElement_[QAz_] * Math.abs((zi - zj)) / 2.0;
              Qj[aZ_] = Qi[aZ_];
            } else {
              Qi[aZ_] = 5.0 / 8.0 * LoadInElement_[QAz_] * lz / 2.0;
              Qj[aZ_] = 3.0 / 8.0 * LoadInElement_[QAz_] * lz / 2.0;
            }
            Qi[gX_] = (LoadInElement_[QAy_] * ly * (zi - zj) - LoadInElement_[QAz_] * lz * (yi - yj)) / 8.0;
            Qj[gX_] = 0.0;
            Qi[gY_] = (LoadInElement_[QAz_] * lz * (xi - xj) - LoadInElement_[QAx_] * lx * (zi - zj)) / 8.0;
            Qj[gY_] = 0.0;
            Qi[gZ_] = (LoadInElement_[QAx_] * lx * (yi - yj) - LoadInElement_[QAy_] * ly * (xi - xj)) / 8.0;
            Qj[gZ_] = 0.0;
          } else {
            if (vi == 0 && vj == 9) {
              if (Math.abs(lx) < 0.0000001 && ly != 0.0 && lz != 0.0) {
                Qi[aX_] = LoadInElement_[QAx_] * Math.abs((xi - xj)) / 2.0;
                Qj[aX_] = Qi[aX_];
              } else {
                Qi[aX_] = LoadInElement_[QAx_] * lx;
                Qj[aX_] = 0.0;
              }
              if ((xi - xj) == 0.0 && (zi - zj) == 0.0) {
                Qi[aY_] = LoadInElement_[QAy_] * Math.abs((yi - yj));
                Qj[aY_] = 0.0;
              } else {
                Qi[aY_] = LoadInElement_[QAy_] * ly;
                Qj[aY_] = 0.0;
              }
              if (Math.abs(lz) < 0.0000001 && lx != 0.0 && ly != 0.0) {
                Qi[aZ_] = LoadInElement_[QAz_] * Math.abs((zi - zj)) / 2.0;
                Qj[aZ_] = Qi[aZ_];
              } else {
                Qi[aZ_] = LoadInElement_[QAz_] * lz;
                Qj[aZ_] = 0.0;
              }
              Qi[gX_] = (LoadInElement_[QAy_] * ly * (zi - zj) - LoadInElement_[QAz_] * lz * (yi - yj)) / 2.0;
              Qj[gX_] = 0.0;
              Qi[gY_] = (LoadInElement_[QAz_] * lz * (xi - xj) - LoadInElement_[QAx_] * lx * (zi - zj)) / 2.0;
              Qj[gY_] = 0.0;
              Qi[gZ_] = (LoadInElement_[QAx_] * lx * (yi - yj) - LoadInElement_[QAy_] * ly * (xi - xj)) / 2.0;
              Qj[gZ_] = 0.0;
            } else {
              if (vi == 9 && vj == 0) {
                if (Math.abs(lx) < 0.0000001 && ly != 0.0 && lz != 0) {
                  Qi[aX_] = LoadInElement_[QAx_] * Math.abs((xi - xj)) / 2.0;
                  Qj[aX_] = Qi[aX_];
                } else {
                  Qi[aX_] = 0;
                  Qj[aX_] = LoadInElement_[QAx_] * lx;
                }
                if ((xi - xj) == 0.0 && (zi - zj) == 0.0) {
                  Qi[aY_] = 0;
                  Qj[aY_] = LoadInElement_[QAy_] * Math.abs((yi - yj));
                } else {
                  Qi[aY_] = 0.0;
                  Qj[aY_] = LoadInElement_[QAy_] * ly;
                }
                if (Math.abs(lz) < 0.0000001 && lx != 0.0 && ly != 0.0) {
                  Qi[aZ_] = LoadInElement_[QAz_] * Math.abs((zi - zj)) / 2.0;
                  Qj[aZ_] = Qi[aZ_];
                } else {
                  Qi[aZ_] = 0.0;
                  Qj[aZ_] = LoadInElement_[QAz_] * lz;
                }
                Qi[gX_] = 0.0;
                Qj[gX_] = -(LoadInElement_[QAy_] * ly * (zi - zj) - LoadInElement_[QAz_] * lz * (yi - yj)) / 2.0;
                Qi[gY_] = 0.0;
                Qj[gY_] = -(LoadInElement_[QAz_] * lz * (xi - xj) - LoadInElement_[QAx_] * lx * (zi - zj)) / 2.0;
                Qi[gZ_] = 0.0;
                Qj[gZ_] = -(LoadInElement_[QAx_] * lx * (yi - yj) - LoadInElement_[QAy_] * ly * (xi - xj)) / 2.0;
              } else {
                if ((vi == 0 && vj == 2) || (vi == 2 && vj == 0)) {
                  System.out.println("invalid link");
                } else {
                  if ((vi == 1 && vj == 2) || (vi == 2 && vj == 1)) {
                    System.out.println("invalid link");
                  } else {
                    if ((vi == 2 && vj == 3) || (vi == 3 && vj == 2)) {
                      System.out.println("invalid link");
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    EBEsMatRot3DLpSaL(el);
    EBEsMatRot3DLaG(el);
    R = EBEsMatrizMultiplicar(Rpij, Rij);
    pi = EBEsMatrizVectorMultiplicar(R, Qi);
    R = EBEsMatrizMultiplicar(Rpji, Rji);
    pj = EBEsMatrizVectorMultiplicar(R, Qj);
  }

  public void EBEsMatRot3DLpSaL(int e) {
    int i, j;
    double lx;
    double mx;
    double nx;
    double ly;
    double my;
    double ny;
    double lz;
    double mz;
    double nz;
    int idx = (int) Element_[e][INDEX_];
    double beta = Groups_[idx][BETA];
    lx = 1.0;
    mx = 0.0;
    nx = 0.0;
    ly = 0.0;
    my = Math.cos(beta * Math.PI / 180.0);
    ny = Math.sin(beta * Math.PI / 180.0);
    lz = 0.0;
    mz = -Math.sin(beta * Math.PI / 180.0);
    nz = Math.cos(beta * Math.PI / 180.0);
    Rpij[0][0] = lx;
    Rpij[0][1] = mx;
    Rpij[0][2] = nx;
    Rpij[1][0] = ly;
    Rpij[1][1] = my;
    Rpij[1][2] = ny;
    Rpij[2][0] = lz;
    Rpij[2][1] = mz;
    Rpij[2][2] = nz;
    for (i = 0; i < 3; i++) {
      for (j = 3; j < 6; j++) {
        Rpij[i][j] = 0.0;
      }
    }
    for (i = 3; i < 6; i++) {
      for (j = 0; j < 3; j++) {
        Rpij[i][j] = 0.0;
      }
    }
    Rpij[3][3] = lx;
    Rpij[3][4] = mx;
    Rpij[3][5] = nx;
    Rpij[4][3] = ly;
    Rpij[4][4] = my;
    Rpij[4][5] = ny;
    Rpij[5][3] = lz;
    Rpij[5][4] = mz;
    Rpij[5][5] = nz;
    RpTij = EBEsMatrizTraspuesta(Rpij);
    lx = 1.0;
    mx = 0.0;
    nx = 0.0;
    ly = 0.0;
    my = Math.cos(beta * Math.PI / 180.0);
    ny = -Math.sin(beta * Math.PI / 180.0);
    lz = 0.0;
    mz = Math.sin(beta * Math.PI / 180.0);
    nz = Math.cos(beta * Math.PI / 180.0);
    Rpji[0][0] = lx;
    Rpji[0][1] = mx;
    Rpji[0][2] = nx;
    Rpji[1][0] = ly;
    Rpji[1][1] = my;
    Rpji[1][2] = ny;
    Rpji[2][0] = lz;
    Rpji[2][1] = mz;
    Rpji[2][2] = nz;
    for (i = 0; i < 3; i++) {
      for (j = 3; j < 6; j++) {
        Rpji[i][j] = 0.0;
      }
    }
    for (i = 3; i < 6; i++) {
      for (j = 0; j < 3; j++) {
        Rpji[i][j] = 0.0;
      }
    }
    Rpji[3][3] = lx;
    Rpji[3][4] = mx;
    Rpji[3][5] = nx;
    Rpji[4][3] = ly;
    Rpji[4][4] = my;
    Rpji[4][5] = ny;
    Rpji[5][3] = lz;
    Rpji[5][4] = mz;
    Rpji[5][5] = nz;
    RpTji = EBEsMatrizTraspuesta(Rpji);
  }

  public double[][] EBEsMatrizTraspuesta(double[][] m) {
    int row = m.length;
    int col = m[0].length;
    double[][] mt = new double[row][col];
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        mt[j][i] = m[i][j];
      }
    }
    return mt;
  }

  public void EBEsEcuationSolution(int hi) throws JMetalException {
    int i, j;
    int s1 = 1;
    int s2, l5, l6, ln, r;
    double det = 1.0;
    double ff = 0.0, t;
    int n2 = numberOfLibertyDegree_ * numberOfNodes;
    for (i = 1; i < n2; i++) {
      if (MatrixStiffness_[s1 - 1] >= 1.0E+25) {
        s1 = s1 + matrixWidthBand_;
        continue;
      }
      ln = i + 1;
      l5 = s1 + 1;
      for (j = 2; j < matrixWidthBand_ + 1; j++) {
        if (ln - n2 > 0) {
          break;
        }
        if (MatrixStiffness_[s1 - 1] == 0) {
          ln = ln + 1;
          l5 = l5 + 1;
          continue;
        }
        t = MatrixStiffness_[l5 - 1] / MatrixStiffness_[s1 - 1];
        l6 = (ln - 1) * matrixWidthBand_ + 1;
        s2 = s1 + j - 1;
        for (r = j; r < matrixWidthBand_ + 1; r++) {
          MatrixStiffness_[l6 - 1] = MatrixStiffness_[l6 - 1] - t * MatrixStiffness_[s2 - 1];
          l6 = l6 + 1;
          s2 = s2 + 1;
        }
        DisplacementNodes_[ln - 1][hi] = DisplacementNodes_[ln - 1][hi] - t * DisplacementNodes_[i - 1][hi];
        ln = ln + 1;
        l5 = l5 + 1;
      }
      s1 = s1 + matrixWidthBand_;
    }
    i = n2 + 1;
    for (int s3 = 1; s3 < n2 + 1; s3++) {
      i = i - 1;
      ff = 0.0;
      ln = i + 1;
      l5 = s1 + 1;
      for (j = 2; j < matrixWidthBand_ + 1; j++) {
        if (ln - n2 > 0) {
          break;
        }
        ff = ff + DisplacementNodes_[ln - 1][hi] * MatrixStiffness_[l5 - 1];
        ln = ln + 1;
        l5 = l5 + 1;
      }
      if (Math.abs(MatrixStiffness_[s1 - 1]) <= 1.0E-35) {
        DisplacementNodes_[i - 1][hi] = 1.0E-35;
      } else {
        DisplacementNodes_[i - 1][hi] = (DisplacementNodes_[i - 1][hi] - ff) / MatrixStiffness_[s1 - 1];
      }
      if (MatrixStiffness_[s1 - 1] < 9.899999E+15) {
        det = det * MatrixStiffness_[s1 - 1] / 100000.0;
      }
      s1 = s1 - matrixWidthBand_;
    }
  }

  public void EBEsMat3DL_iRig_jRig(int e) throws JMetalException {
    double Lij = Element_[e][L_];
    int idx = (int) Element_[e][INDEX_];
    double S = Groups_[idx][AREA];
    double Iz = Groups_[idx][Iz_];
    double Iy = Groups_[idx][Iy_];
    double Ip = Groups_[idx][It_];
    double E = Groups_[idx][E_];
    double G = Groups_[idx][G_];
    Kii[0][0] = E * S / Lij;
    Kii[0][1] = 0;
    Kii[0][2] = 0;
    Kii[0][3] = 0;
    Kii[0][4] = 0;
    Kii[0][5] = 0;
    Kii[1][0] = 0;
    Kii[1][1] = 12 * E * Iz / Math.pow(Lij, 3);
    Kii[1][2] = 0;
    Kii[1][3] = 0;
    Kii[1][4] = 0;
    Kii[1][5] = -6 * E * Iz / Math.pow(Lij, 2);
    Kii[2][0] = 0;
    Kii[2][1] = 0;
    Kii[2][2] = 12 * E * Iy / Math.pow(Lij, 3);
    Kii[2][3] = 0;
    Kii[2][4] = 6 * E * Iy / Math.pow(Lij, 2);
    Kii[2][5] = 0;
    Kii[3][0] = 0;
    Kii[3][1] = 0;
    Kii[3][2] = 0;
    Kii[3][3] = G * Ip / Lij;
    Kii[3][4] = 0;
    Kii[3][5] = 0;
    Kii[4][0] = 0;
    Kii[4][1] = 0;
    Kii[4][2] = 6 * E * Iy / Math.pow(Lij, 2);
    Kii[4][3] = 0;
    Kii[4][4] = 4 * E * Iy / Lij;
    Kii[4][5] = 0;
    Kii[5][0] = 0;
    Kii[5][1] = -6 * E * Iz / Math.pow(Lij, 2);
    Kii[5][2] = 0;
    Kii[5][3] = 0;
    Kii[5][4] = 0;
    Kii[5][5] = 4 * E * Iz / Lij;
    Kij[0][0] = E * S / Lij;
    Kij[0][1] = 0;
    Kij[0][2] = 0;
    Kij[0][3] = 0;
    Kij[0][4] = 0;
    Kij[0][5] = 0;
    Kij[1][0] = 0;
    Kij[1][1] = 12 * E * Iz / Math.pow(Lij, 3);
    Kij[1][2] = 0;
    Kij[1][3] = 0;
    Kij[1][4] = 0;
    Kij[1][5] = -6 * E * Iz / Math.pow(Lij, 2);
    Kij[2][0] = 0;
    Kij[2][1] = 0;
    Kij[2][2] = -12 * E * Iy / Math.pow(Lij, 3);
    Kij[2][3] = 0;
    Kij[2][4] = -6 * E * Iy / Math.pow(Lij, 2);
    Kij[2][5] = 0;
    Kij[3][0] = 0;
    Kij[3][1] = 0;
    Kij[3][2] = 0;
    Kij[3][3] = G * Ip / Lij;
    Kij[3][4] = 0;
    Kij[3][5] = 0;
    Kij[4][0] = 0;
    Kij[4][1] = 0;
    Kij[4][2] = -6 * E * Iy / Math.pow(Lij, 2);
    Kij[4][3] = 0;
    Kij[4][4] = -2 * E * Iy / Lij;
    Kij[4][5] = 0;
    Kij[5][0] = 0;
    Kij[5][1] = -6 * E * Iz / Math.pow(Lij, 2);
    Kij[5][2] = 0;
    Kij[5][3] = 0;
    Kij[5][4] = 0;
    Kij[5][5] = 2 * E * Iz / Lij;
    Kji[0][0] = E * S / Lij;
    Kji[0][1] = 0;
    Kji[0][2] = 0;
    Kji[0][3] = 0;
    Kji[0][4] = 0;
    Kji[0][5] = 0;
    Kji[1][0] = 0;
    Kji[1][1] = 12 * E * Iz / Math.pow(Lij, 3);
    Kji[1][2] = 0;
    Kji[1][3] = 0;
    Kji[1][4] = 0;
    Kji[1][5] = -6 * E * Iz / Math.pow(Lij, 2);
    Kji[2][0] = 0;
    Kji[2][1] = 0;
    Kji[2][2] = -12 * E * Iy / Math.pow(Lij, 3);
    Kji[2][3] = 0;
    Kji[2][4] = -6 * E * Iy / Math.pow(Lij, 2);
    Kji[2][5] = 0;
    Kji[3][0] = 0;
    Kji[3][1] = 0;
    Kji[3][2] = 0;
    Kji[3][3] = G * Ip / Lij;
    Kji[3][4] = 0;
    Kji[3][5] = 0;
    Kji[4][0] = 0;
    Kji[4][1] = 0;
    Kji[4][2] = -6 * E * Iy / Math.pow(Lij, 2);
    Kji[4][3] = 0;
    Kji[4][4] = -2 * E * Iy / Lij;
    Kji[4][5] = 0;
    Kji[5][0] = 0;
    Kji[5][1] = -6 * E * Iz / Math.pow(Lij, 2);
    Kji[5][2] = 0;
    Kji[5][3] = 0;
    Kji[5][4] = 0;
    Kji[5][5] = 2 * E * Iz / Lij;
    Kjj[0][0] = E * S / Lij;
    Kjj[0][1] = 0;
    Kjj[0][2] = 0;
    Kjj[0][3] = 0;
    Kjj[0][4] = 0;
    Kjj[0][5] = 0;
    Kjj[1][0] = 0;
    Kjj[1][1] = 12 * E * Iz / Math.pow(Lij, 3);
    Kjj[1][2] = 0;
    Kjj[1][3] = 0;
    Kjj[1][4] = 0;
    Kjj[1][5] = -6 * E * Iz / Math.pow(Lij, 2);
    Kjj[2][0] = 0;
    Kjj[2][1] = 0;
    Kjj[2][2] = 12 * E * Iy / Math.pow(Lij, 3);
    Kjj[2][3] = 0;
    Kjj[2][4] = 6 * E * Iy / Math.pow(Lij, 2);
    Kjj[2][5] = 0;
    Kjj[3][0] = 0;
    Kjj[3][1] = 0;
    Kjj[3][2] = 0;
    Kjj[3][3] = G * Ip / Lij;
    Kjj[3][4] = 0;
    Kjj[3][5] = 0;
    Kjj[4][0] = 0;
    Kjj[4][1] = 0;
    Kjj[4][2] = 6 * E * Iy / Math.pow(Lij, 2);
    Kjj[4][3] = 0;
    Kjj[4][4] = 4 * E * Iy / Lij;
    Kjj[4][5] = 0;
    Kjj[5][0] = 0;
    Kjj[5][1] = -6 * E * Iz / Math.pow(Lij, 2);
    Kjj[5][2] = 0;
    Kjj[5][3] = 0;
    Kjj[5][4] = 0;
    Kjj[5][5] = 4 * E * Iz / Lij;
  }

  public void EBEsMat3DL_iArt_jRig(int e) throws JMetalException {
    double Lij = Element_[e][L_];
    int idx = (int) Element_[e][INDEX_];
    double S = Groups_[idx][AREA];
    double Iz = Groups_[idx][Iz_];
    double Iy = Groups_[idx][Iy_];
    double E = Groups_[idx][E_];
    Kii[0][0] = E * S / Lij;
    Kii[0][1] = 0;
    Kii[0][2] = 0;
    Kii[0][3] = 0;
    Kii[0][4] = 0;
    Kii[0][5] = 0;
    Kii[1][0] = 0;
    Kii[1][1] = 3 * E * Iz / Math.pow(Lij, 3);
    Kii[1][2] = 0;
    Kii[1][3] = 0;
    Kii[1][4] = 0;
    Kii[1][5] = 0;
    Kii[2][0] = 0;
    Kii[2][1] = 0;
    Kii[2][2] = 3 * E * Iy / Math.pow(Lij, 3);
    Kii[2][3] = 0;
    Kii[2][4] = 0;
    Kii[2][5] = 0;
    Kii[3][0] = 0;
    Kii[3][1] = 0;
    Kii[3][2] = 0;
    Kii[3][3] = 0;
    Kii[3][4] = 0;
    Kii[3][5] = 0;
    Kii[4][0] = 0;
    Kii[4][1] = 0;
    Kii[4][2] = 0;
    Kii[4][3] = 0;
    Kii[4][4] = 0;
    Kii[4][5] = 0;
    Kii[5][0] = 0;
    Kii[5][1] = 0;
    Kii[5][2] = 0;
    Kii[5][3] = 0;
    Kii[5][4] = 0;
    Kii[5][5] = 0;
    Kij[0][0] = E * S / Lij;
    Kij[0][1] = 0;
    Kij[0][2] = 0;
    Kij[0][3] = 0;
    Kij[0][4] = 0;
    Kij[0][5] = 0;
    Kij[1][0] = 0;
    Kij[1][1] = 3 * E * Iz / Math.pow(Lij, 3);
    Kij[1][2] = 0;
    Kij[1][3] = 0;
    Kij[1][4] = 0;
    Kij[1][5] = -3 * E * Iz / Math.pow(Lij, 2);
    Kij[2][0] = 0;
    Kij[2][1] = 0;
    Kij[2][2] = -3 * E * Iy / Math.pow(Lij, 3);
    Kij[2][3] = 0;
    Kij[2][4] = -3 * E * Iy / Math.pow(Lij, 2);
    Kij[2][5] = 0;
    Kij[3][0] = 0;
    Kij[3][1] = 0;
    Kij[3][2] = 0;
    Kij[3][3] = 0;
    Kij[3][4] = 0;
    Kij[3][5] = 0;
    Kij[4][0] = 0;
    Kij[4][1] = 0;
    Kij[4][2] = 0;
    Kij[4][3] = 0;
    Kij[4][4] = 0;
    Kij[4][5] = 0;
    Kij[5][0] = 0;
    Kij[5][1] = 0;
    Kij[5][2] = 0;
    Kij[5][3] = 0;
    Kij[5][4] = 0;
    Kij[5][5] = 0;
    Kji[0][0] = E * S / Lij;
    Kji[0][1] = 0;
    Kji[0][2] = 0;
    Kji[0][3] = 0;
    Kji[0][4] = 0;
    Kji[0][5] = 0;
    Kji[1][0] = 0;
    Kji[1][1] = 3 * E * Iz / Math.pow(Lij, 3);
    Kji[1][2] = 0;
    Kji[1][3] = 0;
    Kji[1][4] = 0;
    Kji[1][5] = 0;
    Kji[2][0] = 0;
    Kji[2][1] = 0;
    Kji[2][2] = -3 * E * Iy / Math.pow(Lij, 3);
    Kji[2][3] = 0;
    Kji[2][4] = 0;
    Kji[2][5] = 0;
    Kji[3][0] = 0;
    Kji[3][1] = 0;
    Kji[3][2] = 0;
    Kji[3][3] = 0;
    Kji[3][4] = 0;
    Kji[3][5] = 0;
    Kji[4][0] = 0;
    Kji[4][1] = 0;
    Kji[4][2] = -3 * E * Iy / Math.pow(Lij, 2);
    Kji[4][3] = 0;
    Kji[4][4] = 0;
    Kji[4][5] = 0;
    Kji[5][0] = 0;
    Kji[5][1] = -3 * E * Iz / Math.pow(Lij, 2);
    Kji[5][2] = 0;
    Kji[5][3] = 0;
    Kji[5][4] = 0;
    Kji[5][5] = 0;
    Kjj[0][0] = E * S / Lij;
    Kjj[0][1] = 0;
    Kjj[0][2] = 0;
    Kjj[0][3] = 0;
    Kjj[0][4] = 0;
    Kjj[0][5] = 0;
    Kjj[1][0] = 0;
    Kjj[1][1] = 3 * E * Iz / Math.pow(Lij, 3);
    Kjj[1][2] = 0;
    Kjj[1][3] = 0;
    Kjj[1][4] = 0;
    Kjj[1][5] = -3 * E * Iz / Math.pow(Lij, 2);
    Kjj[2][0] = 0;
    Kjj[2][1] = 0;
    Kjj[2][2] = 3 * E * Iy / Math.pow(Lij, 3);
    Kjj[2][3] = 0;
    Kjj[2][4] = 3 * E * Iy / Math.pow(Lij, 2);
    Kjj[2][5] = 0;
    Kjj[3][0] = 0;
    Kjj[3][1] = 0;
    Kjj[3][2] = 0;
    Kjj[3][3] = 0;
    Kjj[3][4] = 0;
    Kjj[3][5] = 0;
    Kjj[4][0] = 0;
    Kjj[4][1] = 0;
    Kjj[4][2] = 3 * E * Iy / Math.pow(Lij, 2);
    Kjj[4][3] = 0;
    Kjj[4][4] = 3 * E * Iy / Lij;
    Kjj[4][5] = 0;
    Kjj[5][0] = 0;
    Kjj[5][1] = -3 * E * Iz / Math.pow(Lij, 2);
    Kjj[5][2] = 0;
    Kjj[5][3] = 0;
    Kjj[5][4] = 0;
    Kjj[5][5] = 3 * E * Iz / Lij;
  }

  public void EBEsMat3DL_iRig_jArt(int e) throws JMetalException {
    double Lij = Element_[e][L_];
    int idx = (int) Element_[e][INDEX_];
    double S = Groups_[idx][AREA];
    double Iz = Groups_[idx][Iz_];
    double Iy = Groups_[idx][Iy_];
    double E = Groups_[idx][E_];
    Kii[0][0] = E * S / Lij;
    Kii[0][1] = 0;
    Kii[0][2] = 0;
    Kii[0][3] = 0;
    Kii[0][4] = 0;
    Kii[0][5] = 0;
    Kii[1][0] = 0;
    Kii[1][1] = 3 * E * Iz / Math.pow(Lij, 3);
    Kii[1][2] = 0;
    Kii[1][3] = 0;
    Kii[1][4] = 0;
    Kii[1][5] = -3 * E * Iz / Math.pow(Lij, 2);
    Kii[2][0] = 0;
    Kii[2][1] = 0;
    Kii[2][2] = 3 * E * Iy / Math.pow(Lij, 3);
    Kii[2][3] = 0;
    Kii[2][4] = 3 * E * Iy / Math.pow(Lij, 2);
    Kii[2][5] = 0;
    Kii[3][0] = 0;
    Kii[3][1] = 0;
    Kii[3][2] = 0;
    Kii[3][3] = 0;
    Kii[3][4] = 0;
    Kii[3][5] = 0;
    Kii[4][0] = 0;
    Kii[4][1] = 0;
    Kii[4][2] = 3 * E * Iy / Math.pow(Lij, 2);
    Kii[4][3] = 0;
    Kii[4][4] = 3 * E * Iy / Lij;
    Kii[4][5] = 0;
    Kii[5][0] = 0;
    Kii[5][1] = -3 * E * Iz / Math.pow(Lij, 2);
    Kii[5][2] = 0;
    Kii[5][3] = 0;
    Kii[5][4] = 0;
    Kii[5][5] = 3 * E * Iz / Lij;
    Kij[0][0] = E * S / Lij;
    Kij[0][1] = 0;
    Kij[0][2] = 0;
    Kij[0][3] = 0;
    Kij[0][4] = 0;
    Kij[0][5] = 0;
    Kij[1][0] = 0;
    Kij[1][1] = 3 * E * Iz / Math.pow(Lij, 3);
    Kij[1][2] = 0;
    Kij[1][3] = 0;
    Kij[1][4] = 0;
    Kij[1][5] = 0;
    Kij[2][0] = 0;
    Kij[2][1] = 0;
    Kij[2][2] = -3 * E * Iy / Math.pow(Lij, 3);
    Kij[2][3] = 0;
    Kij[2][4] = 0;
    Kij[2][5] = 0;
    Kij[3][0] = 0;
    Kij[3][1] = 0;
    Kij[3][2] = 0;
    Kij[3][3] = 0;
    Kij[3][4] = 0;
    Kij[3][5] = 0;
    Kij[4][0] = 0;
    Kij[4][1] = 0;
    Kij[4][2] = -3 * E * Iy / Math.pow(Lij, 2);
    Kij[4][3] = 0;
    Kij[4][4] = 0;
    Kij[4][5] = 0;
    Kij[5][0] = 0;
    Kij[5][1] = -3 * E * Iz / Math.pow(Lij, 2);
    Kij[5][2] = 0;
    Kij[5][3] = 0;
    Kij[5][4] = 0;
    Kij[5][5] = 0;
    Kji[0][0] = E * S / Lij;
    Kji[0][1] = 0;
    Kji[0][2] = 0;
    Kji[0][3] = 0;
    Kji[0][4] = 0;
    Kji[0][5] = 0;
    Kji[1][0] = 0;
    Kji[1][1] = 3 * E * Iz / Math.pow(Lij, 3);
    Kji[1][2] = 0;
    Kji[1][3] = 0;
    Kji[1][4] = 0;
    Kji[1][5] = -3 * E * Iz / Math.pow(Lij, 2);
    Kji[2][0] = 0;
    Kji[2][1] = 0;
    Kji[2][2] = -3 * E * Iy / Math.pow(Lij, 3);
    Kji[2][3] = 0;
    Kji[2][4] = -3 * E * Iy / Math.pow(Lij, 2);
    Kji[2][5] = 0;
    Kji[3][0] = 0;
    Kji[3][1] = 0;
    Kji[3][2] = 0;
    Kji[3][3] = 0;
    Kji[3][4] = 0;
    Kji[3][5] = 0;
    Kji[4][0] = 0;
    Kji[4][1] = 0;
    Kji[4][2] = 0;
    Kji[4][3] = 0;
    Kji[4][4] = 0;
    Kji[4][5] = 0;
    Kji[5][0] = 0;
    Kji[5][1] = 0;
    Kji[5][2] = 0;
    Kji[5][3] = 0;
    Kji[5][4] = 0;
    Kji[5][5] = 0;
    Kjj[0][0] = E * S / Lij;
    Kjj[0][1] = 0;
    Kjj[0][2] = 0;
    Kjj[0][3] = 0;
    Kjj[0][4] = 0;
    Kjj[0][5] = 0;
    Kjj[1][0] = 0;
    Kjj[1][1] = 3 * E * Iz / Math.pow(Lij, 3);
    Kjj[1][2] = 0;
    Kjj[1][3] = 0;
    Kjj[1][4] = 0;
    Kjj[1][5] = 0;
    Kjj[2][0] = 0;
    Kjj[2][1] = 0;
    Kjj[2][2] = 3 * E * Iy / Math.pow(Lij, 3);
    Kjj[2][3] = 0;
    Kjj[2][4] = 0;
    Kjj[2][5] = 0;
    Kjj[3][0] = 0;
    Kjj[3][1] = 0;
    Kjj[3][2] = 0;
    Kjj[3][3] = 0;
    Kjj[3][4] = 0;
    Kjj[3][5] = 0;
    Kjj[4][0] = 0;
    Kjj[4][1] = 0;
    Kjj[4][2] = 0;
    Kjj[4][3] = 0;
    Kjj[4][4] = 0;
    Kjj[4][5] = 0;
    Kjj[5][0] = 0;
    Kjj[5][1] = 0;
    Kjj[5][2] = 0;
    Kjj[5][3] = 0;
    Kjj[5][4] = 0;
    Kjj[5][5] = 0;
  }

  public void EBEsMat3DL_iArt_jArt(int e) throws JMetalException {
    double Lij = Element_[e][L_];
    int idx = (int) Element_[e][INDEX_];
    double S = Groups_[idx][AREA];
    double E = Groups_[idx][E_];
    Kii[0][0] = E * S / Lij;
    Kii[0][1] = 0;
    Kii[0][2] = 0;
    Kii[0][3] = 0;
    Kii[0][4] = 0;
    Kii[0][5] = 0;
    Kii[1][0] = 0;
    Kii[1][1] = 0;
    Kii[1][2] = 0;
    Kii[1][3] = 0;
    Kii[1][4] = 0;
    Kii[1][5] = 0;
    Kii[2][0] = 0;
    Kii[2][1] = 0;
    Kii[2][2] = 0;
    Kii[2][3] = 0;
    Kii[2][4] = 0;
    Kii[2][5] = 0;
    Kii[3][0] = 0;
    Kii[3][1] = 0;
    Kii[3][2] = 0;
    Kii[3][3] = 0;
    Kii[3][4] = 0;
    Kii[3][5] = 0;
    Kii[4][0] = 0;
    Kii[4][1] = 0;
    Kii[4][2] = 0;
    Kii[4][3] = 0;
    Kii[4][4] = 0;
    Kii[4][5] = 0;
    Kii[5][0] = 0;
    Kii[5][1] = 0;
    Kii[5][2] = 0;
    Kii[5][3] = 0;
    Kii[5][4] = 0;
    Kii[5][5] = 0;
    Kij[0][0] = E * S / Lij;
    Kij[0][1] = 0;
    Kij[0][2] = 0;
    Kij[0][3] = 0;
    Kij[0][4] = 0;
    Kij[0][5] = 0;
    Kij[1][0] = 0;
    Kij[1][1] = 0;
    Kij[1][2] = 0;
    Kij[1][3] = 0;
    Kij[1][4] = 0;
    Kij[1][5] = 0;
    Kij[2][0] = 0;
    Kij[2][1] = 0;
    Kij[2][2] = 0;
    Kij[2][3] = 0;
    Kij[2][4] = 0;
    Kij[2][5] = 0;
    Kij[3][0] = 0;
    Kij[3][1] = 0;
    Kij[3][2] = 0;
    Kij[3][3] = 0;
    Kij[3][4] = 0;
    Kij[3][5] = 0;
    Kij[4][0] = 0;
    Kij[4][1] = 0;
    Kij[4][2] = 0;
    Kij[4][3] = 0;
    Kij[4][4] = 0;
    Kij[4][5] = 0;
    Kij[5][0] = 0;
    Kij[5][1] = 0;
    Kij[5][2] = 0;
    Kij[5][3] = 0;
    Kij[5][4] = 0;
    Kij[5][5] = 0;
    Kji[0][0] = E * S / Lij;
    Kji[0][1] = 0;
    Kji[0][2] = 0;
    Kji[0][3] = 0;
    Kji[0][4] = 0;
    Kji[0][5] = 0;
    Kji[1][0] = 0;
    Kji[1][1] = 0;
    Kji[1][2] = 0;
    Kji[1][3] = 0;
    Kji[1][4] = 0;
    Kji[1][5] = 0;
    Kji[2][0] = 0;
    Kji[2][1] = 0;
    Kji[2][2] = 0;
    Kji[2][3] = 0;
    Kji[2][4] = 0;
    Kji[2][5] = 0;
    Kji[3][0] = 0;
    Kji[3][1] = 0;
    Kji[3][2] = 0;
    Kji[3][3] = 0;
    Kji[3][4] = 0;
    Kji[3][5] = 0;
    Kji[4][0] = 0;
    Kji[4][1] = 0;
    Kji[4][2] = 0;
    Kji[4][3] = 0;
    Kji[4][4] = 0;
    Kji[4][5] = 0;
    Kji[5][0] = 0;
    Kji[5][1] = 0;
    Kji[5][2] = 0;
    Kji[5][3] = 0;
    Kji[5][4] = 0;
    Kji[5][5] = 0;
    Kjj[0][0] = E * S / Lij;
    Kjj[0][1] = 0;
    Kjj[0][2] = 0;
    Kjj[0][3] = 0;
    Kjj[0][4] = 0;
    Kjj[0][5] = 0;
    Kjj[1][0] = 0;
    Kjj[1][1] = 0;
    Kjj[1][2] = 0;
    Kjj[1][3] = 0;
    Kjj[1][4] = 0;
    Kjj[1][5] = 0;
    Kjj[2][0] = 0;
    Kjj[2][1] = 0;
    Kjj[2][2] = 0;
    Kjj[2][3] = 0;
    Kjj[2][4] = 0;
    Kjj[2][5] = 0;
    Kjj[3][0] = 0;
    Kjj[3][1] = 0;
    Kjj[3][2] = 0;
    Kjj[3][3] = 0;
    Kjj[3][4] = 0;
    Kjj[3][5] = 0;
    Kjj[4][0] = 0;
    Kjj[4][1] = 0;
    Kjj[4][2] = 0;
    Kjj[4][3] = 0;
    Kjj[4][4] = 0;
    Kjj[4][5] = 0;
    Kjj[5][0] = 0;
    Kjj[5][1] = 0;
    Kjj[5][2] = 0;
    Kjj[5][3] = 0;
    Kjj[5][4] = 0;
    Kjj[5][5] = 0;
  }

  public void EBEsMat3DL_SOG(int e) throws JMetalException {
    double l = Element_[e][L_];
    double Ni = AxialForcei_[e];
    double Nj = AxialForcej_[e];
    KiiSOG[0][0] = 0.0;
    KiiSOG[0][1] = 0.0;
    KiiSOG[0][2] = 0.0;
    KiiSOG[0][3] = 0.0;
    KiiSOG[0][4] = 0.0;
    KiiSOG[0][5] = 0.0;
    KiiSOG[1][0] = 0.0;
    KiiSOG[1][1] = Ni * 6.0 / (5.0 * l);
    KiiSOG[1][2] = 0.0;
    KiiSOG[1][3] = 0.0;
    KiiSOG[1][4] = 0.0;
    KiiSOG[1][5] = -Ni / 10.0;
    KiiSOG[2][0] = 0.0;
    KiiSOG[2][1] = 0.0;
    KiiSOG[2][2] = Ni * 6.0 / (5.0 * l);
    KiiSOG[2][3] = 0.0;
    KiiSOG[2][4] = Ni / 10.0;
    KiiSOG[2][5] = 0.0;
    KiiSOG[3][0] = 0.0;
    KiiSOG[3][1] = 0.0;
    KiiSOG[3][2] = 0.0;
    KiiSOG[3][3] = 0.0;
    KiiSOG[3][4] = 0.0;
    KiiSOG[3][5] = 0.0;
    KiiSOG[4][0] = 0.0;
    KiiSOG[4][1] = 0.0;
    KiiSOG[4][2] = Ni / 10.0;
    KiiSOG[4][3] = 0.0;
    KiiSOG[4][4] = Ni * 2.0 * l / 15.0;
    KiiSOG[4][5] = 0.0;
    KiiSOG[5][0] = 0.0;
    KiiSOG[5][1] = -Ni / 10.0;
    KiiSOG[5][2] = 0.0;
    KiiSOG[5][3] = 0.0;
    KiiSOG[5][4] = 0.0;
    KiiSOG[5][5] = Ni * 2.0 * l / 15.0;
    KijSOG[0][0] = 0.0;
    KijSOG[0][1] = 0.0;
    KijSOG[0][2] = 0.0;
    KijSOG[0][3] = 0.0;
    KijSOG[0][4] = 0.0;
    KijSOG[0][5] = 0.0;
    KijSOG[1][0] = 0.0;
    KijSOG[1][1] = Ni * 6.0 / (5.0 * l);
    KijSOG[1][2] = 0.0;
    KijSOG[1][3] = 0.0;
    KijSOG[1][4] = 0.0;
    KijSOG[1][5] = -Ni / 10.0;
    KijSOG[2][0] = 0.0;
    KijSOG[2][1] = 0.0;
    KijSOG[2][2] = -Ni * 6.0 / (5.0 * l);
    KijSOG[2][3] = 0.0;
    KijSOG[2][4] = -Ni / 10.0;
    KijSOG[2][5] = 0.0;
    KijSOG[3][0] = 0.0;
    KijSOG[3][1] = 0.0;
    KijSOG[3][2] = 0.0;
    KijSOG[3][3] = 0.0;
    KijSOG[3][4] = 0.0;
    KijSOG[3][5] = 0.0;
    KijSOG[4][0] = 0.0;
    KijSOG[4][1] = 0.0;
    KijSOG[4][2] = -Ni / l;
    KijSOG[4][3] = 0.0;
    KijSOG[4][4] = Ni * l / 30.0;
    KijSOG[4][5] = 0.0;
    KijSOG[5][0] = 0.0;
    KijSOG[5][1] = -Ni / 10.0;
    KijSOG[5][2] = 0.0;
    KijSOG[5][3] = 0.0;
    KijSOG[5][4] = 0.0;
    KijSOG[5][5] = -Ni * l / 30.0;
    KjiSOG[0][0] = 0.0;
    KjiSOG[0][1] = 0.0;
    KjiSOG[0][2] = 0.0;
    KjiSOG[0][3] = 0.0;
    KjiSOG[0][4] = 0.0;
    KjiSOG[0][5] = 0.0;
    KjiSOG[1][0] = 0.0;
    KjiSOG[1][1] = Nj * 6.0 / (5.0 * l);
    KjiSOG[1][2] = 0.0;
    KjiSOG[1][3] = 0.0;
    KjiSOG[1][4] = 0.0;
    KjiSOG[1][5] = -Nj / 10.0;
    KjiSOG[2][0] = 0.0;
    KjiSOG[2][1] = 0.0;
    KjiSOG[2][2] = -Nj * 6.0 / (5.0 * l);
    KjiSOG[2][3] = 0.0;
    KjiSOG[2][4] = -Nj / 10.0;
    KjiSOG[2][5] = 0.0;
    KjiSOG[3][0] = 0.0;
    KjiSOG[3][1] = 0.0;
    KjiSOG[3][2] = 0.0;
    KjiSOG[3][3] = 0.0;
    KjiSOG[3][4] = 0.0;
    KjiSOG[3][5] = 0.0;
    KjiSOG[4][0] = 0.0;
    KjiSOG[4][1] = 0.0;
    KjiSOG[4][2] = -Nj / 10.0;
    KjiSOG[4][3] = 0.0;
    KjiSOG[4][4] = Nj * l / 30.0;
    KjiSOG[4][5] = 0.0;
    KjiSOG[5][0] = 0.0;
    KjiSOG[5][1] = -Nj / 10.0;
    KjiSOG[5][2] = 0.0;
    KjiSOG[5][3] = 0.0;
    KjiSOG[5][4] = 0.0;
    KjiSOG[5][5] = -Nj * l / 30.0;
    KjjSOG[0][0] = 0.0;
    KjjSOG[0][1] = 0.0;
    KjjSOG[0][2] = 0.0;
    KjjSOG[0][3] = 0.0;
    KjjSOG[0][4] = 0.0;
    KjjSOG[0][5] = 0.0;
    KjjSOG[1][0] = 0.0;
    KjjSOG[1][1] = Nj * 6.0 / (5.0 * l);
    KjjSOG[1][2] = 0.0;
    KjjSOG[1][3] = 0.0;
    KjjSOG[1][4] = 0.0;
    KjjSOG[1][5] = -Nj / 10.0;
    KjjSOG[2][0] = 0.0;
    KjjSOG[2][1] = 0.0;
    KjjSOG[2][2] = Nj * 6.0 / (5.0 * l);
    KjjSOG[2][3] = 0.0;
    KjjSOG[2][4] = Nj / 10.0;
    KjjSOG[2][5] = 0.0;
    KjjSOG[3][0] = 0.0;
    KjjSOG[3][1] = 0.0;
    KjjSOG[3][2] = 0.0;
    KjjSOG[3][3] = 0.0;
    KjjSOG[3][4] = 0.0;
    KjjSOG[3][5] = 0.0;
    KjjSOG[4][0] = 0.0;
    KjjSOG[4][1] = 0.0;
    KjjSOG[4][2] = Nj / 10.0;
    KjjSOG[4][3] = 0.0;
    KjjSOG[4][4] = Nj * 2.0 * l / 15.0;
    KjjSOG[4][5] = 0.0;
    KjjSOG[5][0] = 0.0;
    KjjSOG[5][1] = -Nj / 10.0;
    KjjSOG[5][2] = 0.0;
    KjjSOG[5][3] = 0.0;
    KjjSOG[5][4] = 0.0;
    KjjSOG[5][5] = Nj * 2.0 * l / 15.0;
  }

  public void EBEsMatRot3DLaG(int e) throws JMetalException {
    int i, j;
    double lx;
    double mx;
    double nx;
    double D;
    double ly;
    double my;
    double ny;
    double lz;
    double mz;
    double nz;
    int sgn;
    int ni = (int) Element_[e][i_];
    int nj = (int) Element_[e][j_];
    lx = (Node_[ni][aX_] - Node_[nj][aX_]) / Element_[e][L_];
    mx = (Node_[ni][aY_] - Node_[nj][aY_]) / Element_[e][L_];
    nx = (Node_[ni][aZ_] - Node_[nj][aZ_]) / Element_[e][L_];
    D = Math.sqrt(Math.pow(lx, 2.0) + Math.pow(mx, 2.0));
    if (lx == 0 && mx == 0) {
      sgn = (int) Math.signum(Node_[ni][aZ_] - Node_[nj][aZ_]);
      lx = 0;
      mx = 0;
      nx = sgn;
      ly = 0;
      my = sgn;
      ny = 0;
      lz = -1;
      mz = 0;
      nz = 0;
      D = 0;
    } else {
      ly = -mx / D;
      my = lx / D;
      ny = 0;
      lz = -lx * nx / D;
      mz = -mx * nx / D;
      nz = D;
    }
    Rij[0][0] = lx;
    Rij[0][1] = mx;
    Rij[0][2] = nx;
    Rij[1][0] = ly;
    Rij[1][1] = my;
    Rij[1][2] = ny;
    Rij[2][0] = lz;
    Rij[2][1] = mz;
    Rij[2][2] = nz;
    for (i = 0; i < 3; i++) {
      for (j = 3; j < 6; j++) {
        Rij[i][j] = 0.0;
      }
    }
    for (i = 3; i < 6; i++) {
      for (j = 0; j < 3; j++) {
        Rij[i][j] = 0.0;
      }
    }
    Rij[3][3] = lx;
    Rij[3][4] = mx;
    Rij[3][5] = nx;
    Rij[4][3] = ly;
    Rij[4][4] = my;
    Rij[4][5] = ny;
    Rij[5][3] = lz;
    Rij[5][4] = mz;
    Rij[5][5] = nz;
    RTij = EBEsMatrizTraspuesta(Rij);
    Rji[0][0] = -lx;
    Rji[0][1] = -mx;
    Rji[0][2] = -nx;
    Rji[1][0] = -ly;
    Rji[1][1] = -my;
    Rji[1][2] = ny;
    Rji[2][0] = lz;
    Rji[2][1] = mz;
    Rji[2][2] = nz;
    for (i = 0; i < 3; i++) {
      for (j = 3; j < 6; j++) {
        Rji[i][j] = 0.0;
      }
    }
    for (i = 3; i < 6; i++) {
      for (j = 0; j < 3; j++) {
        Rji[i][j] = 0.0;
      }
    }
    Rji[3][3] = -lx;
    Rji[3][4] = -mx;
    Rji[3][5] = -nx;
    Rji[4][3] = -ly;
    Rji[4][4] = -my;
    Rji[4][5] = ny;
    Rji[5][3] = lz;
    Rji[5][4] = mz;
    Rji[5][5] = nz;
    RTji = EBEsMatrizTraspuesta(Rji);
  }

  public void EBEsMat3DGij() throws JMetalException {
    double[][] r = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];
    double[][] s = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];
    double[][] t = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];
    r = EBEsMatrizMultiplicar(Rpij, Rij);
    s = EBEsMatrizMultiplicar(Kii, r);
    t = EBEsMatrizMultiplicar(RpTij, s);
    KGii = EBEsMatrizMultiplicar(RTij, t);
    r = EBEsMatrizMultiplicar(Rpji, Rji);
    s = EBEsMatrizMultiplicar(Kij, r);
    t = EBEsMatrizMultiplicar(RpTij, s);
    KGij = EBEsMatrizMultiplicar(RTij, t);
    r = EBEsMatrizMultiplicar(Rpij, Rij);
    s = EBEsMatrizMultiplicar(Kji, r);
    t = EBEsMatrizMultiplicar(RpTji, s);
    KGji = EBEsMatrizMultiplicar(RTji, t);
    r = EBEsMatrizMultiplicar(Rpji, Rji);
    s = EBEsMatrizMultiplicar(Kjj, r);
    t = EBEsMatrizMultiplicar(RpTji, s);
    KGjj = EBEsMatrizMultiplicar(RTji, t);
  }

  public void EBEsMat3DG(int e) throws JMetalException {
    int ni, nj;
    int p;
    int p1;
    int p2;
    int p3;
    int p4;
    int p5;
    int p6;
    int r;
    int r1;
    int r2;
    int r3;
    int r4;
    int r5;
    int r6;
    ni = (int) Element_[e][i_];
    nj = (int) Element_[e][j_];
    p = numberOfLibertyDegree_ * ni;
    r = numberOfLibertyDegree_ * nj;
    p1 = matrixWidthBand_ * p;
    p2 = matrixWidthBand_ * (p + 1);
    p3 = matrixWidthBand_ * (p + 2);
    p4 = matrixWidthBand_ * (p + 3);
    p5 = matrixWidthBand_ * (p + 4);
    p6 = matrixWidthBand_ * (p + 5);
    r1 = matrixWidthBand_ * r;
    r2 = matrixWidthBand_ * (r + 1);
    r3 = matrixWidthBand_ * (r + 2);
    r4 = matrixWidthBand_ * (r + 3);
    r5 = matrixWidthBand_ * (r + 4);
    r6 = matrixWidthBand_ * (r + 5);
    MatrixStiffness_[p1] = MatrixStiffness_[p1] + KGii[0][0];
    MatrixStiffness_[p1 + 1] = MatrixStiffness_[p1 + 1] + KGii[0][1];
    MatrixStiffness_[p1 + 2] = MatrixStiffness_[p1 + 2] + KGii[0][2];
    MatrixStiffness_[p1 + 3] = MatrixStiffness_[p1 + 3] + KGii[0][3];
    MatrixStiffness_[p1 + 4] = MatrixStiffness_[p1 + 4] + KGii[0][4];
    MatrixStiffness_[p1 + 5] = MatrixStiffness_[p1 + 5] + KGii[0][5];
    MatrixStiffness_[p1 + r - p] = KGij[0][0];
    MatrixStiffness_[p1 + 1 + r - p] = KGij[0][1];
    MatrixStiffness_[p1 + 2 + r - p] = KGij[0][2];
    MatrixStiffness_[p1 + 3 + r - p] = KGij[0][3];
    MatrixStiffness_[p1 + 4 + r - p] = KGij[0][4];
    MatrixStiffness_[p1 + 5 + r - p] = KGij[0][5];
    MatrixStiffness_[p2] = MatrixStiffness_[p2] + KGii[1][1];
    MatrixStiffness_[p2 + 1] = MatrixStiffness_[p2 + 1] + KGii[1][2];
    MatrixStiffness_[p2 + 2] = MatrixStiffness_[p2 + 2] + KGii[1][3];
    MatrixStiffness_[p2 + 3] = MatrixStiffness_[p2 + 3] + KGii[1][4];
    MatrixStiffness_[p2 + 4] = MatrixStiffness_[p2 + 4] + KGii[1][5];
    MatrixStiffness_[p2 + r - p - 1] = KGij[1][0];
    MatrixStiffness_[p2 + r - p] = KGij[1][1];
    MatrixStiffness_[p2 + r - p + 1] = KGij[1][2];
    MatrixStiffness_[p2 + r - p + 2] = KGij[1][3];
    MatrixStiffness_[p2 + r - p + 3] = KGij[1][4];
    MatrixStiffness_[p2 + r - p + 4] = KGij[1][5];
    MatrixStiffness_[p3] = MatrixStiffness_[p3] + KGii[2][2];
    MatrixStiffness_[p3 + 1] = MatrixStiffness_[p3 + 1] + KGii[2][3];
    MatrixStiffness_[p3 + 2] = MatrixStiffness_[p3 + 2] + KGii[2][4];
    MatrixStiffness_[p3 + 3] = MatrixStiffness_[p3 + 3] + KGii[2][5];
    MatrixStiffness_[p3 + r - p - 2] = KGij[2][0];
    MatrixStiffness_[p3 + r - p - 1] = KGij[2][1];
    MatrixStiffness_[p3 + r - p] = KGij[2][2];
    MatrixStiffness_[p3 + r - p + 1] = KGij[2][3];
    MatrixStiffness_[p3 + r - p + 2] = KGij[2][4];
    MatrixStiffness_[p3 + r - p + 3] = KGij[2][5];
    MatrixStiffness_[p4] = MatrixStiffness_[p4] + KGii[3][3];
    MatrixStiffness_[p4 + 1] = MatrixStiffness_[p4 + 1] + KGii[3][4];
    MatrixStiffness_[p4 + 2] = MatrixStiffness_[p4 + 2] + KGii[3][5];
    MatrixStiffness_[p4 + r - p - 3] = KGij[3][0];
    MatrixStiffness_[p4 + r - p - 2] = KGij[3][1];
    MatrixStiffness_[p4 + r - p - 1] = KGij[3][2];
    MatrixStiffness_[p4 + r - p] = KGij[3][3];
    MatrixStiffness_[p4 + r - p + 1] = KGij[3][4];
    MatrixStiffness_[p4 + r - p + 2] = KGij[3][5];
    MatrixStiffness_[p5] = MatrixStiffness_[p5] + KGii[4][4];
    MatrixStiffness_[p5 + 1] = MatrixStiffness_[p5 + 1] + KGii[4][5];
    MatrixStiffness_[p5 + r - p - 4] = KGij[4][0];
    MatrixStiffness_[p5 + r - p - 3] = KGij[4][1];
    MatrixStiffness_[p5 + r - p - 2] = KGij[4][2];
    MatrixStiffness_[p5 + r - p - 1] = KGij[4][3];
    MatrixStiffness_[p5 + r - p] = KGij[4][4];
    MatrixStiffness_[p5 + r - p + 1] = KGij[4][5];
    MatrixStiffness_[p6] = MatrixStiffness_[p6] + KGii[5][5];
    MatrixStiffness_[p6 + r - p - 5] = KGij[5][0];
    MatrixStiffness_[p6 + r - p - 4] = KGij[5][1];
    MatrixStiffness_[p6 + r - p - 3] = KGij[5][2];
    MatrixStiffness_[p6 + r - p - 2] = KGij[5][3];
    MatrixStiffness_[p6 + r - p - 1] = KGij[5][4];
    MatrixStiffness_[p6 + r - p] = KGij[5][5];
    MatrixStiffness_[r1] = MatrixStiffness_[r1] + KGjj[0][0];
    MatrixStiffness_[r1 + 1] = MatrixStiffness_[r1 + 1] + KGjj[0][1];
    MatrixStiffness_[r1 + 2] = MatrixStiffness_[r1 + 2] + KGjj[0][2];
    MatrixStiffness_[r1 + 3] = MatrixStiffness_[r1 + 3] + KGjj[0][3];
    MatrixStiffness_[r1 + 4] = MatrixStiffness_[r1 + 4] + KGjj[0][4];
    MatrixStiffness_[r1 + 5] = MatrixStiffness_[r1 + 5] + KGjj[0][5];
    MatrixStiffness_[r2] = MatrixStiffness_[r2] + KGjj[1][1];
    MatrixStiffness_[r2 + 1] = MatrixStiffness_[r2 + 1] + KGjj[1][2];
    MatrixStiffness_[r2 + 2] = MatrixStiffness_[r2 + 2] + KGjj[1][3];
    MatrixStiffness_[r2 + 3] = MatrixStiffness_[r2 + 3] + KGjj[1][4];
    MatrixStiffness_[r2 + 4] = MatrixStiffness_[r2 + 4] + KGjj[1][5];
    MatrixStiffness_[r3] = MatrixStiffness_[r3] + KGjj[2][2];
    MatrixStiffness_[r3 + 1] = MatrixStiffness_[r3 + 1] + KGjj[2][3];
    MatrixStiffness_[r3 + 2] = MatrixStiffness_[r3 + 2] + KGjj[2][4];
    MatrixStiffness_[r3 + 3] = MatrixStiffness_[r3 + 3] + KGjj[2][5];
    MatrixStiffness_[r4] = MatrixStiffness_[r4] + KGjj[3][3];
    MatrixStiffness_[r4 + 1] = MatrixStiffness_[r4 + 1] + KGjj[3][4];
    MatrixStiffness_[r4 + 2] = MatrixStiffness_[r4 + 2] + KGjj[3][5];
    MatrixStiffness_[r5] = MatrixStiffness_[r5] + KGjj[4][4];
    MatrixStiffness_[r5 + 1] = MatrixStiffness_[r5 + 1] + KGjj[4][5];
    MatrixStiffness_[r6] = MatrixStiffness_[r6] + KGjj[5][5];
  }

  public double[] EBEsMatrizVectorMultiplicar(double[][] s, double[] t) throws JMetalException {
    int f, c;
    double[] r = new double[t.length];
    for (f = 0; f < s.length; f++) {
      r[f] = 0;
      for (c = 0; c < t.length; c++) {
        r[f] = r[f] + s[f][c] * t[c];
      }
    }
    return r;
  }

  public double[][] EBEsMatrizMultiplicar(double[][] s, double[][] t) throws JMetalException {
    int f, c, q;
    double[][] r = new double[s.length][t[0].length];
    for (f = 0; f < s.length; f++) {
      for (c = 0; c < s[f].length; c++) {
        r[f][c] = 0;
        for (q = 0; q < s[f].length; q++) {
          r[f][c] = r[f][c] + s[f][q] * t[q][c];
        }
      }
    }
    return r;
  }

  public double[][] EBEsMatrixAdd(double[][] s, double[][] t) throws JMetalException {
    double[][] r = new double[s.length][t[0].length];
    for (int f = 0; f < s.length; f++) {
      for (int c = 0; c < t.length; c++) {
        r[f][c] = s[f][c] + t[f][c];
      }
    }
    return r;
  }

  public double[][] EBEsMatrixSubtractions(double[][] s, double[][] t) throws JMetalException {
    double[][] r = new double[s.length][t[0].length];
    for (int f = 0; f < s.length; f++) {
      for (int c = 0; c < t.length; c++) {
        r[f][c] = s[f][c] - t[f][c];
      }
    }
    return r;
  }

  public void EBEsNodesEquilibrium3D(int hi) throws JMetalException {
    for (int ba = 0; ba < Element_.length; ba++) {
      double[][] ri = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];
      double[][] rj = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];
      double[] ei = new double[numberOfLibertyDegree_];
      double[] ej = new double[numberOfLibertyDegree_];
      double[] egi = new double[numberOfLibertyDegree_];
      double[] egj = new double[numberOfLibertyDegree_];
      ei[aX_] = Efforti_[aX_][ba][hi];
      ei[aY_] = Efforti_[aY_][ba][hi];
      ei[aZ_] = Efforti_[aZ_][ba][hi];
      ei[gX_] = Efforti_[gX_][ba][hi];
      ei[gY_] = Efforti_[gY_][ba][hi];
      ei[gZ_] = Efforti_[gZ_][ba][hi];
      ej[aX_] = Effortj_[aX_][ba][hi];
      ej[aY_] = Effortj_[aY_][ba][hi];
      ej[aZ_] = Effortj_[aZ_][ba][hi];
      ej[gX_] = Effortj_[gX_][ba][hi];
      ej[gY_] = Effortj_[gY_][ba][hi];
      ej[gZ_] = Effortj_[gZ_][ba][hi];
      ri = EBEsMatrizMultiplicar(RTij, RpTij);
      egi = EBEsMatrizVectorMultiplicar(ri, ei);
      rj = EBEsMatrizMultiplicar(RTji, RpTji);
      egj = EBEsMatrizVectorMultiplicar(rj, ej);
      int ni = (int) Element_[ba][i_];
      Reaction_[numberOfLibertyDegree_ * ni + aX_][hi] += egi[aX_];
      Reaction_[numberOfLibertyDegree_ * ni + aY_][hi] += egi[aY_];
      Reaction_[numberOfLibertyDegree_ * ni + aZ_][hi] += egi[aZ_];
      Reaction_[numberOfLibertyDegree_ * ni + gX_][hi] += egi[gX_];
      Reaction_[numberOfLibertyDegree_ * ni + gY_][hi] += egi[gY_];
      Reaction_[numberOfLibertyDegree_ * ni + gZ_][hi] += egi[gZ_];
      int nj = (int) Element_[ba][j_];
      Reaction_[numberOfLibertyDegree_ * nj + aX_][hi] += egj[aX_];
      Reaction_[numberOfLibertyDegree_ * nj + aY_][hi] += egj[aY_];
      Reaction_[numberOfLibertyDegree_ * nj + aZ_][hi] += egj[aZ_];
      Reaction_[numberOfLibertyDegree_ * nj + gX_][hi] += egj[gX_];
      Reaction_[numberOfLibertyDegree_ * nj + gY_][hi] += egj[gY_];
      Reaction_[numberOfLibertyDegree_ * nj + gZ_][hi] += egj[gZ_];
    }
  }

  public void EBEsEffortsElements3D(int hi, int countIter, double[][] Slip) throws JMetalException {
    int i, ni, nj;
    for (int ba = 0; ba < numberOfElements_; ba++) {
      switch ((int) Element_[ba][Vij_]) {
        case 00:
        EBEsMat3DL_iRig_jRig(ba);
        break;
        case 01:
        EBEsMat3DL_iRig_jArt(ba);
        break;
        case 10:
        EBEsMat3DL_iArt_jRig(ba);
        break;
        case 11:
        EBEsMat3DL_iArt_jArt(ba);
        break;
        default:
        System.out.println("invalid link");
        return;
      }
      if (lSecondOrderGeometric && countIter == 1) {
        EBEsMat3DL_SOG(ba);
        Kii = EBEsMatrixAdd(Kii, KiiSOG);
        Kij = EBEsMatrixAdd(Kij, KijSOG);
        Kji = EBEsMatrixAdd(Kji, KjiSOG);
        Kjj = EBEsMatrixAdd(Kjj, KjjSOG);
      }
      double[][] r = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];
      double[][] s = new double[numberOfLibertyDegree_][numberOfLibertyDegree_];
      double[] di = new double[numberOfLibertyDegree_];
      double[] dj = new double[numberOfLibertyDegree_];
      double[] eii = new double[numberOfLibertyDegree_];
      double[] eij = new double[numberOfLibertyDegree_];
      double[] eji = new double[numberOfLibertyDegree_];
      double[] ejj = new double[numberOfLibertyDegree_];
      EBEsMatRot3DLpSaL(ba);
      EBEsMatRot3DLaG(ba);
      for (i = 0; i < numberOfLibertyDegree_; i++) {
        ni = (int) Element_[ba][i_];
        nj = (int) Element_[ba][j_];
        di[i] = Slip[numberOfLibertyDegree_ * ni + i][hi];
        dj[i] = Slip[numberOfLibertyDegree_ * nj + i][hi];
      }
      r = EBEsMatrizMultiplicar(Rpij, Rij);
      s = EBEsMatrizMultiplicar(Kii, r);
      eii = EBEsMatrizVectorMultiplicar(s, di);
      r = EBEsMatrizMultiplicar(Rpji, Rji);
      s = EBEsMatrizMultiplicar(Kij, r);
      eij = EBEsMatrizVectorMultiplicar(s, dj);
      r = EBEsMatrizMultiplicar(Rpij, Rij);
      s = EBEsMatrizMultiplicar(Kji, r);
      eji = EBEsMatrizVectorMultiplicar(s, di);
      r = EBEsMatrizMultiplicar(Rpji, Rji);
      s = EBEsMatrizMultiplicar(Kjj, r);
      ejj = EBEsMatrizVectorMultiplicar(s, dj);
      for (i = 0; i < numberOfLibertyDegree_; i++) {
        Efforti_[i][ba][hi] = eii[i] + eij[i];
        Effortj_[i][ba][hi] = eji[i] + ejj[i];
      }
    }
  }

  public void EBEsReactions3D(int hi) {
    for (int i = 0; i < numberOfNodesRestricts_; i++) {
      int no = (int) NodeRestrict_[i][0];
      String strCxyz = String.valueOf((int) NodeRestrict_[i][1]);
      String str = "";
      for (int j = numberOfLibertyDegree_; j > strCxyz.length(); j--) {
        str += "0";
      }
      strCxyz = str + strCxyz;
      char w0 = strCxyz.charAt(aX_);
      if (w0 == '1') {
        Reaction_[numberOfLibertyDegree_ * no + aX_][hi] += -PQ[numberOfLibertyDegree_ * no + aX_][hi];
      }
      w0 = strCxyz.charAt(aY_);
      if (w0 == '1') {
        Reaction_[numberOfLibertyDegree_ * no + aY_][hi] += -PQ[numberOfLibertyDegree_ * no + aY_][hi];
      }
      w0 = strCxyz.charAt(aZ_);
      if (w0 == '1') {
        Reaction_[numberOfLibertyDegree_ * no + aZ_][hi] += -PQ[numberOfLibertyDegree_ * no + aZ_][hi];
      }
      w0 = strCxyz.charAt(gX_);
      if (w0 == '1') {
        Reaction_[numberOfLibertyDegree_ * no + gX_][hi] += -PQ[numberOfLibertyDegree_ * no + gX_][hi];
      }
      w0 = strCxyz.charAt(gY_);
      if (w0 == '1') {
        Reaction_[numberOfLibertyDegree_ * no + gY_][hi] += -PQ[numberOfLibertyDegree_ * no + gY_][hi];
      }
      w0 = strCxyz.charAt(gZ_);
      if (w0 == '1') {
        Reaction_[numberOfLibertyDegree_ * no + gZ_][hi] += -PQ[numberOfLibertyDegree_ * no + gZ_][hi];
      }
    }
  }

  public double[][][] EBEsStrainNode(double[][][] E) throws JMetalException {
    double[][][] Strain = new double[3][numberOfElements_][numberOfWeigthHypothesis_];
    double z, y;
    double A, Iz, Iy, It;
    double Nxx, Qxy, Qxz, Mxx, Mxy, Mxz;
    double Sxx, Sxzu, Sxzd, TQxy, TQxz, TTx, TTxy, TTxz;
    double Sxyl, Sxyr;
    double y1, z1, S1, S2, S3, S4;
    double Ay, Az;
    omegaMax_ = new double[numberOfGroupElements_][numberOfWeigthHypothesis_];
    NxxMin_ = new double[numberOfGroupElements_][numberOfWeigthHypothesis_];
    NxxMax_ = new double[numberOfGroupElements_][numberOfWeigthHypothesis_];
    MxzMin_ = new double[numberOfGroupElements_][numberOfWeigthHypothesis_];
    MxzMax_ = new double[numberOfGroupElements_][numberOfWeigthHypothesis_];
    MxyMin_ = new double[numberOfGroupElements_][numberOfWeigthHypothesis_];
    MxyMax_ = new double[numberOfGroupElements_][numberOfWeigthHypothesis_];
    StrainNxxMin_ = new double[numberOfGroupElements_][numberOfWeigthHypothesis_];
    StrainNxxMax_ = new double[numberOfGroupElements_][numberOfWeigthHypothesis_];
    StrainMxzMin_ = new double[numberOfGroupElements_][numberOfWeigthHypothesis_];
    StrainMxzMax_ = new double[numberOfGroupElements_][numberOfWeigthHypothesis_];
    StrainMxyMin_ = new double[numberOfGroupElements_][numberOfWeigthHypothesis_];
    StrainMxyMax_ = new double[numberOfGroupElements_][numberOfWeigthHypothesis_];
    for (int hi = 0; hi < numberOfWeigthHypothesis_; hi++) {
      for (int ba = 0; ba < numberOfElements_; ba++) {
        int idx = (int) Element_[ba][INDEX_];
        y = Groups_[idx][Y_];
        z = Groups_[idx][Z_];
        A = Groups_[idx][AREA];
        Az = Groups_[idx][Az_];
        Ay = Groups_[idx][Ay_];
        Iz = Groups_[idx][Iz_];
        Iy = Groups_[idx][Iy_];
        It = Groups_[idx][It_];
        Nxx = E[aX_][ba][hi];
        Qxy = E[aY_][ba][hi];
        Qxz = E[aZ_][ba][hi];
        Mxx = E[gX_][ba][hi];
        Mxy = E[gY_][ba][hi];
        Mxz = E[gZ_][ba][hi];
        double omega = BucklingOmega(Nxx, Groups_[idx], Element_[ba]);
        Sxx = omega * Nxx / A;
        omegaMax_[idx][hi] = Math.max(omega, omegaMax_[idx][hi]);
        if (Math.signum(Sxx) > 0) {
          NxxMax_[idx][hi] = Math.max(E[aX_][ba][hi], Nxx);
          StrainNxxMax_[idx][hi] = Math.max(Sxx, StrainNxxMax_[idx][hi]);
        } else {
          NxxMin_[idx][hi] = Math.min(E[aX_][ba][hi], Nxx);
          StrainNxxMin_[idx][hi] = Math.min(Sxx, StrainNxxMin_[idx][hi]);
        }
        if (Element_[ba][Vij_] != 11) {
          y1 = Groups_[idx][uY_];
          Sxzu = Mxz * y1 / Iz;
          Sxzd = -Sxzu;
          z1 = Groups_[idx][lZ_];
          Sxyl = Mxy * z1 / Iy;
          Sxyr = -Sxyl;
          S3 = Sxzu;
          StrainMxzMin_[idx][hi] = Math.min(S3, StrainMxzMin_[idx][hi]);
          if (S3 > Sxzd) {
            S3 = Sxzd;
            StrainMxzMin_[idx][hi] = Math.min(S3, StrainMxzMin_[idx][hi]);
            MxzMin_[idx][hi] = Math.min(-Mxz, MxzMin_[idx][hi]);
          }
          S4 = Sxyl;
          StrainMxyMin_[idx][hi] = Math.min(S4, StrainMxyMin_[idx][hi]);
          if (S4 > Sxyr) {
            S4 = Sxyr;
            StrainMxyMin_[idx][hi] = Math.min(S4, StrainMxyMin_[idx][hi]);
            MxyMin_[idx][hi] = Math.min(-Mxy, MxyMin_[idx][hi]);
          }
          Strain[STRAIN_COMPRESS][ba][hi] = Sxx + S3 + S4;
          S1 = Sxzu;
          StrainMxzMax_[idx][hi] = Math.max(S1, StrainMxzMax_[idx][hi]);
          if (S1 < Sxzd) {
            S1 = Sxzd;
            StrainMxzMax_[idx][hi] = Math.max(S1, StrainMxzMax_[idx][hi]);
            MxzMax_[idx][hi] = Math.max(Mxz, MxzMax_[idx][hi]);
          }
          S2 = Sxyl;
          StrainMxyMax_[idx][hi] = Math.max(S2, StrainMxyMax_[idx][hi]);
          if (S2 < Sxyr) {
            S2 = Sxyr;
            StrainMxyMax_[idx][hi] = Math.max(S2, StrainMxyMax_[idx][hi]);
            MxyMax_[idx][hi] = Math.max(Mxy, MxyMax_[idx][hi]);
          }
          Strain[STRAIN_TRACTION][ba][hi] = Sxx + S1 + S2;
          TQxy = Qxy * Az / (z * Iz);
          TQxz = Qxz * Ay / (y * Iy);
          if (z / y >= 1.0) {
            TTxz = Math.abs(Mxx) * y / It;
            TTxy = 0.9 * TTxz;
          } else {
            TTxy = Math.abs(Mxx) * z / It;
            TTxz = 0.9 * TTxy;
          }
          TTx = TTxz;
          if (TTx < TTxy) {
            TTx = TTxy;
          }
          Strain[STRAIN_CUT][ba][hi] = Math.abs(TQxz) + Math.abs(TQxy) + TTx;
        } else {
          Strain[STRAIN_CUT][ba][hi] = 0.0;
          if (E[aX_][ba][hi] < 0.0) {
            Strain[STRAIN_COMPRESS][ba][hi] = Sxx;
          } else {
            if (E[aX_][ba][hi] > 0.0) {
              Strain[STRAIN_TRACTION][ba][hi] = Sxx;
            } else {
              Strain[STRAIN_COMPRESS][ba][hi] = 0.0;
              Strain[STRAIN_TRACTION][ba][hi] = 0.0;
            }
          }
          StrainNxxMin_[idx][hi] = Math.max(Strain[STRAIN_COMPRESS][ba][hi], StrainNxxMin_[idx][hi]);
          StrainNxxMax_[idx][hi] = Math.max(Strain[STRAIN_TRACTION][ba][hi], StrainNxxMax_[idx][hi]);
        }
      }
    }
    return Strain;
  }

  public double BucklingOmega(double Nxx, double[] G, double[] B) throws JMetalException {
    double w = 1.0;
    if (Nxx < 0.0 && G[AREA] > 0.0 && lBuckling) {
      if (G[BLijY_] <= 0.0) {
        G[BLijY_] = 1.0;
      }
      if (G[BLijZ_] <= 0.0) {
        G[BLijZ_] = 1.0;
      }
      double iy = G[Iy_] / G[AREA];
      double iz = G[Iz_] / G[AREA];
      double lambdao = B[L_] * G[BLijY_] / iy;
      lambdao = Math.min(lambdao, B[L_] * G[BLijZ_] / iz);
      if (G[TypeMaterial_] == 0) {
      } else {
        if (G[TypeMaterial_] == 1) {
          System.out.println("Error in " + G[INDEX_] + " group, the material number " + G[TypeMaterial_] + " is not implemented");
        } else {
          if (G[TypeMaterial_] == 2) {
            double lambda = lambdao;
            if (lambda <= 150) {
              w = 1.113 + 0.0070516 * lambda - 0.000132108 * Math.pow(lambda, 2.0) + 0.000002106132 * Math.pow(lambda, 3.0) - 0.00000000397368332151 * Math.pow(lambda, 4.0);
            } else {
              w = 25.0;
            }
          } else {
            if (G[TypeMaterial_] == 3) {
              System.out.println("Error in " + G[INDEX_] + " group, the material number " + G[TypeMaterial_] + " is not implemented");
            } else {
              if (G[TypeMaterial_] == 10) {
                System.out.println("Error in " + G[INDEX_] + " group, the material number " + G[TypeMaterial_] + " is not implemented");
              } else {
                if (G[TypeMaterial_] == 12) {
                  System.out.println("Error in " + G[INDEX_] + " group, the material number " + G[TypeMaterial_] + " is not implemented");
                } else {
                  if (G[TypeMaterial_] == 14) {
                  } else {
                    if (G[TypeMaterial_] == 20) {
                      double lambda = lambdao;
                      if (lambda <= 150) {
                        w = 1.048 + 0.005524 * lambda - 0.000101666 * Math.pow(lambda, 2.0) + 0.00000301687 * Math.pow(lambda, 3.0) - 0.000000004366246 * Math.pow(lambda, 4.0);
                      } else {
                        w = 25.0;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return w;
  }

  public void EBEsTransversalSectionCircular(int gr, double d) throws JMetalException {
    double r;
    double Am;
    double y;
    Groups_[gr][Y_] = d;
    Groups_[gr][Z_] = d;
    Groups_[gr][eY_] = 0.0;
    Groups_[gr][eZ_] = 0.0;
    r = d / 2.0;
    Groups_[gr][uY_] = r;
    Groups_[gr][dY_] = r;
    Groups_[gr][lZ_] = r;
    Groups_[gr][rZ_] = r;
    y = 4.0 * r / (3.0 * Math.PI);
    Am = Math.PI * Math.pow(r, 2.0) / 2.0;
    Groups_[gr][Az_] = Am * Math.pow(y, 2.0);
    Groups_[gr][Ay_] = Groups_[gr][Az_];
    Groups_[gr][AREA] = Math.PI * Math.pow(d, 2.0) / 4.0;
    Groups_[gr][Iz_] = Math.PI * Math.pow(d, 4.0) / 64.0;
    Groups_[gr][Iy_] = Groups_[gr][Iz_];
    Groups_[gr][It_] = Math.PI * Math.pow(d, 4.0) / 32.0;
    Groups_[gr][Iw_] = Groups_[gr][It_];
  }

  public void EBEsTransversalSectionHoleCircular(int gr, double D, double e) throws JMetalException {
    double d, R, r, Y, y, Am, am;
    Groups_[gr][Y_] = D;
    Groups_[gr][Z_] = D;
    Groups_[gr][eY_] = e;
    Groups_[gr][eZ_] = e;
    Groups_[gr][uY_] = D / 2.0;
    Groups_[gr][dY_] = D / 2.0;
    Groups_[gr][lZ_] = D / 2.0;
    Groups_[gr][rZ_] = D / 2.0;
    R = D / 2.0;
    d = D - 2.0 * e;
    r = d / 2.0;
    Y = 4.0 * R / (3.0 * Math.PI);
    y = 4.0 * r / (3.0 * Math.PI);
    Am = Math.PI * Math.pow(R, 2.0) / 2.0;
    am = Math.PI * Math.pow(r, 2.0) / 2.0;
    Groups_[gr][Az_] = Am * Math.pow(Y, 2.0) - am * Math.pow(y, 2.0);
    Groups_[gr][Ay_] = Groups_[gr][Az_];
    Groups_[gr][AREA] = Math.PI / 4.0 * (Math.pow(D, 2.0) - Math.pow(d, 2.0));
    Groups_[gr][Iz_] = Math.PI / 64.0 * (Math.pow(D, 4.0) - Math.pow(d, 4.0));
    Groups_[gr][Iy_] = Groups_[gr][Iz_];
    Groups_[gr][It_] = Math.PI / 32.0 * (Math.pow(D, 4.0) - Math.pow(d, 4.0));
    Groups_[gr][Iw_] = Groups_[gr][It_];
  }

  public void EBEsTransversalSectionRectangle(int gr, double y, double z) throws JMetalException {
    double y1, z1;
    Groups_[gr][Y_] = y;
    Groups_[gr][Z_] = z;
    Groups_[gr][eY_] = 0.0;
    Groups_[gr][eZ_] = 0.0;
    z1 = z / 2.0;
    y1 = y / 2.0;
    Groups_[gr][uY_] = y1;
    Groups_[gr][dY_] = y1;
    Groups_[gr][lZ_] = z1;
    Groups_[gr][rZ_] = z1;
    Groups_[gr][Ay_] = y * z1 * z1 / 2.0;
    Groups_[gr][Az_] = z * y1 * y1 / 2.0;
    Groups_[gr][AREA] = z * y;
    Groups_[gr][Iz_] = z * Math.pow(y, 3.0) / 12.0;
    Groups_[gr][Iy_] = y * Math.pow(z, 3.0) / 12.0;
    if (z / y >= 1.0) {
      Groups_[gr][It_] = 0.22 * z * Math.pow(y, 3.0);
    } else {
      Groups_[gr][It_] = 0.22 * y * Math.pow(z, 3.0);
    }
    Groups_[gr][Iw_] = Groups_[gr][It_];
  }

  public void EBEsTransversalSectionHoleRectangle(int gr, double y, double z, double ey, double ez) throws JMetalException {
    double yi, zi;
    double as, ys, es, al, yl, el;
    double zl, ae, ze, ee;
    Groups_[gr][Y_] = y;
    Groups_[gr][Z_] = z;
    Groups_[gr][eY_] = ey;
    Groups_[gr][eZ_] = ez;
    Groups_[gr][uY_] = y / 2.0;
    Groups_[gr][dY_] = y / 2.0;
    Groups_[gr][lZ_] = z / 2.0;
    Groups_[gr][rZ_] = z / 2.0;
    yi = y - 2 * ey;
    zi = z - 2 * ez;
    al = y * ez;
    zl = zi / 2.0 + ez / 2.0;
    el = al * Math.pow(zl, 2.0);
    ae = ey * zi / 2.0;
    ze = zi / 4.0;
    ee = 2.0 * ae * Math.pow(ze, 2.0);
    Groups_[gr][Ay_] = el + ee;
    as = z * ey;
    ys = yi / 2.0 + ey / 2.0;
    es = as * Math.pow(ys, 2.0);
    al = ez * (yi / 2.0);
    yl = yi / 4.0;
    el = 2.0 * al * Math.pow(yl, 2.0);
    Groups_[gr][Az_] = es + el;
    double Ai = zi * yi;
    double At = z * y;
    Groups_[gr][AREA] = At - Ai;
    double Iez = z * Math.pow(y, 3.0) / 12.0;
    double Iiz = zi * Math.pow(yi, 3.0) / 12.0;
    Groups_[gr][Iz_] = Iez - Iiz;
    double Iey = y * Math.pow(z, 3) / 12;
    double Iiy = yi * Math.pow(zi, 3) / 12;
    Groups_[gr][Iy_] = Iey - Iiy;
    double It1 = 1.3 * 1 / 3 * (2 * z * Math.pow(ey, 3) + 2 * yi * Math.pow(ez, 3));
    double It2 = Groups_[gr][Iz_] + Groups_[gr][Iy_];
    Groups_[gr][It_] = (It1 + It2) / 2;
    Groups_[gr][Iw_] = Groups_[gr][It_];
  }

  public void EBEsTransversalSection_I_Single(int gr, double y, double z, double ey, double ez) throws JMetalException {
    double yi, zi;
    double as, ys, es, al, yl, el;
    double zl, ae, ze, ee;
    Groups_[gr][Y_] = y;
    Groups_[gr][Z_] = z;
    Groups_[gr][eY_] = ey;
    Groups_[gr][eZ_] = ez;
    Groups_[gr][uY_] = y / 2.0;
    Groups_[gr][dY_] = y / 2.0;
    Groups_[gr][lZ_] = ez / 2.0;
    Groups_[gr][rZ_] = ez / 2.0;
    yi = y - 2 * ey;
    zi = z - ez;
    al = yi * ez;
    zl = ez / 2;
    el = al * Math.pow(zl, 2.0);
    ae = ey * z;
    ze = z / 4.0;
    ee = ae * Math.pow(ze, 2.0);
    Groups_[gr][Ay_] = el + ee;
    as = z * ey;
    ys = yi / 2.0 + ey / 2.0;
    es = as * Math.pow(ys, 2.0);
    al = ez * (yi / 2.0);
    yl = yi / 4.0;
    el = al * Math.pow(yl, 2.0);
    Groups_[gr][Az_] = es + el;
    double Ai = zi * yi;
    double At = z * y;
    Groups_[gr][AREA] = At - Ai;
    double Iez = z * Math.pow(y, 3.0) / 12.0;
    double Iiz = zi * Math.pow(yi, 3.0) / 12.0;
    Groups_[gr][Iz_] = Iez - Iiz;
    Groups_[gr][Iy_] = y * Math.pow(ez, 3.0) / 12.0;
    Groups_[gr][It_] = (2 * z * Math.pow(ey, 3.0) + yi * Math.pow(ez, 3.0)) / 3.0;
    Groups_[gr][Iw_] = Groups_[gr][It_];
  }

  public void EBEsTransversalSection_I_Double(int gr, double y, double z, double ey, double ez) throws JMetalException {
  }

  public void EBEsTransversalSection_H_Single(int gr, double y, double z, double ey, double ez) throws JMetalException {
  }

  public void EBEsTransversalSection_H_Double(int gr, double y, double z, double ey, double ez) throws JMetalException {
  }

  public void EBEsTransversalSection_L_Single(int gr, double y, double z, double ey, double ez) throws JMetalException {
  }

  public void EBEsTransversalSection_L_Double(int gr, double y, double z, double ey, double ez) throws JMetalException {
    double yi, zi;
    Groups_[gr][Y_] = y;
    Groups_[gr][Z_] = z;
    Groups_[gr][eY_] = ey;
    Groups_[gr][eZ_] = ez;
    yi = y - ey;
    zi = z - 2 * ez;
    double Ai = zi * yi;
    double At = z * y;
    Groups_[gr][AREA] = At - Ai;
    Groups_[gr][dY_] = 1 / 2.0 * (ez * Math.pow(y, 2) + zi * Math.pow(ey, 2)) / (ez * y + zi * ey);
    Groups_[gr][uY_] = y - Groups_[gr][dY_];
    Groups_[gr][rZ_] = z / 2.0;
    Groups_[gr][lZ_] = z / 2.0;
    Groups_[gr][Ay_] = 0;
    Groups_[gr][Az_] = 0;
    Groups_[gr][Iz_] = 0;
    Groups_[gr][Iy_] = 0;
    Groups_[gr][It_] = 0;
    Groups_[gr][Iw_] = 0;
  }

  public void EBEsTransversalSection_T_Single(int ba, double y, double z, double ey, double ez) throws JMetalException {
  }

  public void EBEsTransversalSection_T_Double(int ba, double y, double z, double ey, double ez) throws JMetalException {
  }

  public double Interpolation_I_Single_Y_func_Area_(double A) {
    double Y = 0;
    if (5.0 < A && A < 1000.0) {
      Y = 0.000003 * Math.pow(A, 3) - 0.0063 * Math.pow(A, 2) + 4.1118 * A + 75.414;
    }
    return Y;
  }

  public double Interpolation_I_Single_Y_func_Wxy_(double Wxy) {
    double Y = 0;
    if (1.0 < Wxy && Wxy < 400.0) {
      Y = 0.0003 * Math.pow(Wxy, 3) - 0.119 * Math.pow(Wxy, 2) + 16.539 * Wxy + 136.59;
    }
    return Y;
  }

  public double Interpolation_I_Single_Y_func_Wxz_(double Wxz) {
    double Y = 0;
    if (3.0 < Wxz && Wxz < 3500.0) {
      Y = 61.9 * Math.pow(Wxz, 0.3849);
    }
    return Y;
  }

  public double Interpolation_I_Single_Z_func_Y_(double Y) {
    double Z = 0;
    if (50.0 < Y && Y < 2000.0) {
      Z = -0.0002 * Math.pow(Y, 2.0) + 0.4339 * Y + 29.849;
    }
    return Z;
  }

  public double Interpolation_I_Single_ez_func_Y_(double Y) {
    double ez = 0;
    if (50.0 < Y && Y < 2000.0) {
      ez = 7E-08 * Math.pow(Y, 3.0) - 0.0001 * Math.pow(Y, 2.0) + 0.0632 * Y - 3.3817;
    }
    return ez;
  }

  public double Interpolation_I_Single_ey_func_Y_(double Y) {
    double ey = 0;
    if (50.0 < Y && Y < 2000.0) {
      ey = 1E-07 * Math.pow(Y, 3.0) - 0.0002 * Math.pow(Y, 2.0) + 0.1014 * Y - 4.5708;
    }
    return ey;
  }

  public void EBEsStrainMaxWhitElement() throws JMetalException {
    for (int hi = 0; hi < numberOfWeigthHypothesis_; hi++) {
      StrainMax_[0][hi] = Straini_[0][0][hi];
      StrainCutMax_[0][hi] = Math.abs(Straini_[2][0][hi]);
      for (int ba = 0; ba < numberOfElements_; ba++) {
        if (StrainMax_[ba][hi] < Straini_[0][ba][hi]) {
          StrainMax_[ba][hi] = Straini_[0][ba][hi];
        }
        if (StrainMax_[ba][hi] < Strainj_[0][ba][hi]) {
          StrainMax_[ba][hi] = Strainj_[0][ba][hi];
        }
        if (StrainMax_[ba][hi] < Straini_[1][ba][hi]) {
          StrainMax_[ba][hi] = Straini_[1][ba][hi];
        }
        if (StrainMax_[ba][hi] < Strainj_[1][ba][hi]) {
          StrainMax_[ba][hi] = Strainj_[1][ba][hi];
        }
        if (StrainCutMax_[ba][hi] < Math.abs(Straini_[2][ba][hi])) {
          StrainCutMax_[ba][hi] = Math.abs(Straini_[2][ba][hi]);
        }
        if (StrainCutMax_[ba][hi] < Math.abs(Strainj_[2][ba][hi])) {
          StrainCutMax_[ba][hi] = Math.abs(Strainj_[2][ba][hi]);
        }
      }
    }
  }

  public void EBEsStrainMinWhitElement() throws JMetalException {
    for (int hi = 0; hi < numberOfWeigthHypothesis_; hi++) {
      StrainMin_[0][hi] = Straini_[0][0][hi];
      for (int ba = 0; ba < numberOfElements_; ba++) {
        if (StrainMin_[ba][hi] > Straini_[0][ba][hi]) {
          StrainMin_[ba][hi] = Straini_[0][ba][hi];
        }
        if (StrainMin_[ba][hi] > Strainj_[0][ba][hi]) {
          StrainMin_[ba][hi] = Strainj_[0][ba][hi];
        }
        if (StrainMin_[ba][hi] > Straini_[1][ba][hi]) {
          StrainMin_[ba][hi] = Straini_[1][ba][hi];
        }
        if (StrainMin_[ba][hi] > Strainj_[1][ba][hi]) {
          StrainMin_[ba][hi] = Strainj_[1][ba][hi];
        }
      }
    }
  }

  public void EBEsStrainMaxWhitGroup() throws JMetalException {
    for (int hi = 0; hi < numberOfWeigthHypothesis_; hi++) {
      StrainMax_[0][hi] = Straini_[0][0][hi];
      StrainCutMax_[0][hi] = Straini_[2][0][hi];
      for (int ba = 0; ba < numberOfElements_; ba++) {
        int idx = (int) Element_[ba][INDEX_];
        if (StrainMax_[idx][hi] < Straini_[0][ba][hi]) {
          StrainMax_[idx][hi] = Straini_[0][ba][hi];
        }
        if (StrainMax_[idx][hi] < Strainj_[0][ba][hi]) {
          StrainMax_[idx][hi] = Strainj_[0][ba][hi];
        }
        if (StrainMax_[idx][hi] < Straini_[1][ba][hi]) {
          StrainMax_[idx][hi] = Straini_[1][ba][hi];
        }
        if (StrainMax_[idx][hi] < Strainj_[1][ba][hi]) {
          StrainMax_[idx][hi] = Strainj_[1][ba][hi];
        }
        if (StrainCutMax_[idx][hi] < Math.abs(Straini_[2][ba][hi])) {
          StrainCutMax_[idx][hi] = Math.abs(Straini_[2][ba][hi]);
        }
        if (StrainCutMax_[idx][hi] < Math.abs(Strainj_[2][ba][hi])) {
          StrainCutMax_[idx][hi] = Math.abs(Strainj_[2][ba][hi]);
        }
      }
    }
  }

  public void EBEsStrainMinWhitGroup() throws JMetalException {
    for (int hi = 0; hi < numberOfWeigthHypothesis_; hi++) {
      StrainMin_[0][hi] = Straini_[0][0][hi];
      for (int ba = 0; ba < numberOfElements_; ba++) {
        int idx = (int) Element_[ba][INDEX_];
        if (StrainMin_[idx][hi] > Straini_[0][ba][hi]) {
          StrainMin_[idx][hi] = Straini_[0][ba][hi];
        }
        if (StrainMin_[idx][hi] > Strainj_[0][ba][hi]) {
          StrainMin_[idx][hi] = Strainj_[0][ba][hi];
        }
        if (StrainMin_[idx][hi] > Straini_[1][ba][hi]) {
          StrainMin_[idx][hi] = Straini_[1][ba][hi];
        }
        if (StrainMin_[idx][hi] > Strainj_[1][ba][hi]) {
          StrainMin_[idx][hi] = Strainj_[1][ba][hi];
        }
      }
    }
  }

  public void EBEsStrainResidualVerication() throws JMetalException {
    for (int hi = 0; hi < numberOfWeigthHypothesis_; hi++) {
      for (int gr = 0; gr < numberOfGroupElements_; gr++) {
        if (StrainMax_[gr][hi] != 0.0) {
          StrainResidualMax_[hi] += Math.sqrt(Math.pow((StrainMax_[gr][hi] - Groups_[(int) Element_[gr][INDEX_]][STRESS]), 2.0));
        }
        if (StrainMin_[gr][hi] != 0.0) {
          StrainResidualMin_[hi] += Math.sqrt(Math.pow((-StrainMin_[gr][hi] + Groups_[(int) Element_[gr][INDEX_]][COMPRESSION]), 2.0));
        }
        if (StrainCutMax_[gr][hi] != 0.0) {
          StrainResidualCut_[hi] += Math.sqrt(Math.pow((StrainCutMax_[gr][hi] - Groups_[(int) Element_[gr][INDEX_]][STRESS_CUT]), 2.0));
        }
      }
    }
  }

  public void EBEsPrintArchTxtElements() throws JMetalException {
    try {
      PrintStream ps = new PrintStream("EBEs - Groups Elements.txt");
      ps.printf("Groups    Y      Z      eY_     eZ_    uY    dY   lZ    rZ    A      Az    Ay    Iz      Iy      Ip");
      ps.println();
      ps.printf("-----------------------------------------------------------------------------");
      ps.println();
      for (int gr = 0; gr < Groups_.length; gr++) {
        ps.printf("%4d %6.3f %6.3f %7.4f %7.4f %6.3f %6.3f %6.3f %6.3f %9.6f %9.6f %9.6f %9.6f %9.6f", gr, Groups_[gr][Y_], Groups_[gr][Z_], Groups_[gr][eY_], Groups_[gr][eZ_], Groups_[gr][uY_], Groups_[gr][dY_], Groups_[gr][lZ_], Groups_[gr][rZ_], Groups_[gr][Az_], Groups_[gr][Ay_], Groups_[gr][Iz_], Groups_[gr][Iy_], Groups_[gr][It_]);
        ps.println();
      }
      ps.close();
    } catch (Exception ex) {
      System.out.println("Grupos de barras: El archivo no pudo grabarse!");
    }
  }

  public void EBEsPrintArchTxtMKLB(int e) throws JMetalException {
    try {
      PrintStream ps = new PrintStream("EBEs-MKLB(" + e + ").txt");
      ps.print("kii" + e + "=[");
      for (int o = 0; o < 6; o++) {
        for (int p = 0; p < 6; p++) {
          ps.printf("%12.3f", Kii[o][p]);
          if (o != 5 && p == 5) {
            ps.print(";");
          } else {
            if (o == 5 && p == 5) {
              ps.print("]");
            } else {
              ps.print(",");
            }
          }
        }
      }
      ps.println();
      ps.print("kij" + e + "=[");
      for (int o = 0; o < 6; o++) {
        for (int p = 0; p < 6; p++) {
          ps.printf("%12.3f", Kij[o][p]);
          if (o != 5 && p == 5) {
            ps.print(";");
          } else {
            if (o == 5 && p == 5) {
              ps.print("]");
            } else {
              ps.print(",");
            }
          }
        }
      }
      ps.println();
      ps.print("kji" + e + "=[");
      for (int o = 0; o < 6; o++) {
        for (int p = 0; p < 6; p++) {
          ps.printf("%12.3f", Kji[o][p]);
          if (o != 5 && p == 5) {
            ps.print(";");
          } else {
            if (o == 5 && p == 5) {
              ps.print("]");
            } else {
              ps.print(",");
            }
          }
        }
      }
      ps.println();
      ps.print("kjj" + e + "=[");
      for (int o = 0; o < 6; o++) {
        for (int p = 0; p < 6; p++) {
          ps.printf("%12.3f", Kjj[o][p]);
          if (o != 5 && p == 5) {
            ps.print(";");
          } else {
            if (o == 5 && p == 5) {
              ps.print("]");
            } else {
              ps.print(",");
            }
          }
        }
      }
      ps.println();
      ps.close();
    } catch (Exception ex) {
      System.out.println("Mat Rig Local: El archivo no pudo grabarse!");
    }
  }

  public void EBEsPrintArchTxtMKG(String s, int hi) throws JMetalException {
    try {
      PrintStream ps = new PrintStream("EBEs-M" + s + "-H(" + hi + ").txt");
      for (int o = 0; o < MatrixStiffness_.length; o++) {
        ps.printf("(%5d) - %15.4f", o, MatrixStiffness_[o]);
        ps.println();
      }
      ps.close();
    } catch (Exception ex) {
      System.out.println("Mat Rig Global: El archivo no pudo grabarse!");
    }
  }

  public void EBEsPrintArchTxtDesp(int hi) throws JMetalException {
    try {
      PrintStream ps = new PrintStream("EBEs-Desp-H(" + hi + ").txt");
      for (int o = 0; o < DisplacementNodes_.length; o++) {
        ps.printf("(%5d, %2d) = %20.16f", o, hi, DisplacementNodes_[o][hi]);
        ps.println();
      }
      ps.close();
    } catch (Exception ex) {
      System.out.println("Desplazamientos: El archivo no pudo grabarse!");
    }
  }

  public void EBEsPrintArchTxtEfforts(int hi) throws JMetalException {
    try {
      PrintStream ps = new PrintStream("EBEs-Efforts-H(" + hi + ").txt");
      for (int ba = 0; ba < Element_.length; ba++) {
        int ni = (int) Element_[ba][i_];
        int nj = (int) Element_[ba][j_];
        ps.printf("Ei(%3d,%3d)=%10.3f  %10.3f  %10.3f  %10.3f  %10.3f  %10.3f", ba, ni, Efforti_[0][ba][hi], Efforti_[1][ba][hi], Efforti_[2][ba][hi], Efforti_[3][ba][hi], Efforti_[4][ba][hi], Efforti_[5][ba][hi]);
        ps.println();
        ps.printf("Ej(%3d,%3d)=%10.3f  %10.3f  %10.3f  %10.3f  %10.3f  %10.3f", ba, nj, Effortj_[0][ba][hi], Effortj_[1][ba][hi], Effortj_[2][ba][hi], Effortj_[3][ba][hi], Effortj_[4][ba][hi], Effortj_[5][ba][hi]);
        ps.println();
        ps.println();
      }
      ps.close();
    } catch (Exception ex) {
      System.out.println("Esfuerzos: El archivo no pudo grabarse!");
    }
  }

  public void EBEsPrintArchTxtStrain() throws JMetalException {
    try {
      for (int hi = 0; hi < numberOfWeigthHypothesis_; hi++) {
        PrintStream ps = new PrintStream("EBEs-Strain-H(" + hi + ").txt");
        ps.printf("Elements  Nodo   Stracc    Scomp     Scut");
        ps.println();
        ps.printf("--------------------------------------------");
        ps.println();
        for (int ba = 0; ba < Element_.length; ba++) {
          int ni = (int) Element_[ba][i_];
          int nj = (int) Element_[ba][j_];
          ps.printf("%4d  %4d  %10.3f  %10.3f  %10.3f", ba, ni, Straini_[STRAIN_TRACTION][ba][hi], Straini_[STRAIN_COMPRESS][ba][hi], Straini_[STRAIN_CUT][ba][hi]);
          ps.println();
          ps.printf("%4d  %4d  %10.3f  %10.3f  %10.3f", ba, nj, Strainj_[STRAIN_TRACTION][ba][hi], Strainj_[STRAIN_COMPRESS][ba][hi], Strainj_[STRAIN_CUT][ba][hi]);
          ps.println();
          ps.println();
        }
        ps.close();
      }
    } catch (Exception ex) {
      System.out.println("Tensiones: El archivo no pudo grabarse!");
    }
  }

  public void EBEsPrintArchTxtReaction(int hi) throws JMetalException {
    try {
      PrintStream ps = new PrintStream("EBEs-Reaction-H(" + hi + ").txt");
      ps.printf("Nodo   Restriction   X    Y   Z   MX    MY    MZ");
      ps.println();
      ps.printf("--------------------------------------------");
      ps.println();
      for (int o = 0; o < NodeRestrict_.length; o++) {
        int no = (int) NodeRestrict_[o][0];
        int ap = (int) NodeRestrict_[o][1];
        double x = Reaction_[6 * no + aX_][hi];
        double y = Reaction_[6 * no + aY_][hi];
        double z = Reaction_[6 * no + aZ_][hi];
        double mx = Reaction_[6 * no + gX_][hi];
        double my = Reaction_[6 * no + gY_][hi];
        double mz = Reaction_[6 * no + gZ_][hi];
        ps.printf("%5d  %6d  %8.3f %8.3f %8.3f %8.3f %8.3f %8.3f", no, ap, x, y, z, mx, my, mz);
        ps.println();
      }
      ps.close();
    } catch (Exception ex) {
      System.out.println("Reacciones: El archivo no pudo grabarse!");
    }
  }

  public String EBEsReadProblems() throws FileNotFoundException {
    char ch;
    String line = "";
    String var1 = "";
    String txt = "";
    int i = 0, j = 0;
    InputStream inputStream = getClass().getResourceAsStream("/" + "Ebes.txt");
    if (inputStream == null) {
      inputStream = new FileInputStream("Ebes.txt");
    }
    InputStreamReader isr = new InputStreamReader(inputStream);
    BufferedReader br = new BufferedReader(isr);
    Scanner input = new Scanner(br);
    line = input.nextLine();
    j = 0;
    for (i = line.length() - 1; i >= 0; i--) {
      ch = line.charAt(i);
      if (ch == ' ') {
        j++;
      }
    }
    OF_ = new String[j];
    int indOF = j - 1;
    j = 0;
    for (i = line.length() - 1; i >= 0; i--) {
      ch = line.charAt(i);
      if (ch == ' ') {
        j = i + 1;
        break;
      }
    }
    OF_[indOF] = line.substring(j);
    indOF--;
    int m = 0;
    if (indOF >= 0) {
      for (i = j - 2; i >= 0; i--) {
        ch = line.charAt(i);
        if (ch == ' ') {
          m = i + 1;
          break;
        }
      }
      OF_[indOF] = line.substring(m, j - 1);
      indOF--;
    }
    int n = 0;
    if (indOF >= 0) {
      for (i = m - 2; i >= 0; i--) {
        ch = line.charAt(i);
        if (ch == ' ') {
          n = i + 1;
          break;
        }
      }
      OF_[indOF] = line.substring(n, m - 1);
      indOF--;
    }
    int o = 0;
    if (indOF >= 0) {
      for (i = n - 2; i >= 0; i--) {
        ch = line.charAt(i);
        if (ch == ' ') {
          o = i + 1;
          break;
        }
      }
      OF_[indOF] = line.substring(o, n - 1);
      indOF--;
    }
    int p = 0;
    if (indOF >= 0) {
      for (i = o - 2; i >= 0; i--) {
        ch = line.charAt(i);
        if (ch == ' ') {
          p = i + 1;
          break;
        }
      }
      OF_[indOF] = line.substring(p, o - 1);
      indOF--;
    }
    line = line.substring(0, i);
    j = 0;
    for (i = line.length() - 1; i >= 0; i--) {
      ch = line.charAt(i);
      if (ch == ' ') {
        j = i + 1;
        break;
      }
    }
    var1 = line.substring(j);
    if (i == -1) {
      txt = var1;
    } else {
      txt = line.substring(0, i);
    }
    input.close();
    return txt;
  }

  public final void EBEsReadDataFile(String fileName) throws JMetalException {
    int i, j = 0;
    char ch;
    String txt = "";
    try {
      InputStream inputStream = getClass().getResourceAsStream("/" + fileName);
      if (inputStream == null) {
        inputStream = new FileInputStream(fileName);
      }
      Scanner input = new Scanner(inputStream);
      while (input.hasNext()) {
        for (i = 0; i < 5; i++) {
          txt = input.nextLine();
        }
        for (i = txt.length() - 1; i >= 0; i--) {
          ch = txt.charAt(i);
          if (ch == ' ') {
            j = i + 1;
            break;
          }
        }
        txt = txt.substring(j);
        numberOfNodes = Integer.valueOf(txt);
        txt = input.nextLine();
        for (i = txt.length() - 1; i >= 0; i--) {
          ch = txt.charAt(i);
          if (ch == ' ') {
            j = i + 1;
            break;
          }
        }
        txt = txt.substring(j);
        numberOfNodesRestricts_ = Integer.valueOf(txt);
        txt = input.nextLine();
        for (i = txt.length() - 1; i >= 0; i--) {
          ch = txt.charAt(i);
          if (ch == ' ') {
            j = i + 1;
            break;
          }
        }
        txt = txt.substring(j);
        numberOfGroupElements_ = Integer.valueOf(txt);
        txt = input.nextLine();
        for (i = txt.length() - 1; i >= 0; i--) {
          ch = txt.charAt(i);
          if (ch == ' ') {
            j = i + 1;
            break;
          }
        }
        txt = txt.substring(j);
        numberOfElements_ = Integer.valueOf(txt);
        for (i = 0; i < 5; i++) {
          txt = input.nextLine();
        }
        for (i = txt.length() - 1; i >= 0; i--) {
          ch = txt.charAt(i);
          if (ch == ' ') {
            j = i + 1;
            break;
          }
        }
        txt = txt.substring(j);
        numberOfWeigthHypothesis_ = Integer.valueOf(txt);
        numberOfWeigthHypothesis_ = 1;
        txt = input.nextLine();
        for (i = txt.length() - 1; i >= 0; i--) {
          ch = txt.charAt(i);
          if (ch == ' ') {
            j = i + 1;
            break;
          }
        }
        txt = txt.substring(j);
        lLoadsOwnWeight = Boolean.valueOf(txt);
        txt = input.nextLine();
        for (i = txt.length() - 1; i >= 0; i--) {
          ch = txt.charAt(i);
          if (ch == ' ') {
            j = i + 1;
            break;
          }
        }
        txt = txt.substring(j);
        numberOfWeigthsElements_ = Integer.valueOf(txt);
        txt = input.nextLine();
        for (i = txt.length() - 1; i >= 0; i--) {
          ch = txt.charAt(i);
          if (ch == ' ') {
            j = i + 1;
            break;
          }
        }
        txt = txt.substring(j);
        numberOfWeigthsNodes_ = Integer.valueOf(txt);
        for (i = 0; i < 4; i++) {
          txt = input.nextLine();
        }
        for (i = txt.length() - 1; i >= 0; i--) {
          ch = txt.charAt(i);
          if (ch == ' ') {
            j = i + 1;
            break;
          }
        }
        txt = txt.substring(j);
        numberOfConstraintsNodes_ = Integer.valueOf(txt);
        txt = input.nextLine();
        for (i = txt.length() - 1; i >= 0; i--) {
          ch = txt.charAt(i);
          if (ch == ' ') {
            j = i + 1;
            break;
          }
        }
        txt = txt.substring(j);
        numberOfGroupsToCheckGeometry_ = Integer.valueOf(txt);
        txt = input.nextLine();
        txt = input.nextLine();
        for (i = txt.length() - 1; i >= 0; i--) {
          ch = txt.charAt(i);
          if (ch == ' ') {
            j = i + 1;
            break;
          }
        }
        txt = txt.substring(j);
        lSecondOrderGeometric = Boolean.valueOf(txt);
        txt = input.nextLine();
        for (i = txt.length() - 1; i >= 0; i--) {
          ch = txt.charAt(i);
          if (ch == ' ') {
            j = i + 1;
            break;
          }
        }
        txt = txt.substring(j);
        lBuckling = Boolean.valueOf(txt);
        for (i = 0; i < 3; i++) {
          txt = input.nextLine();
        }
        Node_ = new double[numberOfNodes][4];
        for (i = 0; i < numberOfNodes; i++) {
          txt = input.next();
          for (j = 0; j < 4; j++) {
            Node_[i][j] = Double.valueOf(input.next());
          }
          for (j = 0; j < 6; j++) {
            txt = input.next();
          }
        }
        NodeRestrict_ = new double[numberOfNodesRestricts_][2];
        j = 0;
        for (i = 0; i < numberOfNodes; i++) {
          if (Node_[i][3] != 0) {
            NodeRestrict_[j][0] = i;
            NodeRestrict_[j][1] = Node_[i][3];
            j++;
          }
        }
        txt = input.nextLine();
        txt = input.nextLine();
        Groups_ = new double[numberOfGroupElements_][MAX_COLUMN];
        for (i = 0; i < numberOfGroupElements_; i++) {
          for (j = 0; j < MAX_COLUMN - 1; j++) {
            Groups_[i][j] = Double.valueOf(input.next());
          }
          input.next();
        }
        txt = input.nextLine();
        txt = input.nextLine();
        Element_ = new double[numberOfElements_][8];
        for (i = 0; i < numberOfElements_; i++) {
          txt = input.next();
          Element_[i][INDEX_] = Double.valueOf(input.next());
          Element_[i][i_] = Double.valueOf(input.next());
          Element_[i][j_] = Double.valueOf(input.next());
          Element_[i][L_] = Double.valueOf(input.next());
          Element_[i][Vij_] = Double.valueOf(input.next());
          Element_[i][Ei_] = Double.valueOf(input.next());
          Element_[i][Ej_] = Double.valueOf(input.next());
          int ni = (int) Element_[i][i_];
          int nj = (int) Element_[i][j_];
          double xi, yi, zi;
          double xj, yj, zj;
          xi = Node_[ni][aX_];
          yi = Node_[ni][aY_];
          zi = Node_[ni][aZ_];
          xj = Node_[nj][aX_];
          yj = Node_[nj][aY_];
          zj = Node_[nj][aZ_];
          Element_[i][L_] = Math.sqrt(Math.pow((xj - xi), 2.0) + Math.pow((yj - yi), 2.0) + Math.pow((zj - zi), 2.0));
          if (Element_[i][L_] < 0.001) {
            Element_[i][L_] = 0.0;
          }
        }
        txt = input.nextLine();
        txt = input.nextLine();
        OverloadInElement_ = new double[numberOfWeigthsElements_][8];
        for (i = 0; i < numberOfWeigthsElements_; i++) {
          txt = input.next();
          for (j = 0; j < 8; j++) {
            OverloadInElement_[i][j] = Double.valueOf(input.next());
          }
        }
        txt = input.nextLine();
        if (numberOfWeigthsElements_ != 0) {
          txt = input.nextLine();
        }
        WeightNode_ = new double[numberOfWeigthsNodes_][8];
        for (i = 0; i < numberOfWeigthsNodes_; i++) {
          txt = input.next();
          for (j = 0; j < 8; j++) {
            WeightNode_[i][j] = Double.valueOf(input.next());
          }
        }
        txt = input.nextLine();
        txt = input.nextLine();
        txt = input.nextLine();
        txt = input.nextLine();
        if (numberOfWeigthsNodes_ != 0) {
          txt = input.nextLine();
        }
        nodeCheck_ = new double[numberOfConstraintsNodes_][2];
        for (i = 0; i < numberOfConstraintsNodes_; i++) {
          nodeCheck_[i][0] = Double.valueOf(input.next());
          nodeCheck_[i][1] = Double.valueOf(input.next());
        }
        txt = input.nextLine();
        txt = input.nextLine();
        if (numberOfGroupsToCheckGeometry_ != 0) {
          geometryCheck_ = new int[numberOfGroupsToCheckGeometry_][];
          for (i = 0; i < numberOfGroupsToCheckGeometry_; i++) {
            txt = input.nextLine();
            geometryCheck_[i] = new int[(txt.length() + 1) / 2];
            String aTxt[] = txt.split(" ");
            int k = 0;
            for (j = 0; j < aTxt.length; j++) {
              if (aTxt[j] != " ") {
                geometryCheck_[i][k] = Integer.parseInt(aTxt[j]);
                k++;
              }
            }
          }
        }
        while (input.hasNext()) {
          txt = input.nextLine();
        }
      }
      input.close();
    } catch (Exception ex) {
      System.out.println("Error: data file EBEs not readed");
      System.out.println(ex.getMessage());
      System.exit(1);
    }
  }

  public int Variable_Position() {
    int numberOfVariables_ = 0;
    try {
      numberOfConstraintsGeometric_ = 0;
      for (int gr = 0; gr < numberOfGroupElements_; gr++) {
        if (Groups_[gr][SHAPE] == CIRCLE) {
          numberOfVariables_ += 1;
          Groups_[gr][VARIABLES] = 1;
          Groups_[gr][VAR_POSITION] = numberOfVariables_ - 1;
          numberOfConstraintsGeometric_ += 0;
          Groups_[gr][CONSTRAINT] = 0;
        } else {
          if (Groups_[gr][SHAPE] == HOLE_CIRCLE) {
            numberOfVariables_ += 2;
            Groups_[gr][VARIABLES] = 2;
            Groups_[gr][VAR_POSITION] = numberOfVariables_ - 2;
            numberOfConstraintsGeometric_ += 2;
            Groups_[gr][CONSTRAINT] = 2;
          } else {
            if (Groups_[gr][SHAPE] == RECTANGLE) {
              numberOfVariables_ += 2;
              Groups_[gr][VARIABLES] = 2;
              Groups_[gr][VAR_POSITION] = numberOfVariables_ - 2;
              numberOfConstraintsGeometric_ += 2;
              Groups_[gr][CONSTRAINT] = 2;
            } else {
              if (Groups_[gr][SHAPE] == HOLE_RECTANGLE) {
                numberOfVariables_ += 4;
                Groups_[gr][VARIABLES] = 4;
                Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
                numberOfConstraintsGeometric_ += 4;
                Groups_[gr][CONSTRAINT] = 4;
              } else {
                if (Groups_[gr][SHAPE] == I_SINGLE) {
                  numberOfVariables_ += 4;
                  Groups_[gr][VARIABLES] = 4;
                  Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
                  numberOfConstraintsGeometric_ += 4;
                  Groups_[gr][CONSTRAINT] = 4;
                } else {
                  if (Groups_[gr][SHAPE] == I_DOUBLE) {
                    numberOfVariables_ += 4;
                    Groups_[gr][VARIABLES] = 4;
                    Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
                    numberOfConstraintsGeometric_ += 4;
                    Groups_[gr][CONSTRAINT] = 4;
                  } else {
                    if (Groups_[gr][SHAPE] == H_SINGLE) {
                      numberOfVariables_ += 4;
                      Groups_[gr][VARIABLES] = 4;
                      Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
                      numberOfConstraintsGeometric_ += 4;
                      Groups_[gr][CONSTRAINT] = 4;
                    } else {
                      if (Groups_[gr][SHAPE] == H_DOUBLE) {
                        numberOfVariables_ += 4;
                        Groups_[gr][VARIABLES] = 4;
                        Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
                        numberOfConstraintsGeometric_ += 4;
                        Groups_[gr][CONSTRAINT] = 4;
                      } else {
                        if (Groups_[gr][SHAPE] == L_SINGLE) {
                          numberOfVariables_ += 4;
                          Groups_[gr][VARIABLES] = 4;
                          Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
                          numberOfConstraintsGeometric_ += 4;
                          Groups_[gr][CONSTRAINT] = 4;
                        } else {
                          if (Groups_[gr][SHAPE] == L_DOUBLE) {
                            numberOfVariables_ += 4;
                            Groups_[gr][VARIABLES] = 4;
                            Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
                            numberOfConstraintsGeometric_ += 4;
                            Groups_[gr][CONSTRAINT] = 4;
                          } else {
                            if (Groups_[gr][SHAPE] == T_SINGLE) {
                              numberOfVariables_ += 4;
                              Groups_[gr][VARIABLES] = 4;
                              Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
                              numberOfConstraintsGeometric_ += 4;
                              Groups_[gr][CONSTRAINT] = 4;
                            } else {
                              if (Groups_[gr][SHAPE] == T_DOUBLE) {
                                numberOfVariables_ += 4;
                                Groups_[gr][VARIABLES] = 4;
                                Groups_[gr][VAR_POSITION] = numberOfVariables_ - 4;
                                numberOfConstraintsGeometric_ += 4;
                                Groups_[gr][CONSTRAINT] = 4;
                              } else {
                                System.out.println("Error: transversal section not considerated in " + gr + " group");
                                System.exit(1);
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    } catch (Exception ex) {
      System.out.println(ex.getCause());
      System.out.println(ex.getMessage());
      System.exit(1);
    }
    return numberOfVariables_;
  }

  public double FunctionENS(int hi) {
    double SSRes = 0.0;
    double SSTot = 0.0;
    double ENS = 0.0;
    double mOmax = 0.0;
    double mOmin = 0.0;
    for (int i = 0; i < geometryCheck_.length; i++) {
      double[] Omax = new double[geometryCheck_[i].length];
      double[] Omin = new double[geometryCheck_[i].length];
      double[] Emax = new double[geometryCheck_[i].length];
      double[] Emin = new double[geometryCheck_[i].length];
      for (int j = 0; j < geometryCheck_[i].length; j++) {
        int gr = geometryCheck_[i][j];
        Emin[j] = StrainMin_[gr][hi];
        Omin[j] = Groups_[(int) Element_[gr][INDEX_]][COMPRESSION];
        Emax[j] = StrainMax_[gr][hi];
        Omax[j] = Groups_[(int) Element_[gr][INDEX_]][STRESS];
        mOmax += (Omax[j]);
        mOmin += (Omin[j]);
      }
      mOmax = 2 * mOmax / Omax.length;
      mOmin = 2 * mOmin / Omin.length;
      for (int k = 0; k < Omax.length; k++) {
        SSRes += Math.pow((Omin[k] - Emin[k]), 2.0) + Math.pow((Omax[k] - Emax[k]), 2.0);
        SSTot += Math.pow((Omin[k] - mOmin), 2.0) + Math.pow((Omax[k] - mOmax), 2.0);
      }
      ENS += SSRes / SSTot;
    }
    return ENS;
  }

  public double FunctionsMahalanobis_Distance_With_Variance(int hi) {
    double MD = 0.0;
    double[] MDi = new double[geometryCheck_.length];
    for (int i = 0; i < geometryCheck_.length; i++) {
      int N = geometryCheck_[i].length;
      double[] distY = new double[N];
      double[] distZ = new double[N];
      double sumY = 0.0;
      double sumZ = 0.0;
      double sumYxY = 0.0;
      double sumZxZ = 0.0;
      double sumYxZ = 0.0;
      double meanY = 0.0;
      double meanZ = 0.0;
      double S2Y = 0.0;
      double S2Z = 0.0;
      double SY = 0.0;
      double SZ = 0.0;
      double r = 0.0;
      for (int j = 0; j < geometryCheck_[i].length; j++) {
        distY[j] = Groups_[geometryCheck_[i][j]][Y_];
        distZ[j] = Groups_[geometryCheck_[i][j]][Z_];
        sumY += distY[j];
        sumZ += distZ[j];
        sumYxY += distY[j] * distY[j];
        sumZxZ += distZ[j] * distZ[j];
        sumYxZ += distY[j] * distZ[j];
        meanY += distY[j];
        meanZ += distZ[j];
      }
      meanY /= N;
      meanZ /= N;
      r = (N * sumYxZ - sumY * sumZ) / (Math.sqrt((N * sumYxY - Math.pow(sumY, 2.0)) * (N * sumZxZ - Math.pow(sumZ, 2.0))));
      for (int k = 0; k < N; k++) {
        S2Y += Math.pow((distY[k] - meanY), 2.0);
        S2Z += Math.pow((distZ[k] - meanZ), 2.0);
      }
      S2Y /= (N - 1);
      S2Z /= (N - 1);
      SY = Math.sqrt(S2Y);
      SZ = Math.sqrt(S2Z);
      for (int k = 1; k < N; k++) {
        MDi[i] += Math.pow((0 - distY[k]), 2.0) / S2Y + Math.pow((0 - distZ[k]), 2.0) / S2Z - 2.0 * r * (0 - distY[k]) * (0 - distZ[k]) / (SY * SZ);
      }
      MDi[i] = Math.sqrt(1 / (1 - Math.pow(r, 2.0)) * MDi[i]);
    }
    for (int i = 0; i < geometryCheck_.length; i++) {
      MD += MDi[i];
    }
    return MD;
  }
}