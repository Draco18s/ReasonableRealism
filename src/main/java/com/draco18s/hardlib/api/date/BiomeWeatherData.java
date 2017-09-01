package com.draco18s.hardlib.api.date;

public class BiomeWeatherData {
	public float temp;
	public float rain;
	public float tempScale;
	public float rainScale;

	public BiomeWeatherData(float _temp, float _rain, float _tempScale, float _rainScale) {
		if(_temp < -0.1 && _temp >= -0.3) {
			_temp -= 0.1;
		}
		else if(_temp < -0.3) {
			_temp = ((_temp+0.3f)*3f/4f)-0.4f;
		}
		temp = _temp;
		rain = _rain;
		tempScale = _tempScale;
		rainScale = _rainScale;
	}
}
