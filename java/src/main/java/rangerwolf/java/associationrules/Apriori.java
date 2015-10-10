package rangerwolf.java.associationrules;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;

public class Apriori {
	
	static final double MIN_CONF = 0.6;
	static final int MIN_SUPPORT = 2;
	
	/**
	 * 初始化数据集. <br>
	 * 参考： http://zhan.renren.com/dmeryuyang?gid=3602888498023976650&checked=true <br>
	 * 测试数据来源于第一个例子： <br>
	 * Tid    Items<br>
	 * 100    134<br>
	 * 200    235<br>
	 * 300    1235<br>
	 * 400    25<br>
	 * @return
	 */
	static List<String[]> initTrans() {
		
		List<String[]> tranLst = Lists.newArrayList();
		
//		tranLst.add("1 3 4".split(" "));
//		tranLst.add("2 3 5".split(" "));
//		tranLst.add("1 2 3 5".split(" "));
//		tranLst.add("2 5".split(" "));
		
		// add another test sample to verify 
		tranLst.add(new String[] { "I1", "I2", "I5" });
		tranLst.add(new String[] { "I2", "I4"  });
		tranLst.add(new String[] { "I2", "I3" });
		tranLst.add(new String[] { "I1", "I2", "I4" });
		tranLst.add(new String[] { "I1", "I3" });
		tranLst.add(new String[] { "I1", "I3" });
		tranLst.add(new String[] { "I2", "I3" });
		tranLst.add(new String[] { "I1", "I2", "I3" });
		tranLst.add(new String[] { "I1", "I2", "I3", "I5" });
		
		
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
	
	
	/**
	 * 以C2为例 <br>
	 * 100: {(1 3)} <br>
	 * 200: {(2 3), (2 5), (2 5)} <br>
	 * 300: {(1 2), (1 3), (1 5), (2 3), (2 5), (3 5)} <br>
	 * 400: {(2 5)}
	 * @param preItemSets k-1频繁项集，如果要生成C2，那么preItemSets = L1
	 * @return
	 */
	static List<String[]> genCandidates(List<String[]> preItemSets ) {
	
		List<String[]> ret = Lists.newArrayList();
		ret = joinItemSets(preItemSets, preItemSets);
		return ret;
	}
	
	
	/**
	 * 判断两个频繁项集是否可以join在一起
	 * @param a
	 * @param b
	 * @return
	 */
	private static boolean canJoin(String[] a, String[] b) {
		if(a == null || a.length == 0 || b == null || b.length == 0 || a.length != b.length) 
			return false;
		else {
			// 能够join 在一起的前提就是 两者的前缀是一致的
			for(int i = 0; i < a.length; i++) {
				if(!a[i].equals(b[i]) && i == a.length - 1) {
					continue;
				}
				else if(a[i].equals(b[i]) && i < a.length - 1) {
					continue;
				}
				else {
					// 只有上面两种有效情况，其他情况无效
					return false;
				}
			}
			return true;
		}
	}
	
	private static List<String[]> joinItemSets(List<String[]> a, List<String[]> b) {
		
		List<String[]> ret = Lists.newArrayList();
		
		for(int i = 0; i < a.size(); i++) {
			String[] tmpA = a.get(i);
			for(int j = i + 1; j < b.size(); j++) {
				String[] tmpB = b.get(j);
				
				if(canJoin(tmpA, tmpB)) {
					String[] tmp = new String[tmpA.length + 1];
					for(int n = 0 ; n < tmpA.length; n++) {
						tmp[n] = tmpA[n];
					}
					tmp[tmpA.length] = tmpB[tmpB.length - 1];
					ret.add(tmp);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 * @param allTrans
	 * @param candidateItemSets
	 * @return
	 */
	static List<String[]> filterItemSetsByMinSupport(List<String[]> allTrans, List<String[]> candidateItemSets) {
		List<String[]> ret = Lists.newArrayList();
		
		Map<String[], Integer> itemSetCnt = Maps.newHashMap();
		
		
		for(String[] tran : allTrans) {
			
			for(String[] itemSet : candidateItemSets) {
				
				boolean allIn = true;
				
				for(String item : itemSet) {
					if(Arrays.binarySearch(tran, item) < 0) {
						allIn = false;
						break;
					};
				}
				
				if(allIn == true) {
					if(itemSetCnt.containsKey(itemSet)) {
						itemSetCnt.put(itemSet,  itemSetCnt.get(itemSet) + 1 );
					} else {
						itemSetCnt.put(itemSet,  1 );
					}
				}
			}
		}
		
		
		for(String[] itemSet : itemSetCnt.keySet()) {
			if(itemSetCnt.get(itemSet) >= MIN_SUPPORT) {
				ret.add(itemSet);
			}
		}
		
		return ret;
	}
	
	public static void main(String[] args) {
		
		List<String[]> trans = initTrans();
		
		List<String[]> _1FreqItemSets = genOneFeqItemSets(trans);
		
		System.out.println("1-freq item sets: " + new Gson().toJson(_1FreqItemSets));
		
		List<String[]> _2Candidates = genCandidates(_1FreqItemSets);
		System.out.println("2-candidates: " + new Gson().toJson(_2Candidates));
		
		List<String[]> _2FreqItemSets = filterItemSetsByMinSupport(trans, _2Candidates);
		System.out.println("2-freq item sets:" + new Gson().toJson(_2FreqItemSets));
		
		List<String[]> _3Candidates = genCandidates(_2FreqItemSets);
		System.out.println("3-candidates: " + new Gson().toJson(_3Candidates));
		
		List<String[]> _3FreqItemSets = filterItemSetsByMinSupport(trans, _3Candidates);
		System.out.println("3-freq item sets:" + new Gson().toJson(_3FreqItemSets));
		
		List<String[]> _4Candidates = genCandidates(_3FreqItemSets);
		System.out.println("4-candidates: " + new Gson().toJson(_4Candidates));
		
	}
	
}
