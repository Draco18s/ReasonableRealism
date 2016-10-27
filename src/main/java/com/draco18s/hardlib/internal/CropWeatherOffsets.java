package com.draco18s.hardlib.internal;

public class CropWeatherOffsets {
	public final float temperatureFlat;
	public final float rainfallFlat;
	public final int temperatureTimeOffset;
	public final int rainfallTimeOffset;
	
	/**
	 * @param tf - Temperature offset.  Positive is "cold native" negative is "warm native."
	 * @param rf - Rain offset.  Positive is "dry native" negative is "wet native."
	 * @param to - Temp season offset in ticks.  Typically fractions of a year-length.
	 * @param ro - Rain sesaon offset in ticks.  Typically fractions of a year-length.
	 */
	public CropWeatherOffsets(float tf, float rf, int to, int ro) {
		temperatureFlat = tf;
		rainfallFlat = rf;
		temperatureTimeOffset = to;
		rainfallTimeOffset = ro;
	}
	
	/**
	 * Constructor that creates a duplicate with the same values.
	 * @param orig - the original top copy
	 */
	public CropWeatherOffsets(CropWeatherOffsets orig) {
		temperatureFlat = orig.temperatureFlat;
		rainfallFlat = orig.rainfallFlat;
		temperatureTimeOffset = orig.temperatureTimeOffset;
		rainfallTimeOffset = orig.rainfallTimeOffset;
	}
	
	@Override
	public String toString() {
		return ((temperatureFlat >=0)?"+":"") + temperatureFlat + "," + ((rainfallFlat >=0)?"+":"") + rainfallFlat + "|" + temperatureTimeOffset + "," + rainfallTimeOffset;
	}
}