package me.ds.chunklagcheck;

import java.util.Comparator;
import java.util.Map;

public class DictionaryComparator implements Comparator 
{
	@Override
	public int compare(Object o1, Object o2) 
	{
		EntityDetails obj1 = (EntityDetails) o1;
		EntityDetails obj2 = (EntityDetails) o2;
		
		return obj2.entityCount - obj1.entityCount;
	}	
}
