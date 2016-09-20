package net.wpm.record.samples;

import net.wpm.record.Records;

/**
 * The first call to Structs.of(...) is slower then the consecutive ones, 
 * because Java Records analyzes and registers the blueprint, builds a 
 * record view class for it and allocates memory for a new record. 
 * 
 * The registration can be triggered manually Structs.register(blueprint),
 * without instantiating a record view or allocating memory for a record 
 * afterwards. Included in the process is the analysis of the blueprint 
 * and the construction of the new record view class.
 * 
 * Every registered blueprint has a blueprint id, identifying its record 
 * view class. The id can be used alternative to the blueprint class.
 * 
 * The blueprint can define a blueprintId() method in order to receive the id.
 * 
 * @author Nico Hezel
 *
 */
public class RecordsSample_06_BlueprintId {

	public static void main(String[] args) {
		
		// register the blueprint
		int blueprintId = Records.register(Sample06.class);
		
		// create a new record
		Sample06 obj = Records.create(blueprintId);
		obj.setNumber(77);
		obj.setFraction(-0.7f);
		
		// prints -> {Number: 77, Fraction: -0.7}
		System.out.println(obj);
				
		// the id can also be obtained with a special blueprintId() method
		if(blueprintId == obj.blueprintId())
			System.out.println("Blueprint id is "+blueprintId);		
	}

	protected static interface Sample06 {
		
		public int getNumber();
		public void setNumber(int number);
		
		public float getFraction();
		public void setFraction(float fraction);
		
		// this method always returns the blueprint id
		public int blueprintId();
	}
}
