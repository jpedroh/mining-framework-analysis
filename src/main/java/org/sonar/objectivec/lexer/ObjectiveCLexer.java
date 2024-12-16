/*
 * Sonar Objective-C Plugin
 * Copyright (C) 2012 OCTO Technology
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.objectivec.lexer;

import com.sonar.sslr.impl.Lexer;
import com.sonar.sslr.impl.channel.BlackHoleChannel;
import com.sonar.sslr.impl.channel.IdentifierAndKeywordChannel;
import com.sonar.sslr.impl.channel.PunctuatorChannel;
import org.sonar.objectivec.ObjectiveCConfiguration;
import org.sonar.objectivec.api.ObjectiveCKeyword;
import org.sonar.objectivec.api.ObjectiveCPunctuator;
import org.sonar.objectivec.api.ObjectiveCTokenType;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.and;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.commentRegexp;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.o2n;
import static com.sonar.sslr.impl.channel.RegexpChannelBuilder.regexp;


public class ObjectiveCLexer {
  private ObjectiveCLexer() {
  }

  public static Lexer create() {
    return create(new ObjectiveCConfiguration());
  }

  public static Lexer create(ObjectiveCConfiguration conf) {
    return // skip all whitespace chars
        // punctuators/operators
        // identifiers and keywords
        // identifiers starts with a non digit and underscore and continues with either one of these or with digits
        // case sensitive = true
        // float/double
        // decimal
        // hex
        // numeric literals
        // integer/long
        // decimal
    // string literals
        // Comments
    Lexer.builder().withCharset(conf.getCharset()).withFailIfNoChannelToConsumeOneCharacter(true).withChannel(commentRegexp("//[^\\n\\r]*+")).withChannel(commentRegexp("/\\*[\\s\\S]*?\\*/")).withChannel(regexp(ObjectiveCTokenType.STRING_LITERAL, "\"([^\"\\\\]*+(\\\\[\\s\\S])?+)*+\"")).withChannel(regexp(ObjectiveCTokenType.NUMERIC_LITERAL, "[0-9]++[lL]?+")).withChannel(regexp(ObjectiveCTokenType.NUMERIC_LITERAL, "0[xX][0-9A-Fa-f]++[lL]?+")).withChannel(regexp(ObjectiveCTokenType.NUMERIC_LITERAL, "[0-9]++[fFdD]")).withChannel(new IdentifierAndKeywordChannel(and("[a-zA-Z_]", o2n("\\w")), true, ObjectiveCKeyword.values())).withChannel(new PunctuatorChannel(ObjectiveCPunctuator.values())).withChannel(new BlackHoleChannel("[\\s]")).build();
  }
}