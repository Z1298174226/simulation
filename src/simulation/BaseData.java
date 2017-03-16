package simulation;

import java.util.Map;

public class BaseData {
	static final double g_rou	= 40;
	static final double maxlam	= 1000;			//ҵ������rou/lamda
	static final int ServiceQuantity = 5000;	//ҵ�����
	static final int srcMaxBandwidth = 40;		//��Դ�ڵ������ҵ���ܼƿ��ô���
	static final int DBAMode = 2;				//0 ���գ�1 ���ͷ�������ɱ�����2 ����û����飬3 ���ܵ���
	static final double DBA_Mode2Rate = 0.1;	//2 ����û�����,�����ִ���ʹ����ƽ����������10%
	static final double m_OltmaxBWRate	= 0.1;	//����ҵ�����ʹ�ô������
	static final double DBA_samServerNumRate = 0.01;	//����ҵ����������

	static final int	Expect_HighDataSize		= 40;	//æʱҵ������������
	static final int	Expect_MidDataSize1		= 30;	//��ͨҵ������������
	static final int	Expect_MidDataSize2		= 20;	//��ͨҵ������������
	static final int	Expect_LowDataSize		= 10;	//��ʱҵ������������
	static final int	Variance_HighDataSize	= 3;	//æʱҵ����������׼��ң���-3��, ��+3�ң�
	static final int	Variance_MidDataSize	= 1;	//��ͨҵ����������׼��ң���-3��, ��+3�ң�
	static final int	Variance_LowDataSize	= 3;	//��ʱҵ����������׼��ң���-3��, ��+3�ң�

	//const double g_lamda = 0.01; //ҵ�񵽴���
	static final double g_omiga = 3;
	static final double g_theta = 0.5;
	static final double afar = 0.01;			//����ҵ����Դռ���ʵĲ���
	static final double beta = 0.5;			//���ؾ�������
	static final int INFINITE = 999;			//��ʾ�ڵ㲻�ɴ�,"map.data"�еĽڵ㲻�ɴ��趨ֵ
	static final int MAXINUM = 14;				//���˽ڵ���
	static final int SERVERNUMBER = 4;			//����������
	static final double Delay = 0.5;			//��ͨҵ���޿�����Դ�������ʱ
	static final int WaveNumber = 100;			//��·������Ŀ
	static final int BandWidthUnit = 10;		//ÿ�������Ĵ���10G
	static final int caculateMode = 3;			//0 �����1 Ӧ�ã�2 ���磬3 CSO�㷨����
	static final int QosMode = 0;				//0 ��QoS���ԣ� 1 QoS��֤����
	static final double QosResoure = 0.2;		//Ԥ��ʱ������ҵ����Դ����
	
	
	class Pair{
		public Map.Entry<Integer, Integer> pair;               
	}
	class Edge{
		public float[][] edge = new float[MAXINUM][MAXINUM];  //ͼ���ڽӾ���
	}
	class Dist{
		public float[][] dist = new float[MAXINUM][MAXINUM]; //��Ŵӽڵ�m���ڵ�n�����·������
	}
	class Path{
		public int[][] path = new int[MAXINUM][MAXINUM];   //��ŵ�m�ڵ�����·���ϵ�ǰһ�ڵ�Ľڵ��
	}
	class S{
		public int[][] s = new int[MAXINUM][MAXINUM];          //���·���Ƿ��Ѿ�ȷ��
	}
	class Double{
		int inum;
		double dnum;
	}

	Edge g_edge;
	{
		g_edge = new Edge();
	}

}
