import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class DecisionTree {
	
	private DataSet initial;
	private DTNode root;
	private int depth;
	static Logger logger = Logger.getLogger();
	static ArrayList<Attribute> setAttributes;
	
	public DecisionTree(String path) {
		depth = 0;
		try {
<<<<<<< Updated upstream
			
=======
			System.out.println("This is the path" +path);

>>>>>>> Stashed changes
			BufferedReader reader = new BufferedReader(new FileReader(path));
			logger.log("Created new tree from file: " + path);
			Instances in = new Instances(reader);
			initial = new DataSet(in);
			setAttributes = new ArrayList<Attribute>();
			
			for (int i = 0; i < in.numAttributes(); i++){
				if (!in.attribute(i).equals(in.classAttribute()))
					setAttributes.add(in.attribute(i));
			}
			
			root = new DTNode(initial.selectAttribute(),initial);
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DataSet getInitial() {
		return initial;
	}

	public void setInitial(DataSet initial) {
		this.initial = initial;
	}

	public DTNode getRoot() {
		return root;
	}

	public void setRoot(DTNode root) {
		this.root = root;
	}
	
	public void split(DTNode splitter){
		
		DataSet splitterSet = splitter.getDataSet(); //data set of splitter node
		Attribute nodeAtt = splitter.getAttribute(); //node's attribute used to split the set
		
		// remove the current attribute so the child node won't choose it as splitter attribute
    	ArrayList<Attribute> childAtts = splitter.getDataSet().getAttributes();
        childAtts.remove(nodeAtt);
        
        
		//base case is when the set is homogeneous or the set has less than 3 instances or the maximum depth was reached or there are no more attributes left to split
		double entropy = splitterSet.calculateEntropy(splitterSet.getClassAttribute());
		logger.log("Entropy of set in depth " + depth + ", node " + splitter.toString() + ": "+entropy);
		
		if ( entropy == 0 || splitterSet.getNumInstances() <=3 || childAtts.size()<=1 || depth > 100 || childAtts.size() < 1){
			logger.log("Stopped splitting with entropy " + entropy + ", "
														+ splitterSet.getNumInstances() + " instances, "
														+ childAtts.size() + "attributes, "
														+ "and depth "+ depth
														);
			return;
		}
        
		//we separate instances by their attributes value in a map
		HashMap<Double, ArrayList<Instance>> sMap = 
				new HashMap<Double, ArrayList<Instance>>();
		
		for(int i = 0; i < splitterSet.getNumInstances(); i++){
			
			Instance currentInstance = splitterSet.getInstances().get(i);
			Double instanceVal = currentInstance.value(nodeAtt); 

			if(!sMap.containsKey(instanceVal) ){
				sMap.put(instanceVal, new ArrayList<Instance>());//add instance attribute value to the map and an empty list of instances
			}
			sMap.get(instanceVal).add(currentInstance); //add instance to its correspondent attribute's value key
			
		}
		
		//iterate through split map
		Iterator it = sMap.entrySet().iterator();
	    while (it.hasNext()) {
	        //pair contains an attribute's value and all the instances of the set that have it
	    	Map.Entry pair = (Map.Entry)it.next();
	        //create a new set containing the instances already split by the current attribute
	        DataSet childSet = new DataSet((ArrayList<Instance>)pair.getValue(),childAtts);
			double childEntropy = childSet.calculateEntropy(childSet.getClassAttribute());
	        
			if ( childSet.getNumInstances() <= 3 || childSet.getAttributes().size() < 1){
	        	logger.log("Skipped splitting "+splitter.toString()+" with entropy " + childEntropy + ", "
						+ splitterSet.getNumInstances() + " instances, "
						+ childAtts.size() + "attributes, "
						+ "and depth "+ depth
						);
	        	continue;
			}
	        double attVal = (Double)pair.getKey();
	        DTNode child = new DTNode(childSet.selectAttribute(),childSet); //create a child node with the set of attributes (excluding current attribute) and the split instances
	        
	        splitter.children.put((Double)pair.getKey(),child); //add child node to current node's children
	        logger.log("Added child: "+child + " to node: "+splitter + " if : "+attVal);
	        //proceed to split child node
	        depth ++;
	        split(child);
	    }
	}
	
	public HashMap<Attribute,Double> getQueryValues(Instance instance) {
	
		int numAttributes = root.getDataSet().getNumAttributes();
		
		HashMap<Attribute,Double> queryValues = new HashMap<Attribute,Double>(numAttributes);
		
		for(int i = 0; i < numAttributes; i++){
			
			queryValues.put(setAttributes.get(i), instance.value(setAttributes.get(i)));
		}
		return queryValues;
	}
	
	public void query(HashMap<Attribute,Double> values,DTNode node){
		
		Attribute att = node.getAttribute(); //attribute we will use for splitting query's value map
		DataSet nodeSet = node.getDataSet(); //data set of current node
		Attribute classAtt = nodeSet.getClassAttribute(); // class attribute
		String result;
		if(node.children.isEmpty()){ //base case: the node is a leaf
			if (nodeSet.calculateEntropy(classAtt) == 0){
				result = "Result: "+nodeSet.getInstances().get(0).stringValue(classAtt)+" with 100% certainty";
				
			} else {
		
				HashMap<Double, Integer> results = new HashMap<Double,Integer>(classAtt.numValues());
				
				for (Instance i : nodeSet.getInstances()){
					if (!results.containsKey(i.value(classAtt))){
						results.put(i.value(classAtt), 0);
					}
					int val = results.get(i.value(classAtt));
					results.put(i.value(classAtt), val + 1);
				}
				
				Iterator it = results.entrySet().iterator();
				int count = 1;
				result = "Result: ";
			    
				while (it.hasNext()) {
			        //pair contains an attribute's value and all the instances of the set that have it
			    	Map.Entry pair = (Map.Entry)it.next();
			    	double certainty = (int)pair.getValue() / (double) nodeSet.getNumInstances();
			    	Double key = (Double)pair.getKey();
			    	result += classAtt.value(key.intValue())+" with " + (certainty * 100)+"% certainty, ";
			    }
				
			}
			System.out.println(result);
			return;
		}
		
		double attributeVal = (double)values.get(att);
		DTNode parentVal = node.getChild(attributeVal);
		query(values, parentVal);
		
	}
	
	public void print(DTNode node, int level){
		
		if(level == depth) {
			return;
		}
		
		for (int i = 0; i < level; i++){
			System.out.print("\t");
		}
		
		System.out.print(node+"\n");
				
		Iterator it = node.children.entrySet().iterator();
	    while (it.hasNext()) {
	    	
	    	for (int i = 0; i < level + 1; i++){
				System.out.print("\t");
			}
	    	Map.Entry pair = (Map.Entry)it.next();
	    	System.out.println((double)pair.getKey());
	    	print((DTNode)pair.getValue(),level + 1);
	    	
	    }
	}

	public static void main(String[] args) {
<<<<<<< Updated upstream
		
		String path = "weather.arff"; // change this variable to test a data set
=======
		String path = "vote.arff"; // change this variable to test a data set
>>>>>>> Stashed changes
		DecisionTree tree = new DecisionTree(path);
		tree.split(tree.getRoot());
		logger.log("Finished splitting tree");
		logger.log("-----------------------------------------------------------\n");
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("queries.arff"));
			Instances queries = new Instances(reader);
			reader.close();
			
			//Instances qInstances =  tree.getQueryValues();
			for (int i = 0; i < queries.numInstances(); i++){
				HashMap<Attribute,Double> qValues =tree.getQueryValues(queries.instance(i));
				tree.query(qValues, tree.getRoot());
				System.out.println();
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
