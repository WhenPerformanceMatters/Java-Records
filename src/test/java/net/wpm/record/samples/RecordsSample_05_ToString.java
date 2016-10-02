package net.wpm.record.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wpm.record.Records;

/**
 * By default Java Records implements the toString method listing all variables.
 * It is possible to implement a custom version of it which gets called by the
 * Java Records toString() method. The new method must be named "String string()"
 * 
 * @author Nico Hezel
 *
 */
public class RecordsSample_05_ToString {

	private static Logger log = LoggerFactory.getLogger(RecordsSample_05_ToString.class);

	public static void main(String[] args) {
		
		// get a record 
		Sample05 obj = Records.of(Sample05.class);
		obj.setNumber(5);
		obj.setPi(Math.PI);
		
		// prints -> "{Number: 5, Pi: 3.141592653589793}"
		log.info(obj.toString());
		
		
		// record with custom toString method
		Sample05String objStr = Records.of(Sample05String.class);
		objStr.setNumber(5);
		objStr.setPi(Math.PI);
		
		// prints -> "Pi is 3.14 and the number is 5"
		log.info(objStr.toString());
	}

	protected static interface Sample05String extends Sample05 {	
		
		// custom toString method, get called from toString()
		public default String string() {
			return String.format("Pi is %.2f and the number is %d", getPi(), getNumber()); 
		}
	}
	
	protected static interface Sample05 {
				
		public int getNumber();
		public void setNumber(int number);
		
		public double getPi();
		public void setPi(double pi);
	}
}
