package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.command.Command_smite;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.projectiles.ProjectileSource;
import java.util.ArrayList;
import java.util.HashMap;


public class PvpMonitor extends FreedomService {

    HashMap<Player, Boolean> Hassmited = new HashMap<>();
    HashMap<Player, Integer> WarningLevel = new HashMap<>();
    HashMap<Player, Integer> Hits = new HashMap<>(); // THIS IS THE SAME!
    ArrayList<Player> First = new ArrayList<>();
    ArrayList<Player> Clock = new ArrayList<>();
    ArrayList<Player> Clock2 = new ArrayList<>();
    ArrayList<Player> hitsclock = new ArrayList<>();

    public PvpMonitor(TotalFreedomMod plugin) {
        super(plugin);
    }

    @Override
    protected void onStart() {
        for (Player p : server.getOnlinePlayers()) {
            WarningLevel.put(p, 0);
            Hits.put(p, 0);
            Hassmited.put(p,false);
        }
        timer();
        timer2();
    }

    @Override
    protected void onStop() {
        First.clear();
        WarningLevel.clear();
        hitsclock.clear();
        Clock.clear();
        Clock2.clear();
        Hits.clear();
        Hassmited.clear();
    }

    @EventHandler(priority = EventPriority.LOW)


    public void hit(final EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (damager instanceof Player && entity instanceof Player) {
            final Player player = (Player) damager;


            if (plugin.al.isAdmin((player))) {
                return;
            }

            if (player.getGameMode() == GameMode.CREATIVE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                Clock.add(player);
                Hits.put(player, Hits.get(player) + 1);
                event.setCancelled(true);

            }


            if (player.getGameMode() == GameMode.CREATIVE && !plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                Clock.add(player);
                Hits.put(player, Hits.get(player) + 1);
                event.setCancelled(true);
            }


            if (player.getGameMode() == GameMode.SURVIVAL && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                Clock.add(player);
                Hits.put(player, Hits.get(player) + 1);
                event.setCancelled(true);
            }

            if (player.getGameMode() == GameMode.ADVENTURE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                Clock.add(player);
                Hits.put(player, Hits.get(player) + 1);
                event.setCancelled(true);

            }


            if (Hits.get(player) == 4) {
               WarningLevel.put(player, WarningLevel.get(player) + 1);
               Hits.put(player, 0);

            }


            if (WarningLevel.get(player) == 1) {
                if (player.getGameMode() == GameMode.CREATIVE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    player.sendMessage(ChatColor.GREEN + "Hey! you need to turn off your god mode and set your gamemode to survival!");

                } else if (player.getGameMode() == GameMode.CREATIVE && !plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    player.sendMessage(ChatColor.GREEN + "Hey! you need to set your gamemode to survival!");

                } else if (player.getGameMode() == GameMode.SURVIVAL && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    player.sendMessage(ChatColor.GREEN + "Hey! you need to set your godmode off!!");

                } else if (player.getGameMode() == GameMode.ADVENTURE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    player.sendMessage(ChatColor.GREEN + "Hey! you need to set your godmode off!!");

                }
            }

            if (WarningLevel.get(player) > 3) {
                if (player.getGameMode() == GameMode.CREATIVE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    player.sendMessage(ChatColor.RED + "Hey! you need to turn off your god mode and set your gamemode to survival!");

                } else if (player.getGameMode() == GameMode.CREATIVE && !plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    player.sendMessage(ChatColor.RED + "Hey! you need to set your gamemode to survival!");

                } else if (player.getGameMode() == GameMode.SURVIVAL && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    player.sendMessage(ChatColor.RED + "Hey! you need to set your godmode off!!");

                } else if (player.getGameMode() == GameMode.ADVENTURE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    player.sendMessage(ChatColor.RED + "Hey! you need to set your godmode off!!");

                }
            }

            if (WarningLevel.get(player) == 6 && !(WarningLevel.get(player) == 7)) {
                if (player.getGameMode() == GameMode.CREATIVE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    if (Hassmited.get(player) == false)
                    {
                        Command_smite.smite(player, "Constantly trying to pvp with creative and god mode.");
                        Hassmited.put(player, true);
                    }
                } else if (player.getGameMode() == GameMode.CREATIVE && !plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    if (Hassmited.get(player) == false)
                    {
                        Command_smite.smite(player, "Constantly trying to pvp with creative!");
                        Hassmited.put(player, true);
                    }
                } else if (player.getGameMode() == GameMode.SURVIVAL && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    if (Hassmited.get(player) == false)
                    {
                        Command_smite.smite(player, "Constantly trying to pvp with god mode!");
                        Hassmited.put(player, true);
                    }
                } else if (player.getGameMode() == GameMode.ADVENTURE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    if (Hassmited.get(player) == false)
                    {
                        Command_smite.smite(player, "Constantly trying to pvp with god mode!");
                        Hassmited.put(player, true);
                    }
                }
                return;
            }

            if (WarningLevel.get(player) > 7) {
                if (player.getGameMode() == GameMode.CREATIVE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    player.sendMessage(ChatColor.RED + "Hey! you need to turn off your god mode and set your gamemode to survival! , some more warnings and you will get a 5m ban!");

                } else if (player.getGameMode() == GameMode.CREATIVE && !plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    player.sendMessage(ChatColor.RED + "Hey! you need to set your gamemode to survival! , some more warnings and you will get a 5m ban!");

                } else if (player.getGameMode() == GameMode.SURVIVAL && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    player.sendMessage(ChatColor.RED + "Hey! you need to set your godmode off!! , some more warnings and you will get a 5m ban!");

                } else if (player.getGameMode() == GameMode.ADVENTURE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    player.sendMessage(ChatColor.RED + "Hey! you need to set your godmode off!! , some more warnings and you will get a 5m ban!");

                }
            }


            if (WarningLevel.get(player) > 9) {
                if (player.getGameMode() == GameMode.CREATIVE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    plugin.bm.addBan(Ban.forPlayer(player, server.getConsoleSender(), FUtil.parseDateOffset("5m"), "Keeping God and creative PVPing after you got warned multiple times!"));
                    FUtil.bcastMsg(player.getName() + "has been banned for constantly creative and god pvping (5m ban)", ChatColor.RED);
                    WarningLevel.put(player, 0);
                    player.kickPlayer("§cBanned for 5m");
                    Hassmited.put(player,false);
                } else if (player.getGameMode() == GameMode.CREATIVE && !plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    plugin.bm.addBan(Ban.forPlayer(player, server.getConsoleSender(), FUtil.parseDateOffset("5m"), "Keeping Creative PVPing after you got warned multiple times!"));
                    FUtil.bcastMsg(player.getName() + "has been banned for constantly creative pvping (5m ban)", ChatColor.RED);
                    WarningLevel.put(player, 0);
                    player.kickPlayer("§cBanned for 5m");
                    Hassmited.put(player,false);
                } else if (player.getGameMode() == GameMode.SURVIVAL && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    plugin.bm.addBan(Ban.forPlayer(player, server.getConsoleSender(), FUtil.parseDateOffset("5m"), "Keeping God PVPing after you got warned multiple times!"));
                    FUtil.bcastMsg(player.getName() + "has been banned for constantly god pvping (5m ban)", ChatColor.RED);
                    WarningLevel.put(player, 0);
                    Hassmited.put(player,false);
                    player.kickPlayer("§cBanned for 5m");
                } else if (player.getGameMode() == GameMode.ADVENTURE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                    plugin.bm.addBan(Ban.forPlayer(player, server.getConsoleSender(), FUtil.parseDateOffset("5m"), "Keeping God PVPing after you got warned multiple times!"));
                    FUtil.bcastMsg(player.getName() + "has been banned for constantly god pvping (5m ban)", ChatColor.RED);
                    WarningLevel.put(player, 0);
                    Hassmited.put(player,false);
                    player.kickPlayer("§cBanned for 5m");
                }
            }


            if (WarningLevel.get(player) >= 1) {
                if (Clock.contains(player)) {
                    Clock2.add(player);
                }
                if (Hits.get(player) >= 1) {
                    if (hitsclock.contains(player)) {
                        hitsclock.add(player);
                    }
                }
            }
        }


        if (damager instanceof Projectile && entity instanceof Player) {

            ProjectileSource ps = ((Projectile) damager).getShooter();

            Player player = (Player) ps;


            if (plugin.al.isAdmin((player))) {
                return;
            }

            if (player.getGameMode() == GameMode.CREATIVE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                Clock.add(player);
                Hits.put(player, Hits.get(player) + 1);
                event.setCancelled(true);
            }


            if (player.getGameMode() == GameMode.CREATIVE && !plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                Clock.add(player);
                Hits.put(player, Hits.get(player) + 1);
                event.setCancelled(true);
            }


            if (player.getGameMode() == GameMode.SURVIVAL && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                Clock.add(player);
                Hits.put(player, Hits.get(player) + 1);
                event.setCancelled(true);
            }

            if (player.getGameMode() == GameMode.ADVENTURE && plugin.esb.getEssentialsUser(player.getName()).isGodModeEnabled()) {
                Clock.add(player);
                Hits.put(player, Hits.get(player) + 1);
                event.setCancelled(true);

            }


            if (Hits.get(player) == 4) {
                WarningLevel.put(player, WarningLevel.get(player) + 1);
                Hits.put(player, 0);
            }
        }
    }












    @EventHandler(priority = EventPriority.LOW)
    public void OnjoinFirst(final PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (First.contains(p) == true) {
            WarningLevel.put(p, 0);
            Hits.put(p, 0);
            Hassmited.put(p,false);
        } else {
            First.add(p);
            WarningLevel.put(p, 0);
            Hits.put(p, 0);
            Hassmited.put(p,false);
        }

    }

    public void timer() {
        server.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                    public void run() {
                        for (Player player : server.getOnlinePlayers()) {
                            if (WarningLevel.get(player) >= 2) {
                                if (Clock2.contains(player)) {
                                    return;
                                }
                                WarningLevel.put(player, 0);
                                Clock.remove(player);
                                player.sendMessage(ChatColor.GREEN + "Your warning level has been reset!");
                            }
                        }
                    }
                }
                , 6000, 6000);
    }

    public void timer2() {
        server.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                    public void run() {
                        for (Player player : server.getOnlinePlayers()) {
                            if (Hits.get(player) >= 1) {
                                if (hitsclock.contains(player)) {
                                    return;
                                }
                                Hits.put(player, 0);
                                hitsclock.remove(player);
                            }
                        }
                    }
                }
                , 4000, 4000);
    }
}

