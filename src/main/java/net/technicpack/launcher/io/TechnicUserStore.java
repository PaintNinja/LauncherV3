/*
 * This file is part of The Technic Launcher Version 3.
 * Copyright (C) 2013 Syndicate, LLC
 *
 * The Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Technic Launcher  is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with The Technic Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.technicpack.launcher.io;

import com.google.gson.JsonSyntaxException;
import net.technicpack.launchercore.auth.IUserStore;
import net.technicpack.launchercore.auth.User;
import net.technicpack.utilslib.Utils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class TechnicUserStore implements IUserStore {
    private String clientToken = UUID.randomUUID().toString();
    private Map<String, User> savedUsers = new HashMap<String, User>();
    private String lastUser;
    private transient File usersFile;

    public TechnicUserStore() {
    }

    public TechnicUserStore(File userFile) {
        this.usersFile = userFile;
    }

    public static TechnicUserStore load(File userFile) {
        if (!userFile.exists()) {
            Utils.getLogger().log(Level.WARNING, "Unable to load users from " + userFile + " because it does not exist.");
            return new TechnicUserStore(userFile);
        }

        try {
            String json = FileUtils.readFileToString(userFile, Charset.forName("UTF-8"));
            TechnicUserStore newModel = Utils.getGson().fromJson(json, TechnicUserStore.class);
            newModel.setUserFile(userFile);
            return newModel;
        } catch (JsonSyntaxException e) {
            Utils.getLogger().log(Level.WARNING, "Unable to load users from " + userFile);
        } catch (IOException e) {
            Utils.getLogger().log(Level.WARNING, "Unable to load users from " + userFile);
        }

        return new TechnicUserStore(userFile);
    }

    public void setUserFile(File userFile) {
        this.usersFile = userFile;
    }

    public void save() {
        String json = Utils.getGson().toJson(this);

        try {
            FileUtils.writeStringToFile(usersFile, json, Charset.forName("UTF-8"));
        } catch (IOException e) {
            Utils.getLogger().log(Level.WARNING, "Unable to save users " + usersFile);
        }
    }

    public void addUser(User user) {
        savedUsers.put(user.getUsername(), user);
        save();
    }

    public void removeUser(String username) {
        savedUsers.remove(username);
        save();
    }

    public User getUser(String accountName) {
        return savedUsers.get(accountName);
    }

    public String getClientToken() {
        return clientToken;
    }

    public Collection<String> getUsers() {
        return savedUsers.keySet();
    }

    public Collection<User> getSavedUsers() {
        return savedUsers.values();
    }

    public void setLastUser(String lastUser) {
        this.lastUser = lastUser;
        save();
    }

    public String getLastUser() {
        return lastUser;
    }
}
