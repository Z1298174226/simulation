package simulation;

import java.util.Random;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Math;

public class SimulationExtend extends Simulation {
	
	
	
	public SimulationExtend(double start) {
		super(start);
		// TODO Auto-generated constructor stub
	}
	Random r =new Random();
	Random r1 = new Random(1);
	@Override
	public int frand(int ia,int ib)
	{
		double fr;	
		fr = (double)r.nextDouble();	
		return (int) ((ib+1-ia)*fr+ia);	
	}
	@Override
	//生成高斯分布随机数(期望为0.0，方差为1.0), X = gaussrand() * V + E(期望为E，方差为V)
	public double gaussrand(){
		double V1 = 0.0, V2 = 0.0 , S = 0.0;
		double X;
		int phase = 0;
		 if ( phase == 0 ) {
	       while(S >=1 || S == 0){
	            double U1 = r.nextDouble();
	            double U2 = r.nextDouble();
	             
	            V1 = 2 * U1 - 1;
	            V2 = 2 * U2 - 1;
	            S = V1 * V1 + V2 * V2;
	        }
	         
	        X = V1 * Math.sqrt(-2 * Math.log(S) / S);
	    } else
	        X = V2 * Math.sqrt(-2 * Math.log(S) / S);
	         
	    phase = 1 - phase;
		return X;
	}
	
//	public double gaussrand() {
//	    static double V1, V2, S;
//	    static int phase = 0;
//	    double X;
////	    if ( phase == 0 ) {
////	       while(S >=1)|| S == 0){
////	            double U1 = r.nextDouble();
////	            double U2 = r.nextDouble();
////	             
////	            V1 = 2 * U1 - 1;
////	            V2 = 2 * U2 - 1;
////	            S = V1 * V1 + V2 * V2;
////	        }
////	         
////	        X = V1 * sqrt(-2 * log(S) / S);
////	    } else
////	        X = V2 * sqrt(-2 * log(S) / S);
////	         
////	    phase = 1 - phase;
//	 
//	    return X;
//	}

	//随机产生业务的资源占用率(0.01~0.01+2*afar)
	@Override
	public double randoccu()
	{
		double fr;	
		fr = (double)r.nextDouble()*afar*2 + 0.01;	
		return fr;
	}

	//随机生成业务数据量(高斯分布随机数)
	@Override
	public double randdatasize(final int src, int n_id)
	{
		double dsize;
		do
		{
			if(src == 1 || src == 3 || src == 6 || src == 9 || src == 11 )	
			{
				if(n_id <= ServiceQuantity / 2)	//先忙再闲
					dsize = gaussrand() * Variance_HighDataSize * Variance_HighDataSize + Expect_HighDataSize;
				//else if(n_lam <= 2 * maxlam / 3)
					//dsize = gaussrand() * Variance_MidDataSize * Variance_MidDataSize + Expect_MidDataSize1;
				else
					dsize = gaussrand() * Variance_LowDataSize * Variance_LowDataSize + Expect_LowDataSize;
			}
			else
			{
				if(n_id <= ServiceQuantity / 2)	//先忙再闲
					dsize = gaussrand() * Variance_LowDataSize * Variance_LowDataSize + Expect_LowDataSize;
				//else if(n_lam <= 2 * maxlam / 3)
					//dsize = gaussrand() * Variance_MidDataSize * Variance_MidDataSize + Expect_MidDataSize2;
				else
					dsize = gaussrand() * Variance_HighDataSize * Variance_HighDataSize + Expect_HighDataSize;
			}
		}while(dsize <= 0);
		
		return dsize;
	}

	//随机生成最大允许时延(Qos:0 100-500s; Qos:1 1-10s)
	@Override
	public double randallowdelay(final int qos)
	{
		double delay;
		if(qos == 0)
			delay = (double)r.nextDouble()*400 + 100;
		else
			delay = (double)r.nextDouble()*199 + 1;//1-200s
		return delay;
	}

	//随机生成业务QoS等级(0,1)
	@Override
	public int randqos()
	{
		double qos;	
		qos = (double)r.nextDouble() * 2 ;	
		return (int)(qos) % 2;
	}


	//随机产生下一个业务到达时间
	
	 double arrive_time_gen (double m_lamda)
	{
		double u, x, ln;
		u = r.nextDouble();
		ln = Math.log(u);
		
		x = m_lamda * ln;
		x= -1 * x ;
		//cout << "随机产生下一个业务到达时间: "<< x << endl;
		return x;
	}

	//随机生成源宿节点(不能相同且宿节点为数据中心0,4,8,12)
	@Override
	public void randomSrcDst( int src,  int des)
	{
		des = frand(0, SERVERNUMBER-1) * 4;
		do
		{
			src = frand(0, MAXINUM-1);
		}
		while (src == 0 || src == 4 || src == 8 || src == 12);
	}

