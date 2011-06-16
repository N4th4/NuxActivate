package com.bukkit.N4th4.NuxActivate;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class NuxActivate extends JavaPlugin {
	private Connection conn;
	private Configuration config;
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();

    public NuxActivate() {
        NALogger.initialize();
    }

    public void onEnable() {
    	File configFile = new File("plugins/NuxActivate/config.yml");
        if (configFile.exists()) {
            config = new Configuration(configFile);
            config.load();
        } else {
            NALogger.severe("File not found : plugins/NuxActivate/config.yml");
        }
        
    	String url = "jdbc:" + config.getString("url");
		String user = config.getString("user");
		String passwd = config.getString("passwd");
		
		try {
			conn = DriverManager.getConnection(url, user, passwd);
			conn.setAutoCommit(false);
			
			Statement state = conn.createStatement();
			state.executeUpdate("CREATE TABLE IF NOT EXISTS activateTable (login VARCHAR(100) NOT NULL, token VARCHAR(100) NOT NULL, state VARCHAR(10) NOT NULL DEFAULT 'pending');");
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		NALogger.info("Connected to MySQL");
		
        PluginDescriptionFile pdfFile = this.getDescription();
        NALogger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public void onDisable() {
    	try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String commandName = command.getName();
        if (sender instanceof Player) {
            Player senderP = (Player) sender;
            if (commandName.equalsIgnoreCase("Activate")) {
				try {
					Statement state = conn.createStatement();
					ResultSet result = state.executeQuery("SELECT * FROM activateTable WHERE login='" + senderP.getName() + "'");
					result.last();
					if (result.getRow() == 0) {
						Timestamp time = new Timestamp(Calendar.getInstance().getTime().getTime());
						state.executeUpdate("INSERT INTO activateTable VALUES ('" + senderP.getName() + "', ENCRYPT('" + senderP.getName() + time.getTime() + "'), 'pending')");
					}
					result.close();
					result = state.executeQuery("SELECT * FROM activateTable WHERE login='" + senderP.getName() + "'");
					result.first();
					senderP.sendMessage(ChatColor.GREEN + "[NuxActivate] Your token is : " +result.getString("token"));
					result.close();
                    state.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
            }
            return true;
        } else {
            sender.sendMessage("[NuxActivate] Only commands in chat are supported");
            return true;
        }
    }

    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
}
