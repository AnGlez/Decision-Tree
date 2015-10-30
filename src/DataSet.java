import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import weka.core.Attribute;
import weka.core.Instances;

public class DataSet {

	private Instances instances;
	private int numAttributes;
	private int numInstances;
	private Attribute attributes[];
	private Attribute classAttribute;
	
	public DataSet(Instances in) {
		
		instances = in;
		instances.setClassIndex(instances.numAttributes() - 1);
		classAttribute = instances.classAttribute();
		numInstances = instances.numInstances();
		numAttributes = instances.numAttributes()-1;		
		attributes = new Attribute[numAttributes];
		
		for (int i = 0; i < numAttributes; i++){
			if (!instances.attribute(i).equals(classAttribute)){
				attributes[i] = instances.attribute(i);
			}
		}
	}
	public DataSet filter(Attribute att, double val){
		//Instances result = new Instances();	
		for (int i = 0; i < numInstances; i++){
			
		}
		return null;
	}
	
	public String getClassValue(){
		if(calculateEntropy(classAttribute) == 0){
			return instances.instance(0).stringValue(classAttribute);
		}
		return null;
	}
	public double calculateEntropy(Attribute at){

		int totalValues = at.numValues();
		
		//We'll use a HashMap to count repetitions of every attributes possible value
		HashMap<Double,Integer> countValues = new HashMap<Double,Integer>(totalValues);
		double key;
		int value;
		for (int i = 0; i < numInstances; i++){
			key = instances.instance(i).value(at);
			if(countValues.containsKey(key)){
				value = countValues.get(key);
				countValues.put(key, value + 1);
			} else {
				countValues.put(key, 1);
			}
		}
		double entropy = 0;
		Iterator it = countValues.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        double count = Double.parseDouble(pair.getValue().toString());
	        entropy+=(count/numInstances) * Math.log10(numInstances/count);
	    }
	    return entropy;
	}
	
	public Attribute selectAttribute(){
		double minEntropy = 1;
		Attribute minAtt = attributes[0];
		
		for (Attribute a : attributes){
			double entropy = this.calculateEntropy(a);
			if ( entropy < minEntropy){
				minAtt = a;
				minEntropy = this.calculateEntropy(a);
			}
		}
		return minAtt;
	}

}
