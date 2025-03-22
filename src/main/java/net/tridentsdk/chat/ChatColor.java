package net.tridentsdk.chat;
import lombok.Getter;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Represents the different colors that can be sent in chat.
 *
 * @author TridentSDK
 * @since 0.5-alpha
 */@Immutable public enum ChatColor {
  BLACK('0'),
  DARK_BLUE('1'),
  DARK_GREEN('2'),
  DARK_AQUA('3'),
  DARK_RED('4'),
  DARK_PURPLE('5'),
  GOLD('6'),
  GRAY('7'),
  DARK_GRAY('8'),
  BLUE('9'),
  GREEN('a'),
  AQUA('b'),
  RED('c'),
  LIGHT_PURPLE('d'),
  YELLOW('e'),
  WHITE('f'),
  OBFUSCATED('k'),
  BOLD('l'),
  STRIKETHROUGH('m'),
  UNDERLINE('n'),
  ITALIC('o'),
  RESET('r')
  ;

  @Getter private final char colorChar;

  /**
     * Creates a new chatcolor based on the given character
     * which represents the canonical control sequence
     * for that particular color.
     *
     * @param colorChar the color character
     */
  ChatColor(char colorChar) {
    this.colorChar = colorChar;
  }

  /**
     * Gets if this chat color is a format color.
     *
     * @return True iff it is.
     */
  public boolean isFormat() {
    return 'k' <= this.colorChar && this.colorChar <= 'r';
  }

  /**
     * Gets if this chat color is a color.
     *
     * @return The color.
     */
  public boolean isColor() {
    return !this.isFormat();
  }

  /**
     * Gets a string representation of this color, in the
     * form &#167;{@code x}, where x is the color's
     * character.
     *
     * @return The color.
     */
  @Override public String toString() {
    return "\u00a7" + this.colorChar;
  }

  /**
     * Gets a chat color from a given character.
     *
     * @param colorChar The color's character.
     * @return The color, or null if not found.
     */
  @Nonnull public static ChatColor of(char colorChar) {
    for (ChatColor color : values()) {
      if (color.colorChar == colorChar) {
        return color;
      }
    }
    throw new IllegalArgumentException("no color with character " + colorChar);
  }
}