package crcalculator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

/* 
 * This class calculates the cumulative return for
 * a given "base" & "as of" date and a Map of daily returns
 * 
 * Rather than brute-force the calculation every time findCumReturn() 
 * is called, this class creates a map of cumulative returns
 * as of each date when the constructor is called.
 * This initial calculation assumes a base date of the earliest daily return.
 * Daily returns from prior to the base date are divided out when findCumReturn() is called
 * 
 * This means we need to iterate over the entire Map only once, 
 * instead of once every time findCumReturn() is called.
 * 
 * Kate Dlugosz
 * 01/15/2018
 */
public class CumReturnCalculator {
	TreeMap<Date, Double> returns;
	Map<Date, Double> dateCumReturnMap = new TreeMap<Date, Double>();
	
	public CumReturnCalculator (Map<Date, Double> dailyReturns) {
		// TreeMap ensures map is sorted by Date
		returns = new TreeMap<Date, Double>(dailyReturns);
		double cumReturnRate = 1;
		
		// cumulative return is calculated as of each date and stored in a map
		// we only need to calculate these once, to save time 
		for (Map.Entry<Date, Double> currReturn : returns.entrySet()) {
			Date currDate = currReturn.getKey();
			cumReturnRate = cumReturnRate * (currReturn.getValue() + 1);
			dateCumReturnMap.put(currDate, cumReturnRate);
		}
		
	}
	
	// Calculates the cumulative return based on an as-of and base date
	// Returns zero if input dates are out of order or if no daily returns are found in timeframe
	public double findCumReturn(Date asof, Date base) {
		if (asof.before(base))
			return 0;
		
		Date firstDate = base;
		Date lastDate = asof;
		NavigableSet<Date> dates = new TreeSet<Date>(dateCumReturnMap.keySet());
		
		if (!dateCumReturnMap.containsKey(base)) {
			// finds first daily return AFTER the base date
			firstDate = dates.higher(base);
		}
		
		if (!dateCumReturnMap.containsKey(asof)) {
			// find the first daily return BEFORE the as of date
			lastDate = dates.lower(asof);
		}
		
		if (firstDate == null || lastDate == null)
			return 0;
		
		double result = dateCumReturnMap.get(lastDate);
		
		if (firstDate != dates.first()) {
			// get the latest cumulative return before the base date
			Date prevDate = dates.lower(firstDate);
			
			// divide out the returns from before the base date
			result = result/(dateCumReturnMap.get(prevDate));
		}
		
		return result - 1;
	}


@SuppressWarnings("deprecation")
public static void main(String[] args) {
	HashMap<Date, Double> testMap = new HashMap<Date, Double>();
	
	testMap.put(new Date(115, 0, 10), 0.1);
	testMap.put(new Date(115, 1, 10), 0.05);
	testMap.put(new Date(115, 3, 10), 0.15);
	testMap.put(new Date(115, 3, 15), -0.1);
	testMap.put(new Date(115, 5, 10), -0.12);
	
	
	CumReturnCalculator calc = new CumReturnCalculator(testMap);
	
	Date base = new Date(115, 1, 1);
	
	double test1 = calc.findCumReturn(new Date(115, 0, 31), base);
	double test2 = calc.findCumReturn(new Date(115, 1, 28), base);
	double test3 = calc.findCumReturn(new Date(115, 2, 13), base);
	double test4 = calc.findCumReturn(new Date(115, 3, 30), base);
	double test5 = calc.findCumReturn(new Date(115, 4, 8), base);
	double test6 = calc.findCumReturn(new Date(115, 5, 30), base);
	
	System.out.println(test1);
	System.out.println(test2);
	System.out.println(test3);
	System.out.println(test4);
	System.out.println(test5);
	System.out.println(test6);
	
}
}
