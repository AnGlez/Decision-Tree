import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
	
	private File file;
	private FileWriter writer;
	private static Logger instance = null;
	
	protected Logger(){
		file = new File("log.txt");
	}
	
	public static Logger getLogger(){
		
		if (instance == null)
			instance = new Logger();
		
		return instance;
	}
	
	public void log(String msg){
		
		try {
			writer = new FileWriter(file,true);
			writer.write(msg);
			writer.write(System.lineSeparator());
			writer.write(System.lineSeparator());

			writer.flush();
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
