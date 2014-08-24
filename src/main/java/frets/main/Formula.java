package frets.main;

/**
 * Information about chords,scales,voicings, formula and their data.
 *
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class Formula {
	public Formula() {
		
	}
	public Formula( String name, String verbose, String formula ){
		setName( name );
		setNameVerbose( verbose );
		setFormula( formula );
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNameVerbose() {
		return nameVerbose;
	}
	public void setNameVerbose(String nameVerbose) {
		this.nameVerbose = nameVerbose;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	@Override
	public int hashCode() {
		int result = 31;
		result = 17 * result + name.hashCode();
		result = 17 * result + nameVerbose.hashCode();
		result = 17 * result + formula.hashCode();
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		// Should normalize both values before comparing.
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Formula other = (Formula) obj;
		return this.equals( other );
	}

	public boolean equals(Formula other) {
		if ( !( this.name == null ? other.name == null : this.name.equals( other.name )) )
			return false;
		if ( !( this.nameVerbose == null ? other.nameVerbose == null : this.nameVerbose.equals( other.nameVerbose )) )
			return false;
		if ( !( this.formula == null ? other.formula == null : this.formula.equals( other.formula )) )
			return false;
		return true;	
	}

	@Override
	/** Return note name, with octave as superscript. */
	public String toString() {
		return "Formula[name=" + name + ",verbose=" + nameVerbose + ",formula=" + formula + "]";
	}
	
	public String name;
	public String nameVerbose;
	public String formula;
}