package pl.teksusik.customskins.i18n;

import eu.okaeri.i18n.core.minecraft.adventure.AdventureMessage;
import eu.okaeri.i18n.message.MessageDispatcher;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;
import java.util.Map;

public class BukkitMessageDispatcher implements MessageDispatcher<AdventureMessage> {
    private final BI18n i18n;
    private final BukkitAudiences adventure;
    private final String key;

    private final Map<String, Object> fields = new LinkedHashMap<>();

    public BukkitMessageDispatcher(BI18n i18n, BukkitAudiences adventure, String key) {
        this.i18n = i18n;
        this.adventure = adventure;
        this.key = key;
    }

    public BukkitMessageDispatcher with(String key, Object value) {
        this.fields.put(key, value);
        return this;
    }

    @Override
    public BukkitMessageDispatcher sendTo(Object entity) {
        if (entity instanceof CommandSender) {
            return this.sendTo((CommandSender) entity);
        }

        throw new UnsupportedOperationException("Unsupported receiver: " + entity.getClass());
    }

    public BukkitMessageDispatcher sendTo(CommandSender receiver) {
        AdventureMessage adventure = this.i18n.get(receiver, this.key);
        this.fields.forEach(adventure::with);

        if (adventure.raw().isEmpty()) {
            return this;
        }

        this.adventure.sender(receiver).sendMessage(adventure.component());
        return this;
    }
}
