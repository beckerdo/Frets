package frets.main;

/**
 * Abstraction for finger information.
 *
* @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public enum Finger {
	THUMB("Thumb", "T", "Pulgar", "P", 0), 
	INDEX("Index", "I", "Index", "I", 1), 
	MIDDLE("Middle", "M", "Medius", "M", 2), 
	RING("Ring", "R", "Annularis","A", 3), 
	LITTLE("Little", "L", "Quintus", "Q", 4);

	private Finger(String name, String shortName, String latin,
			String shortLatin, int value) {
		this.name = name;
		this.shortName = shortName;
		this.latin = latin;
		this.shortLatin = shortLatin;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}

	public String getLatin() {
		return latin;
	}

	public String getShortLatin() {
		return shortLatin;
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		return getName();
	}

	public static Finger getFinger(int value) {
		int normalValue = value % 5;
		switch (normalValue) {
		case 0:	return THUMB;
		case 1:	return INDEX;
		case 2:	return MIDDLE;
		case 3:	return RING;
		case 4:	return LITTLE;
		}
		return THUMB;
	}

	private String name;
	private String shortName;
	private String latin;
	private String shortLatin;
	private int value;
}