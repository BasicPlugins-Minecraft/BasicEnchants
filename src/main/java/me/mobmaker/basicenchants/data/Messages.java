package me.mobmaker.basicenchants.data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.Template;
import net.kyori.adventure.text.minimessage.markdown.DiscordFlavor;
import net.kyori.adventure.text.minimessage.transformation.TransformationType;

import java.util.ArrayList;
import java.util.List;

public enum Messages {
    ENCHANT_INVALID("enchant.invalid", "**<red>HEY!** <gray>That enchantment doesn't exist."),
    ENCHANT_BAD_TOOL("enchant.badtool","**<red>HEY!** <gray>This item does not support enchantments."),
    ENCHANT_NOT_INSTALLED("enchant.notinstalled", "**<red>HEY!** <gray>The specified enchantment is not on this tool."),
    ENCHANT_NOT_NUMBER("enchant.notnumber", "**<red>HEY!** <gray>You must use a numeric value for your enchantment."),
    ENCHANT_EXTRACTED("enchant.extracted", "<gray>Successfully extracted <green><0> <gray>from your <aqua><1><gray> for <red><2> <gray><3>."),
    ENCHANT_ENCHANTED("enchant.enchanted", "<gray>Successfully enchanted your <aqua><0> <gray>with <green><1><gray>."),
    ENCHANT_FULLINV("enchant.fullinv", "**<red>HEY!** <gray>Your inventory is full! Try emptying it a little :)"),
    ENCHANT_MORE_EXP("enchant.morexp","**<red>HEY!** <gray>You don't have enough levels! You need <aqua><0> <gray>more levels."),
    ENCHANT_MORE_CASH("enchant.morecash","**<red>HEY!** <gray>You don't have enough money! You need <aqua><0> <gray>more <1>.")
    ;
    private final String key;
    private final String defaultValue;

    Messages(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public Component get() {
        return mm.parse(defaultValue);
        /*if (messages == null) color(defaultValue);
        String message = messages.getString(key);
        if (message == null || message.isEmpty())
        return color(message);*/
    }

    public Component get(Component... args) {
        List<Template> translate = new ArrayList<>();
        int argNum = 0;
        for (Component arg : args) {
            translate.add(Template.of(String.valueOf(argNum), arg));
            argNum++;
        }
        return mm.parse(defaultValue, translate);
    }

    //private YamlConfiguration messages = null;

    /*protected void reloadLanguage() {
        File messagesFile = new File(BasicEnchants.getDataFolder(), "messages.yml");
        boolean isNew = !messagesFile.exists();
        boolean isTampered = false;

        if (isNew) {
            try {
                if (messagesFile.createNewFile()) {
                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(messagesFile);
                    for (Messages message : Messages.values()) {
                        configuration.set(message.key, message.defaultValue);
                    }
                    configuration.save(messagesFile);
                } else {
                    System.out.println("Unable to create messages, defaulting to preset.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Unable to create messages (see above error message), defaulting to preset.");
                return;
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(messagesFile);
        for (Messages value : Messages.values()) {
            String string = config.getString(value.key);
            if (string == null) {
                isTampered = true;
                config.set(value.key, value.defaultValue);
            }
        }

        if (isTampered) {
            try {
                config.save(messagesFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }*/

    private MiniMessage mm = MiniMessage.builder()
            .transformation(TransformationType.COLOR)
            .transformation(TransformationType.DECORATION)
            .markdownFlavor(DiscordFlavor.get())
            .markdown()
            .build();
}
