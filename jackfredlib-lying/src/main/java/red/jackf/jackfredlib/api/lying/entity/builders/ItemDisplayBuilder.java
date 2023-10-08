package red.jackf.jackfredlib.api.lying.entity.builders;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import red.jackf.jackfredlib.mixins.lying.entity.ItemDisplayAccessor;

/**
 * Builder to create an Item Display, which shows an {@link net.minecraft.world.item.ItemStack}.
 *
 * @see <a href="https://minecraft.fandom.com/wiki/Display">Minecraft Wiki: Display</a>
 */
public class ItemDisplayBuilder extends DisplayBuilder<Display.ItemDisplay, ItemDisplayBuilder> {
    protected ItemDisplayBuilder(ServerLevel level) {
        super(EntityType.ITEM_DISPLAY, level);
    }

    /**
     * Sets the item stack for this entity to display.
     * @param stack Stack to display in this display entity
     * @return This entity builder
     */
    public ItemDisplayBuilder stack(ItemStack stack) {
        ((ItemDisplayAccessor) this.entity).callSetItemStack(stack);
        return self();
    }

    /**
     * Changes how the item stack is displayed.
     * @implNote Default: {@link ItemDisplayContext#NONE}
     * @param context Context to display the item stack with.
     * @return This entity Builder
     */
    public ItemDisplayBuilder displayContext(ItemDisplayContext context) {
        ((ItemDisplayAccessor) this.entity).callSetItemTransform(context);
        return self();
    }

    @Override
    protected ItemDisplayBuilder self() {
        return this;
    }
}
