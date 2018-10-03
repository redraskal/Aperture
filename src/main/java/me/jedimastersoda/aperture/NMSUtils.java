package me.jedimastersoda.aperture;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NMSUtils {

  public static void sendPacket(Object packetImpl, Player... players) throws NoSuchFieldException, SecurityException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
    Class<?> packet = getNMSClass("Packet");
    Class<?> playerConnection = getNMSClass("PlayerConnection");
    
    for(Player player : players) {
      playerConnection.getDeclaredMethod("sendPacket", packet).invoke(getPlayerConnection(player), packetImpl);
    }
  }

  public static Object getPlayerConnection(Player player) throws NoSuchFieldException, SecurityException, 
      IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
    Class<?> craftPlayer = getCraftbukkitClass("entity.CraftPlayer");
    Class<?> entityPlayer = getNMSClass("EntityPlayer");
    Field field_playerConnection = entityPlayer.getDeclaredField("playerConnection");

    Object entityPlayerImpl = craftPlayer.getDeclaredMethod("getHandle").invoke(craftPlayer.cast(player));
    return field_playerConnection.get(entityPlayerImpl);
  }

  public static String getNMSVersion() {
    return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
  }

  public static Class<?> getNMSClass(String name) {
    try {
        return Class.forName("net.minecraft.server." + getNMSVersion() + "." + name);
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
        return null;
    }
  }

  public static Class<?> getCraftbukkitClass(String name) {
    try {
        return Class.forName("org.bukkit.craftbukkit." + getNMSVersion() + "." + name);
    } catch (ClassNotFoundException e) {
        e.printStackTrace();
        return null;
    }
  }
}