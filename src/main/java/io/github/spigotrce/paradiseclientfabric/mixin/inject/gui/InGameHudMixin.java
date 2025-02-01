package io.github.spigotrce.paradiseclientfabric.mixin.inject.gui;

import io.github.spigotrce.paradiseclientfabric.Constants;
import io.github.spigotrce.paradiseclientfabric.ParadiseClient_Fabric;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Objects;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Final
    @Shadow
    private MinecraftClient client;
    @Shadow
    @Final
    private PlayerListHud playerListHud;

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(MinecraftClient client, CallbackInfo ci) {
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderMainHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (this.client == null) {
            return;
        }

        ArrayList<String> text = new ArrayList<>();

        text.add(Formatting.DARK_BLUE + Constants.WINDOW_TITLE);
        text.add(Formatting.WHITE + "Server" + Formatting.GRAY + ": " + Formatting.AQUA +
                ((!Objects.isNull(this.client.getCurrentServerEntry()) && ParadiseClient_Fabric.hudMod.showServerIP) ? this.client.getCurrentServerEntry().address : "Hidden"));
        assert this.client.player != null;
        text.add(Formatting.WHITE + "Engine" + Formatting.GRAY + ": " + Formatting.AQUA + (Objects.isNull(this.client.player.networkHandler) ? "" : this.client.player.networkHandler.getBrand()));
        text.add(Formatting.WHITE + "FPS" + Formatting.GRAY + ": " + Formatting.AQUA + this.client.getCurrentFps());
        text.add(Formatting.WHITE + "Players" + Formatting.GRAY + ": " + Formatting.AQUA + this.client.player.networkHandler.getPlayerList().size());

        int padding = 10;
        int x = padding;
        int y = padding;
        int maxWidth = 0;

        for (String s : text) {
            int width = this.client.textRenderer.getWidth(s);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }

        int windowHeight = text.size() * this.client.textRenderer.fontHeight + padding * 2;
        int borderThickness = 2;
        int borderColor = 0xFF404040;

        // Vykreslení pozadí
        context.fill(x - padding, y - padding, x + maxWidth + padding, y + windowHeight, 0x80000000);

        // Vykreslení ohraničení (tlustší pomocí více čar)
        for (int i = 0; i < borderThickness; i++) {
            // Horní čára
            context.fill(x - padding + i, y - padding + i, x + maxWidth + padding - i, y - padding + i + 1, borderColor);
            // Dolní čára
            context.fill(x - padding + i, y + windowHeight - i - 1, x + maxWidth + padding - i, y + windowHeight - i, borderColor);
            // Levá čára
            context.fill(x - padding + i, y - padding + i, x - padding + i + 1, y + windowHeight - i, borderColor);
            // Pravá čára
            context.fill(x + maxWidth + padding - i - 1, y - padding + i, x + maxWidth + padding - i, y + windowHeight - i, borderColor);
        }

        for (String s : text) {
            context.drawText(this.client.textRenderer, s, x, y, 0xFFFFFF, false);
            y += this.client.textRenderer.fontHeight;
        }
    }

    @Inject(method = "renderPlayerList", at = @At("HEAD"), cancellable = true)
    private void renderPlayerList(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        assert this.client.world != null;
        Scoreboard scoreboard = this.client.world.getScoreboard();
        ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.LIST);
        if (!this.client.options.playerListKey.isPressed() || this.client.isInSingleplayer() && Objects.requireNonNull(this.client.player).networkHandler.getListedPlayerListEntries().size() <= 1 && scoreboardObjective == null) {
            this.playerListHud.setVisible(false);
            if (ParadiseClient_Fabric.hudMod.showPlayerList) {
                this.renderTAB(context, context.getScaledWindowWidth(), scoreboard, scoreboardObjective);
            }
        } else {
            this.renderTAB(context, context.getScaledWindowWidth(), scoreboard, scoreboardObjective);
        }
        ci.cancel();
    }

    @Unique
    private void renderTAB(DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, @Nullable ScoreboardObjective scoreboardObjective) {
        this.playerListHud.setVisible(true);
        this.playerListHud.render(context, scaledWindowWidth, scoreboard, scoreboardObjective);
    }
}