package org.shuju;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DataRow
{
	public ArrayList<IFeature> features() {
		return this.features;
	}

	public DataRow addFeature(IFeature feature) {
		this.features.add(feature);
		return this;
	}

	public IFeature getLabel() {
		return this.label;
	}

	public DataRow setLabel(IFeature label) {
		this.label = label;
		return this;
	}

	public String toString() {
		String result = "";
		boolean firstPass = true;
		for(IFeature feature: this.features) {
			if(firstPass) {
				result = feature.toString();
				firstPass = false;
			}
			else {
				result += ", " + feature.toString();
			}
		}
		return result + " :- " + this.label.toString();
	}

	private ArrayList<IFeature> features = new ArrayList<IFeature>();
	private IFeature label = null;
}
