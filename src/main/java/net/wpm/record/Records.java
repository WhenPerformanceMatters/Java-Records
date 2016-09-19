package net.wpm.record;

import java.util.ArrayList;
import java.util.List;

import com.koloboke.collect.map.hash.HashIntIntMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;

import net.wpm.record.collection.RecordSequence;

/**
 * Java Records API. 
 * Creates a record with the help of a blueprint (interface). 
 * Records are like pointers in C and point to empty address space. 
 * Record View act like c-structs and help to access the data of a record.
 * 
 * TODO not thread safe
 * 
 * TODO pools with ThreadLocal? -> to expensive
 * http://tutorials.jenkov.com/java-concurrency/threadlocal.html
 * http://stackoverflow.com/questions/609826/performance-of-threadlocal-variable
 * http://www.jutils.com/checks/performance.html
 * 
 * TODO performance checks with http://www.jutils.com/checks/performance.html
 * 
 * @author Nico Hezel
 *
 */
public class Records {


	// lists of all available record adapters
	@SuppressWarnings("rawtypes")
	protected static final List<RecordAdapter> recordAdapters;
	
	// Maps from a blueprint hashCode to an index for the recordAdapters list
	protected static final HashIntIntMap blueprintHashcodeToId;

	static {
		recordAdapters = new ArrayList<RecordAdapter>();
		blueprintHashcodeToId = HashIntIntMaps.newMutableMap();
		
		// blueprint id 0 is always null
		recordAdapters.add(null);
	}
	
	/**
	 * Get the record adapter for a specific blueprint id
	 * 
	 * @param blueprintId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static final <B> RecordAdapter<B> getRecordAdapter(final int blueprintId) {
		return recordAdapters.get(blueprintId);
	}

	/**
	 * Get the record adapter for a record
	 * 
	 * @param record
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static final <B> RecordAdapter<B> getRecordAdapter(final B record) {
		return (RecordAdapter<B>)((RecordView)record).getRecordAdapter();
	}
	
	/**
	 * Register a blueprint with the help of a record adapter. 
	 * Do nothing if the adapter has already been registered. 
	 * Always return the blueprint id of the adapter.
	 * 
	 * @param adapter
	 * @return
	 */
	protected static final <B> int register(final RecordAdapter<B> adapter) {
		
		// try to register the blueprint
		if(adapter.getBlueprintId() == 0) {
			final int newBlueprintId = recordAdapters.size();
			adapter.setBlueprintId(newBlueprintId);
			blueprintHashcodeToId.put(adapter.getBlueprint().hashCode(), newBlueprintId);
			recordAdapters.add(adapter);
		}
		return adapter.getBlueprintId();
	}
	
	/**
	 * Create a new record and a record view pointing to it, with the help of the record adapter.
	 * The underlying blueprint does not has to be registered.
	 * 
	 * @param adapter
	 * @return
	 */
	protected static final <B> B create(final RecordAdapter<B> adapter) {
		return adapter.create();
	}
	
	/**
	 * Create a new record view pointing to the data of another record.
	 * 
	 * @param adapter
	 * @param recordId
	 * @return
	 */
	protected static final <B> B view(final RecordAdapter<B> adapter, final long recordId) {
		return adapter.view(recordId);
	}
	
	/**
	 * Creates multiple records and make them accessible via a RecordSequence.
	 * 
	 * @param adapter
	 * @return
	 */
	protected static final <B> RecordSequence<B> array(final RecordAdapter<B> adapter, final int count) {
		return adapter.array(count);
	}
	
	
	
	
	// --------------------------------------------------------------------------------------------
	// ------------------------------------- Records API ------------------------------------------
	// --------------------------------------------------------------------------------------------
	
	/**
	 * Register a blueprint. Returns its blueprint id.
	 * 
	 * @param blueprint
	 * @return
	 */
	public static final <B> int register(final Class<B> blueprint) {
		int blueprintId = blueprintId(blueprint);
		
		// register if not registered yet
		if(blueprintId == 0)
			blueprintId = register(new RecordAdapter<B>(blueprint));
		
		return blueprintId;
	}

	/**
	 * Registers the blueprint if necessary. 
	 * Creates a record view based on the blueprint structure.
	 * Allocates memory for a new record and points the record view to it.
	 * 
	 * @param blueprint
	 * @return record view pointing to the data of a empty record
	 */
	public static <B> B of(final Class<B> blueprint) {
		final int blueprintId = blueprintId(blueprint);
		final RecordAdapter<B> adapter;
		
		// register or get existing record adapter
		if(blueprintId == 0) {
			adapter = new RecordAdapter<B>(blueprint);
			register(adapter);
		} else
			adapter = getRecordAdapter(blueprintId);
		
		// create a new record
		return create(adapter);
	}
	
	/**
	 * Create a record view from a registered blueprint.
	 * Allocates memory for a new record and points the record view to it.
	 *  
	 * @param blueprint
	 * @return
	 * @throws NullPointerException if blueprint is not registered or null
	 */
	public static final <B> B create(final Class<B> blueprint) {
		return create(blueprintId(blueprint));
	}

	/**
	 * Create a record for a registered blueprint id.
	 * Allocates memory for a new record and points the record view to it.
	 * Faster then using the blueprint itself.
	 * 
	 * @param blueprintId
	 * @return
	 * @throws NullPointerException if blueprint is not registered 
	 */
	public static final <B> B create(final int blueprintId) {
		RecordAdapter<B> adapter = getRecordAdapter(blueprintId);
		return create(adapter);
	}

