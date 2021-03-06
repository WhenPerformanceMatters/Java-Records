
package net.wpm.reflectasm;

/**
 * Copyright (c) 2008, Nathan Sweet
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *  3. Neither the name of Esoteric Software nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ---------------------------
 *
 *
 * This is a special version of the ReflectASM library.
 * https://github.com/EsotericSoftware/reflectasm/issues/24
 * 
 * It is extended to meet the needs of Java Records but follows their license agreement:
 * https://github.com/EsotericSoftware/reflectasm/blob/master/license.txt
 * 
 * 
 * @author Nico Hezel
 */
public class FieldAccess {
    public final ClassAccess classAccess;

    @Override
    public String toString() {
        return classAccess.toString();
    }

    protected FieldAccess(ClassAccess classAccess) {
        this.classAccess = classAccess;
    }

    public int getIndex (String fieldName) {
		return classAccess.indexOfField(fieldName);
	}

	public void set (Object instance, String fieldName, Object value) {
		set(instance, getIndex(fieldName), value);
	}

	public Object get (Object instance, String fieldName) {
		return get(instance, getIndex(fieldName));
	}

	public String[] getFieldNames () {
		return classAccess.getFieldNames();
	}
	
	public int[] getFieldModifiers() {
		return classAccess.getFieldModifiers();
	}

	public Class<?>[] getFieldTypes () {
		return classAccess.getFieldTypes();
	}

	public int getFieldCount () {
		return classAccess.getFieldCount();
	}

    public void set(Object instance, int fieldIndex, Object value) {
        classAccess.set(instance, fieldIndex, value);
    }

    public void setBoolean(Object instance, int fieldIndex, boolean value) {
        classAccess.setBoolean(instance, fieldIndex, value);
    }

    public void setByte(Object instance, int fieldIndex, byte value) {
        classAccess.setByte(instance, fieldIndex, value);
    }

    public void setShort(Object instance, int fieldIndex, short value) {
        classAccess.setShort(instance, fieldIndex, value);
    }

    public void setInt(Object instance, int fieldIndex, int value) {
        classAccess.setInt(instance, fieldIndex, value);
    }

    public void setLong(Object instance, int fieldIndex, long value) {
        classAccess.setLong(instance, fieldIndex, value);
    }

    public void setDouble(Object instance, int fieldIndex, double value) {
        classAccess.setDouble(instance, fieldIndex, value);
    }

    public void setFloat(Object instance, int fieldIndex, float value) {
        classAccess.setFloat(instance, fieldIndex, value);
    }

    public void setChar(Object instance, int fieldIndex, char value) {
        classAccess.setChar(instance, fieldIndex, value);
    }

    public Object get(Object instance, int fieldIndex) {
        return classAccess.get(instance, fieldIndex);
    }

    public char getChar(Object instance, int fieldIndex) {
        return classAccess.getChar(instance, fieldIndex);
    }

    public boolean getBoolean(Object instance, int fieldIndex) {
        return classAccess.getBoolean(instance, fieldIndex);
    }

    public byte getByte(Object instance, int fieldIndex) {
        return classAccess.getByte(instance, fieldIndex);
    }

    public short getShort(Object instance, int fieldIndex) {
        return classAccess.getShort(instance, fieldIndex);
    }

    public int getInt(Object instance, int fieldIndex) {
        return classAccess.getInt(instance, fieldIndex);
    }

    public long getLong(Object instance, int fieldIndex) {
        return classAccess.getLong(instance, fieldIndex);
    }

    public double getDouble(Object instance, int fieldIndex) {
        return classAccess.getDouble(instance, fieldIndex);
    }

    public float getFloat(Object instance, int fieldIndex) {
        return classAccess.getFloat(instance, fieldIndex);
    }

    public String getString(Object instance, int fieldIndex){
        return (String)get(instance, fieldIndex);
    }

    static public FieldAccess get (Class<?> type) {
        return new FieldAccess(ClassAccess.get(type));
	}
    
    static public FieldAccess get (ClassAccess access) {
        return new FieldAccess(access);
	}
}
