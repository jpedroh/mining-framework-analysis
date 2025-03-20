package com.jcabi.github;

/**
 * Reaction for issue / comment.
 *
 * @author Paulo Lobo (pauloeduardolobo@gmail.com)
 * @version $Id$
 * @since 1.0
 * @see <a href="https://developer.github.com/v3/reactions">Reactions API</a>
 * @todo #1467:30min Check reaction values. At the moment only a few types of
 *  reactions are allowed (full list at
 *  https://developer.github.com/v3/reactions/#reaction-types). Implement
 *  reaction validation in RtReaction.value and then uncomment test in
 *  RtReactionTest
 * @todo #1451:30min Create reaction values constants in Reaction. As reaction
 *  values are in a few number they must be created as constants in Reaction.
 *  Then replace all existing code in tests and application to use the new
 *  created constants.
 * @todo #1469:30min Add support to team discussion and team discussion comments
 *  The API does not supports team discussion and team discussion comments (
 *  https://developer.github.com/changes/2018-02-07-team-discussions-api/ )
 *  After this implementation, add reaction support to these elements.
 */
public interface Reaction {
  /**
     * Thumbs up reaction constant.
     */
  String THUMBSUP = "+1";

  /**
     * Thumbs down reaction constant.
     */
  String THUMBSDOWN = "-1";

  /**
     * Laugh reaction constant.
     */
  String LAUGH = "laugh";

  /**
     * Confused reaction constant.
     */
  String CONFUSED = "confused";

  /**
     * Heart reaction constant.
     */
  String HEART = "heart";

  /**
     * Hooray reaction constant.
     */
  String HOORAY = "hooray";

  /**
     * The reaction type.
     * @return The type of the reaction.
     */
  String type();

  final class Simple implements Reaction {
    /**
         * Reaction type.
         */
    private final String type;

    /**
         * Constructor.
         * @param reaction Reaction type.
         */
    Simple(final String reaction) {
      this.type = reaction;
    }

    /**
         * Returns the reaction type.
         * @return Reaction type.
         */
    public String type() {
      return this.type;
    }
  }
}