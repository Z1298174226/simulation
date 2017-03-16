package simulation;

import java.util.Map;

public class BaseData {
	static final double g_rou	= 40;
	static final double maxlam	= 1000;			//业务量，rou/lamda
	static final int ServiceQuantity = 5000;	//业务个数
	static final int srcMaxBandwidth = 40;		//各源节点的所有业务总计可用带宽
	static final int DBAMode = 2;				//0 对照，1 降低分配带宽（成本），2 提高用户体验，3 智能调度
	static final double DBA_Mode2Rate = 0.1;	//2 提高用户体验,即保持带宽使用率平衡在期望±10%
	static final double m_OltmaxBWRate	= 0.1;	//单个业务最大使用带宽比例
	static final double DBA_samServerNumRate = 0.01;	//采样业务数量比例

	static final int	Expect_HighDataSize		= 40;	//忙时业务数据量期望
	static final int	Expect_MidDataSize1		= 30;	//普通业务数据量期望
	static final int	Expect_MidDataSize2		= 20;	//普通业务数据量期望
	static final int	Expect_LowDataSize		= 10;	//闲时业务数据量期望
	static final int	Variance_HighDataSize	= 3;	//忙时业务数据量标准差σ（μ-3σ, μ+3σ）
	static final int	Variance_MidDataSize	= 1;	//普通业务数据量标准差σ（μ-3σ, μ+3σ）
	static final int	Variance_LowDataSize	= 3;	//闲时业务数据量标准差σ（μ-3σ, μ+3σ）

	//const double g_lamda = 0.01; //业务到达率
	static final double g_omiga = 3;
	static final double g_theta = 0.5;
	static final double afar = 0.01;			//调整业务资源占用率的参数
	static final double beta = 0.5;			//负载均衡因子
	static final int INFINITE = 999;			//标示节点不可达,"map.data"中的节点不可达设定值
	static final int MAXINUM = 14;				//拓扑节点数
	static final int SERVERNUMBER = 4;			//数据中心数
	static final double Delay = 0.5;			//普通业务无可用资源情况下延时
	static final int WaveNumber = 100;			//链路波长数目
	static final int BandWidthUnit = 10;		//每个波长的带宽，10G
	static final int caculateMode = 3;			//0 随机，1 应用，2 网络，3 CSO算法策略
	static final int QosMode = 0;				//0 无QoS策略； 1 QoS保证机制
	static final double QosResoure = 0.2;		//预留时延敏感业务资源比例
	
	
	class Pair{
		public Map.Entry<Integer, Integer> pair;               
	}
	class Edge{
		public float[][] edge = new float[MAXINUM][MAXINUM];  //图的邻接矩阵
	}
	class Dist{
		public float[][] dist = new float[MAXINUM][MAXINUM]; //存放从节点m到节点n的最短路径长度
	}
	class Path{
		public int[][] path = new int[MAXINUM][MAXINUM];   //存放到m节点的最短路径上的前一节点的节点号
	}
	class S{
		public int[][] s = new int[MAXINUM][MAXINUM];          //标记路径是否已经确定
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
