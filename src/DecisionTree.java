import java.io.BufferedReader;
import java.io.FileReader;
import java.util.TreeSet;

import weka.core.Instances;

public class DecisionTree {
	 private DataSet initial;
	 private DTNode root;
	 
	public DecisionTree(String path) {
		try {

			BufferedReader reader = new BufferedReader(new FileReader(path));
			initial = new DataSet(new Instances(reader));
			root = new DTNode(initial.selectAttribute(),initial);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public void split(DataSet subset,DTNode splitter){
		if (subset.calculateEntropy(splitter.getAttribute()) == 0){
			return;
		}
		
	}
	
	public static void main(String[] args) {
		DecisionTree tree = new DecisionTree("weather.arff");
	}
	
}
