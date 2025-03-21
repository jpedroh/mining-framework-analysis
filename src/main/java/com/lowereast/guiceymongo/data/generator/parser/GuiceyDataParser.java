package com.lowereast.guiceymongo.data.generator.parser;
import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.antlr.runtime.tree.*;

public class GuiceyDataParser extends Parser {
  public static final String[] tokenNames = new String[] { "<invalid>", "<EOR>", "<DOWN>", "<UP>", "DATA", "TYPE_MAP", "TYPE_SET", "TYPE_LIST", "PARAMETERIZED_TYPE", "TYPE_PRIMITIVE", "PROPERTY", "OPTION", "PAIR", "ENUM", "ID", "COMMENT", "INT", "FLOAT", "STRING", "TYPE", "WS", "EXPONENT", "ESC_SEQ", "HEX_DIGIT", "UNICODE_ESC", "OCTAL_ESC", "\'{\'", "\'}\'", "\'{}\'", "\',\'", "\'/*\'", "\'*/\'", "\'[\'", "\']\'", "\'(\'", "\')]\'", "\'=\'", "\'data;\'", "\';\'", "\'map<\'", "\'>\'", "\'set<\'", "\'list<\'", "\'<\'" };

  public static final int EXPONENT = 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  21
=======
  20
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final int T__40 = 40;

  public static final int T__29 = 29;

  public static final int OPTION = 11;

  public static final int T__28 = 28;

  public static final int T__27 = 27;

  public static final int T__26 = 26;

  public static final int OCTAL_ESC = 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  25
=======
  24
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final int COMMENT = 15;

  public static final int FLOAT = 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  17
=======
  16
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final int UNICODE_ESC = 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  24
=======
  23
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final int TYPE_LIST = 7;

  public static final int HEX_DIGIT = 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  23
=======
  22
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final int ID = 14;

  public static final int INT = 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  16
=======
  15
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final int EOF = -1;

  public static final int TYPE_PRIMITIVE = 9;

  public static final int TYPE = 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  19
=======
  18
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final int ESC_SEQ = 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  22
=======
  21
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final int T__30 = 30;

  public static final int TYPE_MAP = 5;

  public static final int T__31 = 31;

  public static final int PAIR = 12;

  public static final int PARAMETERIZED_TYPE = 8;


<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  public static final int T__42 = 42;
=======
>>>>>>> Unknown file: This is a bug in JDime.


  public static final int T__43 = 43;

  public static final int T__41 = 41;

  public static final int T__32 = 32;

  public static final int T__33 = 33;

  public static final int WS = 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  20
=======
  19
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final int T__34 = 34;

  public static final int ENUM = 13;

  public static final int T__35 = 35;

  public static final int T__36 = 36;

  public static final int T__37 = 37;

  public static final int PROPERTY = 10;

  public static final int T__38 = 38;

  public static final int T__39 = 39;

  public static final int TYPE_SET = 6;

  public static final int DATA = 4;

  public static final int STRING = 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  18
=======
  17
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public GuiceyDataParser(TokenStream input) {
    this(input, new RecognizerSharedState());
  }

  public GuiceyDataParser(TokenStream input, RecognizerSharedState state) {
    super(input, state);
  }

  protected TreeAdaptor adaptor = new CommonTreeAdaptor();

  public void setTreeAdaptor(TreeAdaptor adaptor) {
    this.adaptor = adaptor;
  }

  public TreeAdaptor getTreeAdaptor() {
    return adaptor;
  }

  public String[] getTokenNames() {
    return GuiceyDataParser.tokenNames;
  }

  public String getGrammarFileName() {
    return "com\\lowereast\\guiceymongo\\data\\generator\\parser\\GuiceyData.g";
  }

  public static class start_return extends ParserRuleReturnScope {
    Object tree;

    public Object getTree() {
      return tree;
    }
  }



  public final GuiceyDataParser.start_return start() throws RecognitionException {
    GuiceyDataParser.start_return retval = new GuiceyDataParser.start_return();
    retval.start = input.LT(1);
    Object root_0 = null;
    Token EOF2 = null;
    GuiceyDataParser.entry_return entry1 = null;
    Object EOF2_tree = null;
    try {
      {
        root_0 = (Object) adaptor.nil();
        int cnt1 = 0;
        loop1:
        do {
          int alt1 = 2;
          switch (input.LA(1)) {
            case DATA:
            case ENUM:
            {
              alt1 = 1;
            }
            break;
          }
          switch (alt1) {
            case 1:
            {
              pushFollow(FOLLOW_entry_in_start114);
              entry1 = entry();
              state._fsp--;
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                adaptor.addChild(root_0, entry1.getTree());
              }
            }
            break;
            default:
            if (cnt1 >= 1) {
              break loop1;
            }
            if (state.backtracking > 0) {
              state.failed = true;
              return retval;
            }
            EarlyExitException eee = new EarlyExitException(1, input);
            throw eee;
          }
          cnt1++;
        } while(true);
        EOF2 = (Token) match(input, EOF, FOLLOW_EOF_in_start117);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          EOF2_tree = (Object) adaptor.create(EOF2);
          adaptor.addChild(root_0, EOF2_tree);
        }
      }
      retval.stop = input.LT(-1);
      if (state.backtracking == 0) {
        retval.tree = (Object) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
      retval.tree = (Object) adaptor.errorNode(input, retval.start, input.LT(-1), re);
    } finally {
    }
    return retval;
  }

  public static class entry_return extends ParserRuleReturnScope {
    Object tree;

    public Object getTree() {
      return tree;
    }
  }



  public final GuiceyDataParser.entry_return entry() throws RecognitionException {
    GuiceyDataParser.entry_return retval = new GuiceyDataParser.entry_return();
    retval.start = input.LT(1);
    Object root_0 = null;
    GuiceyDataParser.data_return data3 = null;
    GuiceyDataParser.enumeration_return enumeration4 = null;
    try {
      int alt2 = 2;
      switch (input.LA(1)) {
        case DATA:
        {
          alt2 = 1;
        }
        break;
        case ENUM:
        {
          alt2 = 2;
        }
        break;
        default:
        if (state.backtracking > 0) {
          state.failed = true;
          return retval;
        }
        NoViableAltException nvae = new NoViableAltException("", 2, 0, input);
        throw nvae;
      }
      switch (alt2) {
        case 1:
        {
          root_0 = (Object) adaptor.nil();
          pushFollow(FOLLOW_data_in_entry127);
          data3 = data();
          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            adaptor.addChild(root_0, data3.getTree());
          }
        }
        break;
        case 2:
        {
          root_0 = (Object) adaptor.nil();
          pushFollow(FOLLOW_enumeration_in_entry132);
          enumeration4 = enumeration();
          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            adaptor.addChild(root_0, enumeration4.getTree());
          }
        }
        break;
      }
      retval.stop = input.LT(-1);
      if (state.backtracking == 0) {
        retval.tree = (Object) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
      retval.tree = (Object) adaptor.errorNode(input, retval.start, input.LT(-1), re);
    } finally {
    }
    return retval;
  }

  public static class data_return extends ParserRuleReturnScope {
    Object tree;

    public Object getTree() {
      return tree;
    }
  }



  public final GuiceyDataParser.data_return data() throws RecognitionException {
    GuiceyDataParser.data_return retval = new GuiceyDataParser.data_return();
    retval.start = input.LT(1);
    Object root_0 = null;
    Token DATA5 = null;
    Token ID6 = null;
    Token char_literal7 = null;
    Token char_literal9 = null;
    Token DATA10 = null;
    Token ID11 = null;
    Token string_literal12 = null;
    GuiceyDataParser.data_entry_return data_entry8 = null;
    Object DATA5_tree = null;
    Object ID6_tree = null;
    Object char_literal7_tree = null;
    Object char_literal9_tree = null;
    Object DATA10_tree = null;
    Object ID11_tree = null;
    Object string_literal12_tree = null;
    RewriteRuleTokenStream stream_ID = new RewriteRuleTokenStream(adaptor, "token ID");
    RewriteRuleTokenStream stream_DATA = new RewriteRuleTokenStream(adaptor, "token DATA");
    RewriteRuleTokenStream stream_26 = new RewriteRuleTokenStream(adaptor, "token 26");
    RewriteRuleTokenStream stream_27 = new RewriteRuleTokenStream(adaptor, "token 27");

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
    RewriteRuleTokenStream stream_28 = new RewriteRuleTokenStream(adaptor, "token 28");
=======
>>>>>>> Unknown file: This is a bug in JDime.

    RewriteRuleSubtreeStream stream_data_entry = new RewriteRuleSubtreeStream(adaptor, "rule data_entry");
    try {
      int alt4 = 2;
      switch (input.LA(1)) {
        case DATA:
        {
          switch (input.LA(2)) {
            case ID:
            {
              switch (input.LA(3)) {
                case 25:
                {
                  alt4 = 1;
                }
                break;
                case 27:
                {
                  alt4 = 2;
                }
                break;
                default:
                if (state.backtracking > 0) {
                  state.failed = true;
                  return retval;
                }
                NoViableAltException nvae = new NoViableAltException("", 4, 2, input);
                throw nvae;
              }
            }
            break;
            default:
            if (state.backtracking > 0) {
              state.failed = true;
              return retval;
            }
            NoViableAltException nvae = new NoViableAltException("", 4, 1, input);
            throw nvae;
          }
        }
        break;
        default:
        if (state.backtracking > 0) {
          state.failed = true;
          return retval;
        }
        NoViableAltException nvae = new NoViableAltException("", 4, 0, input);
        throw nvae;
      }

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
      if ((LA4_0 == DATA)) {
        int LA4_1 = input.LA(2);
        if ((LA4_1 == ID)) {
          int LA4_2 = input.LA(3);
          if ((LA4_2 == 26)) {
            alt4 = 1;
          } else {
            if ((LA4_2 == 28)) {
              alt4 = 2;
            } else {
              if (state.backtracking > 0) {
                state.failed = true;
                return retval;
              }
              NoViableAltException nvae = new NoViableAltException("", 4, 2, input);
              throw nvae;
            }
          }
        } else {
          if (state.backtracking > 0) {
            state.failed = true;
            return retval;
          }
          NoViableAltException nvae = new NoViableAltException("", 4, 1, input);
          throw nvae;
        }
      } else {
        if (state.backtracking > 0) {
          state.failed = true;
          return retval;
        }
        NoViableAltException nvae = new NoViableAltException("", 4, 0, input);
        throw nvae;
      }
=======
>>>>>>> Unknown file: This is a bug in JDime.

      switch (alt4) {
        case 1:
        {
          DATA5 = (Token) match(input, DATA, FOLLOW_DATA_in_data142);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_DATA.add(DATA5);
          }
          ID6 = (Token) match(input, ID, FOLLOW_ID_in_data144);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_ID.add(ID6);
          }
          char_literal7 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          26
=======
          25
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_26_in_data146
=======
          FOLLOW_25_in_data146
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_26
=======
            stream_25
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(char_literal7);
          }
          loop3:
          do {
            int alt3 = 2;
            switch (input.LA(1)) {
              case DATA:
              case ENUM:
              case ID:
              case TYPE:
              case 29:
              case 36:
              case 38:
              case 39:
              {
                alt3 = 1;
              }
              break;
            }

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            if ((LA3_0 == DATA || (LA3_0 >= ENUM && LA3_0 <= ID) || LA3_0 == TYPE || LA3_0 == 30 || LA3_0 == 32 || LA3_0 == 39 || (LA3_0 >= 41 && LA3_0 <= 42))) {
              alt3 = 1;
            }
=======
>>>>>>> Unknown file: This is a bug in JDime.

            switch (alt3) {
              case 1:
              {
                pushFollow(FOLLOW_data_entry_in_data148);
                data_entry8 = data_entry();
                state._fsp--;
                if (state.failed) {
                  return retval;
                }
                if (state.backtracking == 0) {
                  stream_data_entry.add(data_entry8.getTree());
                }
              }
              break;
              default:
              break loop3;
            }
          } while(true);
          char_literal9 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          27
=======
          26
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_27_in_data151
=======
          FOLLOW_26_in_data151
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_27
=======
            stream_26
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(char_literal9);
          }
          if (state.backtracking == 0) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
            root_0 = (Object) adaptor.nil();
            {
              {
                Object root_1 = (Object) adaptor.nil();
                root_1 = (Object) adaptor.becomeRoot(stream_DATA.nextNode(), root_1);
                adaptor.addChild(root_1, stream_ID.nextNode());
                while (stream_data_entry.hasNext()) {
                  adaptor.addChild(root_1, stream_data_entry.nextTree());
                }
                stream_data_entry.reset();
                adaptor.addChild(root_0, root_1);
              }
            }
            retval.tree = root_0;
          }
        }
        break;
        case 2:
        {
          DATA10 = (Token) match(input, DATA, FOLLOW_DATA_in_data167);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_DATA.add(DATA10);
          }
          ID11 = (Token) match(input, ID, FOLLOW_ID_in_data169);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_ID.add(ID11);
          }
          string_literal12 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          28