	//生成业务到达事件
	@Override
	public void generateServiceEventPair(int Id)
	{
		double	arriveTime = m_currentTime + arrive_time_gen(m_lamda);
		double	holdTime = arrive_time_gen(m_rou);	
		double	sourceOccu = randoccu();
		//int		Qos = randqos();
		int		Qos = 1;
		double	allowDelay = randallowdelay(Qos);
		int		source = 0, destination = 0;
		randomSrcDst(source, destination);
		double	dataSize = randdatasize(source,Id);  

		Event event0 = new Event(EventType.Arrival,arriveTime, Id);
		event0.m_queueTime = arriveTime;
		event0.setHoldTime(holdTime);
		event0.setResAndDest(source, destination);
		event0.setsourceoccupation(sourceOccu);
		//event0.m_ocuppiedWave = WaveNumber;
		event0.setbandwidth(1);
		event0.setHop(1);
		event0.setAllowDelay(allowDelay);
		event0.setDataSize(dataSize);
		event0.setQos(Qos);
		event0.m_OltbandWidth = 0;
		m_pq.add(event0);	//添加业务到达事件到优先级队列
		sumofservice++;
		if(Id == 796)
		  {int i = 0;}
		/*if(Qos)
			HighQosQuantity++;
		else
			LowQosQuantity++;*/			
	}

	//生成业务离去事件
	public Event generateLeavingevent(int Id,Event event)
	{
		Event event1 = new Event();
		event1 = event;
		event1.setType(EventType.End);
		event1.setTime(event.m_queueTime + event.getHoldTime());	//"业务离去事件的发生时间" = "业务到达事件发生的时间" + "业务持续时间"
		event1.m_queueTime = event1.getTime();
		return event1;
	}

	//生成普通业务延时事件
	public Event generateDelayEvent(Event event)
	{
		Event event1 = new Event();
		event1 = event;
		event1.m_queueTime = event1.m_queueTime + Delay;		//"下一跳事件的发生时间" = "上一跳事件的发生时间" + "路由排队时间"m_routequeue[event.getSourceNode()]
		return event1;
	}

	//读取文件"map.data"
	public void readEdgeMap(String fileName)
	{
		try{
		m_database.readEdgeMap(fileName);
		}catch(IOException e){
		System.out.println("IOException");
		}
	}

	//跨层优化算路
	public int caculatePath(final Event event, final double resourse)
	{
		//cout << "Now is caculating the m_path for service: " << event.getId() 
			//<< "......" << endl;	
		Event servive = new Event();
		servive = event;

		int dest = servive.getDestNode();
		int sour = servive.getSourceNode();

		switch(caculateMode)
		{
			//随机(不改变目的数据中心)
			case 0:
				 break;

			//应用优化(选取应用资源负载最低的数据中心为宿节点)
			case 1:
				{
				
					//cout<<"destformer:"<<servive.getDestNode()<<endl;
					//cout<<"---"<<m_database.m_serverResourse[dest]<<"---"<<endl;
					double sr = m_database.m_serverResourse[dest];
					for(int i = 0; i < MAXINUM; i += SERVERNUMBER)
					{	
						if(sr > m_database.m_serverResourse[i])
						{
							sr = m_database.m_serverResourse[i];
							dest = i;
						}
						//cout<<"---"<<m_database.m_serverResourse[i+SERVERNUMBER]<<"---"<<endl;
					}
					//cout<<"dest:"<<dest<<endl;
					
					servive.changeDestNode(dest);
					break;
				}

			//网络优化(选取路径跳数最短的数据中心为宿节点)
			case 2:
				 {
					//cout<<"destformer:"<<dest<<endl;
					double sr = m_database.getDist(sour,dest);
					for(int i = 0; i < MAXINUM; i += SERVERNUMBER)
					{
						//cout<<"dist:"<<i<<"---"<<m_database.getDist(sour,i)<<endl;
						if(sr > m_database.getDist(sour,i))
						{
							sr = m_database.getDist(sour,i);
							dest = i;
						}
					}
					servive.changeDestNode(dest);
					//cout<<"dest:"<<servive.getDestNode()<<endl;
					break;
				 }

			//CSO跨层优化(综合考虑应用资源与网络资源)
			case 3:
				 {
					//cout<<"destformer:"<<servive.getDestNode()<<endl;
					double maxOccup = 0;
					float maxDist = 0;
					double Optimi[] = new double[SERVERNUMBER];
					double minOptimi = 1;
					for(int i = 0; i < MAXINUM; i += SERVERNUMBER)
					{
						if( maxOccup < m_database.m_serverResourse[i] )
							maxOccup = m_database.m_serverResourse[i];
						if( maxDist < m_database.getDist(sour,i) )
							maxDist = m_database.getDist(sour,i);
						//cout<<"maxOccup:"<<maxOccup<<"---"<<endl;
						//cout<<"maxDist:"<<maxDist<<"---"<<endl;
					}
					if(maxOccup == 0)
						maxOccup = 1;
					for(int i = 0; i < SERVERNUMBER; i++)
					{
						Optimi[i] = beta * ( m_database.m_serverResourse[i*SERVERNUMBER] / maxOccup ) + ( 1-beta ) * ( m_database.getDist(sour, i*SERVERNUMBER) / maxDist );
						if(minOptimi > Optimi[i])
						{
							minOptimi = Optimi[i];
							dest = i * SERVERNUMBER;
						}
						//cout<<"opti:"<<Optimi[i]<<"---"<<endl;
					}
					//cout<<"optimi:"<<minOptimi<<endl;
					servive.changeDestNode(dest);
					//cout<<"dest:"<<servive.getDestNode()<<endl;
					break;
				 }
			//MRMR --Floyd
			case 4:
			{

			}
		}

		

		return 0;
		/*
		int reg1 = m_database.showPath(event,resourse);	//计算路径, -1节点不可达&无可用波长 / -2应用资源阻塞
		if(reg1 == -1)
		{
			//节点不可达&无可用波长
			//servive.m_ocuppiedWave = WaveNumber;	
			return -1;
		}
		else if(reg1 == -2)
		{
			//应用资源阻塞
			//servive.m_ocuppiedWave = WaveNumber;
			return -2;
		}
		else
		{
			//servive.m_ocuppiedWave = reg1;		//设置业务占用波长,完成分配波长资源
			//int s = servive.getSourceNode();
			//int d = servive.getDestNode();
			m_database.reserveSource(event);	//预留波长资源:在链路上标记被占用波长不可用
			return 0;
		}*/
		
	}


