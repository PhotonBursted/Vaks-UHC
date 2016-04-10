package com.photonburst.VaksUHC.Discord;

import com.google.common.util.concurrent.FutureCallback;
import com.photonburst.VaksUHC.Utils;
import com.photonburst.VaksUHC.VaksUHC;
import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.Channel;
import de.btobastian.javacord.entities.Server;
import de.btobastian.javacord.entities.User;
import de.btobastian.javacord.entities.permissions.Role;

import java.util.Collection;

public class Bot {
    private final VaksUHC plugin;

    private DiscordAPI bot;
    Channel general;
    Server server;

    public Bot(VaksUHC plugin) {
        this.plugin = plugin;
    }

    public void handleDeath(String playerName) {
        for(User user: server.getMembers()) {
            if(user.getName().equals(playerName)) {
                Collection<Role> roles = user.getRoles(server);
                for(Role role: roles) {
                    if(role.getName() == "Admin") {
                        roles.remove(role);
                    }
                }

                Role[] rolesToRemove = roles.toArray(new Role[0]);
                server.updateRoles(user, rolesToRemove);
                general.sendMessage("**[DEATH]** Oh no! " + user.getMentionTag() + " just died...");
            }
        }
    }

    public void start() {
        DiscordAPI bot = Javacord.getApi(plugin.getConfig().getString("discord.credentials.email"),
                plugin.getConfig().getString("discord.credentials.password"));

        bot.connect(new FutureCallback<DiscordAPI>() {
            @Override
            public void onSuccess(final DiscordAPI api) {
                String token = api.getToken();
                Utils.println("[DISCORD] Token: " + token);

                general = api.getChannelById(plugin.getConfig().getString("discord.channel-id"));
                server = general.getServer();

                general.sendMessage("**[LOGIN]** Hi guys, virtual Vechs here! :V");
            }

            @Override
            public void onFailure(Throwable t) {
                // login failed
                t.printStackTrace();
            }
        });
    }
}