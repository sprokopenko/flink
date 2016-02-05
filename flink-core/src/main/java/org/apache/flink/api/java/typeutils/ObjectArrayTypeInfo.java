/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.api.java.typeutils;

import java.lang.reflect.Array;

import org.apache.flink.annotation.Experimental;
import org.apache.flink.annotation.Public;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.GenericArraySerializer;

import com.google.common.base.Preconditions;

@Public
public class ObjectArrayTypeInfo<T, C> extends TypeInformation<T> {

	private static final long serialVersionUID = 1L;
	
	private final Class<T> arrayType;
	private final TypeInformation<C> componentInfo;

	private ObjectArrayTypeInfo(Class<T> arrayType, TypeInformation<C> componentInfo) {
		this.arrayType = Preconditions.checkNotNull(arrayType);
		this.componentInfo = Preconditions.checkNotNull(componentInfo);
	}

	// --------------------------------------------------------------------------------------------

	@Override
	@Experimental
	public boolean isBasicType() {
		return false;
	}

	@Override
	@Experimental
	public boolean isTupleType() {
		return false;
	}

	@Override
	@Experimental
	public int getArity() {
		return 1;
	}

	@Override
	@Experimental
	public int getTotalFields() {
		return 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Experimental
	public Class<T> getTypeClass() {
		return arrayType;
	}

	@Experimental
	public TypeInformation<C> getComponentInfo() {
		return componentInfo;
	}

	@Override
	@Experimental
	public boolean isKeyType() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Experimental
	public TypeSerializer<T> createSerializer(ExecutionConfig executionConfig) {
		return (TypeSerializer<T>) new GenericArraySerializer<C>(
			componentInfo.getTypeClass(),
			componentInfo.createSerializer(executionConfig));
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "<" + this.componentInfo + ">";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ObjectArrayTypeInfo) {
			@SuppressWarnings("unchecked")
			ObjectArrayTypeInfo<T, C> objectArrayTypeInfo = (ObjectArrayTypeInfo<T, C>)obj;

			return objectArrayTypeInfo.canEqual(this) &&
				arrayType == objectArrayTypeInfo.arrayType &&
				componentInfo.equals(objectArrayTypeInfo.componentInfo);
		} else {
			return false;
		}
	}

	@Override
	public boolean canEqual(Object obj) {
		return obj instanceof ObjectArrayTypeInfo;
	}

	@Override
	public int hashCode() {
		return 31 * this.arrayType.hashCode() + this.componentInfo.hashCode();
	}

	// --------------------------------------------------------------------------------------------

	@Experimental
	public static <T, C> ObjectArrayTypeInfo<T, C> getInfoFor(Class<T> arrayClass, TypeInformation<C> componentInfo) {
		Preconditions.checkNotNull(arrayClass);
		Preconditions.checkNotNull(componentInfo);
		Preconditions.checkArgument(arrayClass.isArray(), "Class " + arrayClass + " must be an array.");

		return new ObjectArrayTypeInfo<T, C>(arrayClass, componentInfo);
	}

	/**
	 * Creates a new {@link org.apache.flink.api.java.typeutils.ObjectArrayTypeInfo} from a
	 * {@link TypeInformation} for the component type.
	 *
	 * <p>
	 * This must be used in cases where the complete type of the array is not available as a
	 * {@link java.lang.reflect.Type} or {@link java.lang.Class}.
	 */
	@SuppressWarnings("unchecked")
	@Experimental
	public static <T, C> ObjectArrayTypeInfo<T, C> getInfoFor(TypeInformation<C> componentInfo) {
		Preconditions.checkNotNull(componentInfo);

		return new ObjectArrayTypeInfo<T, C>(
			(Class<T>)Array.newInstance(componentInfo.getTypeClass(), 0).getClass(),
			componentInfo);
	}
}