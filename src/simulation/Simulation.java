package simulation;

import java.util.Set;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.PriorityQueue;

public  abstract class Simulation extends BaseData{
	
	public Simulation(double start){
		m_startTime = start;
		m_currentTime = start;
		}
	public void initialize(double g_lamda, double lam)
		{
			m_lamda = g_lamda;	//业务到达率
			m_lam = lam;
			m_rou = g_rou;
			m_omiga = g_omiga;
			m_theta = g_theta;
			m_database.initializeDB();
		}

	 public double getRou()
		{
			return m_rou;
		}
	public	double getLambda()
		{
			return m_lamda;
		}
		
		//生成高斯分布随机数(期望为0.0，方差为1.0), X = gaussrand() * V + E(期望为E，方差为V)
	public abstract	double gaussrand();

		//随机产生业务的资源占用率(0.01~0.01+2*afar)
	public abstract	double randoccu();

		//随机生成业务数据量
	public abstract	double randdatasize(final int src, int n_id);

		//随机生成最大允许时延(Qos:0 100-500s; Qos:1 1-10s)
    public abstract	double randallowdelay(final int qos);

		//随机生成业务QoS等级(0,1)
	public abstract	int randqos();

		/*形参:	文件流 
		  功能:	初始化统计参数, 生成第一个业务事件, 
				循环处理优先级队列, 处理完则写入数据到"result.data", 打印到屏幕*/
	public abstract	void run(PrintWriter out);

		/*形参：业务id
		返回值：无
		功能：产生业务到达事件，业务的各项参数按照业务模型随机产生*/
	public abstract	void generateServiceEventPair(int Id);	

		/*形参：Event型的到达事件以及业务id
		返回值：业务到达事件的离去事件
		功能：产生业务的离去事件，其中离去事件的发生时间为到达事件发生的时间加上业务持续时间*/
	public abstract	Event generateLeavingevent(int Id,Event event);	

		//生成普通业务延时事件
	public abstract	Event generateDelayEvent(Event event);

		/*形参：Event型的事件，double型的数据中心负载均衡指数
		返回值：int型的业务跳数
		功能：对事件到达或离去的两种类型分别处理，到达则进行CSO算法和RWA，
			  成功则统计跳数和数据中心的负载均衡指数，并标记链路波长资源和数据中心中业务占有的资源，
			  失败则计业务阻塞; 离去则释放链路波长资源和数据中心中业务占有的资源*/
	public abstract	int dealWithEvent( Event event,final double d);

		/*形参：Event型的事件，double型的数据中心负载均衡指数
		返回值：int型的业务跳数
		功能：对业务执行CSO算法，调用Database中的showPath函数进行RWA。*/
	public abstract	int caculatePath(final Event event , final double d);

		/*形参：事件event
		  功能：预留资源, 调用Database中的reserveSource函数*/
	public abstract	void reserveSource(Event event);

		/*形参：事件event
		  功能：释放资源, 调用Database中的relieveSource函数*/
	public abstract	void relieveSource(Event event);

	public abstract	void readEdgeMap(String s);
	    
	public  int m_sumOfFailedService;		//总阻塞业务数目
	public	int m_HighQosFailedService;		//总阻塞业务数目
	public	int m_LowQosFailedService;		//总阻塞业务数目
	public	int m_FailedNetwork;			//网络阻塞业务数
	public	int m_FailedServer;				//应用阻塞业务数
	public	double sumOfDelay;				//总时延
	public	double HighQosDelay;			//时延敏感业务时延
	public	double LowQosDelay;				//普通业务时延
	public	int HighQosQuantity;
	public	int LowQosQuantity;
	public	double Sumof_averOltresource;
	public	double Sumof_averMaxBandwidth;
	public	 double Sumof_HoldTime;
	public double Sumof_ResOccu;
	public	int Sumof_averurrentServiceNum;
		
	public	int sumofservice;
		

	protected DatabaseExtend m_database ;
	{
		m_database = new DatabaseExtend();
	}
    protected double m_startTime;
    protected double m_simulationLength;
    protected double m_lamda, m_rou, m_omiga, m_theta, m_lam;
    protected double m_currentTime;
    protected int m_nextServiceId;
		
    protected Set<Integer> m_currentServiceId;
    {
    	m_currentServiceId = new HashSet<Integer>();
    }
	 	

	 	/*类型：优先级队列，按时间的发生时间排序
		元素：已生成的业务的到达事件，以及正在服务中的业务的离去事件*/
    protected PriorityQueue<Event> m_pq;//若使用greater比较函数，必须重载大于号
    {
    	m_pq = new PriorityQueue<Event>();
    }

     abstract int  frand(int ia,int ib);
     abstract double arrive_time_gen (float beta);
     abstract void randomSrcDst(final int src, final int des);

		
	

}
