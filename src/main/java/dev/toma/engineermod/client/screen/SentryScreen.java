package dev.toma.engineermod.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.toma.engineermod.EngineerMod;
import dev.toma.engineermod.client.screen.util.GhostTextResponder;
import dev.toma.engineermod.client.screen.widget.CycleButton;
import dev.toma.engineermod.client.screen.widget.ListView;
import dev.toma.engineermod.common.entity.SentryEntity;
import dev.toma.engineermod.common.entity.SentryTargetType;
import dev.toma.engineermod.network.NetworkHandler;
import dev.toma.engineermod.network.packet.CS_SentryWhitelistAction;
import dev.toma.engineermod.network.packet.CS_SetSentryTargetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Sentry modification screen
 *
 * @author Toma
 * @version 1.0
 */
public class SentryScreen extends CenteredScreen {

    /**
     * The background image.
     */
    private static final ResourceLocation BACKGROUND = EngineerMod.createModPath("textures/screen/sentry.png");

    /**
     * Sentry entity which owns this screen.
     */
    private final SentryEntity owner;

    /**
     * Target selector button.
     */
    private CycleButton<SentryTargetType> buttonTargetType;

    /**
     * Whitelist text field
     */
    private TextFieldWidget whitelistInput;

    /**
     * Whitelist confirmation button for entry addition
     */
    private Button buttonAddWhitelistEntry;

    /**
     * Whitelist entry removal button
     */
    private Button buttonRemoveWhitelistEntry;

    /**
     * Whitelist display.
     */
    private ListView<PlayerId> playerIds;

    /**
     * Constructor
     * @param owner The owner entity.
     */
    public SentryScreen(SentryEntity owner) {
        super(owner.getDisplayName());
        this.owner = Objects.requireNonNull(owner);
        this.imageHeight = 172;
    }

    @Override
    public ResourceLocation getBackgroundTexture() {
        return BACKGROUND;
    }

    @Override
    protected void init() {
        super.init();

        // target selector
        buttonTargetType = addButton(new CycleButton<>(SentryTargetType.values(), 8, 145, 50, 20));
        buttonTargetType.setFormatter(SentryTargetType::getDisplayComponent);
        buttonTargetType.setSelectionIndex(owner.getTargettingId());
        buttonTargetType.onChanged(this::onTargetTypeChanged);

        // whitelist control buttons
        buttonAddWhitelistEntry = addButton(new Button(69, 145, 45, 20, new StringTextComponent("+"), this::buttonWhitelistAdd_pressed));
        buttonAddWhitelistEntry.active = false;
        buttonRemoveWhitelistEntry = addButton(new Button(124, 145, 45, 20, new StringTextComponent("-"), this::buttonWhitelistRemove_pressed));
        buttonRemoveWhitelistEntry.active = false;

        // player whitelist view
        Set<UUID> whitelist = owner.getWhitelist();
        playerIds = addButton(new ListView<>(font, 69, 37, 100, 100, whitelist.stream().map(PlayerId::new).collect(Collectors.toList())));
        playerIds.setTextFormatter(PlayerId::getDisplayName);
        playerIds.setClickResponder(this::entry_selected);

        // whitelist text field
        whitelistInput = addButton(new TextFieldWidget(font, 70, 8, 98, 18, StringTextComponent.EMPTY));
        whitelistInput.setResponder(new GhostTextResponder("Player name", whitelistInput, this::onWhitelistInput));
    }

    @Override
    public void render(MatrixStack poseStack, int mouseX, int mouseY, float partialTicks) {
        drawBackgroundImage(poseStack);
        font.draw(poseStack, "Ammo:", leftPos + 8, topPos + 12, 0x333333);
        String ammoStat = String.format("%d / 150", owner.getFluidVolume() / 2);
        font.draw(poseStack, ammoStat, leftPos + 8, topPos + 24, 0x333333);
        font.draw(poseStack, "Target:", leftPos + 8, topPos + 133, 0x333333);
        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    private void buttonWhitelistAdd_pressed(Button button) {
        playerByName(whitelistInput.getValue()).ifPresent(player -> sendWhitelistRequest(CS_SentryWhitelistAction.ActionType.ADD, player.getUUID()));
        whitelistInput.setValue("");
    }

    private void buttonWhitelistRemove_pressed(Button button) {
        PlayerId id = playerIds.getSelected();
        if (id != null) {
            sendWhitelistRequest(CS_SentryWhitelistAction.ActionType.REMOVE, id.uuid);
        }
    }

    private void sendWhitelistRequest(CS_SentryWhitelistAction.ActionType type, UUID uuid) {
        NetworkHandler.CS_sendServerPacket(new CS_SentryWhitelistAction(type, owner.getId(), uuid));
    }

    private void onWhitelistInput(String value) {
        Optional<? extends PlayerEntity> optional = playerByName(value);
        buttonAddWhitelistEntry.active = !playerIds.isFull() && optional.isPresent() && !owner.isOwner(optional.get().getUUID());
    }

    private void onTargetTypeChanged(SentryTargetType type) {
        CS_SetSentryTargetting packet = new CS_SetSentryTargetting(owner.getId(), type);
        NetworkHandler.CS_sendServerPacket(packet);
    }

    private void entry_selected(PlayerId playerId) {
        buttonRemoveWhitelistEntry.active = playerId != null;
    }

    private Optional<? extends PlayerEntity> playerByName(String name) {
        List<? extends PlayerEntity> list = minecraft.level.players();
        Predicate<PlayerEntity> nameFilter = player -> player.getDisplayName().getString().equalsIgnoreCase(name);
        return list.stream().filter(nameFilter).findFirst();
    }

    private static class PlayerId {

        private final UUID uuid;
        private ITextComponent displayName;

        public PlayerId(UUID uuid) {
            this.uuid = uuid;
        }

        public ITextComponent getDisplayName() {
            if (displayName == null) {
                assignDisplayName();
            }
            return displayName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PlayerId playerId = (PlayerId) o;
            return uuid.equals(playerId.uuid);
        }

        @Override
        public int hashCode() {
            return uuid.hashCode();
        }

        private void assignDisplayName() {
            Minecraft mc = Minecraft.getInstance();
            ClientWorld world = mc.level;
            Optional<AbstractClientPlayerEntity> optional = world.players().stream().filter(player -> player.getUUID().equals(uuid)).findFirst();
            displayName = optional.map(PlayerEntity::getDisplayName).orElseGet(() -> new TranslationTextComponent("sentry.screen.unknown_player"));
        }
    }
}
