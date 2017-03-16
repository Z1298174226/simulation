package simulation;
import java.io.IOException;
import java.util.*;

public abstract class Database extends BaseData{
	Database()
	{
	}
 
	public double[]  m_serverResourse = new double[MAXINUM];	    //各数据中心资源占有率
	public double[]  m_Oltresource = new double[MAXINUM];			//源节点实时网络资源使用
	public double[]  m_averOltresource = new double[MAXINUM];		//源节点平均网络资源使用
	public double[]  m_srcMaxBandwidth = new double[MAXINUM];		//各源节点的分配带宽（可用上限）
	public double[]  m_averMaxBandwidth = new double[MAXINUM];		//各源节点的分配带宽

	
	public double[]  DBA_averBandwidthmax = new double[MAXINUM];								//各源节点带宽最大使用情况
	public Double[] DBA_averBandwidthRate = new Double[MAXINUM];   //DBA_averBandwidth[s] / m_srcMaxBandwidth[s] 带宽负载率
	{
		for(int i = 0;i < MAXINUM; i++){
			DBA_averBandwidthRate[i] = new Double();
		}
	}
	
	
	public int[]  m_currentServicemaxNum = new int[MAXINUM];		//

	public double[][] DBA_SampBandwidth = new double[MAXINUM][(int)(ServiceQuantity * 0.01)];	//带宽采样值	
	public double[][] DBA_SampDataSize = new double[MAXINUM][(int)(ServiceQuantity * 0.01)];	//数据量采样值，用于预测网络流量
	public int[][] DBA_SampcurrentServiceNum = new int[MAXINUM][(int)(ServiceQuantity * 0.01)];
	
	public int m_serviceNum;	//调试用

	/*形参:	节点(0-13)
	  功能:	根据拓扑的邻接矩阵g_edge[][]计算出节点的最短路径长度m_dist[][],
	  	    和s到d最短路径的下一节点号m_path[d][s]*/
	public abstract void caculatePath(int i);

	/*形参:	无
	  功能:	计算数据中心资源均衡度*/
	public  abstract double caculateSource();

	/*形参:	无 
	  功能:	循环调用caculatePath(), 计算出所有节点的最短路径树,
			初始化链路的所有波长均可用, 初始化数据中心资源占有率为0. */
	public abstract void initializeDB();

	/*形参:	业务事件, (引用)数据中心资源均衡度
	  返回: 分配的波长 / -1节点不可达&无可用波长 / -2应用资源阻塞
	  功能:	根据m_path[d][s]获取业务路径, 并根据链路波长占用情况计算出可用波长,
	  		标记业务占用的数据中心资源, 调用caculateSource()计算数据中心资源均衡度*/
	public abstract int showPath(Event event, double d);


	/*形参:	源节点、宿节点、占用波长号
	  功能:	预留资源:标记链路中业务占用的波长不可用*/
	public abstract void reserveSource(final Event event);
	

	/*形参:	源节点、宿节点、占用波长号
	  功能:	释放资源:标记链路中业务占用的波长可用*/
	public abstract void relieveSource(final Event event);

	//动态带宽分配（汇聚层DBA策略）
	public abstract void caculateDBA(final Event event);

	public abstract float getDist(int m, int n);
	
	public abstract void readEdgeMap(String filename) throws IOException;
	
	public LinkedList <Event> m_serviceEvent;
	{
		m_serviceEvent = new LinkedList<Event>();
	}


	//Simulation simulation1;
	protected Dist m_dist ;
	protected Path m_path ;
	protected S m_shortestPoint ;
	{
		m_dist = new Dist();
		m_path = new Path();
		m_shortestPoint = new S();
	}
	protected boolean[][][]  m_resourceMap = new boolean[MAXINUM][MAXINUM][WaveNumber];	//m_resourceMap[m][n][k]表示链路m到n上的第k个波长被业务占用

}
