package net.wpm.record.bytes;

import net.openhft.chronicle.core.Memory;
import net.openhft.chronicle.core.OS;
import sun.misc.Cleaner;

/**
 * A piece of memory.
 * 
 * @author Nico
 *
 */
public class UnsafeBytes {

	protected final long address;
	protected final long capacity;
	protected long used;
	
	protected final Cleaner cleaner;

	public UnsafeBytes(Memory memory, long capacity) {
		this.used = 0;
		this.capacity = capacity;
		this.address = memory.allocate(capacity);
		memory.setMemory(address, capacity, (byte) 0);
		this.cleaner = Cleaner.create(this, new Deallocator(address, capacity));
	}	
	

	/**
	 * Starting address of this piece of memory
	 * 
	 * @return
	 */
	public long address() {
		return address;
	}
	
	/**
	 * Reserve some bytes
	 * 
	 * @param size
	 */
	public void use(long size) {
		used = used + size;
	}
	
	/**
	 * How many bytes are still empty 
	 * 
	 * @return
	 */
	public int remaining() {	
		return (int)(capacity - used);
	}
	
	/**
	 * Does the amount of bytes fit in this piece of memory
	 * 
	 * @param size
	 * @return
	 */
	public boolean hasCapacity(long size) {	
		return (used + size) < capacity;
	}
	
	/**
	 * Release the underlying memory
	 */
	public void release() {
		cleaner.clean();
	}
	
	public static class Deallocator implements Runnable {
        private volatile long address, size;

        public Deallocator(long address, long size) {
            assert address != 0;
            this.address = address;
            this.size = size;
        }

        @Override
        public void run() {
            if (address == 0)
                return;
            address = 0;
            OS.memory().freeMemory(address, size);
        }
    }
}