=======
          27
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_28_in_data171
=======
          FOLLOW_27_in_data171
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_28
=======
            stream_27
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(string_literal12);
          }
          if (state.backtracking == 0) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
            root_0 = (Object) adaptor.nil();
            {
              {
                Object root_1 = (Object) adaptor.nil();
                root_1 = (Object) adaptor.becomeRoot(stream_DATA.nextNode(), root_1);
                adaptor.addChild(root_1, stream_ID.nextNode());
                adaptor.addChild(root_0, root_1);
              }
            }
            retval.tree = root_0;
          }
        }
        break;
      }
      retval.stop = input.LT(-1);
      if (state.backtracking == 0) {
        retval.tree = (Object) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
      retval.tree = (Object) adaptor.errorNode(input, retval.start, input.LT(-1), re);
    } finally {
    }
    return retval;
  }

  public static class enumeration_return extends ParserRuleReturnScope {
    Object tree;

    public Object getTree() {
      return tree;
    }
  }



  public final GuiceyDataParser.enumeration_return enumeration() throws RecognitionException {
    GuiceyDataParser.enumeration_return retval = new GuiceyDataParser.enumeration_return();
    retval.start = input.LT(1);
    Object root_0 = null;
    Token ENUM13 = null;
    Token ID14 = null;
    Token string_literal15 = null;
    Token ENUM16 = null;
    Token ID17 = null;
    Token char_literal18 = null;
    Token ID19 = null;
    Token char_literal20 = null;
    Token ID21 = null;
    Token char_literal22 = null;
    Object ENUM13_tree = null;
    Object ID14_tree = null;
    Object string_literal15_tree = null;
    Object ENUM16_tree = null;
    Object ID17_tree = null;
    Object char_literal18_tree = null;
    Object ID19_tree = null;
    Object char_literal20_tree = null;
    Object ID21_tree = null;
    Object char_literal22_tree = null;
    RewriteRuleTokenStream stream_ENUM = new RewriteRuleTokenStream(adaptor, "token ENUM");
    RewriteRuleTokenStream stream_ID = new RewriteRuleTokenStream(adaptor, "token ID");
    RewriteRuleTokenStream stream_26 = new RewriteRuleTokenStream(adaptor, "token 26");
    RewriteRuleTokenStream stream_27 = new RewriteRuleTokenStream(adaptor, "token 27");
    RewriteRuleTokenStream stream_28 = new RewriteRuleTokenStream(adaptor, "token 28");
    RewriteRuleTokenStream stream_29 = new RewriteRuleTokenStream(adaptor, "token 29");
    try {
      int alt6 = 2;
      switch (input.LA(1)) {
        case ENUM:
        {
          switch (input.LA(2)) {
            case ID:
            {
              switch (input.LA(3)) {
                case 27:
                {
                  alt6 = 1;
                }
                break;
                case 25:
                {
                  alt6 = 2;
                }
                break;
                default:
                if (state.backtracking > 0) {
                  state.failed = true;
                  return retval;
                }
                NoViableAltException nvae = new NoViableAltException("", 6, 2, input);
                throw nvae;
              }
            }
            break;
            default:
            if (state.backtracking > 0) {
              state.failed = true;
              return retval;
            }
            NoViableAltException nvae = new NoViableAltException("", 6, 1, input);
            throw nvae;
          }
        }
        break;
        default:
        if (state.backtracking > 0) {
          state.failed = true;
          return retval;
        }
        NoViableAltException nvae = new NoViableAltException("", 6, 0, input);
        throw nvae;
      }

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
      if ((LA6_0 == ENUM)) {
        int LA6_1 = input.LA(2);
        if ((LA6_1 == ID)) {
          int LA6_2 = input.LA(3);
          if ((LA6_2 == 28)) {
            alt6 = 1;
          } else {
            if ((LA6_2 == 26)) {
              alt6 = 2;
            } else {
              if (state.backtracking > 0) {
                state.failed = true;
                return retval;
              }
              NoViableAltException nvae = new NoViableAltException("", 6, 2, input);
              throw nvae;
            }
          }
        } else {
          if (state.backtracking > 0) {
            state.failed = true;
            return retval;
          }
          NoViableAltException nvae = new NoViableAltException("", 6, 1, input);
          throw nvae;
        }
      } else {
        if (state.backtracking > 0) {
          state.failed = true;
          return retval;
        }
        NoViableAltException nvae = new NoViableAltException("", 6, 0, input);
        throw nvae;
      }
=======
>>>>>>> Unknown file: This is a bug in JDime.

      switch (alt6) {
        case 1:
        {
          ENUM13 = (Token) match(input, ENUM, FOLLOW_ENUM_in_enumeration190);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_ENUM.add(ENUM13);
          }
          ID14 = (Token) match(input, ID, FOLLOW_ID_in_enumeration192);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_ID.add(ID14);
          }
          string_literal15 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          28
=======
          27
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_28_in_enumeration194
=======
          FOLLOW_27_in_enumeration194
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_28
=======
            stream_27
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(string_literal15);
          }
          if (state.backtracking == 0) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
            root_0 = (Object) adaptor.nil();
            {
              {
                Object root_1 = (Object) adaptor.nil();
                root_1 = (Object) adaptor.becomeRoot(stream_ENUM.nextNode(), root_1);
                adaptor.addChild(root_1, stream_ID.nextNode());
                adaptor.addChild(root_0, root_1);
              }
            }
            retval.tree = root_0;
          }
        }
        break;
        case 2:
        {
          ENUM16 = (Token) match(input, ENUM, FOLLOW_ENUM_in_enumeration207);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_ENUM.add(ENUM16);
          }
          ID17 = (Token) match(input, ID, FOLLOW_ID_in_enumeration209);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_ID.add(ID17);
          }
          char_literal18 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          26
