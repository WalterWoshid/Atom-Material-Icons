/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Chris Magnussen and Elior Boukhobza
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 */

package com.mallowigi.tree;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.projectView.ProjectViewNode;
import com.intellij.ide.projectView.ProjectViewNodeDecorator;
import com.intellij.ide.projectView.impl.ProjectRootsUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.packageDependencies.ui.PackageDependenciesNode;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.util.PlatformIcons;
import com.mallowigi.config.AtomFileIconsConfig;
import com.mallowigi.icons.DirIcon;
import icons.MTIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class HollowFoldersDecorator implements ProjectViewNodeDecorator {

  @Nullable
  private static volatile Icon directory = MTIcons.Nodes2.FolderOpen;
  private final boolean useHollowFolders;
  private FileEditorManagerEx manager;

  public HollowFoldersDecorator() {
    useHollowFolders = AtomFileIconsConfig.getInstance().isUseHollowFolders();
  }

  private static void setOpenDirectoryIcon(final PresentationData data, final VirtualFile file, final Project project) {
    try {
      if (data.getIcon(true) instanceof DirIcon) {
        final Icon openedIcon = ((DirIcon) Objects.requireNonNull(data.getIcon(true))).getOpenedIcon();
        data.setIcon(new DirIcon(openedIcon));
      }
      else if (ProjectRootManager.getInstance(project).getFileIndex().isExcluded(file)) {
        data.setIcon(MTIcons.EXCLUDED);
      }
      else if (ProjectRootsUtil.isModuleContentRoot(file, project)) {
        data.setIcon(MTIcons.MODULE);
      }
      else if (ProjectRootsUtil.isInSource(file, project)) {
        data.setIcon(MTIcons.SOURCE);
      }
      else if (ProjectRootsUtil.isInTestSource(file, project)) {
        data.setIcon(MTIcons.TEST);
      }
      else if (Objects.equals(data.getIcon(false), PlatformIcons.PACKAGE_ICON)) {
        //      Looks like an open directory anyway
        data.setIcon(PlatformIcons.PACKAGE_ICON);
      }
      else {
        data.setIcon(getDirectoryIcon());
      }
    }
    catch (final Exception e) {
    }
  }

  @SuppressWarnings("NonThreadSafeLazyInitialization")
  private static Icon getDirectoryIcon() {
    if (directory == null) {
      directory = MTIcons.Nodes2.FolderOpen;
    }

    return directory;
  }

  @Override
  public void decorate(final ProjectViewNode node, final PresentationData data) {
    final VirtualFile file = node.getVirtualFile();
    final Project project = node.getProject();

    // Color file status
    if (project != null && file != null && !Disposer.isDisposed(project)) {
      if (!useHollowFolders || !file.isDirectory()) {
        return;
      }
      if (manager == null) {
        manager = FileEditorManagerEx.getInstanceEx(project);
      }

      ApplicationManager.getApplication().invokeLater(() -> {
        final List<@NotNull VirtualFile> openFiles = Arrays.asList(manager.getOpenFiles());

        openFiles.stream()
          .filter(virtualFile -> virtualFile.getPath().contains(file.getPath()))
          .findFirst()
          .ifPresent(virtualFile -> setOpenDirectoryIcon(data, virtualFile, project));
      });
    }
  }

  @Override
  public void decorate(final PackageDependenciesNode node, final ColoredTreeCellRenderer cellRenderer) {

  }
}
