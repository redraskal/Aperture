package me.jedimastersoda.aperture;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.packetlistener.PacketListenerAPI;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;

import me.jedimastersoda.aperture.listener.JoinEvent;

public class Aperture extends JavaPlugin {

  public void onEnable() {
    this.registerPacketHandler();
    this.getServer().getPluginManager().registerEvents(new JoinEvent(this), this);

    Bukkit.getOnlinePlayers().forEach(player -> checkClient(player));
  }

  @SuppressWarnings("deprecation")
  private void registerPacketHandler() {
    PacketListenerAPI.addPacketHandler(new PacketHandler() {

      @Override
      public void onReceive(ReceivedPacket packet) {
        if(packet.getPacketName().equals("PacketPlayInResourcePackStatus")) {
          Player client = packet.getPlayer();
          String fileHash = (String) packet.getPacketValue("a");
          Object fileStatus = packet.getPacketValue("b");
          Bukkit.broadcastMessage("[DEBUG] " + client.getName() + " | " + fileHash + " | " + fileStatus.toString());
          //TODO
        }
      }

      @Override
      public void onSend(SentPacket arg0) {}
    });
  }

  public void checkClient(Player player) {
    try {
      sendFileRequest(player, "Jigsaw/settings.json", null);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sends a file status request to the specified Player. Base directory is the
   * .minecraft folder.
   * 
   * @param player
   * @param filePath
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws NoSuchFieldException
   */
  public void sendFileRequest(Player player, String filePath, String fileHash) throws InstantiationException, IllegalAccessException, 
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException {
    if(!player.isOnline()) return;
    Object packetPlayOutResourcePackSend = NMSUtils.getNMSClass("PacketPlayOutResourcePackSend");
    String url = "level://../" + filePath;
    if(fileHash == null) fileHash = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    Object packetImpl = packetPlayOutResourcePackSend.getClass()
      .getConstructor(String.class, String.class).newInstance(url, fileHash);
    NMSUtils.sendPacket(packetImpl, player);
  }
}