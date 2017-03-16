package simulation;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main extends BaseData{
	public static void main(String[] args) throws IOException{
		
//		System.out.println(Thread.currentThread());
		String fileName = ".\\src\\simulation\\map.data";
		String file = ".\\src\\simulation\\result.data";
		PrintWriter out =new PrintWriter(new FileWriter(file));
		
			
		
//		ofstream ofile;
//		ofile.open("result.data", ios::out);
//		if(!ofile)
//		{
//			cerr << "open error!" << endl;
//			exit(-2);
//		}

		for(double lam =40; lam < maxlam; lam += 50)	//lam 业务量	
		{
			//double lam = 500;
			double lamda = g_rou / lam;	//rou到达率，到达率除以离去率等于业务量
			//m_occupiedwavemax = 0;
			//occupiedwave = 0;
			Simulation simulation = new SimulationExtend(0);
			simulation.readEdgeMap(fileName);
			simulation.initialize(lamda, lam);
			simulation.run(out);
			
		}
		out.close();
	}

}
