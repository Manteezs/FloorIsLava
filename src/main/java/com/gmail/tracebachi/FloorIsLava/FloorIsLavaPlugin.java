/*
 * This file is part of FloorIsLava.
 *
 * FloorIsLava is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FloorIsLava is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FloorIsLava.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gmail.tracebachi.FloorIsLava;

import com.gmail.tracebachi.FloorIsLava.Arena.Arena;
import com.gmail.tracebachi.FloorIsLava.Commands.FloorCommand;
import com.gmail.tracebachi.FloorIsLava.Commands.ManageFloorCommand;
import com.gmail.tracebachi.FloorIsLava.Gui.FloorGuiMenuListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by Trace Bachi (BigBossZee) on 8/20/2015.
 */
public class FloorIsLavaPlugin extends JavaPlugin
{
    private Arena arena;
    private FloorGuiMenuListener listener;
    private Economy economy;

    @Override
    public void onLoad()
    {
        File config = new File(getDataFolder(), "config.yml");
        if(!config.exists())
        {
            saveDefaultConfig();
        }
    }

    @Override
    public void onEnable()
    {
        reloadConfig();

        /*********************************************************************/
        RegisteredServiceProvider<Economy> economyProvider = getServer()
            .getServicesManager()
            .getRegistration(net.milkbowl.vault.economy.Economy.class);

        if(economyProvider == null)
        {
            getLogger().severe("Economy provider not found! FloorIsLava will not be enabled.");
            return;
        }
        else
        {
            economy = economyProvider.getProvider();
        }
        /*********************************************************************/

        arena = new Arena(this);
        arena.loadConfig(getConfig());
        getServer().getPluginManager().registerEvents(arena, this);
        listener = new FloorGuiMenuListener(arena);
        getServer().getPluginManager().registerEvents(listener, this);

        getCommand("floor").setExecutor(new FloorCommand(arena));
        getCommand("mfloor").setExecutor(new ManageFloorCommand(this, arena));
    }

    @Override
    public void onDisable()
    {
        getCommand("mfloor").setExecutor(null);
        getCommand("floor").setExecutor(null);

        listener = null;

        arena.forceStop(Bukkit.getConsoleSender());
        arena = null;
    }

    public Economy getEconomy()
    {
        return economy;
    }
}