/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2022 Elior "Mallowigi" Boukhobza
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
package com.mallowigi.config.associations.ui.internal

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.ComponentValidator
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.ui.cellvalidators.StatefulValidatingCellEditor
import com.intellij.openapi.ui.cellvalidators.ValidatingTableCellRendererWrapper
import com.intellij.openapi.util.Disposer
import com.intellij.ui.EditorTextField
import org.intellij.lang.regexp.RegExpFileType
import java.awt.Component
import java.awt.event.KeyEvent
import java.util.function.Consumer
import javax.swing.JComponent
import javax.swing.JTable
import javax.swing.JTextField
import javax.swing.KeyStroke
import javax.swing.table.TableCellEditor

/**
 * A Regular expression editor with validation
 *
 * @param textField the text field component
 * @param parent the disposable parent
 */
class RegexpEditor(textField: JTextField, parent: Disposable) :
  StatefulValidatingCellEditor(textField, parent), TableCellEditor {
  /** Instance of an editor with regexp syntax highlighting. */
  private var editor: EditorTextField =
    EditorTextField(
      /* document = */ EditorFactory.getInstance().createDocument("dummy.regexp"),
      /* project = */ ProjectManager.getInstance().defaultProject,
      /* fileType = */ RegExpFileType.INSTANCE,
      /* isViewer = */ false,
      /* oneLineMode = */ true
    )

  /** Floating document during edition. */
  private var myDocument: Document? = null

  /** State updater reducer used for validation. */
  private val stateUpdater = Consumer { _: ValidationInfo? -> }

  init {
    // Creates a regex editor
    editor.setOneLineMode(true)
    editor.fileType = RegExpFileType.INSTANCE
    // Register enter and escape keys
    editor.registerKeyboardAction(
      { stopCellEditing() },
      KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
      JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
    )
    editor.registerKeyboardAction(
      { cancelCellEditing() },
      KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
      JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
    )

    myDocument = editor.document
    clickCountToStart = 2

    // Install validations
    ComponentValidator(parent).withValidator(this).installOn(editor)

    // Reset validations when document changes but revalidates once blurred
    val dl: DocumentListener = object : DocumentListener {
      override fun documentChanged(event: DocumentEvent) {
        editor.putClientProperty(ValidatingTableCellRendererWrapper.CELL_VALIDATION_PROPERTY, null)
        ComponentValidator.getInstance(editor).ifPresent { obj: ComponentValidator -> obj.revalidate() }
      }
    }

    myDocument!!.addDocumentListener(dl)
    Disposer.register(parent) { (myDocument ?: return@register).removeDocumentListener(dl) }
  }

  /**
   * Creates a [TableCellEditor] with validation functionalities
   *
   * @param table the table
   * @param value the value to render
   * @param isSelected whether the cell is selected
   * @param row the cell row
   * @param column the cell column
   * @return an editor for the current cell
   */
  override fun getTableCellEditorComponent(
    table: JTable,
    value: Any,
    isSelected: Boolean,
    row: Int,
    column: Int,
  ): Component {

    editor.text = value.toString()
    // Install validations on renderer
    val renderer = table.getCellRenderer(row, column)
      .getTableCellRendererComponent(table, value, isSelected, true, row, column) as JComponent

    val validationProperty = renderer.getClientProperty(ValidatingTableCellRendererWrapper.CELL_VALIDATION_PROPERTY)
    if (validationProperty != null) {
      val cellInfo = validationProperty as ValidationInfo
      // Add validation info in a property
      editor.putClientProperty(
        ValidatingTableCellRendererWrapper.CELL_VALIDATION_PROPERTY,
        cellInfo.forComponent(editor)
      )

      // Revalidates
      ComponentValidator.getInstance(editor).ifPresent { obj: ComponentValidator -> obj.revalidate() }
    }
    return editor
  }

  /**
   * The cell value: the contents of the edited document
   *
   * @return the contents of the text field
   */
  override fun getCellEditorValue(): Any = myDocument!!.text

  /**
   * Validates cell when edition stopped
   *
   * @return true if valid
   */
  override fun stopCellEditing(): Boolean {
    // Revalidates on blur
    ComponentValidator.getInstance(editor).ifPresent { obj: ComponentValidator -> obj.revalidate() }
    fireEditingStopped()
    return true
  }

  /** Revalidates when cancel editing. */
  override fun cancelCellEditing() {
    // Revalidates on cancel
    ComponentValidator.getInstance(editor).ifPresent { obj: ComponentValidator -> obj.revalidate() }
    fireEditingCanceled()
  }

  /**
   * Executes validations
   *
   * @return the [ValidationInfo] if there is
   */
  override fun get(): ValidationInfo? {
    val validationProperty = editor.getClientProperty(ValidatingTableCellRendererWrapper.CELL_VALIDATION_PROPERTY)
    if (validationProperty != null) {
      val info = validationProperty as ValidationInfo
      stateUpdater.accept(info)
      return info
    }
    return null
  }

  /**
   * Get component: the editor
   *
   * @return the [EditorTextField]
   */
  override fun getComponent(): Component = editor
}
