package jj2000.j2k.entropy.decoder;
import jj2000.j2k.decoder.DecoderSpecs;
import jj2000.j2k.entropy.StdEntropyCoderOptions;
import jj2000.j2k.image.DataBlk;
import jj2000.j2k.image.DataBlkInt;
import jj2000.j2k.util.ArrayUtil;
import jj2000.j2k.util.FacilityManager;
import jj2000.j2k.util.MsgLogger;
import jj2000.j2k.wavelet.Subband;
import jj2000.j2k.wavelet.synthesis.SubbandSyn;

/**
 * This class implements the JPEG 2000 entropy decoder, which codes stripes in
 * code-blocks. This entropy decoding engine decodes one code-block at a time.
 *
 * The code-block are rectangular, with dimensions which must be powers of
 * 2. Each dimension has to be no smaller than 4 and no larger than 256. The
 * product of the two dimensions (i.e. area of the code-block) may not exceed
 * 4096.
 *
 * Context 0 of the MQ-coder is used as the uniform one (uniform, non-adaptive
 * probability distribution). Context 1 is used for RLC coding. Contexts 2-10
 * are used for zero-coding (ZC), contexts 11-15 are used for sign-coding (SC)
 * and contexts 16-18 are used for magnitude-refinement (MR).
 *
 * <P>This implementation also provides some timing features. They can be
 * enabled by setting the 'DO_TIMING' constant of this class to true and
 * recompiling. The timing uses the 'System.currentTimeMillis()' Java API
 * call, which returns wall clock time, not the actual CPU time used. The
 * timing results will be printed on the message output. Since the times
 * reported are wall clock times and not CPU usage times they can not be added
 * to find the total used time (i.e. some time might be counted in several
 * places). When timing is disabled ('DO_TIMING' is false) there is no penalty
 * if the compiler performs some basic optimizations. Even if not the penalty
 * should be negligeable.
 * */
public class StdEntropyDecoder extends EntropyDecoder implements StdEntropyCoderOptions {
  /** Whether to collect timing information or not: false. Used as a compile
     * time directive. */
  private final static boolean DO_TIMING = false;

  /** The cumulative wall time for the entropy coding engine, for each
     * component. */
  private long time[];

  /** The bit based input for arithmetic coding bypass (i.e. raw) coding */
  private ByteToBitInput bin;

  /** The MQ decoder to use. It has in as the underlying source of coded
     * data. */
  private MQDecoder mq;

  /** The decoder spec */
  private DecoderSpecs decSpec;

  /** The options that are turned on, as flag bits. The options are
     * 'OPT_TERM_PASS', 'OPT_RESET_MQ', 'OPT_VERT_STR_CAUSAL', 'OPT_BYPASS' and
     * 'OPT_SEG_SYMBOLS' as defined in the StdEntropyCoderOptions interface
     *
     * @see StdEntropyCoderOptions
     **/
  private int options;

  /** Flag to indicate if we should try to detect errors or just ignore any
     * error resilient information */
  private final boolean doer;

  /** Flag to indicate if we should be verbose about bit stream errors
        detected with the error resilience options */
  private final boolean verber;

  /** Number of bits used for the Zero Coding lookup table */
  private static final int ZC_LUT_BITS = 8;

  /** Zero Coding context lookup tables for the LH global orientation */
  private static final int ZC_LUT_LH[] = new int[1 << ZC_LUT_BITS];

  /** Zero Coding context lookup tables for the HL global orientation */
  private static final int ZC_LUT_HL[] = new int[1 << ZC_LUT_BITS];

  /** Zero Coding context lookup tables for the HH global orientation */
  private static final int ZC_LUT_HH[] = new int[1 << ZC_LUT_BITS];

  /** Number of bits used for the Sign Coding lookup table */
  private static final int SC_LUT_BITS = 9;

  /** Sign Coding context lookup table. The index into the table is a 9 bit
     * index, which correspond the the value in the 'state' array shifted by
     * 'SC_SHIFT'. Bits 8-5 are the signs of the horizontal-left,
     * horizontal-right, vertical-up and vertical-down neighbors,
     * respectively. Bit 4 is not used (0 or 1 makes no difference). Bits 3-0
     * are the significance of the horizontal-left, horizontal-right,
     * vertical-up and vertical-down neighbors, respectively. The least 4 bits
     * of the value in the lookup table define the context number and the sign
     * bit defines the "sign predictor". */
  private static final int SC_LUT[] = new int[1 << SC_LUT_BITS];

  /** The mask to obtain the context index from the 'SC_LUT' */
  private static final int SC_LUT_MASK = (1 << 4) - 1;

  /** The shift to obtain the sign predictor from the 'SC_LUT'. It must be
     * an unsigned shift. */
  private static final int SC_SPRED_SHIFT = 31;

  /** The sign bit for int data */
  private static final int INT_SIGN_BIT = 1 << 31;

  /** The number of bits used for the Magnitude Refinement lookup table */
  private static final int MR_LUT_BITS = 9;

  /** Magnitude Refinement context lookup table */
  private static final int MR_LUT[] = new int[1 << MR_LUT_BITS];

  /** The number of contexts used */
  private static final int NUM_CTXTS = 19;

  /** The RLC context */
  private static final int RLC_CTXT = 1;

  /** The UNIFORM context (with a uniform probability distribution which
     * does not adapt) */
  private static final int UNIF_CTXT = 0;