=======
          25
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_26_in_enumeration211
=======
          FOLLOW_25_in_enumeration211
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_26
=======
            stream_25
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(char_literal18);
          }
          loop5:
          do {
            int alt5 = 2;
            switch (input.LA(1)) {
              case ID:
              {
                switch (input.LA(2)) {
                  case 28:
                  {
                    alt5 = 1;
                  }
                  break;
                }
              }
              break;
            }

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            if ((LA5_0 == ID)) {
              int LA5_1 = input.LA(2);
              if ((LA5_1 == 29)) {
                alt5 = 1;
              }
            }
=======
>>>>>>> Unknown file: This is a bug in JDime.

            switch (alt5) {
              case 1:
              {
                ID19 = (Token) match(input, ID, FOLLOW_ID_in_enumeration214);
                if (state.failed) {
                  return retval;
                }
                if (state.backtracking == 0) {
                  stream_ID.add(ID19);
                }
                char_literal20 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
                29
=======
                28
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
                , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
                FOLLOW_29_in_enumeration216
=======
                FOLLOW_28_in_enumeration216
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
                );
                if (state.failed) {
                  return retval;
                }
                if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
                  stream_29
=======
                  stream_28
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
                  .add(char_literal20);
                }
              }
              break;
              default:
              break loop5;
            }
          } while(true);
          ID21 = (Token) match(input, ID, FOLLOW_ID_in_enumeration220);
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_ID.add(ID21);
          }
          char_literal22 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          27
=======
          26
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_27_in_enumeration222
=======
          FOLLOW_26_in_enumeration222
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_27
=======
            stream_26
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(char_literal22);
          }
          if (state.backtracking == 0) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
            root_0 = (Object) adaptor.nil();
            {
              {
                Object root_1 = (Object) adaptor.nil();
                root_1 = (Object) adaptor.becomeRoot(stream_ENUM.nextNode(), root_1);
                if (!(stream_ID.hasNext())) {
                  throw new RewriteEarlyExitException();
                }
                while (stream_ID.hasNext()) {
                  adaptor.addChild(root_1, stream_ID.nextNode());
                }
                stream_ID.reset();
                adaptor.addChild(root_0, root_1);
              }
            }
            retval.tree = root_0;
          }
        }
        break;
      }
      retval.stop = input.LT(-1);
      if (state.backtracking == 0) {
        retval.tree = (Object) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
      retval.tree = (Object) adaptor.errorNode(input, retval.start, input.LT(-1), re);
    } finally {
    }
    return retval;
  }

  public static class data_entry_return extends ParserRuleReturnScope {
    Object tree;

    public Object getTree() {
      return tree;
    }
  }



  public final GuiceyDataParser.data_entry_return data_entry() throws RecognitionException {
    GuiceyDataParser.data_entry_return retval = new GuiceyDataParser.data_entry_return();
    retval.start = input.LT(1);
    Object root_0 = null;
    GuiceyDataParser.entry_return entry23 = null;
    GuiceyDataParser.javadoc_return javadoc24 = null;
    GuiceyDataParser.option_return option25 = null;
    GuiceyDataParser.property_return property26 = null;
    RewriteRuleSubtreeStream stream_javadoc = new RewriteRuleSubtreeStream(adaptor, "rule javadoc");
    RewriteRuleSubtreeStream stream_property = new RewriteRuleSubtreeStream(adaptor, "rule property");
    RewriteRuleSubtreeStream stream_option = new RewriteRuleSubtreeStream(adaptor, "rule option");
    try {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
      int alt9 = 2;
=======
>>>>>>> Unknown file: This is a bug in JDime.

      int LA9_0 = input.LA(1);

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
      if ((LA9_0 == DATA || LA9_0 == ENUM)) {
        alt9 = 1;
      } else {
        if ((LA9_0 == ID || LA9_0 == TYPE || LA9_0 == 30 || LA9_0 == 32 || LA9_0 == 39 || (LA9_0 >= 41 && LA9_0 <= 42))) {
          alt9 = 2;
        } else {
          if (state.backtracking > 0) {
            state.failed = true;
            return retval;
          }
          NoViableAltException nvae = new NoViableAltException("", 9, 0, input);
          throw nvae;
        }
      }
=======
      switch (input.LA(1)) {
        case DATA:
        case ENUM:
        {
          alt8 = 1;
        }
        break;
        case ID:
        case TYPE:
        case 29:
        case 36:
        case 38:
        case 39:
        {
          alt8 = 2;
        }
        break;
        default:
        if (state.backtracking > 0) {
          state.failed = true;
          return retval;
        }
        NoViableAltException nvae = new NoViableAltException("", 8, 0, input);
        throw nvae;
      }
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java

      switch (alt9) {
        case 1:
        {
          root_0 = (Object) adaptor.nil();
          pushFollow(FOLLOW_entry_in_data_entry242);
          entry23 = entry();
          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            adaptor.addChild(root_0, entry23.getTree());
          }
        }
        break;
        case 2:
        {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          int alt7 = 2;
=======
          loop7:
          do {
            int alt7 = 2;
            switch (input.LA(1)) {
              case 29:
              {
                alt7 = 1;
              }
              break;
            }
            switch (alt7) {
              case 1:
              {
                pushFollow(FOLLOW_option_in_data_entry247);
                option24 = option();
                state._fsp--;
                if (state.failed) {
                  return retval;
                }
                if (state.backtracking == 0) {
                  stream_option.add(option24.getTree());
                }
              }
              break;
              default:
              break loop7;
            }
          } while(true);
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java

          int LA7_0 = input.LA(1);
          if ((LA7_0 == 30)) {
            alt7 = 1;
          }
          switch (alt7) {
            case 1:
            {
              pushFollow(FOLLOW_javadoc_in_data_entry247);
              javadoc24 = javadoc();
              state._fsp--;
              if (state.failed) {
                return retval;
              }
              if (state.backtracking == 0) {
                stream_javadoc.add(javadoc24.getTree());
              }
            }
            break;
          }
          loop8:
          do {
            int alt8 = 2;
            int LA8_0 = input.LA(1);
            if ((LA8_0 == 32)) {
              alt8 = 1;
            }
            switch (alt8) {
              case 1:
              {
                pushFollow(FOLLOW_option_in_data_entry250);
                option25 = option();
                state._fsp--;
                if (state.failed) {
                  return retval;
                }
                if (state.backtracking == 0) {
                  stream_option.add(option25.getTree());
                }
              }
              break;
              default:
              break loop8;
            }
          } while(true);
          pushFollow(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_property_in_data_entry253
=======
          FOLLOW_property_in_data_entry250
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          property26 = property();
          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_property.add(property26.getTree());
          }
          if (state.backtracking == 0) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
            root_0 = (Object) adaptor.nil();
            {
              {
                Object root_1 = (Object) adaptor.nil();
                root_1 = (Object) adaptor.becomeRoot(stream_property.nextNode(), root_1);
                if (stream_javadoc.hasNext()) {
                  adaptor.addChild(root_1, stream_javadoc.nextTree());
                }
                stream_javadoc.reset();
                while (stream_option.hasNext()) {
                  adaptor.addChild(root_1, stream_option.nextTree());
                }
                stream_option.reset();
                adaptor.addChild(root_0, root_1);
              }
            }
            retval.tree = root_0;
          }
        }
        break;
      }
      retval.stop = input.LT(-1);
      if (state.backtracking == 0) {
        retval.tree = (Object) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
      retval.tree = (Object) adaptor.errorNode(input, retval.start, input.LT(-1), re);
    } finally {
    }
    return retval;
  }

  public static class javadoc_return extends ParserRuleReturnScope {
    Object tree;

    public Object getTree() {
      return tree;
    }
  }



  public static class option_return extends ParserRuleReturnScope {
    Object tree;

    public Object getTree() {
      return tree;
    }
  }

  public final GuiceyDataParser.javadoc_return javadoc() throws RecognitionException {
    GuiceyDataParser.javadoc_return retval = new GuiceyDataParser.javadoc_return();
    retval.start = input.LT(1);
    Object root_0 = null;
    Token string_literal27 = null;
    Token COMMENT28 = null;
    Token string_literal29 = null;
    Object string_literal27_tree = null;
    Object COMMENT28_tree = null;
    Object string_literal29_tree = null;
    RewriteRuleTokenStream stream_30 = new RewriteRuleTokenStream(adaptor, "token 30");
    RewriteRuleTokenStream stream_31 = new RewriteRuleTokenStream(adaptor, "token 31");
    RewriteRuleTokenStream stream_COMMENT = new RewriteRuleTokenStream(adaptor, "token COMMENT");
    try {
      {
        string_literal27 = (Token) match(input, 30, FOLLOW_30_in_javadoc276);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_30.add(string_literal27);
        }
        COMMENT28 = (Token) match(input, COMMENT, FOLLOW_COMMENT_in_javadoc278);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_COMMENT.add(COMMENT28);
        }
        string_literal29 = (Token) match(input, 31, FOLLOW_31_in_javadoc280);
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_31.add(string_literal29);
        }
        if (state.backtracking == 0) {
          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
          root_0 = (Object) adaptor.nil();
          {
            {
              Object root_1 = (Object) adaptor.nil();
              root_1 = (Object) adaptor.becomeRoot(stream_COMMENT.nextNode(), root_1);
              adaptor.addChild(root_0, root_1);
            }
          }
          retval.tree = root_0;
        }
      }
      retval.stop = input.LT(-1);
      if (state.backtracking == 0) {
        retval.tree = (Object) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
      retval.tree = (Object) adaptor.errorNode(input, retval.start, input.LT(-1), re);
    } finally {
    }
    return retval;
  }



  public final GuiceyDataParser.option_return option() throws RecognitionException {
    GuiceyDataParser.option_return retval = new GuiceyDataParser.option_return();
    retval.start = input.LT(1);
    Object root_0 = null;
    Token char_literal30 = null;
    Token ID31 = null;
    Token char_literal32 = null;
    Token char_literal33 = null;
    Token ID34 = null;
    Token char_literal35 = null;
    Token string_literal37 = null;
    Token char_literal38 = null;
    Token ID39 = null;
    Token char_literal40 = null;
    Token char_literal42 = null;
    Token string_literal44 = null;
    GuiceyDataParser.value_return value36 = null;
    GuiceyDataParser.pair_return pair41 = null;
    GuiceyDataParser.pair_return pair43 = null;
    Object char_literal30_tree = null;
    Object ID31_tree = null;
    Object char_literal32_tree = null;
    Object char_literal33_tree = null;
    Object ID34_tree = null;
    Object char_literal35_tree = null;
    Object string_literal37_tree = null;
    Object char_literal38_tree = null;
    Object ID39_tree = null;
    Object char_literal40_tree = null;
    Object char_literal42_tree = null;
    Object string_literal44_tree = null;
    RewriteRuleTokenStream stream_32 = new RewriteRuleTokenStream(adaptor, "token 32");
    RewriteRuleTokenStream stream_35 = new RewriteRuleTokenStream(adaptor, "token 35");
    RewriteRuleTokenStream stream_ID = new RewriteRuleTokenStream(adaptor, "token ID");

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
    RewriteRuleTokenStream stream_33 = new RewriteRuleTokenStream(adaptor, "token 33");
=======
>>>>>>> Unknown file: This is a bug in JDime.

    RewriteRuleTokenStream stream_34 = new RewriteRuleTokenStream(adaptor, "token 34");
    RewriteRuleTokenStream stream_29 = new RewriteRuleTokenStream(adaptor, "token 29");
    RewriteRuleSubtreeStream stream_pair = new RewriteRuleSubtreeStream(adaptor, "rule pair");
    RewriteRuleSubtreeStream stream_value = new RewriteRuleSubtreeStream(adaptor, "rule value");
    try {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
      int alt11 = 3;
=======
>>>>>>> Unknown file: This is a bug in JDime.

      int LA11_0 = input.LA(1);

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
      if ((LA11_0 == 32)) {
        int LA11_1 = input.LA(2);
        if ((LA11_1 == ID)) {
          int LA11_2 = input.LA(3);
          if ((LA11_2 == 33)) {
            alt11 = 1;
          } else {
            if ((LA11_2 == 34)) {
              int LA11_4 = input.LA(4);
              if ((LA11_4 == ID)) {
                alt11 = 3;
              } else {
                if (((LA11_4 >= INT && LA11_4 <= STRING))) {
                  alt11 = 2;
                } else {
                  if (state.backtracking > 0) {
                    state.failed = true;
                    return retval;
                  }
                  NoViableAltException nvae = new NoViableAltException("", 11, 4, input);
                  throw nvae;
                }
              }
            } else {
              if (state.backtracking > 0) {
                state.failed = true;
                return retval;
              }
              NoViableAltException nvae = new NoViableAltException("", 11, 2, input);
              throw nvae;
            }
          }
        } else {
          if (state.backtracking > 0) {
            state.failed = true;
            return retval;
          }
          NoViableAltException nvae = new NoViableAltException("", 11, 1, input);
          throw nvae;
        }
      } else {
        if (state.backtracking > 0) {
          state.failed = true;
          return retval;
        }
        NoViableAltException nvae = new NoViableAltException("", 11, 0, input);
        throw nvae;
      }
=======
      switch (input.LA(1)) {
        case 29:
        {
          switch (input.LA(2)) {
            case ID:
            {
              switch (input.LA(3)) {
                case 30:
                {
                  alt10 = 1;
                }
                break;
                case 31:
                {
                  switch (input.LA(4)) {
                    case ID:
                    {
                      alt10 = 3;
                    }
                    break;
                    case INT:
                    case FLOAT:
                    case STRING:
                    {
                      alt10 = 2;
                    }
                    break;
                    default:
                    if (state.backtracking > 0) {
                      state.failed = true;
                      return retval;
                    }
                    NoViableAltException nvae = new NoViableAltException("", 10, 4, input);
                    throw nvae;
                  }
                }
                break;
                default:
                if (state.backtracking > 0) {
                  state.failed = true;
                  return retval;
                }
                NoViableAltException nvae = new NoViableAltException("", 10, 2, input);
                throw nvae;
              }
            }
            break;
            default:
            if (state.backtracking > 0) {
              state.failed = true;
              return retval;
            }
            NoViableAltException nvae = new NoViableAltException("", 10, 1, input);
            throw nvae;
          }
        }
        break;
        default:
        if (state.backtracking > 0) {
          state.failed = true;
          return retval;
        }
        NoViableAltException nvae = new NoViableAltException("", 10, 0, input);
        throw nvae;
      }
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java

      switch (alt11) {
        case 1:
        {
          char_literal30 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          32
=======
          29
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_32_in_option312
=======
          FOLLOW_29_in_option270
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_32
=======
            stream_29
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(char_literal30);
          }
          ID31 = (Token) match(input, ID, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_ID_in_option314
=======
          FOLLOW_ID_in_option272
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_ID.add(ID31);
          }
          char_literal32 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          33
=======
          30
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_33_in_option316
=======
          FOLLOW_30_in_option274
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_33
=======
            stream_30
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(char_literal32);
          }
          if (state.backtracking == 0) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
            root_0 = (Object) adaptor.nil();
            {
              {
                Object root_1 = (Object) adaptor.nil();
                root_1 = (Object) adaptor.becomeRoot((Object) adaptor.create(OPTION, "OPTION"), root_1);
                adaptor.addChild(root_1, stream_ID.nextNode());
                adaptor.addChild(root_0, root_1);
              }
            }
            retval.tree = root_0;
          }
        }
        break;
        case 2:
        {
          char_literal33 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          32
=======
          29
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_32_in_option329
=======
          FOLLOW_29_in_option287
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_32
=======
            stream_29
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(char_literal33);
          }
          ID34 = (Token) match(input, ID, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_ID_in_option331
=======
          FOLLOW_ID_in_option289
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_ID.add(ID34);
          }
          char_literal35 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          34
