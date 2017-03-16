package simulation;

import java.util.LinkedList;


//enum EventType {
//	Arrival,End
//}
public class Event implements Comparable<Event>{
	
	public Event(){
		
	}
	public Event(EventType type,double tim, int i ){
		m_eventType = type;
		m_time = tim;
		m_id = i;
	}
	
	protected double m_time;				//ҵ��ĵ���ʱ��
	protected EventType m_eventType;		//ҵ�������(�������ȥ)
	protected double m_holdTime;			//ҵ��ĳ���ʱ��
	protected double m_sourceoccupation;	            //ҵ�����Դռ����
	
	
	public	int m_sourceNode;			//ҵ���Դ�ڵ�
	public	int m_destNode;				//ҵ����޽ڵ�
	public	int m_hop;					//ҵ�������
    public  int m_bandwidth;	       //ҵ�����(bit/s)

	public	int m_Qos;					//ҵ��QoS�ȼ���0 ��ͨҵ��1 ʱ������ҵ��
	public	double m_datasize;			//ҵ����������bit��
	public	double m_maxAllowDelay;		//ҵ����������ʱ��
	public	double m_queueTime;			//ҵ������ȼ������Ŷ�����
		
	public	int m_id;					//ҵ���id
	public	double m_OltbandWidth;		//�ֶ˵�����������ڵ��ռ�ô���
	public  LinkedList<Integer> m_workPath;		//ҵ�������·��
	public  LinkedList<Integer> m_occupiedwave;	//ҵ��ռ�õĲ���
	
	{
		m_workPath = new LinkedList<Integer>();
		m_occupiedwave = new LinkedList<Integer>();
	}
	
	double getTime() 
	{
		return m_time;
	}
	EventType getEventType() 
	{
		return m_eventType;
	}
	int getId()  
	{
		return m_id;
	}
	double getHoldTime() 
	{
		return m_holdTime;
	}
	double getsourceoccupation() 		
	{
		return m_sourceoccupation;
	}
	
	void setTime(double tim)
	{
		m_time = tim;
	}
	void setType(EventType type)
	{
		m_eventType = type;
	}
	void setHoldTime(double htime)
	{
		m_holdTime = htime;
	}
	void setsourceoccupation(double so)
	{
		m_sourceoccupation = so;
	}
	void setQos(int qos)
	{
		m_Qos = qos;
	}
	int getQos() 
	{
		return m_Qos;
	}
	void setDataSize(double datasize)
	{
		m_datasize = datasize;
	}
	double getDataSize() 
	{
		return m_datasize;
	}
	void setAllowDelay(double allowdelay)
	{
		m_maxAllowDelay = allowdelay;
	}
	double getAllowDelay() 
	{
		return m_maxAllowDelay;
	}
	int getbandwidth()
	{
		return m_bandwidth;
	}
	void setbandwidth(int bandwidth)
	{
		m_bandwidth = bandwidth;
	}

	//�ı��޽ڵ�--ѡ�������������
	void changeDestNode(int dest)
	{
		m_destNode = dest;
	}
	int getSourceNode() 
	{
		return m_sourceNode;
	}
	int getDestNode() 
	{
		return m_destNode;
	}
	void setResAndDest(int source, int dest)
	{
		m_sourceNode = source;
		m_destNode = dest;
	}
	void changeHop()
	{
		m_hop++;
	}
	void setHop(int h)
	{
		m_hop = h;
	}
	int getHop()
	{
		return m_hop;
	}
	@Override
	public int compareTo(Event event) {
		if(m_queueTime < event.m_queueTime){
			return +1;
		}else
			return 0;
	}

}
