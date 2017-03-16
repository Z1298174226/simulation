package simulation;
import java.io.IOException;
import java.util.*;

public abstract class Database extends BaseData{
	Database()
	{
	}
 
	public double[]  m_serverResourse = new double[MAXINUM];	    //������������Դռ����
	public double[]  m_Oltresource = new double[MAXINUM];			//Դ�ڵ�ʵʱ������Դʹ��
	public double[]  m_averOltresource = new double[MAXINUM];		//Դ�ڵ�ƽ��������Դʹ��
	public double[]  m_srcMaxBandwidth = new double[MAXINUM];		//��Դ�ڵ�ķ�������������ޣ�
	public double[]  m_averMaxBandwidth = new double[MAXINUM];		//��Դ�ڵ�ķ������

	
	public double[]  DBA_averBandwidthmax = new double[MAXINUM];								//��Դ�ڵ�������ʹ�����
	public Double[] DBA_averBandwidthRate = new Double[MAXINUM];   //DBA_averBandwidth[s] / m_srcMaxBandwidth[s] ��������
	{
		for(int i = 0;i < MAXINUM; i++){
			DBA_averBandwidthRate[i] = new Double();
		}
	}
	
	
	public int[]  m_currentServicemaxNum = new int[MAXINUM];		//

	public double[][] DBA_SampBandwidth = new double[MAXINUM][(int)(ServiceQuantity * 0.01)];	//�������ֵ	
	public double[][] DBA_SampDataSize = new double[MAXINUM][(int)(ServiceQuantity * 0.01)];	//����������ֵ������Ԥ����������
	public int[][] DBA_SampcurrentServiceNum = new int[MAXINUM][(int)(ServiceQuantity * 0.01)];
	
	public int m_serviceNum;	//������

	/*�β�:	�ڵ�(0-13)
	  ����:	�������˵��ڽӾ���g_edge[][]������ڵ�����·������m_dist[][],
	  	    ��s��d���·������һ�ڵ��m_path[d][s]*/
	public abstract void caculatePath(int i);

	/*�β�:	��
	  ����:	��������������Դ�����*/
	public  abstract double caculateSource();

	/*�β�:	�� 
	  ����:	ѭ������caculatePath(), ��������нڵ�����·����,
			��ʼ����·�����в���������, ��ʼ������������Դռ����Ϊ0. */
	public abstract void initializeDB();

	/*�β�:	ҵ���¼�, (����)����������Դ�����
	  ����: ����Ĳ��� / -1�ڵ㲻�ɴ�&�޿��ò��� / -2Ӧ����Դ����
	  ����:	����m_path[d][s]��ȡҵ��·��, ��������·����ռ�������������ò���,
	  		���ҵ��ռ�õ�����������Դ, ����caculateSource()��������������Դ�����*/
	public abstract int showPath(Event event, double d);


	/*�β�:	Դ�ڵ㡢�޽ڵ㡢ռ�ò�����
	  ����:	Ԥ����Դ:�����·��ҵ��ռ�õĲ���������*/
	public abstract void reserveSource(final Event event);
	

	/*�β�:	Դ�ڵ㡢�޽ڵ㡢ռ�ò�����
	  ����:	�ͷ���Դ:�����·��ҵ��ռ�õĲ�������*/
	public abstract void relieveSource(final Event event);

	//��̬������䣨��۲�DBA���ԣ�
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
	protected boolean[][][]  m_resourceMap = new boolean[MAXINUM][MAXINUM][WaveNumber];	//m_resourceMap[m][n][k]��ʾ��·m��n�ϵĵ�k��������ҵ��ռ��

}
