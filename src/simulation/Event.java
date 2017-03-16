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
	
	protected double m_time;				//业务的到达时间
	protected EventType m_eventType;		//业务的类型(到达或离去)
	protected double m_holdTime;			//业务的持续时间
	protected double m_sourceoccupation;	            //业务的资源占有率
	
	
	public	int m_sourceNode;			//业务的源节点
	public	int m_destNode;				//业务的宿节点
	public	int m_hop;					//业务的跳数
    public  int m_bandwidth;	       //业务带宽(bit/s)

	public	int m_Qos;					//业务QoS等级，0 普通业务；1 时延敏感业务
	public	double m_datasize;			//业务数据量（bit）
	public	double m_maxAllowDelay;		//业务的最大允许时延
	public	double m_queueTime;			//业务的优先级队列排队因子
		
	public	int m_id;					//业务的id
	public	double m_OltbandWidth;		//局端到核心网接入节点的占用带宽
	public  LinkedList<Integer> m_workPath;		//业务的完整路由
	public  LinkedList<Integer> m_occupiedwave;	//业务占用的波长
	
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

	//改变宿节点--选择最佳数据中心
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
