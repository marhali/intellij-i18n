package de.marhali.easyi18n.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import de.marhali.easyi18n.service.DataStore;

import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

/**
 * Reload translations action.
 * @author marhali
 */
public class ReloadAction extends AnAction {

    public ReloadAction() {
        super(ResourceBundle.getBundle("messages").getString("action.reload"),
                null, AllIcons.Actions.Refresh);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DataStore.getInstance(e.getProject()).reloadFromDisk();
    }
}