=======
          31
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_34_in_option333
=======
          FOLLOW_31_in_option291
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_34
=======
            stream_31
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(char_literal35);
          }
          pushFollow(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_value_in_option335
=======
          FOLLOW_value_in_option293
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          value36 = value();
          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_value.add(value36.getTree());
          }
          string_literal37 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          35
=======
          32
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_35_in_option337
=======
          FOLLOW_32_in_option295
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_35
=======
            stream_32
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(string_literal37);
          }
          if (state.backtracking == 0) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
            root_0 = (Object) adaptor.nil();
            {
              {
                Object root_1 = (Object) adaptor.nil();
                root_1 = (Object) adaptor.becomeRoot((Object) adaptor.create(OPTION, "OPTION"), root_1);
                adaptor.addChild(root_1, stream_ID.nextNode());
                adaptor.addChild(root_1, stream_value.nextTree());
                adaptor.addChild(root_0, root_1);
              }
            }
            retval.tree = root_0;
          }
        }
        break;
        case 3:
        {
          char_literal38 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          32
=======
          29
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_32_in_option353
=======
          FOLLOW_29_in_option311
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_32
=======
            stream_29
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(char_literal38);
          }
          ID39 = (Token) match(input, ID, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_ID_in_option355
=======
          FOLLOW_ID_in_option313
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_ID.add(ID39);
          }
          char_literal40 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          34
=======
          31
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_34_in_option357
=======
          FOLLOW_31_in_option315
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_34
=======
            stream_31
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(char_literal40);
          }

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          loop10:
          do {
            int alt10 = 2;
            int LA10_0 = input.LA(1);
            if ((LA10_0 == ID)) {
              int LA10_1 = input.LA(2);
              if ((LA10_1 == 36)) {
                int LA10_2 = input.LA(3);
                if (((LA10_2 >= INT && LA10_2 <= STRING))) {
                  int LA10_3 = input.LA(4);
                  if ((LA10_3 == 29)) {
                    alt10 = 1;
                  }
                }
              }
            }
            switch (alt10) {
              case 1:
              {
                pushFollow(FOLLOW_pair_in_option360);
                pair41 = pair();
                state._fsp--;
                if (state.failed) {
                  return retval;
                }
                if (state.backtracking == 0) {
                  stream_pair.add(pair41.getTree());
                }
                char_literal42 = (Token) match(input, 29, FOLLOW_29_in_option362);
                if (state.failed) {
                  return retval;
                }
                if (state.backtracking == 0) {
                  stream_29.add(char_literal42);
                }
              }
              break;
              default:
              break loop10;
            }
          } while(true);
=======
          loop9:
          do {
            int alt9 = 2;
            switch (input.LA(1)) {
              case ID:
              {
                switch (input.LA(2)) {
                  case 33:
                  {
                    switch (input.LA(3)) {
                      case INT:
                      case FLOAT:
                      case STRING:
                      {
                        switch (input.LA(4)) {
                          case 28:
                          {
                            alt9 = 1;
                          }
                          break;
                        }
                      }
                      break;
                    }
                  }
                  break;
                }
              }
              break;
            }
            switch (alt9) {
              case 1:
              {
                pushFollow(FOLLOW_pair_in_option318);
                pair37 = pair();
                state._fsp--;
                if (state.failed) {
                  return retval;
                }
                if (state.backtracking == 0) {
                  stream_pair.add(pair37.getTree());
                }
                char_literal38 = (Token) match(input, 28, FOLLOW_28_in_option320);
                if (state.failed) {
                  return retval;
                }
                if (state.backtracking == 0) {
                  stream_28.add(char_literal38);
                }
              }
              break;
              default:
              break loop9;
            }
          } while(true);
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java

          pushFollow(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_pair_in_option366
=======
          FOLLOW_pair_in_option324
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          pair43 = pair();
          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_pair.add(pair43.getTree());
          }
          string_literal44 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          35
