package net.wpm.record.samples;

import net.wpm.record.Records;

/**
 * While the blueprint id identifies the record view class 
 * and therefore the structure of the blueprint class, is 
 * the record id the pointer to the content of a record.
 * 
 * Knowing the blueprint or its id and the record id gives 
 * access to the underlying data from everywhere. 
 * 
 * The record id can be received with Records.id(...) or the
 * optional recordId() method of a blueprint.
 * 
 * On the other hand the optional recordId(int) can be used
 * instead of Records.get(blueprintId, structId) to point
 * to another record.
 * 
 * @author Nico Hezel
 *
 */
public class RecordsSample_07_RecordId {

	public static void main(String[] args) {
		
		// register the blueprint
		int blueprintId = Records.register(Sample08.class);
		
		// get a record and a record view of it
		Sample08 obj = Records.create(blueprintId);
		obj.setFraction(0.1f);
		
		// prints -> {Number: 0, Fraction: 0.1}
		System.out.println(obj);
		
		// the id is enough to change the content elsewhere
		long recordId = Records.id(obj);
		changeNumberOf(recordId);
		
		// prints -> {Number: 3, Fraction: 0.1}
		System.out.println(obj);
				
		// another record view, pointing to the same record
		Sample08 otherObj = Records.view(blueprintId, recordId);
		
		// the record id can be obtained with optional recordId() method
		if(otherObj.recordId() == recordId)
			System.out.println("Record id is "+recordId);
		
		// reuse the record view and point it to a new record
		otherObj = Records.create(otherObj);
		
		// prints -> {Number: 0, Fraction: 0}
		System.out.println(otherObj);
		
		// point it back to the first record
		otherObj.recordId(recordId);
		
		// prints -> {Number: 3, Fraction: 0.1}
		System.out.println(obj);
	}
	
	/**
	 * Change the number of an existing record to 3.
	 * 
	 * @param recordId
	 */
	protected static void changeNumberOf(long recordId) {
		// new record view, pointing to the data of an existing record
		Sample08 otherObj = Records.view(Sample08.class, recordId);
		otherObj.setNumber(3);
	}

	protected static interface Sample08 {
		
		public int getNumber();
		public void setNumber(int number);
		
		public float getFraction();
		public void setFraction(float fraction);
		
		// id of the record
		public long recordId();
		public void recordId(long recordId);
	}
}
