package de.marhali.easyi18n.action.treeview;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

/**
 * Action to expand the entire tree (open all nodes with children).
 * @author marhali
 */
public class ExpandTreeViewAction extends AnAction {

    private final Runnable expandRunnable;

    public ExpandTreeViewAction(Runnable expandRunnable) {
        super(ResourceBundle.getBundle("messages").getString("view.tree.expand"),
                null, AllIcons.Actions.Expandall);

        this.expandRunnable = expandRunnable;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        expandRunnable.run();
    }
}