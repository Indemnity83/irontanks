package com.indemnity83.irontanks.neoforge.crash;

import com.indemnity83.irontanks.core.crash.CrashReporting;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;

/**
 * Shows operators a once-per-session crash-reporting status line on join (unless they've silenced
 * it), with clickable toggles and a link to the privacy page. The per-session dedup resets on
 * server start so each session shows it again.
 */
public final class CrashReportNotifier {

    private CrashReportNotifier() {}

    private static final Set<UUID> NOTIFIED_THIS_SESSION = ConcurrentHashMap.newKeySet();

    private static final String DETAILS_URL =
            "https://github.com/Indemnity83/irontanks/blob/mc/26.1/CRASH_REPORTING.md";
    private static final String ENABLE_COMMAND = "/irontanks diagnostics enable";
    private static final String DISABLE_COMMAND = "/irontanks diagnostics disable";
    private static final String HIDE_COMMAND = "/irontanks diagnostics notify off";

    /** Clear the per-session dedup so the next server session shows the notice again. */
    public static void reset() {
        NOTIFIED_THIS_SESSION.clear();
    }

    /** Show the once-per-session status line to operators who haven't silenced it. */
    public static void maybeNotify(ServerPlayer player) {
        if (!CrashReporting.notifyOperators() || !isOperator(player)) {
            return;
        }
        if (NOTIFIED_THIS_SESSION.add(player.getUUID())) {
            player.sendSystemMessage(invite(CrashReporting.isActive()));
        }
    }

    private static boolean isOperator(ServerPlayer player) {
        return Commands.LEVEL_GAMEMASTERS.check(player.permissions());
    }

    private static Component invite(boolean enabled) {
        return Component.empty()
                .append(Component.literal("[Iron Tanks] ").withStyle(ChatFormatting.AQUA))
                .append(Component.literal("Crash reporting is ").withStyle(ChatFormatting.GRAY))
                .append(toggle(enabled))
                .append(Component.literal("  "))
                .append(link(
                        "[hide]",
                        ChatFormatting.DARK_GRAY,
                        "Stop showing this notice",
                        new ClickEvent.RunCommand(HIDE_COMMAND)))
                .append(Component.literal("  "))
                .append(link(
                        "[More Info]",
                        ChatFormatting.BLUE,
                        "Open the crash-reporting & privacy page",
                        new ClickEvent.OpenUrl(URI.create(DETAILS_URL))));
    }

    private static Component toggle(boolean enabled) {
        if (enabled) {
            return link(
                    "[ON]",
                    ChatFormatting.GREEN,
                    "Click to turn crash reporting off",
                    new ClickEvent.RunCommand(DISABLE_COMMAND));
        }
        return link(
                "[OFF]",
                ChatFormatting.RED,
                "Click to turn on sanitized crash reporting",
                new ClickEvent.RunCommand(ENABLE_COMMAND));
    }

    private static Component link(String label, ChatFormatting color, String hover, ClickEvent click) {
        return Component.literal(label)
                .withStyle(style -> style.withColor(color)
                        .withClickEvent(click)
                        .withHoverEvent(new HoverEvent.ShowText(Component.literal(hover))));
    }
}
