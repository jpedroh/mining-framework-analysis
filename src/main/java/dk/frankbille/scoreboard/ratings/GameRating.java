package dk.frankbille.scoreboard.ratings;

public interface GameRating {
	public double getChange(long teamId);
	public double getRating(long teamId);
}