	//按照优先因子处理优先级队列事件, 返回业务的跳数
	public int dealWithEvent(Event event,final double resourse)
	{
		
		m_currentTime = event.getTime();
		int h = 0;
		switch(event.getEventType())
		{
		//业务到达事件	
		case Arrival:
			{ 		
				//cout <<"Arriving: " << event.getId() << " " << event.getSourceNode() 
				//	<< ' ' << ' ' << event.getDestNode() <<' '
				//	<< event.getTime() <<" "<< event.getHoldTime()<<' '<<event.getsourceoccupation()<< endl;
               // System.out.println("Arrival");
				caculatePath( event , resourse );			
				m_database.caculateDBA(event);//动态带宽分配（汇聚层DBA策略）
				Sumof_HoldTime = Sumof_HoldTime + event.getHoldTime();
                Sumof_ResOccu = Sumof_ResOccu + event.getsourceoccupation();
				m_database.m_serviceEvent.push(event);
				m_database.m_serviceNum++;
                //生成离去事件
				m_pq.add(generateLeavingevent(event.getId(),event));

				if( m_nextServiceId < ServiceQuantity )
				{
					generateServiceEventPair(m_nextServiceId);
					m_nextServiceId++;
				}
				else{
					return -2;
				}

				int oldid = m_pq.peek().m_id;
				EventType oldtype = m_pq.peek().getEventType();

				m_pq.remove();

				if(m_pq.peek().m_id == oldid && m_pq.peek().getEventType() == oldtype){
					//System.out.println("error : delete event error");
					}
				break;
			}
		
		//业务离去事件	
		case End:
			{		
				//System.out.println("End");
				// if( m_currentServiceId.count( event.getId() ) !=0 )
				// {
				//cout <<"Ending: " << event.getId() << " " << event.getSourceNode()
				//	<< ' '  << event.getDestNode() << ' '
				//	<< event.getTime() <<" "<< event.getHoldTime() <<' '<<event.getsourceoccupation()<< endl;
				//m_database.relieveSource(event);		//释放波长资源和应用资源
				int s = event.getSourceNode();
				m_database.m_Oltresource[s] -= event.m_OltbandWidth;	//统计实时带宽利用率
				m_database.m_averOltresource[s] = ( m_database.m_averOltresource[s] + m_database.m_Oltresource[s] ) / 2;	//统计平均使用带宽
				
				
				//for(int i=0;i<14;i+=4)
				//	cout<<"---"<<m_database.m_serverResourse[i]<<"---"<<endl;
				m_currentServiceId.remove(event.getId());
				for (Event e :m_database.m_serviceEvent){
					if(e.getId() == event.getId()){
						m_database.m_serviceEvent.remove(e);
						break;
				 }
				}
				

				/*for(vector<Event>::iterator pp = m_database.m_serviceEvent.begin(); pp != m_database.m_serviceEvent.end(); pp++)
				{
					if( (*pp).getId() == event.getId() )
					{
						cout<<"error: delete End event error"<<endl;
					}
				}*/

				m_database.m_serviceNum--;

				m_pq.remove();
				break;
			}
			
		}
		return h;
	}
//		return h;
	
   

