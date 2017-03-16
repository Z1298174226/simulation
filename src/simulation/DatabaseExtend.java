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
			caculatePath(i);//�������нڵ����·����
		}

		for (int i=0;i<MAXINUM;i++)
			for (int j=0;j<MAXINUM;j++)
				for (int k=0;k<WaveNumber;k++)
					if(g_edge.edge[i][j] < INFINITE - 1 && g_edge.edge[i][j] !=0)
						m_resourceMap[i][j][k] = true;		//������·�����в���������	

		for (int i=0;i<MAXINUM;i++)
		{	
			m_serverResourse[i] = 0;				//��ʼ��������������Դռ����Ϊ0
			m_srcMaxBandwidth[i] = srcMaxBandwidth;	//��ʼ����Դ�ڵ���ô���Ϊ��ʼֵ
			m_averMaxBandwidth[i] = srcMaxBandwidth;
			DBA_averBandwidthmax[i] = 0;			//��ʼ����Դ�ڵ�������ô���Ϊ0
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

	//��ȡ�ļ�
	@Override
	public void readEdgeMap(String fileName) throws IOException
	{
		
		Scanner scanner = new Scanner(new File(fileName));   
		Pattern pattern = Pattern.compile("\\d+");        // �洢�ҵ�������.     
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


	//��v������·����,�β�:0-13��14�ڵ�
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
		m_shortestPoint.s[v][v] = 1;			//��ʼ��
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

	//��̬������䣨��۲�DBA���ԣ�
	@Override
	public void caculateDBA(Event event)
	{
		
		int s = event.getSourceNode();	//��ǰҵ��Դ�ڵ�
		int id = event.getId();
		int DBA_samServerNum = (int)( DBA_samServerNumRate * (double)(ServiceQuantity) );	//��������
		int sampNum = id - (int)(id / DBA_samServerNum) * DBA_samServerNum;				//��ǰ����ֵ�������±�
		double DBA_tempBW[] = new double[MAXINUM];	
		double DBA_tempDS[] = new double[MAXINUM];	
		int[] DBA_tempcurrentServiceNum = new int[MAXINUM];
		double[]  DBA_averBandwidth = new double[MAXINUM];			//��Դ�ڵ�ƽ������
		double[] DBA_averDataSize = new double[MAXINUM];			//ƽ��������
		int[] DBA_avercurrentServiceNum = new int[MAXINUM];		//��ǰ�����б���ҵ������

		for(int i = 0; i < MAXINUM; i++)
		{
			DBA_tempBW[i] = 0;
			DBA_tempDS[i] = 0;
			DBA_tempcurrentServiceNum[i] = 0;
			DBA_averBandwidth[i] = 0;				//��ʼ����Դ�ڵ����ô���Ϊ0
			DBA_averDataSize[i] = 0;
			DBA_avercurrentServiceNum[i] = 0;

		}
		if(m_serviceNum != m_serviceEvent.size() || m_serviceNum > id)
//			cout << "error: serviceNum is error" << endl;
		System.out.println("error: serviceNum is error");
		//ѭ�����㵱ǰ�����б�ҵ�������/������/ռ�ô���
		for(Event e :m_serviceEvent){
			DBA_tempcurrentServiceNum[e.getSourceNode()] ++;					//������ǰ�����б�ҵ������
			DBA_tempDS[e.getSourceNode()] += e.getDataSize();		//������ǰ�����б�ҵ��������
			DBA_tempBW[e.getSourceNode()] += e.m_OltbandWidth;		//������ǰ�����б�ҵ��ռ�ô���
			
		}
//		for(vector<Event>::iterator pp = m_serviceEvent.begin(); pp != m_serviceEvent.end(); pp++)
//		{
//			DBA_tempcurrentServiceNum[(*pp).getSourceNode()] ++;					//������ǰ�����б�ҵ������
//			DBA_tempDS[(*pp).getSourceNode()] += (*pp).getDataSize();		//������ǰ�����б�ҵ��������
//			DBA_tempBW[(*pp).getSourceNode()] += (*pp).m_OltbandWidth;		//������ǰ�����б�ҵ��ռ�ô���
//		}
		for(int i = 0; i < MAXINUM; i++)
		{
			DBA_SampcurrentServiceNum[i][sampNum] = DBA_tempcurrentServiceNum[i];	//������ǰ�����б�ҵ������
			DBA_SampDataSize[i][sampNum] = DBA_tempDS[i];		//������ǰ�����б�ҵ��������,1,3,6,9,11�ȸߺ��
			DBA_SampBandwidth[i][sampNum] = DBA_tempBW[i];		//������ǰ�����б�ҵ��ռ�ô���
		}

		if(id >= ServiceQuantity * DBA_samServerNumRate *20)
		{
			
			//����ÿ��Դ�ڵ��ƽ��ҵ����/������/����ʹ��
			for(int i = 0; i < MAXINUM; i++)
			{
				if( i != 0 && i != 4 && i != 8 && i != 12 )
				{
					for(int j =0; j< DBA_samServerNum; j++)
					{
						DBA_avercurrentServiceNum[i] += DBA_SampcurrentServiceNum[i][j];	//����ƽ��ҵ������
						DBA_averDataSize[i] += DBA_SampDataSize[i][j];						//����ƽ��ҵ��������
						DBA_averBandwidth[i] += DBA_SampBandwidth[i][j];					//����ƽ������ʹ����
					}
					DBA_avercurrentServiceNum[i] /= DBA_samServerNum;
					DBA_averDataSize[i] /= DBA_samServerNum;
					DBA_averBandwidth[i] /= DBA_samServerNum;
					//DBA_averBandwidthRate[i].second = DBA_averBandwidth[i] / m_srcMaxBandwidth[i];
				}
				//DBA_averBandwidthRate[i].first = i;
			}

			//����ҵ��������
			double vari_currentServiceNum = 0;
			for(int i = 0; i < DBA_samServerNum; i++)
			{
				vari_currentServiceNum += DBA_SampcurrentServiceNum[s][i] * DBA_SampcurrentServiceNum[s][i] - DBA_avercurrentServiceNum[s] * DBA_avercurrentServiceNum[s];
			}
			vari_currentServiceNum /= DBA_samServerNum;

			//��۲���Դ���Ȳ���
			switch(DBAMode)
			{

			//����
			case 0:
				{
				
					/*------��������������ʱ��
					--------����ƽ����������ƽ��ҵ������ƽ������ռ������Ԥ����һʱ�̵���������ҵ����������*/

					if(DBA_averBandwidth[s] / srcMaxBandwidth > 1 || m_Oltresource[s]  / srcMaxBandwidth > 1)
					{
						//����ʹ��������1��error
						System.out.println("error: Note[" + s + "] OltBandwidth is full");
//						cout << "error: Note[" << s << "] OltBandwidth is full" << endl;
					}
					else if(DBA_averBandwidth[s] / srcMaxBandwidth <= 0.5 && m_Oltresource[s]  / srcMaxBandwidth <= 0.5)
					{
						//����ʹ��������90%������������С�̶�����
						if(DBA_averDataSize[s] > Expect_HighDataSize - 3 * Variance_HighDataSize)	//�������߷壨��-3��,+�ޣ�
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
						//����ʹ��������90%������������С�̶�����
						if(DBA_averDataSize[s] > Expect_HighDataSize - 3 * Variance_HighDataSize)	//�������߷壨��-3��,+�ޣ�
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
						//����ʹ��������90%�������ô����10%����
						event.m_OltbandWidth = (m_srcMaxBandwidth[s] - m_Oltresource[s]) * 0.1;
					}
					
					break;
				}

			//������Ӫ�ɱ�����֤ÿ��Դ�ڵ�ķ���������ͣ�
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

					

					/*------��������������ʱ��
					--------����ƽ����������ƽ��ҵ������ƽ������ռ������Ԥ����һʱ�̵���������ҵ����������*/

					if(m_Oltresource[s]  / srcMaxBandwidth > 1)
					{
						//����ʹ��������1��error
//						cout << "error: Note[" << s << "] OltBandwidth is full" << endl;
						System.out.println("error: Note[" + s + "] OltBandwidth is full");
					}
					else if(DBA_averBandwidth[s] / srcMaxBandwidth <= 0.5 && m_Oltresource[s]  / srcMaxBandwidth <= 0.5)
					{
						//����ʹ��������90%������������С�̶�����
						if(DBA_averDataSize[s] > Expect_HighDataSize - 3 * Variance_HighDataSize)	//�������߷壨��-3��,+�ޣ�
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
						//����ʹ��������90%������������С�̶�����
						if(DBA_averDataSize[s] > Expect_HighDataSize - 3 * Variance_HighDataSize)	//�������߷壨��-3��,+�ޣ�
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
						//����ʹ��������90%�������ô����10%����
						m_srcMaxBandwidth[s] = srcMaxBandwidth;
						event.m_OltbandWidth = (m_srcMaxBandwidth[s] - m_Oltresource[s]) * 0.1;
					}
					

					break;
				}
			//����û����飨��֤ÿ��Դ�ڵ�Ŀ��ô�������ͬ���ȸ�Դ�ڵ����ʹ����������10%���ϵĽڵ����ӷ��������10%���ϵĽ��ͷ������
			case 2:
				{
					//������Դ�����㷨
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

					int sumof_BW_0 = 0;//ͳ�ƴ���ʹ����Ϊ0�Ľڵ����
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
					
					

					/*------��������������ʱ��
					--------����ƽ����������ƽ��ҵ������ƽ������ռ������Ԥ����һʱ�̵���������ҵ����������*/

					if( m_Oltresource[s]  / m_srcMaxBandwidth[s] > 1)
					{
						//����ʹ��������1��error
//						cout << "error: Note[" << s << "] OltBandwidth is full" << endl;
						System.out.println("error: Note[" + s + "] OltBandwidth is full" );
					}
					else if(DBA_averBandwidth[s] / m_srcMaxBandwidth[s] <= 0.6 && m_Oltresource[s]  / m_srcMaxBandwidth[s] <= 0.6)
					{
						//����ʹ��������90%������������С�̶�����
						if(DBA_averDataSize[s] > Expect_HighDataSize - 3 * Variance_HighDataSize)	//�������߷壨��-3��,+�ޣ�
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
						//����ʹ��������90%������������С�̶�����
						if(DBA_averDataSize[s] > Expect_HighDataSize - 3 * Variance_HighDataSize)	//�������߷壨��-3��,+�ޣ�
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
						//����ʹ��������90%�������ô����10%����
						event.m_OltbandWidth = (m_srcMaxBandwidth[s] - m_Oltresource[s]) * 0.2;
					}
					
					break;
				}
			//���ܵ���
			case 3:
				{
				
					break;
				}
			}
		}
		else   
		{
			event.m_OltbandWidth = m_srcMaxBandwidth[s] * 0.2 / (ServiceQuantity * DBA_samServerNumRate *2);	//ҵ������δ�ﵽ2�������������������κε��ȣ�ֱ�Ӱ���С������䡣
		}

		m_Oltresource[s] += event.m_OltbandWidth;	//ͳ��ʵʱʹ�ô���
		if(m_Oltresource[s] > m_srcMaxBandwidth[s])
		{
			//����ʹ��������1��error
			System.out.println("error2: Note[" + s + "] OltBandwidth is full");
//			cout << "error2: Note[" << s << "] OltBandwidth is full" << endl;
		}

		m_averOltresource[s] = ( m_averOltresource[s] + m_Oltresource[s] ) / 2;	//ͳ��ƽ��ʹ�ô���
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



	//�ͷŲ�����Դ��Ӧ����Դ
	@Override
	public void relieveSource(Event event)
	{
		int s = event.getSourceNode();	//��ǰҵ��Դ�ڵ�
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

	//Ԥ��������Դ:����·�ϱ�Ǳ�ռ�ò���������
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

	//����·��,���� ����Ĳ��� / -1�ڵ㲻�ɴ�&�޿��ò��� / -2Ӧ����Դ����
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
			//cout << "����������Դ������·����·ʧ�ܣ�" << endl;
			return -2;
		}

		if( m_dist[s][d]>INFINITE-1 )
		{
			//cout << "�ڵ㲻�ɴ�" << endl;
			return -1;
		}*/
		
		int previousNode = s;
		int formerNode = m_path.path[d][previousNode];	//����previousNode��d����һ���ڵ�,����-1���������޽ڵ�
		
		/*vector<int> vacantWave;	//���ڼ�����ò���
		for(int i = 0; i < sumWaveNumber; i++)
			vacantWave.push_back(i);

		vector<int>::iterator pp = vacantWave.begin();

		//�洢·�� + ������ò���(����·������ʹ��ͬһ����)
		while (formerNode!=-1)
		{
			for(int i=0;i<sumWaveNumber || pp < vacantWave.end();i++)
			{
				if( m_resourceMap[previousNode][formerNode][i]==false)
				{
					
					vacantWave.erase(pp);	//ɾ����ռ�ò���
				}

				if(vacantWave.size() < event.m_bandwidth)
				{
					//cout << "�޿��ò���" << endl;
					return -1;
				}
				pp++;
			}
			event.m_workPath.push_back(previousNode);	//�洢·��
			previousNode = formerNode;
			formerNode = m_path[d][previousNode];
			event.changeHop();
		}*/

		Set<Integer> vacantWave = new HashSet();	//���ڼ�����ò���
		for(int i = 0; i < sumWaveNumber; i++)
		{
			//��ʼ��Ϊ���в���������
			vacantWave.add(i);	
		}

		//�洢·�� + ������ò���(����·������ʹ��ͬһ����)
		while (formerNode!=-1)
		{
			for(int i=0;i<sumWaveNumber;i++)
			{
				if( m_resourceMap[previousNode][formerNode][i]==false)
				{
					vacantWave.remove(i);	//ɾ����ռ�ò���
				}
				if(vacantWave.size() < event.m_bandwidth)
				{
					//cout << "�޿��ò���" << endl;
					return -1;
				}
			}
			event.m_workPath.push(previousNode);	//�洢·��
			previousNode = formerNode;
			formerNode = m_path.path[d][previousNode];
			//event.changeHop();
		}
		event.m_workPath.push(previousNode);	//����޽ڵ㵽·��
		m_serverResourse[d] += event.getsourceoccupation();	//���ҵ��ռ�õ�����������Դ
		//cout << "resource:" << m_serverResourse[d] << endl;
		//resourse = caculateSource();				//��������������Դ�����
		//int reservedWave = *vacantWave.begin();		//ȡ"���ò���"�ĵ�һ�������������ǰҵ��
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


	//��������������Դ�����
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
