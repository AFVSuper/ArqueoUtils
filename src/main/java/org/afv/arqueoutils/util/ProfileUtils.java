package org.afv.arqueoutils.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.text.Text;
import net.minecraft.text.object.PlayerTextObjectContents;

import java.util.UUID;

public class ProfileUtils {
    public static final Text QUESTION_MARK_PLAYER_HEAD = Text.object(new PlayerTextObjectContents(ProfileComponent.ofStatic(makeProfileFromTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDZiYTYzMzQ0ZjQ5ZGQxYzRmNTQ4OGU5MjZiZjNkOWUyYjI5OTE2YTZjNTBkNjEwYmI0MGE1MjczZGM4YzgyIn19fQ")), true));
    public static final Text DISGUISED_PLAYER_HEAD = Text.object(new PlayerTextObjectContents(ProfileComponent.ofStatic(makeProfileFromTexture("ewogICJ0aW1lc3RhbXAiIDogMTc4MDMxMjQ5MjA4MSwKICAicHJvZmlsZUlkIiA6ICIwZWQ2MDFlMDhjZTM0YjRkYWUxZmI4MDljZmEwNTM5NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJOZWVkTW9yZUFjY291bnRzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVkMjk3Y2FmMmQ1MmM5NDBjYmU1OTViZjc4NjI4MDBkY2JkODYzNjNlODk3MjdlMTMyMTAwNjg5Mjc3MTQ1MDYiCiAgICB9CiAgfQp9")), true));

    public static GameProfile makeProfileFromTexture(String base64Texture) {
        // Step 1: Create a mutable multimap
        Multimap<String, Property> mutable = ArrayListMultimap.create();
        mutable.put("textures", new Property("textures", base64Texture));

        // Step 2: Wrap in PropertyMap (immutable copy)
        PropertyMap map = new PropertyMap(mutable);

        // Step 3: Create GameProfile with UUID, name, and PropertyMap
        return new GameProfile(UUID.randomUUID(), "Custom", map);
    }

    public static void init() {}
}
