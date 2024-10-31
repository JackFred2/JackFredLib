package red.jackf.jackfredlib.api.lying.entity.builders;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import org.jetbrains.annotations.ApiStatus;
import red.jackf.jackfredlib.api.lying.entity.EntityUtils;

public class InteractionBuilder extends BuilderBase<Interaction, InteractionBuilder> {
    /**
     * <p>Create a new Interaction builder. Don't use directly, use a method in {@link red.jackf.jackfredlib.api.lying.entity.builders.EntityBuilders}.</p>
     *
     * <p>Interaction entities have a square base</p>
     *
     * @param level Level to create the fake entity in.
     */
    @ApiStatus.Internal
    protected InteractionBuilder(ServerLevel level) {
        super(EntityType.INTERACTION, level);
    }

    /**
     * Sets the width of this interaction entity.
     *
     * @param width Width in blocks of this entity.
     * @return This entity builder
     */
    public InteractionBuilder width(float width) {
        EntityUtils.setInteractionWidth(this.entity, width);
        return self();
    }

    /**
     * Sets the height of this interaction entity.
     *
     * @param height Height in blocks of this entity.
     * @return This entity builder
     */
    public InteractionBuilder height(float height) {
        EntityUtils.setInteractionHeight(this.entity, height);
        return self();
    }

    /**
     * Sets whether this interaction gives visual feedback for any players using it (punching sounds and swinging arms).
     *
     * @param hasResponse Whether this interaction entity should give visual feedback.
     * @return This entity builder
     */
    public InteractionBuilder response(boolean hasResponse) {
        EntityUtils.setInteractionResponse(this.entity, hasResponse);
        return self();
    }

    @Override
    protected InteractionBuilder self() {
        return this;
    }
}
