package rangerwolf.java.associationrules;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;

public class Apriori {
	
	static final double MIN_CONF = 0.6;
	static final int MIN_SUPPORT = 2;
	static final String JOIN_CHAR = "#";
	
	static Map<String, Integer> freqItemSetSup = Maps.newHashMap();
	
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
		
		freqItemSetSup.clear();
		freqItemSetSup = Maps.newHashMap();
		
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
			Integer curSupport = frequences.get(item);
			
			if(curSupport  >= MIN_SUPPORT) {
				ret.add(new String[]{item});
				freqItemSetSup.put(item, curSupport);
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
					
					Arrays.sort(tmp);
					
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
			Integer curSupport = itemSetCnt.get(itemSet);
			
			if(curSupport >= MIN_SUPPORT) {
				ret.add(itemSet);
				freqItemSetSup.put(StringUtils.join(itemSet, JOIN_CHAR), curSupport);
			}
		}
		
		return ret;
	}
	
	
	/**
	 * 假设itemSet = {1 2 3} 那么就生成如下的规则：<br>
	 * {1 2} -> {3}<br>
	 * {1 3} -> {2}<br>
	 * {2 3} -> {1}<br>
	 * @param itemSet
	 * @return
	 */
	private static Multimap<String[], String[]> genCandidateRules(final String[] itemSet) {
		Multimap<String[], String[]> rules = ArrayListMultimap.create();
		if(itemSet == null || itemSet.length <= 1)
			return rules;
		
		for(int i = 0; i < itemSet.length; i++) {
			
			String[] y_x = {itemSet[i]};
			
			String[] x = new String[itemSet.length - 1];
			List<String> itemLst = Lists.newArrayList(itemSet);
			itemLst.remove(i);
			x = itemLst.toArray(x);
			rules.put(x, y_x);
		}
		
		return rules;
	}
	
	
	/**
	 * 从规则生成子规则 <br>
	 * 比如： {1 2} -> {3} <br>
	 * 生成： {1} -> {2 3} and {2} -> {1 3} <br> 
	 * 注意生成规则： 从左边放到右边的那一项一定要大于右边的最大的项
	 * @param x   : 相当于 X
	 * @param y_x : 相当于 Y-X
	 * @return
	 */
	private static Multimap<String[], String[]> genSubCandidateRules(String[] x, String[] y_x, Integer itemSetSup) {
		
		Multimap<String[], String[]> subRules = ArrayListMultimap.create();
		
		if(x == null || y_x == null || x.length <= 1 || y_x.length == 0)
			return subRules;
		
		else {
			String maxRight = Collections.max(Arrays.asList(y_x));
			
			for(int i = 0; i < x.length; i++) {
				// x[i] > maxRight 的时候才能移动到右边
				if(x[i].compareToIgnoreCase(maxRight) < 0) {
					String[] tmpX = Arrays.copyOf(x, x.length);
					tmpX = ArrayUtils.remove(x, i);
					String[] tmpY_X = Arrays.copyOf(y_x, y_x.length);
					tmpY_X = ArrayUtils.add(y_x, x[i]);
					
					String tmpXStr = StringUtils.join(tmpX, JOIN_CHAR);
					if(freqItemSetSup.containsKey(tmpXStr)) {
						Integer tmpXSup = freqItemSetSup.get(tmpXStr);
						double tmpXConf = itemSetSup.doubleValue() / tmpXSup.doubleValue() ; 
						if( tmpXConf >= MIN_CONF) {
							System.out.printf("%s => %s [%.2f] \n", Arrays.toString(tmpX),
									Arrays.toString(tmpY_X), tmpXConf);
							genSubCandidateRules(tmpX, tmpY_X, tmpXSup);
						}
					}
					
				}
			}
			
			return subRules;
		}
	}
	
	/**
	 * 一边生成subItemSets一边进行剪枝，否则需要进行的计算就太多了<br>
	 * example: {1, 2, 3, 4, 5} <br>
	 * 第一遍：<br>
	 * 1234 -> 5 <br>
	 * 1235 -> 4 <br>
	 * 1245 -> 3 <br>
	 * 1345 -> 2 <br>
	 * 2345 -> 1 <br>
	 * 
	 * 假设 1234 -> 5 的阈值不足， 那么 1234 进一步切分的子集就不再需要了, 可以直接到第二个规则上进行<br>
	 * 假设 1235 -> 4 的阈值足够， 那么 1235可以进一步切分子集 , 如下：<br>
	 * 123 -> 45 <br>
	 * 125 -> 43 <br>
	 * 135 -> 42 <br>
	 * 235 -> 41 <br>
	 * 
	 * 相当于每次只切一层，然后按照这个继续递归的进行下去就可以很大程度的减少重复计算 <br>
	 * 
	 * 有个bug： 比如 1234 跟 1235的子集都包括 123 会重复计算了
	 * @return
	 */
	static void genRules() {
		
		for(String itemSetStr : freqItemSetSup.keySet()) {
			
			String[] itemSet = itemSetStr.split(JOIN_CHAR);
			Integer itemSetSup = freqItemSetSup.get(itemSetStr);
			
			Multimap<String[], String[]> rules = genCandidateRules(itemSet);
			
			for(String[] x : rules.keySet()) {
				String xStr = StringUtils.join(x, JOIN_CHAR);
				if(!freqItemSetSup.containsKey(xStr))
					continue;
				
				Integer xSup = freqItemSetSup.get(xStr);
				
				double xConf = itemSetSup.doubleValue() / xSup.doubleValue();
				
				if(xConf >= MIN_CONF) {
					Collection<String[]> y_x_lst = rules.get(x);
					for(String[] y_x : y_x_lst) {
						System.out.printf("%s => %s [%.2f] \n", Arrays.toString(x),
								Arrays.toString(y_x), xConf
								);
						if(x.length > 1)
							genSubCandidateRules(x, y_x, xSup);
					}
				}
			}
		}
		
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
		
		System.out.println("-------------------");
		genRules();
	}
	
	
}
