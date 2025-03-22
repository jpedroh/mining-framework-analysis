package junit.runner;

/**
 * This class defines the current version of JUnit
 */
public class Version {
	private Version() {
		// don't instantiate
	}

	public static String id() {
<<<<<<< /usr/src/app/output/junit-team/junit4/ab7c961572adab958edf4c5cf1f5371b72117300/src/main/java/junit/runner/Version.java/left.java
		return "4.13-SNAPSHOT";
||||||| /usr/src/app/output/junit-team/junit4/ab7c961572adab958edf4c5cf1f5371b72117300/src/main/java/junit/runner/Version.java/base.java
		return "4.12-SNAPSHOT";
=======
		return "4.12";
>>>>>>> /usr/src/app/output/junit-team/junit4/ab7c961572adab958edf4c5cf1f5371b72117300/src/main/java/junit/runner/Version.java/right.java
	}
	
	public static void main(String[] args) {
		System.out.println(id());
	}
}