	//释放波长资源和应用资源
	/*void Simulation::relieveSource(Event event)
	{
		Event servive = event;
		int wave = servive.m_ocuppiedWave;
		int size = servive.m_workPath.size();
		int workp[10];
		int d = servive.getDestNode();
		for(int i=0;i<size&&i<10;i++)
		{
			workp[i]=servive.m_workPath[i];
		}
		                                                                                      
		m_database.relieveSource(event);
		m_database.m_serverResourse[d] = m_database.m_serverResourse[d]-event.getsourceoccupation();
		
	}*/

	//仿真
	public void run(PrintWriter out)
	{
		int hop = 0;			//网络跳数
		double resourse = 0;	//数据中心资源均衡度
		double sourse = 0;		//数据中心资源均衡度
//		srand(1);				//随机数发生器函数
		r1.nextInt();
		m_sumOfFailedService = 0;
		m_HighQosFailedService= 0;
		m_LowQosFailedService = 0;
		m_FailedNetwork = 0;
		m_FailedServer = 0;
		sumOfDelay = 0;
		HighQosDelay = 0;
		LowQosDelay = 0;
		//m_occupiedwavemax = 0;
		//occupiedwave = 0;
		HighQosQuantity = 0;
		LowQosQuantity = 0;
		Sumof_averOltresource = 0;
		Sumof_averMaxBandwidth = 0;
		Sumof_HoldTime = 0;
		Sumof_averurrentServiceNum =0;
		sumofservice = 0;

		//生成第一个业务到达事件(id = 0)
		m_nextServiceId = 0;
		generateServiceEventPair(m_nextServiceId);
		m_nextServiceId++;

		while (!m_pq.isEmpty())
		{
			resourse = 0;
			int h = dealWithEvent( (Event)m_pq.peek(), resourse );		//按照优先因子处理优先级队列事件
			if(h == -2)
				break;
			hop += h;
			//cout<<"---"<<resourse<<"---"<<endl;
			sourse += resourse;
		}
		//计算统计量
		for(int i = 0; i < MAXINUM; i++)
		{
			if( i != 0 && i != 4 && i != 8 && i != 12 )
			{
				Sumof_averOltresource =Sumof_averOltresource + m_database.m_averOltresource[i];
				Sumof_averMaxBandwidth += m_database.m_averMaxBandwidth[i];
				//Sumof_averurrentServiceNum += m_database.DBA_avercurrentServiceNum[i];
			}
		}
		Sumof_averOltresource /= MAXINUM - SERVERNUMBER;	//平均使用带宽量
		Sumof_averMaxBandwidth /= MAXINUM - SERVERNUMBER;	//平均分配带宽（运营商成本）
		//Sumof_averurrentServiceNum /= MAXINUM - SERVERNUMBER;
		
//		PrintWriter out =new PrintWriter(new BufferedWriter(new FileWriter(file)));
//		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		//写入数据到文件"result.data"
		out.println("\t"+(double)Sumof_averOltresource +"\t"+(double)Sumof_averMaxBandwidth+"\t"
				+(double)Sumof_averOltresource / Sumof_averMaxBandwidth+"\t"+(double)Sumof_HoldTime / ServiceQuantity);
		
	//    out.close();
	
		//ofile.close();

		//打印数据到屏幕显示
		//<<"the caculate mode is:"<< '\n' <<caculateMode<<endl;
		//cout<<"the service quantity is:"<< '\n' <<ServiceQuantity<<endl;
		//cout << "the service  quantity     is: "<< Sumof_averurrentServiceNum <<endl;
		 System.out.println(hop);
	    System.out.println("the resource utilization  is: " + (double)Sumof_averOltresource / Sumof_averMaxBandwidth + "= " +
			(double)Sumof_averOltresource + " / " + (double)Sumof_averMaxBandwidth );	//带宽资源利用率
	    System.out.println("the average  of HoldTime  is: " + (double)Sumof_HoldTime / ServiceQuantity);
	    System.out.println("the average  of Occupation  is: " + (double)Sumof_ResOccu / ServiceQuantity);
		System.out.println();
		
	}
	@Override
	public void reserveSource(Event event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void relieveSource(Event event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	double arrive_time_gen(float beta) {
		double u, x, ln;
		u = r.nextDouble();
		ln = Math.log(u);
		
		x = beta * ln;
		x= -1 * x ;
		//cout << "随机产生下一个业务到达时间: "<< x << endl;
		return x;
	}
	
}