=======
          32
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_35_in_option368
=======
          FOLLOW_32_in_option326
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_35
=======
            stream_32
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(string_literal44);
          }
          if (state.backtracking == 0) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
            root_0 = (Object) adaptor.nil();
            {
              {
                Object root_1 = (Object) adaptor.nil();
                root_1 = (Object) adaptor.becomeRoot((Object) adaptor.create(OPTION, "OPTION"), root_1);
                adaptor.addChild(root_1, stream_ID.nextNode());
                if (!(stream_pair.hasNext())) {
                  throw new RewriteEarlyExitException();
                }
                while (stream_pair.hasNext()) {
                  adaptor.addChild(root_1, stream_pair.nextTree());
                }
                stream_pair.reset();
                adaptor.addChild(root_0, root_1);
              }
            }
            retval.tree = root_0;
          }
        }
        break;
      }
      retval.stop = input.LT(-1);
      if (state.backtracking == 0) {
        retval.tree = (Object) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
      retval.tree = (Object) adaptor.errorNode(input, retval.start, input.LT(-1), re);
    } finally {
    }
    return retval;
  }

  public static class pair_return extends ParserRuleReturnScope {
    Object tree;

    public Object getTree() {
      return tree;
    }
  }



  public final GuiceyDataParser.pair_return pair() throws RecognitionException {
    GuiceyDataParser.pair_return retval = new GuiceyDataParser.pair_return();
    retval.start = input.LT(1);
    Object root_0 = null;
    Token char_literal46 = null;
    GuiceyDataParser.key_return key45 = null;
    GuiceyDataParser.value_return value47 = null;
    Object char_literal46_tree = null;
    RewriteRuleTokenStream 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
    stream_36 = new RewriteRuleTokenStream(adaptor, "token 36")
=======
    stream_33 = new RewriteRuleTokenStream(adaptor, "token 33")
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
    ;
    RewriteRuleSubtreeStream stream_value = new RewriteRuleSubtreeStream(adaptor, "rule value");
    RewriteRuleSubtreeStream stream_key = new RewriteRuleSubtreeStream(adaptor, "rule key");
    try {
      {
        pushFollow(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
        FOLLOW_key_in_pair391
=======
        FOLLOW_key_in_pair349
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
        );
        key45 = key();
        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_key.add(key45.getTree());
        }
        char_literal46 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
        36
=======
        33
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
        , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
        FOLLOW_36_in_pair393
=======
        FOLLOW_33_in_pair351
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
        );
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          stream_36
=======
          stream_33
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          .add(char_literal46);
        }
        pushFollow(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
        FOLLOW_value_in_pair395
=======
        FOLLOW_value_in_pair353
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
        );
        value47 = value();
        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          stream_value.add(value47.getTree());
        }
        if (state.backtracking == 0) {
          retval.tree = root_0;
          RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
          root_0 = (Object) adaptor.nil();
          {
            {
              Object root_1 = (Object) adaptor.nil();
              root_1 = (Object) adaptor.becomeRoot((Object) adaptor.create(PAIR, "PAIR"), root_1);
              adaptor.addChild(root_1, stream_key.nextTree());
              adaptor.addChild(root_1, stream_value.nextTree());
              adaptor.addChild(root_0, root_1);
            }
          }
          retval.tree = root_0;
        }
      }
      retval.stop = input.LT(-1);
      if (state.backtracking == 0) {
        retval.tree = (Object) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
      retval.tree = (Object) adaptor.errorNode(input, retval.start, input.LT(-1), re);
    } finally {
    }
    return retval;
  }

  public static class key_return extends ParserRuleReturnScope {
    Object tree;

    public Object getTree() {
      return tree;
    }
  }



  public final GuiceyDataParser.key_return key() throws RecognitionException {
    GuiceyDataParser.key_return retval = new GuiceyDataParser.key_return();
    retval.start = input.LT(1);
    Object root_0 = null;
    Token ID48 = null;
    Object ID48_tree = null;
    try {
      {
        root_0 = (Object) adaptor.nil();
        ID48 = (Token) match(input, ID, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
        FOLLOW_ID_in_key417
=======
        FOLLOW_ID_in_key375
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
        );
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          ID48_tree = (Object) adaptor.create(ID48);
          adaptor.addChild(root_0, ID48_tree);
        }
      }
      retval.stop = input.LT(-1);
      if (state.backtracking == 0) {
        retval.tree = (Object) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
      retval.tree = (Object) adaptor.errorNode(input, retval.start, input.LT(-1), re);
    } finally {
    }
    return retval;
  }

  public static class value_return extends ParserRuleReturnScope {
    Object tree;

    public Object getTree() {
      return tree;
    }
  }



  public final GuiceyDataParser.value_return value() throws RecognitionException {
    GuiceyDataParser.value_return retval = new GuiceyDataParser.value_return();
    retval.start = input.LT(1);
    Object root_0 = null;
    Token set49 = null;
    Object set49_tree = null;
    try {
      {
        root_0 = (Object) adaptor.nil();
        set49 = (Token) input.LT(1);
        if ((input.LA(1) >= INT && input.LA(1) <= STRING)) {
          input.consume();
          if (state.backtracking == 0) {
            adaptor.addChild(root_0, (Object) adaptor.create(set49));
          }
          state.errorRecovery = false;
          state.failed = false;
        } else {
          if (state.backtracking > 0) {
            state.failed = true;
            return retval;
          }
          MismatchedSetException mse = new MismatchedSetException(null, input);
          throw mse;
        }
      }
      retval.stop = input.LT(-1);
      if (state.backtracking == 0) {
        retval.tree = (Object) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
      retval.tree = (Object) adaptor.errorNode(input, retval.start, input.LT(-1), re);
    } finally {
    }
    return retval;
  }

  public static class property_return extends ParserRuleReturnScope {
    Object tree;

    public Object getTree() {
      return tree;
    }
  }



  public final GuiceyDataParser.property_return property() throws RecognitionException {
    GuiceyDataParser.property_return retval = new GuiceyDataParser.property_return();
    retval.start = input.LT(1);
    Object root_0 = null;
    Token string_literal51 = null;
    Token ID53 = null;
    Token char_literal54 = null;
    GuiceyDataParser.type_return type50 = null;
    GuiceyDataParser.type_return type52 = null;
    Object string_literal51_tree = null;
    Object ID53_tree = null;
    Object char_literal54_tree = null;
    RewriteRuleTokenStream stream_ID = new RewriteRuleTokenStream(adaptor, "token ID");
    RewriteRuleTokenStream 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
    stream_37 = new RewriteRuleTokenStream(adaptor, "token 37")
=======
    stream_35 = new RewriteRuleTokenStream(adaptor, "token 35")
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
    ;
    RewriteRuleTokenStream stream_38 = new RewriteRuleTokenStream(adaptor, "token 38");
    RewriteRuleSubtreeStream stream_type = new RewriteRuleSubtreeStream(adaptor, "rule type");
    try {
      int alt12 = 2;
      switch (input.LA(1)) {
        case 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
        39
=======
        36
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
        :
        {
          int LA12_1 = input.LA(2);
          if ((synpred15_GuiceyData())) {
            alt12 = 1;
          } else {
            if ((true)) {
              alt12 = 2;
            } else {
              if (state.backtracking > 0) {
                state.failed = true;
                return retval;
              }
              NoViableAltException nvae = new NoViableAltException("", 12, 1, input);
              throw nvae;
            }
          }
        }
        break;
        case 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
        41
=======
        38
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
        :
        {
          int LA12_2 = input.LA(2);
          if ((synpred15_GuiceyData())) {
            alt12 = 1;
          } else {
            if ((true)) {
              alt12 = 2;
            } else {
              if (state.backtracking > 0) {
                state.failed = true;
                return retval;
              }
              NoViableAltException nvae = new NoViableAltException("", 12, 2, input);
              throw nvae;
            }
          }
        }
        break;
        case 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
        42
=======
        39
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
        :
        {
          int LA12_3 = input.LA(2);
          if ((synpred15_GuiceyData())) {
            alt12 = 1;
          } else {
            if ((true)) {
              alt12 = 2;
            } else {
              if (state.backtracking > 0) {
                state.failed = true;
                return retval;
              }
              NoViableAltException nvae = new NoViableAltException("", 12, 3, input);
              throw nvae;
            }
          }
        }
        break;
        case TYPE:
        {
          int LA12_4 = input.LA(2);
          if ((synpred15_GuiceyData())) {
            alt12 = 1;
          } else {
            if ((true)) {
              alt12 = 2;
            } else {
              if (state.backtracking > 0) {
                state.failed = true;
                return retval;
              }
              NoViableAltException nvae = new NoViableAltException("", 12, 4, input);
              throw nvae;
            }
          }
        }
        break;
        case ID:
        {
          int LA12_5 = input.LA(2);
          if ((synpred15_GuiceyData())) {
            alt12 = 1;
          } else {
            if ((true)) {
              alt12 = 2;
            } else {
              if (state.backtracking > 0) {
                state.failed = true;
                return retval;
              }
              NoViableAltException nvae = new NoViableAltException("", 12, 5, input);
              throw nvae;
            }
          }
        }
        break;
        default:
        if (state.backtracking > 0) {
          state.failed = true;
          return retval;
        }
        NoViableAltException nvae = new NoViableAltException("", 12, 0, input);
        throw nvae;
      }
      switch (alt12) {
        case 1:
        {
          pushFollow(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_type_in_property451
=======
          FOLLOW_type_in_property409
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          type50 = type();
          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_type.add(type50.getTree());
          }
          string_literal51 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          37
=======
          34
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_37_in_property453
=======
          FOLLOW_34_in_property411
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_37
=======
            stream_34
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(string_literal51);
          }
          if (state.backtracking == 0) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
            root_0 = (Object) adaptor.nil();
            {
              {
                Object root_1 = (Object) adaptor.nil();
                root_1 = (Object) adaptor.becomeRoot((Object) adaptor.create(PROPERTY, "PROPERTY"), root_1);
                adaptor.addChild(root_1, (Object) adaptor.create(DATA, "DATA"));
                adaptor.addChild(root_1, stream_type.nextTree());
                adaptor.addChild(root_0, root_1);
              }
            }
            retval.tree = root_0;
          }
        }
        break;
        case 2:
        {
          pushFollow(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_type_in_property468
=======
          FOLLOW_type_in_property426
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          type52 = type();
          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_type.add(type52.getTree());
          }
          ID53 = (Token) match(input, ID, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_ID_in_property470
=======
          FOLLOW_ID_in_property428
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_ID.add(ID53);
          }
          char_literal54 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          38
=======
          35
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_38_in_property472
=======
          FOLLOW_35_in_property430
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_38
=======
            stream_35
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(char_literal54);
          }
          if (state.backtracking == 0) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
            root_0 = (Object) adaptor.nil();
            {
              {
                Object root_1 = (Object) adaptor.nil();
                root_1 = (Object) adaptor.becomeRoot((Object) adaptor.create(PROPERTY, "PROPERTY"), root_1);
                adaptor.addChild(root_1, stream_ID.nextNode());
                adaptor.addChild(root_1, stream_type.nextTree());
                adaptor.addChild(root_0, root_1);
              }
            }
            retval.tree = root_0;
          }
        }
        break;
      }
      retval.stop = input.LT(-1);
      if (state.backtracking == 0) {
        retval.tree = (Object) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
      retval.tree = (Object) adaptor.errorNode(input, retval.start, input.LT(-1), re);
    } finally {
    }
    return retval;
  }

  public static class key_type_return extends ParserRuleReturnScope {
    Object tree;

    public Object getTree() {
      return tree;
    }
  }



  public final GuiceyDataParser.key_type_return key_type() throws RecognitionException {
    GuiceyDataParser.key_type_return retval = new GuiceyDataParser.key_type_return();
    retval.start = input.LT(1);
    Object root_0 = null;
    GuiceyDataParser.type_return type55 = null;
    try {
      {
        root_0 = (Object) adaptor.nil();
        pushFollow(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
        FOLLOW_type_in_key_type493
=======
        FOLLOW_type_in_key_type451
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
        );
        type55 = type();
        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          adaptor.addChild(root_0, type55.getTree());
        }
      }
      retval.stop = input.LT(-1);
      if (state.backtracking == 0) {
        retval.tree = (Object) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
      retval.tree = (Object) adaptor.errorNode(input, retval.start, input.LT(-1), re);
    } finally {
    }
    return retval;
  }

  public static class value_type_return extends ParserRuleReturnScope {
    Object tree;

    public Object getTree() {
      return tree;
    }
  }



  public final GuiceyDataParser.value_type_return value_type() throws RecognitionException {
    GuiceyDataParser.value_type_return retval = new GuiceyDataParser.value_type_return();
    retval.start = input.LT(1);
    Object root_0 = null;
    GuiceyDataParser.type_return type56 = null;
    try {
      {
        root_0 = (Object) adaptor.nil();
        pushFollow(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
        FOLLOW_type_in_value_type504
=======
        FOLLOW_type_in_value_type462
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
        );
        type56 = type();
        state._fsp--;
        if (state.failed) {
          return retval;
        }
        if (state.backtracking == 0) {
          adaptor.addChild(root_0, type56.getTree());
        }
      }
      retval.stop = input.LT(-1);
      if (state.backtracking == 0) {
        retval.tree = (Object) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
      retval.tree = (Object) adaptor.errorNode(input, retval.start, input.LT(-1), re);
    } finally {
    }
    return retval;
  }

  public static class type_return extends ParserRuleReturnScope {
    Object tree;

    public Object getTree() {
      return tree;
    }
  }



  public final GuiceyDataParser.type_return type() throws RecognitionException {
    GuiceyDataParser.type_return retval = new GuiceyDataParser.type_return();
    retval.start = input.LT(1);
    Object root_0 = null;
    Token string_literal57 = null;
    Token char_literal59 = null;
    Token char_literal61 = null;
    Token string_literal62 = null;
    Token char_literal64 = null;
    Token string_literal65 = null;
    Token char_literal67 = null;
    Token TYPE68 = null;
    Token ID69 = null;
    Token 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
    ID70 = null
=======
    ID66 = null
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
    ;
    Token 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
    char_literal71 = null
=======
    char_literal67 = null
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
    ;
    Token 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
    char_literal73 = null
=======
    char_literal69 = null
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
    ;
    Token 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
    char_literal75 = null
=======
    char_literal71 = null
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
    ;
    GuiceyDataParser.type_return type58 = null;
    GuiceyDataParser.type_return type60 = null;
    GuiceyDataParser.type_return type63 = null;
    GuiceyDataParser.type_return type66 = null;
    GuiceyDataParser.type_return 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
    type72 = null
=======
    type68 = null
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
    ;
    GuiceyDataParser.type_return 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
    type74 = null
=======
    type70 = null
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
    ;
    Object string_literal57_tree = null;
    Object char_literal59_tree = null;
    Object char_literal61_tree = null;
    Object string_literal62_tree = null;
    Object char_literal64_tree = null;
    Object string_literal65_tree = null;
    Object char_literal67_tree = null;
    Object TYPE68_tree = null;
    Object ID69_tree = null;
    Object 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
    ID70_tree = null
=======
    ID66_tree = null
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
    ;
    Object 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
    char_literal71_tree = null
=======
    char_literal67_tree = null
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
    ;
    Object 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
    char_literal73_tree = null
=======
    char_literal69_tree = null
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
    ;
    Object 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
    char_literal75_tree = null
=======
    char_literal71_tree = null
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
    ;
    RewriteRuleTokenStream stream_43 = new RewriteRuleTokenStream(adaptor, "token 43");
    RewriteRuleTokenStream stream_42 = new RewriteRuleTokenStream(adaptor, "token 42");

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
    RewriteRuleTokenStream stream_41 = new RewriteRuleTokenStream(adaptor, "token 41");
=======
>>>>>>> Unknown file: This is a bug in JDime.

    RewriteRuleTokenStream stream_40 = new RewriteRuleTokenStream(adaptor, "token 40");
    RewriteRuleTokenStream stream_ID = new RewriteRuleTokenStream(adaptor, "token ID");
    RewriteRuleTokenStream stream_39 = new RewriteRuleTokenStream(adaptor, "token 39");
    RewriteRuleTokenStream stream_TYPE = new RewriteRuleTokenStream(adaptor, "token TYPE");
    RewriteRuleTokenStream stream_28 = new RewriteRuleTokenStream(adaptor, "token 28");
    RewriteRuleTokenStream stream_29 = new RewriteRuleTokenStream(adaptor, "token 29");
    RewriteRuleSubtreeStream stream_type = new RewriteRuleSubtreeStream(adaptor, "rule type");
    try {
      int 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
      alt14 = 6
=======
      alt13 = 6
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
      ;
      switch (input.LA(1)) {
        case 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
        39
=======
        36
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
        :
        {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          alt14
=======
          alt13
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
           = 1;
        }
        break;
        case 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
        41
=======
        38
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
        :
        {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          alt14
=======
          alt13
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
           = 2;
        }
        break;
        case 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
        42
=======
        39
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
        :
        {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          alt14
=======
          alt13
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
           = 3;
        }
        break;
        case TYPE:
        {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          alt14
=======
          alt13
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
           = 4;
        }
        break;
        case ID:
        {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          int LA14_5 = input.LA(2);
=======
          switch (input.LA(2)) {
            case 40:
            {
              alt13 = 6;
            }
            break;
            case EOF:
            case ID:
            case 28:
            case 34:
            case 37:
            {
              alt13 = 5;
            }
            break;
            default:
            if (state.backtracking > 0) {
              state.failed = true;
              return retval;
            }
            NoViableAltException nvae = new NoViableAltException("", 13, 5, input);
            throw nvae;
          }
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java

          if ((LA14_5 == 43)) {
            alt14 = 6;
          } else {
            if ((LA14_5 == EOF || LA14_5 == ID || LA14_5 == 29 || LA14_5 == 37 || LA14_5 == 40)) {
              alt14 = 5;
            } else {
              if (state.backtracking > 0) {
                state.failed = true;
                return retval;
              }
              NoViableAltException nvae = new NoViableAltException("", 14, 5, input);
              throw nvae;
            }
          }
        }
        break;
        default:
        if (state.backtracking > 0) {
          state.failed = true;
          return retval;
        }
        NoViableAltException nvae = new NoViableAltException("", 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
        14
=======
        13
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
        , 0, input);
        throw nvae;
      }
      switch (
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
      alt14
=======
      alt13
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
      ) {
        case 1:
        {
          string_literal57 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          39
=======
          36
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_39_in_type515
=======
          FOLLOW_36_in_type473
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_39
=======
            stream_36
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(string_literal57);
          }
          pushFollow(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_type_in_type517
=======
          FOLLOW_type_in_type475
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          type58 = type();
          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_type.add(type58.getTree());
          }
          char_literal59 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          29
=======
          28
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_29_in_type519
=======
          FOLLOW_28_in_type477
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_29
=======
            stream_28
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(char_literal59);
          }
          pushFollow(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_type_in_type521
=======
          FOLLOW_type_in_type479
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          type60 = type();
          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_type.add(type60.getTree());
          }
          char_literal61 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          40
=======
          37
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_40_in_type523
=======
          FOLLOW_37_in_type481
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_40
=======
            stream_37
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(char_literal61);
          }
          if (state.backtracking == 0) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
            root_0 = (Object) adaptor.nil();
            {
              adaptor.addChild(root_0, (Object) adaptor.create(TYPE_MAP, "TYPE_MAP"));
              if (!(stream_type.hasNext())) {
                throw new RewriteEarlyExitException();
              }
              while (stream_type.hasNext()) {
                adaptor.addChild(root_0, stream_type.nextTree());
              }
              stream_type.reset();
            }
            retval.tree = root_0;
          }
        }
        break;
        case 2:
        {
          string_literal62 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          41
=======
          38
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_41_in_type535
=======
          FOLLOW_38_in_type493
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_41
=======
            stream_38
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(string_literal62);
          }
          pushFollow(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_type_in_type537
=======
          FOLLOW_type_in_type495
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          type63 = type();
          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_type.add(type63.getTree());
          }
          char_literal64 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          40
