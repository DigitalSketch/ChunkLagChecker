package me.ds.chunklagcheck;

import java.util.Comparator;

public class DictionaryComparator implements Comparator<EntityDetails>
{
	@Override
	public int compare(EntityDetails o1, EntityDetails o2) 
	{
		EntityDetails obj1 = (EntityDetails) o1;
		EntityDetails obj2 = (EntityDetails) o2;
		
		return obj2.entityCount - obj1.entityCount;
	}	
}
