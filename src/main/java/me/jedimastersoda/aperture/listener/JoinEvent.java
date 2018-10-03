package me.jedimastersoda.aperture.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import lombok.Getter;
import me.jedimastersoda.aperture.Aperture;

public class JoinEvent implements Listener {

  @Getter final Aperture aperture;

  public JoinEvent(Aperture aperture) {
    this.aperture = aperture;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    this.getAperture().checkClient(event.getPlayer());
  }
}