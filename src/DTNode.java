import java.util.ArrayList;
import java.util.HashMap;

import weka.core.Attribute;


public class DTNode{
	
	private Attribute attribute;
	
	/* Map of child nodes 
	 * The key is the double representation of the attribute's value
	 * Value is the DTNode that corresponds to that choice
	 */
	HashMap<Double,DTNode> children;
	DataSet instanceSet;
	
	public DTNode(Attribute att, DataSet set){
	
		attribute = att;
		children = new HashMap<Double,DTNode>();
		instanceSet = set;
	}
	
	public Attribute getAttribute() {
		return attribute;
	}

	public HashMap<Double,DTNode> getChildren() {
		return children;
	}
	
	public DTNode getChild(Double parentVal){
		if (!children.isEmpty()){
			return children.get(parentVal);
		}
		return this;
	}
	
	public DataSet getDataSet(){
		return instanceSet;
	}
	
	public String getResult(){
		return instanceSet.getClassValue();
	}
	public String toString(){
		return this.attribute.toString();
	}

}
