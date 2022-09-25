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
package com.mallowigi.config

import com.intellij.openapi.application.ApplicationManager
import com.mallowigi.icons.associations.Association
import com.mallowigi.icons.associations.Associations
import com.mallowigi.icons.associations.DefaultAssociations
import com.mallowigi.icons.associations.RegexAssociation
import com.mallowigi.icons.services.AssociationsFactory
import com.mallowigi.models.IconType

/**
 * Service for managing bundled [Associations]
 *
 */
class BundledAssociations {
  /**
   * All loaded file [Associations]
   */
  private var defaultFileAssociations: MutableMap<String, RegexAssociation> = mutableMapOf()

  /**
   * All loaded folder [Associations]
   */
  private var defaultFolderAssociations: MutableMap<String, RegexAssociation> = mutableMapOf()

  init {
    init()
  }

  /**
   * Load bundled associations
   *
   */
  private fun init() {
    folderAssociations.getTheAssociations()
      .filterIsInstance<RegexAssociation>()
      .forEach { insert(it.name, it, IconType.FOLDER) }

    fileAssociations.getTheAssociations()
      .filterIsInstance<RegexAssociation>()
      .forEach { insert(it.name, it, IconType.FILE) }
  }

  /**
   * Returns the relevant list according to the [IconType]
   *
   * @param iconType
   * @return
   */
  fun getMap(iconType: IconType): MutableMap<String, RegexAssociation> {
    return when (iconType) {
      IconType.FILE   -> defaultFileAssociations
      IconType.FOLDER -> defaultFolderAssociations
    }
  }

  /**
   * Insert a new default [RegexAssociation]
   *
   * @param name assoc name
   * @param assoc the [RegexAssociation]
   * @param iconType the [IconType]
   */
  fun insert(name: String, assoc: RegexAssociation, iconType: IconType) {
    if (hasDefault(name, iconType)) return

    val map = getMap(iconType)

    map[name] = assoc
    map[name]?.enabled = true
  }

  /**
   * Checks if an [Association] is already registered in the defaults
   *
   * @param name assoc name
   * @param iconType the [IconType]
   */
  fun hasDefault(name: String, iconType: IconType): Boolean = getMap(iconType).containsKey(name)

  /**
   * Get a default [Association] by name and [IconType]
   *
   * @param name the name
   * @param iconType the [IconType]
   */
  fun getDefault(name: String, iconType: IconType): RegexAssociation? = getMap(iconType)[name]

  /**
   * Get the list of [RegexAssociation]s
   *
   * @param iconType the [IconType]
   * @return the list
   */
  fun getList(iconType: IconType): List<RegexAssociation> = getMap(iconType).values.toList()

  companion object {
    /**
     * Service instance
     */
    val instance: BundledAssociations
      get() = ApplicationManager.getApplication().getService(BundledAssociations::class.java)

    /**
     * Load folder associations from XML
     */
    val folderAssociations: DefaultAssociations =
      AssociationsFactory.create("/iconGenerator/src/associations/folder_associations.xml")

    /**
     * Load file associations from XML
     */
    val fileAssociations: DefaultAssociations =
      AssociationsFactory.create("/iconGenerator/src/associations/icon_associations.xml")

  }
}
