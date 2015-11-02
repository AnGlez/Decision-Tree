import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class DataSet {

	private ArrayList<Instance> instances;
	private ArrayList<Attribute> attributes;
	private int numAttributes;
	private int numInstances;
	private Attribute classAttribute;
	Logger logger = Logger.getLogger();
	
	public DataSet(Instances in) {
		
		in.setClassIndex(in.numAttributes() - 1);
		numAttributes = in.numAttributes()-1;		
		classAttribute = in.classAttribute();
		numInstances = in.numInstances();
		instances = new ArrayList<Instance>(numInstances);	//contains every row of data 			
		attributes = new ArrayList<Attribute>(numAttributes); //contains every category (attribute)
		
		//filling array with every row of data from file
		for (int i = 0; i < numInstances; i++){
			instances.add(in.instance(i));
		}
		
		//filling array of categories with every category
		for (int i = 0; i < numAttributes; i++) {
			attributes.add(instances.get(0).attribute(i));
		}
		
	}
	
	public DataSet(ArrayList<Instance> in, ArrayList<Attribute> atts) {
		instances = in;
		numInstances = instances.size();
		classAttribute = in.get(0).dataset().classAttribute();
		attributes = atts;
		numAttributes = atts.size();
		
	}
	
	public ArrayList<Instance> getInstances() {
		return instances;
	}

	public void setInstances(ArrayList<Instance> instances) {
		this.instances = instances;
	}

	public int getNumAttributes() {
		return numAttributes;
	}

	public void setNumAttributes(int numAttributes) {
		this.numAttributes = numAttributes;
	}

	public int getNumInstances() {
		return numInstances;
	}

	public void setNumInstances(int numInstances) {
		this.numInstances = numInstances;
	}

	public ArrayList<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(ArrayList<Attribute> attributes) {
		this.attributes = attributes;
	}

	public Attribute getClassAttribute() {
		return classAttribute;
	}

	public void setClassAttribute(Attribute classAttribute) {
		this.classAttribute = classAttribute;
	}

	public String getClassValue(){
		if(calculateEntropy(classAttribute) == 0){
			return instances.get(0).stringValue(classAttribute);
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
			key = instances.get(i).value(at);
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
	        entropy-=(count/numInstances) * (Math.log10(count/numInstances)/Math.log10(2));
	        //entropy-=(count/numInstances) * Math.log10(count/numInstances);
	    }
	    return entropy;
	}

	public double infoGain(Attribute at) {
		
		int totalValues = at.numValues();
		//Map that contains the attribute's value and an arraylist of instances that have that value
		HashMap<Double,	ArrayList<Instance>> countValues = new HashMap<Double,ArrayList<Instance>>(totalValues);
		
		double attVal;
		//iterate through current set instances
		for (int i = 0; i < numInstances; i++){
			//attribute's value on "at"
			attVal = instances.get(i).value(at);
			//if atribute's value hasn't been counted
			if ( !countValues.containsKey(attVal) ){
				countValues.put(attVal, new ArrayList<Instance>());
			}
			//add instance to its correspondent attribute value
			countValues.get(attVal).add(instances.get(i));
		}
		
		//calculate set's entropy
		double result = calculateEntropy(classAttribute);
		Iterator it = countValues.entrySet().iterator();
	    while (it.hasNext()) { //iterate through attribute/instance map
	        Map.Entry pair = (Map.Entry)it.next(); //pair contains attribute value and its instances
	        HashMap<Double,Integer> countResult = new HashMap<Double, Integer>(classAttribute.numValues()); //map that separates instances on the class attribute
	        ArrayList<Instance> attInstances =(ArrayList<Instance>) pair.getValue(); //instances on that value	        
	        
	        for (Instance in : attInstances){ //iterate through instances on each attribute value
	        	
	        	if (!countResult.containsKey(in.value(classAttribute))){ //if map doesn't contain class attribute's value, add it 
	        		countResult.put(in.value(classAttribute), 0);
	        	}
	        	int val = countResult.get(in.value(classAttribute));
	        	countResult.put(in.value(classAttribute), val + 1); // ex: humidity : normal ->yes ++
	        }
	        
	        Iterator it2 = countResult.entrySet().iterator();
			double attEntropy = 0;
			
			// round 1: sunny {yes, no}, 2: overcast {yes,no}, 3: rainy {yes, no}
		    while (it2.hasNext()) {
		    	Map.Entry pair2 = (Map.Entry)it2.next();
		    	int count = (int)pair2.getValue(); //how many yes or no in current attribute value
		    	attEntropy+= (count/attInstances.size()) * ( (Math.log10(attInstances.size()/count)) / Math.log10(2) );

		    }
		    result -= attEntropy * (attInstances.size()/numInstances);
	    }
		return result;
	}
	
	/*
	public Attribute selectAttribute(){
		double minEntropy = 1;
		
		Attribute minAtt = attributes.get(0);

		for (Attribute a : attributes){
			double entropy = this.calculateEntropy(a);
			if ( entropy < minEntropy){
				minAtt = a;
				minEntropy = entropy;
			}
		}
		logger.log("Attribute " + minAtt.toString() + " was chosen with entropy: "+ minEntropy);

		return minAtt;
	}
	*/
	public Attribute selectAttribute(){
		double maxInfoG = 0;
		
		Attribute max = attributes.get(0);

		for (Attribute a : attributes){
			double gain = this.infoGain(a);
			if ( gain > maxInfoG){
				max = a;
				maxInfoG = gain;
			}
		}
		//logger.log("Attribute " + max.toString() + " was chosen with info gain: "+ maxInfoG);

		return max;
	}
}
