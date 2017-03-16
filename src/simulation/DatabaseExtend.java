package simulation;

//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseExtend extends Database{
	
	@Override
	public void initializeDB()
	{
		for(int i=0;i<MAXINUM;i++)
		{
			caculatePath(i);//计算所有节点最短路径树
		}

		for (int i=0;i<MAXINUM;i++)
			for (int j=0;j<MAXINUM;j++)
				for (int k=0;k<WaveNumber;k++)
					if(g_edge.edge[i][j] < INFINITE - 1 && g_edge.edge[i][j] !=0)
						m_resourceMap[i][j][k] = true;		//所有链路的所有波长均可用	

		for (int i=0;i<MAXINUM;i++)
		{	
			m_serverResourse[i] = 0;				//初始化各数据中心资源占有率为0
			m_srcMaxBandwidth[i] = srcMaxBandwidth;	//初始化各源节点可用带宽为初始值
			m_averMaxBandwidth[i] = srcMaxBandwidth;
			DBA_averBandwidthmax[i] = 0;			//初始化各源节点最大已用带宽为0
			DBA_averBandwidthRate[i].inum = 0;
			DBA_averBandwidthRate[i].dnum = 0;
			m_currentServicemaxNum[i] = 0;
			m_Oltresource[i] = 0;
			m_averOltresource[i] = 0;

			for(int j = 0; j < (int)( DBA_samServerNumRate * (double)(ServiceQuantity) ); j++)
			{
				DBA_SampDataSize[i][j] = 0;
				DBA_SampBandwidth[i][j] = 0;
				DBA_SampcurrentServiceNum[i][j] = 0;
			}
		}
		m_serviceNum = 0;
	}

	//读取文件
	@Override
	public void readEdgeMap(String fileName) throws IOException
	{
		
		Scanner scanner = new Scanner(new File(fileName));   
		Pattern pattern = Pattern.compile("\\d+");        // 存储找到的整数.     
		LinkedList<Integer> values = new LinkedList<Integer>();	     
		
		while (scanner.hasNextLine()) {           
			Matcher matcher = pattern.matcher(scanner.nextLine());          
			while (matcher.find()) {  	
			values.add ( Integer.parseInt(matcher.group(0)));
		
	          }
		  }
	
	    int num = 0;	
		for(int i=0;i<MAXINUM;i++)
		{
			for(int j=0;j<MAXINUM;j++)
			{
				 g_edge.edge[i][j] = values.get(num);	
				 num++;
			}		
	    }
		scanner.close();
	}


	//求v点的最短路径树,形参:0-13共14节点
	@Override
	public void caculatePath(int v)		
	{
		for(int i=0; i<MAXINUM; i++)
		{
			m_dist.dist[v][i] = g_edge.edge[v][i];
			m_shortestPoint.s[v][i] = 0;
			if(i!=v && m_dist.dist[v][i] < INFINITE -1)
			{
				m_path.path[v][i] = v;
			}
			else	
			{
				m_path.path[v][i] = -1;
			}
		}
		m_dist.dist[v][v] = 0;
		m_shortestPoint.s[v][v] = 1;			//初始化
		for(int i=0;i<MAXINUM-1;i++)
		{
			float min = INFINITE; 
			int u = v;
			for(int j=0;j<MAXINUM;j++)
			{
				if(m_shortestPoint.s[v][j] ==0 && m_dist.dist[v][j] < min)
				{
					u = j;
					min = m_dist.dist[v][j];
				}
			}
			m_shortestPoint.s[v][u] = 1;
			for(int w=0;w<MAXINUM;w++)
			{
				if(m_shortestPoint.s[v][w] == 0 && g_edge.edge[u][w]<INFINITE-1 && m_dist.dist[v][u] + g_edge.edge[u][w] < m_dist.dist[v][w])
				{
					m_dist.dist[v][w] = m_dist.dist[v][u] + g_edge.edge[u][w];
					m_path.path[v][w] = u;
				}
			}
		}
	}

	//动态带宽分配（汇聚层DBA策略）
	@Override
	public void caculateDBA(Event event)
	{
		
		int s = event.getSourceNode();	//当前业务源节点
		int id = event.getId();
		int DBA_samServerNum = (int)( DBA_samServerNumRate * (double)(ServiceQuantity) );	//采样数量
		int sampNum = id - (int)(id / DBA_samServerNum) * DBA_samServerNum;				//当前采样值的数组下标
		double DBA_tempBW[] = new double[MAXINUM];	
		double DBA_tempDS[] = new double[MAXINUM];	
		int[] DBA_tempcurrentServiceNum = new int[MAXINUM];
		double[]  DBA_averBandwidth = new double[MAXINUM];			//各源节点平均带宽
		double[] DBA_averDataSize = new double[MAXINUM];			//平均数据量
		int[] DBA_avercurrentServiceNum = new int[MAXINUM];		//当前服务列表中业务数量

		for(int i = 0; i < MAXINUM; i++)
		{
			DBA_tempBW[i] = 0;
			DBA_tempDS[i] = 0;
			DBA_tempcurrentServiceNum[i] = 0;
			DBA_averBandwidth[i] = 0;				//初始化各源节点已用带宽为0
			DBA_averDataSize[i] = 0;
			DBA_avercurrentServiceNum[i] = 0;

		}
		if(m_serviceNum != m_serviceEvent.size() || m_serviceNum > id)
//			cout << "error: serviceNum is error" << endl;
		System.out.println("error: serviceNum is error");
		//循环计算当前服务列表业务的数量/数据量/占用带宽
		for(Event e :m_serviceEvent){
			DBA_tempcurrentServiceNum[e.getSourceNode()] ++;					//采样当前服务列表业务数量
			DBA_tempDS[e.getSourceNode()] += e.getDataSize();		//采样当前服务列表业务数据量
			DBA_tempBW[e.getSourceNode()] += e.m_OltbandWidth;		//采样当前服务列表业务占用带宽
			
		}
//		for(vector<Event>::iterator pp = m_serviceEvent.begin(); pp != m_serviceEvent.end(); pp++)
//		{
//			DBA_tempcurrentServiceNum[(*pp).getSourceNode()] ++;					//采样当前服务列表业务数量
//			DBA_tempDS[(*pp).getSourceNode()] += (*pp).getDataSize();		//采样当前服务列表业务数据量
//			DBA_tempBW[(*pp).getSourceNode()] += (*pp).m_OltbandWidth;		//采样当前服务列表业务占用带宽
//		}
		for(int i = 0; i < MAXINUM; i++)
		{
			DBA_SampcurrentServiceNum[i][sampNum] = DBA_tempcurrentServiceNum[i];	//采样当前服务列表业务数量
			DBA_SampDataSize[i][sampNum] = DBA_tempDS[i];		//采样当前服务列表业务数据量,1,3,6,9,11先高后低
			DBA_SampBandwidth[i][sampNum] = DBA_tempBW[i];		//采样当前服务列表业务占用带宽
		}

		if(id >= ServiceQuantity * DBA_samServerNumRate *20)
		{
			
			//计算每个源节点的平均业务数/数据量/带宽使用
			for(int i = 0; i < MAXINUM; i++)
			{
				if( i != 0 && i != 4 && i != 8 && i != 12 )
				{
					for(int j =0; j< DBA_samServerNum; j++)
					{
						DBA_avercurrentServiceNum[i] += DBA_SampcurrentServiceNum[i][j];	//计算平均业务数量
						DBA_averDataSize[i] += DBA_SampDataSize[i][j];						//计算平均业务数据量
						DBA_averBandwidth[i] += DBA_SampBandwidth[i][j];					//计算平均带宽使用率
					}
					DBA_avercurrentServiceNum[i] /= DBA_samServerNum;
					DBA_averDataSize[i] /= DBA_samServerNum;
					DBA_averBandwidth[i] /= DBA_samServerNum;
					//DBA_averBandwidthRate[i].second = DBA_averBandwidth[i] / m_srcMaxBandwidth[i];
				}
				//DBA_averBandwidthRate[i].first = i;
			}

			//计算业务数方差
			double vari_currentServiceNum = 0;
			for(int i = 0; i < DBA_samServerNum; i++)
			{
				vari_currentServiceNum += DBA_SampcurrentServiceNum[s][i] * DBA_SampcurrentServiceNum[s][i] - DBA_avercurrentServiceNum[s] * DBA_avercurrentServiceNum[s];
			}
			vari_currentServiceNum /= DBA_samServerNum;

			//汇聚层资源调度策略
			switch(DBAMode)
			{

			//对照
			case 0:
				{
				
					/*------分配带宽，计算持续时间
					--------根据平均数据量、平均业务数、平均带宽占用率来预测下一时刻的数据量、业务数、带宽*/

					if(DBA_averBandwidth[s] / srcMaxBandwidth > 1 || m_Oltresource[s]  / srcMaxBandwidth > 1)
					{
						//带宽使用量高于1，error
						System.out.println("error: Note[" + s + "] OltBandwidth is full");
//						cout << "error: Note[" << s << "] OltBandwidth is full" << endl;
					}
					else if(DBA_averBandwidth[s] / srcMaxBandwidth <= 0.5 && m_Oltresource[s]  / srcMaxBandwidth <= 0.5)
					{
						//带宽使用量低于90%，按数据量大小固定分配
						if(DBA_averDataSize[s] > Expect_HighDataSize - 3 * Variance_HighDataSize)	//数据量高峰（μ-3σ,+∞）
						{
							event.m_OltbandWidth = srcMaxBandwidth * 0.12;
						}
						else
						{
							event.m_OltbandWidth = srcMaxBandwidth * 0.06;
						}
					}
					else if(DBA_averBandwidth[s] / srcMaxBandwidth <= 0.9 && m_Oltresource[s]  / srcMaxBandwidth <= 0.9)
					{
						//带宽使用量低于90%，按数据量大小固定分配
						if(DBA_averDataSize[s] > Expect_HighDataSize - 3 * Variance_HighDataSize)	//数据量高峰（μ-3σ,+∞）
						{
							event.m_OltbandWidth = srcMaxBandwidth * 0.08;
						}
						else
						{
							event.m_OltbandWidth = srcMaxBandwidth * 0.04;
						}
					}
					else
					{
						//带宽使用量高于90%，按可用带宽的10%分配
						event.m_OltbandWidth = (m_srcMaxBandwidth[s] - m_Oltresource[s]) * 0.1;
					}
					
					break;
				}

			//降低运营成本（保证每个源节点的分配带宽尽量低）
			case 1:
				{


					/*if(DBA_averBandwidth[s] / srcMaxBandwidth <= 0.5 && m_Oltresource[s]  / srcMaxBandwidth <= 0.5)
						
					else if(DBA_averBandwidth[s] / srcMaxBandwidth <= 0.9 && m_Oltresource[s]  / srcMaxBandwidth <= 0.9)
					{
						m_srcMaxBandwidth[s] = m_Oltresource[s] + srcMaxBandwidth * 0.05;
					}
					else
					{
						m_srcMaxBandwidth[s] = srcMaxBandwidth;
					}*/

					

					/*------分配带宽，计算持续时间
					--------根据平均数据量、平均业务数、平均带宽占用率来预测下一时刻的数据量、业务数、带宽*/

					if(m_Oltresource[s]  / srcMaxBandwidth > 1)
					{
						//带宽使用量高于1，error
//						cout << "error: Note[" << s << "] OltBandwidth is full" << endl;
						System.out.println("error: Note[" + s + "] OltBandwidth is full");
					}
					else if(DBA_averBandwidth[s] / srcMaxBandwidth <= 0.5 && m_Oltresource[s]  / srcMaxBandwidth <= 0.5)
					{
						//带宽使用量低于90%，按数据量大小固定分配
						if(DBA_averDataSize[s] > Expect_HighDataSize - 3 * Variance_HighDataSize)	//数据量高峰（μ-3σ,+∞）
						{
							m_srcMaxBandwidth[s] = m_Oltresource[s] + srcMaxBandwidth * 0.14;
							event.m_OltbandWidth = srcMaxBandwidth * 0.12;

						}
						else
						{
							m_srcMaxBandwidth[s] = m_Oltresource[s] + srcMaxBandwidth * 0.08;
							event.m_OltbandWidth = srcMaxBandwidth * 0.06;
						}
					}
					else if(DBA_averBandwidth[s] / srcMaxBandwidth <= 0.9 && m_Oltresource[s]  / srcMaxBandwidth <= 0.9)
					{
						//带宽使用量低于90%，按数据量大小固定分配
						if(DBA_averDataSize[s] > Expect_HighDataSize - 3 * Variance_HighDataSize)	//数据量高峰（μ-3σ,+∞）
						{
							m_srcMaxBandwidth[s] = m_Oltresource[s] + srcMaxBandwidth * 0.10;
							event.m_OltbandWidth = srcMaxBandwidth * 0.08;
						}
						else
						{
							m_srcMaxBandwidth[s] = m_Oltresource[s] + srcMaxBandwidth * 0.06;
							event.m_OltbandWidth = srcMaxBandwidth * 0.04;
						}
					}
					else
					{
						//带宽使用量高于90%，按可用带宽的10%分配
						m_srcMaxBandwidth[s] = srcMaxBandwidth;
						event.m_OltbandWidth = (m_srcMaxBandwidth[s] - m_Oltresource[s]) * 0.1;
					}
					

					break;
				}
			//提高用户体验（保证每个源节点的可用带宽尽量相同，比各源节点带宽使用率期望高10%以上的节点增加分配带宽，低10%以上的降低分配带宽）
			case 2:
				{
					//网络资源均衡算法
					double[] bandwidth = new double[MAXINUM];
					for(int i = 0; i < MAXINUM; i++)
					{
						bandwidth[i] = 0;
						if( i != 0 && i != 4 && i != 8 && i != 12 )
						{
							if(m_Oltresource[i] > DBA_averBandwidth[i])
								bandwidth[i] = m_Oltresource[i];
							else
								bandwidth[i] = DBA_averBandwidth[i];
						}
					}

					int sumof_BW_0 = 0;//统计带宽使用量为0的节点个数
					double sumof_BW = 0;
					double sumof_BWRate = 0;

					for(int i = 0; i < MAXINUM; i++)
					{
						if( i != 0 && i != 4 && i != 8 && i != 12 && bandwidth[i] < srcMaxBandwidth * 0.005)
						{
							m_srcMaxBandwidth[i] = srcMaxBandwidth * 0.05;
							sumof_BW_0++;
						}
					}
					
					for(int i = 0; i < MAXINUM; i++)
					{
						if( i != 0 && i != 4 && i != 8 && i != 12 && bandwidth[i] >= srcMaxBandwidth * 0.005)
						{
							sumof_BW += bandwidth[i];
						}
					}

					sumof_BWRate =  sumof_BW / ( (MAXINUM - SERVERNUMBER) * srcMaxBandwidth - sumof_BW_0 * srcMaxBandwidth * 0.05);
					for(int i = 0; i < MAXINUM; i++)
					{
						if( i != 0 && i != 4 && i != 8 && i != 12 && bandwidth[i] >= srcMaxBandwidth * 0.005)
						{
							m_srcMaxBandwidth[i] = bandwidth[i] / sumof_BWRate;
						}
					}
					
					

					/*------分配带宽，计算持续时间
					--------根据平均数据量、平均业务数、平均带宽占用率来预测下一时刻的数据量、业务数、带宽*/

					if( m_Oltresource[s]  / m_srcMaxBandwidth[s] > 1)
					{
						//带宽使用量高于1，error
//						cout << "error: Note[" << s << "] OltBandwidth is full" << endl;
						System.out.println("error: Note[" + s + "] OltBandwidth is full" );
					}
					else if(DBA_averBandwidth[s] / m_srcMaxBandwidth[s] <= 0.6 && m_Oltresource[s]  / m_srcMaxBandwidth[s] <= 0.6)
					{
						//带宽使用量低于90%，按数据量大小固定分配
						if(DBA_averDataSize[s] > Expect_HighDataSize - 3 * Variance_HighDataSize)	//数据量高峰（μ-3σ,+∞）
						{
							event.m_OltbandWidth =  (m_srcMaxBandwidth[s] - m_Oltresource[s])  * 0.8;
						}
						else
						{
							event.m_OltbandWidth =  (m_srcMaxBandwidth[s] - m_Oltresource[s])  * 0.6;
						}
					}
					else if(DBA_averBandwidth[s] / m_srcMaxBandwidth[s] <= 0.9 && m_Oltresource[s]  / m_srcMaxBandwidth[s] <= 0.9)
					{
						//带宽使用量低于90%，按数据量大小固定分配
						if(DBA_averDataSize[s] > Expect_HighDataSize - 3 * Variance_HighDataSize)	//数据量高峰（μ-3σ,+∞）
						{
							event.m_OltbandWidth =  (m_srcMaxBandwidth[s] - m_Oltresource[s])  * 0.5;
						}
						else
						{
							event.m_OltbandWidth =  (m_srcMaxBandwidth[s] - m_Oltresource[s])  * 0.3;
						}
					}
					else
					{
						//带宽使用量高于90%，按可用带宽的10%分配
						event.m_OltbandWidth = (m_srcMaxBandwidth[s] - m_Oltresource[s]) * 0.2;
					}
					
					break;
				}
			//智能调度
			case 3:
				{
				
					break;
				}
			}
		}
		else   
		{
			event.m_OltbandWidth = m_srcMaxBandwidth[s] * 0.2 / (ServiceQuantity * DBA_samServerNumRate *2);	//业务数量未达到2倍采样数量，不进行任何调度，直接按最小带宽分配。
		}

		m_Oltresource[s] += event.m_OltbandWidth;	//统计实时使用带宽
		if(m_Oltresource[s] > m_srcMaxBandwidth[s])
		{
			//带宽使用量高于1，error
			System.out.println("error2: Note[" + s + "] OltBandwidth is full");
//			cout << "error2: Note[" << s << "] OltBandwidth is full" << endl;
		}

		m_averOltresource[s] = ( m_averOltresource[s] + m_Oltresource[s] ) / 2;	//统计平均使用带宽
		//DBA_averBandwidthRate[s].second = DBA_averBandwidth[s] / m_srcMaxBandwidth[s];
		/*if(event.m_OltbandWidth > m_OltmaxBW)
		{
			event.m_OltbandWidth = m_OltmaxBW;
		}*/	
		for(int i = 0; i < MAXINUM; i++)
		{
			if( i != 0 && i != 4 && i != 8 && i != 12 )
			{
				m_averMaxBandwidth[i] = ( m_averMaxBandwidth[i] + m_srcMaxBandwidth[i] ) / 2;
			}
		}
		

		/*double sumofaver_maxBW = 0;
		for(int i = 0; i < MAXINUM; i++)
		{
			if( i != 0 && i != 4 && i != 8 && i != 12 )
			{
				sumofaver_maxBW += m_averMaxBandwidth[i];
			}
		}
		sumofaver_maxBW /= MAXINUM - SERVERNUMBER;
		if(sumofaver_maxBW < srcMaxBandwidth - 0.001 || sumofaver_maxBW > srcMaxBandwidth + 0.001)
			cout<<"error"<<endl;*/
		//int wavenum = int(event.m_OltbandWidth / BandWidthUnit) + 1;
		//event.setbandwidth(wavenum);
		event.setHoldTime( event.getDataSize() / event.m_OltbandWidth );


	}



	//释放波长资源和应用资源
	@Override
	public void relieveSource(Event event)
	{
		int s = event.getSourceNode();	//当前业务源节点
		int d = event.getDestNode();
		int band = event.m_bandwidth;
		//occupiedwave -= band;

		int previousNode;
		int laterNode;
		for ( int i = 1; i < event.m_workPath.size(); i++)
		{
			previousNode = (event.m_workPath.getFirst() + i - 1);
			laterNode = (event.m_workPath.getFirst() + i);
			for(int p:event.m_occupiedwave){
				m_resourceMap[previousNode][laterNode][p] = true;
			}
//			for(vector<int>::iterator p = event.m_occupiedwave.begin(); p != event.m_occupiedwave.end(); p++)
//			{
//				m_resourceMap[previousNode][laterNode][*p] = true;
//			}
		}
		m_serverResourse[d] = m_serverResourse[d]-event.getsourceoccupation();
		
	}

	//预留波长资源:在链路上标记被占用波长不可用
	@Override
	public void reserveSource(Event event)
	{
		int previousNode = event.getSourceNode();
		int d = event.getDestNode();
		int band = event.m_bandwidth;
		//occupiedwave += band;
		//if(m_occupiedwavemax<occupiedwave)
		//	m_occupiedwavemax = occupiedwave;

		int formerNode = m_path.path[d][previousNode];
		while (formerNode!=-1)
		{
			for (int p:event.m_occupiedwave){
				m_resourceMap[previousNode][formerNode][p] = false;
			}
//			for(vector<int>::iterator p = event.m_occupiedwave.begin(); p < event.m_occupiedwave.end(); p++)
//			{
//				m_resourceMap[previousNode][formerNode][*p] = false;
//			}
			previousNode = formerNode;
			formerNode = m_path.path[d][previousNode];
			
		}
	}

	//计算路径,返回 分配的波长 / -1节点不可达&无可用波长 / -2应用资源阻塞
	@Override
	public int showPath(Event event ,double resourse)
	{
		int s = event.getSourceNode();
		int d = event.getDestNode();
		int sumWaveNumber = WaveNumber;
		double sumServerResourse = 1;
		if(QosMode == 1)
		{
			 sumWaveNumber = (int)( (QosResoure*event.getQos() + 1-QosResoure) * WaveNumber );
			 sumServerResourse = QosResoure*event.getQos() + 1-QosResoure;
		}
		/*if(m_serverResourse[d] + event.getsourceoccupation() > sumServerResourse)
		{
			//cout << "服务器无资源，工作路径算路失败！" << endl;
			return -2;
		}

		if( m_dist[s][d]>INFINITE-1 )
		{
			//cout << "节点不可达" << endl;
			return -1;
		}*/
		
		int previousNode = s;
		int formerNode = m_path.path[d][previousNode];	//返回previousNode到d的下一跳节点,返回-1表明到达宿节点
		
		/*vector<int> vacantWave;	//用于计算可用波长
		for(int i = 0; i < sumWaveNumber; i++)
			vacantWave.push_back(i);

		vector<int>::iterator pp = vacantWave.begin();

		//存储路径 + 计算可用波长(所有路径必须使用同一波长)
		while (formerNode!=-1)
		{
			for(int i=0;i<sumWaveNumber || pp < vacantWave.end();i++)
			{
				if( m_resourceMap[previousNode][formerNode][i]==false)
				{
					
					vacantWave.erase(pp);	//删除被占用波长
				}

				if(vacantWave.size() < event.m_bandwidth)
				{
					//cout << "无可用波长" << endl;
					return -1;
				}
				pp++;
			}
			event.m_workPath.push_back(previousNode);	//存储路径
			previousNode = formerNode;
			formerNode = m_path[d][previousNode];
			event.changeHop();
		}*/

		Set<Integer> vacantWave = new HashSet();	//用于计算可用波长
		for(int i = 0; i < sumWaveNumber; i++)
		{
			//初始化为所有波长均可用
			vacantWave.add(i);	
		}

		//存储路径 + 计算可用波长(所有路径必须使用同一波长)
		while (formerNode!=-1)
		{
			for(int i=0;i<sumWaveNumber;i++)
			{
				if( m_resourceMap[previousNode][formerNode][i]==false)
				{
					vacantWave.remove(i);	//删除被占用波长
				}
				if(vacantWave.size() < event.m_bandwidth)
				{
					//cout << "无可用波长" << endl;
					return -1;
				}
			}
			event.m_workPath.push(previousNode);	//存储路径
			previousNode = formerNode;
			formerNode = m_path.path[d][previousNode];
			//event.changeHop();
		}
		event.m_workPath.push(previousNode);	//添加宿节点到路径
		m_serverResourse[d] += event.getsourceoccupation();	//标记业务占用的数据中心资源
		//cout << "resource:" << m_serverResourse[d] << endl;
		//resourse = caculateSource();				//计算数据中心资源均衡度
		//int reservedWave = *vacantWave.begin();		//取"可用波长"的第一个波长分配给当前业务
		//cout<<endl<<"---"<<"resource:"<<resourse<<"---"<<endl;
	    //return reservedWave;
//		set<int>::iterator ix = vacantWave.begin();
//		for( int j = 0; j < event.m_bandwidth; j++)
//		{
//			
//			event.m_occupiedwave.push_back(*ix);
//			ix++;
//		}
		for (int ix:vacantWave){
			event.m_occupiedwave.push(ix);
		}

		
		/*set<int>::iterator end = vacantWave.begin();
		for(set<int>::size_type ix = 0; ix < event.m_bandwidth; ix++)
		{
			end++;
		}
		copy(vacantWave.begin(), end, back_inserter(event.m_occupiedwave));*/
	    return 0;
	}


	//计算数据中心资源均衡度
	@Override
	public double caculateSource()
	{
		for(int i = 0; i + SERVERNUMBER < MAXINUM; i += SERVERNUMBER)
		{
			m_serverResourse[MAXINUM] = m_serverResourse[MAXINUM] + m_serverResourse[i];
			//cout<<"---"<<m_serverResourse[MAXINUM]<<"---"<<endl;
		}
		m_serverResourse[MAXINUM] = m_serverResourse[MAXINUM]/SERVERNUMBER;
		double re = 0;
		for(int i = 0; i + SERVERNUMBER < MAXINUM;i += SERVERNUMBER)
		{
			re = re + ( m_serverResourse[MAXINUM] - m_serverResourse[i] ) * ( m_serverResourse[MAXINUM] - m_serverResourse[i] );
		}
		re = re / SERVERNUMBER;
		//cout<<endl<<"---"<<"re:"<<re<<"---"<<endl;
		return re;
	}

	@Override
	public float getDist(int s,int d)
	{
		return m_dist.dist[s][d];
	}

	
	

}
