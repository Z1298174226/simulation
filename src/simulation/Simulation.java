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
			m_lamda = g_lamda;	//ҵ�񵽴���
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
		
		//���ɸ�˹�ֲ������(����Ϊ0.0������Ϊ1.0), X = gaussrand() * V + E(����ΪE������ΪV)
	public abstract	double gaussrand();

		//�������ҵ�����Դռ����(0.01~0.01+2*afar)
	public abstract	double randoccu();

		//�������ҵ��������
	public abstract	double randdatasize(final int src, int n_id);

		//��������������ʱ��(Qos:0 100-500s; Qos:1 1-10s)
    public abstract	double randallowdelay(final int qos);

		//�������ҵ��QoS�ȼ�(0,1)
	public abstract	int randqos();

		/*�β�:	�ļ��� 
		  ����:	��ʼ��ͳ�Ʋ���, ���ɵ�һ��ҵ���¼�, 
				ѭ���������ȼ�����, ��������д�����ݵ�"result.data", ��ӡ����Ļ*/
	public abstract	void run(PrintWriter out);

		/*�βΣ�ҵ��id
		����ֵ����
		���ܣ�����ҵ�񵽴��¼���ҵ��ĸ����������ҵ��ģ���������*/
	public abstract	void generateServiceEventPair(int Id);	

		/*�βΣ�Event�͵ĵ����¼��Լ�ҵ��id
		����ֵ��ҵ�񵽴��¼�����ȥ�¼�
		���ܣ�����ҵ�����ȥ�¼���������ȥ�¼��ķ���ʱ��Ϊ�����¼�������ʱ�����ҵ�����ʱ��*/
	public abstract	Event generateLeavingevent(int Id,Event event);	

		//������ͨҵ����ʱ�¼�
	public abstract	Event generateDelayEvent(Event event);

		/*�βΣ�Event�͵��¼���double�͵��������ĸ��ؾ���ָ��
		����ֵ��int�͵�ҵ������
		���ܣ����¼��������ȥ���������ͷֱ������������CSO�㷨��RWA��
			  �ɹ���ͳ���������������ĵĸ��ؾ���ָ�����������·������Դ������������ҵ��ռ�е���Դ��
			  ʧ�����ҵ������; ��ȥ���ͷ���·������Դ������������ҵ��ռ�е���Դ*/
	public abstract	int dealWithEvent( Event event,final double d);

		/*�βΣ�Event�͵��¼���double�͵��������ĸ��ؾ���ָ��
		����ֵ��int�͵�ҵ������
		���ܣ���ҵ��ִ��CSO�㷨������Database�е�showPath��������RWA��*/
	public abstract	int caculatePath(final Event event , final double d);

		/*�βΣ��¼�event
		  ���ܣ�Ԥ����Դ, ����Database�е�reserveSource����*/
	public abstract	void reserveSource(Event event);

		/*�βΣ��¼�event
		  ���ܣ��ͷ���Դ, ����Database�е�relieveSource����*/
	public abstract	void relieveSource(Event event);

	public abstract	void readEdgeMap(String s);
	    
	public  int m_sumOfFailedService;		//������ҵ����Ŀ
	public	int m_HighQosFailedService;		//������ҵ����Ŀ
	public	int m_LowQosFailedService;		//������ҵ����Ŀ
	public	int m_FailedNetwork;			//��������ҵ����
	public	int m_FailedServer;				//Ӧ������ҵ����
	public	double sumOfDelay;				//��ʱ��
	public	double HighQosDelay;			//ʱ������ҵ��ʱ��
	public	double LowQosDelay;				//��ͨҵ��ʱ��
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
	 	

	 	/*���ͣ����ȼ����У���ʱ��ķ���ʱ������
		Ԫ�أ������ɵ�ҵ��ĵ����¼����Լ����ڷ����е�ҵ�����ȥ�¼�*/
    protected PriorityQueue<Event> m_pq;//��ʹ��greater�ȽϺ������������ش��ں�
    {
    	m_pq = new PriorityQueue<Event>();
    }

     abstract int  frand(int ia,int ib);
     abstract double arrive_time_gen (float beta);
     abstract void randomSrcDst(final int src, final int des);

		
	

}
