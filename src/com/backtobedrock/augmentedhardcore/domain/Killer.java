package com.backtobedrock.augmentedhardcore.domain;

import com.backtobedrock.augmentedhardcore.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Killer {
    private final String name;
    private final String displayName;
    private final EntityType type;

    public Killer(String name, String displayName, EntityType type) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
    }

    public static Killer Deserialize(ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        String cName = section.getString("Name", null);
        String cDisplayName = section.getString("DisplayName", null);
        EntityType cType = null;

        String sType = section.getString("Type");
        if (sType != null)
            cType = ConfigUtils.getEntityType(sType);

        return new Killer(cName, cDisplayName, cType);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public EntityType getType() {
        return type;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("Name", this.name);
        map.put("DisplayName", this.displayName);
        map.put("Type", this.type == null ? null : this.type.name());

        return map;
    }

    public String getFormattedName() {
        return this.getDisplayName() == null && this.type != EntityType.PLAYER
                ? String.format("a %s", this.getName())
                : this.getDisplayName() == null
                ? String.format("%s", this.getName())
                : String.format("%s (%s)", this.getDisplayName(), this.getType().name().substring(0, 1).toUpperCase() + this.getType().name().substring(1).toLowerCase());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Killer killer = (Killer) o;
        return Objects.equals(getName(), killer.getName()) && getType() == killer.getType();
    }
}
