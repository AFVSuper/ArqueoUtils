package org.afv.arqueoutils.blocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Uuids;

import java.util.UUID;

public class CoreServerData {

    public static final Codec<CoreServerData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.fieldOf("required_item").forGetter(d -> d.requiredItem),
            Codec.STRING.fieldOf("color").forGetter(d -> d.colorId),
            Codec.BOOL.fieldOf("completed").forGetter(d -> d.completed),
            Uuids.CODEC.fieldOf("display_entity").forGetter(d -> d.displayEntity)
        ).apply(instance, CoreServerData::new)
    );

    public String requiredItem;
    public String colorId;
    public UUID displayEntity;
    public boolean completed;

    public CoreServerData(String requiredItem, String colorId, boolean completed, UUID displayEntity) {
        this.requiredItem = requiredItem;
        this.colorId = colorId;
        this.completed = completed;
        this.displayEntity = displayEntity;
    }

    public CoreServerData() {
        this("minecraft:air", "white", false, null);
    }

    public void copyFrom(CoreServerData other) {
        this.requiredItem = other.requiredItem;
        this.colorId = other.colorId;
        this.completed = other.completed;
        this.displayEntity = other.displayEntity;
    }
}