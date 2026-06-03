package com.indemnity83.irontanks.neoforge.crash;

import com.indemnity83.irontanks.core.crash.CrashReporting;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * The {@code /irontanks diagnostics} command tree: status, enable, disable, preview, and the
 * operator join-notice toggle. A dev-only {@code test} subcommand sends a synthetic report so the
 * pipeline can be verified by hand. Operator-only ({@link Commands#LEVEL_GAMEMASTERS}).
 */
public final class IronTanksDiagnosticsCommand {

    private IronTanksDiagnosticsCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, boolean development) {
        LiteralArgumentBuilder<CommandSourceStack> diagnostics = Commands.literal("diagnostics")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(ctx -> {
                    ctx.getSource().sendSuccess(IronTanksDiagnosticsCommand::status, false);
                    return 1;
                })
                .then(Commands.literal("enable").executes(ctx -> {
                    if (CrashReporting.enable()) {
                        ctx.getSource()
                                .sendSuccess(
                                        () -> Component.literal(
                                                        "Sanitized crash reporting enabled. Thanks for helping improve Iron Tanks!")
                                                .withStyle(ChatFormatting.GREEN),
                                        true);
                        return 1;
                    }
                    ctx.getSource()
                            .sendFailure(
                                    Component.literal(
                                            "Could not start crash reporting — no DSN configured or initialization failed. See the log."));
                    return 0;
                }))
                .then(Commands.literal("disable").executes(ctx -> {
                    CrashReporting.disable();
                    ctx.getSource()
                            .sendSuccess(
                                    () -> Component.literal("Crash reporting disabled.")
                                            .withStyle(ChatFormatting.YELLOW),
                                    true);
                    return 1;
                }))
                .then(Commands.literal("preview").executes(ctx -> {
                    ctx.getSource().sendSuccess(() -> Component.literal(CrashReporting.previewReport()), false);
                    return 1;
                }))
                .then(Commands.literal("notify")
                        .then(Commands.literal("on").executes(ctx -> {
                            CrashReporting.setNotifyOperators(true);
                            ctx.getSource()
                                    .sendSuccess(
                                            () -> Component.literal("Join-time crash-reporting notice enabled."), true);
                            return 1;
                        }))
                        .then(Commands.literal("off").executes(ctx -> {
                            CrashReporting.setNotifyOperators(false);
                            ctx.getSource()
                                    .sendSuccess(
                                            () -> Component.literal("Join-time crash-reporting notice disabled."),
                                            true);
                            return 1;
                        })));

        if (development) {
            diagnostics.then(Commands.literal("test").executes(ctx -> {
                if (CrashReporting.sendTestReport()) {
                    ctx.getSource().sendSuccess(() -> Component.literal("Sent a test crash report to Sentry."), false);
                    return 1;
                }
                ctx.getSource()
                        .sendFailure(Component.literal(
                                "Crash reporting is not active. Run /irontanks diagnostics enable first."));
                return 0;
            }));
        }

        dispatcher.register(Commands.literal("irontanks").then(diagnostics));
    }

    private static Component status() {
        boolean active = CrashReporting.isActive();
        return Component.literal("[Iron Tanks] ")
                .withStyle(ChatFormatting.AQUA)
                .append(Component.literal("Crash reporting is ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(active ? "ON" : "OFF")
                        .withStyle(active ? ChatFormatting.GREEN : ChatFormatting.RED));
    }
}
