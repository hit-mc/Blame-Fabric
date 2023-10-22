package com.keuin.blame.util;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.Consumer;

import static com.keuin.blame.util.UuidUtils.UUID_NULL;


public final class PrintUtil implements ServerLifecycleEvents.ServerStarted {

    private static final Object syncMessage = new Object();
    private static final Object syncBroadcast = new Object();

    private static final Style broadcastStyle = Style.EMPTY.withColor(Formatting.AQUA);
    private static final Style infoStyle = Style.EMPTY.withColor(Formatting.WHITE);
    private static final Style stressStyle = Style.EMPTY.withColor(Formatting.AQUA);
    private static final Style warnStyle = Style.EMPTY.withColor(Formatting.YELLOW);
    private static final Style errorStyle = Style.EMPTY.withColor(Formatting.DARK_RED);

    private static final Logger LOGGER = LogManager.getLogger(PrintUtil.class);
    private static final String LOG_HEADING = "[Blame]";
    private static PlayerManager playerManager = null;

    // Used to handle server started event, to get player manager
    // You should put `ServerLifecycleEvents.SERVER_STARTED.register(PrintUtil.INSTANCE);` in the plugin init method
    public static final PrintUtil INSTANCE = new PrintUtil();

    private PrintUtil() {
    }

    @Override
    public void onServerStarted(MinecraftServer minecraftServer) {
        PrintUtil.playerManager = minecraftServer.getPlayerManager();
    }

    public static void setPlayerManager(PlayerManager playerManager) {
        if (PrintUtil.playerManager == null)
            PrintUtil.playerManager = playerManager;
    }

    public static void broadcast(String message) {
        synchronized (syncBroadcast) {
            if (playerManager != null)
                playerManager.broadcastChatMessage(
                        new LiteralText(message).setStyle(broadcastStyle),
                        MessageType.SYSTEM,
                        UUID_NULL
                );
        }
    }

    public static CommandContext<ServerCommandSource> msgStress(CommandContext<ServerCommandSource> context, String messageText) {
        return msgStress(context, messageText, false);
    }

    public static CommandContext<ServerCommandSource> msgInfo(CommandContext<ServerCommandSource> context, String messageText) {
        return msgInfo(context, messageText, false);
    }

    public static CommandContext<ServerCommandSource> msgWarn(CommandContext<ServerCommandSource> context, String messageText) {
        return msgWarn(context, messageText, false);
    }

    public static CommandContext<ServerCommandSource> msgErr(CommandContext<ServerCommandSource> context, String messageText) {
        return msgErr(context, messageText, false);
    }

    public static CommandContext<ServerCommandSource> msgStress(CommandContext<ServerCommandSource> context, String messageText, boolean broadcastToOps) {
        return message(context, messageText, broadcastToOps, stressStyle);
    }

    public static CommandContext<ServerCommandSource> msgInfo(CommandContext<ServerCommandSource> context, String messageText, boolean broadcastToOps) {
        return message(context, messageText, broadcastToOps, infoStyle);
    }

    public static CommandContext<ServerCommandSource> msgWarn(CommandContext<ServerCommandSource> context, String messageText, boolean broadcastToOps) {
        return message(context, messageText, broadcastToOps, warnStyle);
    }

    public static CommandContext<ServerCommandSource> msgErr(CommandContext<ServerCommandSource> context, String messageText, boolean broadcastToOps) {
        return message(context, messageText, broadcastToOps, errorStyle);
    }

    private static CommandContext<ServerCommandSource> message(CommandContext<ServerCommandSource> context, String messageText, boolean broadcastToOps, Style style) {
        synchronized (syncMessage) {
            LiteralText text = new LiteralText(messageText);
            text.setStyle(style);
            context.getSource().sendFeedback(text, broadcastToOps);
        }
        return context;
    }

    /**
     * Print debug message on the server console.
     *
     * @param string the message.
     */
    public static void debug(String string) {
        LOGGER.debug(LOG_HEADING + " " + string);
    }

    /**
     * Print informative message on the server console.
     *
     * @param string the message.
     */
    public static void info(String string) {
        LOGGER.info(LOG_HEADING + " " + string);
    }

    /**
     * Print warning message on the server console.
     *
     * @param string the message.
     */
    public static void warn(String string) {
        LOGGER.warn(LOG_HEADING + " " + string);
    }

    /**
     * Print error message on the server console.
     *
     * @param string the message.
     */
    public static void error(String string) {
        LOGGER.error(LOG_HEADING + " " + string);
    }

    public static void message(CommandContext<ServerCommandSource> context, Object... objects) {
        new Printer(false).append(objects).sendTo(context);
    }

    public static void error(CommandContext<ServerCommandSource> context, Object... objects) {
        new Printer(true).append(objects).sendTo(context);
    }

    public static Printer newPrinter() {
        return new Printer(false);
    }

    public static class Printer {
        private final BaseText message = new LiteralText("");
        private final boolean error;

        public Printer(boolean error) {
            this.error = error;
        }

        public Printer append(Object... objects) {
            Style currentStyle = null; // accumulated style appended with a text
            for (Object obj : objects) {
                if (obj instanceof Formatting) {
                    currentStyle = Optional.ofNullable(currentStyle).orElse(Style.EMPTY)
                            .withFormatting((Formatting) obj);
                } else if (obj != null) {
                    if (!(obj instanceof String))
                        obj = obj.toString();
                    message.append(new LiteralText((String) obj)
                            .setStyle(Optional.ofNullable(currentStyle).orElse(Style.EMPTY)));
                    currentStyle = null;
                }
            }
            if (currentStyle != null)
                throw new IllegalStateException("parameter with type Formatting must be appended " +
                        "with a BaseText-like object");
            return this;
        }

        public Printer newline() {
            return this.append("\n");
        }

        public void sendTo(Consumer<Text> receiver) {
            receiver.accept(message);
        }

        public void sendTo(CommandContext<ServerCommandSource> context) {
            if (error)
                context.getSource().sendError(message);
            else
                context.getSource().sendFeedback(message, false);
        }
    }

}