=======
          37
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_40_in_type539
=======
          FOLLOW_37_in_type497
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_40
=======
            stream_37
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(char_literal64);
          }
          if (state.backtracking == 0) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
            root_0 = (Object) adaptor.nil();
            {
              adaptor.addChild(root_0, (Object) adaptor.create(TYPE_SET, "TYPE_SET"));
              adaptor.addChild(root_0, stream_type.nextTree());
            }
            retval.tree = root_0;
          }
        }
        break;
        case 3:
        {
          string_literal65 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          42
=======
          39
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_42_in_type550
=======
          FOLLOW_39_in_type508
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_42
=======
            stream_39
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(string_literal65);
          }
          pushFollow(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_type_in_type552
=======
          FOLLOW_type_in_type510
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          type66 = type();
          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_type.add(type66.getTree());
          }
          char_literal67 = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          40
=======
          37
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_40_in_type554
=======
          FOLLOW_37_in_type512
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_40
=======
            stream_37
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(char_literal67);
          }
          if (state.backtracking == 0) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
            root_0 = (Object) adaptor.nil();
            {
              adaptor.addChild(root_0, (Object) adaptor.create(TYPE_LIST, "TYPE_LIST"));
              adaptor.addChild(root_0, stream_type.nextTree());
            }
            retval.tree = root_0;
          }
        }
        break;
        case 4:
        {
          TYPE68 = (Token) match(input, TYPE, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_TYPE_in_type565
=======
          FOLLOW_TYPE_in_type523
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_TYPE.add(TYPE68);
          }
          if (state.backtracking == 0) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
            root_0 = (Object) adaptor.nil();
            {
              adaptor.addChild(root_0, (Object) adaptor.create(TYPE_PRIMITIVE, "TYPE_PRIMITIVE"));
              adaptor.addChild(root_0, stream_TYPE.nextNode());
            }
            retval.tree = root_0;
          }
        }
        break;
        case 5:
        {
          ID69 = (Token) match(input, ID, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_ID_in_type576
=======
          FOLLOW_ID_in_type534
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_ID.add(ID69);
          }
          if (state.backtracking == 0) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
            root_0 = (Object) adaptor.nil();
            {
              adaptor.addChild(root_0, (Object) adaptor.create(TYPE_PRIMITIVE, "TYPE_PRIMITIVE"));
              adaptor.addChild(root_0, stream_ID.nextNode());
            }
            retval.tree = root_0;
          }
        }
        break;
        case 6:
        {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          ID70
=======
          ID66
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
           = (Token) match(input, ID, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_ID_in_type587
=======
          FOLLOW_ID_in_type545
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_ID.add(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            ID70
=======
            ID66
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            );
          }

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          char_literal71
=======
          char_literal67
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
           = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          43
=======
          40
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_43_in_type589
=======
          FOLLOW_40_in_type547
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_43
=======
            stream_40
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            char_literal71
=======
            char_literal67
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            );
          }
          pushFollow(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_type_in_type591
=======
          FOLLOW_type_in_type549
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          type72
=======
          type68
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
           = type();
          state._fsp--;
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {
            stream_type.add(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            type72
=======
            type68
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .getTree());
          }

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          loop13:
          do {
            int alt13 = 2;
            int LA13_0 = input.LA(1);
            if ((LA13_0 == 29)) {
              alt13 = 1;
            }
            switch (alt13) {
              case 1:
              {
                char_literal73 = (Token) match(input, 29, FOLLOW_29_in_type594);
                if (state.failed) {
                  return retval;
                }
                if (state.backtracking == 0) {
                  stream_29.add(char_literal73);
                }
                pushFollow(FOLLOW_type_in_type596);
                type74 = type();
                state._fsp--;
                if (state.failed) {
                  return retval;
                }
                if (state.backtracking == 0) {
                  stream_type.add(type74.getTree());
                }
              }
              break;
              default:
              break loop13;
            }
          } while(true);
=======
          loop12:
          do {
            int alt12 = 2;
            switch (input.LA(1)) {
              case 28:
              {
                alt12 = 1;
              }
              break;
            }
            switch (alt12) {
              case 1:
              {
                char_literal69 = (Token) match(input, 28, FOLLOW_28_in_type552);
                if (state.failed) {
                  return retval;
                }
                if (state.backtracking == 0) {
                  stream_28.add(char_literal69);
                }
                pushFollow(FOLLOW_type_in_type554);
                type70 = type();
                state._fsp--;
                if (state.failed) {
                  return retval;
                }
                if (state.backtracking == 0) {
                  stream_type.add(type70.getTree());
                }
              }
              break;
              default:
              break loop12;
            }
          } while(true);
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java


<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          char_literal75
=======
          char_literal71
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
           = (Token) match(input, 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          40
=======
          37
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          , 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
          FOLLOW_40_in_type600
=======
          FOLLOW_37_in_type558
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
          );
          if (state.failed) {
            return retval;
          }
          if (state.backtracking == 0) {

<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            stream_40
=======
            stream_37
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            .add(
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
            char_literal75
=======
            char_literal71
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
            );
          }
          if (state.backtracking == 0) {
            retval.tree = root_0;
            RewriteRuleSubtreeStream stream_retval = new RewriteRuleSubtreeStream(adaptor, "rule retval", retval != null ? retval.tree : null);
            root_0 = (Object) adaptor.nil();
            {
              adaptor.addChild(root_0, (Object) adaptor.create(PARAMETERIZED_TYPE, "PARAMETERIZED_TYPE"));
              adaptor.addChild(root_0, stream_ID.nextNode());
              if (!(stream_type.hasNext())) {
                throw new RewriteEarlyExitException();
              }
              while (stream_type.hasNext()) {
                adaptor.addChild(root_0, stream_type.nextTree());
              }
              stream_type.reset();
            }
            retval.tree = root_0;
          }
        }
        break;
      }
      retval.stop = input.LT(-1);
      if (state.backtracking == 0) {
        retval.tree = (Object) adaptor.rulePostProcessing(root_0);
        adaptor.setTokenBoundaries(retval.tree, retval.start, retval.stop);
      }
    } catch (RecognitionException re) {
      reportError(re);
      recover(input, re);
      retval.tree = (Object) adaptor.errorNode(input, retval.start, input.LT(-1), re);
    } finally {
    }
    return retval;
  }

  public final void synpred15_GuiceyData_fragment() throws RecognitionException {
    {
      pushFollow(FOLLOW_type_in_synpred15_GuiceyData451);
      type();
      state._fsp--;
      if (state.failed) {
        return;
      }
      match(input, 37, FOLLOW_37_in_synpred15_GuiceyData453);
      if (state.failed) {
        return;
      }
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  public final void synpred14_GuiceyData_fragment() throws RecognitionException {
    {
      pushFollow(FOLLOW_type_in_synpred14_GuiceyData409);
      type();
      state._fsp--;
      if (state.failed) {
        return;
      }
      match(input, 34, FOLLOW_34_in_synpred14_GuiceyData411);
      if (state.failed) {
        return;
      }
    }
  }
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java


  public final boolean synpred15_GuiceyData() {
    state.backtracking++;
    int start = input.mark();
    try {
      synpred15_GuiceyData_fragment();
    } catch (RecognitionException re) {
      System.err.println("impossible: " + re);
    }
    boolean success = !state.failed;
    input.rewind(start);
    state.backtracking--;
    state.failed = false;
    return success;
  }

  public static final BitSet FOLLOW_entry_in_start114 = new BitSet(new long[] { 0x0000000000002010L });

  public static final BitSet FOLLOW_EOF_in_start117 = new BitSet(new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_data_in_entry127 = new BitSet(new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_enumeration_in_entry132 = new BitSet(new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_DATA_in_data142 = new BitSet(new long[] { 0x0000000000004000L });

  public static final BitSet FOLLOW_ID_in_data144 = new BitSet(new long[] { 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  0x0000000004000000L
=======
  0x0000000002000000L
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
   });

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_26_in_data146 = new BitSet(new long[] { 0x0000068148086010L })
=======
  FOLLOW_25_in_data146 = new BitSet(new long[] { 0x000000D024046010L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet FOLLOW_data_entry_in_data148 = new BitSet(new long[] { 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  0x0000068148086010L
=======
  0x000000D024046010L
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
   });

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_27_in_data151 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_26_in_data151 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet FOLLOW_DATA_in_data167 = new BitSet(new long[] { 0x0000000000004000L });

  public static final BitSet FOLLOW_ID_in_data169 = new BitSet(new long[] { 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  0x0000000010000000L
=======
  0x0000000008000000L
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
   });

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_28_in_data171 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_27_in_data171 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet FOLLOW_ENUM_in_enumeration190 = new BitSet(new long[] { 0x0000000000004000L });

  public static final BitSet FOLLOW_ID_in_enumeration192 = new BitSet(new long[] { 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  0x0000000010000000L
=======
  0x0000000008000000L
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
   });

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_28_in_enumeration194 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_27_in_enumeration194 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet FOLLOW_ENUM_in_enumeration207 = new BitSet(new long[] { 0x0000000000004000L });

  public static final BitSet FOLLOW_ID_in_enumeration209 = new BitSet(new long[] { 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  0x0000000004000000L
=======
  0x0000000002000000L
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
   });

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_26_in_enumeration211 = new BitSet(new long[] { 0x0000000000004000L })
=======
  FOLLOW_25_in_enumeration211 = new BitSet(new long[] { 0x0000000000004000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet FOLLOW_ID_in_enumeration214 = new BitSet(new long[] { 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  0x0000000020000000L
=======
  0x0000000010000000L
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
   });

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_29_in_enumeration216 = new BitSet(new long[] { 0x0000000000004000L })
=======
  FOLLOW_28_in_enumeration216 = new BitSet(new long[] { 0x0000000000004000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet FOLLOW_ID_in_enumeration220 = new BitSet(new long[] { 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  0x0000000008000000L
=======
  0x0000000004000000L
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
   });

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_27_in_enumeration222 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_26_in_enumeration222 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet FOLLOW_entry_in_data_entry242 = new BitSet(new long[] { 0x0000000000000002L });

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_javadoc_in_data_entry247 = new BitSet(new long[] { 0x0000068140086010L })
=======
  FOLLOW_option_in_data_entry247 = new BitSet(new long[] { 0x000000D020046010L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_option_in_data_entry250 = new BitSet(new long[] { 0x0000068140086010L })
=======
  FOLLOW_property_in_data_entry250 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_property_in_data_entry253 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_29_in_option270 = new BitSet(new long[] { 0x0000000000004000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_30_in_javadoc276 = new BitSet(new long[] { 0x0000000000008000L })
=======
  FOLLOW_ID_in_option272 = new BitSet(new long[] { 0x0000000040000000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_COMMENT_in_javadoc278 = new BitSet(new long[] { 0x0000000080000000L })
=======
  FOLLOW_30_in_option274 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_31_in_javadoc280 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_29_in_option287 = new BitSet(new long[] { 0x0000000000004000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_32_in_option312 = new BitSet(new long[] { 0x0000000000004000L })
=======
  FOLLOW_ID_in_option289 = new BitSet(new long[] { 0x0000000080000000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_ID_in_option314 = new BitSet(new long[] { 0x0000000200000000L })
=======
  FOLLOW_31_in_option291 = new BitSet(new long[] { 0x0000000000038000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_33_in_option316 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_value_in_option293 = new BitSet(new long[] { 0x0000000100000000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_32_in_option329 = new BitSet(new long[] { 0x0000000000004000L })
=======
  FOLLOW_32_in_option295 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_ID_in_option331 = new BitSet(new long[] { 0x0000000400000000L })
=======
  FOLLOW_29_in_option311 = new BitSet(new long[] { 0x0000000000004000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_34_in_option333 = new BitSet(new long[] { 0x0000000000070000L })
=======
  FOLLOW_ID_in_option313 = new BitSet(new long[] { 0x0000000080000000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_value_in_option335 = new BitSet(new long[] { 0x0000000800000000L })
=======
  FOLLOW_31_in_option315 = new BitSet(new long[] { 0x0000000000004000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_35_in_option337 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_pair_in_option318 = new BitSet(new long[] { 0x0000000010000000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_32_in_option353 = new BitSet(new long[] { 0x0000000000004000L })
=======
  FOLLOW_28_in_option320 = new BitSet(new long[] { 0x0000000000004000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_ID_in_option355 = new BitSet(new long[] { 0x0000000400000000L })
=======
  FOLLOW_pair_in_option324 = new BitSet(new long[] { 0x0000000100000000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_34_in_option357 = new BitSet(new long[] { 0x0000000000004000L })
=======
  FOLLOW_32_in_option326 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_pair_in_option360 = new BitSet(new long[] { 0x0000000020000000L })
=======
  FOLLOW_key_in_pair349 = new BitSet(new long[] { 0x0000000200000000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_29_in_option362 = new BitSet(new long[] { 0x0000000000004000L })
=======
  FOLLOW_33_in_pair351 = new BitSet(new long[] { 0x0000000000038000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_pair_in_option366 = new BitSet(new long[] { 0x0000000800000000L })
=======
  FOLLOW_value_in_pair353 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_35_in_option368 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_ID_in_key375 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_key_in_pair391 = new BitSet(new long[] { 0x0000001000000000L })
=======
  FOLLOW_type_in_property409 = new BitSet(new long[] { 0x0000000400000000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet FOLLOW_set_in_value0 = new BitSet(new long[] { 0x0000000000000002L });

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_36_in_pair393 = new BitSet(new long[] { 0x0000000000070000L })
=======
  FOLLOW_34_in_property411 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_value_in_pair395 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_type_in_property426 = new BitSet(new long[] { 0x0000000000004000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_ID_in_key417 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_ID_in_property428 = new BitSet(new long[] { 0x0000000800000000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_type_in_property451 = new BitSet(new long[] { 0x0000002000000000L })
=======
  FOLLOW_35_in_property430 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_37_in_property453 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_type_in_key_type451 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_type_in_property468 = new BitSet(new long[] { 0x0000000000004000L })
=======
  FOLLOW_type_in_value_type462 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_ID_in_property470 = new BitSet(new long[] { 0x0000004000000000L })
=======
  FOLLOW_36_in_type473 = new BitSet(new long[] { 0x000000D000044000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_38_in_property472 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_type_in_type475 = new BitSet(new long[] { 0x0000000010000000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_type_in_key_type493 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_28_in_type477 = new BitSet(new long[] { 0x000000D000044000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_type_in_value_type504 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_type_in_type479 = new BitSet(new long[] { 0x0000002000000000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_39_in_type515 = new BitSet(new long[] { 0x0000068000084000L })
=======
  FOLLOW_37_in_type481 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_type_in_type517 = new BitSet(new long[] { 0x0000000020000000L })
=======
  FOLLOW_38_in_type493 = new BitSet(new long[] { 0x000000D000044000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_29_in_type519 = new BitSet(new long[] { 0x0000068000084000L })
=======
  FOLLOW_type_in_type495 = new BitSet(new long[] { 0x0000002000000000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_type_in_type521 = new BitSet(new long[] { 0x0000010000000000L })
=======
  FOLLOW_37_in_type497 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_40_in_type523 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_39_in_type508 = new BitSet(new long[] { 0x000000D000044000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_41_in_type535 = new BitSet(new long[] { 0x0000068000084000L })
=======
  FOLLOW_type_in_type510 = new BitSet(new long[] { 0x0000002000000000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_type_in_type537 = new BitSet(new long[] { 0x0000010000000000L })
=======
  FOLLOW_37_in_type512 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_40_in_type539 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_TYPE_in_type523 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_42_in_type550 = new BitSet(new long[] { 0x0000068000084000L })
=======
  FOLLOW_ID_in_type534 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_type_in_type552 = new BitSet(new long[] { 0x0000010000000000L })
=======
  FOLLOW_ID_in_type545 = new BitSet(new long[] { 0x0000010000000000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_40_in_type554 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_40_in_type547 = new BitSet(new long[] { 0x000000D000044000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_TYPE_in_type565 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_type_in_type549 = new BitSet(new long[] { 0x0000002010000000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_ID_in_type576 = new BitSet(new long[] { 0x0000000000000002L })
=======
  FOLLOW_28_in_type552 = new BitSet(new long[] { 0x000000D000044000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_ID_in_type587 = new BitSet(new long[] { 0x0000080000000000L })
=======
  FOLLOW_type_in_type554 = new BitSet(new long[] { 0x0000002010000000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_43_in_type589 = new BitSet(new long[] { 0x0000068000084000L })
=======
  FOLLOW_37_in_type558 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_type_in_type591 = new BitSet(new long[] { 0x0000010020000000L })
=======
  FOLLOW_type_in_synpred14_GuiceyData409 = new BitSet(new long[] { 0x0000000400000000L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet 
<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  FOLLOW_29_in_type594 = new BitSet(new long[] { 0x0000068000084000L })
=======
  FOLLOW_34_in_synpred14_GuiceyData411 = new BitSet(new long[] { 0x0000000000000002L })
>>>>>>> /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/right.java
  ;

  public static final BitSet FOLLOW_type_in_type596 = new BitSet(new long[] { 0x0000010020000000L });

  public static final BitSet FOLLOW_40_in_type600 = new BitSet(new long[] { 0x0000000000000002L });

  public static final BitSet FOLLOW_type_in_synpred15_GuiceyData451 = new BitSet(new long[] { 0x0000002000000000L });


<<<<<<< /usr/src/app/output/mattinsler/com.lowereast.guiceymongo/60813c8d71e04f2555a87814ad47ad8cdb621195/src/main/java/com/lowereast/guiceymongo/data/generator/parser/GuiceyDataParser.java/left.java
  public static final BitSet FOLLOW_37_in_synpred15_GuiceyData453 = new BitSet(new long[] { 0x0000000000000002L });
=======
>>>>>>> Unknown file: This is a bug in JDime.
}