  /** The initial states for the MQ coder */
  private static final int MQ_INIT[] = { 46, 3, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

  /** The 4 symbol segmentation marker (decimal 10, which is binary sequence
        1010) */
  private static final int SEG_SYMBOL = 10;

  /**
     * The state array for entropy coding. Each element of the state array
     * stores the state of two coefficients. The lower 16 bits store the state
     * of a coefficient in row 'i' and column 'j', while the upper 16 bits
     * store the state of a coefficient in row 'i+1' and column 'j'. The 'i'
     * row is either the first or the third row of a stripe. This packing of
     * the states into 32 bit words allows a faster scan of all coefficients
     * on each coding pass and diminished the amount of data transferred. The
     * size of the state array is increased by 1 on each side (top, bottom,
     * left, right) to handle boundary conditions without any special logic.
     *
     * <P>The state of a coefficient is stored in the following way in the
     * lower 16 bits, where bit 0 is the least significant bit. Bit 15 is the
     * significance of a coefficient (0 if non-significant, 1 otherwise). Bit
     * 14 is the visited state (i.e. if a coefficient has been coded in the
     * significance propagation pass of the current bit-plane). Bit 13 is the
     * "non zero-context" state (i.e. if one of the eight immediate neighbors
     * is significant it is 1, otherwise is 0). Bits 12 to 9 store the sign of
     * the already significant left, right, up and down neighbors (1 for
     * negative, 0 for positive or not yet significant). Bit 8 indicates if
     * the magnitude refinement has already been applied to the
     * coefficient. Bits 7 to 4 store the significance of the left, right, up
     * and down neighbors (1 for significant, 0 for non significant). Bits 3
     * to 0 store the significance of the diagonal coefficients (up-left,
     * up-right, down-left and down-right; 1 for significant, 0 for non
     * significant).
     *
     * <P>The upper 16 bits the state is stored as in the lower 16 bits,
     * but with the bits shifted up by 16.
     *
     * <P>The lower 16 bits are referred to as "row 1" ("R1") while the upper
     * 16 bits are referred to as "row 2" ("R2").
     * */
  private final int state[];

  /** The separation between the upper and lower bits in the state array: 16
     * */
  private static final int STATE_SEP = 16;

  /** The flag bit for the significance in the state array, for row 1. */
  private static final int STATE_SIG_R1 = 1 << 15;

  /** The flag bit for the "visited" bit in the state array, for row 1. */
  private static final int STATE_VISITED_R1 = 1 << 14;

  /** The flag bit for the "not zero context" bit in the state array, for
     * row 1. This bit is always the OR of bits STATE_H_L_R1, STATE_H_R_R1,
     * STATE_V_U_R1, STATE_V_D_R1, STATE_D_UL_R1, STATE_D_UR_R1, STATE_D_DL_R1
     * and STATE_D_DR_R1. */
  private static final int STATE_NZ_CTXT_R1 = 1 << 13;

  /** The flag bit for the horizontal-left sign in the state array, for row
     * 1. This bit can only be set if the STATE_H_L_R1 is also set. */
  private static final int STATE_H_L_SIGN_R1 = 1 << 12;

  /** The flag bit for the horizontal-right sign in the state array, for
     * row 1. This bit can only be set if the STATE_H_R_R1 is also set. */
  private static final int STATE_H_R_SIGN_R1 = 1 << 11;

  /** The flag bit for the vertical-up sign in the state array, for row
     * 1. This bit can only be set if the STATE_V_U_R1 is also set. */
  private static final int STATE_V_U_SIGN_R1 = 1 << 10;

  /** The flag bit for the vertical-down sign in the state array, for row
     * 1. This bit can only be set if the STATE_V_D_R1 is also set. */
  private static final int STATE_V_D_SIGN_R1 = 1 << 9;

  /** The flag bit for the previous MR primitive applied in the state array,
        for row 1. */
  private static final int STATE_PREV_MR_R1 = 1 << 8;

  /** The flag bit for the horizontal-left significance in the state array,
        for row 1. */
  private static final int STATE_H_L_R1 = 1 << 7;

  /** The flag bit for the horizontal-right significance in the state array,
        for row 1. */
  private static final int STATE_H_R_R1 = 1 << 6;

  /** The flag bit for the vertical-up significance in the state array, for
     row 1.  */
  private static final int STATE_V_U_R1 = 1 << 5;

  /** The flag bit for the vertical-down significance in the state array,
     for row 1.  */
  private static final int STATE_V_D_R1 = 1 << 4;

  /** The flag bit for the diagonal up-left significance in the state array,
        for row 1. */
  private static final int STATE_D_UL_R1 = 1 << 3;

  /** The flag bit for the diagonal up-right significance in the state
        array, for row 1.*/
  private static final int STATE_D_UR_R1 = 1 << 2;

  /** The flag bit for the diagonal down-left significance in the state
        array, for row 1. */
  private static final int STATE_D_DL_R1 = 1 << 1;

  /** The flag bit for the diagonal down-right significance in the state
        array , for row 1.*/
  private static final int STATE_D_DR_R1 = 1;

  /** The flag bit for the significance in the state array, for row 2. */
  private static final int STATE_SIG_R2 = STATE_SIG_R1 << STATE_SEP;

  /** The flag bit for the "visited" bit in the state array, for row 2. */
  private static final int STATE_VISITED_R2 = STATE_VISITED_R1 << STATE_SEP;

  /** The flag bit for the "not zero context" bit in the state array, for
     * row 2. This bit is always the OR of bits STATE_H_L_R2, STATE_H_R_R2,
     * STATE_V_U_R2, STATE_V_D_R2, STATE_D_UL_R2, STATE_D_UR_R2, STATE_D_DL_R2
     * and STATE_D_DR_R2. */
  private static final int STATE_NZ_CTXT_R2 = STATE_NZ_CTXT_R1 << STATE_SEP;

  /** The flag bit for the horizontal-left sign in the state array, for row
     * 2. This bit can only be set if the STATE_H_L_R2 is also set. */
  private static final int STATE_H_L_SIGN_R2 = STATE_H_L_SIGN_R1 << STATE_SEP;

  /** The flag bit for the horizontal-right sign in the state array, for
     * row 2. This bit can only be set if the STATE_H_R_R2 is also set. */
  private static final int STATE_H_R_SIGN_R2 = STATE_H_R_SIGN_R1 << STATE_SEP;

  /** The flag bit for the vertical-up sign in the state array, for row
     * 2. This bit can only be set if the STATE_V_U_R2 is also set. */
  private static final int STATE_V_U_SIGN_R2 = STATE_V_U_SIGN_R1 << STATE_SEP;

  /** The flag bit for the vertical-down sign in the state array, for row
     * 2. This bit can only be set if the STATE_V_D_R2 is also set. */
  private static final int STATE_V_D_SIGN_R2 = STATE_V_D_SIGN_R1 << STATE_SEP;

  /** The flag bit for the previous MR primitive applied in the state array,
        for row 2. */
  private static final int STATE_PREV_MR_R2 = STATE_PREV_MR_R1 << STATE_SEP;

  /** The flag bit for the horizontal-left significance in the state array,
        for row 2. */
  private static final int STATE_H_L_R2 = STATE_H_L_R1 << STATE_SEP;

  /** The flag bit for the horizontal-right significance in the state array,
        for row 2. */
  private static final int STATE_H_R_R2 = STATE_H_R_R1 << STATE_SEP;

  /** The flag bit for the vertical-up significance in the state array, for
     row 2.  */
  private static final int STATE_V_U_R2 = STATE_V_U_R1 << STATE_SEP;

  /** The flag bit for the vertical-down significance in the state array,
     for row 2.  */
  private static final int STATE_V_D_R2 = STATE_V_D_R1 << STATE_SEP;

  /** The flag bit for the diagonal up-left significance in the state array,
        for row 2. */
  private static final int STATE_D_UL_R2 = STATE_D_UL_R1 << STATE_SEP;

  /** The flag bit for the diagonal up-right significance in the state
        array, for row 2.*/
  private static final int STATE_D_UR_R2 = STATE_D_UR_R1 << STATE_SEP;

  /** The flag bit for the diagonal down-left significance in the state
        array, for row 2. */
  private static final int STATE_D_DL_R2 = STATE_D_DL_R1 << STATE_SEP;

  /** The flag bit for the diagonal down-right significance in the state
        array , for row 2.*/
  private static final int STATE_D_DR_R2 = STATE_D_DR_R1 << STATE_SEP;

  /** The mask to isolate the significance bits for row 1 and 2 of the state
     * array. */
  private static final int SIG_MASK_R1R2 = STATE_SIG_R1 | STATE_SIG_R2;

  /** The mask to isolate the visited bits for row 1 and 2 of the state
     * array. */
  private static final int VSTD_MASK_R1R2 = STATE_VISITED_R1 | STATE_VISITED_R2;

  /** The mask to isolate the bits necessary to identify RLC coding state
     * (significant, visited and non-zero context, for row 1 and 2). */
  private static final int RLC_MASK_R1R2 = STATE_SIG_R1 | STATE_SIG_R2 | STATE_VISITED_R1 | STATE_VISITED_R2 | STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2;

  /** The mask to obtain the ZC_LUT index from the 'state' information */
  private static final int ZC_MASK = (1 << 8) - 1;

  /** The shift to obtain the SC index to 'SC_LUT' from the 'state'
     * information, for row 1. */
  private static final int SC_SHIFT_R1 = 4;

  /** The shift to obtain the SC index to 'SC_LUT' from the state
     * information, for row 2. */
  private static final int SC_SHIFT_R2 = SC_SHIFT_R1 + STATE_SEP;

  /** The bit mask to isolate the state bits relative to the sign coding
     * lookup table ('SC_LUT'). */
  private static final int SC_MASK = (1 << SC_LUT_BITS) - 1;

  /** The mask to obtain the MR index to 'MR_LUT' from the 'state'
     * information. It is to be applied after the 'MR_SHIFT' */
  private static final int MR_MASK = (1 << 9) - 1;

  /** The source code-block to entropy code (avoids reallocation for each
        code-block). */
  private DecLyrdCBlk srcblk;

  /** The maximum number of bit planes to decode for any code-block */
  private int mQuit;

  static {
    int i, j;
    double val, deltaMSE;
    int inter_sc_lut[];
    int ds, us, rs, ls;
    int dsgn, usgn, rsgn, lsgn;
    int h, v;
    ZC_LUT_LH[0] = 2;
    for (i = 1; i < 16; i++) {
      ZC_LUT_LH[i] = 4;
    }
    for (i = 0; i < 4; i++) {
      ZC_LUT_LH[1 << i] = 3;
    }
    for (i = 0; i < 16; i++) {
      ZC_LUT_LH[STATE_V_U_R1 | i] = 5;
      ZC_LUT_LH[STATE_V_D_R1 | i] = 5;
      ZC_LUT_LH[STATE_V_U_R1 | STATE_V_D_R1 | i] = 6;
    }
    ZC_LUT_LH[STATE_H_L_R1] = 7;
    ZC_LUT_LH[STATE_H_R_R1] = 7;
    for (i = 1; i < 16; i++) {
      ZC_LUT_LH[STATE_H_L_R1 | i] = 8;
      ZC_LUT_LH[STATE_H_R_R1 | i] = 8;
    }
    for (i = 1; i < 4; i++) {
      for (j = 0; j < 16; j++) {
        ZC_LUT_LH[STATE_H_L_R1 | (i << 4) | j] = 9;
        ZC_LUT_LH[STATE_H_R_R1 | (i << 4) | j] = 9;
      }
    }
    for (i = 0; i < 64; i++) {
      ZC_LUT_LH[STATE_H_L_R1 | STATE_H_R_R1 | i] = 10;
    }
    ZC_LUT_HL[0] = 2;
    for (i = 1; i < 16; i++) {
      ZC_LUT_HL[i] = 4;
    }
    for (i = 0; i < 4; i++) {
      ZC_LUT_HL[1 << i] = 3;
    }
    for (i = 0; i < 16; i++) {
      ZC_LUT_HL[STATE_H_L_R1 | i] = 5;
      ZC_LUT_HL[STATE_H_R_R1 | i] = 5;
      ZC_LUT_HL[STATE_H_L_R1 | STATE_H_R_R1 | i] = 6;
    }
    ZC_LUT_HL[STATE_V_U_R1] = 7;
    ZC_LUT_HL[STATE_V_D_R1] = 7;
    for (i = 1; i < 16; i++) {
      ZC_LUT_HL[STATE_V_U_R1 | i] = 8;
      ZC_LUT_HL[STATE_V_D_R1 | i] = 8;
    }
    for (i = 1; i < 4; i++) {
      for (j = 0; j < 16; j++) {
        ZC_LUT_HL[(i << 6) | STATE_V_U_R1 | j] = 9;
        ZC_LUT_HL[(i << 6) | STATE_V_D_R1 | j] = 9;
      }
    }
    for (i = 0; i < 4; i++) {
      for (j = 0; j < 16; j++) {
        ZC_LUT_HL[(i << 6) | STATE_V_U_R1 | STATE_V_D_R1 | j] = 10;
      }
    }
    int[] twoBits = { 3, 5, 6, 9, 10, 12 };
    int[] oneBit = { 1, 2, 4, 8 };
    int[] twoLeast = { 3, 5, 6, 7, 9, 10, 11, 12, 13, 14, 15 };
    int[] threeLeast = { 7, 11, 13, 14, 15 };
    ZC_LUT_HH[0] = 2;
    for (i = 0; i < oneBit.length; i++) {
      ZC_LUT_HH[oneBit[i] << 4] = 3;
    }
    for (i = 0; i < twoLeast.length; i++) {
      ZC_LUT_HH[twoLeast[i] << 4] = 4;
    }
    for (i = 0; i < oneBit.length; i++) {
      ZC_LUT_HH[oneBit[i]] = 5;
    }
    for (i = 0; i < oneBit.length; i++) {
      for (j = 0; j < oneBit.length; j++) {
        ZC_LUT_HH[(oneBit[i] << 4) | oneBit[j]] = 6;
      }
    }
    for (i = 0; i < twoLeast.length; i++) {
      for (j = 0; j < oneBit.length; j++) {
        ZC_LUT_HH[(twoLeast[i] << 4) | oneBit[j]] = 7;
      }
    }
    for (i = 0; i < twoBits.length; i++) {
      ZC_LUT_HH[twoBits[i]] = 8;
    }
    for (j = 0; j < twoBits.length; j++) {
      for (i = 1; i < 16; i++) {
        ZC_LUT_HH[(i << 4) | twoBits[j]] = 9;
      }
    }
    for (i = 0; i < 16; i++) {
      for (j = 0; j < threeLeast.length; j++) {
        ZC_LUT_HH[(i << 4) | threeLeast[j]] = 10;
      }
    }
    inter_sc_lut = new int[36];
    inter_sc_lut[(2 << 3) | 2] = 15;
    inter_sc_lut[(2 << 3) | 1] = 14;
    inter_sc_lut[(2 << 3) | 0] = 13;
    inter_sc_lut[(1 << 3) | 2] = 12;
    inter_sc_lut[(1 << 3) | 1] = 11;
    inter_sc_lut[(1 << 3) | 0] = 12 | INT_SIGN_BIT;
    inter_sc_lut[(0 << 3) | 2] = 13 | INT_SIGN_BIT;
    inter_sc_lut[(0 << 3) | 1] = 14 | INT_SIGN_BIT;
    inter_sc_lut[(0 << 3) | 0] = 15 | INT_SIGN_BIT;
    for (i = 0; i < (1 << SC_LUT_BITS) - 1; i++) {
      ds = i & 0x01;
      us = (i >> 1) & 0x01;
      rs = (i >> 2) & 0x01;
      ls = (i >> 3) & 0x01;
      dsgn = (i >> 5) & 0x01;
      usgn = (i >> 6) & 0x01;
      rsgn = (i >> 7) & 0x01;
      lsgn = (i >> 8) & 0x01;
      h = ls * (1 - 2 * lsgn) + rs * (1 - 2 * rsgn);
      h = (h >= -1) ? h : -1;
      h = (h <= 1) ? h : 1;
      v = us * (1 - 2 * usgn) + ds * (1 - 2 * dsgn);
      v = (v >= -1) ? v : -1;
      v = (v <= 1) ? v : 1;
      SC_LUT[i] = inter_sc_lut[(h + 1) << 3 | (v + 1)];
    }
    inter_sc_lut = null;
    MR_LUT[0] = 16;
    for (i = 1; i < (1 << (MR_LUT_BITS - 1)); i++) {
      MR_LUT[i] = 17;
    }
    for ( ; i < (1 << MR_LUT_BITS); i++) {
      MR_LUT[i] = 18;
    }
  }

  /**
     * Instantiates a new entropy decoder engine, with the specified source of
     * data, nominal block width and height.
     *
     * @param src The source of data
     *
     * @param opt The options to use for this encoder. It is a mix of the
     * 'OPT_TERM_PASS', 'OPT_RESET_MQ', 'OPT_VERT_STR_CAUSAL', 'OPT_BYPASS' and
     * 'OPT_SEG_SYMBOLS' option flags.
     *
     * @param doer If true error detection will be performed, if any error
     * detection features have been enabled.
     *
     * @param verber This flag indicates if the entropy decoder should be
     * verbose about bit stream errors that are detected and concealed.
     * */
  public StdEntropyDecoder(CodedCBlkDataSrcDec src, DecoderSpecs decSpec, boolean doer, boolean verber, int mQuit) {
    super(src);
    this.decSpec = decSpec;
    this.doer = doer;
    this.verber = verber;
    this.mQuit = mQuit;
    if (DO_TIMING) {
      time = new long[src.getNumComps()];
      System.runFinalizersOnExit(true);
    }
    state = new int[(decSpec.cblks.getMaxCBlkWidth() + 2) * ((decSpec.cblks.getMaxCBlkHeight() + 1) / 2 + 2)];
  }

  /**
     * Prints the timing information, if collected, and calls 'finalize' on
     * the super class.
     * */
  public void finalize() throws Throwable {
    if (DO_TIMING) {
      int c;
      StringBuffer sb;
      sb = new StringBuffer("StdEntropyDecoder decompression wall " + "clock time:");
      for (c = 0; c < time.length; c++) {
        sb.append("\n  component ");
        sb.append(c);
        sb.append(": ");
        sb.append(time[c]);
        sb.append(" ms");
      }
      FacilityManager.getMsgLogger().printmsg(MsgLogger.INFO, sb.toString());
    }
    super.finalize();
  }

  /**
     * Returns the specified code-block in the current tile for the specified
     * component, as a copy (see below).
     *
     * <P>The returned code-block may be progressive, which is indicated by
     * the 'progressive' variable of the returned 'DataBlk' object. If a
     * code-block is progressive it means that in a later request to this
     * method for the same code-block it is possible to retrieve data which is
     * a better approximation, since meanwhile more data to decode for the
     * code-block could have been received. If the code-block is not
     * progressive then later calls to this method for the same code-block
     * will return the exact same data values.
     *
     * <P>The data returned by this method is always a copy of the internal
     * data of this object, if any, and it can be modified "in place" without
     * any problems after being returned. The 'offset' of the returned data is
     * 0, and the 'scanw' is the same as the code-block width. See the
     * 'DataBlk' class.
     *
     * <P>The 'ulx' and 'uly' members of the returned 'DataBlk' object
     * contain the coordinates of the top-left corner of the block, with
     * respect to the tile, not the subband.
     *
     * @param c The component for which to return the next code-block.
     *
     * @param m The vertical index of the code-block to return, in the
     * specified subband.
     *
     * @param n The horizontal index of the code-block to return, in the
     * specified subband.
     *
     * @param sb The subband in which the code-block to return is.
     *
     * @param cblk If non-null this object will be used to return the new
     * code-block. If null a new one will be allocated and returned. If the
     * "data" array of the object is non-null it will be reused, if possible,
     * to return the data.
     *
     * @return The next code-block in the current tile for component 'n', or
     * null if all code-blocks for the current tile have been returned.
     *
     * @see DataBlk
     * */
  public DataBlk getCodeBlock(int c, int m, int n, SubbandSyn sb, DataBlk cblk) {
    long stime = 0L;
    int zc_lut[];
    int out_data[];
    int npasses;
    int curbp;
    boolean error;
    int tslen;
    int tsidx;
    ByteInputBuffer in = null;
    boolean isterm;
    srcblk = src.getCodeBlock(c, m, n, sb, 1, -1, srcblk);
    if (DO_TIMING) {
      stime = System.currentTimeMillis();
    }
    options = ((Integer) decSpec.ecopts.getTileCompVal(tIdx, c)).intValue();
    ArrayUtil.intArraySet(state, 0);
    if (cblk == null) {
      cblk = new DataBlkInt();
    }
    cblk.progressive = srcblk.prog;
    cblk.ulx = srcblk.ulx;
    cblk.uly = srcblk.uly;
    cblk.w = srcblk.w;
    cblk.h = srcblk.h;
    cblk.offset = 0;
    cblk.scanw = cblk.w;
    out_data = (int[]) cblk.getData();
    if (out_data == null || out_data.length < srcblk.w * srcblk.h) {
      out_data = new int[srcblk.w * srcblk.h];
      cblk.setData(out_data);
    } else {
      ArrayUtil.intArraySet(out_data, 0);
    }
    if (srcblk.nl <= 0 || srcblk.nTrunc <= 0) {
      return cblk;
    }
    tslen = (srcblk.tsLengths == null) ? srcblk.dl : srcblk.tsLengths[0];
    tsidx = 0;
    npasses = srcblk.nTrunc;
    if (mq == null) {
      in = new ByteInputBuffer(srcblk.data, 0, tslen);
      mq = new MQDecoder(in, NUM_CTXTS, MQ_INIT);
    } else {
      mq.nextSegment(srcblk.data, 0, tslen);
      mq.resetCtxts();
    }
    error = false;
    if ((options & OPT_BYPASS) != 0) {
      if (bin == null) {
        if (in == null) {
          in = mq.getByteInputBuffer();
        }
        bin = new ByteToBitInput(in);
      }
    }
    switch (sb.orientation) {
      case Subband.WT_ORIENT_HL:
      zc_lut = ZC_LUT_HL;
      break;
      case Subband.WT_ORIENT_LH:
      case Subband.WT_ORIENT_LL:
      zc_lut = ZC_LUT_LH;
      break;
      case Subband.WT_ORIENT_HH:
      zc_lut = ZC_LUT_HH;
      break;
      default:
      throw new Error("JJ2000 internal error");
    }
    curbp = 30 - srcblk.skipMSBP;
    if (mQuit != -1 && (mQuit * 3 - 2) < npasses) {
      npasses = mQuit * 3 - 2;
    }
    if (curbp >= 0 && npasses > 0) {
      isterm = (options & OPT_TERM_PASS) != 0 || ((options & OPT_BYPASS) != 0 && (31 - NUM_NON_BYPASS_MS_BP - srcblk.skipMSBP) >= curbp);
      error = cleanuppass(cblk, mq, curbp, state, zc_lut, isterm);
      npasses--;
      if (!error || !doer) {
        curbp--;
      }
    }
    if (!error || !doer) {
      while (curbp >= 0 && npasses > 0) {
        if ((options & OPT_BYPASS) != 0 && (curbp < 31 - NUM_NON_BYPASS_MS_BP - srcblk.skipMSBP)) {
          bin.setByteArray(null, -1, srcblk.tsLengths[++tsidx]);
          isterm = (options & OPT_TERM_PASS) != 0;
          error = rawSigProgPass(cblk, bin, curbp, state, isterm);
          npasses--;
          if (npasses <= 0 || (error && doer)) {
            break;
          }
          if ((options & OPT_TERM_PASS) != 0) {
            bin.setByteArray(null, -1, srcblk.tsLengths[++tsidx]);
          }
          isterm = (options & OPT_TERM_PASS) != 0 || ((options & OPT_BYPASS) != 0 && (31 - NUM_NON_BYPASS_MS_BP - srcblk.skipMSBP > curbp));
          error = rawMagRefPass(cblk, bin, curbp, state, isterm);
        } else {
          if ((options & OPT_TERM_PASS) != 0) {
            mq.nextSegment(null, -1, srcblk.tsLengths[++tsidx]);
          }
          isterm = (options & OPT_TERM_PASS) != 0;
          error = sigProgPass(cblk, mq, curbp, state, zc_lut, isterm);
          npasses--;
          if (npasses <= 0 || (error && doer)) {
            break;
          }
          if ((options & OPT_TERM_PASS) != 0) {
            mq.nextSegment(null, -1, srcblk.tsLengths[++tsidx]);
          }
          isterm = (options & OPT_TERM_PASS) != 0 || ((options & OPT_BYPASS) != 0 && (31 - NUM_NON_BYPASS_MS_BP - srcblk.skipMSBP > curbp));
          error = magRefPass(cblk, mq, curbp, state, isterm);
        }
        npasses--;
        if (npasses <= 0 || (error && doer)) {
          break;
        }
        if ((options & OPT_TERM_PASS) != 0 || ((options & OPT_BYPASS) != 0 && (curbp < 31 - NUM_NON_BYPASS_MS_BP - srcblk.skipMSBP))) {
          mq.nextSegment(null, -1, srcblk.tsLengths[++tsidx]);
        }
        isterm = (options & OPT_TERM_PASS) != 0 || ((options & OPT_BYPASS) != 0 && (31 - NUM_NON_BYPASS_MS_BP - srcblk.skipMSBP) >= curbp);
        error = cleanuppass(cblk, mq, curbp, state, zc_lut, isterm);
        npasses--;
        if (error) {
          break;
        }
        curbp--;
      }
    }
    if (error && doer) {
      if (verber) {
        FacilityManager.getMsgLogger().printmsg(MsgLogger.WARNING, "Error detected at bit-plane " + curbp + " in code-block (" + m + "," + n + "), sb_idx " + sb.sbandIdx + ", res. level " + sb.resLvl + ". Concealing...");
      }
      conceal(cblk, curbp);
    }
    if (DO_TIMING) {
      time[c] += System.currentTimeMillis() - stime;
    }
    return cblk;
  }

  /**
     * Returns the specified code-block in the current tile for the specified
     * component (as a reference or copy).
     *
     * <P>The returned code-block may be progressive, which is indicated by
     * the 'progressive' variable of the returned 'DataBlk'
     * object. If a code-block is progressive it means that in a later request
     * to this method for the same code-block it is possible to retrieve data
     * which is a better approximation, since meanwhile more data to decode
     * for the code-block could have been received. If the code-block is not
     * progressive then later calls to this method for the same code-block
     * will return the exact same data values.
     *
     * <P>The data returned by this method can be the data in the internal
     * buffer of this object, if any, and thus can not be modified by the
     * caller. The 'offset' and 'scanw' of the returned data can be
     * arbitrary. See the 'DataBlk' class.
     *
     * <P>The 'ulx' and 'uly' members of the returned 'DataBlk' object
     * contain the coordinates of the top-left corner of the block, with
     * respect to the tile, not the subband.
     *
     * @param c The component for which to return the next code-block.
     *
     * @param m The vertical index of the code-block to return, in the
     * specified subband.
     *
     * @param n The horizontal index of the code-block to return, in the
     * specified subband.
     *
     * @param sb The subband in which the code-block to return is.
     *
     * @param cblk If non-null this object will be used to return the new
     * code-block. If null a new one will be allocated and returned. If the
     * "data" array of the object is non-null it will be reused, if possible,
     * to return the data.
     *
     * @return The next code-block in the current tile for component 'n', or
     * null if all code-blocks for the current tile have been returned.
     *
     * @see DataBlk
     * */
  public DataBlk getInternCodeBlock(int c, int m, int n, SubbandSyn sb, DataBlk cblk) {
    return getCodeBlock(c, m, n, sb, cblk);
  }

  /**
     * Performs the significance propagation pass on the specified data and
     * bit-plane. It decodes all insignificant samples which have, at least,
     * one of its immediate eight neighbors already significant, using the ZC
     * and SC primitives as needed. It toggles the "visited" state bit to 1
     * for all those samples.
     *
     * <P>This method also checks for segmentation markers if those are
     * present and returns true if an error is detected, or false
     * otherwise. If an error is detected it means that the bit stream contains
     * some erroneous bit that have led to the decoding of incorrect
     * data. This data affects the whole last decoded bit-plane (i.e. 'bp'). If
     * 'true' is returned the 'conceal' method should be called and no more
     * passes should be decoded for this code-block's bit stream.
     *
     * @param cblk The code-block data to decode
     *
     * @param mq The MQ-coder to use
     *
     * @param bp The bit-plane to decode
     *
     * @param state The state information for the code-block
     *
     * @param zc_lut The ZC lookup table to use in ZC.
     *
     * @param isterm If this pass has been terminated. If the pass has been
     * terminated it can be used to check error resilience.
     *
     * @return True if an error was detected in the bit stream, false otherwise.
     * */
  private boolean sigProgPass(DataBlk cblk, MQDecoder mq, int bp, int[] state, int[] zc_lut, boolean isterm) {
    int j, sj;
    int k, sk;
    int dscanw;
    int sscanw;
    int jstep;
    int kstep;
    int stopsk;
    int csj;
    int setmask;
    int sym;
    int ctxt;
    int data[];
    int s;
    boolean causal;
    int nstripes;
    int sheight;
    int off_ul, off_ur, off_dr, off_dl;
    boolean error;
    dscanw = cblk.scanw;
    sscanw = cblk.w + 2;
    jstep = sscanw * STRIPE_HEIGHT / 2 - cblk.w;
    kstep = dscanw * STRIPE_HEIGHT - cblk.w;
    int one = 1 << bp;
    int half = one >> 1;
    setmask = one | half;
    data = (int[]) cblk.getData();
    nstripes = (cblk.h + STRIPE_HEIGHT - 1) / STRIPE_HEIGHT;
    causal = (options & OPT_VERT_STR_CAUSAL) != 0;
    off_ul = -sscanw - 1;
    off_ur = -sscanw + 1;
    off_dr = sscanw + 1;
    off_dl = sscanw - 1;
    sk = cblk.offset;
    sj = sscanw + 1;
    for (s = nstripes - 1; s >= 0; s--, sk += kstep, sj += jstep) {
      sheight = (s != 0) ? STRIPE_HEIGHT : cblk.h - (nstripes - 1) * STRIPE_HEIGHT;
      stopsk = sk + cblk.w;
      for ( ; sk < stopsk; sk++, sj++) {
        j = sj;
        csj = state[j];
        if ((((~csj) & (csj << 2)) & SIG_MASK_R1R2) != 0) {
          k = sk;
          if ((csj & (STATE_SIG_R1 | STATE_NZ_CTXT_R1)) == STATE_NZ_CTXT_R1) {
            if (mq.decodeSymbol(zc_lut[csj & ZC_MASK]) != 0) {
              ctxt = SC_LUT[(csj >>> SC_SHIFT_R1) & SC_MASK];
              sym = mq.decodeSymbol(ctxt & SC_LUT_MASK) ^ (ctxt >>> SC_SPRED_SHIFT);
              data[k] = (sym << 31) | setmask;
              if (!causal) {
                state[j + off_ul] |= STATE_NZ_CTXT_R2 | STATE_D_DR_R2;
                state[j + off_ur] |= STATE_NZ_CTXT_R2 | STATE_D_DL_R2;
              }
              if (sym != 0) {
                csj |= STATE_SIG_R1 | STATE_VISITED_R1 | STATE_NZ_CTXT_R2 | STATE_V_U_R2 | STATE_V_U_SIGN_R2;
                if (!causal) {
                  state[j - sscanw] |= STATE_NZ_CTXT_R2 | STATE_V_D_R2 | STATE_V_D_SIGN_R2;
                }
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_L_R1 | STATE_H_L_SIGN_R1 | STATE_D_UL_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_R_R1 | STATE_H_R_SIGN_R1 | STATE_D_UR_R2;
              } else {
                csj |= STATE_SIG_R1 | STATE_VISITED_R1 | STATE_NZ_CTXT_R2 | STATE_V_U_R2;
                if (!causal) {
                  state[j - sscanw] |= STATE_NZ_CTXT_R2 | STATE_V_D_R2;
                }
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_L_R1 | STATE_D_UL_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_R_R1 | STATE_D_UR_R2;
              }
            } else {
              csj |= STATE_VISITED_R1;
            }
          }
          if (sheight < 2) {
            state[j] = csj;
            continue;
          }
          if ((csj & (STATE_SIG_R2 | STATE_NZ_CTXT_R2)) == STATE_NZ_CTXT_R2) {
            k += dscanw;
            if (mq.decodeSymbol(zc_lut[(csj >>> STATE_SEP) & ZC_MASK]) != 0) {
              ctxt = SC_LUT[(csj >>> SC_SHIFT_R2) & SC_MASK];
              sym = mq.decodeSymbol(ctxt & SC_LUT_MASK) ^ (ctxt >>> SC_SPRED_SHIFT);
              data[k] = (sym << 31) | setmask;
              state[j + off_dl] |= STATE_NZ_CTXT_R1 | STATE_D_UR_R1;
              state[j + off_dr] |= STATE_NZ_CTXT_R1 | STATE_D_UL_R1;
              if (sym != 0) {
                csj |= STATE_SIG_R2 | STATE_VISITED_R2 | STATE_NZ_CTXT_R1 | STATE_V_D_R1 | STATE_V_D_SIGN_R1;
                state[j + sscanw] |= STATE_NZ_CTXT_R1 | STATE_V_U_R1 | STATE_V_U_SIGN_R1;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DL_R1 | STATE_H_L_R2 | STATE_H_L_SIGN_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DR_R1 | STATE_H_R_R2 | STATE_H_R_SIGN_R2;
              } else {
                csj |= STATE_SIG_R2 | STATE_VISITED_R2 | STATE_NZ_CTXT_R1 | STATE_V_D_R1;
                state[j + sscanw] |= STATE_NZ_CTXT_R1 | STATE_V_U_R1;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DL_R1 | STATE_H_L_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DR_R1 | STATE_H_R_R2;
              }
            } else {
              csj |= STATE_VISITED_R2;
            }
          }
          state[j] = csj;
        }
        if (sheight < 3) {
          continue;
        }
        j += sscanw;
        csj = state[j];
        if ((((~csj) & (csj << 2)) & SIG_MASK_R1R2) != 0) {
          k = sk + (dscanw << 1);
          if ((csj & (STATE_SIG_R1 | STATE_NZ_CTXT_R1)) == STATE_NZ_CTXT_R1) {
            if (mq.decodeSymbol(zc_lut[csj & ZC_MASK]) != 0) {
              ctxt = SC_LUT[(csj >>> SC_SHIFT_R1) & SC_MASK];
              sym = mq.decodeSymbol(ctxt & SC_LUT_MASK) ^ (ctxt >>> SC_SPRED_SHIFT);
              data[k] = (sym << 31) | setmask;
              state[j + off_ul] |= STATE_NZ_CTXT_R2 | STATE_D_DR_R2;
              state[j + off_ur] |= STATE_NZ_CTXT_R2 | STATE_D_DL_R2;
              if (sym != 0) {
                csj |= STATE_SIG_R1 | STATE_VISITED_R1 | STATE_NZ_CTXT_R2 | STATE_V_U_R2 | STATE_V_U_SIGN_R2;
                state[j - sscanw] |= STATE_NZ_CTXT_R2 | STATE_V_D_R2 | STATE_V_D_SIGN_R2;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_L_R1 | STATE_H_L_SIGN_R1 | STATE_D_UL_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_R_R1 | STATE_H_R_SIGN_R1 | STATE_D_UR_R2;
              } else {
                csj |= STATE_SIG_R1 | STATE_VISITED_R1 | STATE_NZ_CTXT_R2 | STATE_V_U_R2;
                state[j - sscanw] |= STATE_NZ_CTXT_R2 | STATE_V_D_R2;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_L_R1 | STATE_D_UL_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_R_R1 | STATE_D_UR_R2;
              }
            } else {
              csj |= STATE_VISITED_R1;
            }
          }
          if (sheight < 4) {
            state[j] = csj;
            continue;
          }
          if ((csj & (STATE_SIG_R2 | STATE_NZ_CTXT_R2)) == STATE_NZ_CTXT_R2) {
            k += dscanw;
            if (mq.decodeSymbol(zc_lut[(csj >>> STATE_SEP) & ZC_MASK]) != 0) {
              ctxt = SC_LUT[(csj >>> SC_SHIFT_R2) & SC_MASK];
              sym = mq.decodeSymbol(ctxt & SC_LUT_MASK) ^ (ctxt >>> SC_SPRED_SHIFT);
              data[k] = (sym << 31) | setmask;
              state[j + off_dl] |= STATE_NZ_CTXT_R1 | STATE_D_UR_R1;
              state[j + off_dr] |= STATE_NZ_CTXT_R1 | STATE_D_UL_R1;
              if (sym != 0) {
                csj |= STATE_SIG_R2 | STATE_VISITED_R2 | STATE_NZ_CTXT_R1 | STATE_V_D_R1 | STATE_V_D_SIGN_R1;
                state[j + sscanw] |= STATE_NZ_CTXT_R1 | STATE_V_U_R1 | STATE_V_U_SIGN_R1;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DL_R1 | STATE_H_L_R2 | STATE_H_L_SIGN_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DR_R1 | STATE_H_R_R2 | STATE_H_R_SIGN_R2;
              } else {
                csj |= STATE_SIG_R2 | STATE_VISITED_R2 | STATE_NZ_CTXT_R1 | STATE_V_D_R1;
                state[j + sscanw] |= STATE_NZ_CTXT_R1 | STATE_V_U_R1;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DL_R1 | STATE_H_L_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DR_R1 | STATE_H_R_R2;
              }
            } else {
              csj |= STATE_VISITED_R2;
            }
          }
          state[j] = csj;
        }
      }
    }
    error = false;
    if (isterm && (options & OPT_PRED_TERM) != 0) {
      error = mq.checkPredTerm();
    }
    if ((options & OPT_RESET_MQ) != 0) {
      mq.resetCtxts();
    }
    return error;
  }

  /**
     * Performs the significance propagation pass on the specified data and
     * bit-plane. It decodes all insignificant samples which have, at least, one
     * of its immediate eight neighbors already significant, using the ZC and
     * SC primitives as needed. It toggles the "visited" state bit to 1 for
     * all those samples.
     *
     * <P>This method bypasses the arithmetic coder and reads "raw" symbols
     * from the bit stream.
     *
     * <P>This method also checks for segmentation markers if those are
     * present and returns true if an error is detected, or false
     * otherwise. If an error is detected it measn that the bit stream contains
     * some erroneous bit that have led to the decoding of incorrect
     * data. This data affects the whole last decoded bit-plane (i.e. 'bp'). If
     * 'true' is returned the 'conceal' method should be called and no more
     * passes should be decoded for this code-block's bit stream.
     *
     * @param cblk The code-block data to decode
     *
     * @param bin The raw bit based input
     *
     * @param bp The bit-plane to decode
     *
     * @param state The state information for the code-block
     *
     * @param isterm If this pass has been terminated. If the pass has been
     * terminated it can be used to check error resilience.
     *
     * @return True if an error was detected in the bit stream, false otherwise.
     * */
  private boolean rawSigProgPass(DataBlk cblk, ByteToBitInput bin, int bp, int[] state, boolean isterm) {
    int j, sj;
    int k, sk;
    int dscanw;
    int sscanw;
    int jstep;
    int kstep;
    int stopsk;
    int csj;
    int setmask;
    int sym;
    int data[];
    int s;
    boolean causal;
    int nstripes;
    int sheight;
    int off_ul, off_ur, off_dr, off_dl;
    boolean error;
    dscanw = cblk.scanw;
    sscanw = cblk.w + 2;
    jstep = sscanw * STRIPE_HEIGHT / 2 - cblk.w;
    kstep = dscanw * STRIPE_HEIGHT - cblk.w;
    int one = 1 << bp;
    int half = one >> 1;
    setmask = one | half;
    data = (int[]) cblk.getData();
    nstripes = (cblk.h + STRIPE_HEIGHT - 1) / STRIPE_HEIGHT;
    causal = (options & OPT_VERT_STR_CAUSAL) != 0;
    off_ul = -sscanw - 1;
    off_ur = -sscanw + 1;
    off_dr = sscanw + 1;
    off_dl = sscanw - 1;
    sk = cblk.offset;
    sj = sscanw + 1;
    for (s = nstripes - 1; s >= 0; s--, sk += kstep, sj += jstep) {
      sheight = (s != 0) ? STRIPE_HEIGHT : cblk.h - (nstripes - 1) * STRIPE_HEIGHT;
      stopsk = sk + cblk.w;
      for ( ; sk < stopsk; sk++, sj++) {
        j = sj;
        csj = state[j];
        if ((((~csj) & (csj << 2)) & SIG_MASK_R1R2) != 0) {
          k = sk;
          if ((csj & (STATE_SIG_R1 | STATE_NZ_CTXT_R1)) == STATE_NZ_CTXT_R1) {
            if (bin.readBit() != 0) {
              sym = bin.readBit();
              data[k] = (sym << 31) | setmask;
              if (!causal) {
                state[j + off_ul] |= STATE_NZ_CTXT_R2 | STATE_D_DR_R2;
                state[j + off_ur] |= STATE_NZ_CTXT_R2 | STATE_D_DL_R2;
              }
              if (sym != 0) {
                csj |= STATE_SIG_R1 | STATE_VISITED_R1 | STATE_NZ_CTXT_R2 | STATE_V_U_R2 | STATE_V_U_SIGN_R2;
                if (!causal) {
                  state[j - sscanw] |= STATE_NZ_CTXT_R2 | STATE_V_D_R2 | STATE_V_D_SIGN_R2;
                }
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_L_R1 | STATE_H_L_SIGN_R1 | STATE_D_UL_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_R_R1 | STATE_H_R_SIGN_R1 | STATE_D_UR_R2;
              } else {
                csj |= STATE_SIG_R1 | STATE_VISITED_R1 | STATE_NZ_CTXT_R2 | STATE_V_U_R2;
                if (!causal) {
                  state[j - sscanw] |= STATE_NZ_CTXT_R2 | STATE_V_D_R2;
                }
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_L_R1 | STATE_D_UL_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_R_R1 | STATE_D_UR_R2;
              }
            } else {
              csj |= STATE_VISITED_R1;
            }
          }
          if (sheight < 2) {
            state[j] = csj;
            continue;
          }
          if ((csj & (STATE_SIG_R2 | STATE_NZ_CTXT_R2)) == STATE_NZ_CTXT_R2) {
            k += dscanw;
            if (bin.readBit() != 0) {
              sym = bin.readBit();
              data[k] = (sym << 31) | setmask;
              state[j + off_dl] |= STATE_NZ_CTXT_R1 | STATE_D_UR_R1;
              state[j + off_dr] |= STATE_NZ_CTXT_R1 | STATE_D_UL_R1;
              if (sym != 0) {
                csj |= STATE_SIG_R2 | STATE_VISITED_R2 | STATE_NZ_CTXT_R1 | STATE_V_D_R1 | STATE_V_D_SIGN_R1;
                state[j + sscanw] |= STATE_NZ_CTXT_R1 | STATE_V_U_R1 | STATE_V_U_SIGN_R1;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DL_R1 | STATE_H_L_R2 | STATE_H_L_SIGN_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DR_R1 | STATE_H_R_R2 | STATE_H_R_SIGN_R2;
              } else {
                csj |= STATE_SIG_R2 | STATE_VISITED_R2 | STATE_NZ_CTXT_R1 | STATE_V_D_R1;
                state[j + sscanw] |= STATE_NZ_CTXT_R1 | STATE_V_U_R1;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DL_R1 | STATE_H_L_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DR_R1 | STATE_H_R_R2;
              }
            } else {
              csj |= STATE_VISITED_R2;
            }
          }
          state[j] = csj;
        }
        if (sheight < 3) {
          continue;
        }
        j += sscanw;
        csj = state[j];
        if ((((~csj) & (csj << 2)) & SIG_MASK_R1R2) != 0) {
          k = sk + (dscanw << 1);
          if ((csj & (STATE_SIG_R1 | STATE_NZ_CTXT_R1)) == STATE_NZ_CTXT_R1) {
            if (bin.readBit() != 0) {
              sym = bin.readBit();
              data[k] = (sym << 31) | setmask;
              state[j + off_ul] |= STATE_NZ_CTXT_R2 | STATE_D_DR_R2;
              state[j + off_ur] |= STATE_NZ_CTXT_R2 | STATE_D_DL_R2;
              if (sym != 0) {
                csj |= STATE_SIG_R1 | STATE_VISITED_R1 | STATE_NZ_CTXT_R2 | STATE_V_U_R2 | STATE_V_U_SIGN_R2;
                state[j - sscanw] |= STATE_NZ_CTXT_R2 | STATE_V_D_R2 | STATE_V_D_SIGN_R2;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_L_R1 | STATE_H_L_SIGN_R1 | STATE_D_UL_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_R_R1 | STATE_H_R_SIGN_R1 | STATE_D_UR_R2;
              } else {
                csj |= STATE_SIG_R1 | STATE_VISITED_R1 | STATE_NZ_CTXT_R2 | STATE_V_U_R2;
                state[j - sscanw] |= STATE_NZ_CTXT_R2 | STATE_V_D_R2;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_L_R1 | STATE_D_UL_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_R_R1 | STATE_D_UR_R2;
              }
            } else {
              csj |= STATE_VISITED_R1;
            }
          }
          if (sheight < 4) {
            state[j] = csj;
            continue;
          }
          if ((csj & (STATE_SIG_R2 | STATE_NZ_CTXT_R2)) == STATE_NZ_CTXT_R2) {
            k += dscanw;
            if (bin.readBit() != 0) {
              sym = bin.readBit();
              data[k] = (sym << 31) | setmask;
              state[j + off_dl] |= STATE_NZ_CTXT_R1 | STATE_D_UR_R1;
              state[j + off_dr] |= STATE_NZ_CTXT_R1 | STATE_D_UL_R1;
              if (sym != 0) {
                csj |= STATE_SIG_R2 | STATE_VISITED_R2 | STATE_NZ_CTXT_R1 | STATE_V_D_R1 | STATE_V_D_SIGN_R1;
                state[j + sscanw] |= STATE_NZ_CTXT_R1 | STATE_V_U_R1 | STATE_V_U_SIGN_R1;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DL_R1 | STATE_H_L_R2 | STATE_H_L_SIGN_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DR_R1 | STATE_H_R_R2 | STATE_H_R_SIGN_R2;
              } else {
                csj |= STATE_SIG_R2 | STATE_VISITED_R2 | STATE_NZ_CTXT_R1 | STATE_V_D_R1;
                state[j + sscanw] |= STATE_NZ_CTXT_R1 | STATE_V_U_R1;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DL_R1 | STATE_H_L_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DR_R1 | STATE_H_R_R2;
              }
            } else {
              csj |= STATE_VISITED_R2;
            }
          }
          state[j] = csj;
        }
      }
    }
    error = false;
    if (isterm) {
      error = bin.checkBytePadding();
    }
    return error;
  }

  /**
     * Performs the magnitude refinement pass on the specified data and
     * bit-plane. It decodes the samples which are significant and which do not
     * have the "visited" state bit turned on, using the MR primitive. The
     * "visited" state bit is not mofified for any samples.
     *
     * <P>This method also checks for segmentation markers if those are
     * present and returns true if an error is detected, or false
     * otherwise. If an error is detected it means that the bit stream contains
     * some erroneous bit that have led to the decoding of incorrect
     * data. This data affects the whole last decoded bit-plane (i.e. 'bp'). If
     * 'true' is returned the 'conceal' method should be called and no more
     * passes should be decoded for this code-block's bit stream.
     *
     * @param cblk The code-block data to decode
     *
     * @param mq The MQ-decoder to use
     *
     * @param bp The bit-plane to decode
     *
     * @param state The state information for the code-block
     *
     * @param isterm If this pass has been terminated. If the pass has been
     * terminated it can be used to check error resilience.
     *
     * @return True if an error was detected in the bit stream, false otherwise.
     * */
  private boolean magRefPass(DataBlk cblk, MQDecoder mq, int bp, int[] state, boolean isterm) {
    int j, sj;
    int k, sk;
    int dscanw;
    int sscanw;
    int jstep;
    int kstep;
    int stopsk;
    int csj;
    int setmask;
    int resetmask;
    int sym;
    int data[];
    int s;
    int nstripes;
    int sheight;
    boolean error;
    dscanw = cblk.scanw;
    sscanw = cblk.w + 2;
    jstep = sscanw * STRIPE_HEIGHT / 2 - cblk.w;
    kstep = dscanw * STRIPE_HEIGHT - cblk.w;
    setmask = (1 << bp) >> 1;
    resetmask = (-1) << (bp + 1);
    data = (int[]) cblk.getData();
    nstripes = (cblk.h + STRIPE_HEIGHT - 1) / STRIPE_HEIGHT;
    sk = cblk.offset;
    sj = sscanw + 1;
    for (s = nstripes - 1; s >= 0; s--, sk += kstep, sj += jstep) {
      sheight = (s != 0) ? STRIPE_HEIGHT : cblk.h - (nstripes - 1) * STRIPE_HEIGHT;
      stopsk = sk + cblk.w;
      for ( ; sk < stopsk; sk++, sj++) {
        j = sj;
        csj = state[j];
        if ((((csj >>> 1) & (~csj)) & VSTD_MASK_R1R2) != 0) {
          k = sk;
          if ((csj & (STATE_SIG_R1 | STATE_VISITED_R1)) == STATE_SIG_R1) {
            sym = mq.decodeSymbol(MR_LUT[csj & MR_MASK]);
            data[k] &= resetmask;
            data[k] |= (sym << bp) | setmask;
            csj |= STATE_PREV_MR_R1;
          }
          if (sheight < 2) {
            state[j] = csj;
            continue;
          }
          if ((csj & (STATE_SIG_R2 | STATE_VISITED_R2)) == STATE_SIG_R2) {
            k += dscanw;
            sym = mq.decodeSymbol(MR_LUT[(csj >>> STATE_SEP) & MR_MASK]);
            data[k] &= resetmask;
            data[k] |= (sym << bp) | setmask;
            csj |= STATE_PREV_MR_R2;
          }
          state[j] = csj;
        }
        if (sheight < 3) {
          continue;
        }
        j += sscanw;
        csj = state[j];
        if ((((csj >>> 1) & (~csj)) & VSTD_MASK_R1R2) != 0) {
          k = sk + (dscanw << 1);
          if ((csj & (STATE_SIG_R1 | STATE_VISITED_R1)) == STATE_SIG_R1) {
            sym = mq.decodeSymbol(MR_LUT[csj & MR_MASK]);
            data[k] &= resetmask;
            data[k] |= (sym << bp) | setmask;
            csj |= STATE_PREV_MR_R1;
          }
          if (sheight < 4) {
            state[j] = csj;
            continue;
          }
          if ((state[j] & (STATE_SIG_R2 | STATE_VISITED_R2)) == STATE_SIG_R2) {
            k += dscanw;
            sym = mq.decodeSymbol(MR_LUT[(csj >>> STATE_SEP) & MR_MASK]);
            data[k] &= resetmask;
            data[k] |= (sym << bp) | setmask;
            csj |= STATE_PREV_MR_R2;
          }
          state[j] = csj;
        }
      }
    }
    error = false;
    if (isterm && (options & OPT_PRED_TERM) != 0) {
      error = mq.checkPredTerm();
    }
    if ((options & OPT_RESET_MQ) != 0) {
      mq.resetCtxts();
    }
    return error;
  }

  /**
     * Performs the magnitude refinement pass on the specified data and
     * bit-plane. It decodes the samples which are significant and which do not
     * have the "visited" state bit turned on, using the MR primitive. The
     * "visited" state bit is not mofified for any samples.
     *
     * <P>This method bypasses the arithmetic coder and reads "raw" symbols
     * from the bit stream.
     *
     * <P>This method also checks for segmentation markers if those are
     * present and returns true if an error is detected, or false
     * otherwise. If an error is detected it measn that the bit stream contains
     * some erroneous bit that have led to the decoding of incorrect
     * data. This data affects the whole last decoded bit-plane (i.e. 'bp'). If
     * 'true' is returned the 'conceal' method should be called and no more
     * passes should be decoded for this code-block's bit stream.
     *
     * @param cblk The code-block data to decode
     *
     * @param bin The raw bit based input
     *
     * @param bp The bit-plane to decode
     *
     * @param state The state information for the code-block
     *
     * @param isterm If this pass has been terminated. If the pass has been
     * terminated it can be used to check error resilience.
     *
     * @return True if an error was detected in the bit stream, false otherwise.
     * */
  private boolean rawMagRefPass(DataBlk cblk, ByteToBitInput bin, int bp, int[] state, boolean isterm) {
    int j, sj;
    int k, sk;
    int dscanw;
    int sscanw;
    int jstep;
    int kstep;
    int stopsk;
    int csj;
    int setmask;
    int resetmask;
    int sym;
    int data[];
    int s;
    int nstripes;
    int sheight;
    boolean error;
    dscanw = cblk.scanw;
    sscanw = cblk.w + 2;
    jstep = sscanw * STRIPE_HEIGHT / 2 - cblk.w;
    kstep = dscanw * STRIPE_HEIGHT - cblk.w;
    setmask = (1 << bp) >> 1;
    resetmask = (-1) << (bp + 1);
    data = (int[]) cblk.getData();
    nstripes = (cblk.h + STRIPE_HEIGHT - 1) / STRIPE_HEIGHT;
    sk = cblk.offset;
    sj = sscanw + 1;
    for (s = nstripes - 1; s >= 0; s--, sk += kstep, sj += jstep) {
      sheight = (s != 0) ? STRIPE_HEIGHT : cblk.h - (nstripes - 1) * STRIPE_HEIGHT;
      stopsk = sk + cblk.w;
      for ( ; sk < stopsk; sk++, sj++) {
        j = sj;
        csj = state[j];
        if ((((csj >>> 1) & (~csj)) & VSTD_MASK_R1R2) != 0) {
          k = sk;
          if ((csj & (STATE_SIG_R1 | STATE_VISITED_R1)) == STATE_SIG_R1) {
            sym = bin.readBit();
            data[k] &= resetmask;
            data[k] |= (sym << bp) | setmask;
          }
          if (sheight < 2) {
            continue;
          }
          if ((csj & (STATE_SIG_R2 | STATE_VISITED_R2)) == STATE_SIG_R2) {
            k += dscanw;
            sym = bin.readBit();
            data[k] &= resetmask;
            data[k] |= (sym << bp) | setmask;
          }
        }
        if (sheight < 3) {
          continue;
        }
        j += sscanw;
        csj = state[j];
        if ((((csj >>> 1) & (~csj)) & VSTD_MASK_R1R2) != 0) {
          k = sk + (dscanw << 1);
          if ((csj & (STATE_SIG_R1 | STATE_VISITED_R1)) == STATE_SIG_R1) {
            sym = bin.readBit();
            data[k] &= resetmask;
            data[k] |= (sym << bp) | setmask;
          }
          if (sheight < 4) {
            continue;
          }
          if ((state[j] & (STATE_SIG_R2 | STATE_VISITED_R2)) == STATE_SIG_R2) {
            k += dscanw;
            sym = bin.readBit();
            data[k] &= resetmask;
            data[k] |= (sym << bp) | setmask;
          }
        }
      }
    }
    error = false;
    if (isterm && (options & OPT_PRED_TERM) != 0) {
      error = bin.checkBytePadding();
    }
    return error;
  }

  /**
     * Performs the cleanup pass on the specified data and bit-plane. It
     * decodes all insignificant samples which have its "visited" state bit
     * off, using the ZC, SC, and RLC primitives. It toggles the "visited"
     * state bit to 0 (off) for all samples in the code-block.
     *
     * <P>This method also checks for segmentation markers if those are
     * present and returns true if an error is detected, or false
     * otherwise. If an error is detected it measn that the bit stream
     * contains some erroneous bit that have led to the decoding of incorrect
     * data. This data affects the whole last decoded bit-plane
     * (i.e. 'bp'). If 'true' is returned the 'conceal' method should be
     * called and no more passes should be decoded for this code-block's bit
     * stream.
     *
     * @param cblk The code-block data to code
     *
     * @param mq The MQ-coder to use
     *
     * @param bp The bit-plane to decode
     *
     * @param state The state information for the code-block
     *
     * @param zc_lut The ZC lookup table to use in ZC.
     *
     * @param isterm If this pass has been terminated. If the pass has been
     * terminated it can be used to check error resilience.
     *
     * @return True if an error was detected in the bit stream, false
     * otherwise.
     * */
  private boolean cleanuppass(DataBlk cblk, MQDecoder mq, int bp, int[] state, int[] zc_lut, boolean isterm) {
    int j, sj;
    int k, sk;
    int dscanw;
    int sscanw;
    int jstep;
    int kstep;
    int stopsk;
    int csj;
    int setmask;
    int sym;
    int rlclen;
    int ctxt;
    int data[];
    int s;
    boolean causal;
    int nstripes;
    int sheight;
    int off_ul, off_ur, off_dr, off_dl;
    boolean error;
    dscanw = cblk.scanw;
    sscanw = cblk.w + 2;
    jstep = sscanw * STRIPE_HEIGHT / 2 - cblk.w;
    kstep = dscanw * STRIPE_HEIGHT - cblk.w;
    int one = 1 << bp;
    int half = one >> 1;

<<<<<<< /usr/src/app/output/jai-imageio/jai-imageio-jpeg2000/96f3e9ffc1450a7b27819ca751a462e1048517d4/src/main/java/jj2000/j2k/entropy/decoder/StdEntropyDecoder.java/left.java
    setmask = one | half
=======
    setmask = 3 << (bp - 1)
>>>>>>> /usr/src/app/output/jai-imageio/jai-imageio-jpeg2000/96f3e9ffc1450a7b27819ca751a462e1048517d4/src/main/java/jj2000/j2k/entropy/decoder/StdEntropyDecoder.java/right.java
    ;
    data = (int[]) cblk.getData();
    nstripes = (cblk.h + STRIPE_HEIGHT - 1) / STRIPE_HEIGHT;
    causal = (options & OPT_VERT_STR_CAUSAL) != 0;
    off_ul = -sscanw - 1;
    off_ur = -sscanw + 1;
    off_dr = sscanw + 1;
    off_dl = sscanw - 1;
    sk = cblk.offset;
    sj = sscanw + 1;
    for (s = nstripes - 1; s >= 0; s--, sk += kstep, sj += jstep) {
      sheight = (s != 0) ? STRIPE_HEIGHT : cblk.h - (nstripes - 1) * STRIPE_HEIGHT;
      stopsk = sk + cblk.w;
      for ( ; sk < stopsk; sk++, sj++) {
        j = sj;
        csj = state[j];
        top_half:
        {
          if (csj == 0 && state[j + sscanw] == 0 && sheight == STRIPE_HEIGHT) {
            if (mq.decodeSymbol(RLC_CTXT) != 0) {
              rlclen = mq.decodeSymbol(UNIF_CTXT) << 1;
              rlclen |= mq.decodeSymbol(UNIF_CTXT);
              k = sk + rlclen * dscanw;
              if (rlclen > 1) {
                j += sscanw;
                csj = state[j];
              }
            } else {
              continue;
            }
            if ((rlclen & 0x01) == 0) {
              ctxt = SC_LUT[(csj >> SC_SHIFT_R1) & SC_MASK];
              sym = mq.decodeSymbol(ctxt & SC_LUT_MASK) ^ (ctxt >>> SC_SPRED_SHIFT);
              data[k] = (sym << 31) | setmask;
              if (rlclen != 0 || !causal) {
                state[j + off_ul] |= STATE_NZ_CTXT_R2 | STATE_D_DR_R2;
                state[j + off_ur] |= STATE_NZ_CTXT_R2 | STATE_D_DL_R2;
              }
              if (sym != 0) {
                csj |= STATE_SIG_R1 | STATE_VISITED_R1 | STATE_NZ_CTXT_R2 | STATE_V_U_R2 | STATE_V_U_SIGN_R2;
                if (rlclen != 0 || !causal) {
                  state[j - sscanw] |= STATE_NZ_CTXT_R2 | STATE_V_D_R2 | STATE_V_D_SIGN_R2;
                }
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_L_R1 | STATE_H_L_SIGN_R1 | STATE_D_UL_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_R_R1 | STATE_H_R_SIGN_R1 | STATE_D_UR_R2;
              } else {
                csj |= STATE_SIG_R1 | STATE_VISITED_R1 | STATE_NZ_CTXT_R2 | STATE_V_U_R2;
                if (rlclen != 0 || !causal) {
                  state[j - sscanw] |= STATE_NZ_CTXT_R2 | STATE_V_D_R2;
                }
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_L_R1 | STATE_D_UL_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_R_R1 | STATE_D_UR_R2;
              }
              if ((rlclen >> 1) != 0) {
                break top_half;
              }
            } else {
              ctxt = SC_LUT[(csj >> SC_SHIFT_R2) & SC_MASK];
              sym = mq.decodeSymbol(ctxt & SC_LUT_MASK) ^ (ctxt >>> SC_SPRED_SHIFT);
              data[k] = (sym << 31) | setmask;
              state[j + off_dl] |= STATE_NZ_CTXT_R1 | STATE_D_UR_R1;
              state[j + off_dr] |= STATE_NZ_CTXT_R1 | STATE_D_UL_R1;
              if (sym != 0) {
                csj |= STATE_SIG_R2 | STATE_NZ_CTXT_R1 | STATE_V_D_R1 | STATE_V_D_SIGN_R1;
                state[j + sscanw] |= STATE_NZ_CTXT_R1 | STATE_V_U_R1 | STATE_V_U_SIGN_R1;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DL_R1 | STATE_H_L_R2 | STATE_H_L_SIGN_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DR_R1 | STATE_H_R_R2 | STATE_H_R_SIGN_R2;
              } else {
                csj |= STATE_SIG_R2 | STATE_NZ_CTXT_R1 | STATE_V_D_R1;
                state[j + sscanw] |= STATE_NZ_CTXT_R1 | STATE_V_U_R1;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DL_R1 | STATE_H_L_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DR_R1 | STATE_H_R_R2;
              }
              state[j] = csj;
              if ((rlclen >> 1) != 0) {
                continue;
              }
              j += sscanw;
              csj = state[j];
              break top_half;
            }
          }
          if ((((csj >> 1) | csj) & VSTD_MASK_R1R2) != VSTD_MASK_R1R2) {
            k = sk;
            if ((csj & (STATE_SIG_R1 | STATE_VISITED_R1)) == 0) {
              if (mq.decodeSymbol(zc_lut[csj & ZC_MASK]) != 0) {
                ctxt = SC_LUT[(csj >>> SC_SHIFT_R1) & SC_MASK];
                sym = mq.decodeSymbol(ctxt & SC_LUT_MASK) ^ (ctxt >>> SC_SPRED_SHIFT);
                data[k] = (sym << 31) | setmask;
                if (!causal) {
                  state[j + off_ul] |= STATE_NZ_CTXT_R2 | STATE_D_DR_R2;
                  state[j + off_ur] |= STATE_NZ_CTXT_R2 | STATE_D_DL_R2;
                }
                if (sym != 0) {
                  csj |= STATE_SIG_R1 | STATE_VISITED_R1 | STATE_NZ_CTXT_R2 | STATE_V_U_R2 | STATE_V_U_SIGN_R2;
                  if (!causal) {
                    state[j - sscanw] |= STATE_NZ_CTXT_R2 | STATE_V_D_R2 | STATE_V_D_SIGN_R2;
                  }
                  state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_L_R1 | STATE_H_L_SIGN_R1 | STATE_D_UL_R2;
                  state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_R_R1 | STATE_H_R_SIGN_R1 | STATE_D_UR_R2;
                } else {
                  csj |= STATE_SIG_R1 | STATE_VISITED_R1 | STATE_NZ_CTXT_R2 | STATE_V_U_R2;
                  if (!causal) {
                    state[j - sscanw] |= STATE_NZ_CTXT_R2 | STATE_V_D_R2;
                  }
                  state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_L_R1 | STATE_D_UL_R2;
                  state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_R_R1 | STATE_D_UR_R2;
                }
              }
            }
            if (sheight < 2) {
              csj &= ~(STATE_VISITED_R1 | STATE_VISITED_R2);
              state[j] = csj;
              continue;
            }
            if ((csj & (STATE_SIG_R2 | STATE_VISITED_R2)) == 0) {
              k += dscanw;
              if (mq.decodeSymbol(zc_lut[(csj >>> STATE_SEP) & ZC_MASK]) != 0) {
                ctxt = SC_LUT[(csj >>> SC_SHIFT_R2) & SC_MASK];
                sym = mq.decodeSymbol(ctxt & SC_LUT_MASK) ^ (ctxt >>> SC_SPRED_SHIFT);
                data[k] = (sym << 31) | setmask;
                state[j + off_dl] |= STATE_NZ_CTXT_R1 | STATE_D_UR_R1;
                state[j + off_dr] |= STATE_NZ_CTXT_R1 | STATE_D_UL_R1;
                if (sym != 0) {
                  csj |= STATE_SIG_R2 | STATE_VISITED_R2 | STATE_NZ_CTXT_R1 | STATE_V_D_R1 | STATE_V_D_SIGN_R1;
                  state[j + sscanw] |= STATE_NZ_CTXT_R1 | STATE_V_U_R1 | STATE_V_U_SIGN_R1;
                  state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DL_R1 | STATE_H_L_R2 | STATE_H_L_SIGN_R2;
                  state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DR_R1 | STATE_H_R_R2 | STATE_H_R_SIGN_R2;
                } else {
                  csj |= STATE_SIG_R2 | STATE_VISITED_R2 | STATE_NZ_CTXT_R1 | STATE_V_D_R1;
                  state[j + sscanw] |= STATE_NZ_CTXT_R1 | STATE_V_U_R1;
                  state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DL_R1 | STATE_H_L_R2;
                  state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DR_R1 | STATE_H_R_R2;
                }
              }
            }
          }
          csj &= ~(STATE_VISITED_R1 | STATE_VISITED_R2);
          state[j] = csj;
          if (sheight < 3) {
            continue;
          }
          j += sscanw;
          csj = state[j];
        }
        if ((((csj >> 1) | csj) & VSTD_MASK_R1R2) != VSTD_MASK_R1R2) {
          k = sk + (dscanw << 1);
          if ((csj & (STATE_SIG_R1 | STATE_VISITED_R1)) == 0) {
            if (mq.decodeSymbol(zc_lut[csj & ZC_MASK]) != 0) {
              ctxt = SC_LUT[(csj >> SC_SHIFT_R1) & SC_MASK];
              sym = mq.decodeSymbol(ctxt & SC_LUT_MASK) ^ (ctxt >>> SC_SPRED_SHIFT);
              data[k] = (sym << 31) | setmask;
              state[j + off_ul] |= STATE_NZ_CTXT_R2 | STATE_D_DR_R2;
              state[j + off_ur] |= STATE_NZ_CTXT_R2 | STATE_D_DL_R2;
              if (sym != 0) {
                csj |= STATE_SIG_R1 | STATE_VISITED_R1 | STATE_NZ_CTXT_R2 | STATE_V_U_R2 | STATE_V_U_SIGN_R2;
                state[j - sscanw] |= STATE_NZ_CTXT_R2 | STATE_V_D_R2 | STATE_V_D_SIGN_R2;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_L_R1 | STATE_H_L_SIGN_R1 | STATE_D_UL_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_R_R1 | STATE_H_R_SIGN_R1 | STATE_D_UR_R2;
              } else {
                csj |= STATE_SIG_R1 | STATE_VISITED_R1 | STATE_NZ_CTXT_R2 | STATE_V_U_R2;
                state[j - sscanw] |= STATE_NZ_CTXT_R2 | STATE_V_D_R2;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_L_R1 | STATE_D_UL_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_H_R_R1 | STATE_D_UR_R2;
              }
            }
          }
          if (sheight < 4) {
            csj &= ~(STATE_VISITED_R1 | STATE_VISITED_R2);
            state[j] = csj;
            continue;
          }
          if ((csj & (STATE_SIG_R2 | STATE_VISITED_R2)) == 0) {
            k += dscanw;
            if (mq.decodeSymbol(zc_lut[(csj >>> STATE_SEP) & ZC_MASK]) != 0) {
              ctxt = SC_LUT[(csj >>> SC_SHIFT_R2) & SC_MASK];
              sym = mq.decodeSymbol(ctxt & SC_LUT_MASK) ^ (ctxt >>> SC_SPRED_SHIFT);
              data[k] = (sym << 31) | setmask;
              state[j + off_dl] |= STATE_NZ_CTXT_R1 | STATE_D_UR_R1;
              state[j + off_dr] |= STATE_NZ_CTXT_R1 | STATE_D_UL_R1;
              if (sym != 0) {
                csj |= STATE_SIG_R2 | STATE_VISITED_R2 | STATE_NZ_CTXT_R1 | STATE_V_D_R1 | STATE_V_D_SIGN_R1;
                state[j + sscanw] |= STATE_NZ_CTXT_R1 | STATE_V_U_R1 | STATE_V_U_SIGN_R1;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DL_R1 | STATE_H_L_R2 | STATE_H_L_SIGN_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DR_R1 | STATE_H_R_R2 | STATE_H_R_SIGN_R2;
              } else {
                csj |= STATE_SIG_R2 | STATE_VISITED_R2 | STATE_NZ_CTXT_R1 | STATE_V_D_R1;
                state[j + sscanw] |= STATE_NZ_CTXT_R1 | STATE_V_U_R1;
                state[j + 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DL_R1 | STATE_H_L_R2;
                state[j - 1] |= STATE_NZ_CTXT_R1 | STATE_NZ_CTXT_R2 | STATE_D_DR_R1 | STATE_H_R_R2;
              }
            }
          }
        }
        csj &= ~(STATE_VISITED_R1 | STATE_VISITED_R2);
        state[j] = csj;
      }
    }
    if ((options & OPT_SEG_SYMBOLS) != 0) {
      sym = mq.decodeSymbol(UNIF_CTXT) << 3;
      sym |= mq.decodeSymbol(UNIF_CTXT) << 2;
      sym |= mq.decodeSymbol(UNIF_CTXT) << 1;
      sym |= mq.decodeSymbol(UNIF_CTXT);
      error = sym != SEG_SYMBOL;
    } else {
      error = false;
    }
    if (isterm && (options & OPT_PRED_TERM) != 0) {
      error = mq.checkPredTerm();
    }
    if ((options & OPT_RESET_MQ) != 0) {
      mq.resetCtxts();
    }
    return error;
  }

  /**
     * Conceals decoding errors detected in the last bit-plane. The
     * concealement resets the state of the decoded data to what it was before
     * the decoding of bit-plane 'bp' started. No more data should be decoded
     * after this method is called for this code-block's data to which it is
     * applied.
     *
     * @param cblk The code-block's data
     *
     * @param bp The last decoded bit-plane (which contains errors).
     * */
  private void conceal(DataBlk cblk, int bp) {
    int l;
    int k;
    int kmax;
    int dk;
    int data[];
    int setmask;
    int resetmask;
    setmask = 1 << bp;
    resetmask = (-1) << (bp);
    data = (int[]) cblk.getData();
    for (l = cblk.h - 1, k = cblk.offset; l >= 0; l--) {
      for (kmax = k + cblk.w; k < kmax; k++) {
        dk = data[k];
        if ((dk & resetmask & 0x7FFFFFFF) != 0) {
          data[k] = (dk & resetmask) | setmask;
        } else {
          data[k] = 0;
        }
      }
      k += cblk.scanw - cblk.w;
    }
  }
}