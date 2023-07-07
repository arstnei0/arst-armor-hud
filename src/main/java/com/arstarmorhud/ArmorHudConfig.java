package com.arstarmorhud;

import io.wispforest.owo.config.annotation.Modmenu;

@Modmenu(modId = "arstarmorhud")
@io.wispforest.owo.config.annotation.Config(name = "arstarmorhudconfig", wrapperName = "Config")
public class ArmorHudConfig {
	public boolean hideEmptySlots = false;
	public boolean showSlots = true;
}
