package me.ds.chunklagcheck;

import java.util.Comparator;

public class EntityLocationComparator implements Comparator<EntityData>
{
    public int compare(EntityData o1, EntityData o2) {
    	int xCompare = Integer.compare(o1.entityX, o2.entityX);
        return xCompare == 0 ? Integer.compare(o1.entityZ, o2.entityZ) : xCompare;
    }
}
