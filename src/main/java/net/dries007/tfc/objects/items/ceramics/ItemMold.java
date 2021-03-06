/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.ceramics;

import java.util.EnumMap;

import net.dries007.tfc.objects.Metal;

public class ItemMold extends ItemFiredPottery
{
    private static final EnumMap<Metal.ItemType, ItemMold> MAP = new EnumMap<>(Metal.ItemType.class);

    public static ItemMold get(Metal.ItemType category)
    {
        return MAP.get(category);
    }

    public final Metal.ItemType type;

    public ItemMold(Metal.ItemType type)
    {
        this.type = type;
        if (MAP.put(type, this) != null) throw new IllegalStateException("There can only be one.");
    }
}