	/**
	 * Reuse a record view but point to an empty new record. 
	 * Faster then creating a new record view and does not waste additional memory. 
	 * The underlying blueprint does not have to be registered.
	 * 
	 * @param reuse
	 * @return
	 */
	public static final <B> B create(final B reuse) {
		final RecordView recordView = ((RecordView) reuse);
		recordView.getRecordAdapter().create(recordView);
		return reuse;
	}

	/**
	 * Get the record id of a record which identifies its content.
	 * 
	 * @param record
	 * @return
	 */
	public static final long id(final Object record) {
		return ((RecordView) record).getRecordId();
	}

	
	/**
	 * Create a new record view pointing no-where.
	 * 
	 * @param blueprint
	 * @return
	 */
	public static final <B> B view(final Class<B> blueprint) {
		return view(blueprint, 0);
	}
		
	/**
	 * Create a new record view pointing no-where.
	 * 
	 * @param blueprint
	 * @return
	 */
	public static final <B> B view(final int blueprintId) {
		return view(blueprintId, 0);
	}
	
	/**
	 * Create a new record view pointing to the data of another record.
	 * 
	 * @param blueprint
	 * @param recordId
	 * @return
	 */
	public static final <B> B view(final Class<B> blueprint, final long recordId) {
		return view(blueprintId(blueprint), recordId);
	}

	/**
	 * Create a new record view pointing to the data of another record.
	 * 
	 * @param blueprintId
	 * @param recordId
	 * @return
	 */
	public static final <B> B view(final int blueprintId, final long recordId) {
		RecordAdapter<B> adapter = getRecordAdapter(blueprintId);
		return view(adapter, recordId);
	}

	/**
	 * Create a new record view and pointing to the data of the given record
	 * 
	 * @param record
	 * @return
	 */
	public static <B> B view(B record) {
		return getRecordAdapter(record).view(record);
	}
	
	/**
	 * Reuse an existing record view, pointing to the data of another record. 
	 * Faster then other view-methods because it does not create a new record view.
	 *  
	 * @param record
	 * @param recordId
	 * @return
	 */
	public static final <B> B view(final B record, final long recordId) {
		final RecordView recordView = (RecordView) record;
		recordView.setRecordId(recordId);
		return record;
	}

	
	/**
	 * Size of the corresponding record in bytes 
	 * 
	 * @param blueprint
	 * @return
	 */
	public static final <B> int size(final Class<B> blueprint) {
		return getRecordAdapter(blueprintId(blueprint)).getRecordSize();	}
	
	/**
	 * Size of the corresponding record in bytes 
	 * 
	 * @param blueprintId
	 * @return
	 */
	public static final <B> int size(final int blueprintId) {
		return getRecordAdapter(blueprintId).getRecordSize();
	}
	
	/**
	 * Size of the record in bytes 
	 * 
	 * @param record
	 * @return
	 */
	public static final <B> int size(final B record) {
		return ((RecordView) record).getRecordSize();
	}
	
	/**
	 * Create a new record view.
	 * Allocates memory for a new record
	 * and copies the data over from the given one
	 * 
	 * @param record
	 * @return new record view pointing to a copy of the record
	 */
	public static final <B> B copy(final B record) {		
		return getRecordAdapter(record).copy(record);
	}
	
	/**
	 * Copy the data from one record to another
	 * 
	 * @param fromRecord
	 * @param toRecord
	 */
	public static final <B> void copy(final B fromRecord, final B toRecord) {	
		getRecordAdapter(fromRecord).copy(fromRecord, toRecord);
	}
	
	/**
	 * Get the id of the blueprint. Zero means the blueprint is not registered.
	 * 
	 * @param blueprint
	 * @return
	 */
	public static final <B> int blueprintId(final Class<B> blueprint) {
		return blueprintHashcodeToId.getOrDefault(blueprint.hashCode(), 0);
	}
	
	/**
	 * Get the underlying blueprint id 
	 * 
	 * @param record
	 * @return
	 */
	public static final <B> int blueprintId(final B record) {
		return ((RecordView) record).getRecordAdapter().getBlueprintId();
	}
	
	/**
	 * Removes all records of a blueprint
	 * 
	 * @param blueprintId
	 */
	public static final void deleteAll(final int blueprintId) {
		getRecordAdapter(blueprintId).releaseAll();
		
		// TODO blueprintId muss weggeworfen werden?
	}
	
	/**
	 * Removes all records of a blueprint
	 * 
	 * @param blueprint
	 */
	public static final <B> void deleteAll(final Class<B> blueprint) {
		deleteAll(blueprintId(blueprint));
	}

	/**
	 * Creates multiple records and makes them accessible via a RecordSequence.
	 *  
	 * @param blueprint
	 * @return
	 * @throws NullPointerException if blueprint is not registered or null
	 */
	public static final <B> RecordSequence<B> array(final Class<B> blueprint, final int count) {
		return array(blueprintId(blueprint), count);
	}

	/**
	 * Creates multiple records and makes them accessible via a RecordSequence.
	 * 
	 * @param blueprintId
	 * @return
	 * @throws NullPointerException if blueprint is not registered
	 */
	public static final <B> RecordSequence<B> array(final int blueprintId, final int count) {
		RecordAdapter<B> adapter = getRecordAdapter(blueprintId);
		return array(adapter, count);
	}
}