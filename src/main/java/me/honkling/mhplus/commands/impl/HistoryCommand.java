package me.honkling.mhplus.commands.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import me.honkling.mhplus.MHPlusClient;
import me.honkling.mhplus.util.SettingsManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class HistoryCommand {

    private static final String name = "history";

    public static LiteralArgumentBuilder<FabricClientCommandSource> register() {
        return literal(name).executes(HistoryCommand::execute);
    }

    public static int execute(CommandContext<FabricClientCommandSource> context) {
        List<SettingsManager.Data.Server> serverList = SettingsManager.Instance.data.getJoinedServers();
        MutableText text = Text.literal("Viewing past servers.")
                .formatted(Formatting.GRAY);

        context.getSource().getPlayer().sendMessage(MHPlusClient.prefix(text), false);

        MutableText servers = Text.literal("Here's 30 previously joined servers.\n")
                .formatted(Formatting.GRAY)
                .append(MHPlusClient.prefix(Text.literal("")));

        int iterations = 0;

        MHPlusClient.logger.warn("[DEBUG] Data has " + serverList.size() + " servers.");

        for(SettingsManager.Data.Server server : serverList.toArray(new SettingsManager.Data.Server[0])) {
            if(iterations > 30) break;
            servers.append(
                            Text.literal(iterations > 0 ? ", " : "")
                                    .formatted(Formatting.GRAY)
                    )
                    .append(
                            Text.literal(server.getName())
                                    .setStyle(
                                            Style.EMPTY
                                                    .withColor(Formatting.WHITE)
                                                    .withHoverEvent(
                                                            new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to join ")
                                                                    .formatted(Formatting.GRAY)
                                                                    .append(
                                                                            Text.literal(server.getName())
                                                                                    .formatted(Formatting.WHITE)
                                                                    ))
                                                    )
                                                    .withClickEvent(
                                                            new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/join " + server.getName())))
                    );
            iterations++;
        }

        context.getSource().getPlayer().sendMessage(MHPlusClient.prefix(servers), false);
        return 1;
    }
}
