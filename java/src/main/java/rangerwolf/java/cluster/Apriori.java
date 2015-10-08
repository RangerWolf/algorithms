package rangerwolf.java.cluster;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

public class Apriori {
	
	static final double MIN_CONF = 0.6;
	static final int MIN_SUPPORT = 2;
	
	/**
	 * 初始化数据集. <br>
	 * 参考： http://zhan.renren.com/dmeryuyang?gid=3602888498023976650&checked=true
	 * 测试数据来源于第一个例子：
	 * Tid    Items
	 * 100    134
	 * 200    235
	 * 300    1235
	 * 400    25
	 * @return
	 */
	static List<String[]> initTrans() {
		
		List<String[]> tranLst = Lists.newArrayList();
		
		tranLst.add("1 3 4".split(" "));
		tranLst.add("2 3 5".split(" "));
		tranLst.add("1 2 3 5".split(" "));
		tranLst.add("2 5".split(" "));
		
		System.out.println("All trans :");
		System.out.println(new Gson().toJson(tranLst) + "\n");
		return tranLst;
	}
	
	/**
	 * 生成1-频繁项集. 相当于生成L1
	 * @return
	 */
	static List<String[]> genOneFeqItemSets(List<String[]> trans) {
		
		List<String[]> ret = Lists.newArrayList();
		
		Map<String, Integer> frequences = Maps.newHashMap();
		for(String[] tran : trans) {
			for(String item : tran) {
				if(frequences.containsKey(item)) {
					frequences.put(item,   frequences.get(item) + 1  );
				} else {
					frequences.put(item,   1  );
				}
			}
		}
		
		for(String item : frequences.keySet()) {
			if(frequences.get(item) >= MIN_SUPPORT) {
				ret.add(new String[]{item});
			}
		}
		
		return ret;
	}
	
	
	static List<String[]> genCandidates(List<String[]> preItemSets ) {
	
		List<String[]> ret = Lists.newArrayList();
		
		
		
		
		return ret;
		
	}
	
	public static void main(String[] args) {
		
		List<String[]> trans = initTrans();
		
		List<String[]> _1FreqItemSets = genOneFeqItemSets(trans);
		
		System.out.println(new Gson().toJson(_1FreqItemSets));
	}
	
}
