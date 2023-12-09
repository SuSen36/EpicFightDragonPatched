package yesman.epicfight.client.gui.screen;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.armor.CustomModelBakery;
import yesman.epicfight.client.gui.widget.ColorSlider;
import yesman.epicfight.client.gui.widget.BasicButton;
import yesman.epicfight.client.gui.widget.RewindableButton;
import yesman.epicfight.config.ClientConfig;
import yesman.epicfight.config.Option;
import yesman.epicfight.main.EpicFightMod;

import java.io.File;
import java.io.IOException;

@OnlyIn(Dist.CLIENT)
public class IngameConfigurationScreen extends Screen {
	protected final Screen parentScreen;
	
	public IngameConfigurationScreen(Minecraft mc, Screen screen) {
		super(Component.literal(EpicFightMod.MODID + ".gui.configuration"));
		this.parentScreen = screen;
	}
	
	@Override
	protected void init() {
		Option<ClientConfig.HealthBarShowOptions> showHealthIndicator = EpicFightMod.CLIENT_INGAME_CONFIG.healthBarShowOption;
		Option<Boolean> showTargetIndicator = EpicFightMod.CLIENT_INGAME_CONFIG.showTargetIndicator;

		Option<Boolean> filterAnimation = EpicFightMod.CLIENT_INGAME_CONFIG.filterAnimation;
		Option<Integer> longPressCounter = EpicFightMod.CLIENT_INGAME_CONFIG.longPressCount;
		Option<Boolean> enableAimHelper = EpicFightMod.CLIENT_INGAME_CONFIG.enableAimHelperPointer;
		Option<Double> aimHelperColor = EpicFightMod.CLIENT_INGAME_CONFIG.aimHelperColor;
		Option<Boolean> cameraAutoSwitch = EpicFightMod.CLIENT_INGAME_CONFIG.cameraAutoSwitch;
		Option<Boolean> autoPreparation = EpicFightMod.CLIENT_INGAME_CONFIG.autoPreparation;
		Option<Boolean> offBlood = EpicFightMod.CLIENT_INGAME_CONFIG.offBloodEffects;
		Option<Boolean> noMiningInCombat = EpicFightMod.CLIENT_INGAME_CONFIG.noMiningInCombat;
		
		int buttonHeight = -32;

		Button longPressCounterButton = this.addRenderableWidget(new RewindableButton(this.width / 2 - 165, this.height / 4 + buttonHeight, 160, 20,
			Component.translatable("gui."+EpicFightMod.MODID+".long_press_counter", (ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(longPressCounter.getValue()))),
			(button) -> {
				longPressCounter.setValue(longPressCounter.getValue() + 1);
				button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".long_press_counter", (ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(longPressCounter.getValue()))));
			},
			(button) -> {
				longPressCounter.setValue(longPressCounter.getValue() - 1);
				button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".long_press_counter", (ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(longPressCounter.getValue()))));
			}, (button, guiGraphics, mouseX, mouseY) -> {
				guiGraphics.renderTooltip(this.minecraft.font, this.minecraft.font.split(Component.translatable("gui.epicfight.long_press_counter.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
			}
		));
		
		Button filterAnimationButton = this.addRenderableWidget(new BasicButton(this.width / 2 + 5, this.height / 4 + buttonHeight, 160, 20,
			Component.translatable("gui."+EpicFightMod.MODID+".filter_animation." + (filterAnimation.getValue() ? "on" : "off")), (button) -> {
				filterAnimation.setValue(!filterAnimation.getValue());
				button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".filter_animation." + (filterAnimation.getValue() ? "on" : "off")));
			}, (button, guiGraphics, mouseX, mouseY) -> {
				guiGraphics.renderTooltip(this.minecraft.font, this.minecraft.font.split(Component.translatable("gui.epicfight.filter_animation.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
			}
		));

		buttonHeight += 24;

		Button health_barShowOptionButton = this.addRenderableWidget(new BasicButton(this.width / 2 - 165, this.height / 4 - 8, 160, 20,
			Component.translatable("gui."+EpicFightMod.MODID+".health_bar_show_option." + showHealthIndicator.getValue().toString()), (button) -> {
				showHealthIndicator.setValue(showHealthIndicator.getValue().nextOption());
				button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".health_bar_show_option." + showHealthIndicator.getValue().toString()));
			}, (button, guiGraphics, mouseX, mouseY) -> {
				guiGraphics.renderTooltip(this.minecraft.font, this.minecraft.font.split(Component.translatable("gui.epicfight.health_bar_show_option.tooltip"), Math.max(this.width / 2 - 43, 400)), mouseX, mouseY);
			}
		));
		
		Button showTargetIndicatorButton = this.addRenderableWidget(new BasicButton(this.width / 2 + 5, this.height / 4 - 8, 160, 20,
				Component.translatable("gui."+EpicFightMod.MODID+".target_indicator." + (showTargetIndicator.getValue() ? "on" : "off")), (button) -> {
					showTargetIndicator.setValue(!showTargetIndicator.getValue());
					button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".target_indicator." + (showTargetIndicator.getValue() ? "on" : "off")));
				}, (button, guiGraphics, mouseX, mouseY) -> {
					guiGraphics.renderTooltip(this.minecraft.font, this.minecraft.font.split(Component.translatable("gui.epicfight.target_indicator.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				}
			));
		buttonHeight+=24;
		Button cameraAutoSwitchButton = this.addRenderableWidget(new BasicButton(this.width / 2 - 165, this.height / 4 + buttonHeight, 160, 20,
				Component.translatable("gui."+EpicFightMod.MODID+".camera_auto_switch." + (cameraAutoSwitch.getValue() ? "on" : "off")), (button) -> {
					cameraAutoSwitch.setValue(!cameraAutoSwitch.getValue());
					button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".camera_auto_switch." + (cameraAutoSwitch.getValue() ? "on" : "off")));
				}, (button, guiGraphics, mouseX, mouseY) -> {
					guiGraphics.renderTooltip(this.minecraft.font, this.minecraft.font.split(Component.translatable("gui."+EpicFightMod.MODID+".camera_auto_switch.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				}
			));
		
		Button enableAimHelperButton = this.addRenderableWidget(new BasicButton(this.width / 2 + 5, this.height / 4 + buttonHeight, 160, 20,
				Component.translatable("gui."+EpicFightMod.MODID+".aim_helper." + (enableAimHelper.getValue() ? "on" : "off")), (button) -> {
					enableAimHelper.setValue(!enableAimHelper.getValue());
					button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".aim_helper." + (enableAimHelper.getValue() ? "on" : "off")));
				}, (button, guiGraphics, mouseX, mouseY) -> {
					guiGraphics.renderTooltip(this.minecraft.font, this.minecraft.font.split(Component.translatable("gui."+EpicFightMod.MODID+".aim_helper.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				}
			));
		buttonHeight+=24;
		Button autoPreparationButton = this.addRenderableWidget(new BasicButton(this.width / 2 - 165, this.height / 4 + buttonHeight, 160, 20,
				Component.translatable("gui."+EpicFightMod.MODID+".auto_preparation." + (autoPreparation.getValue() ? "on" : "off")), (button) -> {
					autoPreparation.setValue(!autoPreparation.getValue());
					button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".auto_preparation." + (autoPreparation.getValue() ? "on" : "off")));
				}, (button, guiGraphics, mouseX, mouseY) -> {
					guiGraphics.renderTooltip(this.minecraft.font, this.minecraft.font.split(Component.translatable("gui.epicfight.auto_preparation.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				}
			));
		
		Button offGoreButton = this.addRenderableWidget(new BasicButton(this.width / 2 + 5, this.height / 4 + buttonHeight, 160, 20,
				Component.translatable("gui."+EpicFightMod.MODID+".off_blood_effects." + (offBlood.getValue() ? "on" : "off")), (button) -> {
					offBlood.setValue(!offBlood.getValue());
					button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".off_blood_effects." + (offBlood.getValue() ? "on" : "off")));
				}, (button, guiGraphics, mouseX, mouseY) -> {
					guiGraphics.renderTooltip(this.minecraft.font, this.minecraft.font.split(Component.translatable("gui.epicfight.off_blood_effects.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				}
			));
		
		buttonHeight += 24;

		this.addRenderableWidget(new BasicButton(this.width / 2 - 165, this.height / 4 + buttonHeight, 160, 20, Component.translatable("gui."+EpicFightMod.MODID+".auto_switching_items"),  (button) -> {
			this.minecraft.setScreen(new EditSwitchingItemScreen(this));
		}, (button, guiGraphics, mouseX, mouseY) -> {
			guiGraphics.renderTooltip(this.minecraft.font, this.minecraft.font.split(Component.translatable("gui.epicfight.auto_switching_items.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
		}));
		
		this.addRenderableWidget(new BasicButton(this.width / 2 + 5, this.height / 4 + buttonHeight, 160, 20, Component.translatable("gui."+EpicFightMod.MODID+".export_custom_armor"),  (button) -> {
			File resourcePackDirectory = Minecraft.getInstance().getResourcePackDirectory().toFile();
			try {
				CustomModelBakery.exportModels(resourcePackDirectory);
				Util.getPlatform().openFile(resourcePackDirectory);
			} catch (IOException e) {
				EpicFightMod.LOGGER.info("Failed to export custom armor models");
				e.printStackTrace();
			}
		}, (button, guiGraphics, mouseX, mouseY) -> {
			guiGraphics.renderTooltip(this.minecraft.font, this.minecraft.font.split(Component.translatable("gui.epicfight.export_custom_armor.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
		}));
		
		buttonHeight += 24;

		this.addRenderableWidget(new BasicButton(this.width / 2 - 165, this.height / 4 + buttonHeight, 160, 20,
			Component.translatable("gui."+EpicFightMod.MODID+".ui_setup"), (button) -> {
				this.minecraft.setScreen(new UISetupScreen(this));
			}, (button, guiGraphics, mouseX, mouseY) -> {
				guiGraphics.renderTooltip(this.minecraft.font, this.minecraft.font.split(Component.translatable("gui."+EpicFightMod.MODID+".ui_setup.tooltip"), Math.max(this.width / 2 - 43, 400)), mouseX, mouseY);
			}
		));

		BasicButton noMiningInCombatButton = this.addRenderableWidget(new BasicButton(this.width / 2 + 5, this.height / 4 + buttonHeight, 160, 20,
			Component.translatable("gui."+EpicFightMod.MODID+".no_mining_in_combat." + (noMiningInCombat.getValue() ? "on" : "off")), (button) -> {
				noMiningInCombat.setValue(!noMiningInCombat.getValue());
				button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".no_mining_in_combat." + (noMiningInCombat.getValue() ? "on" : "off")));
			}, (button, guiGraphics, mouseX, mouseY) -> {
				guiGraphics.renderTooltip(this.minecraft.font, this.minecraft.font.split(Component.translatable("gui."+EpicFightMod.MODID+".no_mining_in_combat.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
			}
		));
		
		buttonHeight += 30;

		this.addRenderableWidget(new ColorSlider(this.width / 2 - 150, this.height / 4 + buttonHeight, 300, 20, Component.translatable("gui."+EpicFightMod.MODID+".aim_helper_color"), aimHelperColor.getValue(), EpicFightMod.CLIENT_INGAME_CONFIG.aimHelperColor));

		this.addRenderableWidget(new BasicButton(this.width / 2 + 90, this.height / 4 + 150, 48, 20, CommonComponents.GUI_DONE, (button) -> {
			EpicFightMod.CLIENT_INGAME_CONFIG.save();
			this.onClose();
		}));
		
		this.addRenderableWidget(new BasicButton(this.width / 2 + 140, this.height / 4 + 150, 48, 20, Component.translatable("controls.reset"), (button) -> {
			EpicFightMod.CLIENT_INGAME_CONFIG.resetSettings();
			filterAnimationButton.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".filter_animation." + (filterAnimation.getValue() ? "on" : "off")));
			longPressCounterButton.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".long_press_counter", (ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(longPressCounter.getValue()))));
			health_barShowOptionButton.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".health_bar_show_option." + showHealthIndicator.getValue().toString()));
			showTargetIndicatorButton.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".target_indicator." + (showTargetIndicator.getValue() ? "on" : "off")));
			enableAimHelperButton.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".aim_helper." + (enableAimHelper.getValue() ? "on" : "off")));
			cameraAutoSwitchButton.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".camera_auto_switch." + (cameraAutoSwitch.getValue() ? "on" : "off")));
			autoPreparationButton.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".auto_preparation." + (autoPreparation.getValue() ? "on" : "off")));
			offGoreButton.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".off_blood_effect." + (offBlood.getValue() ? "on" : "off")));
			noMiningInCombatButton.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".no_mining_in_combat." + (noMiningInCombat.getValue() ? "on" : "off")));
		}));
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderDirtBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parentScreen);
	}
}