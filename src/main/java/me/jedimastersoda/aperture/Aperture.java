package me.jedimastersoda.aperture;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.packetlistener.PacketListenerAPI;
import org.inventivetalent.packetlistener.handler.PacketHandler;
import org.inventivetalent.packetlistener.handler.ReceivedPacket;
import org.inventivetalent.packetlistener.handler.SentPacket;

import me.jedimastersoda.aperture.listener.JoinEvent;

public class Aperture extends JavaPlugin {

  private Map<UUID, FileStatusRequest> requests = new HashMap<>();

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
          try {
            UUID fileHash = UUID.fromString((String) packet.getPacketValue("a"));
            String fileFetchStatus = packet.getPacketValue("b").toString();
            if(fileFetchStatus.equals("SUCCESSFULLY_LOADED") || fileFetchStatus.equals("DECLINED")) return;
            FileStatus fileStatus = fileFetchStatus.equals("ACCEPTED") ? FileStatus.EXISTS : FileStatus.NOT_EXISTS;
            if(requests.containsKey(fileHash)) {
              requests.get(fileHash).complete(fileStatus);
              requests.remove(fileHash);
            }
          } catch (IllegalArgumentException e) {}
        }
      }

      @Override
      public void onSend(SentPacket arg0) {}
    });
  }

  public void checkClient(Player player) {
    try {
      sendFileRequest(player, new FileStatusRequest("Jigsaw/settings.json") {
      
        @Override
        public void complete(FileStatus fileStatus) {
          if(fileStatus == FileStatus.EXISTS) {
            delayedKick(player, "Jigsaw");
          }
        }
      });
      sendFileRequest(player, new FileStatusRequest("wurst/settings.json") {
      
        @Override
        public void complete(FileStatus fileStatus) {
          if(fileStatus == FileStatus.EXISTS) {
            delayedKick(player, "Wurst");
          }
        }
      });
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
      e.printStackTrace();
    }
  }

  private void delayedKick(Player player, String client) {
    new BukkitRunnable() {
    
      @Override
      public void run() {
        if(player.isOnline()) {
          player.sendMessage("Please delete the " + client + " client from your device! Remember, Jesus is watching you.");
          //player.kickPlayer("Internal Exception: java.io.IOException: An existing connection was forcibly closed by the remote host");
        }
      }
    }.runTaskLater(this, (20*5));
  }

  /**
   * Sends a file status request to the specified Player. Base directory is the
   * .minecraft folder.
   * 
   * @param player
   * @param fileStatusRequest
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws NoSuchFieldException
   */
  public void sendFileRequest(Player player, FileStatusRequest fileStatusRequest) throws InstantiationException, IllegalAccessException, 
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException {
    if(!player.isOnline()) return;
    Class<?> packetPlayOutResourcePackSend = NMSUtils.getNMSClass("PacketPlayOutResourcePackSend");
    String url = "level://../" + fileStatusRequest.getFilePath();
    requests.put(fileStatusRequest.getUuid(), fileStatusRequest);
    Object packetImpl = packetPlayOutResourcePackSend.getConstructor(String.class, String.class).newInstance(url, fileStatusRequest.getUuid().toString());
    NMSUtils.sendPacket(packetImpl, player);
  }
}
