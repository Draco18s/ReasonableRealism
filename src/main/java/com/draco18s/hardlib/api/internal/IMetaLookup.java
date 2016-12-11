package com.draco18s.hardlib.api.internal;

public interface IMetaLookup<T extends Enum> {
	public String getID();
	public T getByOrdinal(int i);

	public String getVariantName();
	public int getOrdinal();
}
