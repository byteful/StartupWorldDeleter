package me.byteful.plugin.swd;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public final class StartupWorldDeleterPlugin extends JavaPlugin {
  @Override
  public void onLoad() {
    getConfig().options().copyDefaults(true).copyHeader(true);
    saveDefaultConfig();

    final List<String> worlds = getConfig().getStringList("worlds");

    if(worlds.isEmpty()) {
      getLogger().warning("List of worlds in configuration is empty! Please make sure you configure the plugin correctly.");

      return;
    }

    worlds.forEach(world -> {
      if(Bukkit.getWorld(world) != null) {
        Bukkit.unloadWorld(world, getConfig().getBoolean("save", false));
      }

      try {
        final Path worldFolder = new File(Bukkit.getWorldContainer(), world).toPath();
        Files.walk(worldFolder)
            .sorted(Comparator.reverseOrder())
            .filter(p -> !p.equals(worldFolder))
            .map(Path::toFile)
            .forEach(File::delete);
      } catch (IOException e) {
        throw new RuntimeException("Failed trying to delete world (" + world + ")!", e);
      }
    });
    getLogger().info("Deleted worlds [" + String.join(",", worlds) + "].");
  }
}
